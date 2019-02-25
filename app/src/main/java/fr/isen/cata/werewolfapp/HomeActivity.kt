package fr.isen.cata.werewolfapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        decoButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        settingButton.setOnClickListener{
            val intent = Intent(this, UserSettingsActivity::class.java)
            startActivity(intent)
        }

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }

        joinButton.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }
    }
}
