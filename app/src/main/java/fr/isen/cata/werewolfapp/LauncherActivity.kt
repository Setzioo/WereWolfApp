package fr.isen.cata.werewolfapp

import android.Manifest
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_launcher.*


class LauncherActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val permissionNotGranted = getAllPermissionNotGranted()

        ActivityCompat.requestPermissions(this, permissionNotGranted, MY_PERMISSIONS_REQUEST)
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

/*
        val mp = MediaPlayer.create (this, R.raw.victoire_pipoteur)
        mp.setVolume(0.5f, 0.5f)
        mp.start ()

        val mp2 = MediaPlayer.create (this, R.raw.village_endort)
        mp2.setVolume(0.7f, 0.7f)
        mp2.start ()*/
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

        launcherLayout.setOnClickListener {

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
        valueAnimator.duration = 1000
        valueAnimator.repeatCount = INFINITE
        valueAnimator.repeatMode = REVERSE

        valueAnimator.start()
    }

    private fun getAllPermissionNotGranted(): Array<String> {

        return arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.VIBRATE)

    }

}
