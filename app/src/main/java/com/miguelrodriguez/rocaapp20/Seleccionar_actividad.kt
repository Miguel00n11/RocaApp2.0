package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.miguelrodriguez.rocaapp20.CapacitacionesActivity
import com.miguelrodriguez.rocaapp20.acceso.Sesion
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import com.miguelrodriguez.rocaapp20.cilindros.ReporteCilindros
import com.miguelrodriguez.rocaapp20.mecanicas.ReportesMuestreoMaterial
import com.miguelrodriguez.rocaapp20.morteros.ReportesMorteros
import com.miguelrodriguez.rocaapp20.vigas.ReportesVigas

class Seleccionar_actividad : AppCompatActivity() {

    private lateinit var btnIrReportesCompactacion: CardView
    private lateinit var btnItReportesMorteros: CardView
    private lateinit var btnIrReportesCilindros: CardView
    private lateinit var btnIrReportesVigas: CardView
    private lateinit var btnIrReportesMecanicas: CardView
    private lateinit var navCapacitaciones: View
    private lateinit var navEquiposPredeterminados: View
    private lateinit var navAdministrarPersonal: View
    private lateinit var navInstructivos: View
    private lateinit var navCambiarUsuario: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_actividad)
        // Sin sesión se regresa al login; no se debe seguir configurando la pantalla
        if (!InitComponet()) return
        InitUI()
        mostrarPanelSesion()
        mostrarBannerImpersonacion()
    }

    // Panel lateral con los datos de la sesión; se abre al tocar la barra superior o deslizando desde la izquierda
    private fun mostrarPanelSesion() {
        val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
        val nombre = MainActivity.NombreUsuarioCompanion
        val iniciales = nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

        findViewById<TextView>(R.id.txtInicialesBarra).text = iniciales
        findViewById<TextView>(R.id.txtInicialesSesion).text = iniciales
        findViewById<TextView>(R.id.txtNombreSesion).text = nombre
        mostrarSiHayTexto(findViewById(R.id.txtPuestoSesion), consultar_datos.puestoUsuario)
        mostrarSiHayTexto(findViewById(R.id.txtCorreoSesion), consultar_datos.usuarioApp)
        findViewById<TextView>(R.id.txtAdminSesion).visibility =
            if (consultar_datos.esAdministrador) View.VISIBLE else View.GONE

        findViewById<View>(R.id.barraSesion).setOnClickListener { drawer.openDrawer(GravityCompat.START) }
        findViewById<View>(R.id.btnCerrarSesion).setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            confirmarCierreSesion()
        }

        // Con el panel abierto, "atrás" lo cierra en lugar de salir del menú
        val cerrarPanel = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() = drawer.closeDrawer(GravityCompat.START)
        }
        onBackPressedDispatcher.addCallback(this, cerrarPanel)
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) { cerrarPanel.isEnabled = true }
            override fun onDrawerClosed(drawerView: View) { cerrarPanel.isEnabled = false }
        })
    }

    private fun mostrarBannerImpersonacion() {
        val banner = findViewById<View>(R.id.bannerImpersonacion)
        if (!Sesion.modoImpersonacion) {
            banner.visibility = View.GONE
            return
        }
        banner.visibility = View.VISIBLE
        val nombre = MainActivity.NombreUsuarioCompanion
        banner.findViewById<TextView>(R.id.txtBannerImpersonacion).text =
            "Viendo como: $nombre"
        banner.findViewById<View>(R.id.btnSalirImpersonacion).setOnClickListener {
            restaurarSesionAdmin()
        }
    }

    private fun mostrarSelectorDeUsuario() {
        val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
            .reference.child(com.miguelrodriguez.rocaapp20.acceso.CatalogoPersonal.NODO_PERSONAL)
        ref.get().addOnSuccessListener { snapshot ->
            data class UsuarioItem(val nombre: String, val correo: String, val puesto: String, val esAdmin: Boolean)
            val usuarios = snapshot.children.mapNotNull { child ->
                val correo = child.child("correo").getValue(String::class.java) ?: return@mapNotNull null
                val nombre = child.child("nombre").getValue(String::class.java) ?: return@mapNotNull null
                val puesto = child.child("puesto").getValue(String::class.java) ?: ""
                val activo = child.child("activo").getValue(Boolean::class.java) != false
                val esAdmin = child.child("rol").getValue(String::class.java) == com.miguelrodriguez.rocaapp20.acceso.CatalogoPersonal.ROL_ADMIN
                if (!activo) return@mapNotNull null
                if (correo.trim().lowercase() == consultar_datos.usuarioApp?.trim()?.lowercase()) return@mapNotNull null
                UsuarioItem(nombre, correo, puesto, esAdmin)
            }.sortedBy { it.nombre }

            if (usuarios.isEmpty()) {
                Toast.makeText(this, "No hay otros usuarios activos registrados", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val vistaDialog = layoutInflater.inflate(R.layout.dialog_seleccionar_usuario, null)
            val lista = vistaDialog.findViewById<android.widget.LinearLayout>(R.id.listaUsuariosDialog)
            val inflater = layoutInflater

            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(vistaDialog)
                .setNegativeButton("Cancelar", null)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            for (usuario in usuarios) {
                val tarjeta = inflater.inflate(R.layout.item_personal, lista, false)
                val iniciales = usuario.nombre.split(" ").filter { it.isNotBlank() }
                    .take(2).joinToString("") { it.first().uppercase() }
                tarjeta.findViewById<TextView>(R.id.txtInicialesPersonal).text = iniciales
                tarjeta.findViewById<TextView>(R.id.txtNombreItemPersonal).text = usuario.nombre
                tarjeta.findViewById<TextView>(R.id.txtPuestoItemPersonal).text = usuario.puesto
                tarjeta.findViewById<TextView>(R.id.txtCorreoItemPersonal).text = usuario.correo
                tarjeta.findViewById<View>(R.id.txtRolItemPersonal).visibility =
                    if (usuario.esAdmin) View.VISIBLE else View.GONE
                tarjeta.setOnClickListener {
                    dialog.dismiss()
                    entrarComoUsuario(usuario.nombre, usuario.correo, usuario.puesto)
                }
                lista.addView(tarjeta)
            }

            dialog.show()
        }.addOnFailureListener {
            Toast.makeText(this, "No se pudo cargar la lista de usuarios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun entrarComoUsuario(nombre: String, correo: String, puesto: String) {
        Sesion.correoAdminOriginal = consultar_datos.usuarioApp
        Sesion.nombreAdminOriginal = MainActivity.NombreUsuarioCompanion
        Sesion.puestoAdminOriginal = consultar_datos.puestoUsuario

        Sesion.correo = correo
        Sesion.nombre = nombre
        Sesion.puesto = puesto
        Sesion.esAdministrador = false
        consultar_datos.esAdministrador = false
        MainActivity.NombreUsuarioCompanion = nombre

        recreate()
    }

    private fun restaurarSesionAdmin() {
        val correoAdmin = Sesion.correoAdminOriginal ?: return
        val nombreAdmin = Sesion.nombreAdminOriginal ?: return

        Sesion.correo = correoAdmin
        Sesion.nombre = nombreAdmin
        Sesion.puesto = Sesion.puestoAdminOriginal
        Sesion.esAdministrador = true
        consultar_datos.esAdministrador = true
        MainActivity.NombreUsuarioCompanion = nombreAdmin

        Sesion.correoAdminOriginal = null
        Sesion.nombreAdminOriginal = null
        Sesion.puestoAdminOriginal = null

        recreate()
    }

    private fun mostrarSiHayTexto(vista: TextView, texto: String?) {
        vista.text = texto ?: ""
        vista.visibility = if (texto.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Quieres cerrar la sesión de ${MainActivity.NombreUsuarioCompanion}?")
            .setPositiveButton("Cerrar sesión") { _, _ -> cerrarSesion() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        Sesion.cerrar()
        val login = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(login)
        finish()
    }

    private fun InitUI() {
        btnIrReportesCompactacion.setOnClickListener {
            val intent=Intent(this,ReportesCompactaciones::class.java)
            startActivity(intent)

        }
        btnItReportesMorteros.setOnClickListener {
            Toast.makeText(this, "Aún no disponible", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        btnIrReportesCilindros.setOnClickListener {
            val intent=Intent(this,ReporteCilindros::class.java)
            startActivity(intent)
        }
        btnIrReportesVigas.setOnClickListener {
            val intent=Intent(this,ReportesVigas::class.java)

            startActivity(intent)
        }
        btnIrReportesMecanicas.setOnClickListener {
            val intent=Intent(this,ReportesMuestreoMaterial::class.java)

            startActivity(intent)
        }
        navCapacitaciones.setOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
            drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CapacitacionesActivity::class.java))
        }
        navEquiposPredeterminados.setOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
            drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, EquiposPredeterminadosActivity::class.java))
        }
        navAdministrarPersonal.visibility =
            if (consultar_datos.esAdministrador) View.VISIBLE else View.GONE
        navAdministrarPersonal.setOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
            drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, AdministrarPersonalActivity::class.java))
        }
        navInstructivos.setOnClickListener {
            val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
            drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, InstructivosActivity::class.java))
        }

        if (Sesion.modoImpersonacion) {
            navCambiarUsuario.visibility = View.VISIBLE
            navCambiarUsuario.findViewById<android.widget.TextView>(R.id.txtCambiarUsuario).text = "Volver a mi cuenta"
            navCambiarUsuario.setOnClickListener {
                val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
                drawer.closeDrawer(GravityCompat.START)
                restaurarSesionAdmin()
            }
        } else if (consultar_datos.esAdministrador) {
            navCambiarUsuario.visibility = View.VISIBLE
            navCambiarUsuario.setOnClickListener {
                val drawer = findViewById<DrawerLayout>(R.id.drawerSeleccionar)
                drawer.closeDrawer(GravityCompat.START)
                mostrarSelectorDeUsuario()
            }
        } else {
            navCambiarUsuario.visibility = View.GONE
        }
    }

    // Devuelve false si no hay sesión (en ese caso ya se redirigió al login)
    private fun InitComponet(): Boolean {
        // Validar que el usuario esté correctamente autenticado
        if (MainActivity.NombreUsuarioCompanion == Sesion.NOMBRE_SIN_SESION) {
            // RocaApplication ya redirige al login cuando no hay sesión; aquí solo se evita seguir
            if (!isFinishing) {
                Toast.makeText(this, "Error: Usuario no autenticado. Por favor inicia sesión.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            return false
        }

        btnIrReportesCompactacion=findViewById(R.id.btnIrReportesCompactacion)
        btnItReportesMorteros=findViewById(R.id.btnItReportesMorteros)
        btnIrReportesCilindros=findViewById(R.id.btnIrReportesCilindros)
        btnIrReportesVigas=findViewById(R.id.btnIrReportesVigas)
        btnIrReportesMecanicas=findViewById(R.id.btnIrReportesMecanicas)
        navCapacitaciones = findViewById(R.id.navCapacitaciones)
        navEquiposPredeterminados = findViewById(R.id.navEquiposPredeterminados)
        navAdministrarPersonal = findViewById(R.id.navAdministrarPersonal)
        navInstructivos = findViewById(R.id.navInstructivos)
        navCambiarUsuario = findViewById(R.id.navCambiarUsuario)
        return true
    }
}