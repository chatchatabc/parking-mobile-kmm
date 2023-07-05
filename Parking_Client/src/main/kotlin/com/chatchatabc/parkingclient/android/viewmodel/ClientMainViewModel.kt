package com.chatchatabc.parkingclient.android.viewmodel

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.InvoiceAPI
import com.chatchatabc.parking.api.JeepneyAPI
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.ProfileAPI
import com.chatchatabc.parking.api.RouteAPI
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.api.VehicleAPI
import com.chatchatabc.parking.`interface`.NatsReceiver
import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.Jeepney
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.model.Route
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.nats.InvoicePayload
import com.chatchatabc.parking.model.nats.JeepneyUpdatePayload
import com.chatchatabc.parking.model.nats.NatsEnums
import com.chatchatabc.parking.model.nats.NatsMessage
import com.chatchatabc.parking.model.nats.NatsMessageWithPayload
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import com.chatchatabc.parking.model.response.FlowCall
import com.chatchatabc.parking.model.response.RouteNodesAndEdges
import com.chatchatabc.parking.model.response.flowCall
import com.chatchatabc.parking.service.NatsService
import com.chatchatabc.parking.viewModel.BaseViewModel
import com.chatchatabc.parkingclient.android.db.AppDB
import com.chatchatabc.parkingclient.android.db.entity.dbObject
import com.chatchatabc.parkingclient.android.db.entity.route
import com.chatchatabc.parkingclient.android.db.entity.toDBObject
import com.chatchatabc.parkingclient.android.db.entity.toParkingLot
import com.mapbox.maps.CoordinateBounds
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import java.io.ByteArrayOutputStream

// TODO: Transition to using enums for state.
// TODO: Split this monolith ViewModel into smaller and more manageable ViewModels.

enum class MainUIState {
    PARKING,
    JEEPNEY,
    REPORT,
    ACCOUNT
}

class ClientMainViewModel(
    val userAPI: UserAPI,
    val parkingAPI: ParkingAPI,
    val profileAPI: ProfileAPI,
    val vehicleAPI: VehicleAPI,
    val invoiceAPI: InvoiceAPI,
    val jeepneyAPI: JeepneyAPI,
    val routeAPI: RouteAPI,
    val sharedPreferences: SharedPreferences,
    val natsService: NatsService,
    val appDB: AppDB,
): BaseViewModel(userAPI, parkingAPI, profileAPI, vehicleAPI, invoiceAPI, jeepneyAPI, routeAPI), KoinComponent {
    val json = Json { ignoreUnknownKeys = true }

    val parkingViewModel = ParkingViewModel(userAPI, parkingAPI, vehicleAPI, invoiceAPI, appDB, natsService)
    val jeepneyViewModel = JeepneyViewModel(jeepneyAPI, routeAPI, appDB, natsService)
    val state = MutableStateFlow(MainUIState.PARKING)

    init {
        viewModelScope.launch {
            natsService.initialize()
        }
    }

    fun startListening() = viewModelScope.launch {
        var notificationId: String

        userAPI.getNotificationId().data?.notificationId.let {
            if (it == null) return@launch else notificationId = it
        }
        natsService.subscribeToSubject(notificationId) { rawMessage ->
            println(rawMessage.data.decodeToString())
        }
    }

    val logoutPopupOpened =
        MutableStateFlow(false)

    fun clearAuthToken() = flow<Unit> {
        // Change activity to loginActivity
        sharedPreferences.edit().remove("authToken").apply()
        profileAPI.logout()
    }.launchIn(viewModelScope)

    fun setPage(page: MainUIState) {
        state.value = page
    }

    fun onMapLoaded() {
        parkingViewModel.syncParkingLots()
    }
}

enum class ParkingUIState {
    DEFAULT,
    QR_VIEWER_VEHICLE_SELECT,
    QR_VIEWER,
    PARKED_WITH_QR_VIEWER,
    LEFT_WITH_QR_VIEWER,
}

