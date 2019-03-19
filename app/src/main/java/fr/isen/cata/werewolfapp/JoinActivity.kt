package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val lobbies: ArrayList<LobbyModel?> = ArrayList()

        lobbyView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val adapter = LobbyAdapter(lobbies)
        lobbyView.adapter = adapter

        val mLobbyReference = FirebaseDatabase.getInstance().getReference("Lobby")

        mLobbyReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val user: MutableList<LobbyModel?> = arrayListOf()
                lobbies.clear()
                if (dataSnapshot.exists()) {

                    for(i in dataSnapshot.children){
                        //user.add(i.getValue(LobbyModel::class.java))
                        lobbies.add(i.getValue(LobbyModel::class.java))
                        (lobbyView.adapter as LobbyAdapter).notifyDataSetChanged()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

    }
}
