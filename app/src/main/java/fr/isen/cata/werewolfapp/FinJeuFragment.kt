package fr.isen.cata.werewolfapp

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_fin_jeu.*

class FinJeuFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: FinJeuAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var listId: MutableList<String>? = arrayListOf()
    var winnerNumber: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("FUN", "Affichage des gagnats")
        Log.e("LANCE", "Fin du jeu")

        mDatabase = FirebaseDatabase.getInstance().reference

        finRecyclerView.layoutManager = GridLayoutManager(context!!, 2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = FinJeuAdapter(players)
        finRecyclerView.adapter = adapter

        getWinners(players)

    }

    private fun getWinners(players: ArrayList<PlayerModel?>) {

        val mUserReference = FirebaseDatabase.getInstance().getReference("")
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

        Log.e("FUN", "Test 1")

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
                    Log.e("FUN", "Test 2")
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    winnerNumber = game!!.winner
                    if (game != null) {
                        if (game!!.listPlayer != null) {
                            listId = game!!.listPlayer
                        }
                    }
                }
                Log.e("FUN", "Test 3")
                if (listId != null) {
                    Log.e("FUN", "Test 4")
                    for (i in listId!!) {
                        Log.e("FUN", "Test 5")
                        for (u in dataSnapshot.child("Users").children) {
                            Log.e("FUN", "Test 6")
                            val user = u.getValue(PlayerModel::class.java)
                            if (i == user!!.id) {
                                Log.e("FUN", "Test 7")
                                when(winnerNumber) {
                                    1-> {
                                        if(user.inLove){
                                            players.add(user)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                    2 -> {
                                        if(user.role == "Pipoteur"){
                                            players.add(user)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                    3 -> {
                                        if(user.role == "Villageois" || user.role == "Voyante" || user.role == "Cupidon" || user.role == "Chasseur" || user.role == "Sorcière"){
                                            players.add(user)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                    4 -> {
                                        if(user.role == "Ange"){
                                            players.add(user)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                    5 -> {
                                        if(user.role == "Loup-Garou"){
                                            players.add(user)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                    else -> {
                                        Log.e("FUN", "winnerNumber : " + winnerNumber)
                                    }
                                }
                            }
                        }
                    }
                }
                displayMessageWin()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun displayMessageWin() {
        when(winnerNumber) {
            1-> {
                roleMessageFin.text = "Les amoureux"
                roleMessage2Fin.text = "ont gagné !"
                backgroundFin.setBackgroundResource(R.drawable.cupidon_coeur_2)
            }
            2 -> {
                roleMessageFin.text = "Le pipoteur"
                roleMessage2Fin.text = "a gagné !"
                roleMessageFin.setTextColor(Color.BLACK)
                roleMessage2Fin.setTextColor(Color.BLACK)
                backgroundFin.setBackgroundResource(R.drawable.victoire_pipoteur_2)
            }
            3 -> {
                roleMessageFin.text = "Les villageois"
                roleMessage2Fin.text = "ont gagné !"
                backgroundFin.setBackgroundResource(R.drawable.victoire_village)
            }
            4 -> {
                roleMessageFin.text = "L'ange"
                roleMessage2Fin.text = "a gagné !"
                backgroundFin.setBackgroundResource(R.drawable.victoire_ange_background)
            }
            5 -> {
                roleMessageFin.text = "Les loup-garou"
                roleMessage2Fin.text = "ont gagné !"
                backgroundFin.setBackgroundResource(R.drawable.victoire_loup_garou)
            }
        }
        Thread.sleep(10000)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fin_jeu, container, false)

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
        fun newInstance() = FinJeuFragment()
    }
}