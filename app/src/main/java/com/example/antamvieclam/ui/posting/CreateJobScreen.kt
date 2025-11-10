// app/src/main/java/com/example/antamvieclam/ui/posting/CreateJobScreen.kt

package com.example.antamvieclam.ui.posting

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role // <-- SỬA LỖI #3
import androidx.compose.ui.text.input.ImeAction // <-- SỬA LỖI #1 & #2
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.antamvieclam.data.model.PayType
import com.google.firebase.firestore.GeoPoint
import com.trackasia.android.camera.CameraPosition
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    onNavigateBack: () -> Unit,
    onJobPostedSuccessfully: () -> Unit,
    viewModel: CreateJobViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payRate by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var payType by remember { mutableStateOf(PayType.PER_HOUR) }

    val defaultLocation = LatLng(10.7769, 106.7009) // TP.HCM
    var selectedLatLng by remember { mutableStateOf(defaultLocation) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateJobState.Success -> {
                Toast.makeText(context, "Đăng tin thành công!", Toast.LENGTH_SHORT).show()
                onJobPostedSuccessfully()
            }
            is CreateJobState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng Tin Mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Sử dụng icon AutoMirrored để tự động đảo chiều cho các ngôn ngữ RTL
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Chi tiết công việc", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề công việc") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả chi tiết") }, modifier = Modifier.fillMaxWidth().height(120.dp))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = payRate, onValueChange = { payRate = it }, label = { Text("Mức lương (VD: 50000)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ cụ thể") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Hình thức trả lương", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PayType.values().forEach { type ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (type == payType),
                                onClick = { payType = type },
                                role = Role.RadioButton // <-- Cần import đúng
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton( // <-- Cần import đúng
                            selected = (type == payType),
                            onClick = null
                        )
                        Text(
                            text = type.toVietnamese(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Ghim vị trí trên bản đồ", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                TrackAsiaMapPicker(
                    initialPosition = defaultLocation,
                    onCameraMove = { newLatLng -> selectedLatLng = newLatLng }
                )
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Pin", tint = Color.Red, modifier = Modifier.size(40.dp).padding(bottom = 20.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val geoPoint = GeoPoint(selectedLatLng.latitude, selectedLatLng.longitude)
                    viewModel.postJob(title, description, payRate, payType, address, geoPoint)
                },
                enabled = uiState !is CreateJobState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState is CreateJobState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("ĐĂNG TIN")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun PayType.toVietnamese(): String {
    return when (this) {
        PayType.PER_HOUR -> "Theo giờ"
        PayType.PER_DAY -> "Theo ngày"
        PayType.PER_PACKAGE -> "Theo tháng"
    }
}


@Composable
fun TrackAsiaMapPicker(
    initialPosition: LatLng,
    onCameraMove: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember {
        // XÓA KHỞI TẠO LỒNG NHAU THỪA
        MapView(context)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    AndroidView({ mapView }) { map ->
        map.getMapAsync { trackAsiaMap ->
            val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=52fedb6b306931761836057e5580a05be7"
            trackAsiaMap.setStyle(Style.Builder().fromUri(styleUrl))
            trackAsiaMap.cameraPosition = CameraPosition.Builder()
                .target(initialPosition)
                .zoom(15.0)
                .build()
            trackAsiaMap.addOnCameraIdleListener {
                trackAsiaMap.cameraPosition.target?.let(onCameraMove)
            }
        }
    }
}