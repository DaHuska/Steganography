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
import java.util.Scanner;

@SpringBootApplication
public class SteganographyApplication {
//	private static String IMG_IN_PATH;
//	private static String IMG_OUT_PATH;
//	private static String MESSAGE;
//	private static String PASSWORD;
//
//	public SteganographyApplication(Environment environment) {
//		IMG_IN_PATH = environment.getProperty("steganography.image.input.path");
//		IMG_OUT_PATH = environment.getProperty("steganography.image.output.path");
//		MESSAGE = environment.getProperty("steganography.message");
//		PASSWORD = environment.getProperty("steganography.password");
//	}

	public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException,
			NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
			InvalidKeySpecException, BadPaddingException, InvalidKeyException {

		SpringApplication.run(SteganographyApplication.class, args);
		Scanner scan = new Scanner(System.in);
		String operation = "";
		while (true) {
			System.out.println("Choose operation:");
			System.out.println("1 - Hide message in image");
			System.out.println("2 - Extract message from image");
			System.out.println("Enter 1 or 2 (or 'exit' to quit): ");

			operation = scan.nextLine().trim();

			if ("exit".equalsIgnoreCase(operation)) {
				System.out.println("Exiting...");
				scan.close();
				return;
			}

			if (operation.equals("1") || operation.equals("2")) {
				break;
			} else {
				System.out.println("Invalid option. Please enter 1 or 2.");
			}
		}

		boolean done = false;

		while (!done) {
			try {
				if (operation.equals("1")) {
					// ---------- HIDE MESSAGE ----------
					System.out.println("Image path (or 'exit' to quit): ");
					String imgPath = scan.nextLine();

					if ("exit".equalsIgnoreCase(imgPath)) {
						System.out.println("Exiting...");
						break;
					}

					File file = new File(imgPath);
					if (!file.exists() || !file.isFile() || !file.canRead()) {
						System.out.println("Invalid file path or file not readable. Please try again.");
						continue;
					}

					BufferedImage inputImage = ImageIO.read(file);
					if (inputImage == null) {
						System.out.println("Could not read image (unsupported format or corrupted file). Please try again.");
						continue;
					}

					System.out.println("Type in the message: ");
					String message = scan.nextLine();

					System.out.println("Type in password: ");
					String password = scan.nextLine();

					System.out.println("Output image path (e.g. output.png): ");
					String outPath = scan.nextLine();

					BufferedImage outputImage = EncryptLSB.encryptMessageInImage(inputImage, message, password);
					ImageIO.write(outputImage, "png", new File(outPath));

					System.out.println("Message hidden. Output saved to: " + outPath);
				} else if (operation.equals("2")) {
					System.out.println("Image path with hidden message (or 'exit' to quit): ");
					String imgPath = scan.nextLine();

					if ("exit".equalsIgnoreCase(imgPath)) {
						System.out.println("Exiting...");
						break;
					}

					File file = new File(imgPath);
					if (!file.exists() || !file.isFile() || !file.canRead()) {
						System.out.println("Invalid file path or file not readable. Please try again.");
						continue;
					}

					BufferedImage inputImage = ImageIO.read(file);
					if (inputImage == null) {
						System.out.println("Could not read image (unsupported format or corrupted file). Please try again.");
						continue;
					}

					System.out.println("Type in password: ");
					String password = scan.nextLine();

					String extracted = EncryptLSB.extractMsgFromImage(inputImage, password);
					System.out.println("Extracted message: " + extracted);
				}

				System.out.println("Do you want to perform another operation? (y/n): ");
				String again = scan.nextLine().trim();
				if (!"y".equalsIgnoreCase(again)) {
					done = true;
				} else {
					while (true) {
						System.out.println("Choose operation:");
						System.out.println("1 - Hide message in image");
						System.out.println("2 - Extract message from image");
						System.out.println("Enter 1 or 2 (or 'exit' to quit): ");

						operation = scan.nextLine().trim();

						if ("exit".equalsIgnoreCase(operation)) {
							System.out.println("Exiting...");
							scan.close();
							return;
						}

						if (operation.equals("1") || operation.equals("2")) {
							break;
						} else {
							System.out.println("Invalid option. Please enter 1 or 2.");
						}
					}
				}

			} catch (IOException e) {
				System.out.println("I/O error: " + e.getMessage() + ". Please try again.");
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid argument: " + e.getMessage() + ". Please try again.");
			} catch (Exception e) {
				System.out.println("Error during encryption/decryption: " + e.getMessage() + ". Please try again.");
			}
		}

		scan.close();
	}
}