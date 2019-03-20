package fr.isen.cata.werewolfapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PrintDeadAdapter(private val players: ArrayList<PlayerModel?>) :
    RecyclerView.Adapter<PrintDeadAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        //holder.pseudo.text = players[position]!!
        getPlayerAvatar(holder, players[position]!!)
        holder.pseudoButton.text = players[position]!!.pseudo
        holder.roleButton.text = players[position]!!.role
        if(players[position]!!.charmed){
            holder.heartIcon.setImageResource(R.drawable.coeur_amoureux)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dead_vote_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.avatarPlayerDead)
        var pseudoButton: Button = itemView.findViewById(R.id.pseudoPlayerDead)
        var roleButton: Button = itemView.findViewById(R.id.rolePlayerDead)
        var heartIcon: ImageView = itemView.findViewById(R.id.loveIcon)
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
}
