package fr.isen.cata.werewolfapp

open class RoleModel (
    val id: Int,
    val name: String,
    val description: String
)

class LoupGarouModel():RoleModel(1, "Loup-Garou", "Chaque nuit, ils dévorent un Villageois. Le jour ils essaient de masquer leur identité nocturne pour échapper à la vindicte populaire.")


class Villageois():RoleModel(2, "Villageois", "Il n'a aucune compétence particulière. Ses seules armes sont la capacité d'analyse des comportements pour identifier les Loups Garous et la force de conviction pour empêcher l'exécution de l'innocent qu'il est.")


class Voyante():RoleModel(3, "Voyante", "Chaque nuit, elle découvre la vraie personnalité d'un joueur de son choix. Elle doit aider les autres villageois, mais rester discrète pour ne pas être démasquée par les Loups-garous.")

class Cupidon():RoleModel(4, "Cupidon", "En décochant ses célèbres flèches magique, Cupidon a le pouvoir de rendre 2 personnes amoureuses à jamais. Si l'un des amoureux est éliminé, l'autre meurt de chagrin immédiatement. Si l'un des deux Amoureux est un Loup-Garou et l'autre un Villageois, le but de la partie change pour eux. Pour vivre en paix leur amour et gagner la partie, ils doivent éliminer tous les autres joueurs , Loups-Garous et Villageois, en respectant les règles du jeu.")

class Chasseur():RoleModel(5, "Chasseur", "S'il se fait dévorer par les Loups Garous ou exécuter malencontreusement par les joueurs, le Chasseur a le pouvoir de répliquer avant de rendre l'âme, en éliminant immédiatement n'importe quel autre joueur de son choix.")

class Sorciere(
    val deathPotion: Boolean,
    val lifePotion: Boolean
):RoleModel(6, "Sorcière", "Elle sait concocter 2 potions extrèmement puissantes.\nUne potion de guérison, pour ressusciter le joueur tué par les Loups-Garous, une potion d'empoisonnement, utilisée la nuit pour éliminer un joueur. La sorcière ne peut utiliser chaque potion qu\'une seule fois dans la partie. Elle peut se servir de ses 2 potions dans la même nuit.")


class Ange():RoleModel(7, "Ange", "Son objectif est d'être éliminé par le village lors du premier vote de jour.\nS'il réussit, il gagne la partie. Sinon, il devient un Simple Villageois.")

class Pipoteur():RoleModel(8, "Pipoteur", "Son objectif est d'enchanter tous les joueurs vivants de la partie. Il peut enchanter jusqu'à deux personnes par nuit.")