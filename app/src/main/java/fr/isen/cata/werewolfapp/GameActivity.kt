package fr.isen.cata.werewolfapp

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.widget.VideoView
import android.support.v4.os.HandlerCompat.postDelayed



class GameActivity : AppCompatActivity() {
var story =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


     /*   val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

          // Vibrate for 400 milliseconds
          v.vibrate(400)*/
        Handler().postDelayed({
            CharacterFragment()
        },7000)

        VideoFragment()
       // Thread.sleep(10_000)
        //setContentView(R.layout.layout_character)
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
    }
    fun VideoFragment() {
        var fragment: Fragment?


        fragment = VideoFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)

            .commit()
    }
    fun CharacterFragment(){
        var fragment: Fragment?
        fragment = CharacterFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)

            .commit()
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(2000)}


}
