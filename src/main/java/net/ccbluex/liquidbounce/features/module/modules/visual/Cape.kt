package net.ccbluex.liquidbounce.features.module.modules.visual



import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.ListValue
import net.minecraft.util.ResourceLocation
import java.util.*

@ModuleInfo(name = "Cape", category = ModuleCategory.VISUAL, defaultOn = true, array = false)
class Cape : Module() {

    val styleValue = ListValue(
        "Style",
        arrayOf(
            "AQUALANTIC"
        ),
        "AQUALANTIC"
    )

    private val capeCache = hashMapOf<String, CapeStyle>()
    fun getCapeLocation(value: String): ResourceLocation {
        if (capeCache[value.uppercase(Locale.getDefault())] == null) {
            try {
                capeCache[value.uppercase(Locale.getDefault())] =
                    CapeStyle.valueOf(value.uppercase(Locale.getDefault()))
            } catch (e: Exception) {
                capeCache[value.uppercase(Locale.getDefault())] = CapeStyle.AQUALANTIC
            }
        }
        return capeCache[value.uppercase(Locale.getDefault())]!!.location
    }

    enum class CapeStyle(val location: ResourceLocation) {
        AQUALANTIC(ResourceLocation("crosssine/cape/AQUALANTIC.png")),
    }

    override val tag: String
        get() = styleValue.get()

    init {
        state = true
    }
}
