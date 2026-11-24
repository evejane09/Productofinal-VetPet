package com.evelin.vetpet.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(modifier: Modifier = Modifier) {
    var notificaciones by remember { mutableStateOf(true) }
    var modoOscuro by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de Perfil
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Dr(a). Evelin", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Veterinaria Principal", color = MaterialTheme.colorScheme.outline)
            Text("admin@vetpet.com", color = MaterialTheme.colorScheme.outline, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones de configuración
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Notificaciones Push", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Modo Oscuro", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Switch(checked = modoOscuro, onCheckedChange = { modoOscuro = it })
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Cerrar Sesión
            Button(
                onClick = { /* Aquí podrías regresar al login */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}