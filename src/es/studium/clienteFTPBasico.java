package es.studium;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class clienteFTPBasico extends JFrame {
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JTextField cab = new JTextField();
	static JTextField cab2 = new JTextField();
	static JTextField cab3 = new JTextField();
	// Campos de mensajes parte inferior
	private static JTextField campo = new JTextField();
	private static JTextField campo2 = new JTextField();
	// Botones
	JButton botonCargar = new JButton("Subir fichero");
	JButton botonDescargar = new JButton("Descargar fichero");
	JButton botonBorrar = new JButton("Eliminar fichero");
	JButton botonCreaDir = new JButton("Crear carpeta");
	JButton botonDelDir = new JButton("Eliminar carpeta");
	JButton botonSalir = new JButton("Salir");
	JPanel pnl = new JPanel(new GridLayout(6, 1));
	JPanel pnlTxt = new JPanel(new GridLayout(5, 1));
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container c = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "localhost";
	String user = "oscarest";
	String pasw = "studium2018;";
	boolean login;
	static String direcInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec = direcInicial;
	static String ficheroSelec = "";

	public static void main(String[] args) throws IOException {
		new clienteFTPBasico();
	} // final del main

	public clienteFTPBasico() throws IOException {
		super("CLIENTE BÁSICO FTP");
		// para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor); // conexión al servidor
		login = cliente.login(user, pasw);
		// Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcInicial);
		// Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		// Construyendo la lista de ficheros y directorios
		// del directorio de trabajo actual
		llenarLista(files, direcInicial);
		// preparar campos de pantalla
		campo.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
		cab.setText("Servidor FTP: " + servidor);
		cab2.setText("Usuario: " + user);
		cab3.setText("DIRECTORIO RAIZ: " + direcInicial);
		// Preparación de la lista
		// se configura el tipo de selección para que solo se pueda
		// seleccionar un elemento de la lista
		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(335, 420));
		// barraDesplazamiento.setBounds(new Rectangle(5, 65, 335, 420));

		pnlTxt.add(cab);
		pnlTxt.add(cab2);
		pnlTxt.add(cab3);
		pnlTxt.add(campo);
		pnlTxt.add(campo2);
		c.add(pnlTxt);

		c.add(barraDesplazamiento);
		c.setLayout(new FlowLayout());

		pnl.add(botonCargar);
		pnl.add(botonDescargar);
		pnl.add(botonBorrar);
		pnl.add(botonCreaDir);
		pnl.add(botonDelDir);
		pnl.add(botonSalir);
		c.add(pnl);
		// se añaden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 750);
		setLocationRelativeTo(this);
		setVisible(true);
		// Acciones al pulsar en la lista o en los botones
		listaDirec.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				// TODO Auto-generated method stub
				String fic = "";
				if (lse.getValueIsAdjusting()) {
					ficheroSelec = "";
					// elemento que se ha seleccionado de la lista
					fic = listaDirec.getSelectedValue().toString();
				}
				if (listaDirec.getSelectedIndex() == 0) {
					// Se hace clic en el primer elemento del JList
					// se comprueba si es el directorio inicial
					if (!fic.equals(direcInicial)) {
						// si no estamos en el directorio inicial, hay que
						// subir al directorio padre
						try {
							cliente.changeToParentDirectory();
							direcSelec = cliente.printWorkingDirectory();
							FTPFile[] ff2 = null;
							// directorio de trabajo actual=directorio padre
							cliente.changeWorkingDirectory(direcSelec);
							// se obtienen ficheros y directorios
							ff2 = cliente.listFiles();
							campo.setText("");
							// se llena la lista con ficheros del directorio padre
							llenarLista(ff2, direcSelec);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				// No se hace clic en el primer elemento del JList
				// Puede ser un fichero o un directorio
				else {
					if (fic.contains("(DIR)")) {
						// Se trata de un directorio
						try {
							fic = fic.substring(6);
							String direcSelec2 = "";
							if (direcSelec.equals("/"))
								direcSelec2 = direcSelec + fic;
							else
								direcSelec2 = direcSelec + "/" + fic;
							FTPFile[] ff2 = null;
							cliente.changeWorkingDirectory(direcSelec2);
							ff2 = cliente.listFiles();
							campo.setText("DIRECTORIO: " + fic + ", " + ff2.length + " elementos");
							// directorio actual
							direcSelec = direcSelec2;
							// se llena la lista con datos del directorio
							llenarLista(ff2, direcSelec);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					} else {
						// Se trata de un fichero
						ficheroSelec = direcSelec;
						if (direcSelec.equals("/"))
							direcSelec += fic;

						else
							direcSelec += "/" + fic;
						campo.setText("FICHERO SELECCIONADO: " + ficheroSelec);
						ficheroSelec = fic;// nos quedamos con el nocmbre

					} // fin else
				} // fin else de fichero o directorio
				campo2.setText("DIRECTORIO ACTUAL: " + direcSelec);
				// fin if inicial
			}
		});// fin acción en JList
		botonSalir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cliente.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		botonCreaDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio",
						"carpeta");
				if (!(nombreCarpeta == null)) {
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					// nombre del directorio a crear
					directorio += nombreCarpeta.trim(); // quita blancos a derecha y a izquierda
					try {
						if (cliente.makeDirectory(directorio)) {
							String m = nombreCarpeta.trim() + " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							campo.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2, direcSelec);
						} else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del botón CreaDir
		botonDelDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombreCarpeta = JOptionPane.showInputDialog(null,
						"Introduce el nombre del directorio a eliminar", "carpeta");
				if (!(nombreCarpeta == null)) {
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					// nombre del directorio a eliminar
					directorio += nombreCarpeta.trim(); // quita blancos a derecha y a izquierda
					try {
						if (cliente.removeDirectory(directorio)) {
							String m = nombreCarpeta.trim() + " => Se ha eliminado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							campo.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2, direcSelec);
						} else
							JOptionPane.showMessageDialog(null,
									nombreCarpeta.trim() + " => No se ha podido eliminar ...");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del botón Eliminar Carpeta
		botonCargar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser f;
				File file;
				f = new JFileChooser();
				// solo se pueden seleccionar ficheros
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// título de la ventana
				f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				// se muestra la ventana
				int returnVal = f.showDialog(f, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// fichero seleccionado
					file = f.getSelectedFile();
					// nombre completo del fichero
					String archivo = file.getAbsolutePath();
					// solo nombre del fichero
					String nombreArchivo = file.getName();
					try {
						SubirFichero(archivo, nombreArchivo);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}); // Fin botón subir
		botonDescargar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (
					!direcSelec.equals("")) 
				{
					DescargarFichero(directorio + ficheroSelec, ficheroSelec);
				}
			}
		}); // Fin botón descargar
		botonBorrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String directorio = direcSelec;
				if (!direcSelec.equals("/")) {
					directorio = directorio + "/";
				}
				if (!direcSelec.equals("")) 
				{
					BorrarFichero(directorio + ficheroSelec, ficheroSelec);
				}
			}
		});
	} // fin constructor

	private static void llenarLista(FTPFile[] files, String direc2) {
		if (files == null)
			return;
		// se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		// se definen propiedades para la lista, color y tipo de fuente
		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		// se eliminan los elementos de la lista
		listaDirec.removeAll();
		try {
			// se establece el directorio de trabajo actual
			cliente.changeWorkingDirectory(direc2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		direcSelec = direc2; // directorio actual
		// se añade el directorio de trabajo al listmodel, primer elemento
		modeloLista.addElement(direc2);
		// se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++) {
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) {
				// nos saltamos los directorios . y ..
				// Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				// Si es directorio se añade al nombre (DIR)
				if (files[i].isDirectory())
					f = "(DIR) " + f;
				// se añade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			} // fin if
		} // fin for
		try {
			// se asigna el listmodel al JList,
			// se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		} catch (NullPointerException n) {
			; // Se produce al cambiar de directorio
		}
	}// Fin llenarLista

	private boolean SubirFichero(String archivo, String soloNombre) throws IOException {
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		// directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in)) {
			String s = " " + soloNombre + " => Subido correctamente...";
			campo.setText(s);
			campo2.setText("Se va a actualizar el árbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			// obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			// llenar la lista con los ficheros del directorio actual
			llenarLista(ff2, direcSelec);
			ok = true;
		} else
			campo.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero

	private void DescargarFichero(String NombreCompleto, String nombreFichero) {
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();
		// solo se pueden seleccionar directorios
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// título de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = f.getSelectedFile();
			// obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			// construimos el nombre completo que se creará en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try {
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero

	private void BorrarFichero(String NombreCompleto, String nombreFichero) {
		// pide confirmación
		int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el fichero seleccionado?");
		if (seleccion == JOptionPane.OK_OPTION) {
			try {
				if (cliente.deleteFile(NombreCompleto)) {
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					campo.setText(m);
					// directorio de trabajo actual
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2 = null;
					// obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					// llenar la lista con los ficheros del directorio actual
					llenarLista(ff2, direcSelec);
				} else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}// Final de BorrarFichero
}// Final de la clase ClienteFTPBasico