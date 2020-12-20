package primeraParte;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import segundaParte.Huffman;
import segundaParte.RunLengthCoding;

public class Bloque implements Comparable<Bloque>{
	private int posX, posY, alto, ancho;
	private float[][] mTransicion = new float[256][256];
	private float entropConMemoria, entropSinMemoria;
	private final static int TOTAL = 256;
	private double[] vEstacionario = new double[TOTAL];
	private Histograma histo;
	private double[] vnivelgrises;
	private double[][] matTransAcum = new double[256][256];
	private float media;
	private final float epsilon = 0.00000005f;
	private float desvioEstandar;
	private int idbloque;
	private String nombreArchivo;
	

	
	public Bloque(int posX, int posY, int alto, int ancho) {
		this.posX = posX;
		this.posY = posY;
		this.alto = alto;
		this.ancho = ancho;
		this.nombreArchivo = "Bloque " + idbloque + ".rlc";
		vnivelgrises = new double[alto*ancho];
	}
	private Huffman huffman;
	private RunLengthCoding rlc;
	
	public Bloque() {
	}
	
	public void setidbloque(int id) {
		this.idbloque=id;
		this.nombreArchivo = "Bloque " + idbloque + ".rlc";
	}
	
	public int getidbloque(){
		return idbloque;
	}
	
	public float getMedia() {
		return media;
	}
	
