package fr.isen.cata.werewolfapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_launcher.*
import kotlinx.android.synthetic.main.activity_login.*

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        launcherLayout.setOnClickListener { v: View ->

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
