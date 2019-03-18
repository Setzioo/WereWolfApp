package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.android.synthetic.main.fragment_loup.*

//TODO : Vote parmi les vivants


class LoupFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: LoupAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        Log.e("FUN", "LOUP")
        Toast.makeText(context, "Loups", Toast.LENGTH_LONG).show()

        loupRecyclerView.layoutManager = GridLayoutManager(context!!,2)

        val players: ArrayList<PlayerModel?> = ArrayList()

        adapter = LoupAdapter(players)
        loupRecyclerView.adapter = adapter

        initVotes()

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
