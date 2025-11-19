package bg.tusofia.steganography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class SteganographyApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SteganographyApplication.class, args);

		EncryptLSB encryptor = new EncryptLSB();
	}

}
