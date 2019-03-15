package fr.isen.cata.werewolfapp

import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.VideoView
import kotlinx.android.synthetic.main.activity_launcher.*
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.FileDescriptor
import java.io.PrintWriter


class VideoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
        animateLaunchText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = "android.resource://" + activity?.packageName + "/" + R.raw.coutdown
/*
        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
        videoView.setOnCompletionListener {
            val manager = MyFragmentManager()
            manager.CharacterFragment(context!!)
        }*/
        object : CountDownTimer(5000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                compteur.setText("" + (millisUntilFinished / 1000+1))
            }

            override fun onFinish() {
                compteur.setText("GO")
            }
        }.start()
        animateLaunchText()
    }
    private fun animateLaunchText() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            compteur.alpha = value
        }

        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.duration = 548
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.start()
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


    companion object {
        fun newInstance() = VideoFragment()
    }
}
