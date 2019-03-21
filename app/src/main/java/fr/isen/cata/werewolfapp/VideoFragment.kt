package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_video.*


class VideoFragment : Fragment() {

    val compteurMax: Long = 5


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                compteur.text = timeLeft
            }

            override fun onFinish() {
                val goText = "GO"
                compteur.text = goText
                Handler().postDelayed({
                    val manager = MyFragmentManager()
                    manager.characterFragment(context!!)
                }, 1500)
            }
        }.start()

    }


    companion object {
        fun newInstance() = VideoFragment()
    }
}
