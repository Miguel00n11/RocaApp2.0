package com.miguelrodriguez.rocaapp20.supervisiones

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RegistroSupervision(
    val norma: String = "",
    val fecha: String = "",
    val evaluador: String = "",
    val evaluado: String = "",
    val resultados: List<String> = emptyList(),
    val hallazgos: List<String> = emptyList(),
    // Los llena MetaLab cuando el responsable de laboratorio central revisa y autoriza la supervisión;
    // una supervisión autorizada ya no se puede editar
    val revisoyAutorizo: String = "",
    val fechaAutorizacion: String = ""
)
