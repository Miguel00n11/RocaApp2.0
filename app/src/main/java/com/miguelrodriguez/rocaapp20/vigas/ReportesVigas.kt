package com.miguelrodriguez.rocaapp20.vigas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale


class ReportesVigas : AppCompatActivity() {
    private lateinit var dataReference: DatabaseReference
    private lateinit var dataReferenceCampo: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String

    companion object {
        lateinit var reporteSelecionado: ClaseObraVigas
        var editar: Boolean = false
//        var listaObrasmutableListOf= mutableListOf<ClaseObraVigas>()
    }


    private lateinit var btnRegistroVigas: Button
    private lateinit var rvObrasVigas: RecyclerView
    private lateinit var ObraAdapter: ObraAdapterVigas

    //    private lateinit var claseObra: ClaseObra
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObraVigas>


    private lateinit var swVerTodosReportesVigas: Switch
    private lateinit var listaFiltrada: MutableList<ClaseObraVigas>
    private lateinit var svBuscarReportesVigas: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_vigas)

        initComponent()
        initUI()
    }

    private fun initComponent() {
        rvObrasVigas = findViewById(R.id.rvObrasVigas)

        ReportesVigas.reporteSelecionado = ClaseObraVigas(
            1,
            "estacion",
            "a",
            "1",
            "1",
            "1",
            0,
            "1",
            "1",
            "1",
            1.1,
            1.1,
            1.1,
            "hola",
            1,
            1.1,
            "a",
            "1",
            "1",
            "1",
            1,
            "a",
            1.1,
            1.1,
            1.1,
            1.1,
            1,
            1,
            1,
//            1,
            "1",
            "1",
            "1",
//            "1",
            "1:1",
            "1:1",
            "1:1",
            "a",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            false,
            ""
        )
        btnRegistroVigas = findViewById(R.id.btnRegistroVigas)


        listaObrasmutableListOf = mutableListOf(
        )

        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion

        svBuscarReportesVigas = findViewById(R.id.svBuscarReportesVigas)
        swVerTodosReportesVigas = findViewById(R.id.swVerTodosReportesVigas)


    }
    override fun onResume() {
        super.onResume()
        // Vuelve a cargar la interfaz
        cargarObras(dataReference)
    }

    private fun initUI() {
        // Referencia a la base de datos de Firebase
        dataReference = FirebaseDatabase.getInstance().reference.child("Vigas").child("Reportes").child(personal)
        dataReferenceCampo = FirebaseDatabase.getInstance().reference.child("Vigas").child("Respaldo").child(personal)


        btnRegistroVigas.setOnClickListener {
            val intent = Intent(this, RegistroVigas::class.java)
            startActivity(intent)
        }


        rvObrasVigas.adapter = ObraAdapterVigas(listaObrasmutableListOf, onObraSelected = { position ->
            if (svBuscarReportesVigas.visibility == View.GONE) {
                onItemSelected(position)
            }
        }, onItemDelete = { position -> onItemDelete(position) }, onVerReporteVigas = { position ->
            onVerReporteVigas(
                position, listaObrasmutableListOf
            )
        }, swVerTodosReportesVigas.isChecked
        )

//        rvObrasVigas.adapter = ObraAdapter
        ObraAdapter = rvObrasVigas.adapter as ObraAdapterVigas

        listaFiltrada = listaObrasmutableListOf

        rvObrasVigas.layoutManager = LinearLayoutManager(this)

        cargarObras(dataReference)


        svBuscarReportesVigas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(fecha: String?): Boolean {

                val searchText = fecha!!.toLowerCase(Locale.getDefault())

                if (searchText.isNotEmpty()) {


                    listaFiltrada = listaObrasmutableListOf.filter { it.fecha?.contains(searchText) == true||
                            it.Obra?.contains(searchText, ignoreCase = true) == true || // Ignorar mayúsculas y minúsculas
                            it.Obra?.startsWith(searchText, ignoreCase = true) == true // Comienza con la letra buscada
                            ||
                            it.Cliente?.contains(searchText, ignoreCase = true) == true || // Ignorar mayúsculas y minúsculas
                            it.Cliente?.startsWith(searchText, ignoreCase = true) == true // Comienza con la letra buscada
                    } as MutableList<ClaseObraVigas>
//
//                    var filteredList = mutableListOf<ClaseObra>()
//
//                    // Filtra la lista de obras según el texto de búsqueda
//                    filteredList = listaObrasmutableListOf.filter {
//                        it.fecha?.contains(searchText) == true || it.Obra?.contains(searchText) == true
//                    } as MutableList<ClaseObra>

//                    rvObrasCompactacion.adapter= ObraAdapter(listaFiltrada){}
//                    rvObrasCompactacion.adapter = ObraAdapter(
//                        listaFiltrada,
//                        { position -> /* código para manejar la selección de obra en la posición 'position' */ },
//                        { position -> /* código para manejar la eliminación de obra en la posición 'position' */ },
//                        { position -> /* código para manejar la visualización de reporte de compactación en la posición 'position' */ }
//                    )

                    rvObrasVigas.adapter = ObraAdapterVigas(listaFiltrada,
                        { position -> /* código para manejar la selección de obra en la posición 'position' */ },
                        { position -> },
                        { position -> onVerReporteVigas(position, listaFiltrada) },
                        swVerTodosReportesVigas.isChecked

                    )

                } else {

                    rvObrasVigas.adapter = ObraAdapterVigas(listaObrasmutableListOf,
                        onObraSelected = { position ->
                            if (svBuscarReportesVigas.visibility == View.GONE) {
                                onItemSelected(position)
                            }
                        },
                        { position -> onItemDelete(position) },
                        { position -> onVerReporteVigas(position, listaObrasmutableListOf) },
                        swVerTodosReportesVigas.isChecked

                    )
                    ObraAdapter = rvObrasVigas.adapter as ObraAdapterVigas


                }
                return false
            }

        })





        swVerTodosReportesVigas.setOnCheckedChangeListener { buttonView, isChecked ->
            ObraAdapter.setMostrarBoton(swVerTodosReportesVigas.isChecked)

            if (isChecked) {
                svBuscarReportesVigas.visibility = View.VISIBLE// El switch está activado
                dataReference = FirebaseDatabase.getInstance().reference.child("Vigas").child("Respaldo").child(personal)
                cargarObras(dataReference)

            } else {
                svBuscarReportesVigas.setQuery("", false)
                svBuscarReportesVigas.visibility = View.GONE
                dataReference = FirebaseDatabase.getInstance().reference.child("Vigas").child("Reportes").child(personal)
                cargarObras(dataReference)

                // El switch está desactivado
            }

        }
    }


    private fun cargarObras(dataReference: DatabaseReference) {
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                val personalDeseado = "miguel" // Reemplaza con el nombre del personal que deseas filtrar

                for (snapshot in dataSnapshot.children) {
                    val validado = snapshot.child("validado").getValue(Boolean::class.java)
//                    if (validado == true) {
//                        continue
//                    }


                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)
                    val localizacion = snapshot.child("localizacion").getValue(String::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)

                    val tipoMuestreo = snapshot.child("tipoMuestreo").getValue(String::class.java)
                    val elementoColado = snapshot.child("elementoColado").getValue(String::class.java)
                    val ubicacion = snapshot.child("ubicacion").getValue(String::class.java)
                    val fc = snapshot.child("fc").getValue(Double::class.java)
                    val volumenTotal = snapshot.child("volumenTotal").getValue(Double::class.java)
                    val volumenMuestra = snapshot.child("volumenMuestra").getValue(Double::class.java)
                    val tipoResistencia = snapshot.child("tipoResistencia").getValue(String::class.java)
                    val edad = snapshot.child("edad").getValue(Int::class.java)
                    val tma = snapshot.child("tma").getValue(Double::class.java)
                    val concretera = snapshot.child("concretera").getValue(String::class.java)
                    val proporciones = snapshot.child("proporciones").getValue(String::class.java)
                    val aditivo = snapshot.child("aditivo").getValue(String::class.java)
                    val remision = snapshot.child("remision").getValue(String::class.java)

                    val muestra = snapshot.child("muestra").getValue(Int::class.java)
                    val olla = snapshot.child("olla").getValue(String::class.java)
                    val revenimientoDis = snapshot.child("revenimientoDis").getValue(Double::class.java)
                    val revenimientoR1 = snapshot.child("revenimientoR1").getValue(Double::class.java)
                    val revenimientoR2 = snapshot.child("revenimientoR2").getValue(Double::class.java)
                    val temperatura = snapshot.child("temperatura").getValue(Double::class.java)
                    val molde1 = snapshot.child("molde1").getValue(Int::class.java)
                    val molde2 = snapshot.child("molde2").getValue(Int::class.java)
                    val molde3 = snapshot.child("molde3").getValue(Int::class.java)
                    val molde4 = snapshot.child("molde4").getValue(Int::class.java)
                    val estadoMolde1 = snapshot.child("estadoMolde1").getValue(String::class.java)
                    val estadoMolde2 = snapshot.child("estadoMolde2").getValue(String::class.java)
                    val estadoMolde3 = snapshot.child("estadoMolde3").getValue(String::class.java)
                    val estadoMolde4 = snapshot.child("estadoMolde4").getValue(String::class.java)
                    val horaSalida = snapshot.child("horaSalida").getValue(String::class.java)
                    val horaLLegada = snapshot.child("horaLLegada").getValue(String::class.java)
                    val horaMuestreo = snapshot.child("horaMuestreo").getValue(String::class.java)
                    val observaciones = snapshot.child("observaciones").getValue(String::class.java)

                    val carretilla = snapshot.child("carretilla").getValue(String::class.java)
                    val cono = snapshot.child("cono").getValue(String::class.java)
                    val varilla = snapshot.child("varilla").getValue(String::class.java)
                    val mazo = snapshot.child("mazo").getValue(String::class.java)
                    val termometro = snapshot.child("termometro").getValue(String::class.java)
                    val cucharon = snapshot.child("cucharon").getValue(String::class.java)
                    val placa = snapshot.child("placa").getValue(String::class.java)
                    val flexometro = snapshot.child("flexometro").getValue(String::class.java)
                    val enrasador = snapshot.child("enrasador").getValue(String::class.java)
//                    val validado=snapshot.child("validado").getValue(Boolean::class.java)

                    var llave = snapshot.child("llave").getValue(String::class.java)


                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {
                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
                        val obra = ClaseObraVigas(

                            numReporte!!,
                            obra1.toString(),
                            cliente.toString(),
                            localizacion.toString(),
                            fecha.toString(),
                            personal1.toString(),
                            numReporte.toInt(),
                            tipoMuestreo.toString(),

                            elementoColado.toString(),
                            ubicacion.toString(),
                            fc!!,
                            volumenTotal!!,
                            volumenMuestra!!,
                            tipoResistencia.toString(),
                            edad!!,
                            tma!!,
                            concretera.toString(),
                            proporciones.toString(),
                            aditivo.toString(),
                            remision.toString(),

                            muestra!!,
                            olla.toString(),
                            revenimientoDis!!,
                            revenimientoR1!!,
                            revenimientoR2!!,
                            temperatura!!,
                            molde1!!,
                            molde2!!,
                            molde3!!,
//                            molde4!!,
                            estadoMolde1.toString(),
                            estadoMolde2.toString(),
                            estadoMolde3.toString(),
//                            estadoMolde4.toString(),
                            horaSalida.toString(),
                            horaLLegada.toString(),
                            horaMuestreo.toString(),
                            observaciones.toString(),

                            carretilla.toString(),
                            cono.toString(),
                            varilla.toString(),
                            mazo.toString(),
                            termometro.toString(),
                            cucharon.toString(),
                            placa.toString(),
                            flexometro.toString(),
                            enrasador.toString(),
                            validado!!,

                            llave.toString()


                        ) // Asegúrate de ajustar los parámetros según tu clase
                        listaObrasmutableListOf.add(obra)
                    }


                }
                listaObrasmutableListOf.sortByDescending { SimpleDateFormat("dd/MM/yyyy").parse(it.fecha) }

                // Notifica al adaptador que los datos han cambiado
                ObraAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de base de datos, si es necesario
            }

        })
    }


    private fun onItemDelete(position: Int) {
        if (isNetworkAvailable()) {
            val reportKey = listaObrasmutableListOf[position].llave // Utiliza la clave única del informe

            // Elimina el informe de la base de datos Firebase
            deleteReport(reportKey)

            // Elimina el informe de la lista local
            listaObrasmutableListOf.removeAt(position)

            dataReference = FirebaseDatabase.getInstance().reference.child("Vigas").child("Reportes").child(personal)
            cargarObras(dataReference)

            // Notifica al adaptador que los datos han cambiado
            ObraAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteReport(reportKey: String) {
        val reportReference = dataReference.child(reportKey)
        val reportReferenceCampo = dataReferenceCampo.child(reportKey)

        reportReference.removeValue()
        reportReferenceCampo.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Informe eliminado exitosamente", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(
                this, "Error al eliminar el informe: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

//        listaCalasmutableListOf[position], position


        ReportesVigas.editar = true
        ReportesVigas.reporteSelecionado = listaObrasmutableListOf[position]
        val intent = Intent(this, RegistroVigas::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }

    private fun onVerReporteVigas(position: Int, listaReportes: MutableList<ClaseObraVigas>) = try {

        ReportesVigas.reporteSelecionado = listaReportes[position]


        // Datos de varios registros (solo como ejemplo)
        val registros = listOf(
            arrayOf("Cliente:", ReportesVigas.reporteSelecionado.Cliente),
            arrayOf("Obra:", ReportesVigas.reporteSelecionado.Obra),
            arrayOf("Loc.:", ReportesVigas.reporteSelecionado.localizacion)
        )


        val directorio1 = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        if (directorio1 != null) {
            val archivos = directorio1.listFiles()

            if (archivos != null) {
                for (archivo in archivos) {
                    archivo.delete()
                }
            }
        }

        // Directorio para guardar el archivo PDF en el almacenamiento externo
        val fechaActual = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val nombreArchivo = "registro_concreto_$fechaActual.pdf"
        val directorio = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        val archivoPDF = File(directorio, "registro_concreto_$fechaActual.pdf")
        val alturaTexto = 8f

//        println(directorio)

//            Toast.makeText(this, directorio.toString(), Toast.LENGTH_SHORT).show()
        try {

            val outputStream = FileOutputStream(archivoPDF)
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            pdf.defaultPageSize = PageSize.LETTER
            val document = Document(pdf, PageSize.LETTER, true)


            //calcular el ancho disponible para la tabla
            val anchoDocumento = PageSize.LETTER.width - 72f * 2
            val numeroColumnas = 1
            val anchoColiumna = anchoDocumento / numeroColumnas


            val tableTituloDatosDeControl1 = Table(floatArrayOf(anchoDocumento + 72f))

            // Cargar la imagen desde el directorio drawable
            val drawableId = R.drawable.logoroca // Reemplaza 'logoroca' con el nombre de tu imagen
            val bitmap = BitmapFactory.decodeResource(this.resources, drawableId)

            // Convertir el bitmap en un objeto Image de iText
            val outputStream1 = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream1)
            val imageData = ImageDataFactory.create(outputStream1.toByteArray())
            val image = Image(imageData)
            image.scale(.025f, .025f)

            // Agregar celda con imagen
            val cellImage = Cell(2, 1)
            cellImage.add(image.setHorizontalAlignment(HorizontalAlignment.CENTER))

            // Imagen de ANALISEC
            val drawableId1 = R.drawable.logoanalisec // Reemplaza 'logoroca' con el nombre de tu imagen
            val bitmap1 = BitmapFactory.decodeResource(this.resources, drawableId1)

            val outputStream11 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, outputStream11)
            val imageData1 = ImageDataFactory.create(outputStream11.toByteArray())
            val image1 = Image(imageData1)
            image1.scale(.15f, .15f)

            // Agregar celda con imagen
            val cellImage1 = Cell(2, 1)
            cellImage1.add(image1.setHorizontalAlignment(HorizontalAlignment.CENTER))


            // Crear una tabla
            val table = Table(floatArrayOf(30f, anchoDocumento, 30f))
            table.addCell(cellImage)

            // Nombre del Formato de muestreo de concreto
            val nombreNormaMuestreoConcreto = Cell(1, 1).add(Paragraph("F1-PR14 Rev.:02; MUESTREO DE CONCRETO FRESCO"))
            nombreNormaMuestreoConcreto.setTextAlignment(TextAlignment.CENTER)
            nombreNormaMuestreoConcreto.setFontSize(8f)
            table.addCell(nombreNormaMuestreoConcreto)

            table.addCell(cellImage1)

            val textoTituloNorma = Cell(1, 1).add(Paragraph("NORMAS DE REFERENCIA NMX-C-ONNCCE-161,156,159"))
            textoTituloNorma.setTextAlignment(TextAlignment.CENTER)
            textoTituloNorma.setFontSize(8f)
            table.addCell(textoTituloNorma)

            document.add(table)


            // Crear una tabla
            val tableSeleccionarTipoMuestreo = Table(floatArrayOf(anchoDocumento,80f, anchoDocumento, 80f))
            tableSeleccionarTipoMuestreo.setFontSize(8f)

            val tipoMuestreo = Cell(1, 4).add(Paragraph("SELECCIONAR EL TIPO DE MUESTREO:"))
            tipoMuestreo.setTextAlignment(TextAlignment.CENTER)
            tipoMuestreo.setBold()
            tableSeleccionarTipoMuestreo.addCell(tipoMuestreo)


            val mezcladoras = Cell(1, 1).add(Paragraph("Mezcladoras estacionarias (fijas y basculantes)"))
            mezcladoras.setTextAlignment(TextAlignment.CENTER)
            mezcladoras.setFontSize(8f)
            tableSeleccionarTipoMuestreo.addCell(mezcladoras)

            var seleccionMezcladoras:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Mezcladora estacionarias (fijas y basculantes)"){
                seleccionMezcladoras="/"
            }
            val mezcladorasTxt = Cell(1, 1).add(Paragraph(seleccionMezcladoras))
            mezcladorasTxt.setTextAlignment(TextAlignment.CENTER)
            tableSeleccionarTipoMuestreo.addCell(mezcladorasTxt)

            val camionMezclador = Cell(1, 1).add(Paragraph("Olla del camión mezclador o agitador"))
            camionMezclador.setTextAlignment(TextAlignment.CENTER)
            tableSeleccionarTipoMuestreo.addCell(camionMezclador)

            var seleccioncamionMezcladorTxt:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Olla de camión mezclador o agitador"){
                seleccioncamionMezcladorTxt="/"
            }
            val camionMezcladorTxt = Cell(1, 1).add(Paragraph(seleccioncamionMezcladorTxt))
            camionMezcladorTxt.setTextAlignment(TextAlignment.CENTER)
            camionMezcladorTxt.setFontSize(8f)
            tableSeleccionarTipoMuestreo.addCell(camionMezcladorTxt)


            document.add(tableSeleccionarTipoMuestreo)


            // Crear una tabla
            val tableSeleccionarTipoMuestreo1 = Table(floatArrayOf(anchoDocumento+800f,180f, anchoDocumento, 180f,anchoDocumento,180f, anchoDocumento, 180f))
            tableSeleccionarTipoMuestreo1.setFontSize(8f)

            val camionCaja = Cell(1, 1).add(Paragraph("Camión caja con o sin agitadores"))
            camionCaja.setTextAlignment(TextAlignment.CENTER)
            camionCaja.setBorderTop(Border.NO_BORDER)
            tableSeleccionarTipoMuestreo1.addCell(camionCaja)

            var seleccioncamionCajaTxt:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Camión caja con o sin agitadores"){
                seleccioncamionCajaTxt="/"
            }
            val camionCajaTxt = Cell(1, 1).add(Paragraph(seleccioncamionCajaTxt))
            camionCajaTxt.setTextAlignment(TextAlignment.CENTER)
            camionCajaTxt.setBorderTop(Border.NO_BORDER)
            camionCajaTxt.setFontSize(8f)
            tableSeleccionarTipoMuestreo1.addCell(camionCajaTxt)
