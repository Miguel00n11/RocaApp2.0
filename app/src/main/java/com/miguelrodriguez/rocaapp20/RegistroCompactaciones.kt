package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Calendar



import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistroCompactaciones : AppCompatActivity() {

private lateinit var dataReference:DatabaseReference

    private  lateinit var etObra:EditText
    private  lateinit var etFecha:EditText
    private  lateinit var etCapa:EditText
    private  lateinit var etTramo:EditText

    private lateinit var tvNumeroReporteCompactacion:TextView

    private lateinit var btnCancelar:Button
    private lateinit var btnGuardar:Button
    private lateinit var btnVerCalendarioCompactaciones:Button

    private lateinit var personal:String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_compactaciones)

        initComponet()


        initUI()

        personal= MainActivity.NombreUsuarioCompanion
        ConsultarUltimoRegistro()




    }

    private fun ConsultarUltimoRegistro() {

// Obtén la referencia de la base de datos
        dataReference = FirebaseDatabase.getInstance().reference

        // Agrega un listener para contar el total de registros
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtiene el número total de registros
                tvNumeroReporteCompactacion.text = (dataSnapshot.child(personal).childrenCount+1).toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al leer la base de datos: ${databaseError.message}")
            }


        })

    }

    private fun initUI() {

        btnCancelar.setOnClickListener {onBackPressed()}
        btnGuardar.setOnClickListener {GuardarCompactacion(etObra.text.toString(),etFecha.text.toString(),etCapa.text.toString(),etTramo.text.toString(),personal)

            ConsultarUltimoRegistro()
        }

    }

    fun mostrarCalendario(view: View) {
        val calendario = Calendar.getInstance()
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Manejar la fecha seleccionada, por ejemplo, puedes mostrarla en el EditText
                val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                etFecha.setText(fechaSeleccionada)
            },
            año,
            mes,
            dia
        )

        datePickerDialog.show()
    }

    private fun GuardarCompactacion(obra:String,fecha:String,capa:String,tramo:String,personal:String) {
        // Obtén la referencia de la base de datos
        dataReference=FirebaseDatabase.getInstance().reference

        // Crear un objeto con los datos que deseas almacenar
        val data = HashMap<String, Any>()
        data["Tramo"] = tramo
        data["Fecha"] = fecha
        data["Capa"] = capa

        // Generar un nuevo nodo único en la base de datos
        val newChildRef = dataReference.child(personal).child(tvNumeroReporteCompactacion.text.toString())
        newChildRef.setValue(data)

    }

    private fun initComponet() {
        etObra=findViewById(R.id.etObraCompactacion)
        etFecha=findViewById(R.id.etFechaCompactacion)
        etCapa=findViewById(R.id.etCapaCompactacion)
        etTramo=findViewById(R.id.etTramoCompactacion)

        tvNumeroReporteCompactacion=findViewById(R.id.tvNumeroReporteCompactacion)

        btnCancelar=findViewById(R.id.btnCancelarRegistroCompactacion)
        btnGuardar=findViewById(R.id.btnGuardarRegistroCompactacion)
        btnVerCalendarioCompactaciones=findViewById(R.id.btnVerCalendarioCompactaciones)



    }


}