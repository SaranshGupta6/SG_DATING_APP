package com.example.sgdatingapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.sgdatingapp.Activity.EditProfileActivity
import com.example.sgdatingapp.Auth.LoginActivity
import com.example.sgdatingapp.Modal.userModal
import com.example.sgdatingapp.R
import com.example.sgdatingapp.databinding.FragmentProfileBinding
import com.example.sgdatingapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {


    private lateinit var binding :FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        Config.showDialog(requireContext())

        binding = FragmentProfileBinding.inflate(layoutInflater)


        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val data = it.getValue(userModal::class.java)

                    binding.name.editText!!.setText(data!!.name.toString())
                    binding.city.editText!!.setText(data.city.toString())
                    binding.email.editText!!.setText(data.email.toString())
                    binding.number.editText!!.setText(data.number.toString())


                    val img= data.image
                    Glide.with(requireContext()).load(img).placeholder(R.drawable.user).into(binding.userImage)
                    Config.hideDialog()
                }
            }


        binding.logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.editProfile.setOnClickListener {

            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }


        return binding.root
    }

}