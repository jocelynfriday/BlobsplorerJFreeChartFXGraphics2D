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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
//import javafx.scene.text.Text;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.extension.impl.XYCursor;
import org.jfree.data.general.SelectionChangeEvent;
import org.jfree.data.general.SelectionChangeListener;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.fx.FXGraphics2D;
import org.jfree.ui.HorizontalAlignment;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;

/**
 * A demo scatter plot.
 */
public class Test extends Application implements SelectionChangeListener<XYCursor>
{
	private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
	private static HashMap <String, Integer> taxLevelCount = new HashMap<String, Integer>();
	private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();

	private JTable table;
	private DefaultTableModel model;
	private XYSeriesCollection scatterDataset;
	private static int taxLevel = 0;
	private static int covLevel = 0;
	private static final SwingNode chartSwingNode = new SwingNode();
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
	/*
	public Test (String title)
	{
		ChartPanel cartPanel = (ChartPanel)createDemoPa
		
	}
	*/
	
	private static JFreeChart createChart(XYZDataset dataset) 
	{
		JFreeChart chart = ChartFactory.createBubbleChart(
				"Blobsplorer : GC vs COV",    // title
				"GC",             // x-axis label
				"COV",      // y-axis label
				dataset,            // data
				PlotOrientation.VERTICAL, //orientation
				true,               // create legend?
				true,               // generate tooltips?
				false               // generate URLs?
				);

		String fontName = "Palatino";
		chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
		chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 14));
		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		
		final org.jfree.chart.axis.NumberAxis domainAxis = new org.jfree.chart.axis.NumberAxis("GC");
		//LogAxis rangeAxis = new LogAxis("COV");
		domainAxis.setRange(0.00, 1.00);
		domainAxis.setTickUnit(new NumberTickUnit(0.1));
		
		plot.setDomainAxis(domainAxis);
		//plot.setRangeAxis(rangeAxis);
		plot.setBackgroundPaint(Color.WHITE);
	 
		//remember that the renderer is a bubble renderer
		 return chart;
	}

	private static XYZDataset createScatterDataset(ArrayList <String> span) 
	{
		DefaultXYZDataset dataset = new DefaultXYZDataset();
		double[] gc;
		double [] cov;
		double [] len;
		
		for(int i = 0; i < span.size(); i ++ )
		{
			//make sure arrayLists are clean
			String key = span.get(i);
			int size = taxLevelCount.get(key);
			gc = new double [size];
			cov = new double [size];
			len = new double [size];
			int count = 0;
			for (int j = 0; j < contigSet.size(); j ++)
			{
				if(key.equals(contigSet.get(j).getTax()[taxLevel])) //If mapping taxa level matches desired taxa to be charted
				{
					
					if((double)contigSet.get(j).getCov()[covLevel] == 0)
					{
						contigSet.get(j).setCovAtPos(covLevel, 1E-100);
					}
					
						
						cov[count] =contigSet.get(j).getCov()[covLevel];
						gc[count] = contigSet.get(j).getGC();
						len[count] = (contigSet.get(j).getLen()/20.0);
						count ++;
					
					
				}
				
			}
			double addMe[][] = {Arrays.copyOf(gc, count), Arrays.copyOf(cov, count),Arrays.copyOf(len, count)};
	    	dataset.addSeries(key, addMe);
		}
		return dataset;
	}


	/**
	 * A demonstration application showing a scatter plot.
	 *
	 * @param title  the frame title.
	 */

	/*
   


    

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
		double gc;
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
		double value;
		int count = 0;
		int taxCount = 0;
		StringTokenizer parse;
		st = new StringTokenizer(newEntry, "\t");
		id = st.nextToken();
		len = Integer.parseInt(st.nextToken());
		gc = Double.parseDouble(st.nextToken());
		covString = st.nextToken();
		covST = new StringTokenizer(covString, ";");
		double [] cov = new double [covST.countTokens()];
		String [] tax = new String [4];
		while(covST.hasMoreTokens())
		{
			tempString = covST.nextToken();
			parse = new StringTokenizer(tempString, "=");
			if(parse.countTokens() % 2 == 0  && parse.countTokens() != 0)
			{
				key = parse.nextToken();
				value = Double.parseDouble(parse.nextToken().replace(";",""));
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

	public static void getTaxLevelCounts()
	{
		taxLevelCount.clear();
		for (int i = 0; i < contigSet.size(); i ++)
		{
			String tax = contigSet.get(i).getTax()[taxLevel];
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
			System.out.println("Error, value lost");
		return topSpanTax;
	}

	private static void setup(int taxaDisplayNumber)
	{
		getTaxLevelCounts();
		List<String> popular = mostPopulatedInCutoff (taxaDisplayNumber);
		System.out.println(popular.toString());
		ArrayList<String> span = getTopTaxaByTaxaAndSpan (popular);
		System.out.println(span);
		Stage graph = new Stage();
		drawGraph(graph, span);
	}

	private static void clear()
	{
		contigSet.clear();
		taxLevelCount.clear();
		taxLevelSpan.clear();
	}

	public static void drawGraph(Stage stage, ArrayList<String> span)
	{
		XYZDataset dataset = createScatterDataset(span);
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
	@Override
	public void selectionChanged(SelectionChangeEvent<XYCursor> event) {
		// TODO Auto-generated method stub
		
	}


}

