package com.example.mobilem1androidapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServerInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_info)

        val userName = intent.getStringExtra("USER_NAME")
        val serverInfo = intent.getStringExtra("SERVER_INFO")

        val tvServerInfo: TextView = findViewById(R.id.tvServerInfo)
        tvServerInfo.text = "User: $userName\n\nServer Info:\n$serverInfo"
    }
}
