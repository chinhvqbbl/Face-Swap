# Face Swap App 🎭

Simple face swapping application using InsightFace.

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

1. Clone repository / Tải repository về máy:
```bash
git clone https://github.com/chinhvqbbl/Face-Swap.git
cd Face-Swap
```

2. Install dependencies / Cài đặt thư viện:
```bash
pip install opencv-python numpy insightface onnxruntime
```

3. Download model / Tải model:
* Download file `inswapper_128.onnx` from [Huggingface](https://huggingface.co/ezioruan/inswapper_128.onnx)
* Place it in the root directory / Đặt file vào thư mục gốc của project

> ✅ **Important / Quan trọng**:
> Bạn **bắt buộc phải cài** `Desktop development with C++` thông qua **Visual Studio Installer**
> Nếu không, ONNX Runtime và InsightFace sẽ lỗi khi khởi tạo model.

## Usage / Cách sử dụng

1. Place your source face image as `face.png` / Đặt ảnh khuôn mặt nguồn với tên `face.png`
2. Place your target image as `target.png` / Đặt ảnh đích với tên `target.png`
3. Run the script / Chạy script:
```bash
python main.py
```
4. Check `result.jpg` for the output / Kết quả sẽ được lưu trong file `result.jpg`

## Requirements / Yêu cầu

* Python 3.9+
* OpenCV
* InsightFace
* ONNX Runtime
* NumPy
* ✅ Visual Studio (phải cài Workload: **Desktop development with C++**)

## Notes / Lưu ý

* The source image (`face.png`) should contain a clear, front-facing face
* The target image (`target.png`) should contain the face(s) you want to swap
* Ảnh nguồn (`face.png`) nên chứa một khuôn mặt rõ ràng, nhìn thẳng
* Ảnh đích (`target.png`) nên chứa (các) khuôn mặt bạn muốn hoán đổi

## License / Giấy phép

MIT License 
