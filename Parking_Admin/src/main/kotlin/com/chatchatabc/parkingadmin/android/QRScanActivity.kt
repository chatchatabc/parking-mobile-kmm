package com.chatchatabc.parkingadmin.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import com.chatchatabc.parking.compose.Popup
import com.chatchatabc.parking.compose.Theme.AppTheme
import com.chatchatabc.parking.di.QRScanModule
import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.viewModel.QRScanState
import com.chatchatabc.parking.viewModel.QRScanViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext.loadKoinModules
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScanActivity: ComponentActivity() {
    val koinComponent = loadKoinModules(QRScanModule)

    val viewModel: QRScanViewModel by inject()

    val onCameraPermissionGranted =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.hasPermission.value = true
            } else {
                viewModel.hasPermission.value = false
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.hasPermission.value = EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)

        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            openQRScanner()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Camera permission is required to scan QR code",
                0,
                Manifest.permission.CAMERA
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    fun openQRScanner() {
        setContent {
            val cameraExecutor = Executors.newSingleThreadExecutor()

            val parkingRegex = "VEHICLE:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}):VEHICLE".toRegex()

            val analyzer = QRAnalyzer {
                if (viewModel.uiState.value == QRScanState.SCAN) {
                    it.forEach {
                        if (it.rawValue?.matches(parkingRegex) == true) {
                            viewModel.checkVehicle(parkingRegex.find(it.rawValue!!)!!.groupValues[1])
                            return@QRAnalyzer
                        }
                    }
                }
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(480, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, analyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            setContent {
                AppTheme {
                    Surface {
                        Box(Modifier.fillMaxSize()) {
                            val state by viewModel.uiState.collectAsState()

                            LaunchedEffect(state) {
                                println("STATE CHANGED: $state")
                            }

                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                QRScanLayout(
                                    imageAnalyzer,
                                    cameraSelector,
                                    cameraExecutor
                                )
                            }

                            val currentVehicle by viewModel.currentVehicle.collectAsState()
                            val currentInvoice by viewModel.currentInvoice.collectAsState()

                            if (state != QRScanState.SCAN) {
                                AlertDialog(
                                    onDismissRequest = {
                                        viewModel.cancel()
                                    },
                                    properties = DialogProperties(
                                        dismissOnBackPress = true,
                                        dismissOnClickOutside = true,
                                        usePlatformDefaultWidth = false,
                                    ),
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    when (state) {
                                        QRScanState.VEHICLE_PARKING -> {
                                            StartParkingView(
                                                vehicle = currentVehicle!!,
                                                onCancel = {
                                                    viewModel.cancel()
                                                },
                                                onStartParking = { estimate ->
                                                    viewModel.park(estimate)
                                                }
                                            )
                                        }
                                        QRScanState.VEHICLE_PARKED -> {
                                            Popup(
                                                title = "Vehicle Parked",
                                                icon = Icons.Filled.Check,
                                                content = "Vehicle is parked",
                                                buttons = mapOf(
                                                    "Got it" to {
                                                        viewModel.cancel()
                                                    }
                                                )
                                            )
                                        }

                                        QRScanState.VEHICLE_LEAVING -> {
                                            Column(Modifier.padding(32.dp)) {
                                                currentVehicle?.let {
                                                    VehicleInfo(vehicle = currentVehicle!!, title = "Vehicle Leaving")
                                                }
                                                currentInvoice?.let {
                                                    ParkingEndView(invoice = currentInvoice!!, onCancel = {
                                                        viewModel.cancel()
                                                    }, onEndInvoice = {
                                                        viewModel.leave(it)
                                                    })
                                                }
                                            }
                                        }

                                        QRScanState.VEHICLE_LEFT -> {
                                            Popup(
                                                title = "Vehicle Left",
                                                icon = Icons.Filled.WavingHand,
                                                content = "The vehicle has been successfully released from the parking lot.",
                                                buttons = mapOf(
                                                    "Got it" to {
                                                        viewModel.cancel()
                                                    }
                                                )
                                            )
                                        }

                                        QRScanState.VEHICLE_INVALID -> {
                                            Popup(
                                                title = "Invalid Vehicle",
                                                icon = Icons.Filled.QuestionMark,
                                                content = "The vehicle is not registered or has been deleted. Please ask the driver to register the vehicle.",
                                                buttons = mapOf(
                                                    "I understand" to {
                                                        viewModel.cancel()
                                                    }
                                                )
                                            )
                                        }

                                        QRScanState.LOADING -> {
                                            Card {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(32.dp)
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                    Text(
                                                        text = "Loading...",
                                                        style = MaterialTheme.typography.headlineSmall
                                                    )
                                                }
                                            }
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun QRScanLayout(
        analyzer: ImageAnalysis,
        cameraSelector: CameraSelector,
        cameraExecutor: ExecutorService
    ) {
        val context = LocalContext.current

        Column() {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(32.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalIconButton(onClick = {
                    finish()
                }, colors = IconButtonDefaults.filledIconButtonColors(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onPrimary
                )) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Scan Vehicle QR Code",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Box(Modifier.fillMaxSize()) {
                val previewView by remember {
                    mutableStateOf(PreviewView(context))
                }

                val preview by remember {
                    mutableStateOf(
                        Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    )
                }

                AndroidView(
                    modifier = Modifier
                        .padding(64.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                        .clip(MaterialTheme.shapes.large),
                    factory = {
                        previewView
                    }
                ) {
                    if (EasyPermissions.hasPermissions(context, *REQUIRED_PERMISSIONS)) {
                        val cameraProviderFuture =
                            ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider: ProcessCameraProvider =
                                cameraProviderFuture.get()

                            // Use runOnUiThread to ensure this runs on the Main thread
                            (context as ComponentActivity).runOnUiThread {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    context,
                                    cameraSelector,
                                    preview,
                                    analyzer
                                )
                            }
                        }, cameraExecutor)
                    } else {
                        ActivityCompat.requestPermissions(
                            context as ComponentActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            0
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

class QRAnalyzer(private val onQrCodesDetected: (qrCodes: List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val qrCodes = barcodes.filter {
                        it.format == Barcode.FORMAT_QR_CODE
                    }
                    onQrCodesDetected(qrCodes)
                }
                .addOnFailureListener {

                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

@Composable
fun VehicleInfo(vehicle: Vehicle, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(Modifier.padding(16.dp)) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    vehicle?.plateNumber ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParkingEndView(invoice: Invoice, onCancel: () -> Unit, onEndInvoice: (paid: Boolean) -> Unit) {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)

    Card(colors = CardDefaults.elevatedCardColors(), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Invoice")
            Text("Started", style = MaterialTheme.typography.labelLarge)
            Text(invoice.startAt.toJavaLocalDateTime().format(formatter), style = MaterialTheme.typography.headlineSmall)
            Text("Ending", style = MaterialTheme.typography.labelLarge)
            Text(LocalDateTime.now().toKotlinLocalDateTime().toJavaLocalDateTime().format(formatter), style = MaterialTheme.typography.headlineSmall)
            Text("Duration", style = MaterialTheme.typography.labelLarge)
            Text(ChronoUnit.HOURS.between(invoice.startAt.toJavaLocalDateTime(), LocalDateTime.now().toKotlinLocalDateTime().toJavaLocalDateTime()).toString(), style = MaterialTheme.typography.headlineSmall)
            Text("Total", style = MaterialTheme.typography.labelLarge)
            Text("PHP ${invoice.total}", style = MaterialTheme.typography.headlineSmall)
        }
    }
    Row(Modifier.fillMaxWidth()) {
        FilledTonalIconButton(
            onClick = {
                onCancel()
            }, colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), modifier = Modifier
        ) {
            Icon(Icons.Filled.Cancel, contentDescription = "Cancel")
        }
        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = {
                onEndInvoice(true)
            }
        ) {
            Text("Mark as Paid")
        }

        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = {
                onEndInvoice(false)
            }
        ) {
            Text("End Session")
        }
    }
}

@Composable
fun StartParkingView(vehicle: Vehicle, onStartParking: (Int) -> Unit, onCancel: () -> Unit) {
    var estimate by rememberSaveable {
        mutableStateOf(2)
    }

    LaunchedEffect(Unit) {
        estimate = 0
    }

    Column(Modifier.padding(16.dp)) {
        VehicleInfo(vehicle, "Start Parking")
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Estimated Stay in hours")
                Row(Modifier.height(IntrinsicSize.Max)) {
                    FilledTonalIconButton(
                        onClick = {
                            estimate--
                        }, enabled = estimate > 0
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Remove")
                    }
                    Box(
                        Modifier
                            .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
                            .fillMaxHeight()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$estimate hours", modifier = Modifier.padding(8.dp))
                    }
                    FilledTonalIconButton(onClick = { estimate++ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp)) {
            FilledTonalIconButton(onClick = {
                onCancel()
            }, colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            FilledTonalIconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onStartParking(estimate)
                }
            ) {
                Text("Start Parking")
            }

        }
    }
}