package fr.isen.cata.werewolfapp

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_launcher.*
import android.os.CountDownTimer
import android.widget.VideoView
import android.content.Context.VIBRATOR_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator



class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    /*
      val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Vibrate for 400 milliseconds
        v.vibrate(400)
*/

        /*_________VIDEO_________
        val videoView = findViewById<VideoView>(R.id.videoView)
        val path = "android.resource://" + packageName + "/" + R.raw.coutdown
        videoView?.setVideoURI(Uri.parse(path))
        videoView.start()*/
        animateLaunchText()
        /*_________SON_______
        val mp = MediaPlayer.create (this, R.raw.sf_meute)
        mp.start ()*/
       /* ________CHRONO_______
       animateCards()
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
   /* ____________________ANIMATION CARD___________
   private fun animateCards() {
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
