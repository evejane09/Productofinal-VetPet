package com.evelin.vetpet.vistas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilMascota(mascota: Mascota, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de la Mascota", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto simulada con el emoji de la mascota
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Text(mascota.icono, fontSize = 50.sp)
            }

            Text(
                mascota.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(mascota.especie, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta del dueño
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Propietario", fontSize = 12.sp, color = Color.Gray)
                        Text(mascota.dueno, fontWeight = FontWeight.Bold)
                        Text("+51 987 654 321", fontSize = 14.sp)
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Llamar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historial clínico
            Text(
                "Historial Clínico",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("10 nov 2026 - Consulta general", fontWeight = FontWeight.Bold)
                    Text("Sin complicaciones. Mantener dieta actual.", fontSize = 14.sp, color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("05 oct 2026 - Vacunación antirrábica", fontWeight = FontWeight.Bold)
                    Text("Vacuna aplicada correctamente.", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}