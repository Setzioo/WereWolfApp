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

    lateinit var currentRole : String
    var listId : MutableList<String>? = arrayListOf()
    var listPlayer : MutableList<PlayerModel?>? = arrayListOf()
    var alivePlayers : MutableList<PlayerModel?>? = arrayListOf()
    var gameName : String =""
    var game : PartyModel? = null
    var nbTour : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mLobbyReference = FirebaseDatabase.getInstance().reference.child("")



        //getParty()
        //getPlayers()

        manager.BeginningFragment(context)
        nbTour = 1

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
                        /*
                        if(i!!.id=="f5lJpGohtZhC4ZygEGK4sywc3yz1"){
                            currentPlayer = i
                            gameName = currentPlayer!!.currentGame!!
                            currentRole = currentPlayer!!.role!!
                        }
                        */
                    }
                }
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    if(game!=null){
                        if(game!!.listPlayer != null){
                            listId = game!!.listPlayer
                        }
                    }



                }
                if (listId != null) {
                    for(i in listId!!){
                        for(u in dataSnapshot.child("Users").children){
                            var user = u.getValue(PlayerModel::class.java)
                            if(i == user!!.id){
                                listPlayer!!.add(user)
                            }
                        }
                    }
                }
                gameListener()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


    private fun isSorciere(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Sorcière"){
                return true
            }
        }
        return false
    }

    private fun isVoyante(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Voyante"){
                return true
            }
        }
        return false
    }

    private fun isPipoteur(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Pipoteur"){
                return true
            }
        }
        return false
    }

    private fun isCupidon(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Cupidon"){
                return true
            }
        }
        return false
    }

    private fun isAnge(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Ange"){
                return true
            }
        }
        return false
    }

    private fun isChasseur(): Boolean{
        for(player in listPlayer!!){
            if(player!!.role == "Chasseur"){
                return true
            }
        }
        return false
    }

    private fun playNight(){

        val cupidon = isCupidon()
        val voyante = isVoyante()
        val sorciere = isSorciere()
        val pipoteur = isPipoteur()

        night()
        Log.e("FUN", "cupidon : "+cupidon+" voyante : "+voyante+" sorciere : "+sorciere+" pipoteur : "+pipoteur)
        if(currentPlayer!!.state){//Si vivant
            //Log.e("FUN", "Alive")
            if(cupidon){//Si cupidon alors voyante
                if(!game!!.Flags!!.CupidonFlag){//tour de cupidon
                    Log.e("FUN", "Cupi joue")
                    raiseFlagCupidon()

                }
                else{//Cupidon a joué
                    if(!game!!.Flags!!.VoyanteFlag && game!!.FinishFlags!!.CupidonFlag){//tour de la voyante
                        Log.e("FUN", "Voyante joue avec cupi")
                        raiseFlagVoyante()

                    }
                    else{//la voyante a joué
                        if(!game!!.Flags!!.LoupFlag && game!!.FinishFlags!!.VoyanteFlag){//tour des loups
                            Log.e("FUN", "loup joue avec cupi")
                            raiseFlagLoups()
                        }
                        else{//les loups ont joués
                            if(sorciere && pipoteur){
                                if(!game!!.Flags!!.SorciereFlag && game!!.FinishFlags!!.LoupFlag){//tour de la sorciere
                                    Log.e("FUN", "sorciere joue avec pipo")
                                    raiseFlagSorciere()
                                }
                                else{
                                    if(!game!!.Flags!!.PipoteurFlag && game!!.FinishFlags!!.SorciereFlag){//tour du pipoteur
                                        Log.e("FUN", "pipo joue avec sorciere")
                                        raiseFlagPipoteur()
                                    }
                                    else{
                                        if(game!!.FinishFlags!!.PipoteurFlag){
                                            launchDay()
                                        }
                                    }
                                }
                            }
                            else if(sorciere){
                                if(!game!!.Flags!!.SorciereFlag && game!!.FinishFlags!!.LoupFlag){//tour de la sorciere
                                    Log.e("FUN", "sorciere joue sans pipo")
                                    raiseFlagSorciere()
                                }
                                else{
                                    if(game!!.FinishFlags!!.SorciereFlag){
                                        launchDay()
                                    }
                                }
                            }
                            else if(pipoteur){
                                if(!game!!.Flags!!.PipoteurFlag && game!!.FinishFlags!!.LoupFlag){//tour du pipoteur
                                    Log.e("FUN", "pipo joue sans sorciere")
                                    raiseFlagPipoteur()
                                }
                                else{
                                    if(game!!.FinishFlags!!.PipoteurFlag){
                                        launchDay()
                                    }
                                }
                            }
                            else{
                                if(game!!.FinishFlags!!.LoupFlag){
                                    launchDay()
                                }
                            }
                        }
                    }
                }
            }
            else{//si pas de cupidon voyante? + loups
                if(voyante){
                    if(!game!!.Flags!!.VoyanteFlag){//Tour de la voyante
                        Log.e("FUN", "Voyante joue sans cupi")
                        raiseFlagVoyante()
                    }
                    else{//La voyante a joué
                        if(!game!!.Flags!!.LoupFlag && game!!.FinishFlags!!.VoyanteFlag){
                            Log.e("FUN", "loup joue sans cupi")
                            raiseFlagLoups()
                        }
                        else{
                            if(game!!.FinishFlags!!.LoupFlag){
                                launchDay()
                            }
                        }

                    }
                }
                else{//si pas de voyante que loups
                    Log.e("FUN", "loup joue sans voyante")
                    if(!game!!.Flags!!.LoupFlag){//tour des loups
                        raiseFlagLoups()
                    }
                    else{
                        if(game!!.FinishFlags!!.LoupFlag){
                            launchDay()
                        }
                    }

                }
                if(game!!.FinishFlags!!.LoupFlag){
                    launchDay()
                }
            }
        }
        else{
            //ecran des morts
            Toast.makeText(context, "Mort", Toast.LENGTH_LONG).show()
        }

    }

    private fun playDay(){
        if(!game!!.Flags!!.VoteFlag){
            Log.e("FUN", "check des morts de la nuit")
            checkDead()
            Log.e("FUN", "Heure du vote")
            raiseFlagVote()
        }
        else if(game!!.FinishFlags!!.VoteFlag){
            Log.e("FUN", "check mort du vote")
            listenForDead()
            lowerFlagVote()
        }
    }

    private fun night(){
        manager.NightFragment(context)
    }
    private fun cupidonTurn() {
        if(nbTour==0){
            if((currentRole=="Cupidon")){
                manager.CupidonFragment(context)//Passer le flag de cupidon a true
            }
        }
    }
    private fun voyanteTurn() {
        if(currentRole=="Voyante") {
            manager.VoyanteFragment(context)
        }
    }
    private fun loupsTurn(){
        if(currentRole=="Loup-Garou"){
            manager.LoupsFragment(context)
        }
    }
    private fun sorciereTurn(){
        if(currentRole=="Sorciere"){
            manager.SorciereFragment(context, game!!.deathPotion, game!!.lifePotion)
        }
    }
    private fun pipoteurTurn(){
        if(currentRole=="Pipoteur"){
            manager.PipoteurFragment(context)
        }
    }

    private fun voteTurn(){
        if(currentRole=="Pipoteur"){
            manager.VoteJourFragment(context)
        }
    }

    private fun raiseFlagCupidon(){
        mDatabase.child("Party").child(gameName).child("Flags").child("CupidonFlag").setValue(true)
    }
    private fun raiseFlagVoyante(){
        mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(true)
    }
    private fun raiseFlagLoups(){
        mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(true)
    }
    private fun raiseFlagSorciere(){
        mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(true)
    }
    private fun raiseFlagPipoteur(){
        mDatabase.child("Party").child(gameName).child("Flags").child("PipoteurFlag").setValue(true)
    }
    private fun raiseFlagVote(){
        mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(true)
    }
    private fun raiseFlagTour(){
        mDatabase.child("Party").child(gameName).child("Flags").child("TourFlag").setValue(true)
    }

    private fun lowerFlag(){
        mDatabase.child("Party").child(gameName).child("Flags").child("VoyanteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("LoupFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("SorciereFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("PipoteurFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("Flags").child("TourFlag").setValue(false)

        mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoyanteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("LoupFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("SorciereFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("PipoteurFlag").setValue(false)
    }

    private fun lowerFlagVote(){
        mDatabase.child("Party").child(gameName).child("Flags").child("VoteFlag").setValue(false)
        mDatabase.child("Party").child(gameName).child("FinishFlags").child("VoteFlag").setValue(false)
    }

    private fun gameListener(){
        val mPlayerReference = FirebaseDatabase.getInstance().getReference("Party").child(gameName)

        mPlayerReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    listenForFlags(dataSnapshot)
                    getParty()


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "No Flag", databaseError.toException())
            }
        })
    }

    private fun listenForFlags(dataSnapshot: DataSnapshot){
        val flags : Flagmodel? = dataSnapshot.child("Flags").getValue(Flagmodel::class.java)
        if(flags!!.CupidonFlag){
            cupidonTurn()
        }
        else if(flags.LoupFlag){
            loupsTurn()
        }
        else if(flags.PipoteurFlag){
            pipoteurTurn()
        }
        else if(flags.SorciereFlag){
            sorciereTurn()
        }
        else if(flags.VoyanteFlag){
            voyanteTurn()
        }
        else if(flags.VoteFlag){
            voteTurn()
        }
        else if(flags.TourFlag){
            nbTour++
            mDatabase.child("Party").child(gameName).child("Flags").child("TourFlag").setValue(false)
        }
        else{
            //night()
        }
    }

    private fun checkDead() {
        var deadPlayers: MutableList<PlayerModel>? = arrayListOf()
        var isLoverDead = false
        var isHunterDead = false
        if(nbTour == 1 && listPlayer != null){
            Log.d("FUN", "init alive")
            alivePlayers = listPlayer
        }
        if (alivePlayers != null) {
            for (player in alivePlayers!!) {
                Log.d("FUN", "qui est mort?")
                if (!player!!.state) {//si mort
                    Log.d("FUN", "dead night : "+player.id)
                    deadPlayers?.add(player)
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
            if (deadPlayers != null) {
                Log.d("FUN", "on verifie les morts")
                for (player in deadPlayers) {
                    if (player.role == "Chasseur") {
                        isHunterDead = true
                    }
                }
                for (dead in deadPlayers) {
                    for (p in alivePlayers!!) {
                        Log.d("FUN", "check alive/dead")
                        if (dead.id == p!!.id) {
                            alivePlayers!!.remove(p)
                        }
                    }
                }
            }

            if (isHunterDead) {

                manager.ChasseurFragment(context)
            }
        }
        if(alivePlayers != null){
            for(i in alivePlayers!!){
                Log.d("FUN", "alive : "+i!!.id)
            }
        }
        if(deadPlayers != null){
            for(i in deadPlayers!!){
                Log.d("FUN", "dead : "+i!!.id)
            }
        }
        else{
            Log.d("FUN", "no dead")
        }
    }
    private fun checkDeadAfterVote(){
        Log.e("FUN", "mort après vote")
        var deadPlayer : String? = game!!.voteResult
        if(deadPlayer!=null){
            for(player in alivePlayers!!){
                if(player!!.id == deadPlayer){
                    mDatabase.child("Users").child(player.id).child("state").setValue(false)
                }
            }
        }
        checkDead()
        Log.e("FUN", "vote finie")
    }

    private fun listenForDead(){
        checkDeadAfterVote()
        mDatabase.child("Party").child(gameName).child("voteResult").setValue("")

    }

    private fun allGame(){
        Toast.makeText(context, "role: "+currentPlayer!!.role+", state : "+currentPlayer!!.state.toString(), Toast.LENGTH_LONG).show()
        if(game!!.nightGame){
            playNight()
        }
        else{
            playDay()
        }
    }


private fun getParty(){

    val mPartyRef = FirebaseDatabase.getInstance().getReference("Party").child(gameName)

    mPartyRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                game = dataSnapshot.getValue(PartyModel::class.java)
                getPlayers()
                if(game != null){
                    if(!game!!.endGame){
                        allGame()
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    })
}

    private fun launchDay(){
        Log.e("FUN", "lancement JOUR")
        raiseFlagTour()
        Log.d("FUN", "tour : "+nbTour)
        if(game!!.Flags!!.TourFlag){
            lowerFlag()
            mDatabase.child("Party").child(gameName).child("nightGame").setValue(false)
        }
    }

private fun getPlayers(){
    val mUsersRef = FirebaseDatabase.getInstance().getReference("Users")

    mUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                for(i in listId!!){
                    for(u in dataSnapshot.children){
                        var user = u.getValue(PlayerModel::class.java)
                        if(i == user!!.id){
                            listPlayer!!.add(user)
                        }
                    }
                }
            }
            /*for(i in listPlayer!!){
                Log.e("RECCUP", "player : "+i!!.pseudo)
            }*/
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    })
}

}

