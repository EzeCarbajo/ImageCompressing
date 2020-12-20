package primeraParte;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import java.io.BufferedWriter;

public class Archivador {
	
 private File entropiaendisco;
 private File matrizendisco;
 private File mydendisco;
 
 	public Archivador(String nombretxt) {
 		
 		try {
		String directoriomyd= System.getProperty("user.dir")+ File.separator + nombretxt + ".txt";
		
		this.mydendisco =new File (directoriomyd);
		if (!this.mydendisco.exists()) {
			this.mydendisco.createNewFile();
		}
		else {
			mydendisco.delete();
			this.mydendisco.createNewFile();}
	}
	catch (Exception e) {}
 	}
 
 	public void guardadatos(Vector<Bloque> bloques,int imagen) {
 		
	 	try {	
		FileWriter desvioymedia= new FileWriter(this.mydendisco,true);
		BufferedWriter escribemyd = new BufferedWriter(desvioymedia);
		
		
		escribemyd.write("Media de imagen "+ " = " +bloques.elementAt(0).getMedia());
		escribemyd.newLine();
		escribemyd.write("Desvio de imagen "+ " = " +bloques.elementAt(0).getDesvioEstandar());
	
		escribemyd.close();		
	 	}
		catch(Exception e) {}	
 	}
 	
	public Archivador(String nombreentropia,String nombrematriz,String myd) {
		try {
				String directorioentropia= System.getProperty("user.dir")+ File.separator + nombreentropia + ".txt";
					this.entropiaendisco =new File (directorioentropia);
					if (!this.entropiaendisco.exists()) {
						this.entropiaendisco.createNewFile();
					}
					else { 
						entropiaendisco.delete();
						this.entropiaendisco.createNewFile();
					}

					String directoriomatriz= System.getProperty("user.dir")+ File.separator + nombrematriz + ".txt";
					this.matrizendisco =new File (directoriomatriz);
					if (!this.matrizendisco.exists()) {
						this.matrizendisco.createNewFile();
					}
					else {
						matrizendisco.delete();
						this.matrizendisco.createNewFile();}

					String directoriomyd= System.getProperty("user.dir")+ File.separator + myd + ".txt";
					this.mydendisco =new File (directoriomyd);
					if (!this.mydendisco.exists()) {
						this.mydendisco.createNewFile();
					}
					else {
						mydendisco.delete();
						this.mydendisco.createNewFile();}
		
		}
		catch (Exception e) {}
	}
	
	
	
	public void guardadatos(Vector<Bloque> bloques, float[][] mTransicion, int menor, int mayor) {
		try {
				FileWriter fwe= new FileWriter(this.entropiaendisco,true);
				BufferedWriter wrentropia = new BufferedWriter(fwe);
						for (int i=0;i< bloques.size();i++) {
							wrentropia.write("Entropia con memoria del Bloque "+ bloques.elementAt(i).getidbloque()+ " = " +bloques.elementAt(i).getEntropConMemoria());
							wrentropia.newLine();
							wrentropia.write("Entropia sin memoria del Bloque "+ bloques.elementAt(i).getidbloque()+ " = " +bloques.elementAt(i).getEntropSinMemoria());
							wrentropia.newLine();
							wrentropia.newLine();
						}
						wrentropia.close();
				
				FileWriter fwmatriz= new FileWriter(this.matrizendisco,true);
				BufferedWriter escribe = new BufferedWriter(fwmatriz);
						for (int i=0;i< 256;i++) {
							for (int j=0;j< 256;j++) {
								escribe.write(mTransicion[i][j]+" ,");			
							}
							escribe.newLine();
						}
						escribe.close();		
		
				FileWriter desvioymedia= new FileWriter(this.mydendisco,true);
				BufferedWriter escribemyd = new BufferedWriter(desvioymedia);

				escribemyd.write("Media del Bloque "+ bloques.elementAt(menor).getidbloque()+ " = " +bloques.elementAt(menor).getMedia());
				escribemyd.newLine();
				escribemyd.write("Desvio del Bloque "+ bloques.elementAt(menor).getidbloque()+ " = " +bloques.elementAt(menor).getDesvioEstandar());
				
				escribemyd.newLine();
				escribemyd.newLine();
				
				escribemyd.write("Media del Bloque "+ bloques.elementAt(mayor).getidbloque()+ " = " +bloques.elementAt(mayor).getMedia());
				escribemyd.newLine();
				escribemyd.write("Desvio del Bloque "+ bloques.elementAt(mayor).getidbloque()+ " = " +bloques.elementAt(mayor).getDesvioEstandar());
						
				escribemyd.close();		
			}
		catch(Exception e) {}	
	}
	
}
