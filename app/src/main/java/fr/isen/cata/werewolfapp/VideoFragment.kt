package fr.isen.cata.werewolfapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = "android.resource://" + activity?.packageName + "/" + R.raw.coutdown

        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
        videoView.setOnCompletionListener {
            val manager = MyFragmentManager()
            manager.CharacterFragment(context!!)
        }
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
