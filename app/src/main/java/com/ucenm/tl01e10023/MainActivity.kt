package com.ucenm.tl01e10023

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var db: DbHelper
    private var imgBytes: ByteArray? = null
    private lateinit var imgV: ImageView
    private lateinit var listaP: MutableList<String>
    private lateinit var adaptP: ArrayAdapter<String>

    private val camLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        if (r.resultCode == RESULT_OK) {
            val btm = r.data?.extras?.get("data") as? Bitmap
            btm?.let {
                imgV.setImageBitmap(it)
                val s = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, s)
                imgBytes = s.toByteArray()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)

            db = DbHelper(this)
            imgV = findViewById(R.id.imgContacto)
            val spn = findViewById<Spinner>(R.id.spnPais)
            val txtN = findViewById<EditText>(R.id.txtNombre)
            val txtT = findViewById<EditText>(R.id.txtTelefono)
            val txtNo = findViewById<EditText>(R.id.txtNota)

            listaP = cargarPaises()

            adaptP = ArrayAdapter(this, R.layout.item_spinner, listaP)
            adaptP.setDropDownViewResource(R.layout.item_spinner)
            spn.adapter = adaptP

            findViewById<ImageButton>(R.id.btnAgregarPais).setOnClickListener {
                val input = EditText(this)
                AlertDialog.Builder(this).setTitle("Nuevo País").setView(input).setPositiveButton("Guardar") { _, _ ->
                    val nuevo = input.text.toString().trim()
                    if (nuevo.isNotEmpty()) {
                        guardarPais(nuevo)
                    }
                }.show()
            }

            findViewById<ImageButton>(R.id.btnCapturar).setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    camLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            }

            findViewById<Button>(R.id.btnSalvar).setOnClickListener {
                try {
                    val n = txtN.text.toString().trim()
                    val t = txtT.text.toString().trim()
                    val no = txtNo.text.toString().trim()
                    val p = spn.selectedItem?.toString() ?: ""

                    if (n.isEmpty()) {
                        AlertDialog.Builder(this).setTitle("Alerta").setMessage("debe escribir un nombre").show()
                        return@setOnClickListener
                    }
                    if (t.isEmpty()) {
                        AlertDialog.Builder(this).setTitle("Alerta").setMessage("debe escribir un telefono").show()
                        return@setOnClickListener
                    }
                    if (no.isEmpty()) {
                        AlertDialog.Builder(this).setTitle("Alerta").setMessage("debe escribir una nota").show()
                        return@setOnClickListener
                    }

                    if (!n.matches(Regex("^[a-zA-Z\\s]+$"))) {
                        AlertDialog.Builder(this).setTitle("Alerta").setMessage("El nombre solo debe contener letras").show()
                        return@setOnClickListener
                    }
                    if (!t.matches(Regex("^[0-9]+$"))) {
                        AlertDialog.Builder(this).setTitle("Alerta").setMessage("El telefono solo debe contener numeros").show()
                        return@setOnClickListener
                    }

                    db.insertContacto(Contacto(0, p, n, t, no, imgBytes))

                    Toast.makeText(this, "Guardado exitosamente", Toast.LENGTH_SHORT).show()

                    txtN.text.clear()
                    txtT.text.clear()
                    txtNo.text.clear()
                    imgV.setImageResource(R.drawable.ic_silueta)
                    imgBytes = null

                } catch (e: Exception) {
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            findViewById<Button>(R.id.btnVerContactos).setOnClickListener {
                startActivity(Intent(this, ListActivity::class.java))
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar pantalla: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarPaises(): MutableList<String> {
        val prefs = getSharedPreferences("MisPaisesPrefs", Context.MODE_PRIVATE)
        val guardados = prefs.getString("listaPaises", "Honduras (504),Costa Rica (506),Guatemala (502),El Salvador (503)")
        return guardados!!.split(",").toMutableList()
    }

    private fun guardarPais(nuevo: String) {
        listaP.add(nuevo)
        adaptP.notifyDataSetChanged()
        val prefs = getSharedPreferences("MisPaisesPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("listaPaises", listaP.joinToString(",")).apply()
    }
}