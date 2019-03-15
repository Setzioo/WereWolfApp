package fr.isen.cata.werewolfapp

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create.*

class CreateActivity : AppCompatActivity() {

    private var nbPlayer = 1
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        buttonEffect(createButton)
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

        addButton.setOnClickListener {
            if(nbPlayer < 18) {
                nbPlayer += 1
            } else {
                Toast.makeText(this, "maximum 18 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }

        removeButton.setOnClickListener {
            if(nbPlayer > 1){
                nbPlayer -= 1
            } else {
                Toast.makeText(this, "minimum 4 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }


            createButton.setOnClickListener {
                var nameloby:String
                nameloby = partyNameView.text.toString()
                if(nameloby != "") {
                onCreateLobby()
            }

                else{
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext, "Creation failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun onCreateLobby() {
        val partyName: String = partyNameView.text.toString()
        val mDatabase = FirebaseDatabase.getInstance().reference
        val listPlayer1: MutableList<String> = arrayListOf()
        listPlayer1.add(currentPlayer!!.id)
        mDatabase.child("Users").child(currentPlayer!!.id).child("currentGame").setValue(partyName)
        mDatabase.child("Users").child(currentPlayer!!.id).child("inLobby").setValue(true)
        val lobbyTest = LobbyModel(currentPlayer!!.id, partyName, nbPlayer, listPlayer1)
        mDatabase.child("Lobby").child(partyName).setValue(lobbyTest)

        val intent = Intent(this, LobbyActivity::class.java)
        startActivity(intent)
    }
    fun buttonEffect(button: View) {
        var color = Color.parseColor("#514e4e")
        button.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }
}