//
            val camionVolteo = Cell(1, 1).add(Paragraph("Camión de volteo"))
            camionVolteo.setTextAlignment(TextAlignment.CENTER)
            camionVolteo.setBorderTop(Border.NO_BORDER)
            tableSeleccionarTipoMuestreo1.addCell(camionVolteo)

            var seleccioncamionVolteoTxt:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Camión de volteo"){
                seleccioncamionVolteoTxt="/"
            }
            val camionVolteoTxt = Cell(1, 1).add(Paragraph(seleccioncamionVolteoTxt))
            camionVolteoTxt.setTextAlignment(TextAlignment.CENTER)
            camionVolteoTxt.setBorderTop(Border.NO_BORDER)
            camionVolteoTxt.setFontSize(8f)
            tableSeleccionarTipoMuestreo1.addCell(camionVolteoTxt)
//
            val pavimentadora = Cell(1, 1).add(Paragraph("Pavimentadora"))
            pavimentadora.setTextAlignment(TextAlignment.CENTER)
            pavimentadora.setBorderTop(Border.NO_BORDER)
            tableSeleccionarTipoMuestreo1.addCell(pavimentadora)

            var seleccionPavimentadoraTxt:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Pavimentadora"){
                seleccionPavimentadoraTxt="/"
            }
            val PavimentadoraTxt = Cell(1, 1).add(Paragraph(seleccionPavimentadoraTxt))
            PavimentadoraTxt.setTextAlignment(TextAlignment.CENTER)
            PavimentadoraTxt.setBorderTop(Border.NO_BORDER)
            PavimentadoraTxt.setFontSize(8f)
            tableSeleccionarTipoMuestreo1.addCell(PavimentadoraTxt)
