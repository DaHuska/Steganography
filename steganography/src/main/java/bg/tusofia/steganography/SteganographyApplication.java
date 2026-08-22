package bg.tusofia.steganography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@SpringBootApplication
public class SteganographyApplication {
	private static String IMG_IN_PATH;
	private static String IMG_OUT_PATH;
	private static String MESSAGE;
	private static String PASSWORD;

	public SteganographyApplication(Environment environment) {
		IMG_IN_PATH = environment.getProperty("steganography.image.input.path");
		IMG_OUT_PATH = environment.getProperty("steganography.image.output.path");
		MESSAGE = environment.getProperty("steganography.message");
		PASSWORD = environment.getProperty("steganography.password");
	}

	public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
		SpringApplication.run(SteganographyApplication.class, args);

		File file = new File(IMG_IN_PATH);

		BufferedImage inputImage = ImageIO.read(file);

		String format = URLConnection.guessContentTypeFromName(file.getName()).split("/")[1];

		BufferedImage outputImage =  EncryptLSB.encryptMessageInImage(inputImage, MESSAGE, PASSWORD);
		ImageIO.write(outputImage, "png", new File(IMG_OUT_PATH));

		File file2 = new File(IMG_OUT_PATH);
		BufferedImage inputImage2 = ImageIO.read(file2);

		EncryptLSB.extractMsgBitsFromImg(inputImage2);
	}
}