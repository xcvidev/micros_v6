package com.xcvi.micros.domain.model.food
object RdaCalculator {

    data class VitaminsRDA(
        val vitaminA: Double,     // µg
        val vitaminB1: Double,    // mg (Thiamin)
        val vitaminB2: Double,    // mg (Riboflavin)
        val vitaminB3: Double,    // mg NE (Niacin)
        val vitaminB4: Double,    // mg (Choline) – not in Codex, set to 0
        val vitaminB5: Double,    // mg (Pantothenic Acid)
        val vitaminB6: Double,    // mg
        val vitaminB9: Double,    // µg (Folate)
        val vitaminB12: Double,   // µg
        val vitaminC: Double,     // mg
        val vitaminD: Double,     // µg
        val vitaminE: Double,     // mg
        val vitaminK: Double      // µg
    ) {
        val vitaminBTotal: Double
            get() = vitaminB1 + vitaminB2 + vitaminB3 + vitaminB5 + vitaminB6 + (vitaminB9 / 1000) + (vitaminB12 / 1000)
    }

    fun forVitamins(): VitaminsRDA {
        return VitaminsRDA(
            vitaminA = 800.0,
            vitaminB1 = 1.2,
            vitaminB2 = 1.4,
            vitaminB3 = 18.0,
            vitaminB4 = 0.0, // Not part of Codex NRVs
            vitaminB5 = 5.0,
            vitaminB6 = 1.4,
            vitaminB9 = 400.0,
            vitaminB12 = 2.4,
            vitaminC = 80.0,
            vitaminD = 5.0,
            vitaminE = 10.0,
            vitaminK = 60.0
        )
    }

    data class MineralsRDA(
        val potassium: Double, // mg
        val calcium: Double,   // mg
        val magnesium: Double, // mg
        val iron: Double,      // mg
        val sodium: Double     // mg
    )

    fun forMinerals(): MineralsRDA {
        return MineralsRDA(
            potassium = 3500.0,
            calcium = 800.0,
            magnesium = 300.0,
            iron = 14.0,
            sodium = 2000.0
        )
    }
}
