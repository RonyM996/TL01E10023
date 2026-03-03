package com.ucenm.tl01e10023

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ListActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var listV: ListView
    private var sel: Contacto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_list)
            db = DbHelper(this)
            listV = findViewById(R.id.lstContactos)

            cargar()

            findViewById<EditText>(R.id.txtBuscar).addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, b: Int, c: Int, a: Int) {
                    (listV.adapter as? ContactoAdapter)?.filter?.filter(s)
                }
                override fun beforeTextChanged(s: CharSequence?, b: Int, c: Int, a: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })

            listV.setOnItemClickListener { p, _, pos, _ ->
                sel = p.getItemAtPosition(pos) as Contacto
                AlertDialog.Builder(this).setMessage("¿Llamar a ${sel!!.nombre}?").setPositiveButton("Si") { _, _ ->
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${sel!!.telefono}")))
                    } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                }.setNegativeButton("No", null).show()
            }

            findViewById<Button>(R.id.btnEliminar).setOnClickListener {
                sel?.let { db.deleteContacto(it.id); cargar(); sel = null; Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show() }
            }

            findViewById<Button>(R.id.btnCompartir).setOnClickListener {
                sel?.let {
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "Contacto: ${it.nombre} | Tel: ${it.telefono}")
                    }, "Compartir"))
                }
            }

            findViewById<Button>(R.id.btnVerImagen).setOnClickListener {
                sel?.let { c ->
                    if (c.imagen != null) {
                        val iv = ImageView(this)
                        iv.setImageBitmap(BitmapFactory.decodeByteArray(c.imagen, 0, c.imagen!!.size))
                        AlertDialog.Builder(this).setView(iv).setPositiveButton("Cerrar", null).show()
                    } else {
                        Toast.makeText(this, "Este contacto no tiene imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            findViewById<Button>(R.id.btnActualizar).setOnClickListener {
                sel?.let {
                    startActivity(Intent(this, ActualizarActivity::class.java).apply {
                        putExtra("id", it.id); putExtra("nombre", it.nombre); putExtra("telefono", it.telefono); putExtra("nota", it.nota); putExtra("imagen", it.imagen)
                    })
                }
            }

            findViewById<Button>(R.id.btnAtras).setOnClickListener { finish() }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la pantalla: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargar() {
        try {
            listV.adapter = ContactoAdapter(this, db.getAllContactos())
        } catch (e: Exception) {
            Toast.makeText(this, "Error de base de datos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }
}