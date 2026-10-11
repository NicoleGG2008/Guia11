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

class ActualizarProfesorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Ui.edgeToEdge(this)
        setContentView(R.layout.activity_actualizar_profesor)
        Ui.insets(findViewById(R.id.main), findViewById(R.id.header))

        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilApellido = findViewById<TextInputLayout>(R.id.tilApellido)
        val tilEdad = findViewById<TextInputLayout>(R.id.tilEdad)
        val etNombre = findViewById<TextInputEditText>(R.id.editTextNombre)
        val etApellido = findViewById<TextInputEditText>(R.id.editTextApellido)
        val etEdad = findViewById<TextInputEditText>(R.id.editTextEdad)
        val btnActualizar = findViewById<Button>(R.id.btnGuardar)

        // Datos enviados desde la lista
        val id = intent.getIntExtra("id", -1)
        etNombre.setText(intent.getStringExtra("nombre") ?: "")
        etApellido.setText(intent.getStringExtra("apellido") ?: "")
        etEdad.setText(intent.getIntExtra("edad", 1).toString())

        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }

        btnActualizar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val edad = etEdad.text.toString().trim().toIntOrNull()

            tilNombre.error = if (nombre.isBlank()) "Ingrese el nombre" else null
            tilApellido.error = if (apellido.isBlank()) "Ingrese el apellido" else null
            tilEdad.error = if (edad == null || edad !in 1..120) "Ingrese una edad entre 1 y 120" else null
            if (nombre.isBlank() || apellido.isBlank() || edad == null || edad !in 1..120) {
                return@setOnClickListener
            }
            if (id == -1) {
                Toast.makeText(this, "No se recibió el id del registro", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnActualizar.isEnabled = false
            val actualizado = Profesor(id = id, nombre = nombre, apellido = apellido, edad = edad)

            RetrofitClient.profesorApi.actualizarProfesor(id, actualizado).enqueue(object : Callback<Profesor> {
                override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ActualizarProfesorActivity, "Profesor actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish() // la lista se recarga en onResume()
                    } else {
                        btnActualizar.isEnabled = true
                        Log.e("API", "Error al actualizar: HTTP ${response.code()} ${response.errorBody()?.string()}")
                        Toast.makeText(this@ActualizarProfesorActivity, "No se pudo actualizar (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Profesor>, t: Throwable) {
                    btnActualizar.isEnabled = true
                    Log.e("API", "Error al actualizar", t)
                    Toast.makeText(this@ActualizarProfesorActivity, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}