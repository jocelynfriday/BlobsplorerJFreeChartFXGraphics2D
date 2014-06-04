package org.jfree.chart.demo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.fx.FXGraphics2D;
import org.jfree.ui.HorizontalAlignment;

/**
 * A demo scatter plot.
 */
public class Test extends Application {//ApplicationFrame {
	private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
	private static HashMap <String, Integer> taxLevelCount = new HashMap<String, Integer>();
	private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
	private static final Stage primaryStage = null;

	public static class ChartCanvas extends Canvas
	{
		JFreeChart chart;

		private FXGraphics2D g2;

		public ChartCanvas(JFreeChart chart) 
		{
			this.chart = chart;
			this.g2 = new FXGraphics2D(this.getGraphicsContext2D());
			// Redraw canvas when size changes. 
			widthProperty().addListener(evt -> draw()); 
			heightProperty().addListener(evt -> draw()); 
		}  

		private void draw() 
		{ 
			double width = getWidth(); 
			double height = getHeight();  
			this.chart.draw(this.g2, new Rectangle2D.Double(0, 0, width, 
					height));
		} 

		@Override 
		public boolean isResizable() 
		{ 
			return true;
		}  

		@Override 
		public double prefWidth(double height) 
		{ 
			return getWidth(); 
		}  

		@Override 
		public double prefHeight(double width)
		{ 
			return getHeight(); 
		} 
	}// end of ChartCanvas

	/**
	 * Creates a chart.
	 *
	 * @param dataset  a dataset.
	 *
	 * @return A chart.
	 */
	private static JFreeChart createChart(XYDataset dataset) 
	{
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"International Coffee Organisation : Coffee Prices",    // title
				null,             // x-axis label
				"US cents/lb",      // y-axis label
				dataset,            // data
				true,               // create legend?
				true,               // generate tooltips?
				false               // generate URLs?
				);

