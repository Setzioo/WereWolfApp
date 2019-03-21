package fr.isen.cata.werewolfapp

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PlayerAdapter(private val players: ArrayList<String?>) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null
    private var currentLobby: LobbyModel? = null
    private var masterPlayerId: String? = null
    private var gameName: String? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        idIntoName(players[position]!!, holder)

        getCurrentGame(holder, position)

        holder.kickButton.setOnClickListener {

            changeDatabase(players[position]!!)
            players.removeAt(position)
            notifyDataSetChanged()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_player_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pseudo: TextView = itemView.findViewById(R.id.lobbyPseudoText)
        var kickButton: Button = itemView.findViewById(R.id.kickButton)
        var masterImage: ImageView = itemView.findViewById(R.id.imageMaster)
    }

    private fun getCurrentGame(holder: PlayerAdapter.ViewHolder, position: Int) {

        val mUserReference = FirebaseDatabase.getInstance().getReference("")
        val id: String = auth.currentUser!!.uid

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.child("Users").children) {
                        users.add(i.getValue(PlayerModel::class.java))
                    }
                    for (i in users) {
                        if (i?.id == id) {
                            currentPlayer = i
                            gameName = currentPlayer!!.currentGame!!
                        }
                    }
                }
                if (dataSnapshot.exists()) {
                    currentLobby = dataSnapshot.child("Lobby").child(gameName!!).getValue(LobbyModel::class.java)
                    masterPlayerId = currentLobby!!.masterId
                    if(players[position] == masterPlayerId) {
                        holder.kickButton.setTextColor(Color.WHITE)
                        holder.kickButton.setBackgroundResource(R.drawable.buttonshapelocked)
                        holder.kickButton.isEnabled = false
                        holder.masterImage.setImageResource(R.drawable.wolf_moon)
                    }
                    if(id != masterPlayerId){
                        holder.kickButton.setTextColor(Color.WHITE)
                        holder.kickButton.setBackgroundResource(R.drawable.buttonshapelocked)
                        holder.kickButton.isEnabled = false
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })

    }

    private fun changeDatabase(idToRemove: String) {

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
                            updateList(idToRemove)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun updateList(idToRemove: String) {

        val gameName = currentPlayer!!.currentGame

        mDatabase.child("Lobby").child(gameName!!).child("listPlayer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {

                        val list = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})



                        list!!.remove(idToRemove)


                        mDatabase.child("Lobby").child(gameName).child("listPlayer").setValue(list)
                        mDatabase.child("Users").child(idToRemove).child("inLobby").setValue(false)

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                    // ...
                }
            })
    }


    private fun idIntoName(idPlayer: String, holder: ViewHolder) {

        val mUserReference = FirebaseDatabase.getInstance().getReference("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: MutableList<PlayerModel?> = arrayListOf()
                if (dataSnapshot.exists()) {
                    user.clear()
                    for (i in dataSnapshot.children) {
                        user.add(i.getValue(PlayerModel::class.java))
                    }
                    for (i in user) {
                        if (i?.id == idPlayer) {
                            holder.pseudo.text = i.pseudo
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

}



