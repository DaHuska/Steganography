package bg.tusofia.steganography;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

public class EncryptLSB {
    // TODO: rename variable
    private BufferedImage inputImage;

    // TODO: is constructor going to be empty
    public EncryptLSB() {}

    public void encryptMessageInImage(BufferedImage image, String message) {
        // TODO: Validate message length and image pixels
        BufferedImage imageCopy = getImageCopy(image);
        Pixel[] imagePixels = getPixels(imageCopy);
        String[] messageBinary = convertMessageToBinary(message);
        String messageBinaryText = convertMessageBinaryArrtoString(messageBinary);

        int index = 0;
        for (int i = 0; i < imagePixels.length; i++) {
            // Use same pixel object
            // Check if message reaches end
            Pixel currPixel = imagePixels[i];
            if (index + 3 <= messageBinaryText.length()) {
                encodeIntoPixel(currPixel, messageBinaryText.substring(index, index + 3));

                index+=3;
            } else {
                encodeIntoPixel(currPixel, messageBinaryText.substring(index, messageBinaryText.length() - 1));
            }

            imagePixels[i] = currPixel;
        }
    }

    private void encodeIntoPixel(Pixel pixel, String bits) {
        Map<String, String> colorsBinary = convertPixelColorsToBinary(pixel);

        // TODO: continue with next LSBs if current LSBs run out
        if (bits.length() == 1) {
            colorsBinary.put("red", colorsBinary.get("red").substring(0, 7) + bits.charAt(0));
        } else if (bits.length() == 2) {
            colorsBinary.put("red", colorsBinary.get("red").substring(0, 7) + bits.charAt(0));
            colorsBinary.put("green", colorsBinary.get("green").substring(0, 7) + bits.charAt(1));
        } else if (bits.length() == 3) {
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

    private String[] convertMessageToBinary(String message) {
        String[] binaryValues = new String[message.length()];

        for (int i = 0; i < message.length(); i++) {
            byte asciiValue = (byte) message.charAt(i);

            binaryValues[i] = String
                    .format("%8s", Integer.toBinaryString(asciiValue & 0xFF)).replace(' ', '0');
        }

        return binaryValues;
    }

    private String convertMessageBinaryArrtoString(String[] messageBinary) {
        StringBuilder message = new StringBuilder();

        for (String binary : messageBinary) {
            message.append(binary);
        }

        return message.toString();
    }

    private Map<String, String> convertPixelColorsToBinary(Pixel pixel) {
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

    private BufferedImage getImageCopy(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster writableRaster = image.copyData(image.getRaster().createCompatibleWritableRaster());

        return new BufferedImage(colorModel, writableRaster, isAlphaPremultiplied, null);
    }

    private Pixel[] getPixels(BufferedImage imageToEncrypt) {
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

    public BufferedImage getInputImage() {
        return inputImage;
    }
}
