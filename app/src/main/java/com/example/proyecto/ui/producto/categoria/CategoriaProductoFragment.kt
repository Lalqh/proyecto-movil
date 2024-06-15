package com.example.proyecto.ui.producto.categoria

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R

class CategoriaProductoFragment : Fragment() {

    companion object {
        fun newInstance() = CategoriaProductoFragment()
    }
    private lateinit var scrollView: ScrollView
    private lateinit var linearLayout: LinearLayout
    private lateinit var viewModel: CategoriaProductoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categoria_producto, container, false)
        scrollView = view.findViewById(R.id.scrollViewCategories)
        linearLayout = scrollView.findViewById(R.id.linearLayoutCategories)

        loadCategories()

        return view
    }

    private fun loadCategories() {
        val categories = Utils.getCategoriesFromPreferences(requireContext())

        for (category in categories) {
            val textView = TextView(requireContext())
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 15)
            }
            textView.text = category.name
            textView.setBackgroundResource(R.drawable.bordes)
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_arrow_right_24, 0, 0, 0)
            textView.setTextColor(resources.getColor(R.color.white))

            linearLayout.addView(textView)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoriaProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}