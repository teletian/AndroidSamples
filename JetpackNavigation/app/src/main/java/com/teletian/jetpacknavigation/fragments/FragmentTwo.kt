package com.teletian.jetpacknavigation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.teletian.jetpacknavigation.R

class FragmentTwo: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        view.findViewById<MaterialButton>(R.id.navigate).setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_two_to_three)
        }
        view.findViewById<MaterialButton>(R.id.back).setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        return view
    }
}