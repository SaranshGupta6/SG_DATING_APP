package com.example.sgdatingapp.Auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sgdatingapp.MainActivity
import com.example.sgdatingapp.Modal.userModal
import com.example.sgdatingapp.R
import com.example.sgdatingapp.databinding.ActivityRegisterBinding
import com.example.sgdatingapp.utils.Config
import com.example.sgdatingapp.utils.Config.hideDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding


    private var imageUri: Uri ?= null
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent())
    {
        imageUri= it

        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.saveData.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        if(binding.userName.text.toString().isEmpty() || binding.userEmail.text.toString().isEmpty() ||
            binding.userCity.text.toString().isEmpty() || imageUri==null){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()}

        else if(!binding.termsCondition.isChecked){
            Toast.makeText(this, "Please accept Terms and Condition", Toast.LENGTH_SHORT).show()
        }

        else
        {
            uploadImage()
        }

    }

    private fun uploadImage() {
        Config.showDialog(this)
        val storageRef= FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile.jpg")
            storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    storeData(it)
                }
                    .addOnFailureListener {
                        hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }

    private fun storeData(imageUrl: Uri?) {

        var token :String?=null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
           val  token = task.result


            val data = userModal(
                name = binding.userName.text.toString(),
                image = imageUrl.toString(),
                number = binding.userNumber.text.toString() ,
                email = binding.userEmail.text.toString() ,
                city = binding.userCity.text.toString(),
                fcmToken = token
            )

            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .setValue(data).addOnCompleteListener {
                    hideDialog()
                    if (it.isSuccessful) {
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                        Toast.makeText(this, "User register successful", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

        })



    }
}
