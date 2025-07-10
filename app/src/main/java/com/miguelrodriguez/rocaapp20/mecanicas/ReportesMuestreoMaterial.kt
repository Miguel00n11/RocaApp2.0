package com.miguelrodriguez.rocaapp20.mecanicas

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import com.google.firebase.storage.StorageReference
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
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
import com.itextpdf.layout.properties.VerticalAlignment
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones.Companion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale

class ReportesMuestreoMaterial : AppCompatActivity() {
    private lateinit var dataReference: DatabaseReference
    private lateinit var btnRegistroMuestreoMaterial: Button
    private lateinit var etFechaMuestreoMecanica: Button
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObraMecanica>
    private lateinit var listaEstratossmutableListOf: MutableList<ClaseEstratos>
    private lateinit var listaImagenesmutableListOf: MutableList<String>

    private lateinit var storage:FirebaseStorage
    private lateinit var personal: String
    private lateinit var reporteSelecionado: ClaseObraMecanica


    companion object {
        lateinit var reporteSelecionadoMuestroMaterial: ClaseObraMecanica
        var editarMuestreoMaterial: Boolean = false
    }

    private lateinit var btnRegistroCompactacion: Button
    private lateinit var rvObrasMecanicas: RecyclerView
    private lateinit var ObraAdapter: ObraMecanicaAdapter
    private lateinit var claseObra: ClaseObra

    private lateinit var estratosAdapter: EstratosAdapter
    private lateinit var rvEstratos: RecyclerView

    private lateinit var etCliente: EditText
    private lateinit var etObra: EditText
    private lateinit var etlocalizacion: EditText
    private lateinit var etAtencion: EditText
    private lateinit var etFecha: EditText
//    private lateinit var tipoMuestreo: Spinner

    private lateinit var etSondeoNumMuestreoMecanica: EditText
    private lateinit var etUbicacionMuestreoMecanica: EditText
    private lateinit var etNAFMuestreoMecanica: EditText
    private lateinit var etProfunMuestreoMecanica: EditText
    private lateinit var etProfunNAFMuestreoMecanica: EditText
    private lateinit var etHoraMuestreoMecanica: EditText







    private lateinit var llave: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_muestreo_material)

        InitComponet()
        InitUI()
    }
    private lateinit var listaReporteFallaGA: MutableList<ClaseObraMecanica>

    private fun InitComponet() {
        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(
            1,
            "1",
            1.0,
            1.0,
            1.0,
            "1.0",
            "1.0"
        ))
        listaImagenesmutableListOf = mutableListOf("a1")
        listaObrasmutableListOf = mutableListOf()
        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion

        reporteSelecionadoMuestroMaterial = ClaseObraMecanica(
            1,
            "obra",
            "cliente",
            "localizacion",
            "atencion",
            "fecha",
            "sondeo_num",
            "ubicacion",
            false,
            "profundidad_muestreo",
            "profundidad_naf",
            "hora",
            "llave",
            "latitud",
            "longitud",
            listaEstratossmutableListOf,
            listaImagenesmutableListOf
        )

//        etObra = findViewById(R.id.etObraMuestreoMecanica)
//        etCliente = findViewById(R.id.etClienteMuestreoMecanica)
//        etlocalizacion = findViewById(R.id.etLocalizacionMuestreoMecanica)
//        etAtencion = findViewById(R.id.etAtencionMuestreoMecanica)
//        etFecha = findViewById(R.id.etFechaMuestreoMecanica)
//        etSondeoNumMuestreoMecanica = findViewById(R.id.etSondeoNumMuestreoMecanica)
//        etUbicacionMuestreoMecanica = findViewById(R.id.etUbicacionMuestreoMecanica)
//        etNAFMuestreoMecanica = findViewById(R.id.etNAFMuestreoMecanica)
//        etProfunMuestreoMecanica = findViewById(R.id.etProfundidadMuestreoMecanica)
//        etProfunNAFMuestreoMecanica = findViewById(R.id.etProfundidadNAFMuestreoMecanica)
//        rvEstratos = findViewById(R.id.rvMuestreoEstratos)