//
            val muestreoOtros = Cell(1, 1).add(Paragraph("Otro"))
            muestreoOtros.setTextAlignment(TextAlignment.CENTER)
            muestreoOtros.setBorderTop(Border.NO_BORDER)
            tableSeleccionarTipoMuestreo1.addCell(muestreoOtros)

            var seleccionOtroTxt:String=""
            if (ReportesVigas.reporteSelecionado.tipoMuestreo=="Otro"){
                seleccionOtroTxt="/"
            }
            val muestreoOtrosTxt = Cell(1, 1).add(Paragraph(seleccionOtroTxt))
            muestreoOtrosTxt.setTextAlignment(TextAlignment.CENTER)
            muestreoOtrosTxt.setBorderTop(Border.NO_BORDER)
            muestreoOtrosTxt.setFontSize(8f)
            tableSeleccionarTipoMuestreo1.addCell(muestreoOtrosTxt)

            document.add(tableSeleccionarTipoMuestreo1)


            // DATOS GENERALES
            val tableDatosGenerales = Table(floatArrayOf(130f,anchoDocumento, 100f,100f))
            tableDatosGenerales.setFontSize(8f)

            val datosGenerales = Cell(1, 4).add(Paragraph("DATOS GENERALES"))
            datosGenerales.setTextAlignment(TextAlignment.CENTER)
            datosGenerales.setBold()
            tableDatosGenerales.addCell(datosGenerales)

            //

            val nombreObra = Cell(1, 1).add(Paragraph("Nombre de la obra:"))
            nombreObra.setTextAlignment(TextAlignment.CENTER)
            nombreObra.setFontSize(8f)
            tableDatosGenerales.addCell(nombreObra)

            val nombreObraTxt = Cell(1, 3).add(Paragraph(ReportesVigas.reporteSelecionado.Obra))
            nombreObraTxt.setFontSize(8f)
            tableDatosGenerales.addCell(nombreObraTxt)

            //

            val cliente = Cell(1, 1).add(Paragraph("Cliente:"))
            cliente.setTextAlignment(TextAlignment.CENTER)
            cliente.setFontSize(8f)
            tableDatosGenerales.addCell(cliente)

            val clienteTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.Cliente))
            clienteTxt.setFontSize(8f)
            tableDatosGenerales.addCell(clienteTxt)

            //

            val fecha = Cell(1, 1).add(Paragraph("Fecha:"))
            fecha.setTextAlignment(TextAlignment.CENTER)
            fecha.setFontSize(8f)
            tableDatosGenerales.addCell(fecha)

            val fechaTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.fecha))
            fechaTxt.setTextAlignment(TextAlignment.CENTER)
            fechaTxt.setFontSize(8f)
            tableDatosGenerales.addCell(fechaTxt)

            //

            val localizacion = Cell(1, 1).add(Paragraph("Localización:"))
            localizacion.setTextAlignment(TextAlignment.CENTER)
            localizacion.setFontSize(8f)
            tableDatosGenerales.addCell(localizacion)

            val localizacionTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.localizacion))
            localizacionTxt.setFontSize(8f)
            tableDatosGenerales.addCell(localizacionTxt)

            //

            val expediente = Cell(1, 1).add(Paragraph("Expediente:"))
            expediente.setTextAlignment(TextAlignment.CENTER)
            expediente.setFontSize(8f)
            tableDatosGenerales.addCell(expediente)

            val expedienteTxt = Cell(1, 1).add(Paragraph(""))
            expedienteTxt.setTextAlignment(TextAlignment.CENTER)
            expedienteTxt.setFontSize(8f)
            tableDatosGenerales.addCell(expedienteTxt)

            document.add(tableDatosGenerales)

            //

            // DATOS DE MUESTREO 1
            val tableDatosMuestreo = Table(floatArrayOf(130f,anchoDocumento, 130f,anchoDocumento))
            tableDatosMuestreo.setFontSize(8f)

            //

            val elemetoColado = Cell(1, 1).add(Paragraph("Elemento colado:"))
            elemetoColado.setTextAlignment(TextAlignment.CENTER)
            elemetoColado.setFontSize(8f)
            tableDatosMuestreo.addCell(elemetoColado)

            val elemetoColadoTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.elementoColado))
            elemetoColadoTxt.setTextAlignment(TextAlignment.CENTER)
            elemetoColadoTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(elemetoColadoTxt)

            //

            val ubicacion = Cell(1, 1).add(Paragraph("Ubicación:"))
            ubicacion.setTextAlignment(TextAlignment.CENTER)
            ubicacion.setFontSize(8f)
            tableDatosMuestreo.addCell(ubicacion)

            val ubicacionTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.ubicacion))
            ubicacionTxt.setTextAlignment(TextAlignment.CENTER)
            ubicacionTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(ubicacionTxt)

            //

            val fcTxt = Cell(1, 4).add(Paragraph("f'c (kg/cm²):    ---                                       M.R. (kg/cm²):     "+ ReportesVigas.reporteSelecionado.fc +"                                            Volumen total (m³):    "+ ReportesVigas.reporteSelecionado.volumenTotal ))
            fcTxt.setTextAlignment(TextAlignment.CENTER)
            fcTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(fcTxt)

            //

            var textoTipoConcretoNormal=""
            var textoTipoConcretoRapido="/"
            if (ReportesVigas.reporteSelecionado.tipoResistencia=="Resistencia Normal"){
                textoTipoConcretoNormal="/"
                textoTipoConcretoRapido=""
            }
            val tipoConcretoTxt = Cell(1, 6).add(Paragraph("TIPO DE CONCRETO: NORMAL      "+textoTipoConcretoNormal+"             Resistencia rápida:        "+textoTipoConcretoRapido+ "           EDAD RESIST. DISEÑO (d):    "+ ReportesVigas.reporteSelecionado.edad))
            tipoConcretoTxt.setTextAlignment(TextAlignment.CENTER)
            tipoConcretoTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(tipoConcretoTxt)

            //

            val tma = Cell(1, 1).add(Paragraph("T.M.A. (mm):"))
            tma.setTextAlignment(TextAlignment.CENTER)
            tma.setFontSize(8f)
            tableDatosMuestreo.addCell(tma)

            val tmaTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.tma.toString()))
            tmaTxt.setTextAlignment(TextAlignment.CENTER)
            tmaTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(tmaTxt)

            //

            val concretera = Cell(1, 1).add(Paragraph("CÍA CONCRETERA:"))
            concretera.setTextAlignment(TextAlignment.CENTER)
            concretera.setFontSize(8f)
            tableDatosMuestreo.addCell(concretera)

            val concreteraTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.concretera))
            concreteraTxt.setTextAlignment(TextAlignment.CENTER)
            concreteraTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(concreteraTxt)

            //

            val proporcionesTxt = Cell(1, 4).add(Paragraph("H.O. PROP. (cemento:arena:grava):             "+ ReportesVigas.reporteSelecionado.proporciones+"                                     ADITIVO:                  "+ ReportesVigas.reporteSelecionado.aditivo))
            proporcionesTxt.setTextAlignment(TextAlignment.CENTER)
            proporcionesTxt.setFontSize(8f)
            tableDatosMuestreo.addCell(proporcionesTxt)


            document.add(tableDatosMuestreo)


            // DATOS DE MUESTREO 2
            val tableDatosMuestreo2 = Table(floatArrayOf(140f,140f,anchoDocumento, 140f,140f,140f,140f))
            tableDatosMuestreo2.setFontSize(8f)

            //

            val muestraNumero = Cell(1, 1).add(Paragraph("MUESTRA No."))
            muestraNumero.setTextAlignment(TextAlignment.CENTER)
            muestraNumero.setBold()
            muestraNumero.setFontSize(8f)
            tableDatosMuestreo2.addCell(muestraNumero)

            //

            val olla = Cell(1, 1).add(Paragraph("OLLA"))
            olla.setTextAlignment(TextAlignment.CENTER)
            olla.setBold()
            olla.setFontSize(8f)
            tableDatosMuestreo2.addCell(olla)

            //

            val revenimiento = Cell(1, 1).add(Paragraph("REVENIMIENTO (cm)"))
            revenimiento.setTextAlignment(TextAlignment.CENTER)
            revenimiento.setBold()
            revenimiento.setFontSize(8f)
            tableDatosMuestreo2.addCell(revenimiento)

            //

            val temperatura = Cell(1, 1).add(Paragraph("TEMP. AMB. (°C)"))
            temperatura.setTextAlignment(TextAlignment.CENTER)
            temperatura.setBold()
            temperatura.setFontSize(8f)
            tableDatosMuestreo2.addCell(temperatura)

            //

            val molde = Cell(1, 1).add(Paragraph("No. MOLDE"))
            molde.setTextAlignment(TextAlignment.CENTER)
            molde.setBold()
            molde.setFontSize(8f)
            tableDatosMuestreo2.addCell(molde)

            //

            val identificacion = Cell(1, 1).add(Paragraph("IDENTIFICACIÓN ESPECÍMEN"))
            identificacion.setTextAlignment(TextAlignment.CENTER)
            identificacion.setBold()
            identificacion.setFontSize(8f)
            tableDatosMuestreo2.addCell(identificacion)

            //

            val reporte = Cell(1, 1).add(Paragraph("No. DE REPORTE"))
            reporte.setTextAlignment(TextAlignment.CENTER)
            reporte.setBold()
            reporte.setFontSize(8f)
            tableDatosMuestreo2.addCell(reporte)

            //

            val muestraTxt = Cell(4, 1).add(Paragraph(ReportesVigas.reporteSelecionado.muestra.toString()))
            muestraTxt.setTextAlignment(TextAlignment.CENTER)
            muestraTxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(muestraTxt)

            //

            val ollaTxt = Cell(4, 1).add(Paragraph(ReportesVigas.reporteSelecionado.olla))
            ollaTxt.setTextAlignment(TextAlignment.CENTER)
            ollaTxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(ollaTxt)

            //

            val diseno = Cell(1, 1).add(Paragraph("DISEÑO"))
            diseno.setTextAlignment(TextAlignment.CENTER)
            diseno.setFontSize(8f)
            tableDatosMuestreo2.addCell(diseno)

            //

            val temperaturatxt = Cell(4, 1).add(Paragraph(ReportesVigas.reporteSelecionado.temperatura.toString()))
            temperaturatxt.setTextAlignment(TextAlignment.CENTER)
            temperaturatxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(temperaturatxt)

            //

            val moldeTxt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.Molde1.toString()))
            moldeTxt.setTextAlignment(TextAlignment.CENTER)
            moldeTxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(moldeTxt)
            tableDatosMuestreo2.addCell(Cell(1,1))
            tableDatosMuestreo2.addCell(Cell(4,1))

            //

            val revDis = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.revenimientoDis.toString()))
            revDis.setTextAlignment(TextAlignment.CENTER)
            revDis.setFontSize(8f)
            tableDatosMuestreo2.addCell(revDis)

            //

            val molde2Txt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.Molde2.toString()))
            molde2Txt.setTextAlignment(TextAlignment.CENTER)
            molde2Txt.setFontSize(8f)
            tableDatosMuestreo2.addCell(molde2Txt)
            tableDatosMuestreo2.addCell(Cell(1,1))


            //

            val obtenido = Cell(1, 1).add(Paragraph("OBTENIDO"))
            obtenido.setTextAlignment(TextAlignment.CENTER)
            obtenido.setFontSize(8f)
            tableDatosMuestreo2.addCell(obtenido)

            //

            val molde3Txt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.Molde3.toString()))
            molde3Txt.setTextAlignment(TextAlignment.CENTER)
            molde3Txt.setFontSize(8f)
            tableDatosMuestreo2.addCell(molde3Txt)
            tableDatosMuestreo2.addCell(Cell(1,1))

            //

            val revObtenido = Cell(1, 1).add(Paragraph("R1: "+ ReportesVigas.reporteSelecionado.revenimientoR1.toString()+ "       R2: "+ ReportesVigas.reporteSelecionado.revenimientoR2.toString()))
            revObtenido.setTextAlignment(TextAlignment.CENTER)
            revObtenido.setFontSize(8f)
            tableDatosMuestreo2.addCell(revObtenido)

            //

