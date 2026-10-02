using Toybox.Lang;
using Toybox.Math;

/**
 * QR code encoder (ISO/IEC 18004) for the pairing URL: Toybox has no barcode API.
 *
 * Cut down to what LoginView needs: byte mode, error correction level L, versions 3 and 4 only
 * (one Reed-Solomon block each, no version information) and mask 0 always. Any mask is valid for
 * a reader; scoring the eight of them is the costly part of an encoder and the one that would
 * risk the Connect IQ watchdog.
 *
 * The result is one byte per module, row by row (1 = dark), without the quiet zone.
 * Checked module for module against python-qrcode: QrCodeTest.mc, vectors from scripts/qr-vectors.py.
 */
module QrCode {
    // [version, side, data codewords, ECC codewords] at level L
    const VERSIONS = [
        [3, 29, 55, 15],
        [4, 33, 80, 20],
    ];

    // Format information for level L + mask 0: BCH(15,5) code of 0b01000, XORed with 0x5412
    const FORMAT_BITS = 0x77c4;

    /**
     * Encode text, or null when it does not fit in a version 4 code (78 bytes).
     */
    function encode(text as Lang.String) as Lang.ByteArray? {
        var bytes = text.toUtf8Array();
        for (var v = 0; v < VERSIONS.size(); v++) {
            var spec = VERSIONS[v];
            // Mode (4 bits) and count (8 bits) take a byte and a half of the data codewords
            if (bytes.size() <= spec[2] - 2) {
                return build(spec, bytes);
            }
        }
        return null;
    }

    /**
     * Number of modules on a side of an encoded matrix.
     */
    function sideOf(modules as Lang.ByteArray) as Lang.Number {
        return Math.sqrt(modules.size()).toNumber();
    }

    function build(
        spec as Lang.Array<Lang.Number>,
        bytes as Lang.Array<Lang.Number>
    ) as Lang.ByteArray {
        var side = spec[1];
        var codewords = dataCodewords(bytes, spec[2]);
        codewords.addAll(reedSolomon(codewords, spec[3]));

        var modules = new [side * side]b;
        var reserved = new [side * side]b;
        drawFunctionPatterns(modules, reserved, side);
        drawCodewords(modules, reserved, side, codewords);
        return modules;
    }

    function dataCodewords(
        bytes as Lang.Array<Lang.Number>,
        count as Lang.Number
    ) as Lang.ByteArray {
        var out = new [count]b;
        var pos = putBits(out, 0, 0x4, 4);
        pos = putBits(out, pos, bytes.size(), 8);
        for (var i = 0; i < bytes.size(); i++) {
            pos = putBits(out, pos, bytes[i], 8);
        }
        // Terminator (up to four zero bits) then padding to the byte: the array is already zero
        var used = (pos + 4 + 7) / 8;
        if (used > count) {
            used = count;
        }
        for (var i = used; i < count; i++) {
            out[i] = (i - used) % 2 == 0 ? 0xec : 0x11;
        }
        return out;
    }

    function putBits(
        out as Lang.ByteArray,
        pos as Lang.Number,
        value as Lang.Number,
        len as Lang.Number
    ) as Lang.Number {
        for (var i = len - 1; i >= 0; i--) {
            if (((value >> i) & 1) != 0) {
                out[pos >> 3] = out[pos >> 3] | (0x80 >> (pos & 7));
            }
            pos++;
        }
        return pos;
    }

    /**
     * Product in GF(256) modulo x^8 + x^4 + x^3 + x^2 + 1.
     */
    function multiply(x as Lang.Number, y as Lang.Number) as Lang.Number {
        var z = 0;
        for (var i = 7; i >= 0; i--) {
            z = (z << 1) ^ ((z >> 7) * 0x11d);
            z = z ^ (((y >> i) & 1) * x);
        }
        return z;
    }

    function reedSolomon(data as Lang.ByteArray, degree as Lang.Number) as Lang.ByteArray {
        // Generator polynomial (x - a^0)(x - a^1)...(x - a^(degree-1)), leading term dropped
        var divisor = new [degree]b;
        divisor[degree - 1] = 1;
        var root = 1;
        for (var i = 0; i < degree; i++) {
            for (var j = 0; j < degree; j++) {
                var term = multiply(divisor[j], root);
                if (j + 1 < degree) {
                    term = term ^ divisor[j + 1];
                }
                divisor[j] = term;
            }
            root = multiply(root, 0x02);
        }

        var remainder = new [degree]b;
        for (var k = 0; k < data.size(); k++) {
            var factor = data[k] ^ remainder[0];
            for (var i = 0; i < degree - 1; i++) {
                remainder[i] = remainder[i + 1] ^ multiply(divisor[i], factor);
            }
            remainder[degree - 1] = multiply(divisor[degree - 1], factor);
        }
        return remainder;
    }

