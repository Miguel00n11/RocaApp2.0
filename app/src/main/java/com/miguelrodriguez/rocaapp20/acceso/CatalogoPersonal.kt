package com.miguelrodriguez.rocaapp20.acceso

// Personal y puestos del laboratorio, tomados de la tabla `personal` de la base fortastudio_roca.
// Se usan como sugerencias en el alta de personal; se puede escribir un nombre que no esté aquí.
object CatalogoPersonal {

    // Nodo de Firebase donde se guardan los perfiles del personal
    const val NODO_PERSONAL = "Personal"

    const val ROL_ADMIN = "admin"
    const val ROL_USUARIO = "usuario"

    // Correos con permisos de administrador aunque aún no tengan perfil (para dar de alta al primer administrador)
    val CORREOS_ADMIN_INICIAL = setOf("miguel00n11@gmail.com")

    data class PersonaCatalogo(val nombre: String, val puesto: String, val correo: String? = null)

    val personas = listOf(
        PersonaCatalogo("Carlos Ali Rodríguez Ortega", "Director general"),
        PersonaCatalogo("Jesús Miguel Rodríguez Ortega", "Responsable técnico", "miguel00n11@gmail.com"),
        PersonaCatalogo("Carlos Alfonso Torres Cervantes", "Técnico laboratorista"),
        PersonaCatalogo("José Luis Calixto Ramírez", "Técnico laboratorista"),
        PersonaCatalogo("Cristina Andrea Rodríguez Ortega", "Responsable de laboratorio central", "ca.rodriguez.ortega@outlook.com"),
        PersonaCatalogo("José Adrián Cortés Martínez", "Laboratorista central"),
        PersonaCatalogo("Nely Ruth Ortega Salazar", "Responsable de recursos humanos"),
        PersonaCatalogo("Hugo Pedro Rea de la Cruz", "Responsable de calidad"),
        PersonaCatalogo("Dulce Rocío Salazar Santoyo", "Responsable administrativo")
    )

    val puestos = listOf(
        "Director general",
        "Responsable técnico",
        "Responsable de laboratorio central",
        "Responsable de calidad",
        "Responsable administrativo",
        "Responsable de recursos humanos",
        "Técnico laboratorista",
        "Laboratorista central"
    )

    // Firebase no admite "." en las claves; se usa la convención de reemplazarlo por ","
    fun claveCorreo(correo: String): String = correo.trim().lowercase().replace(".", ",")
}
