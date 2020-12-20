package segundaParte;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import primeraParte.Bloque;

public final class Main {
	private static Vector<Bloque> bloques = new Vector<Bloque>();
	private static int ancho = 500;
	private static int alto = 500;
	private static File[] archivos;

	
	public static BufferedImage subirImagen(String titulo) {
		BufferedImage imageninterna = null;
	    JFileChooser seleccionadorarchivo = new JFileChooser();
	   
	    seleccionadorarchivo.setDialogTitle(titulo);
	    seleccionadorarchivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    FileNameExtensionFilter filtradorformato = new FileNameExtensionFilter("Imagenes BMP", "bmp"); 
	    seleccionadorarchivo.setFileFilter(filtradorformato);
	    int resultado = seleccionadorarchivo.showOpenDialog(null);
	    if (resultado != JFileChooser.CANCEL_OPTION) {
	    	File imagen=seleccionadorarchivo.getSelectedFile();
	    	try{
	    		imageninterna = ImageIO.read(imagen);
	  
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
	    }
		return imageninterna;
    }
	
	public static Color[][] getMatrizPixeles(BufferedImage imageninterna) {
		Color[][] imagen = new Color[imageninterna.getHeight()][imageninterna.getWidth()];
		for(int fila = 0; fila < imageninterna.getHeight(); fila++) {
			for(int col = 0; col < imageninterna.getWidth(); col++) {
				Color color= new Color(imageninterna.getRGB(col, fila), true);
				imagen[fila][col] = color;
			}
		}
		
		return imagen;
	}
	
	 public static  File[] subirarchivos(String titulo) {
         JFileChooser abrirarch = new JFileChooser();
         abrirarch.setDialogTitle(titulo);
         abrirarch.setMultiSelectionEnabled(true);
       //  abrirarch.showOpenDialog(null);
         abrirarch.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
         FileNameExtensionFilter filtradorformato = new FileNameExtensionFilter("Archivos huff o rlc", "huff","rlc"); 
         abrirarch.setFileFilter(filtradorformato); 
 	    int resultado = abrirarch.showOpenDialog(null);
 	    File[] files = abrirarch.getSelectedFiles();
 	    return files; 	
	 }
	 
	//recorre las filas de cada columna
	public static void setBloques(Color[][] imagen) {
		int j = 0, i;
		while(j < imagen.length) {
			i = 0;
			while(i < imagen[0].length) {
				int x = i + ancho;
				int y = j + alto;
				
				Bloque b = new Bloque(i,j,y,x);
				
				b.crearVEstacionario(imagen);
				b.crearMTransicion(imagen);
				b.setEntropConMemoria();
				b.setEntropSinMemoria();
				b.setMedia();
				b.setDesvioEstandar();
				bloques.add(b);
				bloques.elementAt(bloques.size()-1).setidbloque(bloques.size()-1);;
				i+= ancho;
			}
			j += alto;
		}
	}
		
	public static void codificar() {
		bloques.clear();
		String titulo="Seleccione imagen a codificar";
		BufferedImage imageninterna = subirImagen(titulo);
		Color[][] imagen = getMatrizPixeles(imageninterna);
		setBloques(imagen);
		Collections.sort(bloques);
		for(int i = 0; i < bloques.size(); i++) {
			if(bloques.get(i).getEntropConMemoria() < 3.60f) {
				bloques.get(i).runLength(imagen);
			}else {
				bloques.get(i).huffman(imagen);
			}
		}
	}
	
	public static void decodificar() {
		bloques.clear();
		String titulo="Seleccione todos los bloques de la imagen";
		List<Color[][]> subImg = new ArrayList<Color[][]>();
		
		archivos = subirarchivos(titulo);
		int anchoTotal = 0, altoTotal = 0;
		for(int i= 0; i < archivos.length; i++) {
			Bloque b = new Bloque();
			b.setidbloque(i);
			bloques.add(b);
			Color[][] imgMat = b.decodificar(archivos[i]);
			subImg.add(imgMat);
			if(b.getAncho() > anchoTotal) {
				anchoTotal = b.getAncho();
			}
			if(b.getAlto() > altoTotal) {
				altoTotal = b.getAlto();
			}
		}
		//Concatena las imagenes aca
		Color[][] imgCompleta = new Color[altoTotal][anchoTotal];
		for(int i = 0; i < bloques.size();i++) {
			Bloque b = bloques.get(i);
			for(int fila = b.getPosY();fila < b.getAlto(); fila++) {
				for(int col = b.getPosX();col < b.getAncho(); col++) {
					imgCompleta[fila][col] = subImg.get(i)[fila-b.getPosY()][col-b.getPosX()];
				}
			}
		}
		try {
			BufferedImage imagen = new BufferedImage(imgCompleta[0].length,imgCompleta.length,BufferedImage.TYPE_BYTE_GRAY);
			String directorioarchivo= System.getProperty("user.dir")+ File.separator + "Imagen rearmada.bmp";
			File initialImage = new File(directorioarchivo);
			for(int y = 0; y < imgCompleta.length; y++){
				for(int x = 0; x < imgCompleta[0].length; x++){
					imagen.setRGB(x,y,imgCompleta[y][x].getRGB());
				}
			}
		    ImageIO.write(imagen, "bmp", initialImage);
		}catch(IOException e) {
			System.out.println("Exception occured :" + e.getMessage());
		}
	}
		
	public static void ruido() {
		
		String titulo1="Seleccione imagen fuente";
		String titulo2="Seleccione imagen producto del canal";
		Ruidoyperdida r = new Ruidoyperdida();
		BufferedImage imagenuno = subirImagen(titulo1);
		Color[][] imagenfuente = getMatrizPixeles(imagenuno);
		
		BufferedImage imagendos=subirImagen(titulo2);
		Color[][] imagentransmitida = getMatrizPixeles(imagendos);
		
		double ruido=r.calcularruido(imagenfuente, imagentransmitida);
		
		JFrame frame =new JFrame("Aviso");
		String title="Ruido";
		JOptionPane.showMessageDialog(frame, "El ruido producido por el canal es de "+ruido,title, JOptionPane.INFORMATION_MESSAGE);	
	}
	
	public static void perdida() {
		String titulo1="Seleccione imagen fuente";
		String titulo2="Seleccione imagen producto del canal";
		Ruidoyperdida r = new Ruidoyperdida();
		BufferedImage imagenuno = subirImagen(titulo1);
		Color[][] imagenfuente = getMatrizPixeles(imagenuno);
		
		
		BufferedImage imagendos=subirImagen(titulo2);
		Color[][] imagentransmitida = getMatrizPixeles(imagendos);
		
		double perdida=r.calcularperdida(imagenfuente, imagentransmitida);
		
		JFrame frame =new JFrame("Aviso");
		String title="Perdida";
		JOptionPane.showMessageDialog(frame, "La perdida producida por el canal es de "+perdida,title, JOptionPane.INFORMATION_MESSAGE);	
	}
	public static void main(String[] args) {

	}
}
