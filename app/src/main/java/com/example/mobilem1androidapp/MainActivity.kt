package com.example.mobilem1androidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import okhttp3.*
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener {
    // UI Components
    lateinit var btnCity: Button
    lateinit var btnPhoneDetails: Button
    lateinit var btnServer: Button
    lateinit var btnTimer: Button

    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient

    // HTTP Client
    private val client = OkHttpClient()

    companion object {
        const val RC_SIGN_IN = 1001
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize buttons
        btnCity = findViewById(R.id.btn_city)
        btnPhoneDetails = findViewById(R.id.btn_phDetails)
        btnServer = findViewById(R.id.btn_server)
        btnTimer = findViewById(R.id.btn_timer)

        btnCity.setOnClickListener(this)
        btnPhoneDetails.setOnClickListener(this)
        btnServer.setOnClickListener(this)
        btnTimer.setOnClickListener(this)

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Task 1: My Favorite City
            R.id.btn_city -> openFavoriteCity()

            // Task 2: My Phone Details
            R.id.btn_phDetails -> fetchPhoneDetails()

            // Task 3: Login and Server Info
            R.id.btn_server -> signIn()

            // Task 4: Timer
            R.id.btn_timer -> openTimerActivity()
        }
    }

    // Task 1: Open Favorite City in Google Maps
    private fun openFavoriteCity() {
        val latitude = 51.5074
        val longitude = -0.1278
        val cityName = "London"
        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($cityName)")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(browserIntent)
        }
    }

    private fun fetchPhoneDetails() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        var cityName = "Unknown City"

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    cityName = addresses[0].locality ?: "Unknown City"
                }
            } else {
                Toast.makeText(this, "Unable to determine location", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request location permission if not already granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return // Exit the function; result will be handled in onRequestPermissionsResult
        }

        // Display phone details
        val phoneDetails = "City: $cityName\nManufacturer: $manufacturer\nModel: $model"
        val intent = Intent(this, PhoneDetailsActivity::class.java)
        intent.putExtra("PHONE_DETAILS", phoneDetails)
        startActivity(intent)
    }


    // Task 3: Google Sign-In
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val userName = account?.displayName ?: "Unknown User"
                fetchServerInfo(userName)
            } catch (e: ApiException) {
                // Handle specific sign-in errors
                val errorMessage = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign-in was canceled. Please try again."
                    GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error. Please check your internet connection."
                    GoogleSignInStatusCodes.DEVELOPER_ERROR -> "Developer error. Check your configuration in the Google API Console."
                    else -> "Login failed: ${e.message}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch server information
    private fun fetchServerInfo(userName: String) {
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/api/info")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Server request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        runOnUiThread {
                            val intent = Intent(this@MainActivity, ServerInfoActivity::class.java)
                            intent.putExtra("USER_NAME", userName)
                            intent.putExtra("SERVER_INFO", responseBody)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Empty server response", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Server error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Task 4: Open Timer Activity
    private fun openTimerActivity() {
        val intent = Intent(this, TimerActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d("Permissions", "Location permission granted. Fetching details.")
                fetchPhoneDetails()
            } else {
                // Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // User denied permission but didn't select "Don't ask again"
                    Toast.makeText(this, "Location permission is required to fetch city details.", Toast.LENGTH_SHORT).show()
                } else {
                    // User selected "Don't ask again"
                    Toast.makeText(this, "Location permission permanently denied. Enable it in settings.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
