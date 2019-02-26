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

        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")



        mLobbyReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var lobby: LobbyModel? = null
                if (dataSnapshot.exists()) {

                    //Log.e("TAG", "changeData")
                    lobby = dataSnapshot.getValue(LobbyModel::class.java)

                    //Log.e("TAG", lobby?.name.toString())

                    //Log.e("TAG", "changeData")
                    lobby = dataSnapshot.child("Lobby/lobbytest").getValue(LobbyModel::class.java)

                    //Log.e("TAG", lobby?.name)
                    var playerList : MutableList<PlayerModel?> = arrayListOf()
                    for (user in dataSnapshot.child("Users").children) {
                        playerList.add(user.getValue(PlayerModel::class.java))
                    }

                    attributeRole(lobby, playerList)

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
            var id = it.id
            var nbPlayer = lobby.nbPlayer
            var listPlayer = lobby.listPlayer
            /*var nbPlayer = 4
            var listPlayer = listOf<String>(
            "95gfhBLmLWef4soYEESPiIgSIXI3",
            "UECUWHd6DJOopvadKfHcBoY9JSy1",
            "f5lJpGohtZhC4ZygEGK4sywc3yz1",
            "fYSWKCs5PHfz6bhTYPD4nAOWUkl1"
        )*/
            val listPlayerInGame: List<PlayerModel?> = listOfUser.filter { user -> listPlayer!!.contains(user?.id) }

            var roleList = getRandomRoles(nbPlayer)

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

