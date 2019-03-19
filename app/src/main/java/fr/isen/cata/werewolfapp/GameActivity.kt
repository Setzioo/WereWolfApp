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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import    android.media.MediaPlayer
import android.view.WindowManager


class GameActivity : AppCompatActivity() {



    private var musicPlayer: MediaPlayer? = null
    private var previousNightState = false

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
    var gameName: String = ""
    var game: PartyModel? = null
    var nbTour: Int = 0
    var didAngeWin = false
    var isHunterDead = false
    var flagDead = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")


        setMusicListener()



        manager.SorciereVieFragment(context)

        getPlayerInfo()


    }

    override fun onBackPressed() {

    }

    private fun setMusicListener() {
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
                            gameName = i.currentGame!!
                            musicListener()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun musicListener() {
        mDatabase.child("Party").child(gameName).child("nightGame").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nightGame = dataSnapshot.value as Boolean
                    if(musicPlayer != null)
                    {
                        musicPlayer!!.stop()
                    }
                    musicPlayer = if (nightGame) {
                        MediaPlayer.create(context, R.raw.musique_nuit)
                    } else {
                        MediaPlayer.create(context, R.raw.village_reveil_7min)
                    }

                    musicPlayer!!.isLooping = true
                    musicPlayer!!.start()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
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
                        if (alivePlayers != null) {
                            for (i in alivePlayers!!) {
                                //Log.d("FUN", "alive : "+i!!.id)
                            }
                        }
                    }
                }
                gameListener()
                flagListener()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


    private fun isSorciere(): Boolean {
        for (player in listPlayer!!) {
            if (player!!.role == "Sorcière") {
                return true
            }
        }
        return false
    }

    private fun isVoyante(): Boolean {
        for (player in listPlayer!!) {
            if (player!!.role == "Voyante") {
                return true
            }
        }
        return false
    }

    private fun isPipoteur(): Boolean {
        for (player in listPlayer!!) {
            if (player!!.role == "Pipoteur") {
                return true
            }
        }
        return false
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

    private fun playNight() {

        val cupidon = isCupidon()
        val voyante = isVoyante()
        val sorciere = isSorciere()
        val pipoteur = isPipoteur()
        if (game!!.Flags!!.DeadFlag) {
            lowerFlagDead()
        }
        if (game!!.Flags!!.VoteFlag) {
            lowerFlagVote()
        }

        //Log.e("FUN", "cupidon : "+cupidon+" voyante : "+voyante+" sorciere : "+sorciere+" pipoteur : "+pipoteur)
        if (currentPlayer!!.state) {//Si vivant
            //Log.e("FUN", "Alive")
            if (cupidon) {//Si cupidon alors voyante
                if (!game!!.Flags!!.CupidonFlag) {//tour de cupidon
                    Log.e("FUN", "Cupi joue")
                    raiseFlagCupidon()
                } else {//Cupidon a joué
                    if (!game!!.Flags!!.LoverFlag && game!!.FinishFlags!!.CupidonFlag) {
                        Log.e("FUN", "Les amoureux se voient")
                        raiseFlagLover()
                    } else {//Les amoureux se sont vu
                        if (!game!!.Flags!!.VoyanteFlag && game!!.FinishFlags!!.LoverFlag) {//tour de la voyante
                            Log.e("FUN", "Voyante joue avec cupi")
                            raiseFlagVoyante()

                        } else {//la voyante a joué
                            if (!game!!.Flags!!.LoupFlag && game!!.FinishFlags!!.VoyanteFlag) {//tour des loups
                                Log.e("FUN", "loup joue avec cupi")
                                raiseFlagLoups()
                            } else {//les loups ont joués
                                if (sorciere && pipoteur) {
                                    if (!game!!.Flags!!.SorciereFlag && game!!.FinishFlags!!.LoupFlag) {//tour de la sorciere
                                        Log.e("FUN", "sorciere joue avec pipo")
                                        raiseFlagSorciere()
                                    } else {
                                        if (!game!!.Flags!!.PipoteurFlag && game!!.FinishFlags!!.SorciereFlag) {//tour du pipoteur
                                            Log.e("FUN", "pipo joue avec sorciere")
                                            raiseFlagPipoteur()
                                        } else {
                                            if (game!!.FinishFlags!!.PipoteurFlag && !game!!.Flags!!.PipotedFlag) {
                                                Log.e("FUN", "on voit les pipoté")
                                                raiseFlagPipoted()
                                            } else {
                                                if (game!!.FinishFlags!!.PipotedFlag) {
                                                    launchDay()
                                                }
                                            }

                                        }
                                    }
                                } else if (sorciere) {
                                    if (!game!!.Flags!!.SorciereFlag && game!!.FinishFlags!!.LoupFlag) {//tour de la sorciere
                                        Log.e("FUN", "sorciere joue sans pipo")
                                        raiseFlagSorciere()
                                    } else {
                                        if (game!!.FinishFlags!!.SorciereFlag) {
                                            launchDay()
                                        }
                                    }
                                } else if (pipoteur) {
                                    if (!game!!.Flags!!.PipoteurFlag && game!!.FinishFlags!!.LoupFlag) {//tour du pipoteur
                                        Log.e("FUN", "pipo joue sans sorciere")
                                        raiseFlagPipoteur()
                                    } else {
                                        if (game!!.FinishFlags!!.PipoteurFlag && !game!!.Flags!!.PipotedFlag) {
                                            Log.e("FUN", "on voit les pipoté")
                                            raiseFlagPipoted()
                                        } else {
                                            if (game!!.FinishFlags!!.PipotedFlag) {
                                                launchDay()
                                            }
                                        }
                                    }
                                } else {
                                    if (game!!.FinishFlags!!.LoupFlag) {
                                        launchDay()
                                    }
                                }
                            }
                        }
                    }

                }
            } else {//si pas de cupidon voyante? + loups
                if (voyante) {
                    if (!game!!.Flags!!.VoyanteFlag) {//Tour de la voyante
                        Log.e("FUN", "Voyante joue sans cupi")
                        raiseFlagVoyante()
                    } else {//La voyante a joué
                        if (!game!!.Flags!!.LoupFlag && game!!.FinishFlags!!.VoyanteFlag) {
                            Log.e("FUN", "loup joue sans cupi")
                            raiseFlagLoups()
                        } else {
                            if (game!!.FinishFlags!!.LoupFlag) {
                                launchDay()
                            }
                        }

                    }
                } else {//si pas de voyante que loups
                    Log.e("FUN", "loup joue sans voyante")
                    if (!game!!.Flags!!.LoupFlag) {//tour des loups
                        raiseFlagLoups()
                    } else {
                        if (game!!.FinishFlags!!.LoupFlag) {
                            launchDay()
                        }
                    }

                }
                if (game!!.FinishFlags!!.LoupFlag) {
                    launchDay()
                }
            }
        } else {
            //ecran des morts
            Toast.makeText(context, "Mort", Toast.LENGTH_LONG).show()
        }

    }

    private fun playDay() {
        if (game!!.Flags!!.ChasseurFlag && !game!!.FinishFlags!!.ChasseurFlag) {
            checkDead()
        } else if (!game!!.Flags!!.DeadFlag && !game!!.Flags!!.VoteFlag) {
            raiseFlagDead()
        } else if (game!!.FinishFlags!!.VoteFlag && !game!!.Flags!!.DeadFlag) {
            Log.e("FUN", "check mort du vote")
            checkDeadAfterVote()

        }
    }

    private fun night() {
        manager.NightFragment(context)
    }

    private fun cupidonTurn() {
        if (nbTour == 0) {
            if ((currentRole == "Cupidon")) {
                manager.CupidonFragment(context)//Passer le flag de cupidon a true
            }
        }
    }

    private fun loverTurn() {
        //manager.LoveFragment(context)
    }

    private fun voyanteTurn() {
        if (currentRole == "Voyante") {
            manager.VoyanteFragment(context)
        }
    }

    private fun loupsTurn() {
        if (currentRole == "Loup-Garou") {
            manager.LoupsFragment(context)
        }
    }

    private fun sorciereTurn() {
        if (currentRole == "Sorciere") {
            manager.SorciereVieFragment(context)
        }
    }

    private fun pipoteurTurn() {
        if (currentRole == "Pipoteur") {
            manager.PipoteurFragment(context)
        }
    }

    private fun pipotedTurn() {
        //manager.PipotedFragment(context)
    }

    private fun voteTurn() {
        manager.VoteJourFragment(context)
    }
    private fun printDeadTurn(){
        if(game!!.Flags!!.DeadFlag && flagDead){
            flagDead = false
            manager.PrintDeadFragment(context)
        }

    }

    private fun chasseurTurn() {
        //if(currentRole=="Chasseur"){
        manager.ChasseurFragment(context)
        //}
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

    private fun raiseFlagTour() {
        mDatabase.child("Party").child(gameName).child("Flags").child("TourFlag").setValue(true)
    }

    private fun raiseFlagDead() {
        mDatabase.child("Party").child(gameName).child("Flags").child("DeadFlag").setValue(true)
    }

    private fun raiseFlagChasseur() {
        mDatabase.child("Party").child(gameName).child("Flags").child("ChasseurFlag").setValue(true)
    }

    private fun lowerFlag() {
        mDatabase.child("Party").child(gameName).child("Flags").child("LowerFlag").setValue(true)
        mDatabase.child("Party").child(gameName).child("Flags").child("TourFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(false)

        mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoyanteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("SorciereFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipoteurFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("nightGame").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("LowerFlag").setValue(false)

    }

    private fun lowerFlagVote() {
        mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoteFlag").setValue(false)
    }

    private fun lowerFlagDead(){
        Thread.sleep(2000)
        mDatabase.child("Party").child(gameName).child("Flags").child("DeadFlag").setValue(false)
    }

    private fun gameListener() {
        val mPlayerReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName)

        mPlayerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    getParty()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
    }

    private fun flagListener() {
        val mPartyReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName).child("Flags")
        mPartyReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    listenForFlags(dataSnapshot)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
    }

    private fun listenForFlags(dataSnapshot: DataSnapshot){
        val flags : FlagModel? = dataSnapshot.getValue(FlagModel::class.java)
        game!!.Flags = flags
        if(!game!!.endGame){
            if(flags!!.DeadFlag){
                    checkDead()
            }
            else {
                if (flags.VoteFlag) {
                    voteTurn()
                } else if (flags.ChasseurFlag) {
                    chasseurTurn()
                } else if (flags.TourFlag) {
                    nbTour++
                } else if (flags.PipotedFlag) {
                    pipotedTurn()
                } else if (flags.PipoteurFlag) {
                    pipoteurTurn()
                } else if (flags.SorciereFlag) {
                    sorciereTurn()
                } else if (flags.LoupFlag) {
                    loupsTurn()
                } else if (flags.VoyanteFlag) {
                    voyanteTurn()
                } else if (flags.CupidonFlag) {
                    loverTurn()
                } else if (flags.CupidonFlag) {
                    cupidonTurn()
                }
            }

        }
    }

    private fun checkDead() {
        var deadPlayers: MutableList<PlayerModel>? = arrayListOf()
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
            if (deadPlayers != null && deadPlayers.size != 0){
                if(nbTour == 1 && isAnge() && game!!.Flags!!.VoteFlag){
                    for(player in deadPlayers){
                        if(player.role=="Ange"){
                            didAngeWin = true
                        }
                    }
                }
                for (player in deadPlayers) {

                    if (player.role == "Chasseur") {
                        isHunterDead = true
                    }
                }
                Log.d("FUN", "mise à mort")

                alivePlayers!!.removeAll(deadPlayers)

            }
            aliveId = arrayListOf()
            if (alivePlayers != null) {
                for (player in alivePlayers!!) {
                    aliveId?.add(player!!.id)
                }
            }
            //printDeadTurn()


        }
        if (alivePlayers != null) {
            if (isItTheEnd(didAngeWin) != 0) {
                Log.e("FUN", "FIN DE LA PARTIE : " + isItTheEnd(didAngeWin))
                mDatabase.child("Party").child(gameName).child("endGame").setValue(true)
                mDatabase.child("Party").child(gameName).child("winner").setValue(isItTheEnd(didAngeWin))
                manager.FinJeuFragment(context)

            }
        }

        lowerFlagDead()
        if(isHunterDead){
            if (!game!!.Flags!!.ChasseurFlag) {
                //Log.d("FUN", "tour du chasseur")
                raiseFlagChasseur()
            } else {
                if (!game!!.Flags!!.VoteFlag && !game!!.Flags!!.DeadFlag && game!!.FinishFlags!!.ChasseurFlag) {
                    //Log.e("FUN", "Heure du vote")
                    raiseFlagVote()
                } else {
                    if (game!!.FinishFlags!!.VoteFlag) {
                        Log.e("FUN", "Fin de jour, lancement nuit")
                        night()
                        mDatabase.child("Party").child(gameName).child("nightGame").setValue(true)
                    }
                }
            }
        }
        else{
            if(!game!!.Flags!!.VoteFlag && !game!!.Flags!!.DeadFlag){
                Log.e("FUN", "Heure du vote")
                raiseFlagVote()
            } else {
                if (game!!.FinishFlags!!.VoteFlag) {
                    Log.e("FUN", "Fin de jour, lancement nuit")
                    night()
                    mDatabase.child("Party").child(gameName).child("nightGame").setValue(true)
                }
            }
        }


        //lowerFlagPrintDead()
    }

    private fun checkDeadAfterVote() {
        mDatabase.child("Party").child(gameName).child("Flags").child("DeadFlag").setValue(false)
        var deadPlayer: String? = game!!.voteResult
        if (deadPlayer != null) {
            for (player in alivePlayers!!) {
                if (player!!.id == deadPlayer) {
                    mDatabase.child("Users").child(player.id).child("state").setValue(false)
                    deadPlayer = null
                    mDatabase.child("Party").child(gameName).child("voteResult").setValue("")
                }
            }
            getPlayersAfterVote()
        }



    }

    private fun allGame() {
        //Log.d("FUN", "tour : "+nbTour.toString())
        if (game!!.nightGame) {
            playNight()
        } else {
            //Log.e("FUN", "day")
            playDay()
        }
    }


    private fun getParty() {

        val mPartyRef = FirebaseDatabase.getInstance().getReference("Party").child(gameName)

        mPartyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.getValue(PartyModel::class.java)
                    if (game != null) {
                        if (!game!!.endGame) {
                            mDatabase.child("Party").child(gameName).child("startGame").setValue(false)
                            getPlayers()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun launchDay() {


        if (game!!.Flags!!.TourFlag) {
            Log.e("FUN", "lancement JOUR")

            lowerFlag()

        } else {
            raiseFlagTour()
        }
    }

    private fun getPlayersAfterVote() {
        val id: String = auth.currentUser!!.uid

        flagDead = true
        val mUsersRef = FirebaseDatabase.getInstance().getReference("Users")
        val user: MutableList<PlayerModel?> = arrayListOf()
        mUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    alivePlayers = arrayListOf()
                    for (i in aliveId!!) {
                        for (u in dataSnapshot.children) {
                            val users = u.getValue(PlayerModel::class.java)
                            if (i == users!!.id) {
                                alivePlayers!!.add(users)
                            }
                        }
                    }
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
                    raiseFlagDead()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

    }

    private fun getPlayers() {
        val id: String = auth.currentUser!!.uid


        val mUsersRef = FirebaseDatabase.getInstance().getReference("Users")
        val user: MutableList<PlayerModel?> = arrayListOf()
        mUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    alivePlayers = arrayListOf()
                    for (i in aliveId!!) {
                        for (u in dataSnapshot.children) {
                            val users = u.getValue(PlayerModel::class.java)
                            if (i == users!!.id) {
                                alivePlayers!!.add(users)
                            }
                        }
                    }
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
                    allGame()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

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

