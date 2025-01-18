package com.example.mobilem1androidapp
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var minutesInput: EditText
    private lateinit var secondsInput: EditText
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.`timer_activity`)

        // Initialize views
        timerTextView = findViewById(R.id.txtTimer)
        startButton = findViewById(R.id.btnStartTimer)
        minutesInput = findViewById(R.id.etMinutes)
        secondsInput = findViewById(R.id.etSeconds)

        // Start Button Click Listener
        startButton.setOnClickListener {
            val minutes = minutesInput.text.toString().toIntOrNull() ?: 0
            val seconds = secondsInput.text.toString().toIntOrNull() ?: 0
            val totalMillis = (minutes * 60 + seconds) * 1000L

            if (totalMillis > 0) {
                startCountdown(totalMillis)
            } else {
                Toast.makeText(this, "Please enter a valid duration!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCountdown(totalMillis: Long) {
        // Cancel any existing timer
        countDownTimer?.cancel()

        // Create a new timer
        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                Toast.makeText(this@TimerActivity, "Timer Finished!", Toast.LENGTH_SHORT).show()

                // Add any additional functionality here (e.g., show an image or play a sound)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel timer to avoid memory leaks
        countDownTimer?.cancel()
    }
}