package fr.isen.cata.werewolfapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.android.synthetic.main.activity_user_settings.*

class LobbyActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        /*
        auth = FirebaseAuth.getInstance()
        getCurrentPlayer()


        val players: ArrayList<PlayerModel?> = ArrayList()

        playerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val adapter = PlayerAdapter(players)
        playerView.adapter = adapter*/
/*
        val mPlayerReference = FirebaseDatabase.getInstance().getReference("Users")

        mPlayerReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(i in dataSnapshot.children){
                        players.add(i.getValue(PlayerModel::class.java))
                        (playerView.adapter as PlayerAdapter).notifyDataSetChanged()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })*/

        /*startGame.setOnClickListener() {
            startGame()
        }*/
    }

    private fun startGame(){
        mDatabase = FirebaseDatabase.getInstance().reference.child("")

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var lobby: LobbyModel? = null
                if (dataSnapshot.exists()) {
                    lobby = getLobby()
                    if(lobby!!.masterId == currentPlayer!!.id){
                        val playerList : MutableList<PlayerModel?> = arrayListOf()

                        for (user in dataSnapshot.child("Users").children) {
                            playerList.add(user.getValue(PlayerModel::class.java))
                        }

                        attributeRole(lobby, playerList)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }

    private fun attributeRole(lobby: LobbyModel?, listOfUser: MutableList<PlayerModel?>) {
        Log.e("RECCUP", "attribute")
        lobby?.let {
            val nbPlayer = lobby.nbPlayer
            val listPlayer = lobby.listPlayer
            val listPlayerInGame: List<PlayerModel?> = listOfUser.filter { user -> listPlayer!!.contains(user?.id) }

            val roleList = getRandomRoles(nbPlayer)

            listPlayerInGame.forEachIndexed { key, player ->
                player?.role = roleList[key].name
                Log.e("ROLE", "player: "+player?.pseudo+" role : "+player?.role)
            }
            listPlayerInGame.forEach {
                mDatabase = FirebaseDatabase.getInstance().reference.child("")
                mDatabase.child("Users").child(it?.id.toString()).child("role").setValue(it?.role)
                mDatabase.child("Users").child(it?.id.toString()).child("charmed").setValue(false)
                mDatabase.child("Users").child(it?.id.toString()).child("state").setValue(true)
                if(it?.role == "Sorcière"){
                    mDatabase.child("Users").child(it?.id.toString()).child("deathPotion").setValue(true)
                    mDatabase.child("Users").child(it?.id.toString()).child("lifePotion").setValue(true)
                }
            }
        }
    }

    private fun getRandomRoles(nbPlayer: Int): ArrayList<RoleModel> {
        val list = when (nbPlayer) {
            4 -> arrayListOf(
                LoupGarou(),
                Villageois(), Villageois(),
                Voyante()
            )

            5 -> arrayListOf(
                LoupGarou(),
                Villageois(), Villageois(),
                Voyante(),
                Chasseur()
            )

            6 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(),
                Voyante(),
                Chasseur()
            )

            7 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur()
            )

            8 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur()
            )

            9 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur(),
                Cupidon()
            )


            10 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur(),
                Cupidon(),
                Ange()
            )

            11 -> arrayListOf(
                LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur(),
                Cupidon(),
                Sorciere(true, true)
            )

            12 -> arrayListOf(
                LoupGarou(), LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur(),
                Cupidon(),
                Ange(),
                Pipoteur()
            )

            13 -> arrayListOf(
                LoupGarou(), LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Chasseur(),
                Cupidon(),
                Sorciere(true, true),
                Pipoteur()
            )

            14 -> arrayListOf(
                LoupGarou(), LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Ange(),
                Cupidon(),
                Chasseur(),
                Pipoteur()
            )

            15 -> arrayListOf(
                LoupGarou(), LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Cupidon(),
                Chasseur(),
                Sorciere(true, true),
                Pipoteur()
            )

            16 -> arrayListOf(
                LoupGarou(),
                LoupGarou(),
                LoupGarou(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Voyante(),
                Ange(),
                Cupidon(),
                Chasseur(),
                Sorciere(true, true),
                Pipoteur()
            )

            17 -> arrayListOf(
                LoupGarou(), LoupGarou(), LoupGarou(), LoupGarou(),
                Villageois(), Villageois(), Villageois(), Villageois(), Villageois(), Villageois(), Villageois(),
                Voyante(),
                Ange(),
                Cupidon(),
                Chasseur(),
                Sorciere(true, true),
                Pipoteur()
            )

            18 -> arrayListOf(
                LoupGarou(),
                LoupGarou(),
                LoupGarou(),
                LoupGarou(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Villageois(),
                Voyante(),
                Ange(),
                Cupidon(),
                Chasseur(),
                Sorciere(true, true),
                Pipoteur()
            )
            else -> arrayListOf(LoupGarou(), Voyante(), Villageois(), Villageois())
        }
        list.shuffle()
        return list
    }

    private fun getCurrentPlayer() {

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

                            pseudoText.text = currentPlayer!!.pseudo
                            Log.d("USERID------", currentPlayer!!.id)

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun getLobby(): LobbyModel?{

        var lobbbyRef : String? = currentPlayer!!.currentGame
        var lobby: LobbyModel? = null
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")

        mLobbyReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    lobby = dataSnapshot.child("Lobby"+lobbbyRef).getValue(LobbyModel::class.java)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }

        })
        return lobby

    }
}
