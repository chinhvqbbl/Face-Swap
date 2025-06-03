from insightface.model_zoo import get_model

def download_model():
    print("Downloading face swap model...")
    model = get_model('inswapper_128.onnx', download=True, download_zip=True)
    print("Model downloaded successfully!")

if __name__ == "__main__":
    download_model() 