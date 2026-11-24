package com.evelin.vetpet.vistas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// 1. DEFINIMOS LA ESTRUCTURA DE UN LUGAR
data class Lugar(val nombre: String, val descripcion: String, val lat: Double, val lon: Double)

@SuppressLint("MissingPermission")
@Composable
fun PantallaUbicacion(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var estado by remember { mutableStateOf("Sedes de VetPet en Tacna") }


    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    val tacnaLat = -18.04488
    val tacnaLon = -70.24319

    val mapView = remember { MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        controller.setZoom(15.0)

        controller.setCenter(GeoPoint(tacnaLat, tacnaLon))
    }}


    val lugaresVetPet = listOf(
        Lugar(
            nombre = "VetPet - Sede Tacna (Central)",
            descripcion = "Av. Humboldt, cerca al Paradero Ruta 2B",
            lat = tacnaLat,
            lon = tacnaLon
        ),
        Lugar(
            nombre = "VetPet - Sede Mercado Central",
            descripcion = "Calle Alto de la Alianza (Zona céntrica)",
            lat = -18.01500,
            lon = -70.25000
        ),
        Lugar(
            nombre = "VetPet - Sede Bolognesi",
            descripcion = "Av. Bolognesi, zona comercial",
            lat = -18.02500,
            lon = -70.24500
        )
    )


    LaunchedEffect(mapView) {
        mapView.overlays.clear()

        lugaresVetPet.forEach { lugar ->
            val punto = GeoPoint(lugar.lat, lugar.lon)
            val marcador = Marker(mapView).apply {
                position = punto
                title = lugar.nombre
                snippet = lugar.descripcion // La descripción sale al tocar el pin
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(marcador)
        }
        mapView.invalidate()
    }

    fun obtenerUbicacion() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude
                estado = "Tu ubicación: Lat: ${"%.4f".format(lat)}, Lng: ${"%.4f".format(lon)}"

                val punto = GeoPoint(lat, lon)
                mapView.controller.animateTo(punto)
                mapView.controller.setZoom(16.0)
                mapView.invalidate()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) obtenerUbicacion() else estado = "Permiso denegado."
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )


        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(estado, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                Text("Toca los pines verdes para ver la dirección exacta.", fontSize = 12.sp, color = Color.Gray)
            }
        }


        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacion()
                } else {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver mi ubicación GPS", fontWeight = FontWeight.Bold)
        }
    }
}