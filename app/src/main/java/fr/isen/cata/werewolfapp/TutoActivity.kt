package fr.isen.cata.werewolfapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tuto.*

class TutoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuto)

        val textLoup = "• Les LOUPS-GAROUS •\n" +
                "Chaque nuit, ils égorgent un Villageois.\n" +
                "Le jour ils se font passer pour des Villageois\n" +
                "afin de ne pas être démasqués."
        rules_role_loup.text = textLoup

        val textVillageaois = "• Simple Villageois •\n" +
                "Il n’a aucune compétence particulière, il faut juste que le joueur soit très intuitif."
        rules_role_vilageois.text = textVillageaois

        val textCupidon = "• Cupidon •\n" +
                "La première nuit, il désigne 2 joueurs qui seront follement Amoureux l’un de l’autre. Si l’un d’eux meurt, l’autre meurt de chagrin immédiatement. Un Loup-Garou et un villageois peuvent être Amoureux l'un de l'autre. Ils jouent alors contre tous les autres, Loups-Garous et Villageois.Si les amoureux survivent, alors ce sont eux qui gagnentLe cupidon peut se désigner lui-même comme un des 2 Amoureux."
        rules_role_cupidon.text = textCupidon

        val textSorciere = "• Sorcière •\n" +
                "Elle sait concocter 2 potions extrêmement puissantes :\n" +
                "une potion de guérison, pour ressusciter le joueur tué par les Loups-Garous,\n" +
                "une potion d’empoisonnement, utilisée la nuit pour éliminer un joueur. La Sorcière doit utiliser chaque potion 1 seule fois dans la partie. Elle peut se servir des ses 2 potions la même nuit. Le matin suivant l’usage de ce pouvoir, il pourra donc y avoir soit 0 mort, 1 mort ou 2 morts. La sorcière peut utiliser les potions à son profit, et donc se guérir elle-même."
        rules_roles_sorciere.text = textSorciere

        val textPipoteur = "• Pipoteurs •\n" +
                "Ennemi à la fois des villageois et des loups-garous, le joueur de flûte se réveille à la fin de chaque nuit et choisit chaque fois deux nouveaux joueurs qu'il va charmer."
        rules_roles_pipoteur.text = textPipoteur

        val textCartes = "Les cartes :"
        rules_role_carte.text = textCartes

        val textChasseur = "• Chasseur  •\n" +
                "Le chasseur, s’il se fait égorger par les Loups-Garous ou lyncher par les joueurs, a le pouvoir de répliquer en tuant immédiatement n’importe quel autre joueur."
        rules_role_chasseur.text = textChasseur

        val textVoyante = "• Voyante •\n" +
                "Chaque nuit, elle connaît la vrai personnalité d’un joueur de son choix, elle doit aider les Villageois, sans être démasquée par les Loups-Garous"
        rules_role_voyante.text = textVoyante

        val textAnge =
            "• Ange • \n" + "Le but de l'ange est de se faire éliminer dès le premier vote. S'il réussit, la partie se termine et il a gagné.\n" +
                    " Dans le cas contraire, le jeu continue mais l'ange devient un simple villageois sans pouvoir"
        rules_roles_ange.text = textAnge

    }
}
