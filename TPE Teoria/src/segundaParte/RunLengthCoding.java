package segundaParte;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import primeraParte.Bloque;

public class RunLengthCoding {
	private Bloque b;
	private List<Character> secuencia = new ArrayList<Character>();
	
	public RunLengthCoding(Bloque b) {
		this.b = b;
	}
		
	public void codificar(Color[][] imagen) {
		rlcEncabezado();
		int pixel, pixelAnt = imagen[b.getPosY()][b.getPosX()].getRed(), cont = 0;
		for (int fila = b.getPosY(); fila < b.getAlto(); fila++) {
			for (int col = b.getPosX(); col < b.getAncho(); col++) {
				pixel = imagen[fila][col].getRed();
				if(pixel == pixelAnt) {
					cont++;
				}else {
					Integer contador = cont, pixNuevo = pixelAnt;//esto lo hago por no saber si cambiaran los valores de la lista cuando cambien cont y pixelAnt
					integerASecuencia(contador, 32);
					integerASecuencia(pixNuevo, 8);
					pixelAnt = pixel;
					cont = 1;
				}
			}
		}
		integerASecuencia(cont, 32);
		integerASecuencia(pixelAnt, 8);
		char[] secArray = new char[secuencia.size()];
		for(int i = 0; i < secArray.length; i++) {
			secArray[i] = secuencia.get(i);
		}
		try {
			List<Byte> encodedSequence = encodeRunLengthSequence(secArray);
			byte[] byteArray = ConvertByteListToPrimitives(encodedSequence);
			FileOutputStream archivo = new FileOutputStream(b.getNombreArchivo());
			archivo.write(byteArray);
			archivo.close();
		}catch(IOException e1){
			e1.printStackTrace();
		}
	}
	
	private void rlcEncabezado() {
		secuencia.add('0');
		integerASecuencia(b.getAncho(),32);
		integerASecuencia(b.getAlto(),32);
		integerASecuencia(b.getPosX(),32);
		integerASecuencia(b.getPosY(),32);
	}
	
	private void integerASecuencia(int dato, int largo) {
		char[] data = Integer.toBinaryString(dato).toCharArray();
		int pos = 0;
		while(largo-pos > data.length) {
			secuencia.add('0');
			pos++;
		}
		for(int i = 0; i < data.length;i++) {
			secuencia.add(data[i]);
		}
	}
	
	private List<Byte> encodeRunLengthSequence(char[] secuencia){
		List<Byte> result = new ArrayList<Byte>();

		byte buffer = 0;
		int bufferPos = 0;

		int i = 0;
		while (i < secuencia.length) {
			// La operación de corrimiento pone un '0'
			buffer = (byte) (buffer << 1);
			bufferPos++;
			if (secuencia[i] == '1') {
				buffer = (byte) (buffer | 1);
			}

			if (bufferPos == 8) {
				result.add(buffer);
				buffer = 0;
				bufferPos = 0;
			}

			i++;
		}
		//mi parte
		if(bufferPos != 0) {
			buffer = (byte) (buffer << (8-bufferPos));
			result.add(buffer);
		}
		//fin mi parte
		return result;
	}
	
	private static byte[] ConvertByteListToPrimitives(List<Byte> input) {
		byte[] ret = new byte[input.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = input.get(i);
		}

		return ret;
	}
	
	private int extraerEnteroDeArray(int ini, int fin, char[] dBits) {
		String codigo = "";
		for(int i = ini; i < fin;i++) {
			codigo += dBits[i];
		}
		int salida = Integer.parseInt(codigo, 2);
		return salida;
	}
	
	public Color[][] decodificar(char[] dBits) {
		b.setAncho(extraerEnteroDeArray(1,33,dBits));
		b.setAlto(extraerEnteroDeArray(33,65,dBits));
		b.setPosX(extraerEnteroDeArray(65,97,dBits));
		b.setPosY(extraerEnteroDeArray(97,129,dBits));
		Color[][] imagen = new Color[b.getAlto()-b.getPosY()][b.getAncho()-b.getPosX()];
		int cont = 129,cant,simb;
		List<Color> colores = new ArrayList<Color>();
		while(cont <= dBits.length-40) {
			cant = extraerEnteroDeArray(cont,cont+32,dBits);
			simb = extraerEnteroDeArray(cont+32,cont+40,dBits);
			while(cant > 0) {
				colores.add(new Color(simb,simb,simb));
				cant--;
			}
			cont += 40;
		}
		for(int fila = 0; fila < b.getAlto()-b.getPosY(); fila++) {
			for(int col = 0; col < b.getAncho()-b.getPosX(); col++) {
				imagen[fila][col] = colores.get(col+fila*(b.getAncho()-b.getPosX()));
			}
		}
		return imagen;
	}
}
