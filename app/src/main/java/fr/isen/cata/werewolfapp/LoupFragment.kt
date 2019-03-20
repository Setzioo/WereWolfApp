package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_loup.*

class LoupFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var loupAdapter: LoupAdapter
    private lateinit var wolfieAdapter: WolfieAdapter

    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    private val compteurMax: Long = 15
    var isLoupPlayer: Boolean = false
    var isAlivePlayer: Boolean = false
    var isMasterPlayer: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Loup")
        Log.e("LANCE","Loup")


        mDatabase = FirebaseDatabase.getInstance().reference
        Log.e("FUN", "LOUP")

        loupRecyclerView.layoutManager = GridLayoutManager(context!!, 2)
        wolfies.layoutManager = GridLayoutManager(context!!, 1, GridLayoutManager.HORIZONTAL, false)

        val players: ArrayList<PlayerModel?> = ArrayList()
        val wolfs: ArrayList<PlayerModel?> = ArrayList()

        loupAdapter = LoupAdapter(players)
        wolfieAdapter = WolfieAdapter(wolfs)

        loupRecyclerView.adapter = loupAdapter
        wolfies.adapter = wolfieAdapter

        initVotes()

        getVillagers(players)

        getWerewolves(wolfs)

        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                wolfieTiming.text = timeLeft
            }

            override fun onFinish() {
                wolfieTiming.text = "0"
                Handler().postDelayed({
                    endOfWolfiesTurn()
                }, 1500)
            }
        }.start()


    }

    private fun endOfWolfiesTurn() {

        val mDatabaseReference = FirebaseDatabase.getInstance().getReference("")
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

        var idToKill = ""
        var equality = false
        var nbVotesMax = 0

        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
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
                            if(currentPlayer!!.role == "Loup-Garou") {
                                isLoupPlayer = true
                            }
                            if(currentPlayer!!.state) {
                                isAlivePlayer = true
                            }
                        }
                    }
                }
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    if(game!!.masterId == currentPlayer!!.id){
                        isMasterPlayer = true
                    }
                    if (game != null) {
                        if (game!!.listPlayer != null) {
                            listId = game!!.listPlayer
                        }
                    }
                }
                if(isAlivePlayer && isLoupPlayer) {
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.nbVotesLoup > nbVotesMax) {
                                        nbVotesMax = user.nbVotesLoup
                                        equality = false
                                        idToKill = user.id
                                    } else if (user.nbVotesLoup == nbVotesMax) {
                                        equality = true
                                    }
                                }
                            }
                        }
                    }

                    if (!equality) {
                        mDatabaseReference.child("Users").child(idToKill).child("state").setValue(false)
                        mDatabase.child("Party").child(gameName).child("wolfKill").setValue(idToKill)
                    }
                    else
                    {
                        mDatabase.child("Party").child(gameName).child("wolfKill").setValue("")
                    }
                }
                if(isMasterPlayer) {
                    mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag").setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


    private fun getVillagers(players: ArrayList<PlayerModel?>) {

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
                            if(currentPlayer!!.role == "Loup-Garou") {
                                isLoupPlayer = true
                            }
                            if(currentPlayer!!.state) {
                                isAlivePlayer = true
                            }
                        }
                    }
                }
                if(isAlivePlayer && isLoupPlayer){
                    if (dataSnapshot.exists()) {
                        game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                        if (game != null) {
                            if (game!!.listPlayer != null) {
                                listId = game!!.listPlayer
                            }
                        }
                    }
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.id != currentPlayer!!.id && user.state && user.role != "Loup-Garou") {
                                        players.add(user)
                                        loupAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    noWolfMessage.text = "Les loups jouent..."
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun getWerewolves(players: ArrayList<PlayerModel?>) {

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
                            if(currentPlayer!!.role == "Loup-Garou") {
                                isLoupPlayer = true
                            }
                            if(currentPlayer!!.state) {
                                isAlivePlayer = true
                            }
                        }
                    }
                }
                if(isLoupPlayer && isAlivePlayer) {
                    if (dataSnapshot.exists()) {
                        game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                        if (game != null) {
                            if (game!!.listPlayer != null) {
                                listId = game!!.listPlayer
                            }
                        }
                    }
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.state && user.role == "Loup-Garou") {
                                        players.add(user)
                                        wolfieAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
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
                        mDatabase.child("Users").child(tempPlayer!!.id).child("nbVotesLoup").setValue(0)
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
        return inflater.inflate(R.layout.fragment_loup, container, false)
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
        fun newInstance() = LoupFragment()
    }
}
