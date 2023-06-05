package com.chatchatabc.parkingclient.android.compose.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.chatchatabc.parking.realm.ParkingLotRealmObject
import com.chatchatabc.parkingclient.android.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@RequiresApi(Build.VERSION_CODES.O)
@GoogleMapComposable
@Composable
fun MarkerContainerComposable(
    pins: List<ParkingLotRealmObject>
) {
    val context = LocalContext.current

    val primaryColor = MaterialTheme.colorScheme.primary

    pins.forEach { pin ->
        val marker = rememberMarkerState(pin.id, LatLng(pin.latitude!!, pin.longitude!!))

        val icon = remember {
            BitmapDescriptorFactory.fromBitmap(
                createCustomMarkerBitmap(
                    context = context,
                    name = pin.name,
                    color = primaryColor.toArgb(),
                    subtext = "12/33 occupied"
                )
            )
        }

        Marker(state = marker, icon = icon)

        Circle(
            center = marker.position,
            radius = 20.0,
            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

private fun createCustomMarkerBitmap(
    name: String,
    subtext: String,
    context: Context,
    color: Int
): Bitmap {
    val drawable = ContextCompat.getDrawable(context, R.drawable.parking_icon)
    val iconWidth = drawable?.intrinsicWidth ?: 0
    val iconHeight = drawable?.intrinsicHeight ?: 0

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(Color.BLACK)
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }

    val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(Color.WHITE)
        style = Paint.Style.STROKE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        strokeWidth = 2f
        textSize = 32f
        textAlign = Paint.Align.LEFT
    }

    val subtextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(Color.BLACK)
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }

    val subtextOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(Color.WHITE)
        style = Paint.Style.STROKE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        strokeWidth = 1f
        textSize = 24f
        textAlign = Paint.Align.LEFT
    }

    val textBounds = Rect()
    textPaint.getTextBounds(name, 0, name.length, textBounds)

    val subtextBounds = Rect()
    subtextPaint.getTextBounds(subtext, 0, subtext.length, subtextBounds)

    val bitmapWidth = (iconWidth + Integer.max(textBounds.width(), subtextBounds.width())) * 2
    val bitmapHeight = Integer.max(iconHeight, textBounds.height() + subtextBounds.height())

    val outputBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    // Center the icon on the canvas
    val iconX = (bitmapWidth / 2) - (iconWidth / 2)
    val iconY = (bitmapHeight - iconHeight) / 2
    drawable?.setBounds(iconX, iconY, iconX + iconWidth, iconY + iconHeight)
    drawable?.draw(canvas)

    // Draw the text to the right of the icon
    val textX = (bitmapWidth / 2) + (iconWidth / 2) + 10
    val textY = bitmapHeight / 2 - (textBounds.height() / 2) + 10
    canvas.drawText(name, textX.toFloat(), textY.toFloat(), outlinePaint)
    canvas.drawText(name, textX.toFloat(), textY.toFloat(), textPaint)

    // Draw the subtext below the text
    val subtextY = textY + textBounds.height()
    canvas.drawText(subtext, textX.toFloat(), subtextY.toFloat(), subtextOutlinePaint)
    canvas.drawText(subtext, textX.toFloat(), subtextY.toFloat(), subtextPaint)

    return outputBitmap
}