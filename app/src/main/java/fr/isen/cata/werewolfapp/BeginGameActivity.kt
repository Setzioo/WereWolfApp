package fr.isen.cata.werewolfapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError





class BeginGameActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin_game)
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("Lobby")
        Log.e("TAG", mLobbyReference.toString())

        var listPlayer = arrayListOf<Int>()
        listPlayer.add(3)
        listPlayer.add(2)
        //val lobbyTest : LobbyModel = LobbyModel(3, 2, "lobby de test", listPlayer)
        /*mLobbyReference.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })*/


        mLobbyReference.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var lobby: LobbyModel? = null
                if(dataSnapshot.exists()){
                    Log.e("TAG", "changeData")
                    lobby = dataSnapshot.getValue(LobbyModel::class.java)

                    Log.e("TAG", lobby?.name.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }


    private fun attributeRole() {

    }
}
