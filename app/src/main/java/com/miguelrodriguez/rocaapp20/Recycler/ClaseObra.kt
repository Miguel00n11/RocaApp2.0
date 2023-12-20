package com.miguelrodriguez.rocaapp20.Recycler

data class ClaseObra(
    val id:Int,
    val Obra:String,
    var reporte:String,
    val capa:String,
    val fecha:String,
    val tramo:String,
    val subtramo:String,
    val compactacion:String,
    val mvsm:String,
    val humedad:String,
    val listaCalas:MutableList<ClaseCala>


)