class ParkingViewModel(
    val userAPI: UserAPI,
    val parkingAPI: ParkingAPI,
    val vehicleAPI: VehicleAPI,
    val invoiceAPI: InvoiceAPI,
    val appDB: com.chatchatabc.parkingclient.android.db.AppDB,
    val natsService: NatsService,
): ViewModel(), NatsReceiver {
    val json = Json { ignoreUnknownKeys = true }

    override fun subscribe() {
        viewModelScope.launch {
            var notificationId: String
            userAPI.getNotificationId().data?.notificationId.let {
                if (it == null) return@launch else notificationId = it
            }

            natsService.subscribeToSubject(notificationId) { rawMessage ->
                val message: NatsMessage = json.decodeFromString(rawMessage.data.decodeToString())
                when (message.type) {
                    NatsEnums.INVOICE_CREATED -> {
                        val message: NatsMessageWithPayload<InvoicePayload> = json.decodeFromString(rawMessage.data.decodeToString())
                        if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                            viewModelScope.launch {
                                parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                parkingState.value = ParkingUIState.PARKED_WITH_QR_VIEWER
                            }
                        }
                    }
                    NatsEnums.INVOICE_PAID -> {
                        val message: NatsMessageWithPayload<InvoicePayload> =
                            json.decodeFromString(rawMessage.data.decodeToString())
                        if (message.payload?.vehicleUuid == selectedVehicle.value?.vehicleUuid) {
                            viewModelScope.launch {
                                parkedLot.value = parkingAPI.getParkingLot(message.payload!!.parkingLotUuid).data
                                currentInvoice.value = invoiceAPI.getInvoice(message.payload!!.invoiceUuid).data
                                parkingState.value = ParkingUIState.LEFT_WITH_QR_VIEWER
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
                                parkingState.value = ParkingUIState.LEFT_WITH_QR_VIEWER
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    override fun unsubscribe() {}

    var parkingState: MutableStateFlow<ParkingUIState> = MutableStateFlow(ParkingUIState.DEFAULT)

    // QR Code
    val qrCodeState: MutableStateFlow<FlowCall<Bitmap?>> = MutableStateFlow(FlowCall.nothing())

    //
    val parkedLot: MutableStateFlow<ParkingLot?> = MutableStateFlow(null)
    val currentInvoice = MutableStateFlow<Invoice?>(null)

    val visibleParkingLots: MutableStateFlow<FlowCall<List<ParkingLot>>> =
        MutableStateFlow(FlowCall.nothing())

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
                    response.data?.let { parkingLots ->
                    visibleParkingLots.value = FlowCall.success(parkingLots.content)

                    CoroutineScope(Dispatchers.IO).launch {
                        appDB.parkingLotDAO().addAll(parkingLots.content.map { it.toDBObject() })
                    }
                }
            }
        }
    }.launchIn(viewModelScope)

    fun getParkingLotsInRange(bounds: CoordinateBounds) = flow {
        println("Getting parking lots in range!!")
        emit(FlowCall.loading())
        emit(appDB.parkingLotDAO().getParkingLotsWithinCoordinateBounds(bounds).map {
            it.toParkingLot()
        }.flowCall)
    }.onEach {
        visibleParkingLots.value = it
    }.launchIn(CoroutineScope(Dispatchers.IO))


    fun openVehicleSelector() {
        selectedVehicle.value = null
        parkingState.value = ParkingUIState.QR_VIEWER_VEHICLE_SELECT
    }

    fun getAllVehicles() = flow {
        emit(FlowCall.loading())
        emit(vehicleAPI.getAllVehicles().flowCall)
    }.onEach {
        vehicles.value = it
    }.launchIn(viewModelScope)

    fun createQRFromString(uuid: String) = flow<FlowCall<Bitmap?>> {
        emit(FlowCall.loading())
        val cellSize = 20 // pixels
        val outputStream = ByteArrayOutputStream()
        QRCode("VEHICLE:$uuid:VEHICLE")
            .render(cellSize, margin = cellSize)
            .writeImage(outputStream)
        parkingState.value = ParkingUIState.QR_VIEWER
        emit(BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size()).flowCall)
    }.onEach {
        qrCodeState.value = it
    }.launchIn(viewModelScope)
}

class JeepneyViewModel(
    val jeepneyAPI: JeepneyAPI,
    val routeAPI: RouteAPI,
    val appDB: com.chatchatabc.parkingclient.android.db.AppDB,
    val natsService: NatsService
): ViewModel(), NatsReceiver {
    val json = Json

    val isRouteSelectorOpened = MutableStateFlow(false)

    val routes = MutableStateFlow<List<Route>>(emptyList())

    val jeepneys = MutableStateFlow<List<Jeepney>>(listOf())

    fun syncRoutes() = flow {
        emit(FlowCall.loading())
        emit(routeAPI.getAllRoutes().flowCall)
    }.onEach {
        if (it.isSuccess) {
            appDB.routeDAO().insertRoutes(it.response?.data?.content?.map { route -> route.dbObject } ?: emptyList())
        }
    }.launchIn(CoroutineScope(Dispatchers.IO))

    val selectedRoute: MutableStateFlow<FlowCall<Route>> = MutableStateFlow(FlowCall.nothing())
    val selectedRouteNodesAndEdges: MutableStateFlow<FlowCall<RouteNodesAndEdges>> = MutableStateFlow(FlowCall.nothing())

    fun getAllRoutesFromDB() = flow {
        emit(FlowCall.loading())
        emit(appDB.routeDAO().getAllRoutes().map { it.route }.flowCall)
    }.onEach {
        if (it.isSuccess) {
            routes.value = it.response ?: emptyList()
        }
    }.launchIn(CoroutineScope(Dispatchers.IO))

    fun getRoutePath(routeUuid: String) = flow {
        selectedRoute.value.response?.let { selectedRoute ->
            emit(FlowCall.loading())
            emit(routeAPI.getNodesAndEdgesOfRoute(routeUuid).flowCall)
        }
    }.onEach {
        if (it.isSuccess) {
            it.response?.data?.let { nodesAndEdges ->
                CoroutineScope(Dispatchers.IO).launch {
                    appDB.routeDAO().insertRouteNodesAndEdges(nodesAndEdges.dbObject)
                }
                selectedRouteNodesAndEdges.value = nodesAndEdges.flowCall
            }
        }
    }.launchIn(viewModelScope)

    fun setRoute(route: Route) {
        viewModelScope.launch {
            if (selectedRoute.value.response?.routeUuid != route.routeUuid) {
                natsService.unsubscribeFromSubject("route-${selectedRoute.value.response?.slug}")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            getRoutePath(route.routeUuid)
        }

        selectedRoute.value = FlowCall.success(route)
        jeepneys.value = emptyList<Jeepney>()

        flow {
            emit(jeepneyAPI.getAllJeepneysByRoute(route.routeUuid, Pagination(size = 1000)).flowCall)
            subscribe()
        }.onEach {
            if (it.isSuccess) {
                jeepneys.value = it.response?.data?.content ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    override fun subscribe() {
        viewModelScope.launch {
            natsService.subscribeToSubject("route-${selectedRoute.value.response?.slug}") { rawData ->
                val message: NatsMessageWithPayload<JeepneyUpdatePayload> = json.decodeFromString(rawData.data.decodeToString())
                println("MESSAGE FROM SLUG route-${selectedRoute.value.response?.slug}: $message")

                message.payload?.jeepneyUuid?.let { uuid ->
                    val currentJeepneys = jeepneys.value.toMutableList()

                    val updatedJeepneyIndex = currentJeepneys.indexOfFirst { it.jeepneyUuid == uuid }
                    if (updatedJeepneyIndex >= 0) {
                        val updatedJeepney = currentJeepneys[updatedJeepneyIndex].copy(
                            latitude = message.payload!!.latitude,
                            longitude = message.payload!!.longitude,
                            direction = message.payload!!.direction
                        )

                        println("WE ARE UPDATING THIS JEEP $updatedJeepney")

                        currentJeepneys[updatedJeepneyIndex] = updatedJeepney
                        jeepneys.value = currentJeepneys
                    }
                }
            }
        }
    }

    override fun unsubscribe() {
        selectedRoute.value.response?.let {
            viewModelScope.launch {
                natsService.unsubscribeFromSubject("route-${it.routeUuid}")
            }
        }
    }
}