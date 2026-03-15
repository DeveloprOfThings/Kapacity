@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.developrofthings.kapacityapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.allCaps
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.developrofthings.kapacity.KapacityUnit
import io.github.developrofthings.kapacity.toKapacity

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterVertically,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val kapacityTextFieldState = rememberTextFieldState(
                    initialText = 1_000_000_000.toString()
                )
                TextField(
                    state = kapacityTextFieldState,
                    modifier = Modifier.fillMaxWidth(fraction = .45F),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.End
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = InputTransformation.allCaps(Locale.current)
                        .then {
                            if (length != 0 && asCharSequence().any { !it.isDigit() }) {
                                revertAllChanges()
                            }
                        },
                    outputTransformation = {
                        val text = asCharSequence()

                        // Find the decimal point, if any, so we only format the integer portion
                        val decimalIndex = text.indexOf('.')
                        val integerEndIndex = if (decimalIndex != -1) decimalIndex else length

                        // Traverse backward from the end of the integer portion, adding commas every 3 digits
                        var i = integerEndIndex - 3
                        while (i > 0) {
                            // Prevent adding a comma right after a negative sign (e.g., turning "-123" into "-,123")
                            if (i == 1 && text[0] == '-') break

                            insert(i, ",")
                            i -= 3
                        }
                    },
                )

                var fromKapacityUnit by rememberedMutableValue(initialValue = KapacityUnit.Byte)
                var toKapacityUnit by rememberedMutableValue(initialValue = KapacityUnit.Megabyte)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1F),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "From:", style = MaterialTheme.typography.labelLarge)
                        Kapacities(selectedUnit = fromKapacityUnit) {
                            fromKapacityUnit = it
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1F),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "To:", style = MaterialTheme.typography.labelLarge)
                        Kapacities(selectedUnit = toKapacityUnit) {
                            toKapacityUnit = it
                        }
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Kapacity: ")
                        }
                        append(
                            kapacityTextFieldState.longValue.toKapacity(
                                unit = fromKapacityUnit,
                                useMetric = true,
                            ).toString(
                                unit = toKapacityUnit,
                                useMetric = true,
                            )
                        )
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    }
}

@Composable
private fun Kapacities(
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
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

private val TextFieldState.longValue: Long get() = text.toString().toLong()

@Suppress("SameParameterValue")
@Composable
private inline fun rememberedMutableBoolean(initialValue: Boolean): MutableState<Boolean> =
    remember { mutableStateOf(value = initialValue) }

@Suppress("SameParameterValue")
@Composable
private inline fun <T> rememberedMutableValue(initialValue: T): MutableState<T> =
    remember { mutableStateOf(value = initialValue) }