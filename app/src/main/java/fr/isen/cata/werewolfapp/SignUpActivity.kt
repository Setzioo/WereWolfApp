package fr.isen.cata.werewolfapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_user_settings.*
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        buttonEffect(signButton)
        returnSignUpButton.setOnClickListener{
            finish()
        }

        var email:String
        var password:String

        auth = FirebaseAuth.getInstance()

        signButton.setOnClickListener{

            email = emailContainerUp.text.toString()
            password = passwordContainerUp.text.toString()
            if(email !="" && password !="") {
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
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
             }
            else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    baseContext, "Authentication failed",
                    Toast.LENGTH_SHORT
                ).show()}
        }

    }

    private fun createUser(userId: String) {
        val userPseudo = "Wawa nudiste"
        val userTest = PlayerModel(userId, userPseudo)
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("Users").child(userId).setValue(userTest)
        saveAvatar()
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

    private fun saveAvatar() {
        // Create a storage reference from our app
        val storage = FirebaseStorage.getInstance()

        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(auth.currentUser!!.uid + "/avatar")
        // Get the data from an ImageView as bytes
        val bitmap = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.icon_avatar
        )

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {

        }.addOnSuccessListener {

        }
    }
}
