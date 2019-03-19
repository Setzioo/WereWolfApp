package fr.isen.cata.werewolfapp

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sorciere_vie.*


class SorciereMortFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var wolfKill = ""
    var deadPlayer: PlayerModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "Sorciere", Toast.LENGTH_LONG).show()

        buttonEffect(ResurrectButton)
        buttonEffect(letHimDieButton)

        mDatabase = FirebaseDatabase.getInstance().reference


    }



    private fun getPlayerAvatar(player: PlayerModel) {
        val storageReference = FirebaseStorage.getInstance().reference.child(player.id + "/avatar")

        storageReference.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            Picasso.get()
                .load(it)
                .into(deadFace)
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    fun buttonEffect(button: View) {
        val color = Color.parseColor("#514e4e")
        button.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sorciere_mort, container, false)
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
        fun newInstance() = SorciereMortFragment()
    }
}


