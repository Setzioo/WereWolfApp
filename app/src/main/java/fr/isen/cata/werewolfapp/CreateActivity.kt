package fr.isen.cata.werewolfapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create.*

class CreateActivity : AppCompatActivity() {

    private var nbPlayer = 4
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

        val mUserReference = FirebaseDatabase.getInstance().getReference("Users")

        mUserReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for(i in dataSnapshot.children){
                        user.add(i.getValue(PlayerModel::class.java))
                    }
                    for(i in user){
                        if(i?.id == id){
                            currentPlayer = i
                            Log.d("USERID------", currentPlayer!!.id)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

        nbPlayerView.text = nbPlayer.toString()

        returnButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        addButton.setOnClickListener {
            if(nbPlayer < 18) {
                nbPlayer += 1
            } else {
                Toast.makeText(this, "maximum 18 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }

        removeButton.setOnClickListener {
            if(nbPlayer > 4){
                nbPlayer -= 1
            } else {
                Toast.makeText(this, "minimum 4 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }

        createButton.setOnClickListener {
            onCreateLobby()
        }
    }

    private fun onCreateLobby() {
        val partyName: String = partyNameView.text.toString()
        val mDatabase = FirebaseDatabase.getInstance().reference
        var listPlayer1: MutableList<String> = arrayListOf()
        listPlayer1.add(currentPlayer!!.id)
        mDatabase.child("Users").child(currentPlayer!!.id).child("currentGame").setValue(partyName)
        val lobbyTest = LobbyModel(currentPlayer!!.id, partyName, nbPlayer, listPlayer1)
        mDatabase.child("Lobby").child(partyName).setValue(lobbyTest)

        val intent = Intent(this, LobbyActivity::class.java)
        startActivity(intent)
    }
}
