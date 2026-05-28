package com.miguelrodriguez.rocaapp20

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var btnAcceder: Button
    private lateinit var btnAccederInvitado: Button
    private lateinit var NombreUsuario: EditText
    private lateinit var Password: EditText
    private lateinit var auth: FirebaseAuth


    companion object {
        var NombreUsuarioCompanion = "NombreUsuario"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = Firebase.auth

        initComponent()

        initUI()


    }

    private fun obtenerNombreUsuarioDesdeCorreo(correo: String): String {
        val indiceArroba = correo.indexOf('@')
        return if (indiceArroba != -1) {
            var nombreUsuario = correo.substring(0, indiceArroba)

            when(nombreUsuario){
                "adrian"->nombreUsuario="José Adrían Cortés Martínez"
                "carlos"->nombreUsuario="Carlos Alfonso Torres Cervantes"
                "calixto"->nombreUsuario="José Luis Calixto Ramírez"
                "miguel00n11"->nombreUsuario="Jesús Miguel Rodríguez Ortega"
            }
//            when (adrian||Adrian)
            NombreUsuarioCompanion =
                nombreUsuario // Asigna el nombre de usuario a la variable global
            nombreUsuario
        } else {
            correo // Devuelve el correo completo si no se encuentra el símbolo "@"
        }
    }

    private fun initUI() {

        btnAcceder.setOnClickListener {
            val email = NombreUsuario.text.toString().lowercase()
            val password = Password.text.toString()

            // Validar que no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            acceder(email, password)
        }
        btnAccederInvitado.setOnClickListener { Acceder() }
    }

//    private fun abrirCalculo_Compactacion(NombreUsuario:String) {
//        val intent=Intent(this,Calculo_Compactacion::class.java )
//        intent.putExtra(NombreUsuarioCompanion,NombreUsuario)
//        startActivity(intent)
//    }

    private fun initComponent() {
        btnAcceder = findViewById(R.id.btnAcceder)
        btnAccederInvitado = findViewById(R.id.btnAccederInvitado)
        NombreUsuario = findViewById(R.id.etEmail)
        Password = findViewById(R.id.etPassword)

    }

    private fun acceder(email: String, password: String) {
        if (isNetworkAvailable()) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success - SOLO AHORA asignamos el nombre de usuario
                            Log.d("TAG", "createUserWithEmail:success")
                            consultar_datos.usuarioApp = email
                            val user = auth.currentUser

                            // Obtener y asignar el nombre de usuario DESPUÉS de autenticación exitosa
                            obtenerNombreUsuarioDesdeCorreo(email)

                            // Validar que se asignó correctamente
                            if (NombreUsuarioCompanion != "NombreUsuario") {
                                Acceder()
                            } else {
                                // Si falla la obtención del nombre, mostrar error
                                Toast.makeText(
                                    baseContext, "Error al obtener nombre de usuario.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showAlert()
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Autenticación fallida.",
                                Toast.LENGTH_SHORT
                            ).show()
                            showAlert()
                            // Asegurar que el nombre de usuario se resetee en caso de fallo
                            NombreUsuarioCompanion = "NombreUsuario"
                        }
                    }
            } catch (e: Exception) {
                Log.e("TAG", "Error en login: ${e.message}")
                Toast.makeText(
                    baseContext, "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                NombreUsuarioCompanion = "NombreUsuario"
            }
        } else {
            // Muestra un mensaje indicando que no hay conexión a Internet
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }


    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun Acceder() {

        val Acceder = Intent(this, Seleccionar_actividad::class.java)
        consultar_datos.modoInvitado = false
//        Registrarse.putExtra(TAG,"K")
//            putExtra("Provider",provider.name)
        startActivity(Acceder)

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error de autentificación")
        builder.setMessage("Favor de ingresar la contraseña coreccta.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()


    }
}