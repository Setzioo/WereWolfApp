package fr.isen.cata.werewolfapp

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        animateLaunchText()

        launcherLayout.setOnClickListener { v: View ->

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun animateLaunchText() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            LaunchText.alpha = value
        }

        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.duration = 1700
        valueAnimator.repeatCount = INFINITE
        valueAnimator.repeatMode = REVERSE

        valueAnimator.start()
    }

}
