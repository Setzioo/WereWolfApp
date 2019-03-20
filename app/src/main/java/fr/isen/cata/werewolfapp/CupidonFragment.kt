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
import kotlinx.android.synthetic.main.fragment_cupidon.*


class CupidonFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: CupidonAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    var isCupidonPlayer: Boolean = false
    val players: ArrayList<PlayerModel?> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Cupidon")

        Toast.makeText(context, "Cupidon", Toast.LENGTH_LONG).show()

        mDatabase = FirebaseDatabase.getInstance().reference

        getVillagers(players)
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
                            if(currentPlayer!!.role == "Cupidon"){
                                isCupidonPlayer = true
                            }
                        }
                    }
                }
                if(isCupidonPlayer){
                    if (dataSnapshot.exists()) {
                        game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                        if (game != null) {
                            if (game!!.listPlayer != null) {
                                listId = game!!.listPlayer
                            }
                        }
                    }
                    if (listId != null) {
                        players.clear()
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val user = u.getValue(PlayerModel::class.java)
                                if (i == user!!.id) {
                                    if (user.state) {
                                        players.add(user)
                                        cupidonRecyclerView.layoutManager = GridLayoutManager(context!!, 2)
                                        adapter = CupidonAdapter(players)
                                        cupidonRecyclerView.adapter = adapter
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    cupidonTextView.text = "Cupidon fait son choix..."
                }
                beginCompteur(10)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    fun beginCompteur(compteurMax: Long) {
        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = "" + (millisUntilFinished / 1000 + 1)
                cupidonTimer.text = timeLeft
            }

            override fun onFinish() {
                cupidonTimer.text = "0"
                Handler().postDelayed({
                    if(isCupidonPlayer){
                        lovePlayers()
                    }
                }, 1500)
            }
        }.start()
    }

    private fun lovePlayers() {
        if (adapter.victimPlayer != null && adapter.victimPlayer2 != null) {
            val mDatabase = FirebaseDatabase.getInstance().reference
            mDatabase.child("Users").child(adapter.victimPlayer!!.id).child("selected").setValue(false)
            mDatabase.child("Users").child(adapter.victimPlayer2!!.id).child("selected").setValue(false)
            mDatabase.child("Users").child(adapter.victimPlayer!!.id).child("inLove").setValue(true)
            mDatabase.child("Users").child(adapter.victimPlayer2!!.id).child("inLove").setValue(true)
        } else if (adapter.victimPlayer != null) {
            mDatabase.child("Users").child(adapter.victimPlayer!!.id).child("selected").setValue(false)
        } else if (adapter.victimPlayer2 != null) {
            mDatabase.child("Users").child(adapter.victimPlayer2!!.id).child("selected").setValue(false)
        }
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("CupidonFlag").setValue(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cupidon, container, false)
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
        fun newInstance() = CupidonFragment()
    }
}