//            val molde4Txt = Cell(1, 1).add(Paragraph(ReportesVigas.reporteSelecionado.Molde4.toString()))
            val molde4Txt = Cell(1, 1).add(Paragraph("---"))
            molde4Txt.setTextAlignment(TextAlignment.CENTER)
            molde4Txt.setFontSize(8f)
            tableDatosMuestreo2.addCell(molde4Txt)
            tableDatosMuestreo2.addCell(Cell(1,1))

            //

            val remision = Cell(1, 4).add(Paragraph("REMISIÓN No.:  "+ ReportesVigas.reporteSelecionado.remision))
            remision.setTextAlignment(TextAlignment.CENTER)
            remision.setFontSize(8f)
            tableDatosMuestreo2.addCell(remision)

            //

            val volumenMuestra = Cell(1, 3).add(Paragraph("VOLÚMEN (m³):  "+ ReportesVigas.reporteSelecionado.volumenMuestra))
            volumenMuestra.setTextAlignment(TextAlignment.CENTER)
            volumenMuestra.setFontSize(8f)
            tableDatosMuestreo2.addCell(volumenMuestra)

            //

            val estadosEspecimenes = Cell(1, 7).add(Paragraph("ESTADO DE LOS ESPECÍMENES: BIEN     /        MAL                  NO. MOLDE DE ESPECÍMEN EN MAL ESTADO:" ))
            estadosEspecimenes.setTextAlignment(TextAlignment.CENTER)
            estadosEspecimenes.setFontSize(8f)
            tableDatosMuestreo2.addCell(estadosEspecimenes)

            //

            val horarios = Cell(1, 1).add(Paragraph("HORARIOS:" ))
            horarios.setTextAlignment(TextAlignment.CENTER)
            horarios.setFontSize(8f)
            horarios.setBold()
            tableDatosMuestreo2.addCell(horarios)

            //

            val horarioSalida = Cell(1, 2).add(Paragraph("Salida de planta:  "+ ReportesVigas.reporteSelecionado.horaSalida+" h" ))
            horarioSalida.setTextAlignment(TextAlignment.CENTER)
            horarioSalida.setFontSize(8f)
            tableDatosMuestreo2.addCell(horarioSalida)

            //

            val horarioLLegada = Cell(1, 2).add(Paragraph("Llegada a obra:  "+ ReportesVigas.reporteSelecionado.horaLLegada +" h"))
            horarioLLegada.setTextAlignment(TextAlignment.CENTER)
            horarioLLegada.setFontSize(8f)
            tableDatosMuestreo2.addCell(horarioLLegada)

            //

            val horarioMuestreo = Cell(1, 2).add(Paragraph("Inicio muestreo:  "+ ReportesVigas.reporteSelecionado.horaMuestreo +" h"))
            horarioMuestreo.setTextAlignment(TextAlignment.CENTER)
            horarioMuestreo.setFontSize(8f)
            tableDatosMuestreo2.addCell(horarioMuestreo)

            //

            val textoInformacion = Cell(1, 7).add(Paragraph("\"LA INFORMACION CONTENIDA EN ESTE REGISTRO, SÓLO APLICA PARA LA MUESTRA TOMADA\""))
            textoInformacion.setTextAlignment(TextAlignment.CENTER)
            textoInformacion.setFontSize(8f)
            tableDatosMuestreo2.addCell(textoInformacion)

            //

            val observaciones = Cell(1, 7).add(Paragraph("OBSERVACIONES: "+ ReportesVigas.reporteSelecionado.observaciones))
            observaciones.setFontSize(8f)
            tableDatosMuestreo2.addCell(observaciones)

            //

            val identificacionEquipo = Cell(2, 2).add(Paragraph("IDENTIFICACIÓN DE \n EQUIPO"))
            identificacionEquipo.setTextAlignment(TextAlignment.CENTER)
            identificacionEquipo.setFontSize(8f)
            identificacionEquipo.setBold()
            tableDatosMuestreo2.addCell(identificacionEquipo)

            //

            val carretilla = Cell(1, 1).add(Paragraph("Carretilla:  "+ ReportesVigas.reporteSelecionado.carretilla))
            carretilla.setTextAlignment(TextAlignment.CENTER)
            carretilla.setFontSize(8f)
            tableDatosMuestreo2.addCell(carretilla)

            //

            val cono = Cell(1, 1).add(Paragraph("Cono:  "+ ReportesVigas.reporteSelecionado.cono))
            cono.setTextAlignment(TextAlignment.CENTER)
            cono.setFontSize(8f)
            tableDatosMuestreo2.addCell(cono)

            //

            val varilla = Cell(1, 1).add(Paragraph("Varilla:  "+ ReportesVigas.reporteSelecionado.varilla))
            varilla.setTextAlignment(TextAlignment.CENTER)
            varilla.setFontSize(8f)
            tableDatosMuestreo2.addCell(varilla)

            //

            val mazo = Cell(1, 1).add(Paragraph("Mazo de hule:  "+ ReportesVigas.reporteSelecionado.mazo))
            mazo.setTextAlignment(TextAlignment.CENTER)
            mazo.setFontSize(8f)
            tableDatosMuestreo2.addCell(mazo)

            //

            val termometro = Cell(1, 1).add(Paragraph("Termómetro:  "+ ReportesVigas.reporteSelecionado.termometro))
            termometro.setTextAlignment(TextAlignment.CENTER)
            termometro.setFontSize(8f)
            tableDatosMuestreo2.addCell(termometro)

            //

            val cucharon = Cell(1, 1).add(Paragraph("Cucharon:  "+ ReportesVigas.reporteSelecionado.cucharon))
            cucharon.setTextAlignment(TextAlignment.CENTER)
            cucharon.setFontSize(8f)
            tableDatosMuestreo2.addCell(cucharon)

            //

            val placa = Cell(1, 1).add(Paragraph("Placa:  "+ ReportesVigas.reporteSelecionado.placa))
            placa.setTextAlignment(TextAlignment.CENTER)
            placa.setFontSize(8f)
            tableDatosMuestreo2.addCell(placa)

            //

            val flexometro = Cell(1, 1).add(Paragraph("Flexómetro:  "+ ReportesVigas.reporteSelecionado.flexometro))
            flexometro.setTextAlignment(TextAlignment.CENTER)
            flexometro.setFontSize(8f)
            tableDatosMuestreo2.addCell(flexometro)

            //

            val enrasador = Cell(1, 1).add(Paragraph("Enrasador:  "+ ReportesVigas.reporteSelecionado.enrasador))
            enrasador.setTextAlignment(TextAlignment.CENTER)
            enrasador.setFontSize(8f)
            tableDatosMuestreo2.addCell(enrasador)
            tableDatosMuestreo2.addCell(Cell(1,1))

            //

            val elaboradoTxt = Cell(1, 3).add(Paragraph(ReportesVigas.reporteSelecionado.personal))
            elaboradoTxt.setTextAlignment(TextAlignment.CENTER)
            elaboradoTxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(elaboradoTxt)
            val pagina = Cell(2, 1).add(Paragraph("PÁG. 1 DE 1"))
            pagina.setTextAlignment(TextAlignment.CENTER)
            pagina.setFontSize(8f)
            pagina.setBold()
            tableDatosMuestreo2.addCell(pagina)

            val revisadoTxt = Cell(1, 3).add(Paragraph(""))
            revisadoTxt.setTextAlignment(TextAlignment.CENTER)
            revisadoTxt.setFontSize(8f)
            tableDatosMuestreo2.addCell(revisadoTxt)

            //

            val elaborado = Cell(1, 3).add(Paragraph("ELABORADO POR:"))
            elaborado.setTextAlignment(TextAlignment.CENTER)
            elaborado.setFontSize(8f)
            elaborado.setBold()
            tableDatosMuestreo2.addCell(elaborado)




            val revisado = Cell(1, 3).add(Paragraph("REVISADO Y AUTORIZADO POR:"))
            revisado.setTextAlignment(TextAlignment.CENTER)
            revisado.setFontSize(8f)
            revisado.setBold()
            tableDatosMuestreo2.addCell(revisado)


            document.add(tableDatosMuestreo2)





            // DATOS DE MUESTREO 1
            val tableDatosMuestreo1 = Table(floatArrayOf(130f,anchoDocumento, 130f,anchoDocumento))
            tableDatosMuestreo1.setFontSize(8f)

            //

            document.add(tableDatosMuestreo1)



            // DATOS DE MUESTREO
//            val tableDatosMuestreo = Table(floatArrayOf(130f,anchoDocumento, 100f,anchoDocumento))
//            tableDatosMuestreo.setFontSize(8f)
////
//            val elemetoColado = Cell(1, 4).add(Paragraph("Elemento colado:"))
//            elemetoColado.setTextAlignment(TextAlignment.CENTER)
//            elemetoColado.setBold()
//            tableDatosMuestreo.addCell(elemetoColado)

            //






            document.close()

            println("Documento PDF creado correctamente.")

            // Abrir el documento PDF
//            abrirPDF(context, archivoPDF)
            ObraAdapter.notifyDataSetChanged()
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al generar el PDF: ${ex.message}", Toast.LENGTH_SHORT).show()
        }

        // Abrir el documento PDF
        abrirPDF(archivoPDF)
    } catch (ex: Exception) {
        Toast.makeText(this, "Error al generar el PDF: ${ex.message}", Toast.LENGTH_SHORT).show()
    }
    private fun abrirPDF(archivoPDF: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                this.packageName + ".fileprovider",
                archivoPDF
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(this, "Error al abrir el PDF: ${ex.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
