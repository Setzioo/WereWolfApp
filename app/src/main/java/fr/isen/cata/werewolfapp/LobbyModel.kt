package fr.isen.cata.werewolfapp

class LobbyModel(_masterId: String = "0", _name: String = "", _nbPlayer: Int = 0, _listPlayer: MutableList<String>? = null) {
    var masterId: String = _masterId
    var name: String = _name
    var nbPlayer: Int = _nbPlayer
    var listPlayer: MutableList<String>? = _listPlayer
    var startGame: Boolean = false
    }
