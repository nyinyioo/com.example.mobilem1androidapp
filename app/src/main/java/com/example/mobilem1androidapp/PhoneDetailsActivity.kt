package com.example.mobilem1androidapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PhoneDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_details)

        // Get data passed from the main activity
        val phoneDetails = intent.getStringExtra("PHONE_DETAILS")

        // Display phone details in the TextView
        val txtDetails: TextView = findViewById(R.id.txtDetails)
        txtDetails.text = phoneDetails

        // Back Button Logic
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}