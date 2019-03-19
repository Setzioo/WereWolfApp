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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chasseur.*


class ChasseurFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: ChasseurAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Chasseur")


        mDatabase = FirebaseDatabase.getInstance().reference

        chasseurRecyclerView.layoutManager = GridLayoutManager(context!!, 2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = ChasseurAdapter(players)
        chasseurRecyclerView.adapter = adapter

        getVillagers(players)
    }

    fun beginCompteur(compteurMax: Long) {
        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                chasseurTimer.text = timeLeft
            }

            override fun onFinish() {
                chasseurTimer.text = "0"
                Handler().postDelayed({
                    killPlayer(adapter.victimPlayer)
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
                        }
                    }
                }
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
                                    players.add(user)
                                    adapter.notifyDataSetChanged()
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

    private fun killPlayer(victimPlayer: PlayerModel?) {
        if (victimPlayer != null) {
            val mDatabase = FirebaseDatabase.getInstance().reference
            mDatabase.child("Users").child(victimPlayer.id).child("state").setValue(false)
            mDatabase.child("Users").child(victimPlayer.id).child("selected").setValue(false)
            mDatabase.child("Party").child(victimPlayer.currentGame!!).child("FinishFlags").child("ChasseurFlag")
                .setValue(true)
            Toast.makeText(context, adapter.victimPlayer!!.pseudo + " est mort", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Trop tard, vous avez mis trop de temps...", Toast.LENGTH_LONG).show()
        }
        val manager = MyFragmentManager()
        manager.NightFragment(context!!)
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(context, "Chasseur", Toast.LENGTH_LONG).show()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chasseur, container, false)
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
        fun newInstance() = ChasseurFragment()
    }
}
