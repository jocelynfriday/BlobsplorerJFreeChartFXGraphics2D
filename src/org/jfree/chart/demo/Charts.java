package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.panel.selectionhandler.EntitySelectionManager;
import org.jfree.chart.panel.selectionhandler.FreePathSelectionHandler;
import org.jfree.chart.panel.selectionhandler.MouseClickSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RectangularRegionSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RegionSelectionHandler;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.item.IRSUtilities;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.NumberCellRenderer;
import org.jfree.data.extension.DatasetIterator;
import org.jfree.data.extension.DatasetSelectionExtension;
import org.jfree.data.extension.impl.DatasetExtensionManager;
import org.jfree.data.extension.impl.XYCursor;
import org.jfree.data.extension.impl.XYDatasetSelectionExtension;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.SelectionChangeEvent;
import org.jfree.data.general.SelectionChangeListener;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

public class Charts extends JFrame  implements SelectionChangeListener<XYCursor>
{
	//private XYDataset dataset;
	private XYSeriesCollection dataset;
	private DefaultTableModel model;
	private JTable table;
	private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
	private static int numberOfTaxaDisplayed;
	private static int taxaIndex = 0;
	private static int covLibraryIndex = 0; // which cov library to use //** needs to be added to UI
	private static HashMap<String, ArrayList<Contig>> contigByTaxa = new HashMap<String, ArrayList<Contig>>();
	private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
	private static ArrayList<String> topTaxaBySpan = new ArrayList<String>();
	private static int totalLength = 0;


	public Charts(File file, int taxLevel, double eValue, String title)
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		numberOfTaxaDisplayed = taxLevel;
		readFile(file, eValue); // might add boolean check later
		ChartPanel chartPanel = (ChartPanel)createScatterPanel();
		chartPanel.setPreferredSize(new Dimension(500, 270));
		JFreeChart chart = chartPanel.getChart();
		XYPlot plot = (XYPlot)chart.getPlot();
		this.dataset = ((XYSeriesCollection)plot.getDataset());
		JSplitPane split = new JSplitPane(1);
		split.add(chartPanel);

		this.model = new DefaultTableModel(new String[] { "Statistic:", "Value:", ""}, 0);
		this.table = new JTable(this.model);
		TableColumnModel tcm = this.table.getColumnModel();
		tcm.getColumn(2).setCellRenderer(new NumberCellRenderer());
		JPanel p = new JPanel(new BorderLayout());
		JScrollPane scroller = new JScrollPane(this.table);
		p.add(scroller);
		p.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Statistics of Selected: "), new EmptyBorder(4, 4, 4, 4)));

