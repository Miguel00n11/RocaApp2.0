package com.miguelrodriguez.rocaapp20.Recycler

data class ClaseObraMecanica(val id:Int,
                             val Obra:String,
                             var reporte:String,
                             val capa:String,
                             val fecha:String,
                             val tramo:String,
                             val subtramo:String,
                             val procedencia:String,
                             val lugarMuestreo:String,
                             val estacion:String,
                             var llave:String,
                             var tipoMuestreo:String,
                             var estudioMuestreo:String,
                             val listaEstratos:MutableList<ClaseEstratos>


)
