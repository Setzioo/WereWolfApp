package fr.isen.cata.werewolfapp

import android.graphics.Bitmap

class PlayerModel(_id: Int=0, _pseudo: String="", _avatar: Bitmap?=null, _role: RoleModel?=null, _state: Boolean=true, _charmed: Boolean=false, _connected: Boolean=false) {
    var id: Int = _id
    var pseudo: String = _pseudo
    var avatar: Bitmap? = _avatar
    var role: RoleModel? = _role
    var state: Boolean = _state
    var charmed: Boolean = _charmed
    var connected: Boolean = _connected
}
