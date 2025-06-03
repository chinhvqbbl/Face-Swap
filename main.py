import os
import cv2
import time
import numpy as np
import insightface
from insightface.app import FaceAnalysis
from insightface.model_zoo import get_model

# Tắt cảnh báo từ OpenCV
os.environ["OPENCV_LOG_LEVEL"] = "SILENT"

# Resize nếu ảnh quá lớn
def resize_if_needed(img, max_width=640):
    h, w = img.shape[:2]
    if w > max_width:
        scale = max_width / w
        return cv2.resize(img, (int(w * scale), int(h * scale)))
    return img

# Load ảnh an toàn
def load_image(path):
    img = cv2.imread(path, cv2.IMREAD_COLOR)
    if img is None:
        raise FileNotFoundError(f"Không đọc được ảnh: {path}")
    return resize_if_needed(img)

def main():
    start = time.time()

    print("🚀 Khởi tạo mô hình...")
    app = FaceAnalysis(name='buffalo_l', providers=['CPUExecutionProvider'])
    app.prepare(ctx_id=0, det_size=(480, 480))
    swapper = get_model('inswapper_128.onnx', download=False)

    print("📷 Đọc ảnh đầu vào...")
    img_src = load_image("face.png")
    img_dst = load_image("target.png")
    
    # Giảm kích thước ảnh trước khi swap để tăng tốc
    img_src = cv2.resize(img_src, (0, 0), fx=0.5, fy=0.5)
    img_dst = cv2.resize(img_dst, (0, 0), fx=0.5, fy=0.5)

    print("🔍 Phát hiện khuôn mặt...")
    faces_src = app.get(img_src)
    faces_dst = app.get(img_dst)
    print(f" - Ảnh nguồn: {len(faces_src)} khuôn mặt")
    print(f" - Ảnh đích: {len(faces_dst)} khuôn mặt")

    if faces_src and faces_dst:
        print("🔄 Đang hoán đổi khuôn mặt...")
        result = swapper.get(img_dst, faces_dst[0], faces_src[0], paste_back=True)
        cv2.imwrite("result.jpg", result)
        print("✅ Đã lưu kết quả: result.jpg")
    else:
        print("❌ Không phát hiện được khuôn mặt trong một trong hai ảnh.")

    print(f"⏱️ Thời gian xử lý: {time.time() - start:.2f} giây")

if __name__ == "__main__":
    main()
