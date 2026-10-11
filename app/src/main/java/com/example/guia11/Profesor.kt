package com.example.guia11

import com.google.gson.annotations.JsonAdapter

data class Profesor(
    val id: Int? = null,
    val nombre: String,
    val apellido: String,
    @field:JsonAdapter(EdadAdapter::class) val edad: Int
)