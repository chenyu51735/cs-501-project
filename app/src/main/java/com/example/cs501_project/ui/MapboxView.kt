package com.example.cs501_project.ui

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cs501_project.R
import com.example.cs501_project.viewmodel.CustomMapMarker
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

// separate mapbox view for the location screen
@Composable
fun MapboxView(
    initialCameraPosition: Point?, // the initial camera position is based on the current location
    predefinedMarkerLocations: List<Point> = emptyList(), // markers from historical place suggestions
    onMapClick: ((Point) -> Unit)? = null, // callback for map clicks
    customMarkers: List<CustomMapMarker>,
    onNewCustomMarkerAdded: (Point, String, String) -> Unit,
) {
    // mutable state that holds android view that displays map and the remember ensures MapView instance is retained across recompositions
    val mapboxMapView = remember { mutableStateOf<MapView?>(null) }
    // holds mutable state for MapboxMap object which has programmatic control over the map such as adding layers
    val mapboxMap = remember { mutableStateOf<MapboxMap?>(null) }
    // mutable state for AnnotationPlugin, which keeps track of annotations on the map (in our case markers)
    val annotationPlugin = remember { mutableStateOf<AnnotationPlugin?>(null) }
    // specific type of AnnotationManager that is used for placing point markers
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }

    var isCustomMarkerDialogVisible by remember { mutableStateOf(false) }
    // the lat and lon of the new custom marker
    var clickedPointForNewMarker by remember { mutableStateOf<Point?>(null) }

    if (isCustomMarkerDialogVisible && clickedPointForNewMarker != null) {
        // show the custom marker dialog if a point on the map is clicked
        CustomMarkerDialog(
            onDismissRequest = { isCustomMarkerDialogVisible = false },
            onConfirm = { title, symbol ->
                onNewCustomMarkerAdded(clickedPointForNewMarker!!, title, symbol)
                isCustomMarkerDialogVisible = false
                clickedPointForNewMarker = null // reset the clicked point
            }
        )
    }

    Column {
        // used to embed an AndroidView (MapView for us) in our UI
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(bottom = 16.dp),
            factory = { ctx ->
                MapView(ctx).apply {
                    mapboxMapView.value = this // stores the created MapView instance in the mapboxMapView state
                    this.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        mapboxMap.value = this.mapboxMap
                        annotationPlugin.value = this@apply.annotations // this@apply explicitly refers to the outer MapView
                        pointAnnotationManager.value = annotationPlugin.value?.createPointAnnotationManager()

                        // adding a default marker image
                        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_marker)
                        style.addImage("default_marker", bitmap)

                        // making the map zoom in on specific location
                        initialCameraPosition?.let {
                            mapboxMap.value?.setCamera(
                                CameraOptions.Builder()
                                    .center(it)
                                    .zoom(12.0)
                                    .build()
                            )
                        }

                        // add predefined markers
                        predefinedMarkerLocations.forEach { point ->
                            pointAnnotationManager.value?.create(
                                PointAnnotationOptions()
                                    .withPoint(point)
                                    .withIconImage("default_marker")
                                    .withIconSize(0.1)
                            )
                        }

                        // set up map click listener if the callback is provided
                        onMapClick?.let { clickCallback ->
                            mapboxMap.value?.addOnMapClickListener { point ->
                                clickCallback(point)
                                clickedPointForNewMarker = point
                                isCustomMarkerDialogVisible = true
                                true
                            }
                        }
                    }
                }
            },


            // updating the mapview when it receives new info
            update = { mapView ->
                mapboxMapView.value = mapView
                mapboxMap.value?.getStyle { style ->
                    initialCameraPosition?.let {
                        mapboxMap.value?.setCamera(
                            CameraOptions.Builder()
                                .center(it)
                                .zoom(12.0)
                                .build()
                        )
                    }

                    val context = mapView.context

                    pointAnnotationManager.value?.deleteAll()
                    // reload all the markers, both predefined and user-added
                    predefinedMarkerLocations.forEach { point ->
                        pointAnnotationManager.value?.create(
                            PointAnnotationOptions()
                                .withPoint(point)
                                .withIconImage("default_marker")
                                .withIconSize(0.1)
                        )
                    }
                    customMarkers.forEach { customMarker ->
                        val symbolNameWithoutExtension = customMarker.symbol.substringBeforeLast(".")
                        val resId = context.resources.getIdentifier(
                            symbolNameWithoutExtension,
                            "drawable",
                            context.packageName
                        )
                        if (resId != 0) {
                            // if the resource drawable id exists, try fetching it and creating bitmap
                            try {
                                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                                style.addImage(customMarker.symbol, bitmap) // attempt to add the image
                            } catch (e: Exception) {
                                Log.e("MapboxView", "Error loading image: ${customMarker.symbol}", e)
                            }
                        }
                        var iconSize = 0.1
                        if (customMarker.symbol == "flower.png" ||
                            customMarker.symbol == "movie.png" ||
                            customMarker.symbol == "park.png" ||
                            customMarker.symbol == "plane.png") { // symbols too big
                            iconSize = 0.03
                        }
                        pointAnnotationManager.value?.create(
                            PointAnnotationOptions()
                                .withPoint(customMarker.point)
                                .withIconImage(customMarker.symbol)
                                .withIconSize(iconSize)
                        )
                    }
                }
            }
        )
    }
}


// card to display under map that represent user made markers
@Composable
fun CustomLocationCard(marker: CustomMapMarker, onCardClick: (CustomMapMarker) -> Unit) {
    // custom map marker has point, title, symbol
    val context = LocalContext.current
    val symbolName = marker.symbol
    val symbolNameWithoutExtension = symbolName.substringBeforeLast(".")

    val drawableResourceId = remember(marker) {
        context.resources.getIdentifier(
            symbolNameWithoutExtension,
            "drawable",
            context.packageName
        )
    }

    // add ability to edit title
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable{ onCardClick(marker) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            if (drawableResourceId != 0) {
                Image(
                    painter = painterResource(id = drawableResourceId),
                    contentDescription = marker.title,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                // fallback if the drawable resource is not found
                Image(
                    painter = painterResource(R.drawable.default_marker), // use default if custom not found
                    contentDescription = marker.title,
                    modifier = Modifier.size(100.dp)
                )
                Text(text = "Error loading symbol: $symbolName")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = marker.title)
        }
    }
}