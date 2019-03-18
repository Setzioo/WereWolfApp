package fr.isen.cata.werewolfapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_voyante.*


class VoyanteFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: VoyanteAdapter
    private lateinit var auth: FirebaseAuth

    private var currentPlayer: PlayerModel? = null
    var gameName : String =""
    var game : PartyModel? = null
    var listId : MutableList<String>? = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference

        voyanteRecyclerView.layoutManager = GridLayoutManager(context!!,2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = VoyanteAdapter(players)
        voyanteRecyclerView.adapter = adapter

        getVillagers(players)

        lookButton.setOnClickListener {
            lookAtPlayer(adapter.victimPlayer)
        }

    }

    private fun lookAtPlayer(victimPlayer: PlayerModel?) {
        if (victimPlayer != null) {
            val mDatabase = FirebaseDatabase.getInstance().reference
            //mDatabase.child("Users").child(victimPlayer.id).child("state").setValue(false)
            mDatabase.child("Party").child(victimPlayer.currentGame!!).child("FinishFlags").child("VoyanteFlag").setValue(true)
            Toast.makeText(context, adapter.victimPlayer!!.pseudo + " est observé", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Choisissez un joueur", Toast.LENGTH_LONG).show()
        }
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
                            if(i == user!!.id){
                                if(user.id != currentPlayer!!.id && user.state) {
                                    players.add(user)
                                    adapter.notifyDataSetChanged()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voyante, container, false)
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
        fun newInstance() = VoyanteFragment()
    }
}

