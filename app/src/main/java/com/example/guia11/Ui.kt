package com.example.guia11

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/** Utilidades de interfaz compartidas por todas las pantallas. */
object Ui {

    /** Pantalla completa: iconos claros en la barra de estado (el encabezado es verde oscuro). */
    fun edgeToEdge(activity: ComponentActivity) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
    }

    /** Ajusta márgenes del sistema: la barra de estado se suma al encabezado y el teclado empuja el contenido. */
    fun insets(root: View, header: View) {
        val topBase = header.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val b = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.ime()
            )
            v.setPadding(b.left, 0, b.right, b.bottom)
            header.updatePadding(top = topBase + b.top)
            insets
        }
    }

    /** Cambia entre Alumnos y Profesores (barra inferior). */
    @Suppress("DEPRECATION")
    fun irA(origen: Activity, destino: Class<*>) {
        origen.startActivity(Intent(origen, destino))
        origen.finish()
        origen.overridePendingTransition(0, 0)
    }
}