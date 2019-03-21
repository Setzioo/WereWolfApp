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

    private val myPermissionsRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val permissionNotGranted = getAllPermissionNotGranted()

        ActivityCompat.requestPermissions(this, permissionNotGranted, myPermissionsRequestCode)

        animateLaunchText()


        launcherLayout.setOnClickListener {

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
        valueAnimator.duration = 1000
        valueAnimator.repeatCount = INFINITE
        valueAnimator.repeatMode = REVERSE

        valueAnimator.start()
    }

    private fun getAllPermissionNotGranted(): Array<String> {

        return arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.VIBRATE)

    }

}
