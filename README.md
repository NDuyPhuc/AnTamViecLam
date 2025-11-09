# AnTamViecLam
<<<<<<< HEAD
=======

```
├── data/
│   ├── model/                  // Data classes (User.kt, Job.kt, Review.kt...)
│   ├── network/                // Các lớp tương tác với Firebase
│   │   ├── FirestoreClient.kt
│   │   └── FirebaseAuthClient.kt
│   └── repository/             // Nơi tổng hợp dữ liệu từ các nguồn
│       ├── AuthRepository.kt
│       ├── JobRepository.kt
│       ├── UserRepository.kt
│       └── impl/               // Các lớp implementation của interface trên
│           ├── AuthRepositoryImpl.kt
│           ├── JobRepositoryImpl.kt
│           └── UserRepositoryImpl.kt
│
├── di/                         // Dependency Injection (Hilt or Koin)
│   └── AppModule.kt            // Cung cấp các dependency chung
│
├── ui/                         // Chứa toàn bộ UI, chia theo từng feature
│   ├── auth/                   // Feature Đăng ký, Đăng nhập
│   │   ├── LoginFragment.kt
│   │   ├── RegisterFragment.kt
│   │   └── AuthViewModel.kt
│   │
│   ├── main/                   // Màn hình chính chứa Bottom Navigation
│   │   └── MainActivity.kt
│   │
│   ├── home/                   // Feature Trang chủ (danh sách công việc)
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── adapter/
│   │       └── JobAdapter.kt
│   │
│   ├── map/                    // Feature Bản đồ công việc
│   │   ├── MapViewFragment.kt
│   │   └── MapViewModel.kt
│   │
│   ├── job_details/            // Feature Chi tiết công việc
│   │   ├── JobDetailsFragment.kt
│   │   └── JobDetailsViewModel.kt
│   │
│   ├── profile/                // Feature Quản lý hồ sơ
│   │   ├── ProfileFragment.kt
│   │   ├── EditProfileFragment.kt
│   │   └── ProfileViewModel.kt
│   │
│   ├── bhxh/                   // Feature BHXH
│   │   ├── BhxhFragment.kt
│   │   ├── BhxhViewModel.kt
│   │   └── BhxhInfoDetailsFragment.kt
│   │
│   ├── chat/                   // Feature Chat
│   │   ├── ChatListFragment.kt   // Danh sách các cuộc trò chuyện
│   │   ├── ChatDetailFragment.kt // Màn hình chat 1-1
│   │   └── ChatViewModel.kt
│   │
│   ├── posting/                // Feature Đăng tin của NTD
│   │   ├── CreateJobFragment.kt
│   │   └── CreateJobViewModel.kt
│   │
│   └── base/                   // Các lớp base cho Activity, Fragment, ViewModel
│       ├── BaseActivity.kt
│       └── BaseViewModel.kt
│
└── utils/                      // Các lớp tiện ích dùng chung
    ├── Constants.kt            // Chứa các hằng số
    ├── Extensions.kt           // Các extension function (e.g., View.show())
    ├── LocationHelper.kt       // Lớp helper xử lý location
    └── DateTimeUtils.kt        // Lớp helper xử lý ngày giờ

```
>>>>>>> 78d0e2f10b7c9453339a3b4ce51be88ed643f0a5
