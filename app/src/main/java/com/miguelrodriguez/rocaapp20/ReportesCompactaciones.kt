package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Join
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import java.io.File

import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.itextpdf.io.font.otf.GlyphLine
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.properties.HorizontalAlignment
//import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.*
import com.itextpdf.layout.properties.TextAlignment
import kotlinx.coroutines.joinAll
import java.io.ByteArrayOutputStream

import java.io.FileOutputStream

import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Rgb
import com.itextpdf.layout.element.Text


class ReportesCompactaciones : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var dataReferenceCampo: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String
    private lateinit var cala1: MutableList<ClaseCala>

    companion object {
        lateinit var reporteSelecionado: ClaseObra
        var editar: Boolean = false
    }


    private lateinit var btnRegistroCompactacion: Button
    private lateinit var rvObrasCompactacion: RecyclerView
    private lateinit var ObraAdapter: ObraAdapter
    private lateinit var claseObra: ClaseObra
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObra>
    private lateinit var listacalasmutableListOf: MutableList<ClaseCala>

//    private val listaObrasmutableListOf =
//        mutableListOf(ClaseObra(1, "estacion","1","1","1",
//            "1","1","1","1","1",))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_compactaciones)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
//        // Opcional: Configura la persistencia si es necesario
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        listacalasmutableListOf = mutableListOf(ClaseCala(1, "1", 1.1, 1.1, 1.1, 1.1))

        listaObrasmutableListOf = mutableListOf(
        )

        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion



        reporteSelecionado = ClaseObra(
            1, false, "estacion", "a", "a", "atencion", "1", "1", "obs", "1",
            "1", "1", "1", "1", "1", "hola", listacalasmutableListOf
        )

        initComponent()
        initUI()


    }


    private fun updateTask() {
        ObraAdapter.notifyDataSetChanged()
    }

    private fun initUI() {
        // Referencia a la base de datos de Firebase
        dataReference =
            FirebaseDatabase.getInstance().reference.child("Compactaciones").child("Reportes")
                .child(personal)
        dataReferenceCampo =
            FirebaseDatabase.getInstance().reference.child("Compactaciones").child("Respaldo")
                .child(personal)

        btnRegistroCompactacion.setOnClickListener {
            val intent = Intent(this, RegistroCompactaciones::class.java)
            startActivity(intent)
        }

        ObraAdapter = ObraAdapter(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) },
            onItemDelete = { position -> onItemDelete(position) },
            onVerReporteCompactacion = { position -> onVerReporteCompactacion(position, this) }
        )



        rvObrasCompactacion.layoutManager = LinearLayoutManager(this)
        rvObrasCompactacion.adapter = ObraAdapter

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                val personalDeseado =
                    "miguel" // Reemplaza con el nombre del personal que deseas filtrar

                for (snapshot in dataSnapshot.children) {

                    val validado = snapshot.child("validado").getValue(Boolean::class.java)
                    if (validado == true) {
                        continue
                    }

                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)
                    val localizacion = snapshot.child("localizacion").getValue(String::class.java)
                    val atencion = snapshot.child("atencion").getValue(String::class.java)
                    val capa = snapshot.child("capa").getValue(String::class.java)
                    val observaciones = snapshot.child("observaciones").getValue(String::class.java)
                    val compactacion = snapshot.child("compactacion").getValue(Int::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
                    val humedad = snapshot.child("humedad").getValue(Int::class.java)
                    var llave = snapshot.child("llave").getValue(String::class.java)
//                    val listaCalas = snapshot.child("listaCalas").getValue(MutableList<ClaseCala>::class.java)

                    val listaCalasSnapshot = snapshot.child("listaCalas")
                    val listaCalas: MutableList<ClaseCala> = mutableListOf()

                    for (calaSnapshot in listaCalasSnapshot.children) {

                        // Asegúrate de ajustar los nombres de los campos según tu modelo ClaseCala
                        val estacion = calaSnapshot.child("estacion").getValue(String::class.java)
                        val humedad = calaSnapshot.child("humedad").getValue(Double::class.java)
                        val cala = calaSnapshot.child("cala").getValue(Int::class.java)
                        val mvsl = calaSnapshot.child("mvsl").getValue(Double::class.java)
                        val porcentaje =
                            calaSnapshot.child("porcentaje").getValue(Double::class.java)
                        val prof =
                            calaSnapshot.child("prof").getValue(Double::class.java)
                        // Crea un objeto ClaseCala y agrégalo a la lista
                        val cala1 = ClaseCala(
                            cala!!,
                            estacion!!,
                            prof!!,
                            mvsl!!,
                            humedad!!,
                            porcentaje!!
                        )
                        listaCalas.add(cala1)
                    }


                    val mvsm = snapshot.child("mvsm").getValue(Int::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val subTramo = snapshot.child("subTramo").getValue(String::class.java)
                    val tramo = snapshot.child("tramo").getValue(String::class.java)

                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {
                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
                        val obra = ClaseObra(
                            numReporte!!,
                            validado!!,
                            obra1.toString(),
                            cliente.toString(),
                            localizacion.toString(),
                            atencion.toString(),
                            numReporte.toString(),
                            capa.toString(),
                            observaciones.toString(),
                            fecha.toString(),
                            tramo.toString(),
                            subTramo.toString(),
                            compactacion.toString(),
                            mvsm.toString(),
                            humedad.toString(),
                            llave.toString(),

                            listaCalas


                        ) // Asegúrate de ajustar los parámetros según tu clase
                        listaObrasmutableListOf.add(obra)
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

    private fun onItemDelete(position: Int) {
        if (isNetworkAvailable()) {
            val reportKey =
                listaObrasmutableListOf[position].llave // Utiliza la clave única del informe
            val reportReferenceCampo =
                listaObrasmutableListOf[position].llave // Utiliza la clave única del informe

            // Elimina el informe de la base de datos Firebase
            deleteReport(reportKey)
            deleteReport(reportReferenceCampo)

            // Elimina el informe de la lista local
            listaObrasmutableListOf.removeAt(position)

            // Notifica al adaptador que los datos han cambiado
            updateTask()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun onVerReporteCompactacion(position: Int, context: Context) = try {
        Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()

        reporteSelecionado = listaObrasmutableListOf[position]


        // Datos de varios registros (solo como ejemplo)
        val registros = listOf(
            arrayOf("Cliente:", reporteSelecionado.Cliente),
            arrayOf("Obra:", reporteSelecionado.Obra),
            arrayOf("Loc.:", reporteSelecionado.localizacion)
        )


        // Directorio para guardar el archivo PDF en el almacenamiento externo
        val directorio = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val archivoPDF = File(directorio, "registro_compactacion.pdf")
        val alturaTexto = 8f

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
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

            // Convertir el bitmap en un objeto Image de iText
            val outputStream1 = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream1)
            val imageData = ImageDataFactory.create(outputStream1.toByteArray())
            val image = Image(imageData)
            image.scale(.05f, .05f)

            // Agregar celda con imagen
            val cellImage = Cell(4, 1)
            cellImage.add(image.setHorizontalAlignment(HorizontalAlignment.CENTER))


            // Crear una tabla
            val table = Table(floatArrayOf(60f, anchoColiumna, 150f, 110f))

            table.addCell(cellImage)

//            val tableTituloDatosdeObraDeControl = Table(floatArrayOf(anchoDocumento+72f))
            // Crear tabla para titulo de datos de obra
            val tableTituloDatosdeObraDeControl = Cell(1, 3).add(Paragraph("Datos de control"))

            tableTituloDatosdeObraDeControl.setTextAlignment(TextAlignment.CENTER)
            tableTituloDatosdeObraDeControl.setBackgroundColor(DeviceRgb(192, 192, 192))


            table.addCell(tableTituloDatosdeObraDeControl)


            val textoNombreRegistro = Cell(2, 1)
            val paragraph = Paragraph()


// Agrega la primera parte del texto con color negro
            val parte1 = Text("Reporte de campo de compactación de terracería. ").setFontColor(DeviceRgb.BLACK)
            paragraph.add(parte1)

// Agrega la segunda parte del texto con color rojo
            val parte2 = Text("PRELIMINAR.").setFontColor(DeviceRgb.RED)
            paragraph.add(parte2)

            textoNombreRegistro.add(paragraph)
            textoNombreRegistro.setTextAlignment(TextAlignment.CENTER)
            textoNombreRegistro.setKeepTogether(true)
            table.addCell(textoNombreRegistro)


            var textoFechaRegistro = Cell(1, 1)
            textoFechaRegistro.add(Paragraph("Fecha:"))
            textoFechaRegistro.setTextAlignment(TextAlignment.CENTER)
            textoFechaRegistro.setFontSize(alturaTexto)
            table.addCell(textoFechaRegistro)

            textoFechaRegistro = Cell(1, 1)
            textoFechaRegistro.add(Paragraph(reporteSelecionado.fecha))
            textoFechaRegistro.setTextAlignment(TextAlignment.CENTER)
            textoFechaRegistro.setFontSize(alturaTexto)
            table.addCell(textoFechaRegistro)

            var textoNumEnsayeRegistro = Cell(1, 1)
            textoNumEnsayeRegistro.add(Paragraph("Núm. de Ensaye:"))
            textoNumEnsayeRegistro.setTextAlignment(TextAlignment.CENTER)
            textoNumEnsayeRegistro.setFontSize(alturaTexto)
            table.addCell(textoNumEnsayeRegistro)

            textoNumEnsayeRegistro = Cell(1, 1)
            textoNumEnsayeRegistro.add(Paragraph(""))
            textoNumEnsayeRegistro.setTextAlignment(TextAlignment.CENTER)
            textoNumEnsayeRegistro.setFontSize(alturaTexto)
            table.addCell(textoNumEnsayeRegistro)

            var textoCodigoRegistro = Cell(1, 1)
            textoCodigoRegistro.add(Paragraph("Código: FO-L-02"))
            textoCodigoRegistro.setTextAlignment(TextAlignment.CENTER)
            textoCodigoRegistro.setFontSize(alturaTexto)
            table.addCell(textoCodigoRegistro)


            var textoNumReporteRegistro = Cell(1, 1)
            textoNumReporteRegistro.add(Paragraph("Núm. de Reporte:"))
            textoNumReporteRegistro.setTextAlignment(TextAlignment.CENTER)
            textoNumReporteRegistro.setFontSize(alturaTexto)
            table.addCell(textoNumReporteRegistro)

            textoNumReporteRegistro = Cell(1, 1)
            textoNumReporteRegistro.add(Paragraph(""))
            textoNumReporteRegistro.setTextAlignment(TextAlignment.CENTER)
            textoNumReporteRegistro.setFontSize(alturaTexto)
            table.addCell(textoNumReporteRegistro)

            document.add(table)


            val tablaDatosDeObra = Table(floatArrayOf(72f, anchoColiumna, 100f, 100f))


            val tableTituloDatosdeObra = Cell(1, 4).add(Paragraph("Datos de obra"))
            tableTituloDatosdeObra.setTextAlignment(TextAlignment.CENTER)
            tableTituloDatosdeObra.setBackgroundColor(DeviceRgb(192, 192, 192))
            tablaDatosDeObra.addCell(tableTituloDatosdeObra)

            var tableCliente = Cell(1, 1)
            tableCliente.add(Paragraph("Cliente:"))
            tableCliente.setFontSize(alturaTexto)
            tablaDatosDeObra.addCell(tableCliente)

            tableCliente = Cell(1, 3)
            tableCliente.setFontSize(alturaTexto)
            tableCliente.add(Paragraph(reporteSelecionado.Cliente))
            tablaDatosDeObra.addCell(tableCliente)

            var tablaObra = Cell(1, 1)
            tablaObra.add(Paragraph("Obra:"))
            tablaObra.setFontSize(alturaTexto)
            tablaDatosDeObra.addCell(tablaObra)

            tablaObra = Cell(1, 3)
            tablaObra.setFontSize(alturaTexto)
            tablaObra.add(Paragraph(reporteSelecionado.Obra))
            tablaDatosDeObra.addCell(tablaObra)

            var tablaLocalizacion = Cell(1, 1)
            tablaLocalizacion.add(Paragraph("Loc.:"))
            tablaLocalizacion.setFontSize(alturaTexto)
            tablaDatosDeObra.addCell(tablaLocalizacion)

            tablaLocalizacion = Cell(1, 3)
            tablaLocalizacion.setFontSize(alturaTexto)
            tablaLocalizacion.add(Paragraph(reporteSelecionado.localizacion))
            tablaDatosDeObra.addCell(tablaLocalizacion)

            var tablaAtencion = Cell(1, 1)
            tablaAtencion.add(Paragraph("At'n:"))
            tablaAtencion.setFontSize(alturaTexto)
            tablaDatosDeObra.addCell(tablaAtencion)

            tablaAtencion = Cell(1, 1)
            tablaAtencion.setFontSize(alturaTexto)
            tablaAtencion.add(Paragraph(reporteSelecionado.atencion))
            tablaDatosDeObra.addCell(tablaAtencion)

            var tablaExpediente = Cell(1, 1)
            tablaExpediente.add(Paragraph("Expediente:"))
            tablaExpediente.setFontSize(alturaTexto)
            tablaDatosDeObra.addCell(tablaExpediente)

            tablaExpediente = Cell(1, 1)
            tablaExpediente.setFontSize(alturaTexto)
            tablaExpediente.add(Paragraph(""))
            tablaDatosDeObra.addCell(tablaExpediente)


            document.add(tablaDatosDeObra)


            val tableTituloDatosdePrueba = Table(floatArrayOf(72f, anchoColiumna, 160f, 60f))


            val TituloDatosdePrueba = Cell(1, 4).add(Paragraph("Datos de prueba"))
            TituloDatosdePrueba.setTextAlignment(TextAlignment.CENTER)
            TituloDatosdePrueba.setBackgroundColor(DeviceRgb(192, 192, 192))
            tableTituloDatosdePrueba.addCell(TituloDatosdePrueba)

            var tableCapa = Cell(1, 1)
            tableCapa.add(Paragraph("Capa:"))
            tableCapa.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tableCapa)

            tableCapa = Cell(1, 1)
            tableCapa.setFontSize(alturaTexto)
            tableCapa.add(Paragraph(reporteSelecionado.capa))
            tableTituloDatosdePrueba.addCell(tableCapa)

            var tablaCompactacion = Cell(1, 1)
            tablaCompactacion.add(Paragraph("% De Compactación de Proy.:"))
            tablaCompactacion.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tablaCompactacion)

            tablaCompactacion = Cell(1, 1)
            tablaCompactacion.setFontSize(alturaTexto)
            tablaCompactacion.add(Paragraph(reporteSelecionado.compactacion))
            tableTituloDatosdePrueba.addCell(tablaCompactacion)

            var tablaTramo = Cell(1, 1)
            tablaTramo.add(Paragraph("Tramo:"))
            tablaTramo.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tablaTramo)

            tablaTramo = Cell(1, 1)
            tablaTramo.setFontSize(alturaTexto)
            tablaTramo.add(Paragraph(reporteSelecionado.tramo))
            tableTituloDatosdePrueba.addCell(tablaTramo)

            var tablaMVSM = Cell(1, 1)
            tablaMVSM.add(Paragraph("M.V.S.M (kgf/m³):"))
            tablaMVSM.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tablaMVSM)

            tablaMVSM = Cell(1, 1)
            tablaMVSM.setFontSize(alturaTexto)
            tablaMVSM.add(Paragraph(reporteSelecionado.mvsm))
            tableTituloDatosdePrueba.addCell(tablaMVSM)

            var tablaSubtramo = Cell(1, 1)
            tablaSubtramo.add(Paragraph("Subtramo:"))
            tablaSubtramo.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tablaSubtramo)

            tablaSubtramo = Cell(1, 1)
            tablaSubtramo.setFontSize(alturaTexto)
            tablaSubtramo.add(Paragraph(reporteSelecionado.subtramo))
            tableTituloDatosdePrueba.addCell(tablaSubtramo)

            var tablaHumedadOptima = Cell(1, 1)
            tablaHumedadOptima.add(Paragraph("Humedad Óptima (%):"))
            tablaHumedadOptima.setFontSize(alturaTexto)
            tableTituloDatosdePrueba.addCell(tablaHumedadOptima)

            tablaHumedadOptima = Cell(1, 1)
            tablaHumedadOptima.setFontSize(alturaTexto)
            tablaHumedadOptima.add(Paragraph(reporteSelecionado.humedad))
            tableTituloDatosdePrueba.addCell(tablaHumedadOptima)


            document.add(tableTituloDatosdePrueba)


            val tableResultados = Table(floatArrayOf(90f, 130f, 130f, 130f, 130f, 130f, 130f, 130f))

            val TituloResultados = Cell(1, 8).add(Paragraph("Resultados obtenidos"))
            TituloResultados.setTextAlignment(TextAlignment.CENTER)
            TituloResultados.setBackgroundColor(DeviceRgb(192, 192, 192))
            tableResultados.addCell(TituloResultados)

            var columnaCala = Cell(2, 1)
            columnaCala.add(Paragraph("Núm de Cala"))
            columnaCala.setFontSize(alturaTexto)
            tableResultados.addCell(columnaCala)

            var columnaUbicacion = Cell(1, 2)
            columnaUbicacion.add(Paragraph("Ubicación"))
            columnaUbicacion.setFontSize(alturaTexto)
            tableResultados.addCell(columnaUbicacion)

            var columnaProfundidad = Cell(2, 1)
            columnaProfundidad.add(Paragraph("Prof. de sondeo (cm)"))
            columnaProfundidad.setFontSize(alturaTexto)
            tableResultados.addCell(columnaProfundidad)

            var columnaHumedad = Cell(2, 1)
            columnaHumedad.add(Paragraph("% de Humedad de Lugar"))
            columnaHumedad.setFontSize(alturaTexto)
            tableResultados.addCell(columnaHumedad)

            var columnaMVSM = Cell(2, 1)
            columnaMVSM.add(Paragraph("M.V.S. de lugar (kgf/m³)"))
            columnaMVSM.setFontSize(alturaTexto)
            tableResultados.addCell(columnaMVSM)

            var columnaCompactacion = Cell(2, 1)
            columnaCompactacion.add(Paragraph("% de Compactación"))
            columnaCompactacion.setFontSize(alturaTexto)
            tableResultados.addCell(columnaCompactacion)

            var columnaResultado = Cell(2, 1)
            columnaResultado.add(Paragraph("Resultado"))
            columnaResultado.setFontSize(alturaTexto)
            tableResultados.addCell(columnaResultado)


            var columnaEstacion = Cell(1, 1)
            columnaEstacion.add(Paragraph("Estación"))
            columnaEstacion.setFontSize(alturaTexto)
            tableResultados.addCell(columnaEstacion)

            var columnaLado = Cell(1, 1)
            columnaLado.add(Paragraph("Lado"))
            columnaLado.setFontSize(alturaTexto)
            tableResultados.addCell(columnaLado)


            tableResultados.setTextAlignment(TextAlignment.CENTER )

