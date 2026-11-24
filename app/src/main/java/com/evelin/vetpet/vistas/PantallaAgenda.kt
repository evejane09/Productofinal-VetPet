package com.evelin.vetpet.vistas

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelin.vetpet.modelos.CitaVeterinaria
import com.evelin.vetpet.modelos.EstadoCita
import com.evelin.vetpet.viewmodel.EstadoUICitas
import com.evelin.vetpet.viewmodel.EventoUICita
import com.evelin.vetpet.viewmodel.ViewModelCitas
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgenda(modifier: Modifier = Modifier, viewModel: ViewModelCitas = viewModel()) {
    val estado by viewModel.estadouli.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    // Estados para controlar las ventanas flotantes de Fecha y Hora
    var mostrarDialogoFecha by remember { mutableStateOf(false) }
    var mostrarDialogoHora by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventouli.collect {
            if (it is EventoUICita.MostrarSnackbar) snackbar.showSnackbar(message = it.mensaje)
        }
    }

    // --- VENTANA FLOTANTE: CALENDARIO ---
    if (mostrarDialogoFecha) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarDialogoFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val fechaSeleccionada = formatter.format(millis)

                        // Extraemos la hora actual si es que ya se había seleccionado antes
                        val horaActual = if (estado.fechaHora.contains(" ")) estado.fechaHora.substringAfter(" ") else ""
                        viewModel.cambiarFecha("$fechaSeleccionada $horaActual".trim())
                    }
                    mostrarDialogoFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoFecha = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- VENTANA FLOTANTE: RELOJ ---
    if (mostrarDialogoHora) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        AlertDialog(
            onDismissRequest = { mostrarDialogoHora = false },
            confirmButton = {
                TextButton(onClick = {
                    val horaFormateada = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    // Extraemos la fecha actual si es que ya se había seleccionado antes
                    val fechaActual = if (estado.fechaHora.contains(" ")) estado.fechaHora.substringBefore(" ") else estado.fechaHora
                    viewModel.cambiarFecha("${fechaActual.trim()} $horaFormateada".trim())
                    mostrarDialogoHora = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoHora = false }) { Text("Cancelar") } },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("VetAgenda", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Clínica Veterinaria San Marcos", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = { Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.padding(start = 12.dp)) },
                actions = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.padding(end = 12.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00695C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { p ->
        Column(modifier = Modifier.fillMaxSize().padding(p).background(Color(0xFFF5F7FA))) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ResumenCitas(st = estado) }
                item {
                    FormularioRegistroCita(
                        st = estado,
                        vm = viewModel,
                        onFechaClick = { mostrarDialogoFecha = true },
                        onHoraClick = { mostrarDialogoHora = true }
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Turnos de hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00695C))
                        Text("Ver todos >", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                if (estado.citasFiltradas.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                                Text("No hay turnos registrados", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
                            }
                        }
                    }
                }

                items(estado.citasFiltradas) { cita ->
                    TarjetaCitaItem(cita = cita, onEliminar = { viewModel.eliminarCita(cita.idCita) }, onCompletar = { viewModel.completarCita(cita.idCita) })
                }
            }
        }
    }
}

