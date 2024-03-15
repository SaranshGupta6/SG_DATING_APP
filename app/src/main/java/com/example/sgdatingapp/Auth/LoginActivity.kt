package com.example.sgdatingapp.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sgdatingapp.MainActivity
import com.example.sgdatingapp.R
import com.example.sgdatingapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    val auth = FirebaseAuth.getInstance()
    private var verificationId: String?=null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this).setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()
        binding.sendotp.setOnClickListener{
            if (binding.usernumber.text!!.isEmpty())
            {
                binding.usernumber.error="Please Enter Your Number"
            }
            else

            {
                sendOtp(binding.usernumber.text.toString())
            }
        }

        binding.verifyotp.setOnClickListener{
            if (binding.userotp.text!!.isEmpty())
            {
                binding.userotp.error="Please Enter Your OTP"
            }
            else

            {
                verifyOtp(binding.userotp.text.toString())
            }
        }
    }

    private fun verifyOtp(otp: String) {
       // binding.sendotp.showLoadingButton()
        dialog.show()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun sendOtp(number: String) {
      //  binding.sendotp.showLoadingButton()

        try{
            dialog.show()
        }catch(e:Exception){

        }
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        //       binding.sendotp.showNormalButton()
                signInWithPhoneAuthCredential(credential)
               // dialog.dismiss()
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@LoginActivity.verificationId = verificationId
                    dialog.dismiss()
            //    binding.sendotp.showNormalButton()
                binding.numberlayout.visibility=GONE
                binding.otplayout.visibility= VISIBLE
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$number")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
             //   binding.sendotp.showNormalButton()
                if (task.isSuccessful) {
                    
                    checkUserExist(binding.usernumber.text.toString())
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun checkUserExist(number:String) {
        FirebaseDatabase.getInstance().getReference("users").child("+91$number")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError){
                    dialog.dismiss()

                    var token :String?=null
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        token = task.result


                    })

                    Toast.makeText(this@LoginActivity,p0.message,Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot){
                    if(p0.exists()){
                        try {
                            dialog.dismiss()
                        }catch(e:Exception){

                        }
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }
                    else
                    {
                        startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
                        finish()
                    }
                }
            })
    }
}