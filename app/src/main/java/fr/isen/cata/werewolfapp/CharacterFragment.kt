package fr.isen.cata.werewolfapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tuto.*
import kotlinx.android.synthetic.main.fragment_cupidon.*
import kotlinx.android.synthetic.main.fragment_vision.*
import kotlinx.android.synthetic.main.layout_character.*


class CharacterFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentPlayer: PlayerModel? = null
    private var currentRole : String? = null
    var gameName: String = ""
    var game: PartyModel? = null
    var pileOfTurn: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_character, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("LANCE","Character")

        animateCards()


    }


    private fun animateCards() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 720f)

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float

            Cards.rotation = value

        }
        val valueAnimator1 = ValueAnimator.ofFloat(0f, 1f)

        valueAnimator1.addUpdateListener {
            val value = it.animatedValue as Float
            Cards.alpha = value


        }


        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 2000
        valueAnimator1.interpolator = AccelerateInterpolator()
        valueAnimator1.duration = 2000



        valueAnimator.start()
        valueAnimator1.start()

        getCurrentPlayer()
}

    fun beginCompteur(compteurMax: Long) {
        object : CountDownTimer(compteurMax * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                Handler().postDelayed({
                    mDatabase.child("Party").child(gameName).child("pileOfTurn").setValue(pileOfTurn)
                }, 1500)
            }
        }.start()
    }


    private fun getCurrentPlayer() {

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
                            changeCardImageCharacter(currentPlayer!!.role)
                        }

                    }
                }
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.child("Party").child(gameName).getValue(PartyModel::class.java)
                    pileOfTurn = game!!.pileOfTurn
                    pileOfTurn.removeAt(0)
                    beginCompteur(5)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
    fun changeCardImageCharacter(role: String?) {
        if (role == "Sorcière") {
            Cards.setImageResource(R.drawable.sorciere)
            val ruleSorciere = "Sorcière"
            characterResultText.text = ruleSorciere
        }
        if (role == "Voyante") {
            Cards.setImageResource(R.drawable.voyante)
            val ruleSorciere = "Voyante"
            characterResultText.text = ruleSorciere
        }
        if (role == "Villageois") {
            Cards.setImageResource(R.drawable.villageaois)
            val ruleVillageois = "Villageois"
            characterResultText.text = ruleVillageois
        }
        if (role == "Loup-Garou") {
            Cards.setImageResource(R.drawable.loup_garou)
            val ruleLoupGarou = "Loup-Garou"
            characterResultText.text = ruleLoupGarou
        }
        if (role == "Cupidon") {
            Cards.setImageResource(R.drawable.cupidon)
            val ruleCupidon = "Cupidon"
            characterResultText.text = ruleCupidon
        }
        if (role == "Chasseur") {
            Cards.setImageResource(R.drawable.chasseur)
            val ruleChasseur = "Chasseur"
            characterResultText.text = ruleChasseur
        }
        if (role == "Pipoteur") {
            Cards.setImageResource(R.drawable.pipoteur)
            val rulePipoteur = "Pipoteur"
            characterResultText.text = rulePipoteur
        }
        if (role == "Ange") {
            Cards.setImageResource(R.drawable.ange)
            val ruleAnge = "Ange"
            characterResultText.text = ruleAnge
        }
    }
    companion object {
        fun newInstance() = CharacterFragment()
    }
}
