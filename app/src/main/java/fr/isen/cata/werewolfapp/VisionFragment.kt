package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_vision.*

class VisionFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    private var selectedPlayer: PlayerModel?= null
    var gameName : String =""
    var game : PartyModel? = null
    var listId : MutableList<String>? = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findSelectedPlayer()
    }

    private fun findSelectedPlayer() {

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
                    if(game!=null){
                        if(game!!.listPlayer != null){
                            listId = game!!.listPlayer
                        }
                    }
                }
                if (listId != null) {
                    for(i in listId!!){
                        for(u in dataSnapshot.child("Users").children){
                            val user = u.getValue(PlayerModel::class.java)
                            if(i == user!!.id) {
                                if(user.selected){
                                    selectedPlayer = user
                                    selectedPlayerPseudo.text = selectedPlayer!!.pseudo
                                    selectedPlayerRole.text = selectedPlayer!!.role
                                    changeCardImage(selectedPlayer!!.role)
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

    fun changeCardImage(role: String?) {
        if(role == "Sorcière") {
            selectedPlayerCard.setImageResource(R.drawable.sorciere)
        }
        if(role == "Villageois"){
            selectedPlayerCard.setImageResource(R.drawable.villageaois)
        }
        if(role == "Loup-Garou"){
            selectedPlayerCard.setImageResource(R.drawable.loup_garou)
        }
        if(role == "Cupidon"){
            selectedPlayerCard.setImageResource(R.drawable.cupidon)
        }
        if(role == "Chasseur"){
            selectedPlayerCard.setImageResource(R.drawable.chasseur)
        }
        if(role == "Pipoteur"){
            selectedPlayerCard.setImageResource(R.drawable.pipoteur)
        }
        if(role == "Ange"){
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun newInstance() = VisionFragment()
    }
}