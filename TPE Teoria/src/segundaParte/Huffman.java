package segundaParte;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import primeraParte.Bloque;

public class Huffman {
	
	private Vector<Nodo> nodos = new Vector<Nodo>();
	private Nodo arbol;
	private static int bufferLength = 8;
	private char[] bits;
	private char[] dBits;
	private int longCodDec;
	
	class Nodo implements Comparable<Nodo>{
		public int gris = -1;
		public double prob;
		public Nodo cero =null;
		public Nodo uno = null;
		
		public Nodo() {
			this.gris = 0;
			this.prob = 0;
		}
		
		public Nodo(Nodo n) {
			this.gris = n.gris;
			this.prob = n.prob;
			this.cero = n.cero;
			this. uno = n.uno;
		}
		
		public Nodo(int g, double p) {
			this.gris = g;
			this.prob = p;
		}
		
		public 	Nodo getcero() {
			return cero;
		}
		
		public 	Nodo getuno() {
			return uno;
		}
		
		public int getgris() {
			return this.gris;
		}
		
		public double getprob() {
			return this.prob;
		}
		
		public int compareTo(Nodo n) {
			if (prob == n.prob) {
				return 0;
			}else if(prob < n.prob){
				return 1;
			}else {
				return -1;
			}
		}
		
		public void addnodos(Nodo ncero, Nodo nuno) {
			this.cero= ncero;
			this.uno=nuno;		
		}		
	}
	
	
	public Huffman(double[] v) {
		for (int i = 0; i < v.length; i++) {
			if (v[i] > 0) {
				Nodo n = new Nodo(i, v[i]);
				nodos.add(n);
			}
		}
		Collections.sort(nodos);
	}
	
	public Huffman(char[] codigo) {
		dBits = codigo;
	}
	
	public void codificarArbol() {
		Nodo combinacion = new Nodo(-1,nodos.elementAt(nodos.size()-1).getprob()+nodos.elementAt(nodos.size()-2).getprob());
		for(int i=nodos.size();i>1;i--){
			combinacion.addnodos(nodos.elementAt(nodos.size()-1),nodos.elementAt(nodos.size()-2));
			nodos.removeElementAt(nodos.size()-1);
			nodos.removeElementAt(nodos.size()-1);
			nodos.add(new Nodo(combinacion));
			Collections.sort(nodos);
			if(nodos.size()>1)
				combinacion = new Nodo(-1,nodos.elementAt(nodos.size()-1).getprob()+nodos.elementAt(nodos.size()-2).getprob());
		}
		arbol = combinacion;
	}
	
	public  List<Character> generacodigogris(Nodo raizactual,String codigo, int gris) {
		List<Character> retorno = new ArrayList<Character>();
		if (raizactual.getgris()==-1) {
			retorno = generacodigogris(raizactual.getuno(),codigo +"1",gris);
			if (retorno.size() == 0) {
				retorno = generacodigogris(raizactual.getcero(),codigo +"0",gris);
			}
		} else { 
			if (raizactual.getgris()>-1) {
				if (raizactual.getgris()==gris) {
					char[] codArr = codigo.toCharArray();
					for(int i = 0; i < codArr.length; i++) {
						retorno.add(codArr[i]);
					}
				}
			}
		}	
		return retorno;
	}

	private List<Character> generarCodificacionBloque(Bloque b, Color[][] imagen) {
		List<Character> codigo = new ArrayList<Character>();
		for (int fila = b.getPosY(); fila < b.getAlto(); fila++) {
			for (int col = b.getPosX(); col < b.getAncho(); col++) {
				codigo.addAll(generacodigogris(arbol,"", imagen[fila][col].getRed()));
			}
		}
		return codigo;
	}
	
	private void copiarEnteroEnArregloBits(int ini, int fin, char[] aux) {
		for(int i = ini; i < fin; i++) {
			if(i+aux.length < fin) {
				bits[i] = '0';
			}else {
				bits[i] = aux[aux.length+i-fin];
			}
		}
	}
	
	private void seteoDeCabecera(Bloque b, int longCodigo){
		bits[0] = '1';
		char[] aux = Integer.toBinaryString(b.getAncho()).toCharArray();
		copiarEnteroEnArregloBits(1,33,aux);
		aux = Integer.toBinaryString(b.getAlto()).toCharArray();
		copiarEnteroEnArregloBits(33,65,aux);
		aux = Integer.toBinaryString(b.getPosX()).toCharArray();
		copiarEnteroEnArregloBits(65,97,aux);
		aux = Integer.toBinaryString(b.getPosY()).toCharArray();
		copiarEnteroEnArregloBits(97,129,aux);
		aux = Integer.toBinaryString(longCodigo).toCharArray();
		copiarEnteroEnArregloBits(129,161,aux);
		double[] ve = b.getVEstacionario();
		for(int i = 0; i < 256; i++) {
			aux = Long.toBinaryString(Double.doubleToLongBits(ve[i])).toCharArray();
			copiarEnteroEnArregloBits(161+64*i,225+64*i,aux);
		}
	}
	
