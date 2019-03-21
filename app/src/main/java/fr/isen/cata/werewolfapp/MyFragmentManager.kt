package fr.isen.cata.werewolfapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

class MyFragmentManager {

    fun beginningFragment(context: Context) {
        val fragment: Fragment?


        fragment = VideoFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    fun characterFragment(context: Context) {
        val fragment: Fragment?

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

    fun cupidonFragment(context: Context) {
        val fragment: Fragment?

        fragment = CupidonFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }


    fun voyanteFragment(context: Context) {
        val fragment: Fragment?

        fragment = VoyanteFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun visionFragment(context: Context) {
        val fragment: Fragment?

        fragment = VisionFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun loupsFragment(context: Context) {
        val fragment: Fragment?

        fragment = LoupFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun sorciereVieFragment(context: Context) {
        val fragment: Fragment?

        fragment = SorciereVieFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun sorciereMortFragment(context: Context) {
        val fragment: Fragment?

        fragment = SorciereMortFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun pipoteurFragment(context: Context) {
        val fragment: Fragment?

        fragment = PipoteurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun chasseurFragment(context: Context) {
        val fragment: Fragment?

        fragment = ChasseurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun voteJourFragment(context: Context) {
        val fragment: Fragment?

        fragment = VoteJourFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }


    fun loveFragment(context: Context) {
        val fragment: Fragment?
        fragment = LoveFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun pipotedFragment(context: Context) {
        val fragment: Fragment?

        fragment = PipotedFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun finJeuFragment(context: Context) {
        val fragment: Fragment?

        fragment = FinJeuFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }


    fun printDeadNightFragment(context: Context) {
        val fragment: Fragment?

        fragment = PrintDeadNightFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun printDeadVoteFragment(context: Context) {
        val fragment: Fragment?

        fragment = PrintDeadVoteFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun printDeadChasseurFragment(context: Context) {
        val fragment: Fragment?

        fragment = PrintDeadChasseurFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }

    fun resultFragment(context: Context) {
        val fragment: Fragment?

        fragment = ResultFragment.newInstance()
        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commitAllowingStateLoss()
    }


}