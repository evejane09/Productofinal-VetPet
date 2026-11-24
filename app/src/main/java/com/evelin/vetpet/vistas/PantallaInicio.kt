package com.evelin.vetpet.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelin.vetpet.modelos.CitaVeterinaria
import com.evelin.vetpet.modelos.EstadoCita
import com.evelin.vetpet.viewmodel.EstadoUICitas
import com.evelin.vetpet.viewmodel.EventoUICita
import com.evelin.vetpet.viewmodel.ViewModelCitas
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgenda(modifier: Modifier = Modifier, viewModel: ViewModelCitas = viewModel()) {
    val estado by viewModel.estadouli.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Control del panel deslizante de "Nueva Cita"
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(key1 = Unit) {
        viewModel.eventouli.collect {
            if (it is EventoUICita.MostrarSnackbar) snackbar.showSnackbar(message = it.mensaje)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Agenda", fontWeight = FontWeight.Bold, color = Color.Black) },
                actions = {
                    // El botón "+ Agendar"
                    Button(
                        onClick = { mostrarBottomSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Agendar", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF9FAFB))) {

            // Calendario Horizontal
            CalendarioHorizontal()

            // Resumen de turnos
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("${estado.totalCitas} citas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("${estado.citasPendientes} pendientes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                Text("${estado.citasCompletadas} completadas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // Lista tipo Timeline
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.citasFiltradas) { cita ->
                    TarjetaTimeline(cita = cita, onEliminar = { viewModel.eliminarCita(cita.idCita) }, onCompletar = { viewModel.completarCita(cita.idCita) })
                }
            }
        }
    }

    // PANEL DESLIZANTE PARA AGENDAR
    if (mostrarBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            FormularioRegistroCita(
                st = estado,
                vm = viewModel,
                onGuardarExitoso = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) mostrarBottomSheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarioHorizontal() {
    val dias = (1..30).toList()
    var diaSeleccionado by remember { mutableStateOf(24) }
    val letrasDias = listOf("L", "M", "M", "J", "V", "S", "D")

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dias) { dia ->
            val esSeleccionado = dia == diaSeleccionado
            // Simulamos que el día 24 tiene el color verde de la imagen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(if (esSeleccionado) Color(0xFF10B981) else Color.Transparent, CircleShape)
                    .clickable { diaSeleccionado = dia }
                    .padding(8.dp)
            ) {
                Text(letrasDias[dia % 7], fontSize = 12.sp, color = if (esSeleccionado) Color.White else Color.Gray)
                Text(dia.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (esSeleccionado) Color.White else Color.Black)
                if (!esSeleccionado) {
                    Box(modifier = Modifier.size(4.dp).background(Color(0xFF10B981), CircleShape))
                }
            }
        }
    }
}

@Composable
fun TarjetaTimeline(cita: CitaVeterinaria, onEliminar: () -> Unit, onCompletar: () -> Unit) {
    val colorEstado = if (cita.estado == EstadoCita.PENDIENTE) Color(0xFFF59E0B) else Color(0xFF10B981)
    val textoEstado = if (cita.estado == EstadoCita.PENDIENTE) "Agendada" else "Completada"

    Row(modifier = Modifier.fillMaxWidth()) {
        // Columna del tiempo (Izquierda)
        Column(modifier = Modifier.width(50.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = cita.fechaHora.substringAfter(" "), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // Tarjeta de la cita (Derecha)
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                // Barra de color lateral
                Box(modifier = Modifier.width(4.dp).height(40.dp).background(colorEstado, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(cita.nombreMascota, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(cita.motivo, fontSize = 12.sp, color = Color.Gray)
                }

                // Etiqueta de estado
                Surface(color = colorEstado.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = textoEstado,
                        color = colorEstado, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// --- EL FORMULARIO (Ahora dentro del BottomSheet) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioRegistroCita(st: EstadoUICitas, vm: ViewModelCitas, onGuardarExitoso: () -> Unit) {
    // ... Aquí pegas la misma lógica que tenías antes (los Dropdowns de Especie, Motivo, Sede) ...
    // Solo asegúrate de que al final, el botón de "Guardar turno" llame a vm.registrarCita() y luego a onGuardarExitoso()

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text("Agendar Nueva Cita", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // (Tus campos de texto: Mascota, Dueño, etc. van aquí)
        OutlinedTextField(value = st.nombreMascota, onValueChange = vm::cambiarMascota, label = { Text("Nombre de la Mascota") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = st.nombreDueno, onValueChange = vm::cambiarDueno, label = { Text("Nombre del Dueño") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        // (Aquí irían tus Dropdowns de Especie, Motivo y Sede)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                vm.registrarCita()
                onGuardarExitoso()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            Text("Guardar Cita", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}