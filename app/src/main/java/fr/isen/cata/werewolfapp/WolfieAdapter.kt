package fr.isen.cata.werewolfapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class WolfieAdapter(private val players: ArrayList<PlayerModel?>) : RecyclerView.Adapter<WolfieAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        getPlayerAvatar(holder, players[position]!!)
        holder.pseudo.text = players[position]!!.pseudo

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.loup_horizontal_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.wolfieFace)
        var pseudo: TextView = itemView.findViewById(R.id.wolfieName)
    }

    private fun getPlayerAvatar(holder: ViewHolder, player: PlayerModel) {
        val storageReference = FirebaseStorage.getInstance().reference.child(player.id + "/avatar")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get()
                .load(it)
                .into(holder.avatar)
        }.addOnFailureListener {
        }
    }

}



