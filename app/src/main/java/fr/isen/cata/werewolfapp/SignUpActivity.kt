package fr.isen.cata.werewolfapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        returnSignUpButton.setOnClickListener{
            finish()
        }

        var email:String
        var password:String

        auth = FirebaseAuth.getInstance()

        signButton.setOnClickListener{

            email = emailContainerUp.text.toString()
            password = passwordContainerUp.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, start HomeActivity
                        Log.d("TAG", "createUserWithEmail:success")

                        val id: String = auth.currentUser!!.uid
                        createUser(id)

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }

    private fun createUser(userId: String) {
        val userPseudo = "Wawa nudiste"
        val userTest = PlayerModel(userId, userPseudo)
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("Users").child(userId).setValue(userTest)
    }
}
