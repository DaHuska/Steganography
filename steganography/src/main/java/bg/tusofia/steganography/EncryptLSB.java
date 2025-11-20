package bg.tusofia.steganography;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class EncryptLSB {
    // TODO: rename variable
    private BufferedImage bufferedImageCopy;

    // TODO: is constructor going to be empty
    public EncryptLSB() {}

    public void encryptMessageInImage(BufferedImage image, String message) {
        // TODO: Validate message length and image pixels

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
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                pixels[index] = new Pixel(x, y, new Color(imageToEncrypt.getRGB(x, y), true));

                index++;
            }
        }

        return pixels;
    }
}
