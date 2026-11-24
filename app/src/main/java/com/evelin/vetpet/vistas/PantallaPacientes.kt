package com.evelin.vetpet.vistas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelin.vetpet.viewmodel.ViewModelCitas

// Datos de prueba para cuando no haya base de datos
data class Mascota(val nombre: String, val especie: String, val dueno: String, val icono: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPacientes(modifier: Modifier = Modifier, viewModel: ViewModelCitas = viewModel()) {
    val estado by viewModel.estadouli.collectAsState()
    var busqueda by remember { mutableStateOf("") }
    var mascotaSeleccionada by remember { mutableStateOf<Mascota?>(null) }

    // Convertimos las citas de la base de datos en "Mascotas únicas"
    val mascotasReales = estado.citas.map { cita ->
        Mascota(
            nombre = cita.nombreMascota,
            especie = cita.especie,
            dueno = cita.nombreDueno,
            icono = if (cita.especie.lowercase().contains("gato")) "🐱" else "🐶"
        )
    }.distinctBy { it.nombre } // Evita que salga duplicado si tiene varias citas

    // Si no hay datos en la nube, mostramos unos de prueba para que no se vea vacío
    val listaFinal = if (mascotasReales.isEmpty()) {
        listOf(
            Mascota("Firulais", "Perro / Golden", "Juan Pérez", "🐶"),
            Mascota("Michi", "Gato / Siamés", "Ana Gómez", "🐱")
        )
    } else {
        mascotasReales
    }

    if (mascotaSeleccionada != null) {
        PantallaPerfilMascota(
            mascota = mascotaSeleccionada!!,
            onBack = { mascotaSeleccionada = null }
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Directorio de Pacientes", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar mascota o dueño...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFinal.filter { it.nombre.contains(busqueda, ignoreCase = true) || it.dueno.contains(busqueda, ignoreCase = true) }) { mascota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { mascotaSeleccionada = mascota },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(50.dp).clip(CircleShape).padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(mascota.icono, fontSize = 32.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mascota.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text(mascota.especie, color = MaterialTheme.colorScheme.outline, fontSize = 14.sp)
                                    Text("Dueño: ${mascota.dueno}", fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = "Ver perfil")
                            }
                        }
                    }
                }
            }
        }
    }
}