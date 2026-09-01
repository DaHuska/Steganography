package bg.tusofia.steganography;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class EncryptLSB {
    public EncryptLSB() {}

    public static BufferedImage encryptMessageInImage(BufferedImage image, String message, String password)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
            InvalidKeyException {
        BufferedImage imageCopy = getImageCopy(image);

        validateImageSize(imageCopy);

        // Encrypt message
        String cipherMsg = AESImpl.encryptMessage(message, password);

        int cipherLength = cipherMsg.length();
        Pixel[] imagePixels = getPixels(imageCopy);
        String[] messageBinary = convertMessageToBinary(cipherMsg);
        String messageBinaryText = convertMessageBinaryArrtoString(messageBinary);
        Pixel[] startingPixels = new Pixel[11];

        int index = 0;
        int bitsIndex = 0;
        for (int i = 0; i < imagePixels.length; i++) {
            if (i < 11) {
                startingPixels[i] = imagePixels[i];

                continue;
            }
            encodeCipherLength(startingPixels, cipherLength);

            if (bitsIndex + 3 <= messageBinaryText.length()) {
                encodeIntoPixel(imagePixels[i], messageBinaryText.substring(bitsIndex, bitsIndex + 3));

                bitsIndex += 3;
            } else if (bitsIndex < messageBinaryText.length()) {
                encodeIntoPixel(imagePixels[i], messageBinaryText.substring(bitsIndex));

                break;
            } else {
                break;
            }
        }

        return createNewImage(imagePixels, image.getWidth(), image.getHeight());
    }

    public static void extractMsgFromImage(BufferedImage image, String password) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        String cipherText = convertBitsToString(extractMsgBitsFromImg(image));
        String message = AESImpl.decryptMessage(password, cipherText);

        System.out.println(message);
    }

    private static void encodeCipherLength(Pixel[] pixels, int cipherLength) {
        int a = cipherLength / 255;
        int b = cipherLength % 255;

        StringBuilder bd = new StringBuilder();
        for (int i = 0; i < a; i++) {
            bd.append("11111111");
        }

        bd.append(String.format("%8s", Integer.toBinaryString((byte) b & 0xFF)).replace(' ', '0'));

        for (int i = 0; i < 4 - a - 1; i++) {
            bd.append("00000000");
        }

        String bits = bd.toString();
        int index = 0;

        for (Pixel pixel : pixels) {
            if (index + 3 <= 32) {
                encodeIntoPixel(pixel, bits.substring(index, index + 3));
            } else {
                encodeIntoPixel(pixel, bits.substring(bits.length() - 2));
            }

            index += 3;
        }
    }

    public static String extractMsgBitsFromImg(BufferedImage image) {
        Pixel[] pixels = getPixels(image);
        int length = extractCipherLength(pixels);

        StringBuilder messageBits = new StringBuilder();
        for (int i = 11; i < ((length) * 8 / 3) + 12; i++) {
            Pixel currPixel = pixels[i];

            Map<String, String> colorsBinary = convertPixelColorsToBinary(currPixel);
            messageBits.append(colorsBinary.get("red").charAt(7));
            messageBits.append(colorsBinary.get("green").charAt(7));
            messageBits.append(colorsBinary.get("blue").charAt(7));
        }

        return messageBits.toString();
    }

    private static String convertBitsToString(String bits) {
        StringBuilder cipherText = new StringBuilder();
        int size = bits.length();

        for (int i = 0; i <= size - 8 - (size % 8); i+=8) {
            String currBits = bits.substring(i, i + 8);

            byte b = (byte) Integer.parseInt(currBits, 2);
            cipherText.append((char)b);

        }

        return cipherText.toString();
    }

    private static void encodeIntoPixel(Pixel pixel, String bits) {
        Map<String, String> colorsBinary = convertPixelColorsToBinary(pixel);
        int size = bits.length();

        if (size == 1) {
            colorsBinary.put("red", colorsBinary.get("red").substring(0, 7) + bits.charAt(0));
        } else if (size == 2) {
            colorsBinary.put("red", colorsBinary.get("red").substring(0, 7) + bits.charAt(0));
            colorsBinary.put("green", colorsBinary.get("green").substring(0, 7) + bits.charAt(1));
        } else if (size == 3) {
            colorsBinary.put("red", colorsBinary.get("red").substring(0, 7) + bits.charAt(0));
            colorsBinary.put("green", colorsBinary.get("green").substring(0, 7) + bits.charAt(1));
            colorsBinary.put("blue", colorsBinary.get("blue").substring(0, 7) + bits.charAt(2));
        }

        Color newColor = new Color(
                Integer.parseInt(colorsBinary.get("red"), 2),
                Integer.parseInt(colorsBinary.get("green"), 2),
                Integer.parseInt(colorsBinary.get("blue"), 2)
        );

        pixel.setColor(newColor);
    }

    static int extractCipherLength(Pixel[] pixels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            Pixel currPixel = pixels[i];

            Map<String, String> colorsBinary = convertPixelColorsToBinary(currPixel);
            sb.append(colorsBinary.get("red").charAt(7));
            sb.append(colorsBinary.get("green").charAt(7));
            sb.append(colorsBinary.get("blue").charAt(7));
        }

        String bits = sb.toString();
        int length = 0;

        for (int i = 0; i < 32; i+=8) {
            length += Integer.parseInt(bits.substring(i, i + 8), 2);
        }

        return length;
    }

    private static String[] convertMessageToBinary(String message) {
        String[] binaryValues = new String[message.length()];

        for (int i = 0; i < message.length(); i++) {
            byte asciiValue = (byte) message.charAt(i);

            binaryValues[i] = String
                    .format("%8s", Integer.toBinaryString(asciiValue & 0xFF)).replace(' ', '0');
        }

        return binaryValues;
    }

    private static String convertMessageBinaryArrtoString(String[] messageBinary) {
        StringBuilder message = new StringBuilder();

        for (String binary : messageBinary) {
            message.append(binary);
        }

        return message.toString();
    }

    private static Map<String, String> convertPixelColorsToBinary(Pixel pixel) {
        Map<String, String> colorsBinary = new HashMap<>();

        byte red = (byte) pixel.getColor().getRed();
        byte green = (byte) pixel.getColor().getGreen();
        byte blue = (byte) pixel.getColor().getBlue();

        colorsBinary.putIfAbsent("red",
                String.format("%8s", Integer.toBinaryString(red & 0xFF)).replace(' ', '0'));
        colorsBinary.putIfAbsent("green",
                String.format("%8s", Integer.toBinaryString(green & 0xFF)).replace(' ', '0'));
        colorsBinary.putIfAbsent("blue",
                String.format("%8s", Integer.toBinaryString(blue & 0xFF)).replace(' ', '0'));

        return colorsBinary;
    }

    private static BufferedImage getImageCopy(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster writableRaster = image.copyData(image.getRaster().createCompatibleWritableRaster());

        return new BufferedImage(colorModel, writableRaster, isAlphaPremultiplied, null);
    }

    private static Pixel[] getPixels(BufferedImage imageToEncrypt) {
        int width = imageToEncrypt.getWidth();
        int height = imageToEncrypt.getHeight();
        int pixelsCount = width * height;

        Pixel[] pixels = new Pixel[pixelsCount];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[index] = new Pixel(x, y, new Color(imageToEncrypt.getRGB(x, y), true));

                index++;
            }
        }

        return pixels;
    }

    private static BufferedImage createNewImage(Pixel[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (Pixel pixel : pixels) {
            image.setRGB(pixel.getX(), pixel.getY(), pixel.getColor().getRGB());
        }

        return image;
    }

    private static void validateImageSize(BufferedImage image) {
        // Height should be more than 600 pixels
        // Width should be more than 600 pixels
        if (image.getHeight() < 600 || image.getWidth() < 600) {
            throw new IllegalArgumentException("Image size should be at least 600x600");
        }
    }
}
