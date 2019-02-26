package fr.isen.cata.werewolfapp

class LobbyModel(_id: Int = 0, _masterId: Int = 0, _name: String = "", _nbPlayer: Int = 0, _listPlayer: List<Int>? = null) {
    var id: Int = _id
    var masterId: Int = _masterId
    var name: String = _name
    var nbPlayer: Int = _nbPlayer
    var listPlayer: List<Int>? = _listPlayer
    }
