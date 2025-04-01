package com.example.desafio2.datos

data class Tarea(
    var key: String? = null,
    var titulo: String = "",
    var descripcion: String? = null,
    var estado: String = "pendiente",
    var fechaCreacion: Long = System.currentTimeMillis()
)