package fr.isen.cata.werewolfapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_vote_jour.*


class VoteJourFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var jourAdapter: JourAdapter

    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    private val compteurMax: Long = 10
    var isAlivePlayer: Boolean = false
    var isMasterPlayer: Boolean = true
    val players: ArrayList<PlayerModel?> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Vote Jour")


        mDatabase = FirebaseDatabase.getInstance().reference
        Log.e("FUN", "Vote Jour")
        Toast.makeText(context, "Vote Jour", Toast.LENGTH_LONG).show()

        initVotes()

        getPeople(players)

        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                jourTiming.text = timeLeft
            }

            override fun onFinish() {
                jourTiming.text = "0"
                Handler().postDelayed({
                    Log.e("VOTE JOUR", "findu vote")
                    endOfVote()
                }, 1500)
            }
        }.start()


    }

    private fun endOfVote() {

        val mUserReference = FirebaseDatabase.getInstance().getReference("")
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

        var idToKill = ""
        var equality = false
        var nbVotesMax = 0

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
                            if(currentPlayer!!.state) {
                                isAlivePlayer = true
                            }
                        }
                    }
                }
                if(isAlivePlayer) {
                    if (dataSnapshot.exists()) {
                        game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                        if (game != null) {
                            if (game!!.listPlayer != null) {
                                listId = game!!.listPlayer
                                if(currentPlayer!!.id == game!!.masterId) {
                                    isMasterPlayer = true
                                }
                            }
                        }
                    }
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.nbVotesJour > nbVotesMax) {
                                        nbVotesMax = user.nbVotesJour
                                        equality = false
                                        idToKill = user.id
                                    } else if (user.nbVotesJour == nbVotesMax) {
                                        equality = true
                                    }
                                }
                            }
                        }
                    }
                    if (!equality) {
                        mUserReference.child("Users").child(idToKill).child("state").setValue(false)
                    }

                    if(isMasterPlayer)
                    {
                        mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoteFlag").setValue(true)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


    private fun getPeople(players: ArrayList<PlayerModel?>) {

        val mUserReference = FirebaseDatabase.getInstance().getReference("")
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

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
                            if(currentPlayer!!.state) {
                                isAlivePlayer = true
                            }
                        }
                    }
                }
                if(isAlivePlayer) {
                    if (dataSnapshot.exists()) {
                        game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                        if (game != null) {
                            if (game!!.listPlayer != null) {
                                listId = game!!.listPlayer
                                if(currentPlayer!!.id == game!!.masterId) {
                                    isMasterPlayer = true
                                }
                            }
                        }
                    }
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.id != currentPlayer!!.id && user.state) {
                                        players.add(user)
                                        voteRecyclerView.layoutManager = GridLayoutManager(context!!, 2)
                                        jourAdapter = JourAdapter(players)
                                        voteRecyclerView.adapter = jourAdapter
                                        jourAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    noPlayerAliveMessage.text = "Les vivants votent..."
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun initVotes() {
        val mUserReference = mDatabase.child("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        mDatabase.child("Users").child(tempPlayer!!.id).child("nbVotesJour").setValue(0)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote_jour, container, false)
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
        fun newInstance() = VoteJourFragment()
    }
}

