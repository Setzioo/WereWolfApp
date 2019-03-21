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
import kotlinx.android.synthetic.main.fragment_voyante.*


class VoyanteFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: VoyanteAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    var isVoyantePlayer: Boolean = false
    val players: ArrayList<PlayerModel?> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        mDatabase = FirebaseDatabase.getInstance().reference

        getVillagers(players)
    }


    fun beginCompteur(compteurMax: Long) {
        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                voyanteTimer.text = timeLeft
            }

            override fun onFinish() {
                voyanteTimer.text = "0"
                Handler().postDelayed({
                    val manager = MyFragmentManager()
                    manager.visionFragment(context!!)
                }, 1500)
            }
        }.start()
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
                            if (currentPlayer!!.role == "Voyante" && currentPlayer!!.state) {
                                isVoyantePlayer = true
                            } else {
                                val laVoyanteJoueText = "La voyante joue..."
                                voyanteTextView.text = laVoyanteJoueText
                            }
                        }
                    }
                }
                if (isVoyantePlayer) {
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
                                    if (user.id != currentPlayer!!.id && user.state) {
                                        voyanteRecyclerView.layoutManager = GridLayoutManager(context!!, 2)
                                        adapter = VoyanteAdapter(players)
                                        voyanteRecyclerView.adapter = adapter
                                        players.add(user)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
                beginCompteur(10)
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
        return inflater.inflate(R.layout.fragment_voyante, container, false)
    }


    companion object {
        fun newInstance() = VoyanteFragment()
    }
}