@Composable
private fun ResumenCitas(st: EstadoUICitas) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TarjetaMetrica(titulo = "Total", valor = st.totalCitas.toString(), color = Color(0xFFE0F2F1), icono = Icons.Default.Pets, colorIcono = Color(0xFF00695C), modifier = Modifier.weight(1f))
        TarjetaMetrica(titulo = "Pendiente", valor = st.citasPendientes.toString(), color = Color(0xFFFFF3E0), icono = Icons.Default.Schedule, colorIcono = Color(0xFFF57C00), modifier = Modifier.weight(1f))
        TarjetaMetrica(titulo = "Completado", valor = st.citasCompletadas.toString(), color = Color(0xFFE8F5E9), icono = Icons.Default.CheckCircle, colorIcono = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TarjetaMetrica(titulo: String, valor: String, color: Color, icono: ImageVector, colorIcono: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(32.dp).background(colorIcono, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
            Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorIcono)
            Text("turnos", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioRegistroCita(st: EstadoUICitas, vm: ViewModelCitas, onFechaClick: () -> Unit, onHoraClick: () -> Unit) {
    val listaEspecies = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")
    val listaMotivos = listOf("Consulta general", "Vacunación", "Control de peso", "Revisión dental", "Peluquería")

    var especieExpandida by remember { mutableStateOf(false) }
    var motivoExpandido by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddBox, contentDescription = null, tint = Color(0xFF00695C))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar nuevo turno", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00695C))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre Mascota
            OutlinedTextField(
                value = st.nombreMascota,
                onValueChange = vm::cambiarMascota,
                label = { Text("Nombre de la mascota") },
                placeholder = { Text("Ej: Firulais") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = st.errorMascota != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre Dueño
            OutlinedTextField(
                value = st.nombreDueno,
                onValueChange = vm::cambiarDueno,
                label = { Text("Nombre del dueño") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = st.errorDueno != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Especie (Dropdown)
            ExposedDropdownMenuBox(expanded = especieExpandida, onExpandedChange = { especieExpandida = !especieExpandida }) {
                OutlinedTextField(
                    value = st.especie,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = especieExpandida) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(expanded = especieExpandida, onDismissRequest = { especieExpandida = false }) {
                    listaEspecies.forEach { especie ->
                        DropdownMenuItem(text = { Text(especie) }, onClick = { vm.cambiarEspecie(especie); especieExpandida = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Motivo (Dropdown)
            ExposedDropdownMenuBox(expanded = motivoExpandido, onExpandedChange = { motivoExpandido = !motivoExpandido }) {
                OutlinedTextField(
                    value = st.motivo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Motivo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = motivoExpandido) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(expanded = motivoExpandido, onDismissRequest = { motivoExpandido = false }) {
                    listaMotivos.forEach { motivo ->
                        DropdownMenuItem(text = { Text(motivo) }, onClick = { vm.cambiarMotivo(motivo); motivoExpandido = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha y Hora (Clickables para abrir el calendario y reloj)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val fechaStr = if (st.fechaHora.contains(" ")) st.fechaHora.substringBefore(" ") else st.fechaHora
                val horaStr = if (st.fechaHora.contains(" ")) st.fechaHora.substringAfter(" ") else ""

                OutlinedTextField(
                    value = fechaStr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    placeholder = { Text("dd/mm/aaaa") },
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF00695C)) },
                    modifier = Modifier.weight(1f).clickable { onFechaClick() },
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = horaStr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora") },
                    placeholder = { Text("--:--") },
                    trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF00695C)) },
                    modifier = Modifier.weight(1f).clickable { onHoraClick() },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { vm.registrarCita() },
                enabled = !st.guardando,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar turno", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TarjetaCitaItem(cita: CitaVeterinaria, onEliminar: () -> Unit, onCompletar: () -> Unit) {
    val colorEstado = if (cita.estado == EstadoCita.PENDIENTE) Color(0xFFF57C00) else Color(0xFF2E7D32)
    val textoEstado = if (cita.estado == EstadoCita.PENDIENTE) "⏳ Pendiente" else "✔ Completado"
    val iconoEmoji = if (cita.especie.contains("Gato", ignoreCase = true)) "🐱" else "🐶"

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icono de mascota simulado
            Box(modifier = Modifier.size(50.dp).background(Color(0xFFE0F2F1), CircleShape), contentAlignment = Alignment.Center) {
                Text(iconoEmoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(cita.nombreMascota, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(cita.fechaHora, fontSize = 12.sp, color = Color.Gray)
                }
                Text(cita.motivo, fontSize = 12.sp, color = Color.Gray)
            }

            // Etiqueta de estado
            Surface(color = colorEstado.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = textoEstado,
                    color = colorEstado, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}