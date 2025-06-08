# QuizMe

QuizMe là một ứng dụng Android hiện đại cho phép người dùng tạo, tham gia và chơi các quiz tương tác trực tuyến. Ứng dụng hỗ trợ chế độ nhiều người chơi thời gian thực và có giao diện thân thiện với người dùng.

## 📱 Tính Năng Chính

### 🏠 Trang Chủ (Home)
- Hiển thị các quiz phổ biến và mới nhất
- Giao diện trực quan và dễ sử dụng
- Thống kê hoạt động người dùng

### 📚 Thư Viện Quiz (Library) 
- Quản lý các quiz đã tạo
- Xem lịch sử tham gia quiz
- Lưu trữ các quiz yêu thích

### 🚪 Tham Gia Phòng (Join Room)
- Tham gia phòng quiz bằng mã phòng
- Chế độ chơi nhiều người thời gian thực
- Phòng chờ với thông tin người chơi

### ➕ Tạo Quiz (Create Quiz)
- Tạo quiz tùy chỉnh với nhiều loại câu hỏi
- Thiết lập thời gian và độ khó
- Chia sẻ quiz với bạn bè

### 👤 Hồ Sơ Cá Nhân (Profile)
- Quản lý thông tin cá nhân
- Thống kê điểm số và thành tích
- Cài đặt ngôn ngữ và tùy chọn

### 🎮 Chơi Quiz
- Giao diện chơi quiz mượt mà
- Hiển thị kết quả thời gian thực
- Bảng xếp hạng cuối game

## 🛠️ Công Nghệ Sử Dụng

### Framework & Libraries
- **Android SDK** - Phát triển ứng dụng native Android
- **Material Design** - Thiết kế giao diện hiện đại
- **ViewBinding** - Binding view an toàn và hiệu quả
- **Fragment & Navigation** - Điều hướng và quản lý màn hình

### Networking & Data
- **Retrofit 2** - HTTP client cho API calls
- **OkHttp3** - Logging và interceptor
- **Gson** - JSON serialization/deserialization
- **WebSocket** - Kết nối thời gian thực cho multiplayer

### Database & Storage
- **Room Database** - Local database
- **SharedPreferences** - Lưu trữ cài đặt người dùng

### Reactive Programming
- **RxJava 2** - Reactive programming
- **RxAndroid** - Android-specific RxJava bindings

### UI & UX
- **Glide** - Load và cache hình ảnh
- **CircleImageView** - Hiển thị avatar tròn
- **SwipeRefreshLayout** - Pull-to-refresh
- **RecyclerView** - Hiển thị danh sách hiệu quả

### Architecture
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Lifecycle Components** - Quản lý lifecycle aware components
- **LiveData** - Observable data holder

## 📋 Yêu Cầu Hệ Thống

- **Android SDK**: API level 24+ (Android 7.0)
- **Target SDK**: API level 35 (Android 15)
- **Compile SDK**: API level 35
- **Java Version**: Compatible với Java 8+
- **Gradle**: 8.9.2+

## 🚀 Cài Đặt và Chạy Dự Án

### 1. Clone Repository
```bash
git clone https://github.com/huy1235588/QuizMe
cd QuizMe
```

### 2. Cấu Hình Local Properties
Tạo file `local.properties` trong thư mục root với nội dung:
```properties
# API Configuration
BASE_URL=https://your-api-server.com/api/

# Android SDK Path
sdk.dir=/path/to/android/sdk
```

### 3. Build và Chạy
```bash
# Build project
./gradlew build

# Cài đặt debug APK
./gradlew installDebug

# Chạy tests
./gradlew test
```

## 📁 Cấu Trúc Dự Án

```
app/
├── src/main/java/com/huy/QuizMe/
│   ├── data/                    # Data layer (models, repositories)
│   ├── ui/                      # UI layer
│   │   ├── auth/               # Authentication screens
│   │   ├── main/               # Main app screens
│   │   │   ├── home/           # Home screen
│   │   │   ├── library/        # Library screen
│   │   │   ├── join/           # Join room screen
│   │   │   ├── createquiz/     # Create quiz screen
│   │   │   └── profile/        # Profile screen
│   │   ├── quiz/               # Quiz gameplay screens
│   │   ├── room/               # Waiting room screens
│   │   └── splash/             # Splash screen
│   ├── utils/                   # Utility classes
│   ├── MainActivity.java        # Main activity
│   └── QuizMeApplication.java   # Application class
├── src/main/res/               # Resources
│   ├── layout/                 # XML layouts
│   ├── drawable/               # Images and drawables
│   ├── values/                 # Strings, colors, styles
│   └── values-vi/              # Vietnamese translations
└── build.gradle.kts            # App-level build configuration
```

## 🌐 Hỗ Trợ Đa Ngôn Ngữ

Ứng dụng hỗ trợ:
- **Tiếng Anh** (mặc định)
- **Tiếng Việt** (values-vi)

## 🔧 Tính Năng Kỹ Thuật

### Real-time Communication
- WebSocket connection cho multiplayer gaming
- STOMP protocol cho messaging
- Automatic reconnection handling

### Offline Support
- Room database cho local storage
- Cache management với Glide
- Offline mode cho đã tải quiz

### Security
- Network security configuration
- Proguard rules cho code obfuscation
- Safe API calls với error handling

### Performance
- RecyclerView optimization
- Image loading optimization với Glide
- Memory leak prevention với lifecycle awareness

## 🧪 Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedDebugAndroidTest
```

## 📱 APK Build

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

APK files sẽ được tạo trong `app/build/outputs/apk/`

---
