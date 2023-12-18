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

    private fun initUI() {

        btnAcceder.setOnClickListener {
            acceder1(
                NombreUsuario.text.toString(),
                Password.text.toString()
            )
        }
    }

//    private fun abrirCalculo_Compactacion(NombreUsuario:String) {
//        val intent=Intent(this,Calculo_Compactacion::class.java )
//        intent.putExtra(NombreUsuarioCompanion,NombreUsuario)
//        startActivity(intent)
//    }

    private fun abrirCalculo_Compactacion() {
        val intent = Intent(this, SeleccionarActividad::class.java)
        NombreUsuarioCompanion = NombreUsuario.text.toString()
//        intent.putExtra(NombreUsuarioCompanion,NombreUsuario)
        startActivity(intent)
    }

    private fun initComponent() {
        btnAcceder = findViewById(R.id.btnAcceder)
        NombreUsuario = findViewById(R.id.etEmail)
        Password = findViewById(R.id.etPassword)

    }

    private fun acceder1(email: String, password: String) {
        if (isNetworkAvailable()) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")
                            consultar_datos.usuarioApp = email
                            val user = auth.currentUser
                            NombreUsuarioCompanion = NombreUsuario.text.toString()
                            Acceder()
//                    updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            showAlert()
//                    updateUI(null)
                        }
                    }
            } catch (e: Exception) {
            }
        } else {
            // Muestra un mensaje indicando que no hay conexión a Internet
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }


    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun Acceder() {

        val Acceder = Intent(this, SeleccionarActividad::class.java)
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