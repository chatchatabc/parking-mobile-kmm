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
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parking.service.NatsService
import com.google.android.gms.maps.model.LatLngBounds
import io.github.g0dkar.qrcode.QRCode
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableStateFlow
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
    val parkedLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)
    val currentInvoice = MutableStateFlow<Invoice?>(null)

    // TODO: Better implementation, but this works in the meantime.
    // TODO: Better error handling.
    fun startListening() {
        natsService.init(
            // TODO: Implement exponential backoff
            onError = { },
        ) {
            this?.let {
                viewModelScope.launch {
                    userAPI.getNotificationId().data?.notificationId.let {
                        if (it == null) return@launch else notificationId = it
                    }
                    onMessageRecieved = { rawMessage ->
                        val message: NatsMessage = json.decodeFromString(rawMessage.data.decodeToString())
                        when (message.type) {
                            NatsEnums.INVOICE_CREATED -> {
                                val message: NatsMessageWithPayload<InvoicePayload> = json.decodeFromString(rawMessage.data.decodeToString())
                                if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                                    load {
                                        parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                        uiState.value = MainUiState.PARKED_WITH_QR_VIEWER
                                    }
                                }
                            }
                            NatsEnums.INVOICE_PAID -> {
                                val message: NatsMessageWithPayload<InvoicePayload> = json.decodeFromString(rawMessage.data.decodeToString())
                                if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                                    load {
                                        parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                        currentInvoice.value = invoiceAPI.getInvoice(message.payload!!.invoiceUuid).data
                                        uiState.value = MainUiState.LEFT_WITH_QR_VIEWER
                                    }
                                }
                            }
                            NatsEnums.INVOICE_ENDED -> {
                            val message: NatsMessageWithPayload<InvoicePayload> = json.decodeFromString(rawMessage.data.decodeToString())
                            if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                                load {
                                    parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                    currentInvoice.value = invoiceAPI.getInvoice(message.payload!!.invoiceUuid).data
                                    uiState.value = MainUiState.LEFT_WITH_QR_VIEWER
                                }
                            }
                        }
                            else -> {
                                Timber.d("Server sent unknown message type: ${message.type}")
                                Timber.d("Message: ${rawMessage.data.decodeToString()}")
                            }
                        }
                    }
                    listen()
                }
            }
        }
    }

    val visibleParkingLots = MutableStateFlow(listOf<ParkingLotRealmObject>())
    val currentPage = MutableStateFlow(0)

    val logoutPopupOpened = MutableStateFlow(false)

    val selectedVehicle: MutableStateFlow<Vehicle?> = MutableStateFlow(null)

    val vehicles: MutableStateFlow<List<Vehicle>> = MutableStateFlow(listOf())

    fun syncParkingLots() {
        load {
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
    }

    fun getParkingLotsInRange(bounds: LatLngBounds) {
        println("Getting parking lots in range")
        load {
            parkingRealm.query<ParkingLotRealmObject>(
                "latitude >= ${bounds.southwest.latitude} AND latitude <= ${bounds.northeast.latitude} AND " +
                        "longitude >= ${bounds.southwest.longitude} AND longitude <= ${bounds.northeast.longitude}"
            ).find().let {
                visibleParkingLots.value = it
                it.forEach {
                    println("Parking Lot: ${it.name} (${it.latitude}, ${it.longitude})")
                }
            }
        }
    }

    fun openVehicleSelector() {
        selectedVehicle.value = null
        uiState.value = MainUiState.QR_VIEWER_VEHICLE_SELECT
    }

    fun getAllVehicles() {
        load {
            vehicleAPI.getAllVehicles().let {
                if (it.errors.isEmpty()) {
                    it.data!!.content.forEach {
                        println("Vehicle: ${it.plateNumber}")
                    }
                    vehicles.value = it.data.content
                } else {
                    selectedVehicle.value = null
                }
            }
        }
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }

    val isLoadingQRCode = MutableStateFlow(false)
    val qrCode = MutableStateFlow<Bitmap?>(null)

    fun createQRFromString(uuid: String) {
        isLoadingQRCode.value = true
        val cellSize = 20 // pixels
        val outputStream = ByteArrayOutputStream()
        QRCode("VEHICLE:$uuid:VEHICLE")
            .render(cellSize, margin = cellSize)
            .writeImage(outputStream)
        qrCode.value = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        isLoadingQRCode.value = false
        uiState.value = MainUiState.QR_VIEWER
    }

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