		split.add(p);
		/*
	    JPanel boxPanel = new JPanel();
	   	for (int i = 0; i < topTaxaBySpan.size(); i ++)
	   	{
	   		JCheckBox box = new JCheckBox(topTaxaBySpan.get(i));
	   		box.setActionCommand(topTaxaBySpan.get(i));
	   		box.addActionListener((ActionListener) this);
	   		box.setSelected(true);
	   		boxPanel.add(box);
	   	}
		 */
		setContentPane(split);
		// add(boxPanel, BorderLayout.SOUTH);
	}
	/*
	public void actionPerformed (ActionEvent e)
	{
		int series = -1;
		for (int i = 0; i < topTaxaBySpan.size(); i ++)
		{
			if (e.getActionCommand().equals(topTaxaBySpan.get(i)))
			{
				series = i;
				break;
			}
		}
		if (series >= 0)
		{//	boolean visible = this.renderer.getItemVisible(series, 0);
			//this.renderer.setSeriesVisible(series, Boolean.valueOf(!visible));

		}
	}
	 */

	private JPanel createScatterPanel() 
	{
		XYDataset dataset = createDataset();

		DatasetSelectionExtension datasetExtension = new XYDatasetSelectionExtension(dataset);
		datasetExtension.addChangeListener(this);

		JFreeChart chart = createChart(dataset, datasetExtension);
		ChartPanel panel = new ChartPanel(chart);
		panel.setMouseWheelEnabled(true);

		RegionSelectionHandler selectionHandler = new RectangularRegionSelectionHandler();

		panel.addMouseHandler(selectionHandler);
		panel.addMouseHandler(new MouseClickSelectionHandler());
		panel.removeMouseHandler(panel.getZoomHandler());

		DatasetExtensionManager dExManager = new DatasetExtensionManager();
		dExManager.registerDatasetExtension(datasetExtension);

		EntitySelectionManager selectionManager = new EntitySelectionManager(panel, new Dataset[] {dataset}, dExManager);
		selectionManager.setIntersectionSelection(true);
		panel.setSelectionManager(selectionManager);

		return panel;
	}

	private JFreeChart createChart(XYDataset dataset, DatasetSelectionExtension<XYCursor> datasetExtension) 
	{
		JFreeChart chart = ChartFactory.createScatterPlot("BlobSplorer", "GC", "COV", dataset);

		XYPlot plot = (XYPlot)chart.getPlot();
		plot.setNoDataMessage("NO DATA");

		plot.setDomainPannable(true);
		plot.setRangePannable(true);


		plot.setDomainGridlineStroke(new BasicStroke (0.0f));
		plot.setRangeGridlineStroke(new BasicStroke(0.0f));

		XYDotRenderer r = new XYDotRenderer();
		r.setDotWidth(4);
		r.setDotHeight(4);
		plot.setRenderer(r);
		//XYItemRenderer r = (XYItemRenderer) plot.getRenderer();
		//r.setSeriesFillPaint(0, Color.GRAY);

		NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis();
		domainAxis.setRange(0.00, 1.00);
		domainAxis.setTickUnit(new NumberTickUnit(0.1));

		LogAxis yAxis = new LogAxis("COV");
		plot.setRangeAxis(yAxis);

		//IRSUtilities.setSelectedItemOutlinePaint(r, datasetExtension, Color.white);

		datasetExtension.addChangeListener(plot);
		return chart;

	}

	private XYDataset createDataset() 
	{
		//DefaultXYZDataset dataset = new DefaultXYZDataset();
		//double[] gc;
		//double [] cov;
		//double [] len;

		XYSeriesCollection dataset = new XYSeriesCollection();
		ArrayList<String> topTaxa = getTopTaxa();
		topTaxaBySpan = sortBySpan(topTaxa);
		ArrayList<String> taxaForDisplay = sortBySpan(topTaxa); // ArrayList containing top taxa which need to be displayed

		for(int i = 0; i < taxaForDisplay.size(); i ++) //loop through arrayLists associated with top taxa by span
		{
			String taxa = taxaForDisplay.get(i);
			ArrayList<Contig> taxaSet = contigByTaxa.get(taxa);
			//gc = new double [taxaSet.size()];
			//	cov = new double [taxaSet.size()];
			XYSeries series = new XYSeries(taxa); 
			//len = new double [taxaSet.size()];
			for(int j = 0; j < taxaSet.size(); j ++)
			{	
				//as 0 cannot be displayed on log scale, set libraries where coverage is 0 to small value
				if ((double)taxaSet.get(j).getCov()[covLibraryIndex] == 0)
				{
					taxaSet.get(j).setCovAtPos(covLibraryIndex, 1E-10);
				}
				//cov[j] =taxaSet.get(j).getCov()[covLevel];
				//gc[j] = taxaSet.get(j).getGC();
				series.add(taxaSet.get(j).getGC(), taxaSet.get(j).getCov()[covLibraryIndex]);
				//len[j] = (contigSet.get(j).getLen()/20.0);
			}
			//double [][] addMe = {gc, cov, len};
			//dataset.addSeries(taxa, addMe);
			dataset.addSeries(series);
		}
		return dataset;

	}

	//**Should I create a treeMap to ease sorting?
	/*
	 * Selects top x number (taxaLevel) of taxa to be displayed in chart.
	 * Iterates through contigByTaxa and sorts the keys based on the number of contigs belonging to a given taxa
	 * (i.e. sorts the keys of the map based on the length of the value's ArrayList) and truncates result if necessary
	 */
	private ArrayList<String> getTopTaxa()
	{
		ArrayList <String> topTaxa = new ArrayList<String> ();
		Iterator<String> itr = contigByTaxa.keySet().iterator();	
		while(itr.hasNext())
		{
			String key = itr.next();
			topTaxa = sortTaxa(key,topTaxa);
		}


		if (numberOfTaxaDisplayed >= topTaxa.size()) //size of sorted array is less than max number of listed taxa?
		{
			return topTaxa; 
		}
		else
		{
			List<String> truncated = topTaxa.subList(0, numberOfTaxaDisplayed);
			return new ArrayList<String>(truncated);
		}

	}

	/*
	 * Returns an ArrayList of Taxa sorted by their span
	 */
	private ArrayList<String> sortBySpan (ArrayList<String>unsorted)
	{
		ArrayList<String> sorted = new ArrayList<String>();
		for(int i = 0; i < unsorted.size(); i ++)
		{
			sorted = sortSpan(sorted, unsorted.get(i));
		}
		Collections.reverse(sorted); //First in last out in terms of rendering, 
		return sorted;
	}

	/*
	 * helper method for sortBySpan(ArrayList<String> unsorted. 
	 * Takes a sorted ArrayList and the element to be added, placing the new element in the sorted ArrayList
	 */
	private ArrayList<String> sortSpan(ArrayList<String> addToMe, String addMe)
	{
		boolean go = true;
		int count = 0;
		while(go)
		{
			if (addToMe.size() == 0)
			{
				addToMe.add(addMe);
				go = false;
			}
			else if (addToMe.size() == count)
			{
				addToMe.add(addMe);
				go = false;
			}
			else if (taxLevelSpan.get(addMe) >= taxLevelSpan.get(addToMe.get(count)))
			{
				addToMe.add(count, addMe);
				go = false;
			}
			count ++;
		}
		return addToMe;
	}

	private ArrayList<String> sortTaxa(String addMe,  ArrayList<String> sortedTaxa) 
	{
		boolean go = true;
		int count = 0;
		while(go)
		{
			if (sortedTaxa.isEmpty()) //first value to be added
			{
				sortedTaxa.add(addMe);
				go = false;
			}
			else if (sortedTaxa.size() == count) //smaller than all existing values, append at end
			{
				sortedTaxa.add(addMe);
				go = false;
			}
			else if (contigByTaxa.get(addMe).size() >= contigByTaxa.get(sortedTaxa.get(count)).size()) // if new size is larger than the one at count, add in front
			{
				sortedTaxa.add(count, addMe);
				go = false;
			}
			count ++;
		}
		return sortedTaxa;
	}

	
	/***************************************
	*Display statistics for selection of contigs
	*
	***************************************/
	
	/*
	 * Intended to be for calculating the span of a selected group of Contigs
	 */
	public int getSpan (ArrayList<Contig> selection)
	{
		int span = 0;
		for (int i = 0; i < selection.size(); i ++)
		{
			span += selection.get(i).getLen();
		}
		return span;
	}

	public double getMeanLength (ArrayList<Contig> selection)
	{
		double total = 0;
		for(int i = 0; i < selection.size(); i++)
		{
			total += selection.get(i).getLen();
		}

		return total/selection.size();
	}
	

	public double getMedianLength(ArrayList<Contig> selection)
	{
		if(selection.size() == 0)
			return -1.0;
		
		ArrayList<Integer> sorted = new ArrayList<Integer>(selection.size());
		for(int i = 0; i < selection.size(); i ++)
		{
			sorted.add(selection.get(i).getLen());
		}
		Collections.sort(sorted);
		for(int i = 0; i < sorted.size(); i ++)
		{
			System.out.println(sorted.get(i));
		}
		if(sorted.size() == 1)
			return sorted.get(0)*1.0;
		else if (sorted.size() == 2)
			return (sorted.get(0) + sorted.get(1))/2.0;
		else if(sorted.size() > 3)
		{
			if(sorted.size()%2 != 0)
			{
				int index = (sorted.size()/2) + 1;
				return sorted.get(index)*1.0;
			}
			else
			{
				int index1 = (sorted.size()/2);
				int index2 = index1 + 1;
				return (sorted.get(index1) + sorted.get(index2))/2.0;
			}
		}
		return -10; // check that All possible medians are caught
	}
	
	public double getMeanGC(ArrayList<Contig> selection)
	{
		double total = 0;
		for (int i = 0; i < selection.size(); i ++)
		{
			total += selection.get(i).getGC();
		}
		return total/selection.size();
	}
	
	/*
	 * Retrns the N50 of a selected group of Contigs.  If a contig bridges the rounded midpoint, it is included in the N50 calculation
	 */
	public int calculateN50(ArrayList<Contig> selection)
	{
		ArrayList<Integer> sorted = new ArrayList<Integer>(selection.size());
		int n50 = 0;
		int midPoint = (int)Math.round(getSpan(selection)/2.0); //calculates midpoint by getting total length of span/ 2 and truncates
		for(int i = 0; i < selection.size(); i ++) // populate ArrayList of integer
		{
			sorted.add(selection.get(i).getLen());
		}
		Collections.sort(sorted);
		Collections.reverse(sorted);
		
		for(int i = 0; i < sorted.size(); i ++)
		{
			if (n50 >= midPoint)
				 break;
			n50 += sorted.get(i);
		}
		return n50;
	}
	
	private String [] [] separateByTaxa(ArrayList<Contig> selected) 
	{
		HashMap<String, ArrayList<Contig>> grouped = new HashMap<String, ArrayList<Contig>>();
		HashMap<String, Integer> groupedSpan = new HashMap<String, Integer>();
		String [] [] groupedSpanTotalsByTaxa;
		for(int i = 0; i < selected.size(); i ++)
		{
			if(grouped.containsKey(selected.get(i).getTax()[taxaIndex]))//Key already exists in Map, default for phylum
			{
				ArrayList<Contig> temp = grouped.get((selected.get(i).getTax()[taxaIndex]));
				temp.add(selected.get(i));
				grouped.put(selected.get(i).getTax()[taxaIndex], temp);
				groupedSpan.put(selected.get(i).getTax()[taxaIndex],groupedSpan.get(selected.get(i).getTax()[taxaIndex]) + selected.get(i).getLen());

			}
			else
			{
				ArrayList<Contig> temp = new ArrayList<Contig>();
				temp.add(selected.get(i));
				grouped.put(selected.get(i).getTax()[taxaIndex], temp);
				groupedSpan.put(selected.get(i).getTax()[taxaIndex], selected.get(i).getLen());
			}
		}
		groupedSpanTotalsByTaxa = new String [grouped.keySet().size()][2];
		Iterator<String> itr = grouped.keySet().iterator();
		int count = 0;
		while(itr.hasNext())
		{
			String taxa = itr.next();
			groupedSpanTotalsByTaxa[count][0] = taxa;
			groupedSpanTotalsByTaxa[count][1] = groupedSpan.get(taxa).toString();
			count ++;
		}
		return groupedSpanTotalsByTaxa;
	}

	private void statistics(ArrayList<Contig> selected)
	{
		int number = selected.size();
		if(number > 0)
		{
			while(this.model.getRowCount() > 0)
			{
				this.model.removeRow(0);
			}
			
			int n50 = calculateN50(selected);
			double meanGC = getMeanGC(selected);
			double medianLen = getMedianLength(selected);
			double meanLen = getMeanLength(selected);
			int span = getSpan(selected);
			String selectedSpan = span + "/" + totalLength;
			this.model.addRow(new Object[] {"Mean length: ", new Double(meanLen)});
			this.model.addRow(new Object[] {"Median length: ", new Double(medianLen)});
			this.model.addRow(new Object[] {"Mean GC: ", new Double(meanGC)});
			this.model.addRow(new Object[] {"Span: ", new String(selectedSpan)});
			this.model.addRow(new Object[] {"Number of contigs: ", new Integer(number)});
			this.model.addRow(new Object[] {"N50 of selected: ", new Integer(n50)}); 
			this.model.addRow(new Object[] {"", }); 
			this.model.addRow(new Object[] {"Span breakdown: ", "Taxa/Selection"}); 
			String[][] selectedContigByTaxa = separateByTaxa(selected);
			for(int i = 0; i < selectedContigByTaxa.length; i ++)
			{
				this.model.addRow(new Object[] {selectedContigByTaxa[i][0], new String(selectedContigByTaxa[i][1]+"/"+span)}); 
			}
		}
	}
	
	
	/*
	 * Helper method for getTopTaxa()
	 * Adds taxa to sorted ArrayList<Stirng> of taxa based on the length of the ArrayList associated with said 
	 */
	
	/*
	 * Reads file passed in from JavaFX scene in Test.java
	 * parses each line of the file to generate a Contig
	 * Passes on replacement value for "N/A" result, E values to the user entered eValue, to parseContig(String text, double eValue) 
	 * Adds new contig to ArrayList of existing contigs 
	 * Builds HashMap<String, ArrayList<Contig>> contigByTaxa, where Contigs are grouped by taxa
	 * Builds HashMap <String, Integer> taxLevelSpan, where the span of each taxa is calculated 
	 * Returns false if error occures
	 */
	public static boolean readFile(File file, double eValue)
	{
		BufferedReader bufferedReader = null;
		boolean correct = true;
		try 
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			Object dummy = bufferedReader.readLine();

			String text;
			while ((text = bufferedReader.readLine()) != null) 
			{
				Contig addMe = parseContig(text, eValue);
				if (addMe == null)
				{
					System.out.println("Unable to parseContig");
				}
				else
				{	
					contigSet.add(addMe); //add Contig to Arraylist
					String tax = addMe.getTax()[taxaIndex];
					totalLength += addMe.getLen();
					if(contigByTaxa.containsKey(tax))//Key already exists in Map, default for phylum
					{
						ArrayList<Contig> temp = contigByTaxa.get(tax);
						temp.add(addMe);
						contigByTaxa.put(tax, temp);
						taxLevelSpan.put(tax,taxLevelSpan.get(tax) + addMe.getLen());

					}
					else
					{
						ArrayList<Contig> temp = new ArrayList<Contig>();
						temp.add(addMe);
						contigByTaxa.put(tax,  temp);
						taxLevelSpan.put(tax, addMe.getLen());
					}
				}
			} 

		} 
		catch (FileNotFoundException ex) 
		{
			correct = false;
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
		} 
		catch (IOException ex) 
		{
			correct = false;
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
				correct = false;
				Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
			}
		} 

		return correct;
	}

	/*
	 * Parses out Contrig from sinle line of text and replaces evalue "N/A" results with the user defined double
	 * Parsing structure based on https://github.com/blaxterlab/blobology/tree/master/dev format as of 18/06/14
	 */
	private static Contig parseContig(String newEntry, double eValue2)
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
				return null;
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
				return null;
			}
		}
		temp = st.nextToken();
		if (temp.contains("N/A"))
			eValue = eValue2;
		else
			eValue = Double.parseDouble(temp);

		contigToAdd = new Contig(id, len, gc, cov, tax, eValue);

		return contigToAdd;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jfree.data.general.SelectionChangeListener#selectionChanged(org.jfree.data.general.SelectionChangeEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangeEvent<XYCursor> event) 
	{
		while(this.model.getRowCount() > 0)
		{
			this.model.removeRow(0);
		}

		XYDatasetSelectionExtension ext = (XYDatasetSelectionExtension)event.getSelectionExtension();
		DatasetIterator itr = ext.getSelectionIterator(true);
		//XYPlot plot = (XYPlot) chart.getPlot();
		
		ArrayList<Contig> selected = new ArrayList<Contig>();
		while(itr.hasNext())
		{
			XYCursor dc = (XYCursor)itr.next();
			Comparable seriesKey = this.dataset.getSeriesKey(dc.series);
			System.out.println("item" + dc.item);
			Number x = this.dataset.getX(dc.series, dc.item);
			Number y = this.dataset.getY(dc.series, dc.item);
			ArrayList<Contig> taxa = contigByTaxa.get(seriesKey);
			selected.add(taxa.get(dc.item));
		}
		long end = System.nanoTime();
		statistics(selected);
	}
	

}

