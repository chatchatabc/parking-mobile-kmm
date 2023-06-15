package com.chatchatabc.parking.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ParkingAPI
import com.chatchatabc.parking.api.RateAPI
import com.chatchatabc.parking.model.ParkingLotImage
import com.chatchatabc.parking.model.dto.ParkingLotDraftDTO
import com.chatchatabc.parking.model.getFlags
import com.chatchatabc.parking.model.toIntFlag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class NewParkingLotViewModel(val parkingApi: ParkingAPI, val rateApi: RateAPI, val application: Application): BaseViewModel(parkingApi, rateApi) {
    var page = MutableStateFlow(0)

    var errors = MutableStateFlow(mapOf<String, String>())

    var openingTime = MutableStateFlow(Pair(0, 0))
    var closingTime = MutableStateFlow(Pair(1, 0))

    var daysOpen = MutableStateFlow(listOf<Int>())

    var parkingLotName = MutableStateFlow("")
    var parkingLotAddress = MutableStateFlow("")
    var description = MutableStateFlow("")
    var capacity = MutableStateFlow(0)

    var parkingLotUuid: MutableStateFlow<String?> = MutableStateFlow(null)
    var rateUuid = MutableStateFlow<String?>(null)

    var images: MutableStateFlow<List<ImageUpload>> = MutableStateFlow(listOf())

    var location: MutableStateFlow<Pair<Double, Double>?> = MutableStateFlow(null)

    var progress: MutableStateFlow<Int> = MutableStateFlow(0)

    val rateBuilderViewModel = RateBuilderViewModel()

    init {
        viewModelScope.launch {
            images.collectLatest {
                Log.d("UPLOAD", "Collecting images")
                if (it.none { it.status == ImageUploadState.UPLOADING } &&
                    it.any { it.status == ImageUploadState.QUEUED }) {
                    val pendingUpload = it.first { it.status == ImageUploadState.QUEUED }

                    val imageByteArray = pendingUpload.fileUri?.let { uri ->
                        application.contentResolver.openInputStream(uri)?.let { stream ->
                            stream.readBytes().also {
                                stream.close()
                            }
                        }
                    }

                    if (imageByteArray == null) {
                        images.value = images.value.map { image ->
                            if (image.fileUri == pendingUpload.fileUri) {
                                image.copy(status = ImageUploadState.ERROR)
                            } else {
                                image
                            }
                        }
                    } else {
                        parkingApi.uploadImage(imageByteArray) {
                            images.value = images.value.toMutableList().apply {
                                Log.d("UPLOAD: ", "Progress: $it")
                                progress.value = it
                            }
                        }.let {
                            if (it.errors.isEmpty()) {
                                images.value = images.value.toMutableList().apply {
                                    progress.value = 0
                                    if (it.errors.isEmpty()) {
                                        it.data?.let {data ->
                                            set(indexOf(pendingUpload), pendingUpload.copy(status = ImageUploadState.UPLOADED, remoteUrl = parkingApi.getImage(data.id).also { Timber.d("URL: $it") }))
                                        }
                                    } else {
                                        println("Error uploading image")
                                        set(indexOf(pendingUpload), pendingUpload.copy(status = ImageUploadState.ERROR))
                                    }
                                }
                            } else {
                                Log.d("UPLOAD", "Error uploading image")
                                images.value = images.value.toMutableList().apply {
                                    set(indexOf(pendingUpload), pendingUpload.copy(status = ImageUploadState.ERROR))
                                }
                            }
                        }
                    }
                } else Log.d("UPLOAD", "No images to upload")
            }
        }
    }

    fun addToUploadQueue(fileUri: Uri) {
        Log.d("UPLOAD", "Adding to upload queue: $fileUri")
        images.value = images.value.plus(
            ImageUpload(
                status = ImageUploadState.QUEUED,
                fileUri = fileUri
            )
        )
    }

    fun setId(id: String) {
        this.parkingLotUuid.value = id
        restoreDraft()
    }

    val validations = mapOf(
        0 to {
            errors.value = mapOf()
            if (parkingLotName.value.isEmpty()) errors.value += "parkingLotName" to "Name is required"
            if (parkingLotAddress.value.isEmpty()) errors.value += "parkingLotAddress" to "Address is required"
            if (location.value == null) errors.value += "location" to "Location is required"
        },
        1 to {
            errors.value = mapOf()
            if (daysOpen.value.isEmpty()) errors.value += "daysOpen" to "At least one day must be selected"
            if (openingTime.value.first > closingTime.value.first || (openingTime.value.first == closingTime.value.first && openingTime.value.second > closingTime.value.second)) errors.value += "openingTime" to "Opening time must be before closing time"
        },
        2 to {
            errors.value = rateBuilderViewModel.validateAll()
        },
        3 to {
            errors.value = mapOf()
            // TODO: Add validation logic for file uploads
        }
    )

    private fun restoreDraft() {
        viewModelScope.launch {
            parkingApi.getParkingLot().let {
                if (it.errors.isEmpty()) {
                    it.data?.let { parkingLot ->
                        parkingLotUuid.value = parkingLot.parkingLotUuid
                        parkingLotName.value = parkingLot.name ?: ""
                        parkingLotAddress.value = parkingLot.address ?: ""
                        description.value = parkingLot.description ?: ""
                        capacity.value = parkingLot.capacity ?: 0
                        location.value = if (parkingLot.latitude == null || parkingLot.longitude == null) null else Pair(parkingLot.latitude, parkingLot.longitude)
                        openingTime.value = parkingLot.businessHoursStart?.let {
                                Pair(it.substring(11, 13).toInt(), it.substring(14, 16).toInt())
                            } ?: Pair(0, 0)
                        closingTime.value = parkingLot.businessHoursEnd?.let {
                                Pair(it.substring(11, 13).toInt(), it.substring(14, 16).toInt())
                            } ?: Pair(1, 0)
                        daysOpen.value = parkingLot.openDaysFlag?.getFlags() ?: listOf()
                        parkingApi.getImages(parkingLot.parkingLotUuid).let {
                            if (it.errors.isEmpty()) {
                                images.value = it.data?.content?.map { image ->
                                    ImageUpload(
                                        status = ImageUploadState.UPLOADED,
                                        image = image,
                                        remoteUrl = parkingApi.getImage(image.id)
                                    )
                                } ?: listOf()
                            } else {
                                println()
                            }
                        }
                        parkingLot.rate?.let {
                            rateUuid.value = it.id
                            rateBuilderViewModel.restore(it)
                        }
                    }
                }
            }
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            if (page.value == 2) {
                Timber.d("Updating rate")
                rateApi.updateRate(parkingLotUuid.value!!, rateBuilderViewModel.createDTO())
            } else {
                parkingApi.saveDraft(createDTO())
            }
        }
    }

    fun createDraft() {
        viewModelScope.launch {
            if (parkingLotUuid.value == null) {
                parkingApi.createDraft(createDTO())
                parkingApi.getParkingLot().let {
                    if (it.errors.isEmpty()) {
                        parkingLotUuid.value = it.data?.parkingLotUuid
                    }
                }
            } else {
                parkingApi.saveDraft(createDTO())
            }
        }
    }

    fun setToPending() {
        viewModelScope.launch {
            val result = parkingApi.setToPending()
            if (result.errors.isEmpty()) {
                page.value += 1
            }
        }
    }

    private fun createDTO(): ParkingLotDraftDTO {
        return ParkingLotDraftDTO(
            name = parkingLotName.value,
            address = parkingLotAddress.value,
            latitude = location.value!!.first,
            longitude = location.value!!.second,
            openDaysFlag = daysOpen.value.toIntFlag(),
            capacity = capacity.value,
            description = description.value,
            businessHoursStart = "1970-01-01T${openingTime.value.first.toString().padStart(2, '0')}:${openingTime.value.second.toString().padStart(2, '0')}:00Z",
            businessHoursEnd = "1970-01-01T${closingTime.value.first.toString().padStart(2, '0')}:${closingTime.value.second.toString().padStart(2, '0')}:00Z",
        )
    }

    fun validate(page: Int): Boolean {
        validations[page]?.invoke()
        return errors.value.isEmpty()
    }

    fun validateAll() {
        validations.onEach { (index, function) ->
            function.invoke()
            if (errors.value.isNotEmpty()) {
                page.value = index
                return
            }
        }
    }
}

enum class ImageUploadState {
    QUEUED,
    UPLOADING,
    UPLOADED,
    ERROR,
    RESTORED
}

data class ImageUpload(
    val status: ImageUploadState,
    val fileUri: Uri? = null,
    val image: ParkingLotImage? = null,
    var remoteUrl: String? = null,
)