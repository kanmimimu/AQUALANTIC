package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.utils.Rotation


interface RotationMode {

    fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation?
    

    fun calculateSearchRotation(
        placeRotation: PlaceRotation,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean,
        steps4590: ArrayList<Float>
    ): Rotation
}
