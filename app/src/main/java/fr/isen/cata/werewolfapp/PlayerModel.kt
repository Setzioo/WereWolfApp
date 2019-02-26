package fr.isen.cata.werewolfapp

data class PlayerModel (
    val id: Int,
    val pseudo: String,
    val avatar: String,
    val role: RoleModel,
    val state: Boolean,
    val charmed: Boolean
)