package fr.isen.cata.werewolfapp

import android.graphics.Bitmap
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class LoupAdapter(private val players: ArrayList<PlayerModel?>): RecyclerView.Adapter<LoupAdapter.ViewHolder>() {

    val mDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth

    var currentVote:String = ""


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()

        holder.avatar.setImageBitmap(players[position]!!.avatar)

        holder.pseudo.text = players[position]!!.pseudo
        val nbVotesString = players[position]!!.nbVotesLoup.toString()
        holder.nbVotes.text = "$nbVotesString votes"

        holder.card.setOnClickListener {
            val id = players[position]!!.id
            changeVote(id)
        }

    }

    private fun changeVote(id: String) {

        val mUserReference = mDatabase.child("Users")


        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)

                        if (tempPlayer!!.id == id)
                        {
                            tempPlayer.nbVotesLoup += 1
                            mUserReference.child(id).child("nbVotesLoup").setValue(tempPlayer.nbVotesLoup)
                        }

                        if (tempPlayer.id == currentVote)
                        {
                            tempPlayer.nbVotesLoup -= 1
                            mUserReference.child(currentVote).child("nbVotesLoup").setValue(tempPlayer.nbVotesLoup)
                        }


                    }
                    currentVote = id

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.loup_vote_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.avatarPlayerLoup)
        var pseudo: TextView = itemView.findViewById(R.id.pseudoPlayerLoup)
        var nbVotes: TextView = itemView.findViewById(R.id.nbVotesLoup)
        var card: CardView = itemView.findViewById(R.id.playerCardLoup)
    }



}



