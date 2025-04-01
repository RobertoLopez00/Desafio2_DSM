package com.example.desafio2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio2.datos.Tarea
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddTareaActivity : AppCompatActivity() {

    private lateinit var txtTitulo: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var refTareas: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tarea)

        inicializar()
    }

    private fun inicializar() {
        txtTitulo = findViewById(R.id.txtTitulo)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        refTareas = FirebaseDatabase.getInstance().getReference("tareas").child(userId)

        val accion = intent.getStringExtra("accion")
        if (accion == "e") {
            txtTitulo.setText(intent.getStringExtra("titulo"))
            txtDescripcion.setText(intent.getStringExtra("descripcion"))
        }

        btnGuardar.setOnClickListener {
            val titulo = txtTitulo.text.toString()
            val descripcion = txtDescripcion.text.toString()
            val estado = "pendiente"
            val fechaCreacion = System.currentTimeMillis()

            if (accion == "a") {
                val key = refTareas.push().key
                val tarea = Tarea(key, titulo, descripcion, estado, fechaCreacion)
                if (key != null) {
                    refTareas.child(key).setValue(tarea)
                }
            } else if (accion == "e") {
                val key = intent.getStringExtra("key")
                val tarea = Tarea(key, titulo, descripcion, estado, fechaCreacion)
                if (key != null) {
                    refTareas.child(key).setValue(tarea)
                }
            }
            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}