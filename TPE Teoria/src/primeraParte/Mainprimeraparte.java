package primeraParte;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class Mainprimeraparte {
	
	private static Vector<Bloque> bloques = new Vector<Bloque>();
	private static int ancho = 500;
	private static int alto = 500;
	private static int contador=1;
	private static String nombreimagen="";
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
	    	nombreimagen=imagen.getName();
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
		for(int x = 0; x < imageninterna.getWidth(); x++) {
			for(int y = 0; y < imageninterna.getHeight(); y++) {
				Color color= new Color(imageninterna.getRGB(x, y), true);
				imagen[y][x] = color;
			}
		}
		return imagen;
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
	
	public static float entropromedio(Vector<Bloque> bloques) {
		float promedio=0;
		
			for(int i=0;i<bloques.size();i++) {
				promedio= promedio+ bloques.elementAt(i).getEntropConMemoria();
			}
			return (promedio/bloques.size());

	}


	public static int indiceBloquemascercanoalpromedio(Vector<Bloque> bloques) {
		float difvieja=0;
		float difnueva=0;
		float Hprom=0;
		int indicebloque=0;
		
				Hprom= entropromedio(bloques);
				difvieja= Math.abs(bloques.elementAt(0).getEntropConMemoria()-Hprom);
			
				
			for(int i=1;i<bloques.size();i++) {
				difnueva= Math.abs(bloques.elementAt(i).getEntropConMemoria()-Hprom);
				if (difnueva<difvieja) {
					difvieja=difnueva;
					indicebloque=i;
				}
			}
		
		return indicebloque;
	}
	
public static void puntouno() {
		
	

			bloques.clear();
			String titulo="Seleccione una imagen";
			BufferedImage imageninterna = subirImagen(titulo);
			Color[][] imagen = getMatrizPixeles(imageninterna);
			setBloques(imagen);
		
			Collections.sort(bloques);
		
			//punto a ,c y d
			
			String menor=" (con menor entropia)";
			String media=" (con entropia mas cercana al promedio)";
			String mayor=" (con mayor entropia)";
			String entropias="Punto a - Entropias";
			String mediaydesvio="Punto d - Media y Desvio";
			
			int indicemenorentropia=0;
			int indicemayorentropia=bloques.size()-1;
			int indicecercanoapromedio=indiceBloquemascercanoalpromedio(bloques);
	
			String matriztransicionmenor="Punto c - Matriz transicion bloque "+bloques.elementAt(indicemenorentropia).getidbloque() +menor;
			
			String matriztransicionmayor= "Punto c - Matriz transicion bloque "+bloques.elementAt(indicemayorentropia).getidbloque() +mayor;
			
			String matriztransicionpromedio= "Punto c - Matriz transicion bloque "+bloques.elementAt(indicecercanoapromedio).getidbloque() +media;
			
	
			Archivador savemenor= new Archivador(entropias,matriztransicionmenor,mediaydesvio);
				savemenor.guardadatos(bloques,bloques.elementAt(indicemenorentropia).getMatTransicion(),indicemenorentropia,indicemayorentropia);
			
			
			Archivador savemayor= new Archivador(entropias,matriztransicionmayor,mediaydesvio);
			savemayor.guardadatos(bloques,bloques.elementAt(indicemayorentropia).getMatTransicion(),indicemenorentropia,indicemayorentropia);
			
	
			Archivador savepromedio= new Archivador(entropias,matriztransicionpromedio,mediaydesvio);
			savepromedio.guardadatos(bloques,bloques.elementAt(indicecercanoapromedio).getMatTransicion(),indicemenorentropia,indicemayorentropia);
				
				
			// Punto b
			String nombrearchivo1="Punto b - Histograma del bloque " + bloques.elementAt(indicemenorentropia).getidbloque() + menor+ ".PNG";
			bloques.elementAt(indicemenorentropia).gethistograma(nombrearchivo1,menor);
			
			String nombrearchivo2="Punto b - Histograma del bloque " + bloques.elementAt(indicemenorentropia).getidbloque() + mayor+ ".PNG";
			bloques.elementAt(indicemayorentropia).gethistograma(nombrearchivo2,mayor);
		
			String nombrearchivo3="Punto b - Histograma del bloque " + bloques.elementAt(indicemenorentropia).getidbloque() + media+ ".PNG";
			bloques.elementAt(indicecercanoapromedio).gethistograma(nombrearchivo3,media);
		
}
	
public static void setBloquesimagenentera(Color[][] imagen) {
		
			int i=0;
			int j=0;
			Bloque b = new Bloque(i,j,imagen.length,imagen[0].length);
			b.crearVEstacionario(imagen);
			b.crearMTransicion(imagen);
			b.setEntropConMemoria();
			b.setEntropSinMemoria();
			b.setMedia();
			b.setDesvioEstandar();
			bloques.add(b);
			bloques.elementAt(bloques.size()-1).setidbloque(bloques.size()-1);;

}


	public static void histogramaMediaDesvio() {
	
			bloques.clear();
			String titulo="Seleccione imagen numero "+contador;
			BufferedImage imageninterna = subirImagen(titulo);
			Color[][] imagen = getMatrizPixeles(imageninterna);
			setBloquesimagenentera(imagen);
		
	
			// histograma
			String nombrearchivo="imagen "+nombreimagen;
			bloques.elementAt(0).gethistograma(nombrearchivo,nombrearchivo);
			
			String nombretxt="Media y desvio imagen "+nombreimagen ;
			Archivador save= new Archivador(nombretxt);
			save.guardadatos(bloques,contador);
			
			contador++;

}


	public static void main(String[] args) {
	histogramaMediaDesvio();
	}
}