//        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(1, "h", 1.0))
        btnRegistroMuestreoMaterial = findViewById(R.id.btnRegistroMuestreoMaterial)
        rvObrasMecanicas = findViewById(R.id.rvObrasMecanicas)
    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMecanica) {

        etObra.setText(reporteSelecionado.Obra)
        etCliente.setText(reporteSelecionado.cliente)
        etAtencion.setText(reporteSelecionado.atencion)
        etlocalizacion.setText(reporteSelecionado.localizacion)
        etFecha.setText(reporteSelecionado.fecha)
        etSondeoNumMuestreoMecanica.setText(reporteSelecionado.sondeo_num)
        etUbicacionMuestreoMecanica.setText(reporteSelecionado.ubicacion)
        etNAFMuestreoMecanica.setText(reporteSelecionado.naf.toString())

        etProfunMuestreoMecanica.setText(reporteSelecionado.profundidad_muestreo)
        etProfunNAFMuestreoMecanica.setText(reporteSelecionado.profundidad_naf)


//
//        etSubTramo.setText(reporteSelecionado.subtramo)
//        procedencia.setText(reporteSelecionado.procedencia)
//        lugarMuestreo.setText(reporteSelecionado.lugarMuestreo)
//        estacion.setText(reporteSelecionado.estacion)
//        tipoMuestreo.setSelection(reporteSelecionado.tipoMuestreo)
//        estudioMuestreo.setSelection(reporteSelecionado.estudioMuestreo)
        llave = reporteSelecionado.llave

        listaEstratossmutableListOf = reporteSelecionado.listaEstratos

        estratosAdapter =
            EstratosAdapter(reporteSelecionado.listaEstratos,
                onEstratoSelected = { position -> onItemSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvEstratos.layoutManager = LinearLayoutManager(this)
        rvEstratos.adapter = estratosAdapter

    }

    private fun InitUI() {
        btnRegistroMuestreoMaterial.setOnClickListener {
            val intent = Intent(this, RegistroMecanica::class.java)
            startActivity(intent)
        }
        cargarDatosFirebase()
        cargarObras(dataReference)

    }


    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

//        listaCalasmutableListOf[position], position


        editarMuestreoMaterial = true
        reporteSelecionadoMuestroMaterial = listaObrasmutableListOf[position]
        val intent = Intent(this, RegistroMecanica::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }

    private fun onItemDelete(position: Int) {
        if (isNetworkAvailable()) {
            val reportKey =
                listaObrasmutableListOf[position].llave // Utiliza la clave única del informe

            // Elimina el informe de la base de datos Firebase
            deleteReport(reportKey)

            // Elimina el informe de la lista local
            listaObrasmutableListOf.removeAt(position)

            // Notifica al adaptador que los datos han cambiado
            updateTask()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    private fun deleteReport(reportKey: String) {
        val reportReference = FirebaseDatabase.getInstance().reference.child("Mecanicas").child("ReportesMecanicas").child(personal).child(reportKey)
        val reportReference1= FirebaseDatabase.getInstance().reference.child("Mecanicas").child("RespaldoMecanicas").child(personal).child(reportKey)
        val reportReference2= FirebaseDatabase.getInstance().reference.child("ImagenesMecanicas").child(personal).child(reportKey)

//        val storage = FirebaseStorage.getInstance()
//        val storage = FirebaseStorage.getInstance().reference.child(reportKey)

        val storageRef: StorageReference = storage.reference.child(reportKey)


        println(reportReference.toString())
        println(storageRef.toString())



        // Eliminar imágenes asociadas en Firebase Storage
        val imageRef =
            storage.reference.child(reportKey) // Suponiendo que las imágenes están en una carpeta con el nombre del `reportKey`

        imageRef.listAll().addOnSuccessListener { listResult ->
            // Recorre cada archivo en el directorio y elimínalo
            for (fileRef in listResult.items) {
                fileRef.delete().addOnSuccessListener {
                    Log.d("FirebaseStorage", "Imagen eliminada: ${fileRef.name}")
                }.addOnFailureListener { e ->
                    Log.e(
                        "FirebaseStorage",
                        "Error al eliminar imagen ${fileRef.name}: ${e.message}"
                    )
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseStorage", "Error al listar archivos para eliminar: ${e.message}")
        }


        reportReference.removeValue().addOnSuccessListener {
            Toast.makeText(
                this,
                "Reporte eliminado existosamente.",
                Toast.LENGTH_SHORT
            ).show() }

        reportReference1.removeValue().addOnSuccessListener {}

        reportReference2.removeValue().addOnSuccessListener {}

    }

    private fun updateTask() {
        ObraAdapter.notifyDataSetChanged()
    }


    private fun onVerReporteFallaMantenimientoGA(
        position: Int,
        listaReportes: MutableList<ClaseObraMecanica>
    ) {
        val reporteSeleccionado = listaReportes[position]

        Toast.makeText(this, reporteSeleccionado.listaImagenes.toString(), Toast.LENGTH_SHORT).show()
        // Verificar imágenes
        Toast.makeText(this, reporteSeleccionado.listaImagenes.count().toString(), Toast.LENGTH_SHORT).show()
        if (reporteSeleccionado.listaImagenes.isNotEmpty()) {
            Log.d("ListaImagenes", "Imágenes del reporte seleccionado: ${reporteSeleccionado.listaImagenes.count()}")
        } else {
            Log.d("ListaImagenes", "No hay imágenes asociadas al reporte seleccionado.")
        }

        // Limpiar PDF anterior
        val directorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        directorio?.listFiles()?.forEach { it.delete() }

        // Crear y procesar PDF
        lifecycleScope.launch {
            val imagenesProcesadas = mutableListOf<Image>()
            for (url in reporteSeleccionado.listaImagenes) {
                try {
                    val imageBytes = descargarImagenConGlide(this@ReportesMuestreoMaterial, url)
                    if (imageBytes != null) {
                        val imageData = ImageDataFactory.create(imageBytes)
                        val image = Image(imageData).apply {
                            scaleToFit(PageSize.LETTER.width - 100, 200f)
                            setHorizontalAlignment(HorizontalAlignment.CENTER)
                        }
                        imagenesProcesadas.add(image)
                    } else {
                        Log.e("PDFImageError", "No se pudo descargar la imagen desde la URL: $url")
                    }
                } catch (e: Exception) {
                    Log.e("PDFImageError", "Error al procesar la imagen: ${e.message}")
                }
            }


            // Generar el PDF usando las imágenes procesadas
            generarPDF(reporteSeleccionado, imagenesProcesadas)
        }
    }
    private suspend fun descargarImagenConGlide(context: Context, url: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val futureTarget = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()

                val bitmap = futureTarget.get()
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)

                byteArrayOutputStream.toByteArray().also {
                    Log.d("GlideImageDownload", "Imagen descargada con éxito: $url")
                }
            } catch (e: Exception) {
                Log.e("ImageDownloadError", "Error al descargar imagen con Glide: ${e.message}")
                null
            }
        }
    }
    private fun generarPDF(reporte: ClaseObraMecanica, imagenes: List<Image>) {
        try {
            val fechaActual = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val directorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val archivoPDF = File(directorio, "reporte_mecanica_$fechaActual.pdf")

            val outputStream = FileOutputStream(archivoPDF)
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.LETTER)
            document.setFontSize(8f)







            //calcular el ancho disponible para la tabla
            val anchoDocumento = PageSize.LETTER.width - 72f * 2
            val numeroColumnas = 1
            val anchoColiumna = anchoDocumento / numeroColumnas

            // Cargar la imagen desde el directorio drawable
            val drawableId =
                R.drawable.logoroca // Reemplaza 'logoroca' con el nombre de tu imagen
            val bitmap = BitmapFactory.decodeResource(this.resources, drawableId)

            // Convertir el bitmap en un objeto Image de iText
            val outputStream1 = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream1)
            val imageData = ImageDataFactory.create(outputStream1.toByteArray())
            val image = Image(imageData)
            image.scale(.05f, .05f)

            // Crear una tabla
            val table = Table(floatArrayOf(150f, 150f, 150f, 150f))

            // Agregar celda con imagen
            val cellImage = Cell(5, 1)
            cellImage.add(image.setHorizontalAlignment(HorizontalAlignment.CENTER))
            table.addCell(cellImage)


            // Crear tabla para titulo de datos de obra

            var tableTituloDatosdeObraDeControl =
                Cell(1, 3).add(Paragraph("Datos de control"))
            tableTituloDatosdeObraDeControl.setTextAlignment(TextAlignment.CENTER)
            tableTituloDatosdeObraDeControl.setBold()
            tableTituloDatosdeObraDeControl.setBackgroundColor(DeviceRgb(192, 192, 192))
            tableTituloDatosdeObraDeControl.setFontSize(10f)
            table.addCell(tableTituloDatosdeObraDeControl)


            var etiquetaNombreFormato = Cell(1, 3).add(Paragraph("Nombre del formato"))
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setBackgroundColor(DeviceRgb(192, 192, 192))
            table.addCell(etiquetaNombreFormato)

            var textoNombreFormato = Cell(1, 3).add(Paragraph("Reporte de muestreo"))
            .setTextAlignment(TextAlignment.CENTER)
            .setItalic()
            .setUnderline()
            table.addCell(textoNombreFormato)

            var etiquetaTitulodelProyecto = Cell(1, 1).add(Paragraph("Código del formato"))
            etiquetaTitulodelProyecto.setTextAlignment(TextAlignment.CENTER)
            etiquetaTitulodelProyecto.setBold()
            etiquetaTitulodelProyecto.setBackgroundColor(DeviceRgb(192, 192, 192))
            table.addCell(etiquetaTitulodelProyecto)

            var textoTitulodelProyecto = Cell(1, 1).add(Paragraph("Procedimiento"))
            textoTitulodelProyecto.setTextAlignment(TextAlignment.CENTER)
            textoTitulodelProyecto.setItalic()
            textoTitulodelProyecto.setUnderline()
            textoTitulodelProyecto.setBackgroundColor(DeviceRgb(192, 192, 192))
            table.addCell(textoTitulodelProyecto)

            var textoTitulodelProyecto1 = Cell(1, 1).add(Paragraph("Fecha de sondeo"))
            .setTextAlignment(TextAlignment.CENTER)
            .setItalic()
            .setUnderline()
            .setBackgroundColor(DeviceRgb(192, 192, 192))
            table.addCell(textoTitulodelProyecto1)

            var textoTitulodelFormato = Cell(1, 1).add(Paragraph("F1-PR21"))
            textoTitulodelFormato.setTextAlignment(TextAlignment.CENTER)
            textoTitulodelFormato.setItalic()
            textoTitulodelFormato.setUnderline()
            table.addCell(textoTitulodelFormato)

            var textoTitulodelProcedimiento = Cell(1, 1).add(Paragraph("PR21"))
            textoTitulodelProcedimiento.setTextAlignment(TextAlignment.CENTER)
            textoTitulodelProcedimiento.setItalic()
            textoTitulodelProcedimiento.setUnderline()
            table.addCell(textoTitulodelProcedimiento)

            var textoTitulodelFecha = Cell(1, 1).add(Paragraph(reporte.fecha))
            textoTitulodelFecha.setTextAlignment(TextAlignment.CENTER)
            textoTitulodelFecha.setItalic()
            textoTitulodelFecha.setUnderline()
            table.addCell(textoTitulodelFecha)






            document.add(table)



            // Crear una tabla para reporte de falla
            val tableDatosObra = Table(floatArrayOf(200f, 200f, 200f, 200f,200f))


            var etiquetaReporteFalla = Cell(1, 5).add(Paragraph("Datos de obra"))
            etiquetaReporteFalla.setTextAlignment(TextAlignment.CENTER)
            etiquetaReporteFalla.setBold()
            etiquetaReporteFalla.setBackgroundColor(DeviceRgb(192, 192, 192))
            tableDatosObra.addCell(etiquetaReporteFalla)

            var etiquetaCliente = Cell(1, 1).add(Paragraph("Cliente:"))
            .setBackgroundColor(DeviceRgb(192, 192, 192))
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(etiquetaCliente)

            var textoCliente = Cell(1, 4).add(Paragraph(reporte.cliente))
            .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(textoCliente)

            var etiquetaObra = Cell(1, 1).add(Paragraph("Obra:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(etiquetaObra)

            var textoNoObra = Cell(1, 4).add(Paragraph(reporte.Obra))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(textoNoObra)


            var etiquetaLocalizacion= Cell(1, 1).add(Paragraph("Localización:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(etiquetaLocalizacion)

            var textoLocalizacion = Cell(1, 4).add(Paragraph(reporte.localizacion))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(textoLocalizacion)

            var etiquetaAtencion= Cell(1, 1).add(Paragraph("En atención:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(etiquetaAtencion)

            var textoAtencion = Cell(1, 2).add(Paragraph(reporte.atencion))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(textoAtencion)

            var etiquetaExpediente= Cell(1, 1).add(Paragraph("Expediente:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(etiquetaExpediente)

            var textoExpediente = Cell(1, 1).add(Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosObra.addCell(textoExpediente)

            document.add(tableDatosObra)



            val tableDatosSondeo = Table(floatArrayOf(200f, 200f, 200f, 200f,200f))

            var etiquetaDatosSondeo = Cell(1, 5).add(Paragraph("Datos del sondeo"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setBackgroundColor(DeviceRgb(192, 192, 192))
            tableDatosSondeo.addCell(etiquetaDatosSondeo)

            var etiquetaSondeoNum= Cell(1, 1).add(Paragraph("Sondeo Núm.:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaSondeoNum)

            var textoSondeoNum = Cell(1, 2).add(Paragraph(reporte.sondeo_num))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoSondeoNum)

            var etiquetaProfundidadSondeo= Cell(1, 1).add(Paragraph("Profundidad del sondeo [cm]:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaProfundidadSondeo)

            var textoProfundidadSondeo = Cell(1, 1).add(Paragraph(reporte.profundidad_muestreo))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoProfundidadSondeo)

            var etiquetaUicacion= Cell(1, 1).add(Paragraph("Ubicación:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaUicacion)

            var textoUbicacion = Cell(1, 2).add(Paragraph(reporte.ubicacion))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoUbicacion)

            var etiquetaHora= Cell(1, 1).add(Paragraph("Hora de muestreo:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaHora)

            var textoHora = Cell(1, 1).add(Paragraph(reporte.hora))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoHora)

            var etiquetaNAF= Cell(1, 1).add(Paragraph("NAF:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaNAF)

            var textoNAF = Cell(1, 2).add(Paragraph(reporte.naf.toString()))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoNAF)

            var etiquetaProfundidadNAF= Cell(1, 1).add(Paragraph("Profundidad NAF [cm]:"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaProfundidadNAF)

            var textoProfundidadNAF = Cell(1, 1).add(Paragraph(reporte.profundidad_naf))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoProfundidadNAF)

            var etiquetaCoordenadas= Cell(1, 1).add(Paragraph(reporte.latitud + reporte.longitud))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaCoordenadas)

            var etiquetaLatitud= Cell(1, 1).add(Paragraph("Latitud:"))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaLatitud)

            var textoLatitud = Cell(1, 1).add(Paragraph(reporte.latitud))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoLatitud)

            var etiquetaLongitud= Cell(1, 1).add(Paragraph("Longitud:"))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(etiquetaLongitud)

            var textoLongitud = Cell(1, 1).add(Paragraph(reporte.longitud))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosSondeo.addCell(textoLongitud)

            document.add(tableDatosSondeo)


            val tableDatosEstratoMuestreo = Table(floatArrayOf(200f, 200f, 200f, 200f,200f, 200f,200f))

            var etiquetaDatosEstratoMuestreo= Cell(1, 7).add(Paragraph("Datos del estrato muestreado"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaDatosEstratoMuestreo)

            var etiquetaNumEstrato= Cell(2, 1).add(Paragraph("Numero de estrato"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaNumEstrato)

            var etiquetaTipoMuestreo= Cell(2, 1).add(Paragraph("Tipo de muestreo"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaTipoMuestreo)

            var etiquetaProfundidad= Cell(1, 2).add(Paragraph("Profundidad [m]"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaProfundidad)

            var etiquetaProfundidadMuestreo= Cell(2, 1).add(Paragraph("Profundidad del muestreo [cm]"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaProfundidadMuestreo)

            var etiquetaClasificacionVisual= Cell(2, 1).add(Paragraph("Clasificación visual"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaClasificacionVisual)

            var etiquetaObservaciones= Cell(2, 1).add(Paragraph("Clasificación visual"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaObservaciones)

            var etiquetaProfundidadInicio= Cell(1, 1).add(Paragraph("Inicio"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaProfundidadInicio)

            var etiquetaProfundidadFinal= Cell(1, 1).add(Paragraph("Final"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setTextAlignment(TextAlignment.CENTER)
            tableDatosEstratoMuestreo.addCell(etiquetaProfundidadFinal)

            val alturaTexto = 8f
            reporte.listaEstratos.forEach { muestreo ->
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.idEstrato + 1}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.tipo_muestreo}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.profundidad_inicio}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.profundidad_final}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.profundidad_muestreo}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.clasificacion_visual}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
                tableDatosEstratoMuestreo.addCell(Cell().add(Paragraph("${muestreo.observaciones}"))).setFontSize(alturaTexto).setHorizontalAlignment(HorizontalAlignment.CENTER)
//                tableDatosEstratoMuestreo.addCell(Cell(1,2).add(Paragraph("${muestreo.Porcentaje}"))).setFontSize(alturaTexto)
            }

            document.add(tableDatosEstratoMuestreo)









            // Crear una tabla para las imágenes
            val tableDeImagenes = Table(floatArrayOf(200f, 200f, 200f)) // Ajusta los tamaños de las columnas según sea necesario

// Título para la sección de imágenes
            val etiquetaReporteImagenFalla = Cell(1, 3)
                .add(Paragraph("Imágenes de la falla"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setBackgroundColor(DeviceRgb(192, 192, 192))
            tableDeImagenes.addCell(etiquetaReporteImagenFalla)

// Dimensiones máximas de la celda
            val maxWidth = 150f
            val maxHeight = 130f

// Agregar imágenes a la tabla
            if (imagenes.isNotEmpty()) {
                for (image in imagenes) {
                    // Ajustar la escala de la imagen para que se ajuste a la celda
                    val imageWidth = image.imageWidth
                    val imageHeight = image.imageHeight
                    val widthScale = maxWidth / imageWidth
                    val heightScale = maxHeight / imageHeight
                    val scale = minOf(widthScale, heightScale) // Escoge la escala que mantenga las dimensiones dentro de la celda

                    image.scale(scale, scale) // Aplica el escalado
                    image.setHorizontalAlignment(HorizontalAlignment.CENTER)

                    // Agregar la imagen dentro de una celda
                    val cell = Cell().add(image)
                    cell.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    cell.setVerticalAlignment(VerticalAlignment.MIDDLE)
                    cell.setPadding(10f) // Añadir un margen interno
                    tableDeImagenes.addCell(cell)
                }
            } else {
                val cellNoImages = Cell(1, 3)
                    .add(Paragraph("No hay imágenes disponibles.").setTextAlignment(TextAlignment.CENTER))
                tableDeImagenes.addCell(cellNoImages)
            }

// Agregar la tabla al documento
            document.add(tableDeImagenes)

            val tableNormaReferencia = Table(floatArrayOf(200f, 200f, 200f, 200f, 200f, 200f))

            var etiquetaNormaReferencia= Cell(1, 6).add(Paragraph("Norma de referencia: NMX-C-467-ONNCCE-2019, Métodos de muestreo"))
                .setBackgroundColor(DeviceRgb(192, 192, 192))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            tableNormaReferencia.addCell(etiquetaNormaReferencia)

            var textoMuestreador = Cell(1, 2).add(Paragraph("Muestreador"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)

            tableNormaReferencia.addCell(textoMuestreador)

            var textoEspacioVacio = Cell(1, 2).add(Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER)
            tableNormaReferencia.addCell(textoEspacioVacio)

            var textoAutoriza = Cell(1, 2).add(Paragraph("Autoriza:"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderLeft(Border.NO_BORDER)
            tableNormaReferencia.addCell(textoAutoriza)

            var etiquetaMuestreador= Cell(1, 2).add(Paragraph("Nombre de muestreador"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
            tableNormaReferencia.addCell(etiquetaMuestreador)


            tableNormaReferencia.addCell(textoEspacioVacio)


            var etiquetaAutoriza= Cell(1, 2).add(Paragraph("Nombre de quien autoriza"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)


            tableNormaReferencia.addCell(etiquetaAutoriza)


            document.add(tableNormaReferencia)



            val tableNotasAdicionales = Table(floatArrayOf(200f, 200f, 200f, 200f, 200f, 200f))

            var etiquetaNotasAdicionales= Cell(1, 6).add(Paragraph("Se prohibe la reproducción total o parcial de este documento sin la autorización de laboratorio ROCA"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setItalic()
            tableNotasAdicionales.addCell(etiquetaNotasAdicionales)

            var etiquetaNotasAdicionales1= Cell(1, 6).add(Paragraph("www.rocalaboratorio.com"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setItalic()
            tableNotasAdicionales.addCell(etiquetaNotasAdicionales1)


            document.add(tableNotasAdicionales)



//
//
//// Crear una tabla para Reporte de Mantenimiento
//            val tableReporteDeMantenimiento = Table(floatArrayOf(200f, 50f, 200f, 150f,200f))
//
//            var etiquetaReporteDeMantenimiento = Cell(1, 5).add(Paragraph("Reporte de Mantenimiento"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//                .setBackgroundColor(DeviceRgb(192, 192, 192))
//            tableReporteDeMantenimiento.addCell(etiquetaReporteDeMantenimiento)
//
//            var etiquetaPeriodoActividad = Cell(1, 1).add(Paragraph("Periodo de la actividad"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaPeriodoActividad)
//
//            var etiquetaDel = Cell(1, 1).add(Paragraph("del"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaDel)
//
//            var textoPeriodoActividad = Cell(1, 1).add(Paragraph(reporte.fecha))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(textoPeriodoActividad)
//
//            var etiquetaAl = Cell(1, 1).add(Paragraph("al"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaAl)
//
////            var textoPeriodoActividadAl = Cell(1, 1).add(Paragraph(reporte.fechaMantenimiento))
////                .setBold()
////                .setTextAlignment(TextAlignment.CENTER)
////            tableReporteDeMantenimiento.addCell(textoPeriodoActividadAl)
//
//            var etiquetaResponsableGeneralMIP = Cell(1, 1).add(Paragraph("Responsable general MIPGroup"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaResponsableGeneralMIP)
//
//            var textoResponsableGeneralMIP = Cell(1, 2).add(Paragraph("Ing. Marco Antonio Perez Marquez"))
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(textoResponsableGeneralMIP)
//
//            var etiquetaResponsableSitiolMIP = Cell(1, 1).add(Paragraph("Responsable en sitio MIPGroup"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaResponsableSitiolMIP)
//
//            var textoResponsableSitiolMIP = Cell(1, 2).add(Paragraph("Ing. Antonio Jimenez"))
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(textoResponsableSitiolMIP)
//
//            var etiquetaTecnicoMIP = Cell(1, 1).add(Paragraph("Tecnico (s) MIPGroup:"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaTecnicoMIP)
//
////            var textoTecnicoMIP = Cell(1, 2).add(Paragraph(reporte.TecnicoMantenimiento))
////                .setTextAlignment(TextAlignment.CENTER)
////            tableReporteDeMantenimiento.addCell(textoTecnicoMIP)
//
//            var etiquetaReporteMantenimietoFalla = Cell(1, 1).add(Paragraph("Falla"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableReporteDeMantenimiento.addCell(etiquetaReporteMantenimietoFalla)
//
////            var textoReporteMantenimietoFallaP = Cell(1, 2).add(Paragraph(reporte.FallaMantenimiento))
////            textoReporteMantenimietoFallaP.setTextAlignment(TextAlignment.CENTER)
////            tableReporteDeMantenimiento.addCell(textoReporteMantenimietoFallaP)
//
//
//            document.add(tableReporteDeMantenimiento)
//
//
//
//            val tableRumenDeLaActividad= Table(floatArrayOf(200f, 50f, 200f, 50f,200f))
//
//            var etiquetaResumenDeLaActividad = Cell(1, 5)
//                .add(Paragraph("Resumen de la actividad"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//                .setBackgroundColor(DeviceRgb(192, 192, 192))
//            tableRumenDeLaActividad.addCell(etiquetaResumenDeLaActividad)
//
////            var textoResumenDeLaActividad = Cell(1, 5).add(Paragraph(reporte.resumenActividadMantenimiento))
////            textoResumenDeLaActividad.setTextAlignment(TextAlignment.CENTER)
////            tableRumenDeLaActividad.addCell(textoResumenDeLaActividad)
//
////            document.add(tableRumenDeLaActividad)
//
//
//            var etiquetaMaterialesUtilizados = Cell(1, 1)
//                .setBold()
//                .add(Paragraph("Materiales utilizados:"))
//                .setTextAlignment(TextAlignment.CENTER)
//            tableRumenDeLaActividad.addCell(etiquetaMaterialesUtilizados)
//
////            var textoMaterialesUtilizados = Cell(1, 4).add(Paragraph(reporte.materialesUtilizadosMantenimiento))
////                .setTextAlignment(TextAlignment.CENTER)
////            tableRumenDeLaActividad.addCell(textoMaterialesUtilizados)
//
//
//
////            var textoObservaciones = Cell(1, 4).add(Paragraph(reporte.ObservacionesMantenimiento))
////                .setTextAlignment(TextAlignment.CENTER)
////            tableRumenDeLaActividad.addCell(textoObservaciones)
//
//            var etiquetaNotas = Cell(1, 1)
//                .add(Paragraph("Notas:"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//            tableRumenDeLaActividad.addCell(etiquetaNotas)
//
////            var textoNotas = Cell(1, 4).add(Paragraph(reporte.NotasMantenimiento))
////                .setTextAlignment(TextAlignment.CENTER)
////            tableRumenDeLaActividad.addCell(textoNotas)
//
//
//            document.add(tableRumenDeLaActividad)
//
//
////            val tableImagenesDeLaActividad= Table(floatArrayOf(200f, 50f, 200f, 50f,200f))
////            // Imagenes de mantenimiento
////            var etiquetaImagenDeLaActividad = Cell(1, 5)
////                .add(Paragraph("Imagen de la Actividad"))
////                .setTextAlignment(TextAlignment.CENTER)
////                .setBackgroundColor(DeviceRgb(192, 192, 192))
////            tableImagenesDeLaActividad.addCell(etiquetaImagenDeLaActividad)
////
////            var textoImagenDeLaActividad = Cell(1, 5).add(Paragraph(""))
////            textoImagenDeLaActividad.setTextAlignment(TextAlignment.CENTER)
////            tableImagenesDeLaActividad.addCell(textoImagenDeLaActividad)
////
////
////
////            document.add(tableImagenesDeLaActividad)
//
//            // Crear una tabla para las imágenes
//            val tableDeImagenesMantenimiento = Table(floatArrayOf(200f, 200f, 200f)) // Ajusta los tamaños de las columnas según sea necesario
//
//// Título para la sección de imágenes
//            val etiquetaReporteImagenFallaMantenimiento = Cell(1, 3)
//                .add(Paragraph("Imágenes de la actividad"))
//                .setBold()
//                .setTextAlignment(TextAlignment.CENTER)
//                .setBackgroundColor(DeviceRgb(192, 192, 192))
//            tableDeImagenesMantenimiento.addCell(etiquetaReporteImagenFallaMantenimiento)
//
//// Dimensiones máximas de la celda
//            val maxWidthMantenimiento = 150f
//            val maxHeightMantenimiento = 130f
//
//// Agregar imágenes a la tabla
////            if (imagenesMantenimiento.isNotEmpty()) {
////                for (image in imagenesMantenimiento) {
////                    // Ajustar la escala de la imagen para que se ajuste a la celda
////                    val imageWidth = image.imageWidth
////                    val imageHeight = image.imageHeight
////                    val widthScale = maxWidthMantenimiento / imageWidth
////                    val heightScale = maxHeightMantenimiento / imageHeight
////                    val scale = minOf(widthScale, heightScale) // Escoge la escala que mantenga las dimensiones dentro de la celda
////
////                    image.scale(scale, scale) // Aplica el escalado
////                    image.setHorizontalAlignment(HorizontalAlignment.CENTER)
////
////                    // Agregar la imagen dentro de una celda
////                    val cell = Cell().add(image)
////                    cell.setHorizontalAlignment(HorizontalAlignment.CENTER)
////                    cell.setVerticalAlignment(VerticalAlignment.MIDDLE)
////                    cell.setPadding(10f) // Añadir un margen interno
////                    tableDeImagenesMantenimiento.addCell(cell)
////                }
////            } else {
////                val cellNoImages = Cell(1, 3)
////                    .add(Paragraph("No hay imágenes disponibles.").setTextAlignment(TextAlignment.CENTER))
////                tableDeImagenesMantenimiento.addCell(cellNoImages)
////            }
//
//// Agregar la tabla al documento
//            document.add(tableDeImagenesMantenimiento)





            document.close()
            Log.d("PDF", "Documento PDF creado correctamente.")
            abrirPDF(archivoPDF)
        } catch (e: Exception) {
            Log.e("PDFError", "Error al crear el PDF: ${e.message}")
        }
    }
    private fun abrirPDF(archivoPDF: File) {
//        try {
        val uri = FileProvider.getUriForFile(this, "com.miguelrodriguez.rocaapp20.fileprovider", archivoPDF)


        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
    }


    private fun cargarObras(dataReference: DatabaseReference){
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                for (snapshot in dataSnapshot.children) {
                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)
                    val localizacion = snapshot.child("localizacion").getValue(String::class.java)
                    val atencion = snapshot.child("atencion").getValue(String::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
//                    val estudioMuestreo =snapshot.child("estudioMuestreo").getValue(String::class.java)
//                    val tipoMuestreo = snapshot.child("tipoMuestreo").getValue(String::class.java)

                    val sondeo_num = snapshot.child("sondeo_num").getValue(String::class.java)
                    val ubicacion = snapshot.child("ubicacion").getValue(String::class.java)
                    val naf = snapshot.child("naf").getValue(Boolean::class.java)
                    val profundidad_muestreo =snapshot.child("profundidad_muestreo").getValue(String::class.java)
                    val profundidad_naf =snapshot.child("profundidad_naf").getValue(String::class.java)
                    val hora = snapshot.child("hora").getValue(String::class.java)

                    var llave = snapshot.child("llave").getValue(String::class.java)
//                    val listaCalas = snapshot.child("listaCalas").getValue(MutableList<ClaseCala>::class.java)



                    val listaEstratosSnapshot = snapshot.child("listaEstratos")
                    val listaEstratos: MutableList<ClaseEstratos> = mutableListOf()
                    val listaImagenes: MutableList<String> = mutableListOf()


                    for (snapshot in listaEstratosSnapshot.children) {

                        val idEstrato = snapshot.child("idEstrato").getValue(Int::class.java)
                        val tipo_muestreo = snapshot.child("tipo_muestreo").getValue(String::class.java)
                        val profundidad_inicio = snapshot.child("profundidad_inicio").getValue(Double::class.java)
                        val profundidad_final = snapshot.child("profundidad_final").getValue(Double::class.java)
                        val profundidad_muestreo= snapshot.child("profundidad_muestreo").getValue(Double::class.java)
                        val clasificacion_visual = snapshot.child("clasificacion_visual").getValue(String::class.java)
                        val observaciones = snapshot.child("observaciones").getValue(String::class.java)
//                        val nombre = snapshot.child("nombre").getValue(String::class.java)

//                        // Asegúrate de ajustar los nombres de los campos según tu modelo ClaseCala
//                        val estacion = calaSnapshot.child("estacion").getValue(String::class.java)
//                        val humedad = calaSnapshot.child("humedad").getValue(Double::class.java)
//                        val estrato = calaSnapshot.child("cala").getValue(Int::class.java)
//                        val mvsl = calaSnapshot.child("mvsl").getValue(Double::class.java)
//                        val porcentaje =
//                            calaSnapshot.child("porcentaje").getValue(Double::class.java)
//                        val prof =
//                            calaSnapshot.child("prof").getValue(Double::class.java)
                        // Crea un objeto ClaseCala y agrégalo a la lista
                        val Estrato = ClaseEstratos(
                            idEstrato!!,
                            tipo_muestreo!!,
                            profundidad_inicio!!,
                            profundidad_final!!,
                            profundidad_muestreo!!,
                            clasificacion_visual!!,
                            observaciones!!,
                        )
                        listaEstratos.add(Estrato)
                    }


                    val procedencia = snapshot.child("procedencia").getValue(String::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val subTramo = snapshot.child("subTramo").getValue(String::class.java)
                    val tramo = snapshot.child("tramo").getValue(String::class.java)
                    val lugarMuestreo = snapshot.child("lugarMuestreo").getValue(String::class.java)
                    val latitud = snapshot.child("latitud").getValue(String::class.java)
                    val longitud = snapshot.child("longitud").getValue(String::class.java)


                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {

                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
//                        val llaveReporte = snapshot.child("llave").getValue(String::class.java).toString()

                        val imagenesRef = FirebaseDatabase.getInstance().reference
                            .child("ImagenesMecanicas")
                            .child(personal)
                            .child(llave.toString())

                        imagenesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(imagenSnapshot: DataSnapshot) {
                                val listaImagenes: MutableList<String> = mutableListOf()
                                for (img in imagenSnapshot.children) {
                                    val url = img.getValue(String::class.java)
                                    if (!url.isNullOrBlank()) {
                                        listaImagenes.add(url)
                                    }
                                }

                                // Construye la clase cuando ya tienes las imágenes
                                val obra = ClaseObraMecanica(
                                    numReporte!!,
                                    obra1.toString(),
                                    cliente.toString(),
                                    localizacion.toString(),
                                    atencion.toString(),
                                    fecha.toString(),
                                    sondeo_num.toString(),
                                    ubicacion.toString(),
                                    naf!!,
                                    profundidad_muestreo.toString(),
                                    profundidad_naf.toString(),
                                    hora.toString(),
                                    llave.toString(),
                                    latitud.toString(),
                                    longitud.toString(),
                                    listaEstratos,
                                    listaImagenes
                                )

                                listaObrasmutableListOf.add(obra)


                                listaObrasmutableListOf.sortByDescending { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.fecha) }
                                ObraAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error al obtener imágenes: ${error.message}")
                            }
                        })

                    }

                }



                // Notifica al adaptador que los datos han cambiado
                ObraAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de base de datos, si es necesario
            }

        })
    }
    private fun cargarDatosFirebase(){
        ObraAdapter = ObraMecanicaAdapter(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) },
            onItemDelete = { position -> onItemDelete(position) },
            onVerReporteFallaMantenimientoGA = { position ->
                onVerReporteFallaMantenimientoGA(position, listaObrasmutableListOf)
            }
        )

        rvObrasMecanicas.layoutManager = LinearLayoutManager(this)
        rvObrasMecanicas.adapter = ObraAdapter

        storage= FirebaseStorage.getInstance()
        dataReference =
            FirebaseDatabase.getInstance().reference.child("Mecanicas").child("ReportesMecanicas").child(personal)
    }
}