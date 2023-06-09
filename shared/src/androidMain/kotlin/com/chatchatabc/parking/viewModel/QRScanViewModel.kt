package com.chatchatabc.parking.viewModel

import com.chatchatabc.parking.api.InvoiceAPI
import com.chatchatabc.parking.api.VehicleAPI
import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.dto.CreateInvoiceDTO
import kotlinx.coroutines.flow.MutableStateFlow

class QRScanViewModel(val invoiceAPI: InvoiceAPI, val vehicleAPI: VehicleAPI) : BaseViewModel(invoiceAPI, vehicleAPI) {

    val hasPermission = MutableStateFlow(false)

    val currentVehicle: MutableStateFlow<Vehicle?> = MutableStateFlow(null)
    val currentInvoice: MutableStateFlow<Invoice?> = MutableStateFlow(null)

    val uiState: MutableStateFlow<QRScanState> = MutableStateFlow(QRScanState.SCAN)

    fun checkVehicle(string: String) {
        uiState.value = QRScanState.LOADING
        load {
            val vehicle = vehicleAPI.getVehicle(string)

            println("Checking vehicle: $string")
            if (vehicle.errors.isNotEmpty()) {
                vehicle.errors.forEach {
                    println("Error: $it")
                }
                println("somethign went wrong")
                uiState.value = QRScanState.VEHICLE_INVALID
                return@load
            }

            currentVehicle.value = vehicle.data

            println("Vehicle: ${vehicle.data}")
            if (currentVehicle.value == null) {
                println("Vehicle is null")
                uiState.value = QRScanState.VEHICLE_INVALID
                return@load
            }

            println("Vehicle is not null")
            println("Checking for active invoice")

            val invoice = invoiceAPI.getActiveInvoice(currentVehicle.value!!.vehicleUuid)

            if (invoice.errors.isNotEmpty()) {
                invoice.errors.forEach {
                    println("Error: $it")
                }
                uiState.value = QRScanState.VEHICLE_INVALID
                return@load
            }

            currentInvoice.value = invoice.data

            if (currentInvoice.value != null) {
                currentInvoice.value = currentInvoice.value!!.copy(
                    total = invoiceAPI.getEstimate(currentInvoice.value!!.invoiceUuid).data ?: 0.0,
                )
            }

            if (currentInvoice.value == null) {
                println("Invoice is null, parking")
                uiState.value = QRScanState.VEHICLE_PARKING
                return@load
            } else {
                println("Invoice is not null, leaving")
                uiState.value = QRScanState.VEHICLE_LEAVING
                return@load
            }
        }
    }

    fun park(estimate: Int) {
        load {
            invoiceAPI.startInvoice(currentVehicle.value!!.vehicleUuid, CreateInvoiceDTO(estimate))
            uiState.value = QRScanState.VEHICLE_PARKED
        }
    }

    fun leave(paid: Boolean) {
        load {
            invoiceAPI.endInvoice(currentInvoice.value!!.invoiceUuid)
            if (paid) {
                invoiceAPI.payInvoice(currentInvoice.value!!.invoiceUuid)
            }
            uiState.value = QRScanState.VEHICLE_LEFT
        }
    }

    fun cancel() {
        uiState.value = QRScanState.SCAN
    }
}

enum class QRScanState {
    SCAN,
    LOADING,
    VEHICLE_PARKING,
    VEHICLE_PARKED,
    VEHICLE_LEAVING,
    VEHICLE_LEFT,
    VEHICLE_INVALID,
}