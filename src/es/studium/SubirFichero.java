package es.studium;

import java.io.*;
import org.apache.commons.net.ftp.*;

public class SubirFichero {
	public static void main(String[] args) {
		FTPClient cliente = new FTPClient(); // cliente
		String servidor = "localhost"; // servidor
		String user = "oscarest";
		String pasw = "studium2018;";
		try {
			System.out.println("Conectandose a " + servidor);
			cliente.connect(servidor);
			boolean login = cliente.login(user, pasw);
			String direc = "/";
			if (login) {
				cliente.changeWorkingDirectory(direc);
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
//stream de entrada con el fichero a subir
				BufferedInputStream in = new BufferedInputStream(new FileInputStream("C:\\eula2052.txt"));
				cliente.storeFile("TEXTO1.txt", in);
				in = new BufferedInputStream(new FileInputStream("C:\\Users\\pc\\Pictures\\image.jpg"));
				cliente.storeFile("image.jpg", in);
				in.close(); // cerrar flujo
				cliente.logout(); // logout del usuario
				cliente.disconnect(); // desconexión del servidor
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