    function setFunction(
        modules as Lang.ByteArray,
        reserved as Lang.ByteArray,
        side as Lang.Number,
        x as Lang.Number,
        y as Lang.Number,
        dark as Lang.Boolean
    ) as Void {
        var i = y * side + x;
        modules[i] = dark ? 1 : 0;
        reserved[i] = 1;
    }

    function drawFunctionPatterns(
        modules as Lang.ByteArray,
        reserved as Lang.ByteArray,
        side as Lang.Number
    ) as Void {
        // Timing patterns, partly overwritten by the finders
        for (var i = 0; i < side; i++) {
            setFunction(modules, reserved, side, 6, i, i % 2 == 0);
            setFunction(modules, reserved, side, i, 6, i % 2 == 0);
        }

        // Finder patterns with their separators, in three corners
        var centers = [
            [3, 3],
            [side - 4, 3],
            [3, side - 4],
        ];
        for (var c = 0; c < centers.size(); c++) {
            for (var dy = -4; dy <= 4; dy++) {
                for (var dx = -4; dx <= 4; dx++) {
                    var x = centers[c][0] + dx;
                    var y = centers[c][1] + dy;
                    if (x >= 0 && x < side && y >= 0 && y < side) {
                        var dist = chebyshev(dx, dy);
                        setFunction(modules, reserved, side, x, y, dist != 2 && dist != 4);
                    }
                }
            }
        }

        // The single alignment pattern of versions 2 to 6
        for (var dy = -2; dy <= 2; dy++) {
            for (var dx = -2; dx <= 2; dx++) {
                setFunction(
                    modules,
                    reserved,
                    side,
                    side - 7 + dx,
                    side - 7 + dy,
                    chebyshev(dx, dy) != 1
                );
            }
        }

        // Format information, both copies; the mask is fixed so it can be drawn now
        for (var i = 0; i <= 5; i++) {
            setFunction(modules, reserved, side, 8, i, formatBit(i));
        }
        setFunction(modules, reserved, side, 8, 7, formatBit(6));
        setFunction(modules, reserved, side, 8, 8, formatBit(7));
        setFunction(modules, reserved, side, 7, 8, formatBit(8));
        for (var i = 9; i < 15; i++) {
            setFunction(modules, reserved, side, 14 - i, 8, formatBit(i));
        }
        for (var i = 0; i < 8; i++) {
            setFunction(modules, reserved, side, side - 1 - i, 8, formatBit(i));
        }
        for (var i = 8; i < 15; i++) {
            setFunction(modules, reserved, side, 8, side - 15 + i, formatBit(i));
        }
        // The dark module
        setFunction(modules, reserved, side, 8, side - 8, true);
    }

    function chebyshev(dx as Lang.Number, dy as Lang.Number) as Lang.Number {
        var ax = dx < 0 ? -dx : dx;
        var ay = dy < 0 ? -dy : dy;
        return ax > ay ? ax : ay;
    }

    function formatBit(i as Lang.Number) as Lang.Boolean {
        return ((FORMAT_BITS >> i) & 1) != 0;
    }

    /**
     * Zigzag placement in two-column strips from the bottom right, mask 0 applied on the way.
     * Remainder modules (7 in versions 3 and 4) stay light before masking.
     */
    function drawCodewords(
        modules as Lang.ByteArray,
        reserved as Lang.ByteArray,
        side as Lang.Number,
        codewords as Lang.ByteArray
    ) as Void {
        var total = codewords.size() * 8;
        var bit = 0;
        var right = side - 1;
        while (right >= 1) {
            if (right == 6) {
                // The vertical timing pattern takes a whole column
                right = 5;
            }
            var upward = ((right + 1) & 2) == 0;
            for (var vert = 0; vert < side; vert++) {
                var y = upward ? side - 1 - vert : vert;
                for (var j = 0; j < 2; j++) {
                    var x = right - j;
                    var i = y * side + x;
                    if (reserved[i] == 0) {
                        var dark = false;
                        if (bit < total) {
                            dark = ((codewords[bit >> 3] >> (7 - (bit & 7))) & 1) != 0;
                            bit++;
                        }
                        if ((x + y) % 2 == 0) {
                            dark = !dark;
                        }
                        modules[i] = dark ? 1 : 0;
                    }
                }
            }
            right -= 2;
        }
    }
}
