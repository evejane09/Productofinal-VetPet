package com.evelin.vetpet.datos

import com.evelin.vetpet.modelos.CitaVeterinaria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Clase para recibir el JSON del Login
data class RespuestaServidor(val status: String, val mensaje: String)

// 1. Interfaz de la API (Retrofit)
interface VetPetApi {
    // Para el Login
    @GET("mensaje.php")
    suspend fun obtenerMensajeServidor(@Query("usuario") nombre: String): RespuestaServidor

    // Para traer las citas de la web (MySQL)
    @GET("citas.php")
    suspend fun obtenerCitasApi(): List<CitaVeterinaria>

    // Para guardar una cita
    @POST("guardar_cita.php")
    suspend fun registrarCitaApi(@Body cita: CitaVeterinaria): CitaVeterinaria
}

// 2. Interfaz de nuestro Repositorio
interface RepositorioCitas {
    fun obtenerCitas(): Flow<List<CitaVeterinaria>>
    suspend fun registrarCita(cita: CitaVeterinaria)
    suspend fun eliminarCita(idCita: String): Boolean
    suspend fun cambiarEstadoCita(idCita: String, nuevoEstado: CitaVeterinaria.() -> CitaVeterinaria)
}

// 3. Implementación en Memoria (Para pruebas locales)
class RepositorioCitasEnMemoria : RepositorioCitas {
    private val _citas = MutableStateFlow<List<CitaVeterinaria>>(emptyList())
    override fun obtenerCitas(): Flow<List<CitaVeterinaria>> = _citas.asStateFlow()
    override suspend fun registrarCita(cita: CitaVeterinaria) = _citas.update { listOf(cita) + it }
    override suspend fun eliminarCita(idCita: String): Boolean {
        var ok = false
        _citas.update { lista -> lista.filterNot { if (it.idCita == idCita) { ok = true; true } else false } }
        return ok
    }
    override suspend fun cambiarEstadoCita(idCita: String, nuevoEstado: CitaVeterinaria.() -> CitaVeterinaria) {
        _citas.update { lista -> lista.map { if (it.idCita == idCita) it.nuevoEstado() else it } }
    }
}

// 4. Implementación con API (Retrofit) - ¡CONEXIÓN REAL!
class RepositorioCitasApi : RepositorioCitas {
    // Apuntamos a vetpet_web y al puerto 8080
    private val baseUrl = "http://localhost:8080/vetpet_web/"

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VetPetApi::class.java)

    override fun obtenerCitas(): Flow<List<CitaVeterinaria>> = flow {
        try {
            val respuesta = api.obtenerCitasApi()
            emit(respuesta)
        } catch (e: Exception) {
            e.printStackTrace() // <-- ESTO IMPRIMIRÁ EL ERROR EN ANDROID STUDIO
            emit(emptyList())
        }
    }

    override suspend fun registrarCita(cita: CitaVeterinaria) {
        try {
            api.registrarCitaApi(cita)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun eliminarCita(idCita: String): Boolean {
        return true // Lo implementaremos luego si hay tiempo
    }

    override suspend fun cambiarEstadoCita(idCita: String, nuevoEstado: CitaVeterinaria.() -> CitaVeterinaria) {
        // Lo implementaremos luego si hay tiempo
    }
}