package com.miguelrodriguez.rocaapp20.acceso

class consultar_datos {

    companion object consulta{
        var UsuarioCilindroActualziar:String=""
        var fechaCilindroActualizar:String=""
        var idCilindroActualizar:Int=1
        var elemento:String?=null
        // Datos de sesión: se guardan en el teléfono para que sobrevivan si Android cierra el proceso
        var usuarioApp:String?
            get() = Sesion.correo
            set(valor) { Sesion.correo = valor }
        var modoInvitado:Boolean
            get() = Sesion.modoInvitado
            set(valor) { Sesion.modoInvitado = valor }
        var esAdministrador:Boolean
            get() = Sesion.esAdministrador
            set(valor) { Sesion.esAdministrador = valor }
        var puestoUsuario:String?
            get() = Sesion.puesto
            set(valor) { Sesion.puesto = valor }

    }
}