package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.InvoiceAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.api.VehicleAPI
import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.nats.InvoicePayload
import com.chatchatabc.parking.model.nats.NatsEnums
import com.chatchatabc.parking.model.nats.NatsMessage
import com.chatchatabc.parking.model.nats.NatsMessageWithPayload
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.response.ApiResponse
import com.chatchatabc.parking.model.response.FlowCall
import com.chatchatabc.parking.model.response.flowCall
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parking.service.NatsService
import com.google.android.gms.maps.model.LatLngBounds
import io.github.g0dkar.qrcode.QRCode
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.io.ByteArrayOutputStream

// TODO: Transition to using enums for state.
// TODO: Split this monolith ViewModel into smaller and more manageable ViewModels.
class ClientMainViewModel(
    val userAPI: UserAPI,
    val parkingAPI: ParkingAPI,
    val profileAPI: ProfileAPI,
    val vehicleAPI: VehicleAPI,
    val invoiceAPI: InvoiceAPI,
    val parkingRealm: Realm,
    val sharedPreferences: SharedPreferences,
    val natsService: NatsService,
): BaseViewModel(userAPI, parkingAPI, profileAPI, vehicleAPI, invoiceAPI), KoinComponent {
    val json = Json { ignoreUnknownKeys = true }

    var uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.DEFAULT)

    // QR Code
    val qrCodeState: MutableStateFlow<FlowCall<Bitmap?>> = MutableStateFlow(FlowCall.nothing())
    val parkedLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)
    val currentInvoice = MutableStateFlow<Invoice?>(null)

    // TODO: Better implementation, but this works in the meantime.
    // TODO: Better error handling.
    fun startListening() = viewModelScope.launch {
        var notificationId: String
        userAPI.getNotificationId().data?.notificationId.let {
            if (it == null) return@launch else notificationId = it
        }

        natsService.init(
            onError = { Timber.e(it) }
        ) {
            onMessageRecieved = { rawMessage ->
                val message: NatsMessage = json.decodeFromString(rawMessage.data.decodeToString())
                when (message.type) {
                    NatsEnums.INVOICE_CREATED -> {
                        val message: NatsMessageWithPayload<InvoicePayload> =
                            json.decodeFromString(rawMessage.data.decodeToString())
                        if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                            viewModelScope.launch {
                                parkedLot.value =
                                    parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                uiState.value = MainUiState.PARKED_WITH_QR_VIEWER
                            }
                        }
                    }

                    NatsEnums.INVOICE_PAID -> {
                        val message: NatsMessageWithPayload<InvoicePayload> =
                            json.decodeFromString(rawMessage.data.decodeToString())
                        if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                            viewModelScope.launch {
                                parkedLot.value =
                                    parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                currentInvoice.value =
                                    invoiceAPI.getInvoice(message.payload!!.invoiceUuid).data
                                uiState.value = MainUiState.LEFT_WITH_QR_VIEWER
                            }
                        }
                    }

                    NatsEnums.INVOICE_ENDED -> {
                        val message: NatsMessageWithPayload<InvoicePayload> =
                            json.decodeFromString(rawMessage.data.decodeToString())
                        if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                            viewModelScope.launch {
                                parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                currentInvoice.value = invoiceAPI.getInvoice(message.payload!!.invoiceUuid).data
                                uiState.value = MainUiState.LEFT_WITH_QR_VIEWER
                            }
                        }
                    }
                }

                viewModelScope.launch { listen(notificationId) }
            }
        }
    }

    val visibleParkingLots: MutableStateFlow<FlowCall<List<ParkingLotRealmObject>>> =
        MutableStateFlow(FlowCall.nothing())
    val currentPage = MutableStateFlow(0)

    val logoutPopupOpened =
        MutableStateFlow(false)

    val selectedVehicle: MutableStateFlow<Vehicle?> =
        MutableStateFlow(null)

    val vehicles: MutableStateFlow<FlowCall<ApiResponse<Page<Vehicle>>>> =
        MutableStateFlow(FlowCall.nothing())

    fun syncParkingLots() = flow {
        emit(FlowCall.loading())
        emit(parkingAPI.getAllParkingLots().flowCall)
    }.onEach { flowCall ->
        if (flowCall.isSuccess) {
            parkingAPI.getAllParkingLots().let { response ->
                parkingRealm.writeBlocking {
                    if (response.errors.isEmpty()) response.data?.content?.forEach { parkingLot ->
                        copyToRealm(
                            instance = parkingLot.let {
                                ParkingLotRealmObject().apply {
                                    id = it.parkingLotUuid
                                    name = it.name!!
                                    latitude = it.latitude
                                    longitude = it.longitude
                                }
                            },
                            updatePolicy = UpdatePolicy.ALL
                        )
                    }
                }
            }
        }
    }.launchIn(viewModelScope)

    fun getParkingLotsInRange(bounds: LatLngBounds) = flow {
        emit(FlowCall.loading())
        val query = StringBuilder()
        query.append("latitude >= ${bounds.southwest.latitude} AND latitude <= ${bounds.northeast.latitude}")

        if (bounds.northeast.longitude < bounds.southwest.longitude) {
            query.append(" AND (longitude >= ${bounds.southwest.longitude} OR longitude <= ${bounds.northeast.longitude})")
        } else {
            query.append(" AND longitude >= ${bounds.southwest.longitude} AND longitude <= ${bounds.northeast.longitude}")
        }

        val results = parkingRealm.query<ParkingLotRealmObject>(query.toString()).find() as List<ParkingLotRealmObject>
        emit(FlowCall.success(results))
        results.forEach {
            println("Parking Lot: ${it.name} (${it.latitude}, ${it.longitude})")
        }
    }.onEach {
        visibleParkingLots.value = it
    }.launchIn(viewModelScope)


    fun openVehicleSelector() {
        selectedVehicle.value = null
        uiState.value = MainUiState.QR_VIEWER_VEHICLE_SELECT
    }

    fun getAllVehicles() = flow {
        emit(FlowCall.loading())
        emit(vehicleAPI.getAllVehicles().flowCall)
    }.onEach {
        vehicles.value = it
    }.launchIn(viewModelScope)

    fun clearAuthToken() = flow<Unit> {
        // Change activity to loginActivity
        sharedPreferences.edit().remove("authToken").apply()
        profileAPI.logout()
    }.launchIn(viewModelScope)

    fun createQRFromString(uuid: String) = flow<FlowCall<Bitmap?>> {
        emit(FlowCall.loading())
        val cellSize = 20 // pixels
        val outputStream = ByteArrayOutputStream()
        QRCode("VEHICLE:$uuid:VEHICLE")
            .render(cellSize, margin = cellSize)
            .writeImage(outputStream)
        uiState.value = MainUiState.QR_VIEWER
        emit(BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size()).flowCall)
    }.onEach {
        qrCodeState.value = it
    }.launchIn(viewModelScope)

    fun setCurrentPage(page: Int) {
        currentPage.value = page
    }
}

enum class MainUiState {
    DEFAULT,
    QR_VIEWER_VEHICLE_SELECT,
    QR_VIEWER,
    PARKED_WITH_QR_VIEWER,
    INVOICE_PAGE,
    LEFT_WITH_QR_VIEWER
}