	public float getDesvioEstandar() {
		return desvioEstandar;
	}
	
	
	
	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int x) {
		posX = x;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public void setPosY(int y) {
		posY = y;
	}

	public int getAncho() {
		return ancho;
	}
	
	public void setAncho(int ancho) {
		this.ancho = ancho;
	}
	
	public int getAlto() {
		return alto;
	}
	
	public void setAlto(int alto) {
		this.alto = alto;
	}
	
	public float[][] getMatTransicion(){
		    return mTransicion;
	}
	
	public float getEntropConMemoria() {
		return entropConMemoria;
	}
	
	public float getEntropSinMemoria() {
		return entropSinMemoria;
	}
	
	public double[] getVEstacionario() {
		return Arrays.copyOf(vEstacionario, vEstacionario.length);
	}
	
	public void gethistograma(String nombre,String cualentropia) {
		this.histo = new Histograma("Histograma",vnivelgrises, cualentropia);
		histo.gethistograma(nombre);
		
	}
	
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	
	public void crearMTransicion(Color[][] imagen){
		Color ant = null;
		Color act = null;
		for(int j = posY; j < alto; j++) {
			for(int i = posX; i < ancho; i++) {
				act = imagen[j][i];
				int fila = act.getRed();
				if (ant != null) {
					int col = ant.getRed();
					mTransicion[fila][col] += 1;
				}
				ant = act;
			}
		}
		float total;
		for(int col = 0; col < 256; col++) {
			total = 0;
			for(int fila = 0; fila < 256; fila++) {
				total += mTransicion[fila][col];
			}
			if(total>0) {
				for(int fila = 0; fila < 256; fila++) {
					mTransicion[fila][col] = mTransicion[fila][col]/total;
				}
			}
		}
		generarMatAcum();
	}
	
	
	public void crearVEstacionario(Color[][] imagen) { //Recorre 250000
		double[] v = new double[TOTAL];
		int o=0;
		
		for(int fila = posY; fila < alto; fila++) {
			for(int col = posX; col < ancho; col++) {
				Color act = imagen[fila][col];
				int g = act.getRed();
				v[g]++;
				vnivelgrises[o]= g;
				o++;
			}
			
		}

		for(int k = 0 ; k < TOTAL; k++)
			v[k] = (double)v[k]/(double)((ancho-posX)*(alto-posY));
		
		this.vEstacionario = v;
	}
	
	public void setEntropConMemoria() {
		float entropia = 0;
		for(int col = 0; col < 256; col++) {
			float entrCol = 0;
			for(int fila = 0; fila < 256; fila++){
				float valor = mTransicion[fila][col];
				if(valor > 0)
					entrCol -= valor * Math.log(valor)/Math.log(2);
			}
			entropia += entrCol * vEstacionario[col];
		}
		entropConMemoria = entropia;
	}
	
	public void setEntropSinMemoria() {
		float entropia = 0;
		for(int i = 0; i < vEstacionario.length; i++) {
			if(vEstacionario[i] > 0)
				entropia -= vEstacionario[i] * Math.log(vEstacionario[i])/Math.log(2);
		}
		entropSinMemoria = entropia;
	}
	
	private int primerSimbolo() {
		float[] vEstacAcum = new float[256];
		float cont = 0f;
		for(int i = 0; i < vEstacAcum.length; i++) {
			cont += vEstacionario[i];
			vEstacAcum[i] = cont;
		}
		float rand = (float) Math.random();
		for(int i = 0; i < vEstacAcum.length; i++) {
			if(rand<vEstacAcum[i])
				return i;
		}
		return 255;
	}
	
	private void generarMatAcum() {
		float suma;
		for(int col = 0; col < 256; col++) {
			suma = 0;
			for(int fila = 0; fila < 256; fila++) {
				suma += mTransicion[fila][col];
				matTransAcum[fila][col] = suma;
			}
		}
	}
	
	private int sigSimbolo(int simbolo) {
		float rand = (float)Math.random();
		for(int fila = 0; fila < matTransAcum.length;fila++) {
			if(rand < matTransAcum[fila][simbolo]) {
				return fila;
			}
		}
		return 0;
	}
	
	private boolean converge(float dato1, float dato2) {
		return Math.abs(dato1-dato2) < epsilon;
	}
	
	public void setMedia() {
		float promAnt = -1f;
		float promAct = 0f;
		int tiradas = 0, suma = 0, simbolo = primerSimbolo();
		while((!converge(promAnt,promAct))||(tiradas < 100)) {
			suma += simbolo;
			tiradas++;
			promAnt = promAct;
			promAct = (float)suma/(float)tiradas;
			simbolo = sigSimbolo(simbolo);
		}
		this.media = promAct;
	}
	
	public void setMedia(Color[][] imagen) {
		int cont = 0;
		for(int fila = posY; fila < alto; fila++) {
			for(int col = posX; col < ancho; col++) {
				cont += imagen[fila][col].getRed();
			}
		}
		this.media = (float)cont/(float)((alto-posY)*(ancho-posX));
	}
	
	public void setDesvioEstandar(Color[][] imagen) {
		double cont = 0; double cont2 = Math.pow(this.media,2);
		for(int fila = posY; fila < alto; fila++) {
			for(int col = posX; col < ancho; col++) {
				cont += Math.pow(imagen[fila][col].getRed(),2);
			}
		}
		double cont1 = cont/(double)((alto-posY)*(ancho-posX));
		this.desvioEstandar = (float) Math.pow((cont1 - cont2),0.5);
	}
	
	public void setDesvioEstandar() {
		//[(Sumatoria (xi - media)^2 ) / N ]^1/2
		float varAnt = -1f;
		float varAct = 0f;
		int tiradas = 0, simbolo = primerSimbolo();
		float suma = 0, termino;
		while ((!converge(varAnt, varAct)) || (tiradas < 100)) {
			termino = simbolo;
			termino -= media;
			termino = (float) Math.pow(termino,2);
			suma += termino;
			tiradas++;
			varAnt = varAct;
			varAct = suma/(float)tiradas;
			simbolo = sigSimbolo(simbolo);
		}
		this.desvioEstandar = (float) Math.pow(varAct,0.5);
	}
	
	public int compareTo(Bloque b) {
		if(entropConMemoria > b.getEntropConMemoria()) {
			return 1;
		}else if (entropConMemoria == b.getEntropConMemoria())
			return 0;
		else
			return -1;
	}

	public void impvector() {
		for (int k = 0; k < vEstacionario.length; k++)
			System.out.println(vEstacionario[k]);
	}
	
	public void impmatriz() {
		for(int l=0; l< 256; l++)
		for (int k = 0; k < 256; k++)
			System.out.println(mTransicion[l][k]);
	}
	
	public void runLength(Color[][] imagen) {
		rlc = new RunLengthCoding(this);
		rlc.codificar(imagen);
	}
	
	public void huffman(Color[][] imagen) {
		huffman = new Huffman(vEstacionario);
		huffman.codificarArbol();
		huffman.generarArchivoComprimido(this,imagen);
	}
	
	private static char[] fileToCharArray(File archivo) {
		int bufferLength = 8;
		char[] restoredSequence = new char[(int)(archivo.length()*8)];
		
		try {
			byte[] inputSequence = Files.readAllBytes(new File(archivo.getName()).toPath());			
			int globalIndex = 0;			
			byte mask = (byte) (1 << (bufferLength - 1)); // mask: 10000000
			int bufferPos = 0;
			int i = 0;
			long longitud = archivo.length()*8;
			while (globalIndex < longitud) 
			{
				byte buffer = inputSequence[i];			
				while (bufferPos < bufferLength) {
					
					if ((buffer & mask) == mask) {
						restoredSequence[globalIndex] = '1';
					} else {
						restoredSequence[globalIndex] = '0';
					}
					
					buffer = (byte) (buffer << 1);
					bufferPos++;
					globalIndex++;
					
					if (globalIndex == longitud) {
						break;
					}
				}
				i++;
				bufferPos = 0;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return restoredSequence;
	}
	
	
	
	public Color[][] decodificar(File archivo){
		Color[][] imagen;
		char[] dBits = fileToCharArray(archivo);
		if(dBits[0] == '0') {
			rlc = new RunLengthCoding(this);
			imagen = rlc.decodificar(dBits);
		}else {
			Huffman huffman = new Huffman(dBits);
			imagen = huffman.decodificar(this);
		}
		return imagen;
	}
	
}