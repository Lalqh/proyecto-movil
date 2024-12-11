package com.example.proyecto.ModelClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import androidx.core.net.toUri
import com.bumptech.glide.Glide
data class ProductoData(
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val descuento: String = "",
    val stock: String,
    val categoria: String,
    val code:String,
    val img: String,

    )
class ProductoAdapter(private var productos:List<ProductoData>):RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductoAdapter.ProductoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.producto_item,parent,false)
        return ProductoViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }
    fun updateProductos(newProductos: List<ProductoData>) {
        productos = newProductos
        notifyDataSetChanged()
    }
    override fun getItemCount() = productos.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
        private val precioTextView: TextView = itemView.findViewById(R.id.textViewPrecio)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcion)
        private val descuentoTextView: TextView = itemView.findViewById(R.id.textViewDescuento)
        private val stockTextView: TextView = itemView.findViewById(R.id.textViewStock)
        private val categoriaTextView: TextView = itemView.findViewById(R.id.textViewCategoria)
        private val productImage:ImageView=itemView.findViewById(R.id.imageViewProducto)

        fun bind(producto: ProductoData) {
            nombreTextView.text = "Nombre: ${producto.nombre}"
            precioTextView.text = "Precio: ${producto.precio}"
            descripcionTextView.text = "Descripción: ${producto.descripcion}"
            descuentoTextView.text = "Descuento: ${producto.descuento}"
            stockTextView.text = "Stock: ${producto.stock}"
            categoriaTextView.text = "Categoría: ${producto.categoria}"
            if (producto.img.isNullOrEmpty()){
                Glide.with(itemView.context)
                    .load(producto.img.toUri())
                    .into(productImage)

            }

            itemView.setOnClickListener{



            }
        }
    }
}