	public void generarArchivoComprimido(Bloque b, Color[][] imagen) {
		List<Character> codigo = generarCodificacionBloque(b,imagen);
		int longCodigo = codigo.size();
		bits = new char[16545 + longCodigo];//bit que indica que es Huffman + ancho + alto + posx + posy + longcodigo + vectorestacionario + codigo
		seteoDeCabecera(b,longCodigo);
		for(int i = 16545; i < bits.length;i++) {
			bits[i] = codigo.get(i-16545);
		}
		List<Byte> codEnBytes = encodeSequence();
		byte[] codbytes = ConvertByteListToPrimitives(codEnBytes);
		try {
			String directorioarchivo= System.getProperty("user.dir")+ File.separator + "Bloque "+b.getidbloque() + ".huff";
			FileOutputStream fos = new FileOutputStream(directorioarchivo);
			fos.write(codbytes);
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] ConvertByteListToPrimitives(List<Byte> input) {
		byte[] ret = new byte[input.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = input.get(i);
		}
		
		return ret;
	}
	
	private List<Byte> encodeSequence() {
		List<Byte> result = new ArrayList<Byte>();
		byte buffer = 0;
		int bufferPos = 0;
		int i = 0;
		while (i < bits.length) {
			// La operación de corrimiento pone un '0'
			buffer = (byte) (buffer << 1);
			bufferPos++;
			if (bits[i] == '1') {
				buffer = (byte) (buffer | 1);
			}
			if (bufferPos == bufferLength) {
				result.add(buffer);
				buffer = 0;
				bufferPos = 0;
			}
			i++;
		}
		if ((bufferPos < bufferLength) && (bufferPos != 0)) {
			buffer = (byte) (buffer << (bufferLength - bufferPos));
			result.add(buffer);
		}
		return result;
	}
	
	private int extraerEnteroDeDBits(int ini, int fin) {
		String codigo = "";
		for(int i = ini; i < fin;i++) {
			codigo += dBits[i];
		}
		int salida = Integer.parseInt(codigo, 2);
		return salida;
	}
	
	private double extraerDoubleDeDBits(int ini, int fin) {
		String codigo = "";
		for(int i = ini; i < fin;i++) {
			codigo += dBits[i];
		}
		double salida = Double.longBitsToDouble(new BigInteger(codigo, 2).longValue());
		return salida;
	}
	
	private void decodificarCabecera(Bloque b){
		int aux = extraerEnteroDeDBits(1,33);
		b.setAncho(aux);
		aux = extraerEnteroDeDBits(33,65);
		b.setAlto(aux);
		aux = extraerEnteroDeDBits(65,97);
		b.setPosX(aux);
		aux = extraerEnteroDeDBits(97,129);
		b.setPosY(aux);
		aux = extraerEnteroDeDBits(129,161);
		longCodDec = aux;
		double[] ve = new double[256];
		double aux2;
		for(int i = 0; i < 256; i++) {
			aux2 = extraerDoubleDeDBits(161+64*i,225+64*i);
			ve[i] = aux2;
		}
		for (int i = 0; i < ve.length; i++) {
			if (ve[i] > 0) {
				Nodo n = new Nodo(i, ve[i]);
				nodos.add(n);
			}
		}
		Collections.sort(nodos);
		codificarArbol();
	}
	
	private List<Color> decodificarImagen(){
		List<Color> colores = new ArrayList<Color>();
		int cont = 0;
		char aux;
		Nodo puntero = arbol;
		while(cont < longCodDec) {
			aux = dBits[cont+16545];
			if(aux == '0') {
				puntero = puntero.getcero();
			}else {
				puntero = puntero.getuno();
			}
			if(puntero.getuno()==null) {
				int nivel = puntero.getgris();
				Color color = new Color(nivel,nivel,nivel);
				colores.add(color);
				puntero = arbol;
			}
			cont++;
		}
		return colores;
	}
	
	private Color[][] decodificarCodigo(Bloque b){
		Color[][] imagen = new Color[b.getAlto()-b.getPosY()][b.getAncho()-b.getPosX()];
		List<Color> colores = decodificarImagen();
		for(int fila = 0; fila <b.getAlto()-b.getPosY();fila++) {
			for(int col = 0; col < b.getAncho()-b.getPosX();col++) {
				imagen[fila][col] = colores.get(col + fila*(b.getAncho()-b.getPosX()));
			}
		}
		return imagen;
	}
	
	public Color[][] decodificar(Bloque b){
		decodificarCabecera(b);
		Color[][] imagen = decodificarCodigo(b);
		return imagen;
	}
   
}