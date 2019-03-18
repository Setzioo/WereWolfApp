package fr.isen.cata.werewolfapp

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.android.synthetic.main.fragment_loup.*
import java.lang.Exception

//TODO : Vote parmi les vivants


class LoupFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: LoupAdapter
    private lateinit var players: ArrayList<PlayerModel?>
    private lateinit var avatars: ArrayList<Bitmap?>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        Log.e("FUN", "LOUP")
        Toast.makeText(context, "Loups", Toast.LENGTH_LONG).show()

        loupRecyclerView.layoutManager = GridLayoutManager(context!!,2)

        players = ArrayList()

        adapter = LoupAdapter(players)
        loupRecyclerView.adapter = adapter

        initVotes()

        getVillagers()

        Handler().postDelayed({
            setListenerOnUsers()
        },1000)




    }



    private fun getAvatars(player: PlayerModel?) {
        val storageReference = FirebaseStorage.getInstance().reference.child(player!!.id + "/avatar")

        storageReference.downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'

            class MyTarget : Target{
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    player.avatar = bitmap
                    players.add(player)
                    adapter.notifyDataSetChanged()
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

            }

            Picasso.get()
                .load(it)
                .into(MyTarget())
        }.addOnFailureListener {
            // Handle any errors
        }


    }

    private fun setListenerOnUsers() {
        val mUserReference = mDatabase.child("Users")

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val avatars: ArrayList<Bitmap?> = ArrayList()

                    for (i in players) {
                        avatars.add(i!!.avatar)
                    }
                    players.clear()
                    for ((cmp,i) in dataSnapshot.children.withIndex()) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                            if (cmp<avatars.size)
                            {
                                tempPlayer!!.avatar = avatars[cmp]
                            }
                        players.add(tempPlayer)

                    }

                    adapter.notifyDataSetChanged()


                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun getVillagers() {
        val mUserReference = mDatabase.child("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    players.clear()

                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        mDatabase.child("Users").child(tempPlayer!!.id).child("nbVotesLoup").setValue(0)
                        getAvatars(tempPlayer)

                    }




                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun initVotes() {
        val mUserReference = mDatabase.child("Users")

        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        val tempPlayer = i.getValue(PlayerModel::class.java)
                        mDatabase.child("Users").child(tempPlayer!!.id).child("nbVotesLoup").setValue(0)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loup, container, false)
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        fun newInstance() = LoupFragment()
    }
}
