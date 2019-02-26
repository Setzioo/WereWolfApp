package fr.isen.cata.werewolfapp

class LobbyModel(_masterId: String = "0", _name: String = "", _nbPlayer: Int = 0, _listPlayer: List<String>? = null) {
    var masterId: String = _masterId
    var name: String = _name
    var nbPlayer: Int = _nbPlayer
    var listPlayer: List<String>? = _listPlayer
    var startGame: Boolean = false
    }
