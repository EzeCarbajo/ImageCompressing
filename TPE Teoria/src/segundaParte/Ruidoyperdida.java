package segundaParte;

import java.awt.Color;

public class Ruidoyperdida {
	private double[][] mprob;	
	private final static int totalgrises = 256;
	double[] vectorimagenfuente;
	
	public Ruidoyperdida(){
		mprob=new double[totalgrises][totalgrises];
		vectorimagenfuente= new double[256];
	}
	
	public double calcularruido(Color[][] imagenfuente,Color[][] imagentransmitida) {
		this.setmatrizprob(imagenfuente, imagentransmitida);
		this.vestacionario(imagenfuente);
		double ruido=this.entropiacond();
		return ruido;
	}
	
	
	public void setmatrizprob(Color[][] imagenfuente,Color[][] imagentransmitida) {
			
			for (int y=0; y < imagenfuente.length;y++) {
				for (int x=0;x < imagenfuente[0].length;x++) {
						mprob[imagentransmitida[y][x].getRed()][imagenfuente[y][x].getRed()]+=1;
				}
			}
			double total;
			for (int col=0;col<totalgrises;col++) {//normalizo por columnas
					total=0;
					for(int fila = 0; fila < totalgrises; fila++) {
						total += mprob[fila][col];
						}//for fila
					if(total>0) {
						for(int fila = 0; fila < totalgrises; fila++) {
							mprob[fila][col] = mprob[fila][col]/total;
						}//for
					}//if
			}//for col
		}
	
	public void vestacionario(Color[][] imagen) { //Recorre 2500*2000
		double[] vector = new double[totalgrises];
		for(int fila = 0; fila < imagen.length; fila++) {
			for(int col = 0; col < imagen[0].length; col++) {
				Color act = imagen[fila][col];
				int g = act.getRed();
				vector[g]+= 1.0;
			}
		}
		double cantPix = (double)(imagen.length*imagen[0].length);
		for(int k = 0 ; k < totalgrises; k++)
			vector[k] = vector[k]/cantPix;
		this.vectorimagenfuente = vector;
	}
	
	public double entropiacond() {
		double entropia = 0;
		for(int col = 0; col < totalgrises; col++) {
			double entrCol = 0;
			for(int fila = 0; fila < totalgrises; fila++){
				double valor = mprob[fila][col];
				if(valor > 0)
					entrCol -= valor * Math.log(valor)/Math.log(2);
			}
			entropia += entrCol * vectorimagenfuente[col];
		}
		return entropia;
	}

	public double calcularperdida(Color[][] imagenfuente,Color[][] imagentransmitida) {
		
		this.setmatrizprob(imagentransmitida,imagenfuente);
		this.vestacionario(imagentransmitida);
		double perdida=entropiacond();
		return perdida;
		
	}
}//fin de clase