package com.example.sgdatingapp.Activity

import android.app.Notification
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sgdatingapp.Adapter.MessageAdapter
import com.example.sgdatingapp.Modal.MessageModal
import com.example.sgdatingapp.Modal.userModal
import com.example.sgdatingapp.R
import com.example.sgdatingapp.databinding.ActivityMessageBinding
import com.example.sgdatingapp.notification_model.NotificationData
import com.example.sgdatingapp.notification_model.PushNotification
import com.example.sgdatingapp.notification_model.api.ApiUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getData(intent.getStringExtra("chat_id"))


        verifyChatId()
        binding.imageView4.setOnClickListener {
            if (binding.yourMessage.text!!.isEmpty()) {
                Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show()
            } else {
                storedata(binding.yourMessage.text.toString())
            }
        }
    }

    private var senderId: String? = null
    private var chatid: String? = null
    private var receiverid: String? = null

    private fun verifyChatId() {

        receiverid = intent.getStringExtra("userId")
        senderId = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        chatid = senderId + receiverid
        val reverseChatId = receiverid + senderId


        val reference = FirebaseDatabase.getInstance().getReference("chats")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.hasChild(chatid!!)) {
                    getData(chatid)
                } else if (snapshot.hasChild(reverseChatId!!)) {
                    chatid = reverseChatId
                    getData(chatid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()

            }

        })

    }

    private fun getData(chatId: String?) {
        FirebaseDatabase.getInstance().getReference("chats")
            .child(chatId!!).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val list = arrayListOf<MessageModal>()

                        for (show in snapshot.children) {
                            list.add(show.getValue(MessageModal::class.java)!!)
                        }
                        binding.recyclerView2.adapter = MessageAdapter(this@MessageActivity, list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MessageActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            )
    }


    private fun storedata(msg: String) {

        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date())

        val map = hashMapOf<String, String>()
        map["message"] = msg
        map["senderId"] = senderId!!
        map["currentTime"] = currentTime
        map["currentDate"] = currentDate


        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatid!!)

        reference.child(reference.push().key!!).setValue(map).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.yourMessage.text = null


                sendNotification(msg)
                Toast.makeText(this, "Message Sended", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun sendNotification(msg: String) {

        FirebaseDatabase.getInstance().getReference("users")
            .child(receiverid!!).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            val data = snapshot.getValue(userModal::class.java)

                            val notificationData =
                                PushNotification(
                                    NotificationData("New Message", msg),
                                    data!!.fcmToken
                                )

                            ApiUtilities.getInstance().sendNotification(
                                notificationData
                            ).enqueue(object :Callback<PushNotification>{
                                override fun onResponse(
                                    call: Call<PushNotification>,
                                    response: Response<PushNotification>
                                ) {
                                    Toast.makeText(this@MessageActivity, "Notification Sended", Toast.LENGTH_SHORT)
                                        .show()
                                }

                                override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                    Toast.makeText(this@MessageActivity, "Something went wrong", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MessageActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )


    }
}