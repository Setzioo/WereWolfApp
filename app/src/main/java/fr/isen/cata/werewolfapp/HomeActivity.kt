package fr.isen.cata.werewolfapp

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        buttonEffect(createButton)
        buttonEffect(joinButton)
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
