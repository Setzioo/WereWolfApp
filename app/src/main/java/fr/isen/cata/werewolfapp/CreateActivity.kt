package fr.isen.cata.werewolfapp

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create.*

class CreateActivity : AppCompatActivity() {

    private var nbPlayer = 4
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null
    private var nameValid: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        buttonEffect(createButton)
        auth = FirebaseAuth.getInstance()
        val id: String = auth.currentUser!!.uid

        val mUserReference = FirebaseDatabase.getInstance().getReference("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        user.add(i.getValue(PlayerModel::class.java))
                    }
                    for (i in user) {
                        if (i?.id == id) {
                            currentPlayer = i
                            Log.e("teeesssesseeeese", currentPlayer!!.pseudo)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

        val lobbies: ArrayList<LobbyModel?> = ArrayList()

        val mLobbyReference = FirebaseDatabase.getInstance().getReference("Lobby")

        mLobbyReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lobbies.clear()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        lobbies.add(i.getValue(LobbyModel::class.java))
                    }
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })





        val partys: ArrayList<PartyModel?> = ArrayList()

        val mPartyReference = FirebaseDatabase.getInstance().getReference("Party")

        mPartyReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                partys.clear()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        partys.add(i.getValue(PartyModel::class.java))
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
        nbPlayerView.text = nbPlayer.toString()

        addButton.setOnClickListener {
            if (nbPlayer < 18) {
                nbPlayer += 1
            } else {
                Toast.makeText(this, "maximum 18 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }

        removeButton.setOnClickListener {
            if (nbPlayer > 4) {
                nbPlayer -= 1
            } else {
                Toast.makeText(this, "minimum 4 joueurs!", Toast.LENGTH_LONG).show()
            }
            nbPlayerView.text = nbPlayer.toString()
        }

        createButton.setOnClickListener {
            val nameLobby: String = partyNameView.text.toString()
            if (nameLobby == "") {
                nameValid = false
                Toast.makeText(this, "Rentrez un nom!!", Toast.LENGTH_LONG).show()
            }
            for (lobby in lobbies) {
                if (nameLobby == lobby!!.name) {
                    nameValid = false
                    Toast.makeText(this, "Attention ce nom existe déjà!", Toast.LENGTH_LONG).show()
                }
            }
            for (party in partys) {
                if (nameLobby == party!!.name) {
                    nameValid = false
                    Toast.makeText(this, "Attention ce nom existe déjà, la partie est en cours!", Toast.LENGTH_LONG).show()
                }
            }
            if (nameValid) {
                Log.e("lobbyyyyyyyyyyyy", "est tu là")
                onCreateLobby()
            }
            nameValid = true
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
        val color = Color.parseColor("#514e4e")
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
