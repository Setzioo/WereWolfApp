package fr.isen.cata.werewolfapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MyFragmentManager {

    fun BeginningFragment(context: Context) {
        val fragment: Fragment?

        Log.e("MANGER","Beginning")

        fragment = VideoFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    fun CharacterFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Character")

        fragment = CharacterFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)

            .commit()


        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(2000)
            }

        }
    }

    fun CupidonFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Cupidon")

        fragment = CupidonFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun NightFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Night")

        fragment = NightFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun VoyanteFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Voyante")

        fragment = VoyanteFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun VisionFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Vision")

        fragment = VisionFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun LoupsFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Loups")

        fragment = LoupFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun SorciereVieFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Sorciere Vie")

        fragment = SorciereVieFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun SorciereMortFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Sorciere Mort")

        fragment = SorciereMortFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun PipoteurFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Pipoteur")

        fragment = PipoteurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun ChasseurFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Chasseur")

        fragment = ChasseurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun VoteJourFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Vote Jour")

        fragment = VoteJourFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun DayFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Day")

        fragment = DayFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun LoveFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Love")
        fragment = LoveFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun PipotedFragment(context: Context) {
        val fragment: Fragment?
        Log.e("MANGER","Pipoted")

        fragment = PipotedFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun FinJeuFragment(context: Context){
        val fragment: Fragment?
        Log.e("MANGER","Fin Jeu")

        fragment = FinJeuFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun PrintDeadNightFragment(context: Context){
        val fragment: Fragment?
        Log.e("MANGER","Print Dead")

        fragment = PrintDeadNightFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun PrintDeadVoteFragment(context: Context){
        val fragment: Fragment?
        Log.e("MANGER","Print Dead")

        fragment = PrintDeadVoteFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun PrintDeadChasseurFragment(context: Context){
        val fragment: Fragment?
        Log.e("MANGER","Print Dead")

        fragment = PrintDeadChasseurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }
}