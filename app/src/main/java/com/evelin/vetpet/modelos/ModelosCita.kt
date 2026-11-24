package com.evelin.vetpet.modelos

enum class EstadoCita(val etiqueta: String) {
    TODOS("Todas"),
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}

data class CitaVeterinaria(
    val idCita: String,
    val nombreMascota: String,
    val especie: String,
    val nombreDueno: String,
    val motivo: String,
    val fechaHora: String,
    val estado: EstadoCita = EstadoCita.PENDIENTE
)