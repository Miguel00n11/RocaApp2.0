package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.acceso.CatalogoPersonal
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdministrarPersonalActivity : AppCompatActivity() {

    private lateinit var txtTituloFormulario: TextView
    private lateinit var etNombre: MaterialAutoCompleteTextView
    private lateinit var etCorreo: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilPuesto: TextInputLayout
    private lateinit var etPuesto: MaterialAutoCompleteTextView
    private lateinit var chkAdministrador: CheckBox
    private lateinit var chkActivo: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var btnRestablecerPassword: Button
    private lateinit var btnCancelarEdicion: Button
    private lateinit var btnVerExamenesPersona: Button
    private lateinit var btnEntrarComoUsuario: Button
    private lateinit var progresoAlta: ProgressBar
    private lateinit var listaPersonal: LinearLayout
    private lateinit var txtSinPersonal: TextView
    private lateinit var nodoPersonal: DatabaseReference
    private lateinit var sectionPermisos: View
    private lateinit var separadorPermisos: View

    private val checkboxCapacitaciones: Map<String, CheckBox> by lazy {
        mapOf(
            "083" to findViewById(R.id.chkAcceso083),
            "109" to findViewById(R.id.chkAcceso109),
            "156" to findViewById(R.id.chkAcceso156),
            "159" to findViewById(R.id.chkAcceso159),
            "161" to findViewById(R.id.chkAcceso161),
            "17025" to findViewById(R.id.chkAcceso17025),
            "008" to findViewById(R.id.chkAcceso008)
        )
    }

    private val checkboxInstructivos: Map<String, CheckBox> by lazy {
        mapOf(
            "IT01" to findViewById(R.id.chkAccesoIT01),
            "IT02" to findViewById(R.id.chkAccesoIT02),
            "IT03" to findViewById(R.id.chkAccesoIT03),
            "IT04" to findViewById(R.id.chkAccesoIT04),
            "IT05" to findViewById(R.id.chkAccesoIT05)
        )
    }

    private data class Perfil(
        val clave: String,
        val nombre: String,
        val correo: String,
        val puesto: String,
        val esAdmin: Boolean,
        val activo: Boolean
    )

    private var personalRegistrado = listOf<Perfil>()
    // Perfil que se está editando; null = modo alta
    private var perfilEnEdicion: Perfil? = null

    // Instancia secundaria de Firebase: crear la cuenta ahí evita cerrar la sesión del administrador
    private val authAltas: FirebaseAuth by lazy {
        val nombreApp = "AltaPersonal"
        val app = FirebaseApp.getApps(this).firstOrNull { it.name == nombreApp }
            ?: FirebaseApp.initializeApp(this, FirebaseApp.getInstance().options, nombreApp)
        FirebaseAuth.getInstance(app)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!consultar_datos.esAdministrador) {
            Toast.makeText(this, "Solo un administrador puede dar de alta personal.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_administrar_personal)

        nodoPersonal = FirebaseDatabase.getInstance().reference.child(CatalogoPersonal.NODO_PERSONAL)
        txtTituloFormulario = findViewById(R.id.txtTituloFormulario)
        etNombre = findViewById(R.id.etNombrePersonal)
        etCorreo = findViewById(R.id.etCorreoPersonal)
        tilPassword = findViewById(R.id.tilPasswordPersonal)
        etPassword = findViewById(R.id.etPasswordPersonal)
        tilPuesto = findViewById(R.id.tilPuestoPersonal)
        etPuesto = findViewById(R.id.etPuestoPersonal)
        chkAdministrador = findViewById(R.id.chkAdministrador)
        chkActivo = findViewById(R.id.chkActivo)
        btnGuardar = findViewById(R.id.btnDarDeAlta)
        btnRestablecerPassword = findViewById(R.id.btnRestablecerPassword)
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion)
        btnVerExamenesPersona = findViewById(R.id.btnVerExamenesPersona)
        btnEntrarComoUsuario = findViewById(R.id.btnEntrarComoUsuario)
        progresoAlta = findViewById(R.id.progresoAlta)
        listaPersonal = findViewById(R.id.listaPersonal)
        txtSinPersonal = findViewById(R.id.txtSinPersonal)
        sectionPermisos = findViewById(R.id.sectionPermisos)
        separadorPermisos = findViewById(R.id.separadorPermisos)

        etPuesto.setSimpleItems(CatalogoPersonal.puestos.toTypedArray())
        actualizarSugerenciasNombre()

        // Al elegir a alguien del catálogo se sugiere su correo, si se conoce
        etNombre.setOnItemClickListener { _, _, _, _ ->
            val persona = personaDelCatalogo(etNombre.text.toString()) ?: return@setOnItemClickListener
            if (etCorreo.text.isNullOrBlank() && persona.correo != null) {
                etCorreo.setText(persona.correo)
            }
        }
        // El puesto de alguien del catálogo viene de la base de datos y no se puede cambiar
        etNombre.doAfterTextChanged { actualizarBloqueoPuesto() }

        btnGuardar.setOnClickListener {
            if (perfilEnEdicion == null) validarYDarDeAlta() else validarYGuardarEdicion()
        }
        btnRestablecerPassword.setOnClickListener { confirmarRestablecerPassword() }
        btnCancelarEdicion.setOnClickListener { salirDeEdicion() }
        findViewById<Button>(R.id.btnVerExamenesPersonal).setOnClickListener {
            startActivity(android.content.Intent(this, ExamenesPersonalActivity::class.java))
        }
        btnVerExamenesPersona.setOnClickListener {
            val perfil = perfilEnEdicion ?: return@setOnClickListener
            startActivity(
                android.content.Intent(this, ExamenesPersonalActivity::class.java)
                    .putExtra(ExamenesPersonalActivity.EXTRA_NOMBRE, perfil.nombre)
            )
        }
        btnEntrarComoUsuario.setOnClickListener {
            val perfil = perfilEnEdicion ?: return@setOnClickListener
            confirmarEntrarComoUsuario(perfil)
        }

        cargarPersonal()
    }

    // ---------- Alta ----------

    private fun validarYDarDeAlta() {
        val nombre = limpiarNombre(etNombre.text.toString())
        val correo = etCorreo.text.toString().trim().lowercase()
        val password = etPassword.text.toString()
        val puesto = personaDelCatalogo(nombre)?.puesto ?: etPuesto.text.toString().trim()

        when {
            nombre.isEmpty() -> { etNombre.error = "Escribe el nombre completo"; return }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> { etCorreo.error = "Correo no válido"; return }
            password.length < 6 -> { etPassword.error = "Mínimo 6 caracteres"; return }
            puesto.isEmpty() -> { etPuesto.error = "Selecciona el puesto"; return }
        }

        // No se permite registrar dos veces a la misma persona ni el mismo correo
        val porNombre = buscarPorNombre(nombre)
        if (porNombre != null) {
            avisarYaRegistrado(porNombre, "Ya hay una persona registrada con ese nombre.")
            return
        }
        val porCorreo = personalRegistrado.firstOrNull { it.clave == CatalogoPersonal.claveCorreo(correo) }
        if (porCorreo != null) {
            avisarYaRegistrado(porCorreo, "Ese correo ya está registrado a nombre de ${porCorreo.nombre}.")
            return
        }

        mostrarProgreso(true)
        authAltas.createUserWithEmailAndPassword(correo, password)
            .addOnSuccessListener { resultado ->
                val uid = resultado.user?.uid
                authAltas.signOut()
                guardarPerfilNuevo(nombre, correo, puesto, uid)
            }
            .addOnFailureListener { error ->
                mostrarProgreso(false)
                when (error) {
                    is FirebaseAuthUserCollisionException -> confirmarCuentaExistente(nombre, correo, puesto)
                    is FirebaseAuthWeakPasswordException -> etPassword.error = "La contraseña es muy débil"
                    is FirebaseAuthInvalidCredentialsException -> etCorreo.error = "Correo no válido"
                    else -> Toast.makeText(this, "No se pudo crear la cuenta: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun avisarYaRegistrado(perfil: Perfil, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Ya está registrado")
            .setMessage("$mensaje\n\nSi necesitas cambiar sus datos, edítalo.")
            .setPositiveButton("Editar") { _, _ -> entrarEnEdicion(perfil) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // El correo ya tiene cuenta en Firebase Auth (p. ej. personal que ya usaba la app): solo se registran sus datos
    private fun confirmarCuentaExistente(nombre: String, correo: String, puesto: String) {
        AlertDialog.Builder(this)
            .setTitle("El correo ya tiene cuenta")
            .setMessage("$correo ya puede entrar a la aplicación. ¿Quieres registrar su nombre, puesto y rol?\n\nSu contraseña actual no cambia.")
            .setPositiveButton("Registrar datos") { _, _ ->
                mostrarProgreso(true)
                guardarPerfilNuevo(nombre, correo, puesto, null)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarPerfilNuevo(nombre: String, correo: String, puesto: String, uid: String?) {
        val perfil = hashMapOf<String, Any>(
            "nombre" to nombre,
            "correo" to correo,
            "puesto" to puesto,
            "rol" to if (chkAdministrador.isChecked) CatalogoPersonal.ROL_ADMIN else CatalogoPersonal.ROL_USUARIO,
            "activo" to true,
            "altaPor" to MainActivity.NombreUsuarioCompanion,
            "fechaAlta" to ahora()
        )
        if (uid != null) perfil["uid"] = uid

        nodoPersonal.child(CatalogoPersonal.claveCorreo(correo))
            .updateChildren(perfil)
            .addOnSuccessListener {
                mostrarProgreso(false)
                Toast.makeText(this, "$nombre quedó dado de alta", Toast.LENGTH_SHORT).show()
                limpiarFormulario()
                cargarPersonal()
            }
            .addOnFailureListener {
                mostrarProgreso(false)
                Toast.makeText(this, "La cuenta se creó, pero no se guardaron sus datos: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ---------- Edición ----------

    private fun entrarEnEdicion(perfil: Perfil) {
        perfilEnEdicion = perfil

        txtTituloFormulario.text = "Editar integrante"
        etNombre.setText(perfil.nombre, false)
        etCorreo.setText(perfil.correo)
        etCorreo.isEnabled = false
        tilPassword.visibility = View.GONE
        etPuesto.setText(perfil.puesto, false)
        tilPuesto.isEnabled = false
        chkAdministrador.isChecked = perfil.esAdmin
        chkActivo.isChecked = perfil.activo
        chkActivo.visibility = View.VISIBLE
        btnGuardar.text = "Guardar cambios"
        btnRestablecerPassword.visibility = View.VISIBLE
        btnCancelarEdicion.visibility = View.VISIBLE
        btnVerExamenesPersona.visibility = View.VISIBLE
        btnEntrarComoUsuario.visibility = if (esUsuarioActual(perfil)) View.GONE else View.VISIBLE
        separadorPermisos.visibility = View.VISIBLE
        sectionPermisos.visibility = View.VISIBLE
        limpiarErrores()
        cargarPermisos(perfil.clave)

        findViewById<ScrollView>(R.id.scrollAdministrarPersonal).smoothScrollTo(0, 0)
    }

    private fun salirDeEdicion() {
        perfilEnEdicion = null
        txtTituloFormulario.text = "Nuevo integrante"
        etCorreo.isEnabled = true
        tilPassword.visibility = View.VISIBLE
        tilPuesto.isEnabled = true
        chkActivo.visibility = View.GONE
        btnGuardar.text = "Dar de alta"
        btnRestablecerPassword.visibility = View.GONE
        btnCancelarEdicion.visibility = View.GONE
        btnVerExamenesPersona.visibility = View.GONE
        btnEntrarComoUsuario.visibility = View.GONE
        separadorPermisos.visibility = View.GONE
        sectionPermisos.visibility = View.GONE
        limpiarFormulario()
    }

    private fun validarYGuardarEdicion() {
        val perfil = perfilEnEdicion ?: return
        val nombre = limpiarNombre(etNombre.text.toString())
        // El puesto está vinculado a la base de datos del personal: en edición no se modifica
        val puesto = perfil.puesto

        if (nombre.isEmpty()) {
            etNombre.error = "Escribe el nombre completo"
            return
        }

        val otro = buscarPorNombre(nombre)
        if (otro != null && otro.clave != perfil.clave) {
            etNombre.error = "Ya hay otra persona registrada con ese nombre"
            return
        }

        confirmarCambioDeNombre(perfil, nombre, puesto)
    }

    private fun confirmarCambioDeNombre(perfil: Perfil, nombre: String, puesto: String) {
        if (nombre == perfil.nombre) {
            confirmarCambiosPropios(perfil, nombre, puesto)
            return
        }
        // Los registros y exámenes se guardan con el nombre: cambiarlo separa el historial anterior
        AlertDialog.Builder(this)
            .setTitle("¿Cambiar el nombre?")
            .setMessage("Los reportes y exámenes se guardan con el nombre de la persona.\n\n" +
                "Lo registrado como \"${perfil.nombre}\" ya no aparecerá junto con lo nuevo de \"$nombre\".")
            .setPositiveButton("Cambiar nombre") { _, _ -> confirmarCambiosPropios(perfil, nombre, puesto) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Si el administrador se quita permisos a sí mismo, se pide confirmación antes de guardar
    private fun confirmarCambiosPropios(perfil: Perfil, nombre: String, puesto: String) {
        val quitaAdmin = perfil.esAdmin && !chkAdministrador.isChecked
        val seDesactiva = perfil.activo && !chkActivo.isChecked
        if (!esUsuarioActual(perfil) || (!quitaAdmin && !seDesactiva)) {
            guardarEdicion(perfil, nombre, puesto)
            return
        }

        val consecuencias = buildList {
            if (quitaAdmin) add("• Dejarás de ser administrador y saldrás de esta pantalla.")
            if (seDesactiva) add("• Tu cuenta quedará desactivada y se cerrará tu sesión.")
        }.joinToString("\n")

        AlertDialog.Builder(this)
            .setTitle("Estás editando tu propio perfil")
            .setMessage("$consecuencias\n\nOtro administrador tendría que devolverte el acceso. ¿Continuar?")
            .setPositiveButton("Continuar") { _, _ -> guardarEdicion(perfil, nombre, puesto) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarEdicion(perfil: Perfil, nombre: String, puesto: String) {
        val cambios = hashMapOf<String, Any>(
            "nombre" to nombre,
            "rol" to if (chkAdministrador.isChecked) CatalogoPersonal.ROL_ADMIN else CatalogoPersonal.ROL_USUARIO,
            "activo" to chkActivo.isChecked,
            "modificadoPor" to MainActivity.NombreUsuarioCompanion,
            "fechaModificacion" to ahora(),
            "acceso" to mapOf(
                "capacitaciones" to checkboxCapacitaciones.mapValues { (_, chk) -> chk.isChecked },
                "instructivos" to checkboxInstructivos.mapValues { (_, chk) -> chk.isChecked }
            )
        )

        mostrarProgreso(true)
        nodoPersonal.child(perfil.clave)
            .updateChildren(cambios)
            .addOnSuccessListener {
                mostrarProgreso(false)
                Toast.makeText(this, "Se guardaron los cambios de $nombre", Toast.LENGTH_SHORT).show()
                if (esUsuarioActual(perfil)) {
                    MainActivity.NombreUsuarioCompanion = nombre
                    if (!chkActivo.isChecked) {
                        cerrarSesion()
                        return@addOnSuccessListener
                    }
                    if (!chkAdministrador.isChecked &&
                        consultar_datos.usuarioApp !in CatalogoPersonal.CORREOS_ADMIN_INICIAL) {
                        consultar_datos.esAdministrador = false
                        finish()
                        return@addOnSuccessListener
                    }
                }
                salirDeEdicion()
                cargarPersonal()
            }
            .addOnFailureListener {
                mostrarProgreso(false)
                Toast.makeText(this, "No se pudieron guardar los cambios: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun confirmarEntrarComoUsuario(perfil: Perfil) {
        AlertDialog.Builder(this)
            .setTitle("Entrar como usuario")
            .setMessage("Vas a ver la app como ${perfil.nombre}.\n\nPodrás volver a tu cuenta de administrador desde el menú principal.")
            .setPositiveButton("Entrar") { _, _ -> entrarComoUsuario(perfil) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun entrarComoUsuario(perfil: Perfil) {
        val sesion = com.miguelrodriguez.rocaapp20.acceso.Sesion
        sesion.correoAdminOriginal = consultar_datos.usuarioApp
        sesion.nombreAdminOriginal = MainActivity.NombreUsuarioCompanion
        sesion.puestoAdminOriginal = consultar_datos.puestoUsuario

        sesion.correo = perfil.correo
        sesion.nombre = perfil.nombre
        sesion.puesto = perfil.puesto
        sesion.esAdministrador = false
        consultar_datos.esAdministrador = false
        MainActivity.NombreUsuarioCompanion = perfil.nombre

        startActivity(
            android.content.Intent(this, Seleccionar_actividad::class.java)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    private fun cargarPermisos(clave: String) {
        nodoPersonal.child(clave).child("acceso").get()
            .addOnSuccessListener { snapshot ->
                val caps = snapshot.child("capacitaciones").children
                    .filter { it.getValue(Boolean::class.java) == true }
                    .mapNotNull { it.key }.toSet()
                val instr = snapshot.child("instructivos").children
                    .filter { it.getValue(Boolean::class.java) == true }
                    .mapNotNull { it.key }.toSet()
                checkboxCapacitaciones.forEach { (id, chk) -> chk.isChecked = id in caps }
                checkboxInstructivos.forEach { (id, chk) -> chk.isChecked = id in instr }
            }
    }

    // Firebase no permite cambiar la contraseña de otra persona desde la app; se le envía un enlace para que la cambie
    private fun confirmarRestablecerPassword() {
        val perfil = perfilEnEdicion ?: return
        AlertDialog.Builder(this)
            .setTitle("Restablecer contraseña")
            .setMessage("Se enviará a ${perfil.correo} un enlace para que ${perfil.nombre} elija una contraseña nueva.")
            .setPositiveButton("Enviar") { _, _ ->
                FirebaseAuth.getInstance().sendPasswordResetEmail(perfil.correo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Correo enviado a ${perfil.correo}", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "No se pudo enviar el correo: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        MainActivity.NombreUsuarioCompanion = "NombreUsuario"
        consultar_datos.esAdministrador = false
        consultar_datos.puestoUsuario = null
        val intent = android.content.Intent(this, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    // ---------- Lista ----------

    private fun cargarPersonal() {
        nodoPersonal.get()
            .addOnSuccessListener { snapshot ->
                personalRegistrado = snapshot.children.mapNotNull { persona ->
                    val nombre = persona.child("nombre").getValue(String::class.java) ?: return@mapNotNull null
                    Perfil(
                        clave = persona.key ?: return@mapNotNull null,
                        nombre = nombre,
                        correo = persona.child("correo").getValue(String::class.java) ?: "",
                        puesto = persona.child("puesto").getValue(String::class.java) ?: "",
                        esAdmin = persona.child("rol").getValue(String::class.java) == CatalogoPersonal.ROL_ADMIN,
                        activo = persona.child("activo").getValue(Boolean::class.java) != false
                    )
                }.sortedBy { it.nombre }

                mostrarPersonal()
                actualizarSugerenciasNombre()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo cargar el personal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarPersonal() {
        listaPersonal.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (perfil in personalRegistrado) {
            val tarjeta = inflater.inflate(R.layout.item_personal, listaPersonal, false)
            tarjeta.findViewById<TextView>(R.id.txtInicialesPersonal).text = iniciales(perfil.nombre)
            tarjeta.findViewById<TextView>(R.id.txtNombreItemPersonal).text = perfil.nombre
            tarjeta.findViewById<TextView>(R.id.txtPuestoItemPersonal).text = perfil.puesto
            tarjeta.findViewById<TextView>(R.id.txtCorreoItemPersonal).text = perfil.correo
            tarjeta.findViewById<TextView>(R.id.txtRolItemPersonal).visibility =
                if (perfil.esAdmin) View.VISIBLE else View.GONE
            tarjeta.findViewById<TextView>(R.id.txtInactivoItemPersonal).visibility =
                if (perfil.activo) View.GONE else View.VISIBLE
            tarjeta.alpha = if (perfil.activo) 1f else 0.6f
            tarjeta.setOnClickListener { entrarEnEdicion(perfil) }
            listaPersonal.addView(tarjeta)
        }

        txtSinPersonal.visibility = if (personalRegistrado.isEmpty()) View.VISIBLE else View.GONE
    }

    // Las sugerencias solo incluyen a quienes aún no están registrados
    private fun actualizarSugerenciasNombre() {
        val pendientes = CatalogoPersonal.personas
            .map { it.nombre }
            .filter { buscarPorNombre(it) == null }
        etNombre.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, pendientes))
    }

    // ---------- Utilidades ----------

    private fun personaDelCatalogo(nombre: String): CatalogoPersonal.PersonaCatalogo? {
        val buscado = normalizar(nombre)
        return CatalogoPersonal.personas.firstOrNull { normalizar(it.nombre) == buscado }
    }

    private fun actualizarBloqueoPuesto() {
        if (perfilEnEdicion != null) return
        val persona = personaDelCatalogo(etNombre.text.toString())
        if (persona != null) {
            etPuesto.setText(persona.puesto, false)
            etPuesto.error = null
            tilPuesto.isEnabled = false
        } else if (!tilPuesto.isEnabled) {
            // Se dejó de escribir un nombre del catálogo: el puesto vuelve a elegirse a mano
            tilPuesto.isEnabled = true
            etPuesto.setText("", false)
        }
    }

    // Compara nombres sin distinguir mayúsculas, acentos ni espacios repetidos
    private fun buscarPorNombre(nombre: String): Perfil? {
        val buscado = normalizar(nombre)
        return personalRegistrado.firstOrNull { normalizar(it.nombre) == buscado }
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(limpiarNombre(texto).lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

    private fun limpiarNombre(nombre: String): String = nombre.trim().replace(Regex("\\s+"), " ")

    private fun esUsuarioActual(perfil: Perfil): Boolean =
        consultar_datos.usuarioApp?.let { CatalogoPersonal.claveCorreo(it) } == perfil.clave

    private fun iniciales(nombre: String): String =
        nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

    private fun ahora(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    private fun limpiarErrores() {
        etNombre.error = null
        etCorreo.error = null
        etPassword.error = null
        etPuesto.error = null
    }

    private fun limpiarFormulario() {
        etNombre.setText("", false)
        etCorreo.setText("")
        etPassword.setText("")
        etPuesto.setText("", false)
        chkAdministrador.isChecked = false
        chkActivo.isChecked = true
        limpiarErrores()
    }

    private fun mostrarProgreso(enCurso: Boolean) {
        progresoAlta.visibility = if (enCurso) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !enCurso
        btnRestablecerPassword.isEnabled = !enCurso
    }
}
