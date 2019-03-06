package es.studium;

import java.io.IOException;
import java.net.SocketException;
import org.apache.commons.net.ftp.*;

public class ClienteFTP1 {
	public static void main(String[] args) throws SocketException, IOException {
		FTPClient cliente = new FTPClient();
		String servFTP = "ftp.rediris.es"; // servidor FTP
		System.out.println("Nos conectamos a: " + servFTP);
		cliente.connect(servFTP);
//respuesta del servidor FTP
		System.out.print(cliente.getReplyString());
//c�digo de respuesta
		int respuesta = cliente.getReplyCode();
//comprobaci�n del c�digo de respuesta
		if (!FTPReply.isPositiveCompletion(respuesta)) {
			cliente.disconnect();
			System.out.println("Conexi�n rechazada: " + respuesta);
			System.exit(0);
		}
//desconexi�n del servidor FTP
		cliente.disconnect();
		System.out.println("Conexi�n finalizada.");
	}
}