//            document.add(tableResultados)


//            val tablaCalas = Table(8)

            // Agregar los registros a la tabla
            reporteSelecionado.listaCalas.forEach { cala ->
                tableResultados.addCell(Cell().add(Paragraph("${cala.cala+1}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Paragraph("${cala.Estacion}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Cell())).setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Paragraph("${cala.prof}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Paragraph("${cala.Humedad}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Paragraph("${cala.MVSL}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Paragraph("${cala.Porcentaje}")))
                    .setFontSize(alturaTexto)
                tableResultados.addCell(Cell().add(Cell())).setFontSize(alturaTexto)
            }

            document.add(tableResultados)

            val tableCroquis = Table(floatArrayOf(anchoColiumna, anchoColiumna))

            val Croquis = Cell(1, 2).add(Paragraph("Croquis:"))
            Croquis.setHeight(100f)
            tableCroquis.addCell(Croquis)

            document.add(tableCroquis)

            val tableObservaciones = Table(floatArrayOf(60f, anchoColiumna))

            val observaciones = Cell(1, 1).add(Paragraph("Observaciones:"))
            tableObservaciones.addCell(observaciones)
            tableObservaciones.addCell(Cell())
//
            document.add(tableObservaciones)

//            // Agregar marca de agua en cada página
//            val pageSize = PageSize.A4
//            for (i in 1..pdf.numberOfPages) {
//                val page = pdf.getPage(i)
//                val canvas = PdfCanvas(page)
//                val font = PdfFontFactory.createFont("Helvetica")
//                val fontSize = 100f
//
//
//                canvas.setFontAndSize(font, fontSize)
//
//
//                canvas.setColor(ColorConstants.GRAY,true)
//
//
//                val colorRes =
//                    R.color.Texto_Registro_Preliminar // Reemplaza "my_color" con el nombre de tu color definido en colors.xml
//                val color = ContextCompat.getColor(context, colorRes)
////                val deviceColor = DeviceRgb(color.red, color.green, color.blue)
//
//                color.alpha
//                // Dibujar texto con el color seleccionado
////                canvas.setFillColor(deviceColor)
//
//
//                val text = "PRELIMINAR"
//                val x = (pageSize.width - font.getWidth(text) * fontSize) / 2
//                val y = (pageSize.height - font.getWidth(text) * fontSize) / 2
//                canvas.saveState()
//                canvas.beginText()
//
//// Girar texto 45 grados en sentido antihorario
//                val radianes = Math.toRadians(45.0)
//                val coseno = Math.cos(radianes).toFloat()
//                val seno = Math.sin(radianes).toFloat()
//                canvas.setTextMatrix(coseno, seno, -seno, coseno, 100f, 100f)
//
//
//
//                canvas.showText(text)
////                canvas.setTextMatrix(4.5f,.9f)
//                canvas.endText()
//                canvas.restoreState()
//            }

            document.close()

            println("Documento PDF creado correctamente.")

            // Abrir el documento PDF
            abrirPDF(context, archivoPDF)
            updateTask()
        } catch (ex: Exception) {
            Toast.makeText(context, "Error al generar el PDF: ${ex.message}", Toast.LENGTH_SHORT)
                .show()
        }

        // Abrir el documento PDF
        abrirPDF(context, archivoPDF)
    } catch (ex: Exception) {
        Toast.makeText(context, "Error al generar el PDF: ${ex.message}", Toast.LENGTH_SHORT).show()
    }

    private fun abrirPDF(context: Context, archivoPDF: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                archivoPDF
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(context, "Error al abrir el PDF: ${ex.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun deleteReport(reportKey: String) {
        val reportReference = dataReference.child(reportKey)

        reportReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Informe eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al eliminar el informe: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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


    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

//        listaCalasmutableListOf[position], position


        editar = true
        reporteSelecionado = listaObrasmutableListOf[position]
        val intent = Intent(this, RegistroCompactaciones::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }

    private fun initComponent() {
        btnRegistroCompactacion = findViewById(R.id.btnRegistroCompactacion)
        rvObrasCompactacion = findViewById(R.id.rvObrasCompactacion)
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->


        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }
}