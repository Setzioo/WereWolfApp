package fr.isen.cata.werewolfapp

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class GameActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var context = this
    /* Roles actuels : Loup  Villageois  Voyante  Ange  Cupidon  Chasseur  Sorciere  Pipoteur

        Ordre de jeu la nuit:
            - Cupidon (Première nuit seulement)
            - Voyante
            - Loups
            - Sorciere
            - Pipoteur (1 nuit sur 2)
     */

    private var currentPlayer: PlayerModel? = null
    val manager = MyFragmentManager()
    lateinit var currentRole: String
    var listId: MutableList<String>? = arrayListOf()
    var listPlayer: MutableList<PlayerModel?>? = arrayListOf()
    var aliveId: MutableList<String>? = arrayListOf()
    var alivePlayers: MutableList<PlayerModel?>? = arrayListOf()
    var deadPlayers: MutableList<PlayerModel?>? = arrayListOf()
    var gameName: String = ""
    var game: PartyModel? = null
    var nbTour: Int = 0
    var didAngeWin = false
    var gameStarted = false

    var listRole: MutableList<String>? = arrayListOf()
    var listRoleAlive: MutableList<String>? = arrayListOf()

    var musicPlayer: MediaPlayer? = null
    var soundPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")




        manager.beginningFragment(context)

        getPlayerInfo()

    }

    private fun getPlayerInfo() {

        val id: String = auth.currentUser!!.uid

        val mUserReference = FirebaseDatabase.getInstance().getReference("")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.child("Users").children) {
                        user.add(i.getValue(PlayerModel::class.java))
                    }
                    for (i in user) {
                        if (i?.id == id) {
                            currentPlayer = i
                            gameName = currentPlayer!!.currentGame!!
                            currentRole = currentPlayer!!.role!!

                        }

                    }
                }
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    if (game != null) {
                        if (game!!.listPlayer != null) {
                            listId = game!!.listPlayer
                            aliveId = listId
                        }
                    }


                }
                if(dataSnapshot.exists()) {
                    if (listId != null) {
                        for (i in listId!!) {
                            for (u in dataSnapshot.child("Users").children) {
                                val users = u.getValue(PlayerModel::class.java)
                                if (i == users!!.id) {
                                    listPlayer!!.add(users)
                                }
                            }
                        }

                        if (nbTour == 0 && listPlayer != null && !game!!.Flags!!.DeadNightFlag) {
                            alivePlayers = listPlayer


                            if (listPlayer != null) {
                                for (i in listPlayer!!) {
                                    listRole!!.add(i!!.role!!)
                                }
                            }

                        }
                    }
                }

                gameListener()

                setOnlyFlagListener()
                setOnlyFinishFlagListener()


            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun setOnlyFlagListener() {
        val mPartyReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName).child("Flags")
        mPartyReference.child("ChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var randomFlag = false
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Chasseur")) {
                            if (deadPlayers != null) {
                                for (i in deadPlayers!!) {
                                    if (i!!.role == "Chasseur") {
                                        if (soundPlayer != null) {
                                            soundPlayer!!.stop()
                                        }
                                        soundPlayer = MediaPlayer.create(context, R.raw.chasseur_mort_modif)
                                        soundPlayer!!.start()
                                        soundPlayer!!.setOnCompletionListener {
                                            chasseurTurn()

                                        }
                                        randomFlag = true
                                    }
                                }
                                if (!randomFlag) {
                                    raiseFlagVote()
                                }
                            }

                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("ChasseurFlag")
                                    .setValue(true)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("CupidonFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.cupidon_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                cupidonTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("CupidonFlag")
                                    .setValue(true)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadNightFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadNightFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadVoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadVoteFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadChasseurFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("LoupFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Loup-Garou")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.loup_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                loupsTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag")
                                    .setValue(true)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("LoverFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.village_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                loverTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoverFlag")
                                    .setValue(true)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        mPartyReference.child("PipotedFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Pipoteur")) { //TODO : changer en listRoleAlive
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.village_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                pipotedTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipotedFlag")
                                    .setValue(true)
                            }

                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PipoteurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Pipoteur")) { //TODO : changer en listRoleAlive
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.pipoteur_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                pipoteurTurn()
                            }
                        } else {

                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipoteurFlag")
                                    .setValue(true)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PrintNightFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        printDeadNightTurn()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PrintChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        printDeadChasseurTurn()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PrintVoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        printDeadVoteTurn()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("SorciereFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRoleAlive!!.contains("Sorciere")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.sorciere_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                sorciereTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("SorciereFlag")
                                    .setValue(true)
                            }

                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("VoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag")
                                .setValue(false)
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("ChasseurFlag")
                                .setValue(false)
                        }


                        voteTurn()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("VoyanteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Voyante")) { //TODO : changer listRole en listRoleAlive
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.voyante_reveil)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                voyanteTurn()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoyanteFlag")
                                    .setValue(true)
                            }

                }
            }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mDatabase.child("Party").child(gameName).child("nightGame").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (gameStarted) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            if (musicPlayer != null) {
                                musicPlayer!!.stop()
                            }
                            musicPlayer = MediaPlayer.create(context, R.raw.musique_nuit)
                            musicPlayer!!.start()

                            lowerFlags()
                            if (!game!!.Flags!!.CupidonFlag) {
                                raiseFlagCupidon()
                            } else {
                                raiseFlagVoyante()
                            }
                        } else {
                            if (musicPlayer != null) {
                                musicPlayer!!.stop()
                            }
                            musicPlayer = MediaPlayer.create(context, R.raw.village_reveil_7min)
                            musicPlayer!!.start()

                            nbTour++
                            raiseFlagDeadNight()
                        }
                    } else {
                        gameStarted = true
                    }


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mDatabase.child("Party").child(gameName).child("endGame").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        when (game!!.winner) {
                            1 -> {
                                if (musicPlayer != null) {
                                    musicPlayer!!.stop()
                                }
                                musicPlayer = MediaPlayer.create(context, R.raw.victoire_cupidon)
                                musicPlayer!!.start()

                            }
                            2 -> {
                                if (musicPlayer != null) {
                                    musicPlayer!!.stop()
                                }
                                musicPlayer = MediaPlayer.create(context, R.raw.victoire_pipoteur)
                                musicPlayer!!.start()

                            }
                            3 -> {
                                if (musicPlayer != null) {
                                    musicPlayer!!.stop()
                                }
                                musicPlayer = MediaPlayer.create(context, R.raw.victoire_villageaois)
                                musicPlayer!!.start()

                            }
                            4 -> {
                                if (musicPlayer != null) {
                                    musicPlayer!!.stop()
                                }
                                musicPlayer = MediaPlayer.create(context, R.raw.victoire_ange)
                                musicPlayer!!.start()

                            }
                            5 -> {
                                if (musicPlayer != null) {
                                    musicPlayer!!.stop()
                                }
                                musicPlayer = MediaPlayer.create(context, R.raw.victoire_loup)
                                musicPlayer!!.start()

                            }
                        }
                        manager.finJeuFragment(context)
                    }


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        mDatabase.child("Party").child(gameName).child("goToHome").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (musicPlayer != null) {
                            musicPlayer!!.stop()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


    }

    private fun setOnlyFinishFlagListener() {
        val mPartyReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName).child("FinishFlags")
        mPartyReference.child("ChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var randomFlag = false
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Chasseur")) {
                            if (deadPlayers != null) {
                                for (i in deadPlayers!!) {
                                    if (i!!.role == "Chasseur") {
                                        raiseFlagDeadChasseur()
                                        randomFlag = true
                                    }
                                }
                                if (!randomFlag) {
                                    if (game!!.Flags!!.VoteFlag) {
                                        mDatabase.child("Party").child(gameName).child("nightGame")
                                            .setValue(true)
                                    } else {
                                        raiseFlagVote()
                                    }
                                }
                            }

                        } else {
                            if (game!!.Flags!!.VoteFlag) {
                                mDatabase.child("Party").child(gameName).child("nightGame")
                                    .setValue(true)
                            } else {
                                raiseFlagVote()
                            }
                        }
                    }
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("CupidonFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.cupidon_endort)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                raiseFlagLover()
                            }
                        } else {
                            raiseFlagVoyante()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadNightFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagPrintNight()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadVoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("DeadChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagPrintChasseur()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("LoupFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (soundPlayer != null) {
                            soundPlayer!!.stop()
                        }
                        soundPlayer = MediaPlayer.create(context, R.raw.village_endort)
                        soundPlayer!!.start()
                        soundPlayer!!.setOnCompletionListener {
                            raiseFlagSorciere()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("LoverFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (soundPlayer != null) {
                            soundPlayer!!.stop()
                        }
                        soundPlayer = MediaPlayer.create(context, R.raw.village_endort)
                        soundPlayer!!.start()
                        soundPlayer!!.setOnCompletionListener {
                            raiseFlagVoyante()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("LowerFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PipotedFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (soundPlayer != null) {
                            soundPlayer!!.stop()
                        }
                        soundPlayer = MediaPlayer.create(context, R.raw.village_endort)
                        soundPlayer!!.start()
                        soundPlayer!!.setOnCompletionListener {
                            raiseFlagLoups()
                        }


                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PipoteurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRoleAlive!!.contains("Pipoteur")) {
                            if (soundPlayer != null) {
                                soundPlayer!!.stop()
                            }
                            soundPlayer = MediaPlayer.create(context, R.raw.pipoteur_endort)
                            soundPlayer!!.start()
                            soundPlayer!!.setOnCompletionListener {
                                raiseFlagPipoted()
                            }
                        } else {
                            if (currentPlayer!!.id == game!!.masterId) {
                                raiseFlagLoups()
                            }
                        }
                    }


                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        mPartyReference.child("PrintNightFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            if (isItTheEnd(didAngeWin) != 0) {
                                if (currentPlayer!!.id == game!!.masterId) {
                                    mDatabase.child("Party").child(gameName).child("winner")
                                        .setValue(isItTheEnd(didAngeWin))
                                    mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                                }

                            } else {
                                raiseFlagChasseur()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        mPartyReference.child("PrintChasseurFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            if (isItTheEnd(didAngeWin) != 0) {
                                if (currentPlayer!!.id == game!!.masterId) {
                                    mDatabase.child("Party").child(gameName).child("winner")
                                        .setValue(isItTheEnd(didAngeWin))
                                    mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                                }

                            } else {
                                if (game!!.Flags!!.VoteFlag) {
                                    if (currentPlayer!!.id == game!!.masterId) {
                                        mDatabase.child("Party").child(gameName).child("nightGame").setValue(true)
                                    }
                                } else {
                                    raiseFlagVote()
                                }
                            }

                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        mPartyReference.child("PrintVoteFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            if (isItTheEnd(didAngeWin) != 0) {
                                if (currentPlayer!!.id == game!!.masterId) {
                                    mDatabase.child("Party").child(gameName).child("winner")
                                        .setValue(isItTheEnd(didAngeWin))
                                    mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                                }

                            } else {
                                raiseFlagChasseur()
                            }

                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        mPartyReference.child("SorciereFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            if (listRoleAlive!!.contains("Sorciere")) {
                                if (soundPlayer != null) {
                                    soundPlayer!!.stop()
                                }
                                soundPlayer = MediaPlayer.create(context, R.raw.sorciere_endort)
                                soundPlayer!!.start()
                                soundPlayer!!.setOnCompletionListener {
                                    if (currentPlayer!!.id == game!!.masterId) {

                                        mDatabase.child("Party").child(gameName).child("nightGame")
                                            .setValue(false)
                                    }
                                }
                            } else {
                                if (currentPlayer!!.id == game!!.masterId) {

                                    mDatabase.child("Party").child(gameName).child("nightGame")
                                        .setValue(false)
                                }
                            }

                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        mPartyReference.child("VoteFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {
                            checkForDeadVote()
                            raiseFlagPrintVote()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        mPartyReference.child("VoyanteFlag").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val bool = dataSnapshot.value as Boolean
                        if (bool) {

                            if (listRoleAlive!!.contains("Voyante")) {

                                if (soundPlayer != null) {
                                    soundPlayer!!.stop()
                                }
                                soundPlayer = MediaPlayer.create(context, R.raw.voyante_endort)
                                soundPlayer!!.start()
                                soundPlayer!!.setOnCompletionListener {
                                    raiseFlagPipoteur()
                                }
                            } else {
                                raiseFlagPipoteur()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

    }


    private fun isPipoteurAlive(): Boolean {
        for (player in alivePlayers!!) {
            if (player!!.role == "Pipoteur") {
                return true
            }
        }
        return false
    }

    private fun isCupidon(): Boolean {
        for (player in listPlayer!!) {
            if (player!!.role == "Cupidon") {
                return true
            }
        }
        return false
    }

    private fun isAnge(): Boolean {
        for (player in listPlayer!!) {
            if (player!!.role == "Ange") {
                return true
            }
        }
        return false
    }

    private fun cupidonTurn() {
        manager.cupidonFragment(context)

    }

    private fun loverTurn() {
        manager.loveFragment(context)
    }

    private fun voyanteTurn() {
        manager.voyanteFragment(context)

    }

    private fun loupsTurn() {
        manager.loupsFragment(context)

    }

    private fun sorciereTurn() {
        manager.sorciereVieFragment(context)

    }

    private fun pipoteurTurn() {
        manager.pipoteurFragment(context)
    }

    private fun pipotedTurn() {
        manager.pipotedFragment(context)
    }

    private fun voteTurn() {
        manager.voteJourFragment(context)
    }

    private fun printDeadNightTurn() {
        manager.printDeadNightFragment(context)

    }

    private fun printDeadVoteTurn() {
        manager.printDeadVoteFragment(context)


    }

    private fun printDeadChasseurTurn() {
        manager.printDeadChasseurFragment(context)


    }

    private fun chasseurTurn() {
        manager.chasseurFragment(context)

    }

    private fun raiseFlagCupidon() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("CupidonFlag").setValue(true)
        }
    }

    private fun raiseFlagLover() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("LoverFlag").setValue(true)
        }
    }

    private fun raiseFlagVoyante() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(true)
        }
    }

    private fun raiseFlagLoups() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(true)
        }
    }

    private fun raiseFlagSorciere() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(true)
        }
    }

    private fun raiseFlagPipoteur() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("PipoteurFlag").setValue(true)
        }
    }

    private fun raiseFlagPipoted() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("PipotedFlag").setValue(true)
        }
    }

    private fun raiseFlagVote() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(true)
        }
    }

    private fun raiseFlagDeadNight() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("DeadNightFlag").setValue(true)
        }
    }

    private fun raiseFlagDeadChasseur() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("DeadChasseurFlag").setValue(true)
        }
    }

    private fun raiseFlagChasseur() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag").setValue(true)
        }
    }

    private fun raiseFlagPrintNight() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintNightFlag").setValue(true)
        }
    }

    private fun raiseFlagPrintVote() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintVoteFlag").setValue(true)
        }
    }

    private fun raiseFlagPrintChasseur() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintChasseurFlag").setValue(true)
        }
    }

    private fun lowerFlags() {
        if (currentPlayer!!.id == game!!.masterId) {
            mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("PipoteurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("DeadNightFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("DeadVoteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("DeadChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("LoverFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("PipotedFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintNightFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("Flags").child("PrintVoteFlag").setValue(false)

            mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoyanteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("SorciereFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipoteurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadNightFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadVoteFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("ChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoverFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipotedFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PrintNightFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PrintChasseurFlag").setValue(false)
            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PrintVoteFlag").setValue(false)
        }


    }

    private fun gameListener() {
        val mPlayerReference = FirebaseDatabase.getInstance().reference

        mPlayerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listPlayer: MutableList<PlayerModel>? = arrayListOf()
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)


                }
                if (listId != null) {
                    listPlayer!!.clear()
                    for (i in listId!!) {
                        for (u in dataSnapshot.child("Users").children) {
                            val users = u.getValue(PlayerModel::class.java)
                            if (i == users!!.id) {
                                listPlayer.add(users)
                            }
                        }
                    }

                    alivePlayers!!.clear()

                    if (aliveId != null) {
                        for (i in aliveId!!) {
                            for (u in listPlayer) {
                                if (i == u.id) {
                                    alivePlayers!!.add(u)
                                    listRoleAlive!!.add(u.role!!)
                                }
                            }

                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }


    private fun checkForDeadVote() {
        deadPlayers!!.clear()
        var isLoverDead = false

        if (alivePlayers != null) {


            for (player in alivePlayers!!) {

                if (!player!!.state) {//si mort
                    deadPlayers!!.add(player)
                    if (player.inLove) {
                        isLoverDead = true
                    }
                }
            }
            if (isLoverDead) {
                for (p in alivePlayers!!) {
                    if (p!!.inLove) {
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Users").child(p.id).child("state").setValue(false)
                        }
                        deadPlayers!!.add(p)
                    }
                }
            }

            if (deadPlayers != null && deadPlayers!!.size != 0) {
                if (nbTour == 1 && isAnge()) {
                    for (player in deadPlayers!!) {
                        if (player!!.role == "Ange") {
                            didAngeWin = true
                        }
                    }
                }


                alivePlayers!!.removeAll(deadPlayers!!)

            }
            aliveId = arrayListOf()
            if (alivePlayers != null) {
                for (player in alivePlayers!!) {
                    aliveId?.add(player!!.id)
                }
            }
        }
    }

    private fun checkForDead() {
        deadPlayers!!.clear()
        var isLoverDead = false



        if (alivePlayers != null) {
            for (player in alivePlayers!!) {

                if (!player!!.state) {//si mort
                    deadPlayers!!.add(player)
                    if (player.inLove) {
                        isLoverDead = true
                    }
                }
            }
            if (isLoverDead) {
                for (p in alivePlayers!!) {
                    if (p!!.inLove) {
                        if (currentPlayer!!.id == game!!.masterId) {
                            mDatabase.child("Users").child(p.id).child("state").setValue(false)
                        }
                        deadPlayers!!.add(p)
                    }
                }
            }

            if (deadPlayers != null && deadPlayers!!.size != 0) {


                alivePlayers!!.removeAll(deadPlayers!!)


            }
            aliveId = arrayListOf()
            if (alivePlayers != null) {
                for (player in alivePlayers!!) {
                    aliveId?.add(player!!.id)
                }
            }
        }
    }

    private fun isItTheEnd(angeAlreadyWin: Boolean): Int {
        var codeGame = 0

        val nbPlayer = alivePlayers!!.size

        val amoureux = isCupidon()
        val pipoteur = isPipoteurAlive()

        var nbLoup = 0
        var nbVillageois = 0
        /*****WINNER******
         * 1 : amoureux
         * 2 : pipoteur
         * 3 : villageois
         * 4 : ange
         * 5 : loups
         * */
        for (player in alivePlayers!!) {
            if (player!!.role == "Loup-Garou") {
                nbLoup++

            } else {
                nbVillageois++

            }
        }

        if (nbLoup == nbPlayer) {
            codeGame = 5
        } else if (nbVillageois == nbPlayer) {
            codeGame = 3
        }
        if (pipoteur) {

            var nbPipo = 1
            for (player in alivePlayers!!) {
                if (player!!.charmed) {
                    nbPipo++
                }
            }
            if (nbPipo == nbPlayer) {
                codeGame = 2
            }
        }
        if (amoureux) {
            if (alivePlayers!!.size == 2) {
                for (player in alivePlayers!!) {
                    if (player!!.inLove) {
                        codeGame = 1

                    }
                }
            }
        }
        if (angeAlreadyWin) {
            codeGame = 4
        }
        return codeGame
    }
}

