package fr.isen.cata.werewolfapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*

class LobbyActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")

        mLobbyReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var lobby: LobbyModel? = null
                if (dataSnapshot.exists()) {
                    lobby = dataSnapshot.child("Lobby/lobbytest").getValue(LobbyModel::class.java)

                    if(lobby!!.startGame){
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
            }
            listPlayerInGame.forEach {
                mDatabase = FirebaseDatabase.getInstance().reference.child("")
                mDatabase.child("Users").child(it?.id.toString()).child("role").setValue(it?.role)
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
}
