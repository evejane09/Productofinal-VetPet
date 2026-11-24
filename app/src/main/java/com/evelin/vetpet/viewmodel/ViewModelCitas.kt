package com.evelin.vetpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evelin.vetpet.datos.RepositorioCitas
import com.evelin.vetpet.datos.RepositorioCitasApi
import com.evelin.vetpet.modelos.CitaVeterinaria
import com.evelin.vetpet.modelos.EstadoCita
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EstadoUICitas(
    val cargando: Boolean = false,
    val citas: List<CitaVeterinaria> = emptyList(),
    val citasFiltradas: List<CitaVeterinaria> = emptyList(),
    val filtroEstadoSeleccionado: EstadoCita = EstadoCita.TODOS,
    val nombreMascota: String = "",
    val especie: String = "Perro",
    val nombreDueno: String = "",
    val motivo: String = "Consulta general",
    val fechaHora: String = "",
    val errorMascota: String? = null,
    val errorDueno: String? = null,
    val errorFecha: String? = null,
    val guardando: Boolean = false,
    val totalCitas: Int = 0,
    val citasPendientes: Int = 0,
    val citasCompletadas: Int = 0
)

sealed interface EventoUICita {
    data class MostrarSnackbar(val mensaje: String, val esAdvertencia: Boolean = false) : EventoUICita
}

class ViewModelCitas(private val repo: RepositorioCitas = RepositorioCitasApi()) : ViewModel() {
    private val _estado = MutableStateFlow(EstadoUICitas())
    val estadouli = _estado.asStateFlow()

    private val _evento = MutableSharedFlow<EventoUICita>()
    val eventouli = _evento.asSharedFlow()

    init {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            repo.obtenerCitas().collect { lista ->
                _estado.update { st ->
                    val f = st.filtroEstadoSeleccionado
                    st.copy(
                        cargando = false,
                        citas = lista,
                        citasFiltradas = if (f == EstadoCita.TODOS) lista else lista.filter { it.estado == f },
                        totalCitas = lista.size,
                        citasPendientes = lista.count { it.estado == EstadoCita.PENDIENTE },
                        citasCompletadas = lista.count { it.estado == EstadoCita.COMPLETADA }
                    )
                }
            }
        }
    }

    fun cambiarMascota(v: String) = _estado.update { it.copy(nombreMascota = v, errorMascota = if (v.isBlank()) "Requerido" else null) }
    fun cambiarDueno(v: String) = _estado.update { it.copy(nombreDueno = v, errorDueno = if (v.isBlank()) "Requerido" else null) }
    fun cambiarEspecie(v: String) = _estado.update { it.copy(especie = v) }
    fun cambiarMotivo(v: String) = _estado.update { it.copy(motivo = v) }
    fun cambiarFecha(v: String) = _estado.update { it.copy(fechaHora = v.trim(), errorFecha = if (v.trim().isBlank()) "Requerido" else null) }

    fun seleccionarFiltroEstado(e: EstadoCita) = _estado.update { st ->
        st.copy(
            filtroEstadoSeleccionado = e,
            citasFiltradas = if (e == EstadoCita.TODOS) st.citas else st.citas.filter { it.estado == e }
        )
    }

    fun registrarCita() {
        val st = _estado.value
        val eM = if (st.nombreMascota.trim().isBlank()) "Requerido" else null
        val eD = if (st.nombreDueno.trim().isBlank()) "Requerido" else null
        val eF = if (st.fechaHora.trim().isBlank()) "Requerido" else null

        if (listOf(eM, eD, eF).any { it != null }) {
            _estado.update { it.copy(errorMascota = eM, errorDueno = eD, errorFecha = eF) }
            viewModelScope.launch { _evento.emit(EventoUICita.MostrarSnackbar("Complete los campos", true)) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val nuevaCita = CitaVeterinaria(
                idCita = "CITA-${System.currentTimeMillis()}",
                nombreMascota = st.nombreMascota.trim(),
                especie = st.especie,
                nombreDueno = st.nombreDueno.trim(),
                motivo = st.motivo,
                fechaHora = st.fechaHora.trim(),
                estado = EstadoCita.PENDIENTE
            )
            repo.registrarCita(nuevaCita)
            _estado.update { it.copy(guardando = false, nombreMascota = "", nombreDueno = "", fechaHora = "", errorMascota = null, errorDueno = null, errorFecha = null) }

            // Refrescamos la lista
            val listaActualizada = repo.obtenerCitas().first()
            _estado.update {
                it.copy(
                    citas = listaActualizada,
                    citasFiltradas = listaActualizada,
                    totalCitas = listaActualizada.size,
                    citasPendientes = listaActualizada.count { c -> c.estado == EstadoCita.PENDIENTE },
                    citasCompletadas = listaActualizada.count { c -> c.estado == EstadoCita.COMPLETADA }
                )
            }
            _evento.emit(EventoUICita.MostrarSnackbar("Turno de ${nuevaCita.nombreMascota} guardado"))
        }
    }

    fun eliminarCita(id: String) = viewModelScope.launch {
        if (repo.eliminarCita(id)) {
            _estado.update { st ->
                val listaNueva = st.citas.filterNot { it.idCita == id }
                st.copy(
                    citas = listaNueva,
                    citasFiltradas = listaNueva,
                    totalCitas = listaNueva.size,
                    citasPendientes = listaNueva.count { it.estado == EstadoCita.PENDIENTE },
                    citasCompletadas = listaNueva.count { it.estado == EstadoCita.COMPLETADA }
                )
            }
            _evento.emit(EventoUICita.MostrarSnackbar("Cita eliminada"))
        }
    }

    fun completarCita(id: String) = viewModelScope.launch {
        repo.cambiarEstadoCita(id) { this.copy(estado = EstadoCita.COMPLETADA) }
        val listaActualizada = repo.obtenerCitas().first()
        _estado.update {
            it.copy(
                citas = listaActualizada,
                citasFiltradas = listaActualizada,
                citasPendientes = listaActualizada.count { c -> c.estado == EstadoCita.PENDIENTE },
                citasCompletadas = listaActualizada.count { c -> c.estado == EstadoCita.COMPLETADA }
            )
        }
        _evento.emit(EventoUICita.MostrarSnackbar("Cita completada"))
    }
}