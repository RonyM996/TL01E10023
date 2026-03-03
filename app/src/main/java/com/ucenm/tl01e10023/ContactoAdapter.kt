package com.ucenm.tl01e10023

import android.content.Context
import android.view.*
import android.widget.*

class ContactoAdapter(val ctx: Context, val list: List<Contacto>) : BaseAdapter(), Filterable {
    var filtered = list

    override fun getCount() = filtered.size
    override fun getItem(p: Int) = filtered[p]
    override fun getItemId(p: Int) = filtered[p].id.toLong()

    override fun getView(p: Int, v: View?, parent: ViewGroup?): View {
        val view = v ?: LayoutInflater.from(ctx).inflate(R.layout.item_contacto, parent, false)
        val lbl = view.findViewById<TextView>(R.id.lblNombreTelefono)
        lbl.text = "${filtered[p].nombre} | ${filtered[p].telefono}"
        return view
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(c: CharSequence?): FilterResults {
            val res = FilterResults()
            if (c.isNullOrEmpty()) {
                res.values = list
            } else {
                val filterPattern = c.toString().lowercase().trim()
                res.values = list.filter {
                    it.nombre.lowercase().contains(filterPattern) ||
                            it.telefono.contains(filterPattern)
                }
            }
            return res
        }

        override fun publishResults(c: CharSequence?, r: FilterResults?) {
            filtered = r?.values as List<Contacto>
            notifyDataSetChanged()
        }
    }
}