/*



public class Test extends Application 
{
	private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
	private static HashMap <String, Integer> taxLevelCount = new HashMap<String, Integer>();
	private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
	private static ArrayList<String> span = new ArrayList<String>();
	private JTable table;
	private DefaultTableModel model;
	private XYSeriesCollection scatterDataset;
	private static int taxLevel = 0;
	private static int covLevel = 0;
	private static double defaultEValue = 1.0;
	private static JFrame temp = new JFrame();
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

	public Test (String title)
	{
		ChartPanel cartPanel = (ChartPanel)createDemoPa

	}


	private static JFreeChart createChart(XYZDataset dataset, DatasetSelectionExtension datasetExtension) 
	{
		JFreeChart chart = ChartFactory.createBubbleChart(
				"Blobsplorer : GC vs COV",    // title
				"GC",             // x-axis label
				"COV",      // y-axis label
				dataset
				);

		String fontName = "Palatino";
		chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
		chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 14));
		chart.getLegend().setFrame(BlockBorder.NONE);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);


		final org.jfree.chart.axis.NumberAxis domainAxis = new org.jfree.chart.axis.NumberAxis("GC");
		LogAxis rangeAxis = new LogAxis("COV");
		domainAxis.setRange(0.00, 1.00);
		domainAxis.setTickUnit(new NumberTickUnit(0.1));

		plot.setDomainAxis(domainAxis);
		//plot.setRangeAxis(rangeAxis);
		plot.setRenderer(new XYBubbleRenderer(2));
		//plot.setBackgroundPaint(Color.WHITE);

		//remember that the renderer is a bubble renderer
		return chart;
	}

	private static XYZDataset createScatterDataset() 
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
						contigSet.get(j).setCovAtPos(covLevel, 1E-10);
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


	public final static JPanel createDemoPanel()
	  {
	    XYZDataset xyzdataset = createScatterDataset();

	    DatasetSelectionExtension datasetExtension = new XYDatasetSelectionExtension(xyzdataset);

	   // datasetExtension.addChangeListener(this);

	    JFreeChart chart = createChart(xyzdataset, datasetExtension);
	    ChartPanel panel = new ChartPanel(chart);
	    panel.setMouseWheelEnabled(true);

	    RegionSelectionHandler selectionHandler = new FreePathSelectionHandler();

	    panel.addMouseHandler(selectionHandler);
	    panel.addMouseHandler(new MouseClickSelectionHandler());
	    panel.removeMouseHandler(panel.getZoomHandler());

	    DatasetExtensionManager dExManager = new DatasetExtensionManager();
	    dExManager.registerDatasetExtension(datasetExtension);

	    EntitySelectionManager selectionManager = new EntitySelectionManager(panel, new Dataset[] { xyzdataset }, dExManager);

	    selectionManager.setIntersectionSelection(true);
	    panel.setSelectionManager(selectionManager);

	    return panel;
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

	public static void getTopTaxaByTaxaAndSpan (List<String> tax)
	{
		for (int i = 0; i < tax.size(); i ++)
		{
			span = sort(taxLevelSpan, span, tax.get(i), taxLevelSpan.get(tax.get(i)));
		}
		if (tax.size() != span.size())
			System.out.println("Error, value lost");
	}

	//*$* need to incorporate eValue
	private static void setup(int taxaDisplayNumber, double eValue)
	{
		getTaxLevelCounts();
		List<String> popular = mostPopulatedInCutoff (taxaDisplayNumber);
		System.out.println(popular.toString());
		getTopTaxaByTaxaAndSpan (popular);
		System.out.println(span);
		Stage graph = new Stage();
		drawGraph(graph);
	}



	public static void drawGraph(Stage stage)
	{
		createDemoPanel();
		XYZDataset dataset = createScatterDataset();
		JFreeChart chart = createChart(dataset, null); 
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
	public void selectionChanged(SelectionChangeEvent<XYCursor> event) 
	{
		// TODO Auto-generated method stub

	}


}
 */