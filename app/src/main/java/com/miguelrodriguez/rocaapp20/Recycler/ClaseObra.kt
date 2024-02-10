package com.miguelrodriguez.rocaapp20.Recycler

data class ClaseObra(
    val id:Int,
    val validado:Boolean,
    val Obra:String,
    val Cliente:String,
    var reporte:String,
    val capa:String,
    val fecha:String,
    val tramo:String,
    val subtramo:String,
    val compactacion:String,
    val mvsm:String,
    val humedad:String,
    var llave:String,
    val listaCalas:MutableList<ClaseCala>


)
