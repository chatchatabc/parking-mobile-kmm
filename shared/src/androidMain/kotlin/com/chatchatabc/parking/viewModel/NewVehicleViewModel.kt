package com.chatchatabc.parking.viewModel

import com.chatchatabc.parking.api.NewVehicleDTO
import com.chatchatabc.parking.api.VehicleAPI
import kotlinx.coroutines.flow.MutableStateFlow

enum class VehicleType {
    CAR,
    MOTORCYCLE,
    NONE
}

class NewVehicleViewModel(val vehicleApi: VehicleAPI): BaseViewModel() {
    var page = MutableStateFlow(0)
    var name = MutableStateFlow("")
    val platenumber = MutableStateFlow("")
    var type = MutableStateFlow(VehicleType.NONE)

    val errors = MutableStateFlow(mapOf<String, String>())

    fun validateAndSubmit() {
        validations.forEach { (currentPage, validation) ->
            validation.invoke()
            if (errors.value.isNotEmpty()) page.value = currentPage
        }

        if (errors.value.isEmpty()) {
            load {
                vehicleApi.register(
                    NewVehicleDTO(
                        name = name.value,
                        plateNumber = platenumber.value,
                        type = type.value.ordinal
                    )
                ).let {
                    if (it.errors.isEmpty()) {
                        page.value += 1
                    } else {
                        errors.value += errors.value + mapOf("global" to "Something went wrong. Please try again.")
                    }
                }
            }
        }
    }

    val validations: Map<Int, () -> Unit> = mapOf(
        0 to {
            errors.value = mapOf()
            if (name.value.isEmpty()) errors.value = errors.value + mapOf("name" to "Name cannot be empty")
            if (platenumber.value.isEmpty()) errors.value = errors.value + mapOf("platenumber" to "Plate number cannot be empty")
            if (type.value == VehicleType.NONE) errors.value = errors.value + mapOf("type" to "Please select a vehicle type")
            if (type.value == VehicleType.CAR) {
                if ("^[A-Z]{2}-\\d{5}\$\n".toRegex().matches(platenumber.value) || // Temporary Car
                    "^[A-Z]{3}-\\d{4}\$\n".toRegex().matches(platenumber.value) // Permanent Car
                ) errors.value = errors.value + mapOf("plateNumber" to "Invalid plate number. Please follow the format of XX-XXXXX or XXX-XXXX")
            } else if (type.value == VehicleType.MOTORCYCLE) {
                if ("^\\d{4}-\\d{7}\$\n".toRegex().matches(platenumber.value) || // Temporary
                    "^[A-Z]{2}-\\d{5}\$\n".toRegex().matches(platenumber.value) // Permanent
                ) errors.value = errors.value + mapOf("plateNumber" to "Invalid plate number. Please follow the format of XXXX-XXXXXXX or XX-XXXXX")
            }
        }
    )

    fun validate(page: Int): Boolean {
        validations[page]?.invoke()
        return errors.value.isEmpty()
    }
}