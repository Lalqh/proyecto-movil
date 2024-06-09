package com.example.proyecto.ui.usuario.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class AddUserFragment : Fragment() {

    companion object {
        fun newInstance() = AddUserFragment()
    }

    private lateinit var viewModel: AddUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddUserViewModel::class.java)
        // TODO: Use the ViewModel
    }

}