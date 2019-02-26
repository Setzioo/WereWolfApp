package fr.isen.cata.werewolfapp

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import kotlinx.android.synthetic.main.activity_launcher.*
import android.os.CountDownTimer



class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        animateLaunchText()
        /*val mp = MediaPlayer.create (this, R.raw.sf_meute)
        mp.start ()*/
       /* animateCards()
        object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                LaunchText.setText("seconds remaining: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                LaunchText.setText("done!")
            }
        }.start()*/

        launcherLayout.setOnClickListener { v: View ->

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
   /* private fun animateCards() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 720f)

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float

            Cards.rotation =value

        }
        val valueAnimator1 = ValueAnimator.ofFloat(0f, 1f)

        valueAnimator1.addUpdateListener {
            val value = it.animatedValue as Float
            Cards.alpha=value


        }


        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 2000
        valueAnimator1.interpolator = AccelerateInterpolator()
        valueAnimator1.duration = 2000



        valueAnimator.start()
        valueAnimator1.start()
    }*/

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
