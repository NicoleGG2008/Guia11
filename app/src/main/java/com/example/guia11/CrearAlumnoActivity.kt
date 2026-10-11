package com.example.guia11

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearAlumnoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Ui.edgeToEdge(this)
        setContentView(R.layout.activity_crear_alumno)
        Ui.insets(findViewById(R.id.main), findViewById(R.id.header))

        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilApellido = findViewById<TextInputLayout>(R.id.tilApellido)
        val tilEdad = findViewById<TextInputLayout>(R.id.tilEdad)
        val etNombre = findViewById<TextInputEditText>(R.id.editTextNombre)
        val etApellido = findViewById<TextInputEditText>(R.id.editTextApellido)
        val etEdad = findViewById<TextInputEditText>(R.id.editTextEdad)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val edad = etEdad.text.toString().trim().toIntOrNull()

            tilNombre.error = if (nombre.isBlank()) "Ingrese el nombre" else null
            tilApellido.error = if (apellido.isBlank()) "Ingrese el apellido" else null
            tilEdad.error = if (edad == null || edad !in 1..120) "Ingrese una edad entre 1 y 120" else null
            if (nombre.isBlank() || apellido.isBlank() || edad == null || edad !in 1..120) {
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            val nuevo = Alumno(nombre = nombre, apellido = apellido, edad = edad)

            RetrofitClient.alumnoApi.crearAlumno(nuevo).enqueue(object : Callback<Alumno> {
                override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearAlumnoActivity, "Alumno creado exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // la lista se recarga en onResume()
                    } else {
                        btnGuardar.isEnabled = true
                        Log.e("API", "Error al crear: HTTP ${response.code()} ${response.errorBody()?.string()}")
                        Toast.makeText(this@CrearAlumnoActivity, "No se pudo crear (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Alumno>, t: Throwable) {
                    btnGuardar.isEnabled = true
                    Log.e("API", "Error al crear", t)
                    Toast.makeText(this@CrearAlumnoActivity, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}