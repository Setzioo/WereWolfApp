package fr.isen.cata.werewolfapp

data class LobbyModel(
    val id: Int,
    val masterId: Int,
    val name: String,
    val listPlayer: List<PlayerModel>
)