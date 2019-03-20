package fr.isen.cata.werewolfapp

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.widget.VideoView
import android.support.v4.os.HandlerCompat.postDelayed
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class GameActivityTest : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mLobbyReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var context = this
    private var previousLowerFlag = false
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
    var isHunterDead = false
    var flagDead = true
    var listRole: MutableList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")




        manager.BeginningFragment(context)

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
                if (listId != null) {
                    for (i in listId!!) {
                        for (u in dataSnapshot.child("Users").children) {
                            val users = u.getValue(PlayerModel::class.java)
                            if (i == users!!.id) {
                                listPlayer!!.add(users)
                            }
                        }
                    }
                    if (nbTour == 0 && listPlayer != null && !game!!.Flags!!.VoteFlag) {
                        Log.d("FUN", "init alive")
                        alivePlayers = listPlayer
                        if (listPlayer != null) {
                            for (i in listPlayer!!) {
                                listRole!!.add(i!!.role!!)
                            }
                        }
                    }
                }

                gameListener()

                setOnlyFlagListener()
                setOnlyFinishFlagListener()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
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
                            if(deadPlayers != null) {
                                for (i in deadPlayers!!) {
                                    if (i!!.role == "Chasseur") {
                                        chasseurTurn()
                                        randomFlag = true
                                    }
                                }
                                if(!randomFlag) {
                                    raiseFlagVote()
                                }
                            }

                        }
                        else {
                            raiseFlagVote()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("CupidonFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            cupidonTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("CupidonFlag")
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("DeadNightFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadNightFlag")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("DeadVoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadVoteFlag")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("DeadChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        checkForDead()
                        mDatabase.child("Party").child(gameName).child("FinishFlags").child("DeadChasseurFlag")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("LoupFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Loup-Garou")) {
                            loupsTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("LoverFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            loverTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoverFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PipotedFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Pipoteur")) {
                            pipotedTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipotedFlag")
                                .setValue(true)
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("Pipoteur").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Pipoteur")) {
                            pipoteurTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipoteurFlag")
                                .setValue(true)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("SorciereFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Sorciere")) {
                            sorciereTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("SorciereFlag")
                                .setValue(true)
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("TaamponFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("TourFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("VoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag").setValue(false)
                        mDatabase.child("Party").child(gameName).child("FinishFlags").child("ChasseurFlag").setValue(false)

                        voteTurn()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("VoyanteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Voyante")) {
                            voyanteTurn()
                        } else {
                            mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoyanteFlag")
                                .setValue(true)
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })

        mPartyReference.child("endPrint").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mDatabase.child("Party").child(gameName).child("nightGame").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        lowerFlags()
                        if (!game!!.Flags!!.CupidonFlag) {
                            raiseFlagCupidon()
                        } else {
                            raiseFlagVoyante()
                        }
                    } else {
                        nbTour++
                        raiseFlagDeadNight()
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })

    }

    private fun setOnlyFinishFlagListener() {
        val mPartyReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName).child("FinishFlags")
        mPartyReference.child("ChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagDeadChasseur()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("CupidonFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Cupidon")) {
                            raiseFlagLover()
                        } else {
                            raiseFlagVoyante()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("LoupFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagSorciere()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("LoverFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagVoyante()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
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
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PipotedFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        mDatabase.child("Party").child(gameName).child("nightGame")
                            .setValue(false)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PipoteurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (listRole!!.contains("Pipoteur")) {
                            raiseFlagPipoted()
                        } else {
                            mDatabase.child("Party").child(gameName).child("nightGame")
                                .setValue(false)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PrintNightFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (isItTheEnd(didAngeWin) != 0) {
                            mDatabase.child("Party").child(gameName).child("winner").setValue(isItTheEnd(didAngeWin))
                            mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                        } else {
                            raiseFlagChasseur()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PrintChasseurFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (isItTheEnd(didAngeWin) != 0) {
                            mDatabase.child("Party").child(gameName).child("winner").setValue(isItTheEnd(didAngeWin))
                            mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                        } else {
                            if(game!!.Flags!!.VoteFlag) {
                                mDatabase.child("Party").child(gameName).child("nightGame").setValue(true)
                            }
                            else
                            {
                                raiseFlagVote()
                            }
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("PrintVoteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        if (isItTheEnd(didAngeWin) != 0) {
                            mDatabase.child("Party").child(gameName).child("winner").setValue(isItTheEnd(didAngeWin))
                            mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                        }
                        else {
                            raiseFlagChasseur()
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("SorciereFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagPipoteur()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("VoteFlag").addValueEventListener(object : ValueEventListener {
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
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
        mPartyReference.child("VoyanteFlag").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bool = dataSnapshot.value as Boolean
                    if (bool) {
                        raiseFlagLoups()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
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
        manager.CupidonFragment(context)

    }

    private fun loverTurn() {
        manager.LoveFragment(context)
    }

    private fun voyanteTurn() {
        manager.VoyanteFragment(context)

    }

    private fun loupsTurn() {
        manager.LoupsFragment(context)

    }

    private fun sorciereTurn() {
        manager.SorciereVieFragment(context)

    }

    private fun pipoteurTurn() {
        manager.PipoteurFragment(context)
    }

    private fun pipotedTurn() {
        manager.PipotedFragment(context)
    }

    private fun voteTurn() {
        manager.VoteJourFragment(context)
    }

    private fun printDeadNightTurn() {
        manager.PrintDeadNightFragment(context)

    }

    private fun printDeadVoteTurn() {
        manager.PrintDeadVoteFragment(context)


    }

    private fun printDeadChasseurTurn() {
        manager.PrintDeadChasseurFragment(context)


    }

    private fun chasseurTurn() {
        manager.ChasseurFragment(context)

    }

    private fun raiseFlagCupidon() {
        mDatabase.child("Party").child(gameName).child("Flags").child("CupidonFlag").setValue(true)
    }

    private fun raiseFlagLover() {
        mDatabase.child("Party").child(gameName).child("Flags").child("LoverFlag").setValue(true)
    }

    private fun raiseFlagVoyante() {
        mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(true)
    }

    private fun raiseFlagLoups() {
        mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(true)
    }

    private fun raiseFlagSorciere() {
        mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(true)
    }

    private fun raiseFlagPipoteur() {
        mDatabase.child("Party").child(gameName).child("Flags").child("PipoteurFlag").setValue(true)
    }

    private fun raiseFlagPipoted() {
        mDatabase.child("Party").child(gameName).child("Flags").child("PipotedFlag").setValue(true)
    }

    private fun raiseFlagVote() {
            mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(true)
    }

    private fun raiseFlagDeadNight() {
        mDatabase.child("Party").child(gameName).child("Flags").child("DeadNightFlag").setValue(true)
    }

    private fun raiseFlagDeadChasseur() {
        mDatabase.child("Party").child(gameName).child("Flags").child("DeadChasseurFlag").setValue(true)
    }

    private fun raiseFlagChasseur() {
        mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag").setValue(true)
    }

    private fun raiseFlagPrintNight() {
        mDatabase.child("Party").child(gameName).child("Flags").child("PrintNightFlag").setValue(true)
    }

    private fun raiseFlagPrintVote() {
        mDatabase.child("Party").child(gameName).child("Flags").child("PrintVoteFlag").setValue(true)
    }

    private fun raiseFlagPrintChasseur() {
        mDatabase.child("Party").child(gameName).child("Flags").child("PrintFlagChasseur").setValue(true)
    }

    private fun lowerFlags() {

        mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("CupidonFlag").setValue(false)
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
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("CupidonFlag").setValue(false)
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

    private fun gameListener() {
        val mPlayerReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName)

        mPlayerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.getValue(PartyModel::class.java)

                }
                if (listId != null) {
                    listPlayer!!.clear()
                    for (i in listId!!) {
                        for (u in dataSnapshot.child("Users").children) {
                            val users = u.getValue(PlayerModel::class.java)
                            if (i == users!!.id) {
                                listPlayer!!.add(users)
                            }
                        }
                    }
                    alivePlayers!!.clear()
                    if (aliveId != null && listPlayer != null) {
                        for (i in aliveId!!) {
                            for (u in listPlayer!!) {
                                if (i == u!!.id) {
                                    alivePlayers!!.add(u)
                                }
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
    }



    private fun checkForDeadVote() {
        deadPlayers!!.clear()
        var isLoverDead = false

        if (alivePlayers != null) {
            Log.e("FUN", "check des morts")
            for (player in alivePlayers!!) {

                if (!player!!.state) {//si mort
                    Log.d("FUN", "dead : " + player.id)
                    deadPlayers!!.add(player)
                    if (player.inLove) {
                        isLoverDead = true
                    }
                }
            }
            if (isLoverDead) {
                for (p in alivePlayers!!) {
                    if (p!!.inLove) {
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

                Log.d("FUN", "mise à mort")

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
            Log.e("FUN", "check des morts")
            for (player in alivePlayers!!) {

                if (!player!!.state) {//si mort
                    Log.d("FUN", "dead : " + player.id)
                    deadPlayers!!.add(player)
                    if (player.inLove) {
                        isLoverDead = true
                    }
                }
            }
            if (isLoverDead) {
                for (p in alivePlayers!!) {
                    if (p!!.inLove) {
                        deadPlayers!!.add(p)
                    }
                }
            }

            if (deadPlayers != null && deadPlayers!!.size != 0) {

                Log.d("FUN", "mise à mort")

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

