import os
from PIL import Image

# Ensure mipmap directories exist
sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

img_path = '1000039951_2.png'  # Agar image ka naam yeh hai
if not os.path.exists(img_path):
    # Try finding any png in current directory
    files = [f for f in os.listdir('.') if f.endswith('.png')]
    if files:
        img_path = files[0]

try:
    img = Image.open(img_path).convert('RGBA')
    for folder, size in sizes.items():
        dir_path = os.path.join('app', 'src', 'main', 'res', folder)
        os.makedirs(dir_path, exist_ok=True)
        
        # Resize and save as launcher icons
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        resized.save(os.path.join(dir_path, 'ic_launcher.png'))
        resized.save(os.path.join(dir_path, 'ic_launcher_round.png'))
    print("Icons generated successfully!")
except Exception as e:
    print(f"Error: {e}")
