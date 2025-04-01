package com.example.desafio2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.example.desafio2.datos.Tarea
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorTarea(private val context: Context, private val tareas: ArrayList<Tarea>) : BaseAdapter() {

    override fun getCount(): Int {
        return tareas.size
    }

    override fun getItem(position: Int): Any {
        return tareas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val tarea = tareas[position]
        viewHolder.titulo.text = tarea.titulo
        viewHolder.descripcion.text = tarea.descripcion
        viewHolder.estado.text = tarea.estado

        // Formatear la fecha de creación
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val fechaCreacion = Date(tarea.fechaCreacion)
        viewHolder.fechaCreacion.text = sdf.format(fechaCreacion)

        viewHolder.completada.isChecked = tarea.estado == "completada"

        viewHolder.completada.setOnClickListener {
            val estado = if (viewHolder.completada.isChecked) "completada" else "pendiente"
            tarea.key?.let {
                FirebaseDatabase.getInstance().getReference("tareas").child(it).child("estado").setValue(estado)
            }
        }

        viewHolder.btnEditar.setOnClickListener {
            val intent = Intent(context, AddTareaActivity::class.java)
            intent.putExtra("accion", "e")
            intent.putExtra("key", tarea.key)
            intent.putExtra("titulo", tarea.titulo)
            intent.putExtra("descripcion", tarea.descripcion)
            intent.putExtra("estado", tarea.estado)
            context.startActivity(intent)
        }

        viewHolder.btnEliminar.setOnClickListener {
            tarea.key?.let {
                FirebaseDatabase.getInstance().getReference("tareas").child(it).removeValue()
            }
        }

        return view
    }

    private class ViewHolder(view: View) {
        val titulo: TextView = view.findViewById(R.id.txtTitulo)
        val descripcion: TextView = view.findViewById(R.id.txtDescripcion)
        val estado: TextView = view.findViewById(R.id.txtEstado)
        val fechaCreacion: TextView = view.findViewById(R.id.txtFechaCreacion)
        val completada: CheckBox = view.findViewById(R.id.chkCompletada)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }
}