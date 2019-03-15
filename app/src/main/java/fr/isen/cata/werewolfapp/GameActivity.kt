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


//TODO : Dans l'ordre a faire : Loup Voyante Chasseur Cupi Sorciere Pipoteur


class GameActivity : AppCompatActivity() {

    /* Roles actuels : Loup  Villageois  Voyante  Ange  Cupidon  Chasseur  Sorciere  Pipoteur

        Ordre de jeu la nuit:
            - Cupidon (Première nuit seulement)
            - Voyante
            - Loups
            - Sorciere
            - Pipoteur (1 nuit sur 2)
     */

    var nbTour : Int = 0

    var story =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val manager = MyFragmentManager()
        manager.fragmentCupidonLauncher(this)




     /*   val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

          // Vibrate for 400 milliseconds
          v.vibrate(400)*/


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

}
