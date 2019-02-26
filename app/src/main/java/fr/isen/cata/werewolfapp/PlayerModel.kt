package fr.isen.cata.werewolfapp

class PlayerModel {
    var id: Int = 0
    var pseudo: String = ""
    var avatar: String = ""
    var role: RoleModel? = null
    var state: Boolean = true
    var charmed: Boolean = false
    var connected: Boolean = false
}