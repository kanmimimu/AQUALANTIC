package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import java.awt.Color

/**
 * Material Design 3 Color System - Light Theme
 * Based on Material Design 3 specification
 */
object M3Colors {
    
    // Primary (Purple baseline)
    val primary = Color(103, 80, 164)              // #6750A4
    val onPrimary = Color(255, 255, 255)           // #FFFFFF
    val primaryContainer = Color(234, 221, 255)    // #EADDFF
    val onPrimaryContainer = Color(33, 0, 93)      // #21005E
    
    // Secondary
    val secondary = Color(98, 91, 113)             // #625B71
    val onSecondary = Color(255, 255, 255)
    val secondaryContainer = Color(232, 222, 248)  // #E8DEF8
    val onSecondaryContainer = Color(29, 25, 43)   // #1D192B
    
    // Tertiary
    val tertiary = Color(125, 82, 96)              // #7D5260
    val onTertiary = Color(255, 255, 255)
    val tertiaryContainer = Color(255, 216, 228)   // #FFD8E4
    val onTertiaryContainer = Color(49, 17, 29)    // #31111D
    
    // Error
    val error = Color(179, 38, 30)                 // #B3261E
    val onError = Color(255, 255, 255)
    val errorContainer = Color(249, 222, 220)      // #F9DEDC
    val onErrorContainer = Color(65, 14, 11)       // #410E0B
    
    // Surface colors (Light theme)
    val surface = Color(254, 247, 255)             // #FEF7FF
    val surfaceDim = Color(222, 216, 225)          // #DED8E1
    val surfaceBright = Color(254, 247, 255)       // #FEF7FF
    val surfaceContainerLowest = Color(255, 255, 255)  // #FFFFFF
    val surfaceContainerLow = Color(247, 242, 250)     // #F7F2FA
    val surfaceContainer = Color(243, 237, 247)        // #F3EDF7
    val surfaceContainerHigh = Color(236, 230, 240)    // #ECE6F0
    val surfaceContainerHighest = Color(230, 224, 233) // #E6E0E9
    
    // Text colors
    val onSurface = Color(28, 27, 31)              // #1C1B1F
    val onSurfaceVariant = Color(73, 69, 79)       // #49454F
    
    // Outline
    val outline = Color(121, 116, 126)             // #79747E
    val outlineVariant = Color(202, 196, 208)      // #CAC4D0
    
    // Inverse
    val inverseSurface = Color(49, 48, 51)         // #313033
    val inverseOnSurface = Color(244, 239, 244)    // #F4EFF4
    val inversePrimary = Color(208, 188, 255)      // #D0BCFF
    
    // Scrim & Shadow
    val scrim = Color(0, 0, 0, 80)
    val shadow = Color(0, 0, 0, 30)
    
    // State layers
    fun hovered(base: Color): Color = Color(
        (base.red * 0.92 + 0 * 0.08).toInt().coerceIn(0, 255),
        (base.green * 0.92 + 0 * 0.08).toInt().coerceIn(0, 255),
        (base.blue * 0.92 + 0 * 0.08).toInt().coerceIn(0, 255),
        base.alpha
    )
    
    fun pressed(base: Color): Color = Color(
        (base.red * 0.88 + 0 * 0.12).toInt().coerceIn(0, 255),
        (base.green * 0.88 + 0 * 0.12).toInt().coerceIn(0, 255),
        (base.blue * 0.88 + 0 * 0.12).toInt().coerceIn(0, 255),
        base.alpha
    )
    
    fun withAlpha(color: Color, alpha: Int): Color = Color(color.red, color.green, color.blue, alpha.coerceIn(0, 255))
}

/**
 * Material Design 3 Shape & Dimension constants
 */
object M3Dimensions {
    // Corner radius (M3 spec)
    const val cornerExtraSmall = 4f
    const val cornerSmall = 8f
    const val cornerMedium = 12f
    const val cornerLarge = 16f
    const val cornerExtraLarge = 28f
    const val cornerFull = 50f
    
    // Elevation
    const val elevationLevel0 = 0f
    const val elevationLevel1 = 1f
    const val elevationLevel2 = 3f
    const val elevationLevel3 = 6f
    const val elevationLevel4 = 8f
    const val elevationLevel5 = 12f
    
    // Component sizes (M3 spec)
    const val navRailWidth = 80f
    const val navRailItemHeight = 56f
    const val moduleCardHeight = 56f
    const val moduleCardExpandedPadding = 12f
    const val switchWidth = 52f
    const val switchHeight = 32f
    const val switchThumbSize = 24f
    const val sliderTrackHeight = 4f
    const val sliderThumbSize = 20f
    
    // Spacing (M3 spec)
    const val spacingXs = 4f
    const val spacingSm = 8f
    const val spacingMd = 12f
    const val spacingLg = 16f
    const val spacingXl = 24f
}
