from flask import Flask, request, send_file
import os
import cv2
import numpy as np
from insightface.app import FaceAnalysis
from insightface.model_zoo import get_model
import io

app = Flask(__name__)

# Initialize face analysis model
print("🚀 Initializing models...")
face_analyzer = FaceAnalysis(name='buffalo_l', providers=['CPUExecutionProvider'])
face_analyzer.prepare(ctx_id=0, det_size=(480, 480))
face_swapper = get_model('inswapper_128.onnx', download=False)

def process_uploaded_image(file):
    # Read image file into memory
    file_bytes = file.read()
    nparr = np.frombuffer(file_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    # Resize if needed
    h, w = img.shape[:2]
    if w > 640:
        scale = 640 / w
        img = cv2.resize(img, (int(w * scale), int(h * scale)))
    
    return img

@app.route('/swap-face', methods=['POST'])
def swap_face():
    try:
        # Check if both files are present in request
        if 'source_face' not in request.files or 'target_image' not in request.files:
            return {'error': 'Please provide both source_face and target_image files'}, 400

        source_file = request.files['source_face']
        target_file = request.files['target_image']

        # Process uploaded images
        source_img = process_uploaded_image(source_file)
        target_img = process_uploaded_image(target_file)

        # Resize images to improve processing speed
        source_img = cv2.resize(source_img, (0, 0), fx=0.5, fy=0.5)
        target_img = cv2.resize(target_img, (0, 0), fx=0.5, fy=0.5)

        # Detect faces
        source_faces = face_analyzer.get(source_img)
        target_faces = face_analyzer.get(target_img)

        if not source_faces or not target_faces:
            return {'error': 'No faces detected in one or both images'}, 400

        # Perform face swap
        result_img = face_swapper.get(target_img, target_faces[0], source_faces[0], paste_back=True)

        # Convert result to bytes
        _, buffer = cv2.imencode('.jpg', result_img)
        io_buf = io.BytesIO(buffer)

        return send_file(
            io_buf,
            mimetype='image/jpeg',
            as_attachment=True,
            download_name='swapped_face.jpg'
        )

    except Exception as e:
        return {'error': str(e)}, 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True) 