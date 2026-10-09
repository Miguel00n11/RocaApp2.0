package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EquiposPredeterminadosActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var personal: String
    private var equiposListener: ValueEventListener? = null

    private lateinit var etCarretilla: EditText
    private lateinit var etCono: EditText
    private lateinit var etVarilla: EditText
    private lateinit var etMazo: EditText
    private lateinit var etTermometro: EditText
    private lateinit var etCucharon: EditText
    private lateinit var etPlaca: EditText
    private lateinit var etFlexometro: EditText
    private lateinit var etEnrasador: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnAtras: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipos_predeterminados)

        personal = MainActivity.NombreUsuarioCompanion
        dbRef = FirebaseDatabase.getInstance().reference
            .child("personal").child("inventario").child(personal)

        etCarretilla  = findViewById(R.id.etCarretillaEquipos)
        etCono        = findViewById(R.id.etConoEquipos)
        etVarilla     = findViewById(R.id.etVarillaEquipos)
        etMazo        = findViewById(R.id.etMazoEquipos)
        etTermometro  = findViewById(R.id.etTermometroEquipos)
        etCucharon    = findViewById(R.id.etCucharonEquipos)
        etPlaca       = findViewById(R.id.etPlacaEquipos)
        etFlexometro  = findViewById(R.id.etFlexometroEquipos)
        etEnrasador   = findViewById(R.id.etEnrasadorEquipos)
        btnGuardar    = findViewById(R.id.btnGuardarEquipos)
        btnAtras      = findViewById(R.id.btnAtrasEquipos)

        btnAtras.setOnClickListener { finish() }
        btnGuardar.setOnClickListener { guardarEquipos() }

        escucharEquipos()
    }

    private fun escucharEquipos() {
        equiposListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                setIfNotFocused(etCarretilla, snapshot.child("carretilla").getValue(Any::class.java)?.toString())
                setIfNotFocused(etCono,       snapshot.child("cono").getValue(Any::class.java)?.toString())
                setIfNotFocused(etVarilla,    snapshot.child("varilla").getValue(Any::class.java)?.toString())
                setIfNotFocused(etMazo,       snapshot.child("mazo").getValue(Any::class.java)?.toString())
                setIfNotFocused(etTermometro, snapshot.child("termometro").getValue(Any::class.java)?.toString())
                setIfNotFocused(etCucharon,   snapshot.child("cucharon").getValue(Any::class.java)?.toString())
                setIfNotFocused(etPlaca,      snapshot.child("placa").getValue(Any::class.java)?.toString())
                setIfNotFocused(etFlexometro, snapshot.child("flexometro").getValue(Any::class.java)?.toString())
                setIfNotFocused(etEnrasador,  snapshot.child("enrasador").getValue(Any::class.java)?.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Equipos", "Error al escuchar equipos", error.toException())
                Toast.makeText(this@EquiposPredeterminadosActivity, "Error al cargar equipos", Toast.LENGTH_SHORT).show()
            }
        }
        dbRef.addValueEventListener(equiposListener!!)
    }

    private fun setIfNotFocused(field: EditText, value: String?) {
        if (!field.isFocused) {
            field.setText(value ?: "")
        }
    }

    private fun guardarEquipos() {
        val datos = mapOf(
            "carretilla"  to (etCarretilla.text.toString().toIntOrNull() ?: 0),
            "cono"        to (etCono.text.toString().toIntOrNull() ?: 0),
            "varilla"     to (etVarilla.text.toString().toIntOrNull() ?: 0),
            "mazo"        to (etMazo.text.toString().toIntOrNull() ?: 0),
            "termometro"  to (etTermometro.text.toString().toIntOrNull() ?: 0),
            "cucharon"    to (etCucharon.text.toString().toIntOrNull() ?: 0),
            "placa"       to (etPlaca.text.toString().toIntOrNull() ?: 0),
            "flexometro"  to (etFlexometro.text.toString().toIntOrNull() ?: 0),
            "enrasador"   to (etEnrasador.text.toString().toIntOrNull() ?: 0)
        )

        dbRef.updateChildren(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Equipos guardados correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Equipos", "Error al guardar", e)
                Toast.makeText(this, "Error al guardar los equipos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        equiposListener?.let { dbRef.removeEventListener(it) }
    }
}
