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
        val fragment: Fragment?

        fragment = CupidonFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun NightFragment(context: Context){
        val fragment: Fragment?

        fragment = NightFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun VoyanteFragment(context : Context){
        val fragment: Fragment?

        fragment = VoyanteFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun LoupsFragment(context: Context){
        val fragment: Fragment?

        fragment = LoupFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun SorciereFragment(context: Context, deathPotion : Boolean, lifePotion : Boolean){
        val fragment: Fragment?

    fun DebutNuitFragment(context: Context) {
        val fragment: Fragment?

        fragment = DebutNuitFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

        fragment = SorciereFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun PipoteurFragment(context: Context){
        val fragment: Fragment?

        fragment = PipoteurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun ChasseurFragment(context: Context){
        val fragment: Fragment?

        fragment = ChasseurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
    fun VoteJourFragment(context : Context){
        val fragment: Fragment?

        fragment = VoteJourFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
}