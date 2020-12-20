package segundaParte;

import primeraParte.Bloque;

public class Encabezado {

	private byte huffman;
	private byte altura;
	private byte anchura;
	private byte posx;
	private byte posy;
	private byte largocodimagen;
	private byte largovector;
	
	public Encabezado(Bloque b,byte[] bytes) {
	
	int h=1;
		huffman =(byte)h ;
	
		altura= (byte)b.getAlto();
	
		anchura= (byte)b.getAncho();
	
		posx= (byte) b.getPosX();
	
		posy= (byte) b.getPosY();
	
		largocodimagen=(byte) bytes.length ;
	
		largovector=(byte) b.getVEstacionario().length;
	
	}
	
}
