package com.xcvi.micros.ui.screens.create

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.usecases.MealUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class VitaminsInt(
    val vitaminA: Int = 0,
    val vitaminB1: Int = 0,
    val vitaminB2: Int = 0,
    val vitaminB4: Int = 0,
    val vitaminB3: Int = 0,
    val vitaminB5: Int = 0,
    val vitaminB6: Int = 0,
    val vitaminB9: Int = 0,
    val vitaminB12: Int = 0,
    val vitaminC: Int = 0,
    val vitaminD: Int = 0,
    val vitaminE: Int = 0,
    val vitaminK: Int = 0,
)
data class AminoAcidsInt(
    val histidine: Int = 0,
    val isoleucine: Int = 0,
    val leucine: Int = 0,
    val lysine: Int = 0,
    val methionine: Int = 0,
    val phenylalanine: Int = 0,
    val threonine: Int = 0,
    val tryptophan: Int = 0,
    val valine: Int = 0,
)

data class NutrientsInt(
    val calories: Int = 0,
    val protein:Int = 0,
    val carbohydrates:Int = 0,
    val fats:Int = 0,
    val saturatedFats:Int = 0,
    val fiber:Int = 0,
    val sugars:Int = 0,
)

data class MineralsInt(
    val potassium: Int = 0,
    val calcium: Int = 0,
    val magnesium: Int = 0,
    val iron: Int = 0,
    val sodium: Int = 0,
)
data class CreateState(
    val amount: Int = 0,
    val name: String = "",
    val minerals: MineralsInt = MineralsInt(),
    val nutrients: NutrientsInt = NutrientsInt(),
    val vitamins: VitaminsInt = VitaminsInt(),
    val aminoAcids: AminoAcidsInt = AminoAcidsInt()
)

sealed interface CreateEvent{
    data class InputName(val value: String) : CreateEvent
    data class Input(val value: Int, val position: InputFieldPosition) : CreateEvent
    data class Create(val date: Int, val meal: Int, val onError: () -> Unit, val onSuccess: () -> Unit) : CreateEvent
}

enum class InputFieldPosition {
    NAME,
    AMOUNT,
    CALORIES,

    PROTEIN,
    CARBOHYDRATE,
    FAT,
    SATURATED_FAT,
    SUGARS,
    FIBRE,

    POTASSIUM,
    SODIUM,
    CALCIUM,
    MAGNESIUM,
    IRON,

    VITAMIN_A,
    VITAMIN_B1,
    VITAMIN_B2,
    VITAMIN_B3,
    VITAMIN_B4,
    VITAMIN_B5,
    VITAMIN_B6,
    VITAMIN_B9,
    VITAMIN_B12,
    VITAMIN_C,
    VITAMIN_D,
    VITAMIN_E,
    VITAMIN_K,

    HISTIDINE,
    ISOLEUCINE,
    LEUCINE,
    LYSINE,
    METHIONINE,
    PHENYLALANINE,
    THREONINE,
    TRYPTOPHAN,
    VALINE,
}


