package fr.isen.cata.werewolfapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
//import com.firebase.ui.auth.data.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create.*

class CreateActivity : AppCompatActivity() {

    private var nbPlayer = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

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
            onCreate()
        }
    }

    private fun onCreate() {
        val partyName: String = partyNameView.text.toString()
        val idParty = 6

        val mDatabase = FirebaseDatabase.getInstance().reference
        val  listPlayer1: List<Int> = emptyList()
        //val lobbyTest = LobbyModel(idParty, nbPlayer, listPlayer1, 5, partyName)
        //mDatabase.child("Lobby").child(partyName).setValue(lobbyTest)

    }


}
