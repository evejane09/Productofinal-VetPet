package com.vistas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.evelin.vetpet.vistas.PantallaAgenda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Controla si el usuario está en el Login o en la App
    var mostrarLogin by remember { mutableStateOf(true) }
    // Controla la pestaña seleccionada en la barra inferior
    var opcionSeleccionada by remember { mutableIntStateOf(0) }

    if (mostrarLogin) {
        // Pantalla de Login (Conexión con PHP/MySQL)
        PantallaLogin(onLoginSuccess = { mostrarLogin = false })
    } else {
        // App Principal
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF10B981) // Tu nuevo verde esmeralda
                ) {
                    // 1. Inicio
                    NavigationBarItem(
                        selected = opcionSeleccionada == 0,
                        onClick = { opcionSeleccionada = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFFD1FAE5) // Verde muy claro
                        )
                    )

                    // 2. Agenda
                    NavigationBarItem(
                        selected = opcionSeleccionada == 1,
                        onClick = { opcionSeleccionada = 1 },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agenda") },
                        label = { Text("Agenda") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFFD1FAE5)
                        )
                    )

                    // 3. Pacientes
                    NavigationBarItem(
                        selected = opcionSeleccionada == 2,
                        onClick = { opcionSeleccionada = 2 },
                        icon = { Icon(Icons.Default.Pets, contentDescription = "Pacientes") },
                        label = { Text("Pacientes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFFD1FAE5)
                        )
                    )

                    // 4. Mapa (Sedes y Geolocalización)
                    NavigationBarItem(
                        selected = opcionSeleccionada == 3,
                        onClick = { opcionSeleccionada = 3 },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Mapa") },
                        label = { Text("Mapa") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFFD1FAE5)
                        )
                    )

                    // 5. Perfil
                    NavigationBarItem(
                        selected = opcionSeleccionada == 4,
                        onClick = { opcionSeleccionada = 4 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFFD1FAE5)
                        )
                    )
                }
            }
        ) { paddingValues ->
            // Aquí se intercambian las pantallas según el botón que toques
            when (opcionSeleccionada) {
                0 -> PantallaInicio(modifier = Modifier.padding(paddingValues))
                1 -> PantallaAgenda(modifier = Modifier.padding(paddingValues))
                2 -> PantallaPacientes(modifier = Modifier.padding(paddingValues))
                3 -> PantallaUbicacion(modifier = Modifier.padding(paddingValues))
                4 -> PantallaPerfil(modifier = Modifier.padding(paddingValues))
            }
        }
    }
}