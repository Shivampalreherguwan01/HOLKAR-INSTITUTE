import os
from PIL import Image

sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

# Image file ko detect karte hain
img_path = ''
for f in os.listdir('.'):
    if f.endswith('.png') and f != 'ic_launcher.png':
        img_path = f
        break

if not img_path:
    print("Error: Icon image not found in directory!")
else:
    print(f"Using icon source: {img_path}")
    try:
        img = Image.open(img_path).convert('RGBA')
        for folder, size in sizes.items():
            dir_path = os.path.join('app', 'src', 'main', 'res', folder)
            os.makedirs(dir_path, exist_ok=True)
            
            resized = img.resize((size, size), Image.Resampling.LANCZOS)
            resized.save(os.path.join(dir_path, 'ic_launcher.png'))
            resized.save(os.path.join(dir_path, 'ic_launcher_round.png'))
        print("Icons generated successfully for all resolutions!")
    except Exception as e:
        print(f"Error: {e}")
