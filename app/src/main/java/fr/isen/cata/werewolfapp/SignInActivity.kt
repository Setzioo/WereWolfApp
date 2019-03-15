package fr.isen.cata.werewolfapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var loginPrefs: SharedPreferences? = null
    val loginFilename: String = "loginPreferences"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        rememberMeCheckBox.isChecked = true
        buttonEffect(loginButton)
        loginPrefs = this.getSharedPreferences(loginFilename, 0)

        if (loginPrefs!=null)
        {
            val instantEmail = loginPrefs!!.getString("email","")
            val instantPassword = loginPrefs!!.getString("password","")
            emailContainerIn.setText(instantEmail)
            passwordContainerIn.setText(instantPassword)
        }

        returnSignInButton.setOnClickListener{
            finish()
        }

        var email:String
        var password:String

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener{

            email = emailContainerIn.text.toString()
            password = passwordContainerIn.text.toString()
            val editor = loginPrefs!!.edit()

            if (rememberMeCheckBox.isChecked)
            {
                saveLoginPreferences(email, password, editor)
            }
            else
            {
                clearLoginPreferences(editor)
            }

            logIn(email, password)
        }
    }

    private fun clearLoginPreferences(editor: SharedPreferences.Editor) {
        editor.clear()
        editor.apply()
    }

    private fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, start HomeActivity
                    Log.d("TAG", "signInWithEmail:success")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveLoginPreferences(email: String, password: String, editor: SharedPreferences.Editor) {
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    fun buttonEffect(button: View) {
        var color = Color.parseColor("#514e4e")
        button.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }
}