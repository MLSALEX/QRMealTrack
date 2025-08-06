package com.example.qrmealtrack.presentation.utils

import androidx.compose.ui.graphics.Color
import com.example.qrmealtrack.domain.model.ReceiptCategory
import javax.inject.Inject

class CategoryColorProvider @Inject constructor() {
    private val colors = mapOf(
        ReceiptCategory.MEALS.key to Color(0xFF00FFFF),
        ReceiptCategory.CLOTHING.key to Color(0xFF0066FF),       // яркий голубой
        ReceiptCategory.BEAUTY.key to Color(0xFFFF66CC),         // розово-неоновый
        ReceiptCategory.TRANSPORT.key to Color(0xFFFFFF66),      // желто-неоновый
        ReceiptCategory.GROCERIES.key to Color(0xFF00FF66),      // зелёный лайм
        ReceiptCategory.NO_CATEGORY.key to Color(0xFF5F9EA0)     // серыый неон
    )

    fun getColorForCategory(key: String): Color = colors[key] ?: Color(0xFF5F9EA0)
}

object NeonColors {

    val CyanNeon = Color(0xFF00FFFF)         // 1
    val BrightSkyBlue = Color(0xFF00E5FF)    // 2
    val LightNeonBlue = Color(0xFF00CCFF)    // 3
    val DeepSkyBlue = Color(0xFF00BFFF)      // 4
    val NeonAqua = Color(0xFF00AFFF)         // 5
    val SkyBlue = Color(0xFF0099FF)          // 6
    val DodgerBlue = Color(0xFF007FFF)       // 7
    val BlueNeon = Color(0xFF0066FF)         // 8
    val IndigoNeon = Color(0xFF0055FF)       // 9
    val RoyalBlue = Color(0xFF003CFF)        // 10

    val NeonMagenta = Color(0xFFFF00FF)      // 11
    val PinkNeon = Color(0xFFFF33CC)         // 12
    val BubblegumPink = Color(0xFFFF66CC)    // 13
    val BarbiePink = Color(0xFFFF3399)       // 14
    val NeonSalmon = Color(0xFFFF6699)       // 15
    val NeonRose = Color(0xFFFF0066)         // 16
    val FuchsiaRed = Color(0xFFFF0033)       // 17
    val RoseNeon = Color(0xFFFF3366)         // 18
    val CoralNeon = Color(0xFFFF5050)        // 19
    val OrangeNeon = Color(0xFFFF6600)       // 20

    val YellowNeon = Color(0xFFFFFF00)       // 21
    val LimeYellow = Color(0xFFCCFF00)       // 22
    val AcidGreen = Color(0xFF99FF00)        // 23
    val ElectricGreen = Color(0xFF66FF00)    // 24
    val BrightGreen = Color(0xFF33FF00)      // 25
    val NeonGreen = Color(0xFF00FF00)        // 26
    val GreenMint = Color(0xFF00FF66)        // 27
    val MintNeon = Color(0xFF00FF99)         // 28
    val AquaNeon = Color(0xFF00FFCC)         // 29
    val NeonIce = Color(0xFF00FFFF)          // 30 (повтор)

    val DeepPink = Color(0xFFFF1493)         // 31
    val Tomato = Color(0xFFFF6347)           // 32
    val NeonOrange = Color(0xFFFFA500)       // 33
    val GoldNeon = Color(0xFFFFD700)         // 34
    val GreenYellow = Color(0xFFADFF2F)      // 35
    val LawnGreen = Color(0xFF7CFC00)        // 36
    val LimeGreen = Color(0xFF32CD32)        // 37
    val Turquoise = Color(0xFF40E0D0)        // 38
    val NeonAzure = Color(0xFF1E90FF)        // 39
    val RoyalNeonBlue = Color(0xFF4169E1)    // 40

    val BlueViolet = Color(0xFF8A2BE2)       // 41
    val MediumOrchid = Color(0xFFBA55D3)     // 42
    val Orchid = Color(0xFFDA70D6)           // 43
    val Thistle = Color(0xFFD8BFD8)          // 44
    val HotPink = Color(0xFFFF69B4)          // 45
    val OrangeRed = Color(0xFFFF4500)        // 46
    val DarkOrange = Color(0xFFFF8C00)       // 47
    val PowderBlue = Color(0xFFB0E0E6)       // 48
    val CadetBlue = Color(0xFF5F9EA0)        // 49
    val DarkTurquoise = Color(0xFF00CED1)    // 50

    // Список всех
    val all = listOf(
        CyanNeon, BrightSkyBlue, LightNeonBlue, DeepSkyBlue, NeonAqua,
        SkyBlue, DodgerBlue, BlueNeon, IndigoNeon, RoyalBlue,
        NeonMagenta, PinkNeon, BubblegumPink, BarbiePink, NeonSalmon,
        NeonRose, FuchsiaRed, RoseNeon, CoralNeon, OrangeNeon,
        YellowNeon, LimeYellow, AcidGreen, ElectricGreen, BrightGreen,
        NeonGreen, GreenMint, MintNeon, AquaNeon, NeonIce,
        DeepPink, Tomato, NeonOrange, GoldNeon, GreenYellow,
        LawnGreen, LimeGreen, Turquoise, NeonAzure, RoyalNeonBlue,
        BlueViolet, MediumOrchid, Orchid, Thistle, HotPink,
        OrangeRed, DarkOrange, PowderBlue, CadetBlue, DarkTurquoise
    )
}
