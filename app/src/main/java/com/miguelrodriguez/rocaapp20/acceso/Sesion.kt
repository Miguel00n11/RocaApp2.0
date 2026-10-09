package com.miguelrodriguez.rocaapp20.acceso

import android.content.Context
import android.content.SharedPreferences

// Datos de la sesión guardados en el teléfono.
// Android puede cerrar el proceso de la app mientras está en segundo plano y, al volver, restaura
// directamente la última pantalla sin pasar por el login. Si estos datos vivieran solo en memoria
// regresarían a su valor inicial y los reportes se guardarían como "NombreUsuario".
object Sesion {

    const val NOMBRE_SIN_SESION = "NombreUsuario"

    private const val ARCHIVO = "sesion_usuario"
    private const val CLAVE_NOMBRE = "nombre"
    private const val CLAVE_CORREO = "correo"
    private const val CLAVE_PUESTO = "puesto"
    private const val CLAVE_ADMIN = "esAdministrador"
    private const val CLAVE_INVITADO = "modoInvitado"
    private const val CLAVE_CORREO_ADMIN_ORIGINAL = "correoAdminOriginal"
    private const val CLAVE_NOMBRE_ADMIN_ORIGINAL = "nombreAdminOriginal"
    private const val CLAVE_PUESTO_ADMIN_ORIGINAL = "puestoAdminOriginal"

    private lateinit var preferencias: SharedPreferences

    // Se llama al arrancar la app (RocaApplication), antes de crear cualquier pantalla
    fun inicializar(context: Context) {
        preferencias = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
    }

    var nombre: String
        get() = preferencias.getString(CLAVE_NOMBRE, null) ?: NOMBRE_SIN_SESION
        set(valor) = preferencias.edit().putString(CLAVE_NOMBRE, valor).apply()

    var correo: String?
        get() = preferencias.getString(CLAVE_CORREO, null)
        set(valor) = preferencias.edit().putString(CLAVE_CORREO, valor).apply()

    var puesto: String?
        get() = preferencias.getString(CLAVE_PUESTO, null)
        set(valor) = preferencias.edit().putString(CLAVE_PUESTO, valor).apply()

    var esAdministrador: Boolean
        get() = preferencias.getBoolean(CLAVE_ADMIN, false)
        set(valor) = preferencias.edit().putBoolean(CLAVE_ADMIN, valor).apply()

    var modoInvitado: Boolean
        get() = preferencias.getBoolean(CLAVE_INVITADO, false)
        set(valor) = preferencias.edit().putBoolean(CLAVE_INVITADO, valor).apply()

    var correoAdminOriginal: String?
        get() = preferencias.getString(CLAVE_CORREO_ADMIN_ORIGINAL, null)
        set(valor) = preferencias.edit().putString(CLAVE_CORREO_ADMIN_ORIGINAL, valor).apply()

    var nombreAdminOriginal: String?
        get() = preferencias.getString(CLAVE_NOMBRE_ADMIN_ORIGINAL, null)
        set(valor) = preferencias.edit().putString(CLAVE_NOMBRE_ADMIN_ORIGINAL, valor).apply()

    var puestoAdminOriginal: String?
        get() = preferencias.getString(CLAVE_PUESTO_ADMIN_ORIGINAL, null)
        set(valor) = preferencias.edit().putString(CLAVE_PUESTO_ADMIN_ORIGINAL, valor).apply()

    val modoImpersonacion: Boolean
        get() = correoAdminOriginal != null

    fun cerrar() {
        preferencias.edit().clear().apply()
    }
}
