package com.example.sgdatingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.sgdatingapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity()  , NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding:ActivityMainBinding
    var actionBarDrawerToggle: ActionBarDrawerToggle?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarDrawerToggle= ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val navController= findNavController(R.id.fragment)

        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rateUs -> {
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show()
            }
            R.id.shareApp -> {
                Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
            }
            R.id.terms -> {
                Toast.makeText(this, "T&C", Toast.LENGTH_SHORT).show()
            }
            R.id.privacyPolicy -> {
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
            }
            R.id.developer -> {
                Toast.makeText(this, "Developer", Toast.LENGTH_SHORT).show()
            }
            R.id.favourite -> {
                Toast.makeText(this, "Favourite", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else {
            super.onContextItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.close()
        }
        else
            super.onBackPressed()
    }

}