package com.example.mobilem1androidapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ServerInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_info)

        // Retrieve the data passed via Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Unknown User"
        val serverInfo = intent.getStringExtra("SERVER_INFO") ?: "{}"

        // Parse the server information and display it
        val parsedServerInfo = parseServerInfo(serverInfo, userName)

        // Find the TextView and set the formatted text
        val tvServerInfo: TextView = findViewById(R.id.tvServerInfo)
        tvServerInfo.text = parsedServerInfo
    }

    // Parse server information from the JSON string
    private fun parseServerInfo(serverInfo: String, userName: String): String {
        return try {
            val jsonObject = JSONObject(serverInfo)
            val serverIp = jsonObject.getString("server_ip")
            val clientIp = jsonObject.getString("client_ip")
            val serverTime = jsonObject.getString("server_time")
            val nameObject = jsonObject.getJSONObject("name")
            val firstName = nameObject.getString("first")
            val lastName = nameObject.getString("last")

            """
            Server IP address: $serverIp
            Client IP address: $clientIp

            Server local time: $serverTime
            Client local time: $serverTime

            My name: $firstName $lastName
            Logged in: $userName
            """.trimIndent()
        } catch (e: Exception) {
            "Error parsing server info: ${e.message}"
        }
    }
}
