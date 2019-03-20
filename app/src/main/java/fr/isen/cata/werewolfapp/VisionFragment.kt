package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_vision.*

class VisionFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    private var selectedPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    var pileOfTurn: MutableList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Vision")


        mDatabase = FirebaseDatabase.getInstance().reference

        findSelectedPlayer()
    }

    private fun findSelectedPlayer() {

        val mUserReference = FirebaseDatabase.getInstance().getReference("")
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid
        var onePlayerSelected = false

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.child("Users").children) {
                        users.add(i.getValue(PlayerModel::class.java))
                    }
                    for (i in users) {
                        if (i?.id == id) {
                            currentPlayer = i
                            gameName = currentPlayer!!.currentGame!!
                        }
                    }
                }
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    if (game != null) {
                        if (game!!.listPlayer != null) {
                            listId = game!!.listPlayer
                            pileOfTurn = game!!.pileOfTurn
                            pileOfTurn.removeAt(0)
                        }
                    }
                }
                if (listId != null) {
                    for (i in listId!!) {
                        for (u in dataSnapshot.child("Users").children) {
                            val user = u.getValue(PlayerModel::class.java)
                            if (i == user!!.id) {
                                if (user.selected) {
                                    selectedPlayer = user
                                    selectedPlayerPseudo.text = selectedPlayer!!.pseudo
                                    val estText = "est"
                                    messageText.text = estText
                                    selectedPlayerRole.text = selectedPlayer!!.role
                                    changeCardImage(selectedPlayer!!.role)
                                    mDatabase.child("Users").child(selectedPlayer!!.id).child("selected")
                                        .setValue(false)
                                    onePlayerSelected = true
                                    beginCompteur(5)
                                }
                            }
                        }
                    }
                }
                if (!onePlayerSelected) {
                    val tooMuchTimeText = "Trop tard ! Vous avez pris trop de temps pour choisir!    Rendormez-vous!"
                    messageText.text = tooMuchTimeText
                    beginCompteur(5)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    fun beginCompteur(compteurMax: Long) {
        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                //val timeLeft = "" + (millisUntilFinished / 1000)
                //visionTimer.text = timeLeft
            }

            override fun onFinish() {
                //visionTimer.text = "0"
                Handler().postDelayed({
                    mDatabase.child("Party").child(gameName).child("pileOfTurn").setValue(pileOfTurn)
                }, 1500)
            }
        }.start()
    }

    fun changeCardImage(role: String?) {
        if (role == "Sorcière") {
            selectedPlayerCard.setImageResource(R.drawable.sorciere)
        }
        if (role == "Villageois") {
            selectedPlayerCard.setImageResource(R.drawable.villageaois)
        }
        if (role == "Loup-Garou") {
            selectedPlayerCard.setImageResource(R.drawable.loup_garou)
        }
        if (role == "Cupidon") {
            selectedPlayerCard.setImageResource(R.drawable.cupidon)
        }
        if (role == "Chasseur") {
            selectedPlayerCard.setImageResource(R.drawable.chasseur)
        }
        if (role == "Pipoteur") {
            selectedPlayerCard.setImageResource(R.drawable.pipoteur)
        }
        if (role == "Ange") {
            selectedPlayerCard.setImageResource(R.drawable.ange)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vision, container, false)
    }


    companion object {
        fun newInstance() = VisionFragment()
    }
}