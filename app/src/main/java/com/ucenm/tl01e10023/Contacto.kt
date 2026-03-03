package com.ucenm.tl01e10023

data class Contacto(
    var id: Int = 0,
    var pais: String = "",
    var nombre: String = "",
    var telefono: String = "",
    var nota: String = "",
    var imagen: ByteArray? = null
)