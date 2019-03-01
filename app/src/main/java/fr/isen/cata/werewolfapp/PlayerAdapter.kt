package fr.isen.cata.werewolfapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PlayerAdapter(private val players: ArrayList<PlayerModel?>): RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pseudo.text = players[position]!!.pseudo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_lobby_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pseudo: TextView = itemView.findViewById(R.id.pseudoText)
    }
}