		String fontName = "Palatino";
		chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
		chart.addSubtitle(new TextTitle("Source: http://www.ico.org/historical/2010-19/PDF/HIST-PRICES.pdf", new Font(fontName, Font.PLAIN, 14)));

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(false);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setLabelFont(new Font(fontName, Font.BOLD, 14));
		plot.getDomainAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 12));
		plot.getRangeAxis().setLabelFont(new Font(fontName, Font.BOLD, 14));
		plot.getRangeAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 12));
		chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 14));
		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);
			renderer.setDrawSeriesLineAsPath(true);
			// set the default stroke for all series
			renderer.setAutoPopulateSeriesStroke(false);
			renderer.setBaseStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL), false);
			renderer.setSeriesPaint(0, Color.RED);
			renderer.setSeriesPaint(1, new Color(24, 123, 58));
			renderer.setSeriesPaint(2, new Color(149, 201, 136));
			renderer.setSeriesPaint(3, new Color(1, 62, 29));
			renderer.setSeriesPaint(4, new Color(81, 176, 86));
			renderer.setSeriesPaint(5, new Color(0, 55, 122));
			renderer.setSeriesPaint(6, new Color(0, 92, 165));
		}

		return chart;
	}
	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 *
	 * @return the dataset.
	 */
	private static XYDataset createDataset() {

		TimeSeries s1 = new TimeSeries("Indicator Price");
		s1.add(new Month(1, 2010), 126.80);
		s1.add(new Month(2, 2010), 123.37);
		s1.add(new Month(3, 2010), 125.30);
		s1.add(new Month(4, 2010), 126.89);
		s1.add(new Month(5, 2010), 128.10);
		s1.add(new Month(6, 2010), 142.20);
		s1.add(new Month(7, 2010), 153.41);
		s1.add(new Month(8, 2010), 157.46);
		s1.add(new Month(9, 2010), 163.61);
		s1.add(new Month(10, 2010), 161.56);
		s1.add(new Month(11, 2010), 173.90);
		s1.add(new Month(12, 2010), 184.26);
		s1.add(new Month(1, 2011), 197.35);
		s1.add(new Month(2, 2011), 216.03);
		s1.add(new Month(3, 2011), 224.33);
		s1.add(new Month(4, 2011), 231.24);
		s1.add(new Month(5, 2011), 227.97);
		s1.add(new Month(6, 2011), 215.58);
		s1.add(new Month(7, 2011), 210.36);
		s1.add(new Month(8, 2011), 212.19);
		s1.add(new Month(9, 2011), 213.04);
		s1.add(new Month(10, 2011), 193.90);
		s1.add(new Month(11, 2011), 193.66);
		s1.add(new Month(12, 2011), 189.02);
		s1.add(new Month(1, 2012), 188.90);
		s1.add(new Month(2, 2012), 182.29);
		s1.add(new Month(3, 2012), 167.77);
		s1.add(new Month(4, 2012), 160.46);
		s1.add(new Month(5, 2012), 157.68);
		s1.add(new Month(6, 2012), 145.31);
		s1.add(new Month(7, 2012), 159.07);
		s1.add(new Month(8, 2012), 148.50);
		s1.add(new Month(9, 2012), 151.28);
		s1.add(new Month(10, 2012), 147.12);
		s1.add(new Month(11, 2012), 136.35);
		s1.add(new Month(12, 2012), 131.31);
		s1.add(new Month(1, 2013), 135.38);
		s1.add(new Month(2, 2013), 131.51);
		s1.add(new Month(3, 2013), 131.38);

		TimeSeries s2 = new TimeSeries("Columbian Milds");
		s2.add(new Month(1, 2010), 207.51);
		s2.add(new Month(2, 2010), 204.71);
		s2.add(new Month(3, 2010), 205.71);
		s2.add(new Month(4, 2010), 200.00);
		s2.add(new Month(5, 2010), 200.54);
		s2.add(new Month(6, 2010), 224.49);
		s2.add(new Month(7, 2010), 235.52);
		s2.add(new Month(8, 2010), 243.98);
		s2.add(new Month(9, 2010), 247.77);
		s2.add(new Month(10, 2010), 230.02);
		s2.add(new Month(11, 2010), 244.02);
		s2.add(new Month(12, 2010), 261.97);
		s2.add(new Month(1, 2011), 279.88);
		s2.add(new Month(2, 2011), 296.44);
		s2.add(new Month(3, 2011), 300.68);
		s2.add(new Month(4, 2011), 312.95);
		s2.add(new Month(5, 2011), 302.17);
		s2.add(new Month(6, 2011), 287.95);
		s2.add(new Month(7, 2011), 285.21);
		s2.add(new Month(8, 2011), 286.97);
		s2.add(new Month(9, 2011), 287.54);
		s2.add(new Month(10, 2011), 257.66);
		s2.add(new Month(11, 2011), 256.99);
		s2.add(new Month(12, 2011), 251.60);
		s2.add(new Month(1, 2012), 255.91);
		s2.add(new Month(2, 2012), 244.14);
		s2.add(new Month(3, 2012), 222.84);
		s2.add(new Month(4, 2012), 214.46);
		s2.add(new Month(5, 2012), 207.32);
		s2.add(new Month(6, 2012), 184.67);
		s2.add(new Month(7, 2012), 202.56);
		s2.add(new Month(8, 2012), 187.14);
		s2.add(new Month(9, 2012), 190.10);
		s2.add(new Month(10, 2012), 181.39);
		s2.add(new Month(11, 2012), 170.08);
		s2.add(new Month(12, 2012), 164.40);
		s2.add(new Month(1, 2013), 169.19);
		s2.add(new Month(2, 2013), 161.70);
		s2.add(new Month(3, 2013), 161.53);

		TimeSeries s3 = new TimeSeries("Other Milds");
		s3.add(new Month(1, 2010), 158.90);
		s3.add(new Month(2, 2010), 157.86);
		s3.add(new Month(3, 2010), 164.50);
		s3.add(new Month(4, 2010), 169.55);
		s3.add(new Month(5, 2010), 173.38);
		s3.add(new Month(6, 2010), 190.90);
		s3.add(new Month(7, 2010), 203.21);
		s3.add(new Month(8, 2010), 211.59);
		s3.add(new Month(9, 2010), 222.71);
		s3.add(new Month(10, 2010), 217.64);
		s3.add(new Month(11, 2010), 233.48);
		s3.add(new Month(12, 2010), 248.17);
		s3.add(new Month(1, 2011), 263.77);
		s3.add(new Month(2, 2011), 287.89);
		s3.add(new Month(3, 2011), 292.07);
		s3.add(new Month(4, 2011), 300.12);
		s3.add(new Month(5, 2011), 291.09);
		s3.add(new Month(6, 2011), 274.98);
		s3.add(new Month(7, 2011), 268.02);
		s3.add(new Month(8, 2011), 270.44);
		s3.add(new Month(9, 2011), 274.88);
		s3.add(new Month(10, 2011), 247.82);
		s3.add(new Month(11, 2011), 245.09);
		s3.add(new Month(12, 2011), 236.71);
		s3.add(new Month(1, 2012), 237.21);
		s3.add(new Month(2, 2012), 224.16);
		s3.add(new Month(3, 2012), 201.26);
		s3.add(new Month(4, 2012), 191.45);
		s3.add(new Month(5, 2012), 184.65);
		s3.add(new Month(6, 2012), 168.69);
		s3.add(new Month(7, 2012), 190.45);
		s3.add(new Month(8, 2012), 174.82);
		s3.add(new Month(9, 2012), 178.98);
		s3.add(new Month(10, 2012), 173.32);
		s3.add(new Month(11, 2012), 159.91);
		s3.add(new Month(12, 2012), 152.74);
		s3.add(new Month(1, 2013), 157.29);
		s3.add(new Month(2, 2013), 149.46);
		s3.add(new Month(3, 2013), 149.78);

		TimeSeries s4 = new TimeSeries("Brazilian Naturals");
		s4.add(new Month(1, 2010), 131.67);
		s4.add(new Month(2, 2010), 124.57);
		s4.add(new Month(3, 2010), 126.21);
		s4.add(new Month(4, 2010), 126.07);
		s4.add(new Month(5, 2010), 127.45);
		s4.add(new Month(6, 2010), 143.20);
		s4.add(new Month(7, 2010), 156.87);
		s4.add(new Month(8, 2010), 163.21);
		s4.add(new Month(9, 2010), 175.15);
		s4.add(new Month(10, 2010), 175.38);
		s4.add(new Month(11, 2010), 190.62);
		s4.add(new Month(12, 2010), 204.25);
		s4.add(new Month(1, 2011), 219.77);
		s4.add(new Month(2, 2011), 247.00);
		s4.add(new Month(3, 2011), 260.98);
		s4.add(new Month(4, 2011), 273.40);
		s4.add(new Month(5, 2011), 268.66);
		s4.add(new Month(6, 2011), 250.59);
		s4.add(new Month(7, 2011), 245.69);
		s4.add(new Month(8, 2011), 249.83);
		s4.add(new Month(9, 2011), 255.64);
		s4.add(new Month(10, 2011), 234.28);
		s4.add(new Month(11, 2011), 236.75);
		s4.add(new Month(12, 2011), 228.79);
		s4.add(new Month(1, 2012), 228.21);
		s4.add(new Month(2, 2012), 215.40);
		s4.add(new Month(3, 2012), 192.03);
		s4.add(new Month(4, 2012), 180.90);
		s4.add(new Month(5, 2012), 174.17);
		s4.add(new Month(6, 2012), 156.17);
		s4.add(new Month(7, 2012), 175.98);
		s4.add(new Month(8, 2012), 160.05);
		s4.add(new Month(9, 2012), 166.53);
		s4.add(new Month(10, 2012), 161.20);
		s4.add(new Month(11, 2012), 148.25);
		s4.add(new Month(12, 2012), 140.69);
		s4.add(new Month(1, 2013), 145.17);
		s4.add(new Month(2, 2013), 136.63);
		s4.add(new Month(3, 2013), 133.61);

		TimeSeries s5 = new TimeSeries("Robustas");
		s5.add(new Month(1, 2010), 69.92);
		s5.add(new Month(2, 2010), 67.88);
		s5.add(new Month(3, 2010), 67.25);
		s5.add(new Month(4, 2010), 71.59);
		s5.add(new Month(5, 2010), 70.70);
		s5.add(new Month(6, 2010), 76.92);
		s5.add(new Month(7, 2010), 85.27);
		s5.add(new Month(8, 2010), 82.68);
		s5.add(new Month(9, 2010), 81.28);
		s5.add(new Month(10, 2010), 85.27);
		s5.add(new Month(11, 2010), 92.04);
		s5.add(new Month(12, 2010), 94.09);
		s5.add(new Month(1, 2011), 101.09);
		s5.add(new Month(2, 2011), 109.35);
		s5.add(new Month(3, 2011), 118.13);
		s5.add(new Month(4, 2011), 117.37);
		s5.add(new Month(5, 2011), 121.98);
		s5.add(new Month(6, 2011), 117.95);
		s5.add(new Month(7, 2011), 112.73);
		s5.add(new Month(8, 2011), 112.07);
		s5.add(new Month(9, 2011), 106.06);
		s5.add(new Month(10, 2011), 98.10);
		s5.add(new Month(11, 2011), 97.24);
		s5.add(new Month(12, 2011), 98.41);
		s5.add(new Month(1, 2012), 96.72);
		s5.add(new Month(2, 2012), 101.93);
		s5.add(new Month(3, 2012), 103.57);
		s5.add(new Month(4, 2012), 101.80);
		s5.add(new Month(5, 2012), 106.88);
		s5.add(new Month(6, 2012), 105.70);
		s5.add(new Month(7, 2012), 107.06);
		s5.add(new Month(8, 2012), 106.52);
		s5.add(new Month(9, 2012), 104.95);
		s5.add(new Month(10, 2012), 104.47);
		s5.add(new Month(11, 2012), 97.67);
		s5.add(new Month(12, 2012), 96.59);
		s5.add(new Month(1, 2013), 99.69);
		s5.add(new Month(2, 2013), 104.03);
		s5.add(new Month(3, 2013), 106.26);

		TimeSeries s6 = new TimeSeries("Futures (London)");
		s6.add(new Month(1, 2010), 62.66);
		s6.add(new Month(2, 2010), 60.37);
		s6.add(new Month(3, 2010), 58.64);
		s6.add(new Month(4, 2010), 62.21);
		s6.add(new Month(5, 2010), 62.46);
		s6.add(new Month(6, 2010), 69.72);
		s6.add(new Month(7, 2010), 78.17);
		s6.add(new Month(8, 2010), 78.42);
		s6.add(new Month(9, 2010), 75.87);
		s6.add(new Month(10, 2010), 80.08);
		s6.add(new Month(11, 2010), 86.40);
		s6.add(new Month(12, 2010), 88.70);
		s6.add(new Month(1, 2011), 96.02);
		s6.add(new Month(2, 2011), 104.53);
		s6.add(new Month(3, 2011), 111.36);
		s6.add(new Month(4, 2011), 111.34);
		s6.add(new Month(5, 2011), 116.76);
		s6.add(new Month(6, 2011), 110.51);
		s6.add(new Month(7, 2011), 103.36);
		s6.add(new Month(8, 2011), 102.71);
		s6.add(new Month(9, 2011), 96.10);
		s6.add(new Month(10, 2011), 88.64);
		s6.add(new Month(11, 2011), 85.78);
		s6.add(new Month(12, 2011), 87.65);
		s6.add(new Month(1, 2012), 84.19);
		s6.add(new Month(2, 2012), 88.69);
		s6.add(new Month(3, 2012), 91.37);
		s6.add(new Month(4, 2012), 91.81);
		s6.add(new Month(5, 2012), 96.82);
		s6.add(new Month(6, 2012), 94.75);
		s6.add(new Month(7, 2012), 96.14);
		s6.add(new Month(8, 2012), 96.12);
		s6.add(new Month(9, 2012), 94.65);
		s6.add(new Month(10, 2012), 94.66);
		s6.add(new Month(11, 2012), 87.32);
		s6.add(new Month(12, 2012), 85.94);
		s6.add(new Month(1, 2013), 88.85);
		s6.add(new Month(2, 2013), 94.41);
		s6.add(new Month(3, 2013), 97.22);

		TimeSeries s7 = new TimeSeries("Futures (New York)");
		s7.add(new Month(1, 2010), 142.76);
		s7.add(new Month(2, 2010), 134.35);
		s7.add(new Month(3, 2010), 134.97);
		s7.add(new Month(4, 2010), 135.12);
		s7.add(new Month(5, 2010), 135.81);
		s7.add(new Month(6, 2010), 152.36);
		s7.add(new Month(7, 2010), 165.23);
		s7.add(new Month(8, 2010), 175.10);
		s7.add(new Month(9, 2010), 187.80);
		s7.add(new Month(10, 2010), 190.43);
		s7.add(new Month(11, 2010), 206.92);
		s7.add(new Month(12, 2010), 221.51);
		s7.add(new Month(1, 2011), 238.05);
		s7.add(new Month(2, 2011), 261.41);
		s7.add(new Month(3, 2011), 274.10);
		s7.add(new Month(4, 2011), 285.58);
		s7.add(new Month(5, 2011), 277.72);
		s7.add(new Month(6, 2011), 262.52);
		s7.add(new Month(7, 2011), 255.90);
		s7.add(new Month(8, 2011), 260.39);
		s7.add(new Month(9, 2011), 261.39);
		s7.add(new Month(10, 2011), 236.74);
		s7.add(new Month(11, 2011), 235.25);
		s7.add(new Month(12, 2011), 227.23);
		s7.add(new Month(1, 2012), 227.50);
		s7.add(new Month(2, 2012), 212.09);
		s7.add(new Month(3, 2012), 188.78);
		s7.add(new Month(4, 2012), 181.75);
		s7.add(new Month(5, 2012), 176.50);
		s7.add(new Month(6, 2012), 159.93);
		s7.add(new Month(7, 2012), 183.20);
		s7.add(new Month(8, 2012), 169.77);
		s7.add(new Month(9, 2012), 175.36);
		s7.add(new Month(10, 2012), 170.43);
		s7.add(new Month(11, 2012), 155.72);
		s7.add(new Month(12, 2012), 149.58);
		s7.add(new Month(1, 2013), 154.28);
		s7.add(new Month(2, 2013), 144.89);
		s7.add(new Month(3, 2013), 141.43);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);        
		dataset.addSeries(s4);
		dataset.addSeries(s5);
		dataset.addSeries(s6);
		dataset.addSeries(s7);
		return dataset;
	}


	/**
	 * A demonstration application showing a scatter plot.
	 *
	 * @param title  the frame title.
	 */

	/*
	private static int COUNT = 50000;
	private static long time;
	private static int numPhy;
	private static int taxaLevel;


    public Test(String title) {
        super(title);
        JPanel chartPanel = createPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 5000));
        setContentPane(chartPanel);
    }


    public static boolean checkFlags(int numP, int taxLevel)
    {
    	if (numP < 1) 
    	{
    		raiseError("Specify the number of phyla to the plot!");
    		return false;
    	}
    	else if (taxLevel < 0 || taxLevel > 3)
    	{
    		raiseError("Specify the taxa level [0-4]");
    		return false;
    	}
    	return true;
    }
    //****will be replaced with reading in from file, just holding temporary classifications


    public static JFreeChart createBubbleChart(XYZDataset bubbleDataSet)
    {
    	JFreeChart chart = ChartFactory.createBubbleChart("Central", "GC", "COV", bubbleDataSet, PlotOrientation.VERTICAL, true, true, false);
    	XYPlot xyplot = chart.getXYPlot();
    	 XYItemRenderer xyitemrenderer = xyplot.getRenderer();
         xyitemrenderer.setSeriesPaint(0, Color.blue);

    	//Axis settings
    	NumberAxis x = new NumberAxis("GC");
    	LogAxis y = new LogAxis("COV");
    	((LogAxis)y).setBase(10);
    	xyplot.setDomainAxis(x);
    	xyplot.setRangeAxis(y);


    	return chart;	
    }

    public static XYZDataset createBubbleDataSet()
    {
    	DefaultXYZDataset defaultBubbleData = new DefaultXYZDataset();
    	double []gc = new double[COUNT+1];
    	double []cov = new double[COUNT+1];
    	double []len = new double [COUNT+1];
    	for (int i = 0; i < contigSet.size(); i ++)
    	{
    		gc[i] = (double) contigSet.get(i).getGC();
    		cov[i] = (double) contigSet.get(i).getCov()[0];
    		len[i] = (double) contigSet.get(i).getLen()/20.0;
    	}
    	double addMe[][] = {gc, cov,len};
    	defaultBubbleData.addSeries("Test", addMe);
    	return defaultBubbleData;
    }

    public static JPanel createPanel()
    {
    	JFreeChart jfreechart = createBubbleChart(createBubbleDataSet());
    	ChartPanel chartPanel = new ChartPanel(jfreechart);
    	return chartPanel;
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createScatterPlot("Scatter Plot Demo 1",
                "X", "Y", dataset, PlotOrientation.VERTICAL, true, false, false);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setNoDataMessage("NO DATA");
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);

        XYLineAndShapeRenderer renderer 
                = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesOutlinePaint(0, Color.black);
        renderer.setUseOutlinePaint(true);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTickMarkInsideLength(2.0f);
        domainAxis.setTickMarkOutsideLength(0.0f);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickMarkInsideLength(2.0f);
        rangeAxis.setTickMarkOutsideLength(0.0f);

        return chart;
    }

    private static HistogramDataset createDatasetX()
    {
    	HistogramDataset histx = new HistogramDataset();
    	histx.setType(HistogramType.FREQUENCY);
    	double [] gc = new double [COUNT];
    	for (int i = 0; i <= COUNT; i++) 
    	{
    		gc[i] = (double)contigSet.get(i).getGC();
    	}
    	histx.addSeries(key, gc, 1000, 0, 1);
    	return histx;
    }
    public static JPanel createDemoPanel() {
        JFreeChart chart = createBubbleChart(createDatasetScatter());
        ChartPanel chartPanel = new ChartPanel(chart);
        //chartPanel.setVerticalAxisTrace(true);
        //chartPanel.setHorizontalAxisTrace(true);
        // popup menu conflicts with axis trace
        chartPanel.setPopupMenu(null);

        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        return chartPanel;
    }


    private static void raiseError(String message)
	{
		try
		{
			JOptionPane.showMessageDialog(null, message);
		}
		catch(HeadlessException h)
		{
			System.out.println(message);
		}
	}
    /**
	 * Starting point for the demonstration application.
	 *
	 * @param args  ignored.
	 */

	public static void main(String[] args) 
	{
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		startWindow(primaryStage);
	}

	public static void startWindow(Stage stage)
	{
		//Group root = new Group();
		stage.setTitle("Welcome");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(30,30,30,30));
		Scene scene = new Scene (grid, 500, 300);
		stage.setScene(scene);

		Text sceneTitle = new Text("Welcome to Blobsplorer");
		//sceneTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 40.0));		
		grid.add(sceneTitle,0, 0, 2,1);

		Label fileSource = new Label ("File Source");
		grid.add(fileSource, 0, 1);


		Button buttonLoad = new Button ("Load Resouce");
		buttonLoad.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle (ActionEvent arg0) 
			{

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Blobplot.txt files (*.blobplot.txt)", "*.blobplot.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(stage);
				if (file != null)
				{
					boolean attempt = readFile(file);
					if (attempt == false)
						System.out.println("Something wrong with file");
				}
			}
				});
		grid.add(buttonLoad, 1, 1);

		Label taxa = new Label("Taxa cutoff");
		grid.add(taxa, 0, 2);
		TextField taxaNumber = new TextField();
		taxaNumber.setPromptText("No. of taxa displayed");
		taxaNumber.getText();
		grid.add(taxaNumber, 1, 2);

		Button submit = new Button("Submit");
		HBox hbtn = new HBox(10);
		hbtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbtn.getChildren().add(submit);
		grid.add(hbtn, 2, 4);

		Button clear = new Button("Clear");
		HBox hbtn2 = new HBox(10);
		hbtn2.setAlignment(Pos.BOTTOM_RIGHT);
		hbtn2.getChildren().add(clear);
		grid.add(hbtn2, 1, 4);

		final Text errorMessage = new Text();
		//errorMessage.setFill(Color.RED);
		grid.add(errorMessage, 1, 6, 2, 1);

		submit.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle(ActionEvent e)
			{
				boolean run = true;
				String tax = taxaNumber.getText();
				int taxNum = 0;
				//check for if file populated memory
				if (contigSet.isEmpty())
				{
					errorMessage.setText("Enter resource file");
					run = false;
				}

				//check if user entered valid taxa number
				if(tax == null)
				{
					errorMessage.setText("Enter number of taxa");
					run = false;
				}
				else
				{
					try
					{
						taxNum = Integer.parseInt(tax);
						if (taxNum < 0)
						{
							errorMessage.setText("Enter an integer greater than 0");
							run = false;
						}

					}
					catch(NumberFormatException nft)
					{
						errorMessage.setText("Enter an integer value (e.g. 1, 2, etc)");
						run = false;
					}
				}

				if(run)
				{
					setup(taxNum);
					stage.close();
				}

			}
				}
				);
		clear.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle (ActionEvent e)
			{
				taxaNumber.clear();
				clear();
				errorMessage.setText(null);
			}
				}
				);

		stage.show();

	}

	public static boolean parseContig(String newEntry)
	{
		String id;
		int len;
		float gc;
		String covString;
		String taxString;
		double eValue;
		String temp;
		StringTokenizer st;
		StringTokenizer covST;
		StringTokenizer taxST;
		Contig contigToAdd;
		String key;
		String tempString;
		float value;
		int count = 0;
		int taxCount = 0;
		StringTokenizer parse;
		st = new StringTokenizer(newEntry, "\t");
		id = st.nextToken();
		len = Integer.parseInt(st.nextToken());
		gc = Float.parseFloat(st.nextToken());
		covString = st.nextToken();
		covST = new StringTokenizer(covString, ";");
		float [] cov = new float [covST.countTokens()];
		String [] tax = new String [4];
		while(covST.hasMoreTokens())
		{
			tempString = covST.nextToken();
			parse = new StringTokenizer(tempString, "=");
			if(parse.countTokens() % 2 == 0  && parse.countTokens() != 0)
			{
				key = parse.nextToken();
				value = Float.parseFloat(parse.nextToken().replace(";",""));
				cov[count] = value;
				count ++;
			}
			else
			{
				System.out.println("Incorrect number of key value pair entries");
				return false;
			}
		}
		taxString = st.nextToken();
		taxST = new StringTokenizer(taxString, ";");
		String keyValue;
		while(taxST.hasMoreTokens())
		{
			tempString = taxST.nextToken();
			parse = new StringTokenizer(tempString, "=");
			if(parse.countTokens() % 2 == 0)
			{
				key = parse.nextToken();
				keyValue = parse.nextToken().replace(";","");
				tax[taxCount] = keyValue;
				taxCount ++;
			}
			else
			{
				System.out.println("Incorrect number of key value pair entries");
				return false;
			}
		}
		temp = st.nextToken();
		if (temp.contains("N/A"))
			eValue = -1;
		else
			eValue = Double.parseDouble(temp);

		contigToAdd = new Contig(id, len, gc, cov, tax, eValue);
		contigSet.add(contigToAdd);
		return true;
	}

	public static boolean readFile(File file)
	{
		BufferedReader bufferedReader = null;
		boolean correct = true;

		try 
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			System.out.println(bufferedReader.readLine());
			String text;
			while ((text = bufferedReader.readLine()) != null) 
			{
				if(!parseContig(text))
					System.out.println("Unable to parseContig");
			} 

		} 
		catch (FileNotFoundException ex) 
		{
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
		} 
		catch (IOException ex) 
		{
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
		} 
		finally 
		{
			try 
			{
				bufferedReader.close();
			} 
			catch (IOException ex)
			{
				Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
			}
		} 

		return correct;
	}

	public static void getTaxLevelCounts(int taxCategory)
	{
		taxLevelCount.clear();
		for (int i = 0; i < contigSet.size(); i ++)
		{
			String tax = contigSet.get(i).getTax()[taxCategory];
			if(taxLevelCount.containsKey(tax) && taxLevelSpan.containsKey(tax))
			{
				taxLevelCount.put(tax, taxLevelCount.get(tax) +1);
				taxLevelSpan.put(tax,taxLevelSpan.get(tax) + contigSet.get(i).getLen());
			}
			else
			{
				taxLevelCount.put(tax,1);
				taxLevelSpan.put(tax, contigSet.get(i).getLen());
			}
		}
	}

	public static  List<String> mostPopulatedInCutoff (int taxNumber)
	{
		ArrayList<String> mostPopularTax = new ArrayList<String>();
		Iterator<String> it = taxLevelCount.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			Integer value = taxLevelCount.get(key);
			mostPopularTax = sort(taxLevelCount, mostPopularTax, key, value);
		}
		if (taxNumber >= mostPopularTax.size())
			return mostPopularTax; 
		else
			return  mostPopularTax.subList(0, taxNumber);
	}

	private static ArrayList<String> sort(HashMap<String, Integer> source, ArrayList<String> sortedArray, String key, Integer value) 
	{
		boolean go = true;
		int count = 0;
		while(go)
		{
			if (sortedArray.isEmpty())
			{
				sortedArray.add(key);
				go = false;
				break;
			}
			else if (sortedArray.size() == count)
			{
				sortedArray.add(key);
				go = false;
			}
			else if(value >= source.get(sortedArray.get(count)))
			{
				sortedArray.add(count, key);
				go = false;
			}
			count ++;
		}
		return sortedArray;
	}

	public static ArrayList<String> getTopTaxaByTaxaAndSpan (List<String> tax)
	{
		ArrayList<String > topSpanTax = new ArrayList<String>();
		for (int i = 0; i < tax.size(); i ++)
		{
			topSpanTax = sort(taxLevelSpan, topSpanTax, tax.get(i), taxLevelSpan.get(tax.get(i)));
		}
		if (tax.size() != topSpanTax.size())
			System.out.println("Error");
		return topSpanTax;
	}

	private static void setup(int taxaDisplayNumber)
	{
		getTaxLevelCounts(0);
		List<String> popular = mostPopulatedInCutoff (taxaDisplayNumber);
		System.out.println(popular.toString());
		ArrayList<String> span = getTopTaxaByTaxaAndSpan (popular);
		System.out.println(span);
		Stage graph = new Stage();
		drawGraph(graph);
	}

	private static void clear()
	{
		contigSet.clear();
		taxLevelCount.clear();
		taxLevelSpan.clear();
	}

	public static void drawGraph(Stage stage)
	{
		XYDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset); 
		ChartCanvas canvas = new ChartCanvas(chart);
		StackPane stackPane = new StackPane(); 
		stackPane.getChildren().add(canvas);  
		// Bind canvas size to stack pane size. 
		canvas.widthProperty().bind( stackPane.widthProperty()); 
		canvas.heightProperty().bind( stackPane.heightProperty());  
		stage.setScene(new Scene(stackPane)); 
		stage.setTitle("FXGraphics2DDemo1.java"); 
		stage.setWidth(700);
		stage.setHeight(390);
		stage.show(); 
	}


}

