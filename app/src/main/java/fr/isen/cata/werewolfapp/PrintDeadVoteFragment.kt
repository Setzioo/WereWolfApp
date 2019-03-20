package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_print_dead.*

class PrintDeadVoteFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: PrintDeadAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("FUN", "Affichage des morts")
        Log.e("LANCE", "Print dead")


        mDatabase = FirebaseDatabase.getInstance().reference

        deadRecyclerView.layoutManager = GridLayoutManager(context!!, 2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = PrintDeadAdapter(players)
        deadRecyclerView.adapter = adapter

        getDeadPlayers(players)

    }

    private fun getDeadPlayers(players: ArrayList<PlayerModel?>) {

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
                                if (!user.state) {
                                    players.add(user)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                Thread.sleep(6000)

                mDatabase.child("Party").child(gameName).child("Flags").child("endPrint").setValue(true)
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
        return inflater.inflate(R.layout.fragment_print_dead, container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        fun newInstance() = PrintDeadVoteFragment()
    }
}