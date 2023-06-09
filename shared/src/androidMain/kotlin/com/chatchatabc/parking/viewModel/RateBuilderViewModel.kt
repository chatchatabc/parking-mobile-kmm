package com.chatchatabc.parking.viewModel

import com.chatchatabc.parking.model.Rate
import com.chatchatabc.parking.model.dto.UpdateRateDTO
import kotlinx.coroutines.flow.MutableStateFlow

class RateBuilderViewModel: BaseViewModel() {
    val rateType: MutableStateFlow<RateType> = MutableStateFlow(RateType.None)
    val rateInterval: MutableStateFlow<RateInterval> = MutableStateFlow(RateInterval.None)

    val freeHours: MutableStateFlow<Int> = MutableStateFlow(0)
    val payFreeHoursWhenExceeded: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val startRate: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val rateValue: MutableStateFlow<Double> = MutableStateFlow(0.0)

    val errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun validateAll(): Map<String, String> {
        val errors: MutableMap<String, String> = mutableMapOf()
        if (rateType.value == RateType.None) errors["rateType"] = "Please select a rate type"
        if (rateInterval.value == RateInterval.None) errors["rateInterval"] = "Please select a rate interval"
        if (startRate.value == 0.0) errors["startRate"] = "Start rate must be a non-zero value"
        if (rateValue.value == 0.0) errors["rateValue"] = "Rate value must be a non-zero value"
        return errors.toMap().also {
            this.errors.value = it
        }
    }

    fun createDTO(): UpdateRateDTO {
        return UpdateRateDTO(
            type = rateType.value.ordinal,
            interval = rateInterval.value.ordinal,
            freeHours = freeHours.value,
            payForFreeHoursWhenExceeding = payFreeHoursWhenExceeded.value,
            startingRate = startRate.value,
            rate = rateValue.value
        )
    }

    fun restore(savedRate: Rate) {
        rateType.value = when (savedRate.type) {
            RateType.Fixed.ordinal -> RateType.Fixed
            RateType.Flexible.ordinal -> RateType.Flexible
            else -> RateType.None
        }
        rateInterval.value = when (savedRate.interval) {
            RateInterval.Hourly.ordinal -> RateInterval.Hourly
            RateInterval.Daily.ordinal -> RateInterval.Daily
            else -> RateInterval.None
        }
        freeHours.value = savedRate.freeHours
        payFreeHoursWhenExceeded.value = savedRate.payForFreeHoursWhenExceeding
        startRate.value = savedRate.startingRate
        rateValue.value = savedRate.rate
    }
}

enum class RateType { Fixed, Flexible, None }
enum class RateInterval { Hourly, Daily, None }
