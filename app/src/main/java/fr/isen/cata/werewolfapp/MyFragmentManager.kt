package fr.isen.cata.werewolfapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity

class MyFragmentManager {

    fun BeginningFragment(context: Context) {
        val fragment: Fragment?

        fragment = VideoFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
    fun CharacterFragment(context: Context){
        val fragment: Fragment?
        fragment = CharacterFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)

            .commit()


        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(v.hasVibrator())
        {
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            else
            {
                v.vibrate(2000)
            }

        }
    }
    fun CupidonFragment(context: Context){

    }
    fun SleepFragment(context: Context){
    }
    fun VoyanteFragment(context : Context){

    }
    fun LoupsFragment(context: Context){

    }
    fun SorciereFragment(context: Context, deathPotion : Boolean, lifePotion : Boolean){

    }
    fun PipoteurFragment(context: Context){

    }
    fun ChasseurFragment(context: Context){

    }
    fun VoteJourFragment(context : Context){

    }
}