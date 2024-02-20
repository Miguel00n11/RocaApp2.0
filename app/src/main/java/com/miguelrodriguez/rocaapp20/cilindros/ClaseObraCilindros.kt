package com.miguelrodriguez.rocaapp20.cilindros

import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala

data class ClaseObraCilindros(
    val id: Int,
    val Obra: String,
    val Cliente:String,
    val localizacion:String,
    var fecha: String,
    val personal: String,
    val numeroReporte: Int,
    val tipoMuestreo: String,

    val elementoColado: String,
    val ubicacion: String,
    val fc: Double,
    val volumenTotal: Double,
    val volumenMuestra: Double,
    val tipoResistencia: String,
    val edad: Int,
    val tma: Double,
    val concretera: String,
    val proporciones: String,
    val aditivo: String,
    val remision: String,

    val muestra: Int,
    val olla: String,
    val revenimientoDis: Double,
    val revenimientoR1: Double,
    val revenimientoR2: Double,
    val temperatura: Double,
    val Molde1: Int,
    val Molde2: Int,
    val Molde3: Int,
    val Molde4: Int,
    val estadoMolde1: String,
    val estadoMolde2: String,
    val estadoMolde3: String,
    val estadoMolde4: String,
    val horaSalida: String,
    val horaLLegada: String,
    val horaMuestreo: String,
    val observaciones: String,

    val carretilla: String,
    val cono: String,
    val varilla: String,
    val mazo: String,
    val termometro: String,
    val cucharon: String,
    val placa: String,
    val flexometro: String,
    val enrasador: String,
    val validado: Boolean,

    var llave: String

)
