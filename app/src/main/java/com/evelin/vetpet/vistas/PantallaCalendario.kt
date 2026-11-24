package com.evelin.vetpet.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCalendario(modifier: Modifier = Modifier) {
    val diasSemana = listOf("L", "M", "M", "J", "V", "S", "D")
    val diasMes = (1..31).toList()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Calendario de Turnos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Cabecera del mes
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior") }
                Text("Octubre 2026", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                diasSemana.forEach { dia ->
                    Text(dia, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cuadrícula del calendario (simplificada)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(diasMes.size) { index ->
                    val dia = diasMes[index]
                    // Simulamos que los días 5, 12, 18 y 25 tienen citas
                    val tieneCita = listOf(5, 12, 18, 25).contains(dia)
                    val esHoy = dia == 24

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = if (esHoy) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dia.toString(),
                                color = if (esHoy) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (tieneCita) FontWeight.Bold else FontWeight.Normal
                            )
                            if (tieneCita && !esHoy) {
                                Box(modifier = Modifier.size(4.dp).background(Color(0xFFF57C00), CircleShape))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Turnos para Hoy", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            Spacer(modifier = Modifier.height(8.dp))

            // Lista de turnos de hoy
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFF57C00), CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("10:00 AM - Firulais", fontWeight = FontWeight.Bold)
                        Text("Consulta general", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }
                }
            }
        }
    }
}