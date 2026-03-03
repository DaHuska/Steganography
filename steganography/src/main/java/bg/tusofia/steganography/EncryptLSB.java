package bg.tusofia.steganography;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

public class EncryptLSB {
    // TODO: rename variable
    private BufferedImage inputImage;

    // TODO: is constructor going to be empty
    public EncryptLSB() {}

    public static BufferedImage encryptMessageInImage(BufferedImage image, String message) throws InterruptedException {
        BufferedImage imageCopy = getImageCopy(image);

        validateImageSize(imageCopy);

        Pixel[] imagePixels = getPixels(imageCopy);
        String[] messageBinary = convertMessageToBinary(message);
        String messageBinaryText = convertMessageBinaryArrtoString(messageBinary);

        int index = 0;
        for (Pixel currPixel : imagePixels) {
            // Check if message reaches end
            if (index + 3 <= messageBinaryText.length()) {
                encodeIntoPixel(currPixel, messageBinaryText.substring(index, index + 3));

                index += 3;
            } else if (index < messageBinary.length) {
                encodeIntoPixel(currPixel, messageBinaryText.substring(index, messageBinaryText.length() - 1));

                break;
            } else {
                break;
            }
        }

        return createNewImage(imagePixels, image.getWidth(), image.getHeight());
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

    public BufferedImage getInputImage() {
        return inputImage;
    }
}
