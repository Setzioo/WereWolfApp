package fr.isen.cata.werewolfapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Random




class BeginGameActivity : AppCompatActivity() {

    private lateinit var mPlayersRef: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference
    val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin_game)
        var rand : Int

        var lobby: LobbyModel? = null
        var playerList: MutableList<PlayerModel?> = arrayListOf()

        mLobbyReference = FirebaseDatabase.getInstance().reference.child("Lobby/arnolobby")
        //Log.e("TAG", "rand : " + rand)


        mLobbyReference.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    //Log.e("TAG", "changeData")
                    lobby = dataSnapshot.getValue(LobbyModel::class.java)

                    //Log.e("TAG", lobby?.name)

                    mPlayersRef = FirebaseDatabase.getInstance().reference.child("Users")
                    //Log.e("TAG", "rand : " + rand)


                    mPlayersRef.addListenerForSingleValueEvent(object: ValueEventListener{

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if(dataSnapshot.exists()){
                                for(user in dataSnapshot.children){
                                    playerList.add(user.getValue(PlayerModel::class.java))
                                }
                                for(player in playerList){
                                    //Log.e("TAG", "flag")
                                    //Log.e("TEST PLAYER", player?.id)
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
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })




    }


    private fun attributeRole(lobby : LobbyModel?, listOfUser : MutableList<PlayerModel?>) {
        Log.e("RECCUP", "attribute")
        if(lobby != null) {
            var id = lobby.id
            var masterId = lobby.masterId
            var name = lobby.name
            //var nbPlayer = lobby.nbPlayer
            //var listPlayer = lobby.listPlayer
            var nbPlayer = 4
            var listPlayer = listOf<String>(
                "95gfhBLmLWef4soYEESPiIgSIXI3",
                "UECUWHd6DJOopvadKfHcBoY9JSy1",
                "f5lJpGohtZhC4ZygEGK4sywc3yz1",
                "fYSWKCs5PHfz6bhTYPD4nAOWUkl1"
            )
            var listPlayerInGame: MutableList<PlayerModel?> = arrayListOf()

            for (p in listPlayer) {
                for (player in listOfUser) {
                    if (p == player?.id) {
                        listPlayerInGame.add(player)
                    }
                }
            }

            for (gamer in listPlayerInGame) {
                Log.e("RECCUP", gamer?.pseudo)
            }

            if (listPlayer?.size == nbPlayer) {
                var roleList: MutableList<RoleModel> = arrayListOf()
                if (nbPlayer == 4) {
                    roleList.add(LoupGarouModel())
                    roleList.add(Voyante())
                    roleList.add(Villageois())
                    roleList.add(Villageois())
                    /*for(i in roleList){
                    Log.e("BEFORE", "role : " + i.name)
                }*/
                    roleList.shuffle()
                    /*for(i in roleList){
                    Log.e("AFTER", "role : " + i.name)
                }*/

                } else if (nbPlayer == 5) {

                } else if (nbPlayer == 6) {

                } else if (nbPlayer == 7) {

                } else if (nbPlayer == 8) {

                } else if (nbPlayer == 9) {

                } else if (nbPlayer == 10) {

                } else if (nbPlayer == 11) {

                } else if (nbPlayer == 12) {

                } else if (nbPlayer == 13) {

                } else if (nbPlayer == 14) {

                } else if (nbPlayer == 15) {

                } else if (nbPlayer == 16) {

                } else if (nbPlayer == 17) {

                } else if (nbPlayer == 18) {

                }
            }
        }
    }
    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
}
