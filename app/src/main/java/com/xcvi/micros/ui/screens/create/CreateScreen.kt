package com.xcvi.micros.ui.screens.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.comp.BackIcon
import com.xcvi.micros.ui.core.comp.rememberShakeOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    modifier: Modifier = Modifier,
    date: Int,
    meal: Int,
    onEvent: (CreateEvent) -> Unit,
    onBack: () -> Unit,
    state: CreateState
) {

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false
    }

    Scaffold(
        modifier = modifier.offset(x = shakeOffset),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackIcon { onBack() }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onEvent(
                                CreateEvent.Create(
                                    date = date,
                                    meal = meal,
                                    onError = { shakeTrigger = true },
                                    onSuccess = { onBack() }
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            )
        }
    ) {
        InputForm(
            modifier = modifier.padding(it),
            state = state,
            onValueChange = { value, position ->
                onEvent(CreateEvent.Input(value, position))
            },
            onNameChange = { value, position ->
                onEvent(CreateEvent.InputName(value))
            }
        )
    }


}

@Composable
fun InputForm(
    modifier: Modifier = Modifier,
    state: CreateState,
    onValueChange: (Int, InputFieldPosition) -> Unit,
    onNameChange: (String, InputFieldPosition) -> Unit
) {
    LazyColumn {
        item {
            Column(modifier = modifier.padding(16.dp)) {
                InputFieldPosition.entries.forEach { position ->
                    val value = getFieldValue(state, position)
                    val textFieldValue = if (value == "0.0" || value == "0" || value == "0,0") {
                        ""
                    } else {
                        value
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { text ->
                            if(position == InputFieldPosition.NAME){
                                onNameChange(text, position)
                            } else {
                                val intValue = text.toIntOrNull()
                                if (intValue != null){
                                    onValueChange(intValue, position)
                                }
                            }
                        },
                        label = { Text(stringResource(getLabelResId(position))) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )
                }
            }
        }
    }
}


fun getFieldValue(state: CreateState, position: InputFieldPosition): String {
    return when (position) {
        InputFieldPosition.NAME -> state.name
        InputFieldPosition.AMOUNT -> state.amount.toString()
        InputFieldPosition.CALORIES -> state.nutrients.calories.toString()
        InputFieldPosition.PROTEIN -> state.nutrients.protein.toString()
        InputFieldPosition.CARBOHYDRATE -> state.nutrients.carbohydrates.toString()
        InputFieldPosition.FAT -> state.nutrients.fats.toString()
        InputFieldPosition.SATURATED_FAT -> state.nutrients.saturatedFats.toString()
        InputFieldPosition.SUGARS -> state.nutrients.sugars.toString()
        InputFieldPosition.FIBRE -> state.nutrients.fiber.toString()
        InputFieldPosition.POTASSIUM -> state.minerals.potassium.toString()
        InputFieldPosition.SODIUM -> state.minerals.sodium.toString()
        InputFieldPosition.CALCIUM -> state.minerals.calcium.toString()
        InputFieldPosition.MAGNESIUM -> state.minerals.magnesium.toString()
        InputFieldPosition.IRON -> state.minerals.iron.toString()
        InputFieldPosition.VITAMIN_A -> state.vitamins.vitaminA.toString()
        InputFieldPosition.VITAMIN_B1 -> state.vitamins.vitaminB1.toString()
        InputFieldPosition.VITAMIN_B2 -> state.vitamins.vitaminB2.toString()
        InputFieldPosition.VITAMIN_B3 -> state.vitamins.vitaminB3.toString()
        InputFieldPosition.VITAMIN_B5 -> state.vitamins.vitaminB5.toString()
        InputFieldPosition.VITAMIN_B6 -> state.vitamins.vitaminB6.toString()
        InputFieldPosition.VITAMIN_B9 -> state.vitamins.vitaminB9.toString()
        InputFieldPosition.VITAMIN_B12 -> state.vitamins.vitaminB12.toString()
        InputFieldPosition.VITAMIN_C -> state.vitamins.vitaminC.toString()
        InputFieldPosition.VITAMIN_D -> state.vitamins.vitaminD.toString()
        InputFieldPosition.VITAMIN_E -> state.vitamins.vitaminE.toString()
        InputFieldPosition.VITAMIN_K -> state.vitamins.vitaminK.toString()
        InputFieldPosition.HISTIDINE -> state.aminoAcids.histidine.toString()
        InputFieldPosition.ISOLEUCINE -> state.aminoAcids.isoleucine.toString()
        InputFieldPosition.LEUCINE -> state.aminoAcids.leucine.toString()
        InputFieldPosition.LYSINE -> state.aminoAcids.lysine.toString()
        InputFieldPosition.METHIONINE -> state.aminoAcids.methionine.toString()
        InputFieldPosition.PHENYLALANINE -> state.aminoAcids.phenylalanine.toString()
        InputFieldPosition.THREONINE -> state.aminoAcids.threonine.toString()
        InputFieldPosition.TRYPTOPHAN -> state.aminoAcids.tryptophan.toString()
        InputFieldPosition.VALINE -> state.aminoAcids.valine.toString()
        InputFieldPosition.VITAMIN_B4 -> state.vitamins.vitaminB4.toString()
    }
}


private fun getLabelResId(position: InputFieldPosition): Int {
    return when (position) {
        InputFieldPosition.HISTIDINE -> R.string.histidine
        InputFieldPosition.ISOLEUCINE -> R.string.isoleucine
        InputFieldPosition.LEUCINE -> R.string.leucine
        InputFieldPosition.LYSINE -> R.string.lysine
        InputFieldPosition.METHIONINE -> R.string.methionine
        InputFieldPosition.PHENYLALANINE -> R.string.phenylalanine
        InputFieldPosition.THREONINE -> R.string.threonine
        InputFieldPosition.TRYPTOPHAN -> R.string.tryptophan
        InputFieldPosition.VALINE -> R.string.valine

        InputFieldPosition.PROTEIN -> R.string.protein
        InputFieldPosition.CARBOHYDRATE -> R.string.carbs
        InputFieldPosition.FAT -> R.string.fats
        InputFieldPosition.SATURATED_FAT -> R.string.saturated_fats
        InputFieldPosition.FIBRE -> R.string.fiber
        InputFieldPosition.SUGARS -> R.string.sugars

        InputFieldPosition.CALCIUM -> R.string.calcium
        InputFieldPosition.IRON -> R.string.iron
        InputFieldPosition.MAGNESIUM -> R.string.magnesium
        InputFieldPosition.POTASSIUM -> R.string.potassium
        InputFieldPosition.SODIUM -> R.string.sodium

        InputFieldPosition.VITAMIN_A -> R.string.vitaminA
        InputFieldPosition.VITAMIN_B1 -> R.string.vitaminB1
        InputFieldPosition.VITAMIN_B2 -> R.string.vitaminB2
        InputFieldPosition.VITAMIN_B3 -> R.string.vitaminB3
        InputFieldPosition.VITAMIN_B4 -> R.string.vitaminB4
        InputFieldPosition.VITAMIN_B5 -> R.string.vitaminB5
        InputFieldPosition.VITAMIN_B6 -> R.string.vitaminB6
        InputFieldPosition.VITAMIN_B9 -> R.string.vitaminB9
        InputFieldPosition.VITAMIN_B12 -> R.string.vitaminB12
        InputFieldPosition.VITAMIN_C -> R.string.vitaminC
        InputFieldPosition.VITAMIN_D -> R.string.vitaminD
        InputFieldPosition.VITAMIN_E -> R.string.vitaminE
        InputFieldPosition.VITAMIN_K -> R.string.vitaminK

        InputFieldPosition.CALORIES -> R.string.calories
        InputFieldPosition.AMOUNT -> R.string.amount
        InputFieldPosition.NAME -> R.string.name
    }
}