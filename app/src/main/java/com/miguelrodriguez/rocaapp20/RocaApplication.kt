package com.miguelrodriguez.rocaapp20

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.miguelrodriguez.rocaapp20.acceso.Sesion

class RocaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // La sesión debe estar disponible incluso cuando Android restaura una pantalla tras cerrar el proceso
        Sesion.inicializar(this)
        registerActivityLifecycleCallbacks(VigilanteSesion)
    }

    // Ninguna pantalla (salvo el login) debe abrirse sin usuario: así ningún reporte se guarda como "NombreUsuario"
    private object VigilanteSesion : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is MainActivity || Sesion.nombre != Sesion.NOMBRE_SIN_SESION) return

            Toast.makeText(activity, "Tu sesión expiró. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
            val login = Intent(activity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(login)
            activity.finish()
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
}
