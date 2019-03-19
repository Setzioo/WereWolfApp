package fr.isen.cata.werewolfapp

class PartyModel(
    _masterId: String = "0",
    _name: String = "",
    _nbPlayer: Int = 0,
    _listPlayer: MutableList<String>? = null,
    _startGame: Boolean = false
) {
    var masterId: String = _masterId
    var name: String = _name
    var nbPlayer: Int = _nbPlayer
    var listPlayer: MutableList<String>? = _listPlayer
    var startGame: Boolean = _startGame
    var Flags: Flagmodel? = null
    var FinishFlags: Flagmodel? = null
    var deathPotion: Boolean = true
    var lifePotion: Boolean = true
    var Flags : FlagModel? = null
    var FinishFlags : FlagModel? = null
    var deathPotion: Boolean = false
    var lifePotion: Boolean = false
    var voteResult: String = ""//id of the dead
    var nightGame: Boolean = false
    var endGame: Boolean = false
    var wolfKill: String = ""
    var winner: Int = 0
}