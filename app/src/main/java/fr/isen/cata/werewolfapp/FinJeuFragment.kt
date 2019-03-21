package fr.isen.cata.werewolfapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
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
                    winnerNumber = game!!.winner
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
                                        if(user.role == "Villageois" || user.role == "Voyante" || user.role == "Cupidon" || user.role == "Chasseur" || user.role == "Sorciere" || user.role == "Ange"){
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
                                    }
                                }
                            }
                        }
                    }
                }
                displayMessageWin()
                Handler().postDelayed({
                    val manager = MyFragmentManager()
                    manager.resultFragment(context!!)
                }, 7000)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun displayMessageWin() {
        val ontGagne = "ont gagné !"
        val aGagne = "a gagné !"
        when(winnerNumber) {
            1-> {
                val lesAmoureux = "Les amoureux"
                roleMessageFin.text = lesAmoureux
                roleMessage2Fin.text = ontGagne
                backgroundFin.setBackgroundResource(R.drawable.cupidon_coeur_2)
            }
            2 -> {
                val lePipoteur = "Le pipoteur"
                roleMessageFin.text = lePipoteur
                roleMessage2Fin.text = aGagne
                roleMessageFin.setTextColor(Color.BLACK)
                roleMessage2Fin.setTextColor(Color.BLACK)
                backgroundFin.setBackgroundResource(R.drawable.victoire_pipoteur_2)
            }
            3 -> {
                val lesVillageois = "Les villageois"
                roleMessageFin.text = lesVillageois
                roleMessage2Fin.text = ontGagne
                backgroundFin.setBackgroundResource(R.drawable.victoire_village)
            }
            4 -> {
                val lAnge = "L'ange"
                roleMessageFin.text = lAnge
                roleMessage2Fin.text = aGagne
                backgroundFin.setBackgroundResource(R.drawable.victoire_ange_background)
            }
            5 -> {
                val lesLoupGarous = "Les loup-garou"
                roleMessageFin.text = lesLoupGarous
                roleMessage2Fin.text = ontGagne
                backgroundFin.setBackgroundResource(R.drawable.victoire_loup_garou)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fin_jeu, container, false)

    }

    companion object {
        fun newInstance() = FinJeuFragment()
    }
}