package fr.isen.cata.werewolfapp

data class LobbyModel(
    val id: Int,
    val nbPlayer: Int,
    val listPlayer: List<Int>,
    val masterId: Int,
    val name: String
)