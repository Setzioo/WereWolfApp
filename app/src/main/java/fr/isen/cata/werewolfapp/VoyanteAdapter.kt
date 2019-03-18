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

class VoyanteAdapter(private val players: ArrayList<PlayerModel?>): RecyclerView.Adapter<VoyanteAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    var victimPlayer: PlayerModel? = null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        getPlayerAvatar(holder, players[position]!!)
        holder.pseudoButton.text = players[position]!!.pseudo

        getSelectedChange(holder, position)

        holder.cards.setOnClickListener {
            val mDatabase = FirebaseDatabase.getInstance().reference

            for(player in players){
                if(player!!.id == players[position]!!.id){
                    mDatabase.child("Users").child(player.id).child("selected").setValue(true)
                } else {
                    mDatabase.child("Users").child(player.id).child("selected").setValue(false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.voyante_vote_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.avatarPlayerVoyante)
        var pseudoButton: Button = itemView.findViewById(R.id.pseudoPlayerVoyante)
        var cards: CardView = itemView.findViewById(R.id.voyanteCard)
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
        //val playersSelected: ArrayList<PlayerModel?> = ArrayList()

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        if (tempPlayer!!.id == players[position]!!.id) {
                            if(tempPlayer.selected){
                                holder.pseudoButton.setBackgroundResource(R.drawable.pseudoselectedshape)
                                holder.pseudoButton.setTextColor(Color.BLACK)
                                victimPlayer = tempPlayer
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