package fr.isen.cata.werewolfapp

import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class CupidonAdapter(private val players: ArrayList<PlayerModel?>) : RecyclerView.Adapter<CupidonAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    var victimPlayer: PlayerModel? = null
    var victimPlayer2: PlayerModel? = null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        getPlayerAvatar(holder, players[position]!!)
        holder.pseudoButton.text = players[position]!!.pseudo

        getSelectedChange(holder, position)

        holder.cards.setOnClickListener {
            val mDatabase = FirebaseDatabase.getInstance().reference

            if (victimPlayer != null && victimPlayer2 != null) {
                Log.e(
                    "CUPIDON",
                    players[position]!!.pseudo + " - " + victimPlayer!!.pseudo + " - " + victimPlayer2!!.pseudo
                )
            }

            if ((victimPlayer2 != null && players[position]!!.pseudo == victimPlayer2!!.pseudo) && (victimPlayer != null && players[position]!!.pseudo == victimPlayer!!.pseudo)) {
                mDatabase.child("Users").child(victimPlayer!!.id).child("selected").setValue(false)
                victimPlayer = null
                victimPlayer2 = null
                Log.e("CUPIDON", players[position]!!.pseudo + "  n'est plus sélectionné")
            } else if (victimPlayer != null && players[position]!!.pseudo == victimPlayer!!.pseudo) {
                mDatabase.child("Users").child(victimPlayer!!.id).child("selected").setValue(false)
                victimPlayer = null
                Log.e("CUPIDON", players[position]!!.pseudo + "  n'est plus sélectionné")
            } else if (victimPlayer2 != null && players[position]!!.pseudo == victimPlayer2!!.pseudo) {
                mDatabase.child("Users").child(victimPlayer2!!.id).child("selected").setValue(false)
                victimPlayer2 = null
                Log.e("CUPIDON", players[position]!!.pseudo + " n'est plus sélectionné")
            } else if (victimPlayer != null && victimPlayer2 != null) {
                Log.e("CUPIDON", "---DEJA-DEUX-JOUEURS")
            } else {
                mDatabase.child("Users").child(players[position]!!.id).child("selected").setValue(true)
                Log.e("CUPIDON", players[position]!!.pseudo + " selectionné !")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cupidon_vote_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.avatarPlayerCupidon)
        var pseudoButton: Button = itemView.findViewById(R.id.pseudoPlayerCupidon)
        var cards: CardView = itemView.findViewById(R.id.cupidonCard)
    }

    private fun getPlayerAvatar(holder: ViewHolder, player: PlayerModel) {
        val storageReference = FirebaseStorage.getInstance().reference.child(player.id + "/avatar")

        storageReference.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            Picasso.get()
                .load(it)
                .into(holder.avatar)
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    private fun getSelectedChange(holder: ViewHolder, position: Int) {
        val mUserReference = mDatabase.child("Users")
        var playerExist: Boolean = false

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        if (tempPlayer!!.id == players[position]!!.id) {
                            if (tempPlayer.selected) {
                                playerExist = false
                                if (victimPlayer != null) {
                                    if (victimPlayer!!.id == tempPlayer.id) {
                                        playerExist = true
                                    }
                                }
                                if (victimPlayer2 != null) {
                                    if (victimPlayer2!!.id == tempPlayer.id) {
                                        playerExist = true
                                    }
                                }
                                if (victimPlayer == null && !playerExist) {
                                    victimPlayer = tempPlayer
                                    Log.e("CUPIDON", "nouvelle victime 1 : " + victimPlayer!!.pseudo)
                                } else if (victimPlayer2 == null && !playerExist) {
                                    victimPlayer2 = tempPlayer
                                    Log.e("CUPIDON", "nouvelle victime 2 : " + victimPlayer2!!.pseudo)
                                }

                                holder.pseudoButton.setBackgroundResource(R.drawable.pseudoselectedshape)
                                holder.pseudoButton.setTextColor(Color.BLACK)

                            } else {
                                holder.pseudoButton.setBackgroundResource(R.drawable.pseudoshape)
                                holder.pseudoButton.setTextColor(Color.WHITE)
                            }
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