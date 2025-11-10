// app/src/main/java/com/example/antamvieclam/ui/posting/CreateJobScreen.kt
package com.example.antamvieclam.ui.posting

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.antamvieclam.data.model.PayType
import com.google.firebase.firestore.GeoPoint
import com.trackasia.android.TrackAsia
import com.trackasia.android.camera.CameraPosition
import com.trackasia.android.geometry.LatLng
import com.trackasia.android.maps.MapView
import com.trackasia.android.maps.Style

@Composable
fun CreateJobScreen(
    onJobPosted: () -> Unit,
    viewModel: CreateJobViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payRate by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var payType by remember { mutableStateOf(PayType.PER_HOUR) }

    // State để lưu vị trí trung tâm bản đồ hiện tại
    val defaultLocation = LatLng(10.7769, 106.7009) // TP.HCM
    var selectedLatLng by remember { mutableStateOf(defaultLocation) }

    LaunchedEffect(uiState) {
        when(val state = uiState) {
            is CreateJobState.Success -> {
                Toast.makeText(context, "Đăng tin thành công!", Toast.LENGTH_SHORT).show()
                onJobPosted()
            }
            is CreateJobState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { /* ... TopAppBar ... */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Đăng tin tuyển dụng mới", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            // ... Các OutlinedTextField cho title, description, payRate, address ...
            OutlinedTextField(value = title, onValueChange = {title = it}, label={Text("Tiêu đề công việc")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = {description = it}, label={Text("Mô tả chi tiết")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = payRate, onValueChange = {payRate = it}, label={Text("Mức lương (VD: 50000)")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = {address = it}, label={Text("Địa chỉ cụ thể")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            // --- Vùng bản đồ TrackAsia ---
            Text("Ghim vị trí công việc", style = MaterialTheme.typography.titleMedium)
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
                    onCameraMove = { newLatLng ->
                        // Cập nhật state khi người dùng di chuyển bản đồ
                        selectedLatLng = newLatLng
                    }
                )
                // Marker cố định ở giữa
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pin",
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp).padding(bottom = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val geoPoint = GeoPoint(selectedLatLng.latitude, selectedLatLng.longitude)
                    viewModel.postJob(title, description, payRate, payType, address, geoPoint)
                },
                enabled = uiState != CreateJobState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState == CreateJobState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("ĐĂNG TIN")
                }
            }
        }
    }
}

@Composable
fun TrackAsiaMapPicker(
    initialPosition: LatLng,
    onCameraMove: (LatLng) -> Unit
) {
    val context = LocalContext.current
    // Tạo và nhớ instance của MapView
    val mapView = remember {
        MapView(context).apply {
            // Khởi tạo TrackAsia instance trước khi sử dụng MapView
            MapView(context)
        }
    }

    // Quản lý vòng đời của MapView một cách an toàn
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

    // Sử dụng AndroidView để hiển thị MapView
    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { trackAsiaMap ->
                    // TODO: THAY "public_key" BẰNG API KEY CỦA BẠN
                    val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=52fedb6b306931761836057e5580a05be7"
                    trackAsiaMap.setStyle(Style.Builder().fromUri(styleUrl))

                    trackAsiaMap.cameraPosition = CameraPosition.Builder()
                        .target(initialPosition)
                        .zoom(15.0)
                        .build()

                    // Lắng nghe sự kiện khi người dùng ngừng di chuyển bản đồ
                    trackAsiaMap.addOnCameraIdleListener {
                        // Cập nhật tọa độ mới lên Composable state
                        trackAsiaMap.cameraPosition.target?.let { p1 -> onCameraMove(p1) }
                    }
                }
            }
        }
    )
}