package com.ucenm.tl01e10023

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ActualizarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar)
        val db = DbHelper(this)
        val id = intent.getIntExtra("id", 0)
        val n = findViewById<EditText>(R.id.txtEditNombre).apply { setText(intent.getStringExtra("nombre")) }
        val t = findViewById<EditText>(R.id.txtEditTelefono).apply { setText(intent.getStringExtra("telefono")) }
        val no = findViewById<EditText>(R.id.txtEditNota).apply { setText(intent.getStringExtra("nota")) }

        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            if (db.updateContacto(Contacto(id, "", n.text.toString(), t.text.toString(), no.text.toString())) > 0) {
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}