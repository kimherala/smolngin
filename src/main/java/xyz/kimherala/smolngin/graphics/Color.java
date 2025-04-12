package xyz.kimherala.smolngin.graphics;

public class Color {
    private float red;
    private float green;
    private float blue;

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static Color hex(String hex) {
        float red;
        float green;
        float blue;

        byte[] result = hexToBytes(hex);

        if (result.length != 3) {
            throw new RuntimeException("Error in Color class: hexToBytes too short or too long.");
        }

        red = byteToFloat(result[0]);
        green = byteToFloat(result[1]);
        blue = byteToFloat(result[2]);

        return new Color(red, green, blue);
    }

    public static Color hsl(float hue, float saturation, float lightness) {
        int[] result = hslToRgb(hue, saturation, lightness);
        return new Color(intToFloat(result[0]), intToFloat(result[1]), intToFloat(result[2]));
    }

    public void setHex(String hex) {
        byte[] result = hexToBytes(hex);

        if (result.length != 3) {
            throw new RuntimeException("Error in Color class: hexToBytes too short or too long.");
        }

        this.red = result[0];
        this.green = result[1];
        this.blue = result[2];
    }

    public float[] getAsFloats() {
        return new float[]{red, green, blue};
    }

    public static int[] hslToRgb(float h, float s, float l) {
        s = Math.max(0, Math.min(1, s));
        l = Math.max(0, Math.min(1, l));

        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r = 0, g = 0, b = 0;

        if (0 <= h && h < 60) {
            r = c; g = x; b = 0;
        } else if (60 <= h && h < 120) {
            r = x; g = c; b = 0;
        } else if (120 <= h && h < 180) {
            r = 0; g = c; b = x;
        } else if (180 <= h && h < 240) {
            r = 0; g = x; b = c;
        } else if (240 <= h && h < 300) {
            r = x; g = 0; b = c;
        } else if (300 <= h && h < 360) {
            r = c; g = 0; b = x;
        }

        int red = Math.round((r + m) * 255);
        int green = Math.round((g + m) * 255);
        int blue = Math.round((b + m) * 255);

        return new int[]{red, green, blue};
    }

    private static byte[] hexToBytes(String hex) {
        String hexValue = hex.split("#")[1];

        byte[] result = new byte[hexValue.length() / 2];
        for (int i = 0; i < 6; i+=2) {
            result[i / 2] = (byte) ((Character.digit(hexValue.charAt(i), 16) << 4) + Character.digit(hexValue.charAt(i+1), 16));
        }

        return result;
    }

    private static float byteToFloat(byte input) {
        return 1.0f / 255.0f * (float)(input & 0xFF);
    }

    private byte floatToByte(float input) {
        return (byte) (input * 255.0f);
    }

    private static float intToFloat(int input) {
        return 1.0f / 255.0f * input;
    }
}
