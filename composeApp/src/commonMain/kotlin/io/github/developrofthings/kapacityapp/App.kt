@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.developrofthings.kapacityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.developrofthings.kapacity.KapacityUnit

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var selectedKapacityUnit by rememberedMutableValue(initialValue = KapacityUnit.Byte)
            Capacities(selectedUnit = selectedKapacityUnit) {
                selectedKapacityUnit = it
            }
        }
    }
}

@Composable
private fun Capacities(
    selectedUnit: KapacityUnit,
    modifier: Modifier = Modifier,
    onKapacityUnit: (KapacityUnit) -> Unit,
) {
    var expanded by rememberedMutableBoolean(initialValue = false)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        TextField(
            value = selectedUnit.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier,
        ) {
            KapacityUnit.entries.fastForEach { kapUnit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = kapUnit.name,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        onKapacityUnit(kapUnit)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Suppress("SameParameterValue")
@Composable
private inline fun rememberedMutableBoolean(initialValue: Boolean): MutableState<Boolean> =
    remember { mutableStateOf(value = initialValue) }

@Suppress("SameParameterValue")
@Composable
private inline fun <T> rememberedMutableValue(initialValue: T): MutableState<T> =
    remember { mutableStateOf(value = initialValue) }

@Composable
private inline fun <T> rememberedMutableValue(
    key: Any?,
    crossinline predicate: () -> T
): MutableState<T> =
    remember(key1 = key) { mutableStateOf(value = predicate()) }

@Composable
private inline fun rememberedMutableBoolean(
    key: Any?,
    crossinline predicate: () -> Boolean
): MutableState<Boolean> =
    remember(key1 = key) { mutableStateOf(value = predicate()) }