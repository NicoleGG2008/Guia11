package com.example.guia11

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfesorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: View
    private lateinit var tvVacio: TextView
    private lateinit var tvResumen: TextView
    private val api = RetrofitClient.profesorApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Ui.edgeToEdge(this)
        setContentView(R.layout.activity_profesor)
        Ui.insets(findViewById(R.id.main), findViewById(R.id.header))

        recyclerView = findViewById(R.id.recyclerView)
        progress = findViewById(R.id.progress)
        tvVacio = findViewById(R.id.tvVacio)
        tvResumen = findViewById(R.id.tvResumen)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ExtendedFloatingActionButton>(R.id.fab_agregar).setOnClickListener {
            startActivity(Intent(this, CrearProfesorActivity::class.java))
        }

        // Barra inferior: Alumnos / Profesores
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profesores
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_alumnos) Ui.irA(this, MainActivity::class.java)
            true
        }
    }

    // Se ejecuta al abrir y también al regresar de crear/modificar
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        progress.visibility = View.VISIBLE
        api.obtenerProfesores().enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(call: Call<List<Profesor>>, response: Response<List<Profesor>>) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val lista = response.body().orEmpty()
                    recyclerView.adapter = ProfesorAdapter(lista) { mostrarOpciones(it) }
                    tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    tvResumen.text = "${lista.size} profesores registrados"
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener: HTTP ${response.code()} $error")
                    tvResumen.text = "No se pudo cargar la lista"
                    Toast.makeText(
                        this@ProfesorActivity,
                        "Error del servidor: HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                progress.visibility = View.GONE
                Log.e("API", "Error al obtener", t)
                tvResumen.text = "Sin conexión con el servidor"
                Toast.makeText(
                    this@ProfesorActivity,
                    "No se pudo conectar: ${t.javaClass.simpleName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun mostrarOpciones(item: Profesor) {
        MaterialAlertDialogBuilder(this)
            .setTitle("${item.nombre} ${item.apellido}")
            .setItems(arrayOf("Modificar", "Eliminar")) { _, which ->
                when (which) {
                    0 -> modificar(item)
                    1 -> confirmarEliminar(item)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun modificar(item: Profesor) {
        val i = Intent(this, ActualizarProfesorActivity::class.java)
        i.putExtra("id", item.id ?: -1)
        i.putExtra("nombre", item.nombre)
        i.putExtra("apellido", item.apellido)
        i.putExtra("edad", item.edad)
        startActivity(i)
    }

    private fun confirmarEliminar(item: Profesor) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar profesor")
            .setMessage("¿Seguro que deseas eliminar a ${item.nombre} ${item.apellido}?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ -> eliminar(item) }
            .show()
    }

    private fun eliminar(item: Profesor) {
        val id = item.id ?: return
        api.eliminarProfesor(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfesorActivity, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else {
                    Log.e("API", "Error al eliminar: HTTP ${response.code()} ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfesorActivity, "No se pudo eliminar (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar", t)
                Toast.makeText(this@ProfesorActivity, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show()
            }
        })
    }
}