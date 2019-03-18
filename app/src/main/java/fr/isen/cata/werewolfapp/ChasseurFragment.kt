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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chasseur.*
import kotlinx.android.synthetic.main.fragment_loup.*


class ChasseurFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: ChasseurAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        Log.e("FUN", "CHASSEUR")
        Toast.makeText(context, "Chasseur", Toast.LENGTH_LONG).show()

        chasseurRecyclerView.layoutManager = GridLayoutManager(context!!,2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = ChasseurAdapter(players)
        chasseurRecyclerView.adapter = adapter

        getVillagers(players)

    }

    private fun getVillagers(players: ArrayList<PlayerModel?>) {
        val mUserReference = mDatabase.child("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    players.clear()
                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        mDatabase.child("Users").child(tempPlayer!!.id).child("nbVotesLoup").setValue(0)
                        players.add(tempPlayer)
                        adapter.notifyDataSetChanged()
                    }


                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
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
