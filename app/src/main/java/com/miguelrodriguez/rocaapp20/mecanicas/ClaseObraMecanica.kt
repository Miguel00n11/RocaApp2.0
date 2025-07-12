package com.miguelrodriguez.rocaapp20.mecanicas

import com.google.android.material.switchmaterial.SwitchMaterial
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import java.util.Date

data class ClaseObraMecanica(val id:Int,
                             val Obra:String,
                             val cliente:String,
                             val localizacion:String,
                             val atencion:String,
                             val fecha:String,
                             val sondeo_num:String,
                             val ubicacion:String,
                             val naf:Boolean,
                             val profundidad_muestreo:String,
                             val profundidad_naf:String,
                             val hora:String,
                             var llave:String,
                             var latitud:String,
                             var longitud:String,
                             val listaEstratos:MutableList<ClaseEstratos>,
                             val listaImagenes:MutableList<String>,
                             val personal:String,


)