class CreateViewModel(
    private val useCases: MealUseCases
): BaseViewModel<CreateState>(CreateState()) {


    fun onEvent(event: CreateEvent){
        when(event){
            is CreateEvent.InputName -> updateData { copy(name = event.value) }
            is CreateEvent.Input -> input(event.value, event.position)
            is CreateEvent.Create -> create(date = event.date, meal =event.meal, onError = event.onError, onSuccess = event.onSuccess)
        }
    }

    private fun input(
        value: Int,
        position: InputFieldPosition
    ){
        when(position){
            InputFieldPosition.NAME -> state = state.copy(name = value.toString())
            InputFieldPosition.AMOUNT -> state = state.copy(amount = value)
            InputFieldPosition.CALORIES -> state = state.copy(nutrients = state.nutrients.copy(calories = value ))
            InputFieldPosition.PROTEIN -> state = state.copy(nutrients = state.nutrients.copy(protein = value ))
            InputFieldPosition.CARBOHYDRATE -> state = state.copy(nutrients = state.nutrients.copy(carbohydrates = value ))
            InputFieldPosition.FAT -> state = state.copy(nutrients = state.nutrients.copy(fats = value ))
            InputFieldPosition.SATURATED_FAT -> state = state.copy(nutrients = state.nutrients.copy(saturatedFats = value ))
            InputFieldPosition.SUGARS -> state = state.copy(nutrients = state.nutrients.copy(sugars = value ))
            InputFieldPosition.FIBRE -> state = state.copy(nutrients = state.nutrients.copy(fiber = value ))
            InputFieldPosition.POTASSIUM -> state = state.copy(minerals = state.minerals.copy(potassium = value ))
            InputFieldPosition.SODIUM -> state = state.copy(minerals = state.minerals.copy(sodium = value ))
            InputFieldPosition.CALCIUM -> state = state.copy(minerals = state.minerals.copy(calcium = value ))
            InputFieldPosition.MAGNESIUM -> state = state.copy(minerals = state.minerals.copy(magnesium = value ))
            InputFieldPosition.IRON -> state = state.copy(minerals = state.minerals.copy(iron = value ))
            InputFieldPosition.VITAMIN_A -> state = state.copy(vitamins = state.vitamins.copy(vitaminA = value ))
            InputFieldPosition.VITAMIN_B1 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB1 = value ))
            InputFieldPosition.VITAMIN_B2 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB2 = value ))
            InputFieldPosition.VITAMIN_B3 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB3 = value ))
            InputFieldPosition.VITAMIN_B4 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB4 = value ))
            InputFieldPosition.VITAMIN_B5 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB5 = value ))

            InputFieldPosition.VITAMIN_B6 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB6 = value ))
            InputFieldPosition.VITAMIN_B9 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB9 = value ))
            InputFieldPosition.VITAMIN_B12 -> state = state.copy(vitamins = state.vitamins.copy(vitaminB12 = value ))
            InputFieldPosition.VITAMIN_C -> state = state.copy(vitamins = state.vitamins.copy(vitaminC = value ))
            InputFieldPosition.VITAMIN_D -> state = state.copy(vitamins = state.vitamins.copy(vitaminD = value ))
            InputFieldPosition.VITAMIN_E -> state = state.copy(vitamins = state.vitamins.copy(vitaminE = value ))
            InputFieldPosition.VITAMIN_K -> state = state.copy(vitamins = state.vitamins.copy(vitaminK = value ))
            InputFieldPosition.HISTIDINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(histidine = value ))
            InputFieldPosition.ISOLEUCINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(isoleucine = value ))
            InputFieldPosition.LEUCINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(leucine = value ))
            InputFieldPosition.LYSINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(lysine = value ))
            InputFieldPosition.METHIONINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(methionine = value ))
            InputFieldPosition.PHENYLALANINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(phenylalanine = value ))
            InputFieldPosition.THREONINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(threonine = value ))
            InputFieldPosition.TRYPTOPHAN -> state = state.copy(aminoAcids = state.aminoAcids.copy(tryptophan = value ))
            InputFieldPosition.VALINE -> state = state.copy(aminoAcids = state.aminoAcids.copy(valine = value ))
        }
    }

    private fun create(
        date: Int,
        meal: Int,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ){
        if(state.amount <= 0 || state.name.isBlank()){
            onError()
            return
        }
        val nutrients = Nutrients(
            calories = state.nutrients.calories,
            protein = state.nutrients.protein.toDouble(),
            carbohydrates = state.nutrients.carbohydrates.toDouble(),
            fats = state.nutrients.fats.toDouble(),
            saturatedFats = state.nutrients.saturatedFats.toDouble(),
            fiber = state.nutrients.fiber.toDouble(),
            sugars = state.nutrients.sugars.toDouble()
        )
        val minerals = Minerals.empty().copy(
            potassium = state.minerals.potassium.toDouble(),
            calcium = state.minerals.calcium.toDouble(),
            magnesium = state.minerals.magnesium.toDouble(),
            iron = state.minerals.iron.toDouble(),
            sodium = state.minerals.sodium.toDouble()
        )

        val vitamins = Vitamins(
            vitaminA = state.vitamins.vitaminA.toDouble(),
            vitaminB1 = state.vitamins.vitaminB1.toDouble(),
            vitaminB2 = state.vitamins.vitaminB2.toDouble(),
            vitaminB3 = state.vitamins.vitaminB3.toDouble(),
            vitaminB4 = state.vitamins.vitaminB4.toDouble(),
            vitaminB5 = state.vitamins.vitaminB5.toDouble(),
            vitaminB6 = state.vitamins.vitaminB6.toDouble(),
            vitaminB9 = state.vitamins.vitaminB9.toDouble(),
            vitaminB12 = state.vitamins.vitaminB12.toDouble(),
            vitaminC = state.vitamins.vitaminC.toDouble(),
            vitaminD = state.vitamins.vitaminD.toDouble(),
            vitaminE = state.vitamins.vitaminE.toDouble(),
            vitaminK = state.vitamins.vitaminK.toDouble()
        )

        val aminoAcids = AminoAcids(
            histidine = state.aminoAcids.histidine.toDouble(),
            isoleucine = state.aminoAcids.isoleucine.toDouble(),
            leucine = state.aminoAcids.leucine.toDouble(),
            lysine = state.aminoAcids.lysine.toDouble(),
            methionine = state.aminoAcids.methionine.toDouble(),
            phenylalanine = state.aminoAcids.phenylalanine.toDouble(),
            threonine = state.aminoAcids.threonine.toDouble(),
            tryptophan = state.aminoAcids.tryptophan.toDouble(),
            valine = state.aminoAcids.valine.toDouble()
        )

        viewModelScope.launch {
            val res = useCases.create(
                date = date,
                meal = meal,
                amount = state.amount,
                name = state.name,
                minerals = minerals,
                nutrients = nutrients,
                vitamins = vitamins,
                aminoAcids = aminoAcids
            )
            when(res){
                is Response.Error -> onError()
                is Response.Success -> onSuccess()
            }
        }
    }
}
















