package se.gritacademy.lifecyklev2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Save the image URI to SharedPreferences
            sharedPreferences.edit().putString("profileImageUri", uri.toString()).apply()
            // Set the ImageView to display the selected image
            profileImageView.setImageURI(uri)
            Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Withdraw the data from SharedPreferences.
        profileImageView = findViewById(R.id.profileImageView)
        val changeImageButton = findViewById<Button>(R.id.changeImageButton)
        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        // Load saved profile image, if available
        val savedImageUri = sharedPreferences.getString("profileImageUri", null)
        if (savedImageUri != null) {
            profileImageView.setImageURI(Uri.parse(savedImageUri))
        }

        changeImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // navigate back to login
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val ageEditText = findViewById<EditText>(R.id.ageInputField)
        val emailEditText = findViewById<EditText>(R.id.emailInputField)
        val hasLicenseCheckBox = findViewById<CheckBox>(R.id.hasLicenseCheckBox)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Prepare the data that already is added.
        ageEditText.setText(sharedPreferences.getString("age", ""))
        emailEditText.setText(sharedPreferences.getString("email", ""))
        hasLicenseCheckBox.isChecked = sharedPreferences.getBoolean("hasLicense", false)
        val savedGender = sharedPreferences.getString("gender", null)
        if (savedGender != null) {
            when (savedGender) {
                "Male" -> findViewById<RadioButton>(R.id.maleRadioButton).isChecked = true
                "Female" -> findViewById<RadioButton>(R.id.femaleRadioButton).isChecked = true
                "Other" -> findViewById<RadioButton>(R.id.otherRadioButton).isChecked = true
                else -> Toast.makeText(this, "Unknown gender saved!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No gender found in SharedPreferences!", Toast.LENGTH_SHORT).show()
        }


        submitButton.setOnClickListener {
            val age = ageEditText.text.toString()
            val email = emailEditText.text.toString()
            val hasLicense = hasLicenseCheckBox.isChecked
            val genderId = genderRadioGroup.checkedRadioButtonId
            val gender = findViewById<RadioButton>(genderId)?.text.toString()

            // Save the information without the picture to shared preferences.
            sharedPreferences.edit().apply {
                putString("age", age)
                putString("email", email)
                putBoolean("hasLicense", hasLicense)
                putString("gender", gender)
                apply()
            }
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()

        }
    }
}