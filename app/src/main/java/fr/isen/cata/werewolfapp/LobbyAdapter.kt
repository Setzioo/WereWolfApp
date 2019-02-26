package fr.isen.cata.werewolfapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LobbyAdapter(private val lobbies: ArrayList<LobbyModel?>): RecyclerView.Adapter<LobbyAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = lobbies[position]!!.name
        val nbPlayerMessage = lobbies[position]!!.listPlayer?.size.toString() + "/" + lobbies[position]!!.nbPlayer.toString()
        holder.nbPlayer.text = nbPlayerMessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_lobby_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lobbies.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.nameText)
        var nbPlayer: TextView = itemView.findViewById(R.id.nbPlayerText)
    }
}