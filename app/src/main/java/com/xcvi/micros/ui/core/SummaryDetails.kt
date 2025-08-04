package com.xcvi.micros.ui.core

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.ui.core.comp.LoadingIndicator
import com.xcvi.micros.ui.core.comp.NumberPicker
import com.xcvi.micros.ui.core.comp.OnNavigation
import com.xcvi.micros.ui.core.comp.rememberShakeOffset
import com.xcvi.micros.ui.core.utils.toLabeledPairs
import kotlinx.coroutines.launch

@Composable
fun SummaryDetails(
    isEnhancing: Boolean = false,
    nutrients: Nutrients,
    minerals: Minerals,
    vitamins: Vitamins,
    aminoAcids: AminoAcids,
    context: Context
) {
    val nutrientsLabeled = nutrients.toLabeledPairs(context)
    val mineralsLabeled = minerals.toLabeledPairs(context)
    val vitaminsLabeled = vitamins.toLabeledPairs(context)
    val aminoAcidsLabeled = aminoAcids.toLabeledPairs(context)
    val labels = listOf(
        nutrientsLabeled,
        mineralsLabeled,
        vitaminsLabeled,
        aminoAcidsLabeled
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        fun alpha(isEmpty: Boolean): Float = if(isEnhancing){
            0f
        } else {
            if (isEmpty) {
                0.4f
            } else {
                1f
            }
        }
        labels.forEach { section ->
            section.forEachIndexed { index, data ->
                val data = section[index]
                if (index > 0 && data.first.isNotBlank()) {
                    HorizontalDivider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                    )
                }
                Row {
                    Text(
                        text = data.first,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha(
                                data.second.startsWith("0.0") || data.second.startsWith("0,0")
                            )
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = data.second,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha(
                                data.second.startsWith("0.0") || data.second.startsWith("0,0")
                            )
                        )
                    )
                }

            }
        }
    }
}
