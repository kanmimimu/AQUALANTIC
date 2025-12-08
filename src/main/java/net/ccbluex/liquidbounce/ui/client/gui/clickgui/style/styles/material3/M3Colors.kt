package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import java.awt.Color

/**
 * Material Design 3 Color System - Dark Theme
 */
object M3Colors {
    // Surface colors (with explicit alpha)
    val surface = Color(28, 27, 31, 255)              // #1C1B1F
    val surfaceDim = Color(20, 19, 24, 255)           // #141318
    val surfaceBright = Color(59, 56, 62, 255)        // #3B383E
    val surfaceContainerLowest = Color(15, 14, 18, 255)
    val surfaceContainerLow = Color(33, 31, 36, 255)
    val surfaceContainer = Color(37, 35, 40, 255)     // #252328
    val surfaceContainerHigh = Color(42, 40, 46, 255)
    val surfaceContainerHighest = Color(54, 52, 59, 255)
    
    // Primary accent (Purple)
    val primary = Color(208, 188, 255, 255)           // #D0BCFF
    val onPrimary = Color(56, 30, 114, 255)           // #381E72
    val primaryContainer = Color(79, 55, 139, 255)    // #4F378B
    val onPrimaryContainer = Color(234, 221, 255, 255)// #EADDFF
    
    // Secondary
    val secondary = Color(204, 194, 220, 255)         // #CCC2DC
    val onSecondary = Color(51, 45, 65, 255)
    val secondaryContainer = Color(74, 68, 88, 255)   // #4A4458
    val onSecondaryContainer = Color(232, 222, 248, 255)
    
    // Tertiary (Pink)
    val tertiary = Color(239, 184, 200, 255)          // #EFB8C8
    val onTertiary = Color(73, 37, 50, 255)
    val tertiaryContainer = Color(99, 59, 72, 255)
    val onTertiaryContainer = Color(255, 216, 228, 255)
    
    // Error
    val error = Color(242, 184, 181, 255)             // #F2B8B5
    val onError = Color(96, 20, 16, 255)
    val errorContainer = Color(140, 29, 24, 255)
    val onErrorContainer = Color(249, 222, 220, 255)
    
    // Text colors
    val onSurface = Color(230, 225, 229, 255)         // #E6E1E5
    val onSurfaceVariant = Color(202, 196, 208, 255)  // #CAC4D0
    
    // Outline
    val outline = Color(147, 143, 153, 255)           // #938F99
    val outlineVariant = Color(73, 69, 79, 255)       // #49454F
    
    // Inverse
    val inverseSurface = Color(230, 225, 229, 255)
    val inverseOnSurface = Color(49, 48, 51, 255)
    val inversePrimary = Color(103, 80, 164, 255)     // #6750A4
    
    // Scrim & Shadow
    val scrim = Color(0, 0, 0, 128)
    val shadow = Color(0, 0, 0, 255)
    
    // State layers
    fun hovered(base: Color): Color = Color(
        (base.red * 0.92 + 255 * 0.08).toInt().coerceIn(0, 255),
        (base.green * 0.92 + 255 * 0.08).toInt().coerceIn(0, 255),
        (base.blue * 0.92 + 255 * 0.08).toInt().coerceIn(0, 255),
        base.alpha
    )
    
    fun pressed(base: Color): Color = Color(
        (base.red * 0.88 + 255 * 0.12).toInt().coerceIn(0, 255),
        (base.green * 0.88 + 255 * 0.12).toInt().coerceIn(0, 255),
        (base.blue * 0.88 + 255 * 0.12).toInt().coerceIn(0, 255),
        base.alpha
    )
    
    fun withAlpha(color: Color, alpha: Int): Color = Color(color.red, color.green, color.blue, alpha.coerceIn(0, 255))
}

/**
 * Material Design 3 Shape & Dimension constants
 */
object M3Dimensions {
    // Corner radius
    const val cornerExtraSmall = 4f
    const val cornerSmall = 6f
    const val cornerMedium = 10f
    const val cornerLarge = 12f
    const val cornerExtraLarge = 16f
    const val cornerFull = 10f  // For pill shapes (safe value)
    
    // Elevation (shadow)
    const val elevationLevel0 = 0f
    const val elevationLevel1 = 1f
    const val elevationLevel2 = 3f
    const val elevationLevel3 = 6f
    const val elevationLevel4 = 8f
    const val elevationLevel5 = 12f
    
    // Component sizes
    const val navRailWidth = 72f
    const val navRailItemHeight = 48f
    const val moduleCardHeight = 48f
    const val moduleCardExpandedPadding = 10f
    const val switchWidth = 44f
    const val switchHeight = 24f
    const val switchThumbSize = 18f
    const val sliderTrackHeight = 4f
    const val sliderThumbSize = 12f
    
    // Spacing
    const val spacingXs = 4f
    const val spacingSm = 8f
    const val spacingMd = 12f
    const val spacingLg = 16f
    const val spacingXl = 24f
}
