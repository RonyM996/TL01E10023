package com.ucenm.tl01e10023

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DbHelper(context: Context) : SQLiteOpenHelper(context, "ContactosDB", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE contactos (id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, nombre TEXT, telefono TEXT, nota TEXT, imagen BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS contactos")
        onCreate(db)
    }

    fun insertContacto(c: Contacto): Long {
        val db = this.writableDatabase
        val v = ContentValues().apply {
            put("pais", c.pais)
            put("nombre", c.nombre)
            put("telefono", c.telefono)
            put("nota", c.nota)
            put("imagen", c.imagen)
        }
        return db.insert("contactos", null, v)
    }

    fun getAllContactos(): ArrayList<Contacto> {
        val list = ArrayList<Contacto>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM contactos", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(Contacto(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getBlob(5)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteContacto(id: Int) = this.writableDatabase.delete("contactos", "id=?", arrayOf(id.toString()))

    fun updateContacto(c: Contacto): Int {
        val v = ContentValues().apply { put("nombre", c.nombre); put("telefono", c.telefono); put("nota", c.nota) }
        return this.writableDatabase.update("contactos", v, "id=?", arrayOf(c.id.toString()))
    }
}