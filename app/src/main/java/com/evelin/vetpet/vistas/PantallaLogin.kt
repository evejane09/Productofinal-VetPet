package com.evelin.vetpet.vistas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(onLoginSuccess: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    // Para mostrar el mensaje del servidor (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color(0xFF00897B))
            Text("VetPet", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
            Text("Tu clínica veterinaria en un solo lugar", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo o Nombre") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (correo.isNotBlank()) {
                        cargando = true
                        // Aquí hacemos la petición al servidor (Práctica 12)
                        val urlApi = "http://10.0.2.2:8080/vetpet_api/mensaje.php"

                        scope.launch(Dispatchers.IO) {
                            try {
                                val url = URL(urlApi)
                                val conn = url.openConnection() as HttpURLConnection
                                conn.requestMethod = "GET"
                                conn.connectTimeout = 5000
                                conn.readTimeout = 5000

                                if (conn.responseCode == 200) {
                                    val respuesta = conn.inputStream.bufferedReader().use { it.readText() }
                                    val json = JSONObject(respuesta)
                                    val datos = json.getJSONObject("datos")
                                    val contenido = datos.getString("contenido")

                                    withContext(Dispatchers.Main) {
                                        // Mostramos lo que dijo el servidor en un mensajito
                                        snackbarHostState.showSnackbar(contenido)
                                        cargando = false
                                        // Esperamos un ratito y entramos a la app
                                        delay(1500)
                                        onLoginSuccess()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar("Error del servidor")
                                        cargando = false
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    snackbarHostState.showSnackbar ("Error: ${e.message}")
                                    cargando = false
                                }
                            }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Escribe tu nombre o correo") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                shape = RoundedCornerShape(12.dp),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}