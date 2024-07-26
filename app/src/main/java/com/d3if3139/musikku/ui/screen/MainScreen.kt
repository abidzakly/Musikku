package com.d3if3139.musikku.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.d3if3139.musikku.model.BookTelU
import com.d3if3139.musikku.model.User
import com.d3if3139.musikku.network.Api
import com.d3if3139.musikku.network.ApiStatus
import com.d3if3139.musikku.network.UserDataStore
import com.d3if3139.musikku.ui.component.MediumText
import com.d3if3139.musikku.ui.component.RegularText
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.d3if3139.musikku.BuildConfig
import com.d3if3139.musikku.R
import com.d3if3139.musikku.ui.component.LargeText
import com.d3if3139.musikku.ui.component.MediumLargeText
import com.d3if3139.musikku.ui.component.SmallText
import com.d3if3139.musikku.ui.theme.BG
import com.d3if3139.musikku.ui.theme.DarkGrayIco
import com.d3if3139.musikku.ui.theme.GreenNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val dataStore = UserDataStore(context)
    val errorMessage by viewModel.errorMessage
    val user by dataStore.userFlow.collectAsState(User())
    var showDialog by remember { mutableStateOf(false) }
    var showImgDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showImgDialog = true
    }

    val isUploading by viewModel.isUploading
    val isSuccess by viewModel.querySuccess

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    LaunchedEffect(isUploading) {
        if (isUploading) {
            Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    val showList by dataStore.layoutFlow.collectAsState(true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.top_left_ico),
                            contentDescription = "Icon",
                        )
                        if (user.email == "") {
                            Spacer(modifier = Modifier.width(16.dp))
                            MediumLargeText(
                                text = "Musikku",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                },
                actions = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!showList)
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                if (!showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_view_list_24
                            ),
                            contentDescription =
                            if (showList) "List"
                            else "Grid",
                            tint = GreenNeon
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        if (user.email.isEmpty()) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.account_circle),
                                contentDescription = null,
                                tint = GreenNeon
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.broken_image),
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(shape = CircleShape)
                            )
                        }
                    }
                },
                colors = topAppBarColors(containerColor = BG)
            )
        },
        floatingActionButton = {
            FloatingActionButton(containerColor = GreenNeon, onClick = {
                if (user.email.isNotEmpty() && user.email != "") {
                    val options = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = true,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                } else {
                    Toast.makeText(context, "Please login first.", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Tambah Data",
                    tint = BG
                )
            }

        },
        containerColor = BG
    ) {
        ScreenContent(modifier = Modifier.padding(it), viewModel, user, showList)


        if (showDialog) {
            ProfilDialog(user = user, onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showImgDialog) {
            ImageDialog(
                bitmap = bitmap,
                onDismissRequest = { showImgDialog = false }) { title, genre, description ->
                viewModel.saveData(user.email, title, genre, description, bitmap!!)
                showImgDialog = false
            }
        }

        LaunchedEffect(errorMessage) {
            if (errorMessage != null) {
                Log.d("MainScreen", "$errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    user: User,
    showList: Boolean
) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var musicData by remember { mutableStateOf<BookTelU?>(null) }
    val retrieveErrorMessage by viewModel.errorMessageNoToast

    LaunchedEffect(data) {
        viewModel.retrieveData(user.email)
    }

    LaunchedEffect(user) {
        viewModel.retrieveData(user.email)
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            if (user.name != "") {
                LargeText(
                    text = "Hello, \n${user.name}",
                    color = Color.White,
                    maxLines = 4,
                    modifier = Modifier.width(350.dp)
                )
            }
        }
        when (status) {
            ApiStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            ApiStatus.SUCCESS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {

                    if (showList) {
                        LazyColumn(
                            modifier = modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 84.dp, top = 16.dp)
                        ) {
                            items(data) {
                                ListItem(data = it) {
                                    musicData = it
                                    showDeleteDialog = true
                                }
                            }
                        }
                    } else {
                        LazyVerticalStaggeredGrid(
                            modifier = modifier.fillMaxSize(),
                            columns = StaggeredGridCells.Fixed(2),
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
                        ) {
                            items(data) {
                                GridItem(it) {
                                    musicData = it
                                    showDeleteDialog = true
                                }
                            }
                        }
                        if (showDeleteDialog) {
                            HapusDialog(
                                musicData!!,
                                onDismissRequest = { showDeleteDialog = false }) {
                                viewModel.deleteData(user.email, musicData!!.id)
                                showDeleteDialog = false
                            }
                        }
                    }
                }
                if (showDeleteDialog) {
                    HapusDialog(musicData!!, onDismissRequest = { showDeleteDialog = false }) {
                        viewModel.deleteData(user.email, musicData!!.id)
                        showDeleteDialog = false
                    }
                }
            }

            ApiStatus.FAILED -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (user.email.isEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.not_logged_in),
                            contentDescription = "Empty Data Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(15.dp))
                                .size(300.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MediumText(
                            text = "Log in to upload and enjoy content.",
                            color = Color.White
                        )
                    } else {
                        if (retrieveErrorMessage != null) {
                            when (retrieveErrorMessage) {
                                "Anda belum memasukkan data." -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.empty_state),
                                        contentDescription = "Empty Data Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape = RoundedCornerShape(15.dp))
                                            .size(250.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    RegularText(
                                        text = "You haven't uploaded any yet.",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }

                                else -> {
                                    MediumText(text = retrieveErrorMessage!!, color = Color.White)
                                    OutlinedButton(
                                        onClick = { viewModel.retrieveData(user.email) },
                                        modifier = Modifier.padding(top = 16.dp),
                                        contentPadding = PaddingValues(
                                            horizontal = 32.dp,
                                            vertical = 16.dp
                                        ),
                                        border = BorderStroke(1.dp, GreenNeon)
                                    ) {
                                        RegularText(text = "Try again", color = GreenNeon)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(data: BookTelU, onClick: () -> Unit) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenNeon,
        ),
        border = BorderStroke(1.dp, GreenNeon)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Api.getImageUrl(data.image_id))
                    .crossfade(true)
                    .build(),
                contentDescription = "Gambar ${data.title}",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.broken_image),
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RegularText(
                        modifier = Modifier.width(125.dp),
                        text = data.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    SmallText(text = data.genre)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallText(
                        modifier = Modifier.width(125.dp),
                        text = data.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    IconButton(onClick = { onClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Icon",
                            tint = DarkGrayIco
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridItem(data: BookTelU, onClick: () -> Unit) {
    Card(
        onClick = {},
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GreenNeon,
        ),
        border = BorderStroke(1.dp, BG)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Api.getImageUrl(data.image_id))
                .crossfade(true)
                .build(),
            contentDescription = "Gambar ${data.image_id}",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.broken_image),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RegularText(
                    text = data.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { onClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Icon",
                        tint = DarkGrayIco
                    )
                }
            }
            RegularText(text = data.genre, fontWeight = FontWeight.Light)
            SmallText(text = data.description)
        }
    }
}

private suspend fun signIn(
    context: Context,
    dataStore: UserDataStore
) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


@Preview
@Composable
private fun Prev() {
    MainScreen()
}