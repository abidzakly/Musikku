package com.d3if3139.musikku.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.d3if3139.musikku.ui.component.Poppins
import com.d3if3139.musikku.ui.component.RegularText
import com.d3if3139.musikku.ui.theme.DarkGrayIco
import com.d3if3139.musikku.ui.theme.GreenNeon
import com.d3if3139.musikku.ui.theme.WhiteFont

@Composable
fun ImageDialog(
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = DarkGrayIco, contentColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        RegularText(text = "Music Title", color = Color.White)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    textStyle = TextStyle(color = Color.White, fontFamily = Poppins)
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = {
                        RegularText(text = "Genre", color = Color.White)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    textStyle = TextStyle(color = Color.White, fontFamily = Poppins)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        RegularText(text = "Description", color = Color.White)
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                    textStyle = TextStyle(color = Color.White, fontFamily = Poppins)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp),
                    border = BorderStroke(1.dp, WhiteFont)
                ) {
                    RegularText(text = "Cancel", color = WhiteFont)
                }
                OutlinedButton(
                    onClick = { onConfirmation(title, genre, description) },
                    enabled = title.isNotEmpty() && genre.isNotEmpty() && description.isNotEmpty(),
                    modifier = Modifier.padding(8.dp),
                    border = BorderStroke(1.dp, GreenNeon)
                ) {
                    RegularText(text = "Save", color = GreenNeon)
                }
            }
        }
    }
}

@Composable
fun RadiOptions(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
