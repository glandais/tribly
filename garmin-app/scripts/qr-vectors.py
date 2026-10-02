# /// script
# dependencies = ["qrcode", "opencv-python-headless", "numpy"]
# ///
"""Reference matrices for source/QrCodeTest.mc, encoded by python-qrcode with QrCode.mc's
settings (byte mode, level L, mask 0) and checked to decode with OpenCV.

    uv run scripts/qr-vectors.py    # then paste the output into VECTORS in QrCodeTest.mc

segno is not usable as a reference: it pads the data differently, which gives another valid code.
"""

import cv2
import numpy as np
import qrcode

# A typical pairing URL (version 3), the longest that fits version 3 (53 bytes), one byte more
# (version 4) and the longest that fits version 4 (78 bytes).
TEXTS = [
    "https://pedalons.fr/garmin?code=ABC123",
    "https://" + "a" * 45,
    "https://" + "a" * 46,
    "https://" + "a" * 70,
]


def decode(rows):
    n, scale = len(rows), 10
    img = np.full(((n + 8) * scale, (n + 8) * scale), 255, np.uint8)
    for y, row in enumerate(rows):
        for x, dark in enumerate(row):
            if dark:
                img[(y + 4) * scale : (y + 5) * scale, (x + 4) * scale : (x + 5) * scale] = 0
    return cv2.QRCodeDetector().detectAndDecode(img)[0]


for text in TEXTS:
    version = 3 if len(text.encode()) <= 53 else 4
    qr = qrcode.QRCode(
        version=version, error_correction=qrcode.constants.ERROR_CORRECT_L, mask_pattern=0, border=0
    )
    qr.add_data(text.encode())
    qr.make(fit=False)
    rows = qr.get_matrix()
    assert decode(rows) == text, text
    print(f'        ["{text}", [')
    for row in rows:
        print('            "' + "".join("1" if m else "0" for m in row) + '",')
    print("        ]],")
