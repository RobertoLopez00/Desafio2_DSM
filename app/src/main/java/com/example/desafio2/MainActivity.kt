package com.example.desafio2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio2.datos.Tarea
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var consultaOrdenada: Query
    private lateinit var tareas: MutableList<Tarea>
    private lateinit var lvTareas: ListView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializar()
    }

    private fun inicializar() {
        tareas = ArrayList()
        fabAgregar = findViewById(R.id.fab_agregar)
        lvTareas = findViewById(R.id.ListaTareas)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        consultaOrdenada = FirebaseDatabase.getInstance().getReference("tareas").child(userId).orderByChild("titulo")

        lvTareas.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(baseContext, AddTareaActivity::class.java)
            intent.putExtra("accion", "e")
            intent.putExtra("key", tareas[i].key)
            intent.putExtra("titulo", tareas[i].titulo)
            intent.putExtra("descripcion", tareas[i].descripcion)
            intent.putExtra("estado", tareas[i].estado)
            startActivity(intent)
        }

        lvTareas.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            alertDialog.setMessage("¿Está seguro de eliminar la tarea?")
            alertDialog.setTitle("Confirmación")
            alertDialog.setPositiveButton("Sí") { _, _ ->
                tareas[position].key?.let {
                    FirebaseDatabase.getInstance().getReference("tareas").child(userId).child(it).removeValue()
                }
                Toast.makeText(this@MainActivity, "Tarea borrada!", Toast.LENGTH_SHORT).show()
            }
            alertDialog.setNegativeButton("No") { _, _ ->
                Toast.makeText(this@MainActivity, "Operación de borrado cancelada!", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
            true
        }

        fabAgregar.setOnClickListener {
            val i = Intent(baseContext, AddTareaActivity::class.java)
            i.putExtra("accion", "a")
            i.putExtra("key", "")
            i.putExtra("titulo", "")
            i.putExtra("descripcion", "")
            i.putExtra("estado", "pendiente")
            startActivity(i)
        }

        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tareas.clear()
                for (dato in dataSnapshot.children) {
                    val tarea: Tarea? = dato.getValue(Tarea::class.java)
                    tarea?.key = dato.key
                    if (tarea != null) {
                        tareas.add(tarea)
                    }
                }
                val adapter = AdaptadorTarea(this@MainActivity, tareas as ArrayList<Tarea>)
                lvTareas.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut().also {
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}