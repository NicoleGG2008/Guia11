package com.example.guia11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfesorAdapter(
    private val items: List<Profesor>,
    private val onItemClick: (Profesor) -> Unit
) : RecyclerView.Adapter<ProfesorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inicial: TextView = view.findViewById(R.id.tvInicial)
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val apellido: TextView = view.findViewById(R.id.tvApellido)
        val edad: TextView = view.findViewById(R.id.tvEdad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.inicial.text = item.nombre.trim().firstOrNull()?.uppercase() ?: "?"
        holder.nombre.text = item.nombre.trim()
        holder.apellido.text = item.apellido.trim()
        holder.edad.text = "${item.edad} años"
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}