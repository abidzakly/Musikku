package com.d3if3139.musikku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.d3if3139.musikku.R
import com.d3if3139.musikku.model.BookTelU
import com.d3if3139.musikku.network.Api
import com.d3if3139.musikku.ui.component.RegularText
import com.d3if3139.musikku.ui.theme.DarkGrayIco
import com.d3if3139.musikku.ui.theme.WhiteFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HapusDialog(
    data: BookTelU,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = DarkGrayIco)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegularText(text = "Delete this post?", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Api.getImageUrl(data.image_id))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Image ${data.title}",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                    onClick = { onConfirmation() },
                    modifier = Modifier.padding(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    RegularText(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
//    HapusDialog(onDismissRequest = { /*TODO*/ }) {
//
//    }
}
