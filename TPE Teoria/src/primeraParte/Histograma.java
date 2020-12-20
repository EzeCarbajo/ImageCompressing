package primeraParte;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;



@SuppressWarnings("serial")
public class Histograma extends ApplicationFrame {

private JFreeChart histoaux;


	// agregamos el set de datos que vamos a utilizar y seteamos las escalas
    private IntervalXYDataset createDataset(double[] vnivelgrises) {
    	
        HistogramDataset setdatos = new HistogramDataset();
        setdatos.setType(HistogramType.RELATIVE_FREQUENCY);

        setdatos.addSeries("Probabilidades de nivel de gris", vnivelgrises, vnivelgrises.length, 0, 255); 
        return setdatos;     
    }
        
     // configuracion del grafico usando el set de datos y algunas opciones
    private JFreeChart createChart(IntervalXYDataset setdatos,String cualentropia) {
        JFreeChart grafico = ChartFactory.createHistogram(
            "Histograma "+cualentropia, 
            "Nivel de gris", 
            "Probabilidad", 
            setdatos, 
            PlotOrientation.VERTICAL, 
            true, 
            false, 
            false
        );
        XYPlot trama = (XYPlot) grafico.getPlot();
        trama.setForegroundAlpha(0.75f);
        NumberAxis axis = (NumberAxis) trama.getDomainAxis();
        axis.setAutoRangeIncludesZero(false);
        NumberAxis rangeAxis = (NumberAxis) trama.getRangeAxis();
        rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());
        return grafico;
    }
  
    //constructor de clase y set de la variable histoaux, que despues utilizamos para guardar el grafico como una imagen formato PNG
    public Histograma(String title,double[] vnivelgrises, String cualentropia) {
        super(title);    
        IntervalXYDataset setdatos = createDataset(vnivelgrises);
        JFreeChart grafico = createChart(setdatos,cualentropia);
        histoaux=grafico;
    }
    
    
    public void gethistograma(String nombre){
        
 	   
        try {
			ChartUtilities.saveChartAsPNG(new File(nombre+ ".PNG"), histoaux, 1280, 720);
		} catch (IOException e) {
			e.printStackTrace();
		}
 
    }

	

}
