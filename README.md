# Face Swap App 🎭

Simple face swapping application using InsightFace with Mobile support.

## Features / Tính năng
- Face swapping using InsightFace
- Android mobile app support
- REST API for face swapping
- Real-time face swapping through API
- Full-screen image preview
- Support both emulator and real devices

## System Architecture / Kiến trúc hệ thống

```
┌─────────────┐     HTTP     ┌──────────┐
│ Android App │ ──────────── │ Flask API│
└─────────────┘   (Images)   └──────────┘
```

## Demo Results / Kết quả demo

<table>
  <tr>
    <td><img src="face.png" width="250" alt="Source Face"></td>
    <td><img src="target.png" width="250" alt="Target Image"></td>
    <td><img src="result.jpg" width="250" alt="Result"></td>
  </tr>
  <tr>
    <td>Source Face / Khuôn mặt nguồn</td>
    <td>Target Image / Ảnh đích</td>
    <td>Result / Kết quả</td>
  </tr>
</table>

## Installation / Cài đặt

### Backend API Setup / Cài đặt API Backend

1. Clone repository / Tải repository về máy:
```bash
git clone https://github.com/chinhvqbbl/Face-Swap.git
cd Face-Swap
```

2. Install dependencies / Cài đặt thư viện:
```bash
pip install -r requirements.txt
```

3. Download model / Tải model:
```bash
python download_model.py
```

4. Start the API server / Khởi động server:
```bash
python api.py
```

The API will be available at `http://localhost:5000`

### Android App Setup / Cài đặt ứng dụng Android

1. Open the `Mobile/Android` folder in Android Studio
2. Configure the API URL in `MainActivity.kt`:
   - For emulator: `http://10.0.2.2:5000/swap-face`
   - For real device: `http://YOUR_PC_IP:5000/swap-face`
3. Build and run the app

## API Endpoints / Các API

### Face Swap API
- **URL**: `/swap-face`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `source_face`: Source face image file
  - `target_image`: Target image file
- **Response**: JPEG image file

## Mobile App Features / Tính năng ứng dụng Mobile

- Select source face image
- Select target image
- Real-time face swapping
- Full-screen image preview
- Progress indication
- Error handling and retry
- Support both emulator and real devices

## Requirements / Yêu cầu

### Backend Requirements
* Python 3.9+
* Flask
* OpenCV
* InsightFace
* ONNX Runtime
* NumPy
* ✅ Visual Studio (phải cài Workload: **Desktop development with C++**)

### Android Requirements
* Android Studio
* Android SDK 24+
* Kotlin support
* Internet permission
* Storage permission

## Configuration / Cấu hình

### Android App
1. **API URL Configuration**:
   ```kotlin
   // For emulator
   private val API_URL = "http://10.0.2.2:5000/swap-face"
   
   // For real device (replace with your PC's IP)
   private val API_URL = "http://192.168.1.x:5000/swap-face"
   ```

2. **Required Permissions**:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
   ```

### API Server
1. **Server Configuration**:
   ```python
   app.run(host='0.0.0.0', port=5000, debug=True)
   ```

2. **Firewall Settings**:
   - Ensure port 5000 is open
   - Allow incoming connections for Python/Flask

## Troubleshooting / Xử lý sự cố

1. **Cannot connect to API**:
   - Check if API server is running
   - Verify IP address configuration
   - Ensure device and PC are on same network
   - Check firewall settings

2. **Image upload fails**:
   - Check storage permissions
   - Verify image file size
   - Ensure valid image format

3. **Face detection fails**:
   - Ensure clear, front-facing faces
   - Check image quality
   - Verify lighting conditions

## Notes / Lưu ý

* The source image should contain a clear, front-facing face
* The target image should contain the face(s) you want to swap
* For real devices, ensure both phone and PC are on the same network
* API server must be running before using the mobile app
* Ảnh nguồn nên chứa một khuôn mặt rõ ràng, nhìn thẳng
* Ảnh đích nên chứa (các) khuôn mặt bạn muốn hoán đổi
* Với thiết bị thật, đảm bảo điện thoại và PC cùng mạng
* Server API phải được chạy trước khi sử dụng ứng dụng mobile

## License / Giấy phép

MIT License 
