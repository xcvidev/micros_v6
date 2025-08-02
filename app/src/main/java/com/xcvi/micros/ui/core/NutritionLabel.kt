package com.xcvi.micros.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NutritionLabel() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        val white = Color.White
        val gray = Color.Gray

        LabelText("Dimensioni porzione 100 g", white)
        Divider(color = white, thickness = 1.dp)
        LabelText("Quantità per porzione", white)
        Spacer(modifier = Modifier.height(8.dp))
        LabelText("Calorie 130", white)

        Divider(color = white, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        LabelText("% valori giornalieri", white)

        NutritionItem("Grassi totali", "0,2 g", "0%", white)
        NutritionItem("Grassi saturi", "0 g", "0%", gray)
        NutritionItem("Colesterolo", "0 mg", "0%", white)
        NutritionItem("Sodio", "1 mg", "0%", white)
        NutritionItem("Carboidrati totali", "28,1 g", "9%", white)
        NutritionItem("Fibra dietetica", "0,4 g", "2%", gray)
        NutritionItem("Zuccheri", "0 g", "", gray)
        NutritionItem("Proteine", "2,6 g", "", white)

        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LabelText("Vitamina A 0%", white)
            LabelText("Vitamina C 0%", white)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LabelText("Calcio 1%", white)
            LabelText("Ferro 7%", white)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "* I valori giornalieri percentuali sono basati su una dieta da 2000 calorie. " +
                    "I valori giornalieri potrebbero essere maggiori o inferiori in base al fabbisogno di calorie.",
            color = gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun NutritionItem(name: String, amount: String, percent: String, textColor: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$name $amount", color = textColor)
        if (percent.isNotBlank()) {
            Text(text = percent, color = textColor)
        }
    }
}

@Composable
fun LabelText(text: String, color: Color) {
    Text(text = text, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}
