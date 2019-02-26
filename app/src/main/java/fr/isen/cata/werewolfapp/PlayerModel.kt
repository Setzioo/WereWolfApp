package fr.isen.cata.werewolfapp

import android.graphics.Bitmap

class PlayerModel(_id: String="0", _pseudo: String=""/*, _avatar: Bitmap?=null, _role: RoleModel?=null, _state: Boolean=true, _charmed: Boolean=false, _connected: Boolean=false*/) {
    var id: String = _id
    var pseudo: String = _pseudo
    var avatar: Bitmap? = null
    var role: RoleModel? = null
    var state: Boolean = true
    var charmed: Boolean = false
    var connected: Boolean = false
}
