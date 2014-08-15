package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.text.Text;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.panel.selectionhandler.EntitySelectionManager;
import org.jfree.chart.panel.selectionhandler.MouseClickSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RectangularRegionSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RegionSelectionHandler;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.extension.DatasetIterator;
import org.jfree.data.extension.DatasetSelectionExtension;
import org.jfree.data.extension.impl.DatasetExtensionManager;
import org.jfree.data.extension.impl.XYCursor;
import org.jfree.data.extension.impl.XYDatasetSelectionExtension;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.SelectionChangeEvent;
import org.jfree.data.general.SelectionChangeListener;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class Charts extends ApplicationFrame{
	/**
	 *  Taxon-annotated coverage-GC plot (TAGC) is the formal name for the mainChart
	 */
	private static final long serialVersionUID = 1L;


	public static class BlobPanel extends DemoPanel implements  ChangeListener, ChartChangeListener, KeyListener , SelectionChangeListener<XYCursor>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private XYSeriesCollection dataset;
		private JFreeChart mainChart;
		private JFreeChart ySubChart;
		private JFreeChart xSubChart;
		private JSlider eValueJSlider;
		private JSlider lengthJSlider;
		private JSlider covJSlider;
		private Range lastXRange;
		private Range lastYRange;
		private DefaultTableModel model;
		private DefaultTableModel stats;
		private ChartPanel chartPanel;
		private JPanel checkBoxPanel;
		private JFrame controlFrame;
		private JTable table;
		private static ArrayList<String> history;
		private HashMap<String, Color>  colors;
		private int previousTaxa = 0;
		private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
		private JCheckBox unselect;

		private static File file;
		private static double defaultEValue;
		private static ArrayList<Contig> contigMaster = new ArrayList<Contig>();
		private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
		private static int taxaIndex = 2;
		private static  int covLibraryIndex = 0; // which cov library to use //** needs to be added to UI
		private static  HashMap<String, ArrayList<Contig>> contigByTaxa = new HashMap<String, ArrayList<Contig>>();
		private static  HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
		private static  int totalLength = 0;
		private static  double maxEValue = 1.0;
		private static  int minContigLength = 0;
		private static  double defaultMinCov = 1E-5;
		private static  double minCov;
		private static  double maxCov = 0;
		private static  double minX = 0;
		private static  double maxX = 0;
		private static  double minY = 0;
		private static  double maxY = 0;
		private static  int totalNumberOfContigs = 0;
		private static  double minFoundEValue= 0;
		private static  String header = "";
		private static  String[] taxaNames;
		private static  String [] covLibraryNames;
		private  final Color [] basicColors = {Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GREEN, Color.PINK, Color.ORANGE, new Color(199,21,133), new Color(72,209,204), new Color(46,139,87), 
				new Color(0,128,128), new Color(128,0,128), new Color(47,79,79), new Color(0,0,128), new Color(138,43,226), new Color(199,21,133), new Color(0,255,0), new Color(220,20,60), new Color(216,191,216), new Color(255,215,0), new Color(0,100,0), new Color(186,85,211), new Color(255,140,0), new Color(153,0,76), new Color(204,255,255), new Color(153,255,204), new Color(255,204,153), new Color(204,153,255), new Color(51,0,102), new Color(0,76,153), new Color(255,0,127), new Color(0,102,102), new Color(102,0,51) };

		private static  ArrayList<String> taxaForDisplay;

		/**
		 * Central control for Blobsplorer and controls how a session is created.  
		 * @param file blobplot.txt file to be used for a session
		 * @param covLevel user set default coverage
		 * @param eValue user set default E-Value
		 */
		public BlobPanel(File file, int covLevel, double eValue)
		{
			super(new BorderLayout());
			long start = System.nanoTime();
			System.out.println("in blobPanel");
			defaultMinCov= covLevel;
			this.file = file;
			this.defaultEValue = eValue;
			previousTaxa = taxaIndex;
			readFile(); // might add boolean check later
			getTaxaForDisplay();
			colors = createColorHashMap();

			chartPanel = (ChartPanel) createMainPanel();
			chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));

			add(chartPanel);

			JPanel minEvaluePanel = new JPanel(new BorderLayout());			
			DefaultTableXYDataset yDataset = createYDataset();
			//this.ySubChart = ChartFactory.createXYStackedBarChart("Domain count", "COV", "Count", yDataset, PlotOrientation.HORIZONTAL, false, false, false);
			StackedXYBarRenderer stackedR = new StackedXYBarRenderer(.8);
			stackedR.setBarPainter(new StandardXYBarPainter());
			stackedR.setRenderAsPercentages(true);
			stackedR.setBase(1);
			stackedR.setDrawBarOutline(false);
			stackedR.setShadowVisible(false);
			stackedR.setBarAlignmentFactor(10);
			LogAxis yDomainAxis = new LogAxis("COV"); 
			yDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			yDomainAxis.setLowerMargin(0.0);
			yDomainAxis.setUpperMargin(0.0);
			//yDomainAxis.setBase(10);
			//yDomainAxis.setRange(1E-4, (maxY+10));
			NumberAxis yRange = new NumberAxis("Percentage");
			yRange.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			//yDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			XYPlot plot = new XYPlot(yDataset, yDomainAxis, yRange,stackedR);
			plot.getDomainAxis().setRange(( (XYPlot) this.mainChart.getPlot()).getDomainAxis().getRange());

			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			plot.getRangeAxis().setLowerMargin(0.0);
			plot.getRangeAxis().setUpperMargin(0.0);
			plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
			plot.setOrientation(PlotOrientation.HORIZONTAL);
			this.ySubChart = new JFreeChart(plot);
			this.ySubChart.removeLegend();
			ChartPanel ySubChartPanel = new ChartPanel(ySubChart);
			ySubChartPanel.setMinimumDrawWidth(0);
			ySubChartPanel.setMinimumDrawHeight(0);

			ySubChartPanel.setPreferredSize(new Dimension (300, 500));

			minEvaluePanel.add(ySubChartPanel);
			//minEvaluePanel.setPreferredSize(new Dimension(200,250));

			//Add X dataset <- GC
			JPanel minLengthPanel = new JPanel(new BorderLayout());
			DefaultTableXYDataset xDataset = createXDataset();
			StackedXYBarRenderer stackedD = new StackedXYBarRenderer(.99);
			stackedD.setBarPainter(new StandardXYBarPainter());
			stackedD.setRenderAsPercentages(true);
			stackedD.setDrawBarOutline(false);
			stackedD.setShadowVisible(false);
			NumberAxis xRangeAxis = new NumberAxis("Percentage"); 
			//xRangeAxis.setRange(-1, 1E5);
			xRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			NumberAxis domainAxis = new NumberAxis("GC");
			domainAxis.setLowerMargin(0.0);
			domainAxis.setUpperMargin(0.0);
			domainAxis.setRange(0.0, 1.0);
			XYPlot plot1 = new XYPlot(xDataset, domainAxis, xRangeAxis,stackedD);
			plot1.setOrientation(PlotOrientation.VERTICAL);
			plot1.getDomainAxis().setLowerMargin(0.0);
			plot1.getDomainAxis().setUpperMargin(0.0);
			plot1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			this.xSubChart = new JFreeChart(plot1);
			this.xSubChart.removeLegend();

			ChartPanel lengthPanel = new ChartPanel(xSubChart);
			lengthPanel.setMinimumDrawWidth(0);
			lengthPanel.setMinimumDrawHeight(0);
			lengthPanel.setPreferredSize(new Dimension(200,300));


			XYItemRenderer r = ((XYPlot) chartPanel.getChart().getPlot()).getRenderer();
			StackedXYBarRenderer yRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();
			StackedXYBarRenderer xRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				Color paintMe = colors.get(taxaForDisplay.get(i));

				r.setSeriesPaint(i, paintMe);
				yRenderer.setSeriesPaint(i, paintMe);
				xRenderer.setSeriesPaint(i, paintMe);
			}

			((XYPlot) chartPanel.getChart().getPlot()).setRenderer(r);
			((XYPlot) xSubChart.getPlot()).setRenderer(xRenderer);
			((XYPlot) ySubChart.getPlot()).setRenderer(yRenderer);
			chartPanel.getChart().removeLegend();

			//panel for layout
			JPanel spacing = new JPanel();
			spacing.setPreferredSize(new Dimension(300,10));
			minLengthPanel.add(spacing, BorderLayout.EAST);

			minLengthPanel.add(lengthPanel);

			add(minEvaluePanel, BorderLayout.EAST);
			add(minLengthPanel, BorderLayout.NORTH);
			this.mainChart.setNotify(true);
			createTabbedControl();

			long end = System.nanoTime();
			System.out.println("Total time: " + (end - start));
		}


		/**
		 * Creates the control window with a tab each of: statistics, taxonomic annotations, filters (e.g. contig length and E-value) and exports (e.g. SVG files).  
		 * @see createStatsPanel
		 * @see createTaxonomyPanel
		 * @see createFilterPanel
		 * @see createExportPanel
		 */
		public void createTabbedControl()
		{
			long start = System.nanoTime();
			controlFrame = new JFrame("Control Panel");
			controlFrame.setSize(800, 200);
			JTabbedPane control = new JTabbedPane();
			control.addTab("Stats", createStatsPanel());
			control.addTab("Taxonomy", createTaxonomyPanel());
			control.addTab("Filters", createFilterPanel());
			control.addTab("Export", createExportPanel());
			controlFrame.add(control);
			controlFrame.pack();
			controlFrame.setVisible(true);
			long end = System.nanoTime();
			System.out.println("create tabbed control: " + (end - start));
		}


		/**
		 * Creates the Stats panel for the tabed control panel. 
		 * The panel is split into two sections, with tables on scroll panels on each side.  The right panel contains the statistics for the filtered contigs and the left contains the stats for the selected contigs.
		 * @return a JPanel containing the statistical information of both selected contigs and filtered contigs
		 * @see statistics
		 * @see createTabbedControl()
		 * @see DefaultTableModel
		 * @see JSplitPane
		 * @see JScrollPane
		 */
		private JPanel createStatsPanel()
		{
			long start = System.nanoTime();
			JPanel statsPanel = new JPanel(new BorderLayout());

			this.stats = new DefaultTableModel(new String [] {"Statistic", "Value", "Percentage"}, 0);
			JTable statsTable = new JTable(this.stats);
			JPanel visibleStats = new JPanel();
			JScrollPane statsScroller = new JScrollPane(statsTable);
			visibleStats.add(statsScroller);
			//statsTable.setPreferredSize(new Dimension(300,300));
			visibleStats.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Filtered contigs: "), new EmptyBorder(4,4,4,4)));
			JSplitPane split = new JSplitPane(1);
			split.add(visibleStats);
			statistics(contigSet, this.stats);



			this.model = new DefaultTableModel(new String[] { "S. Statistic:", "S. Value:", "S. Percentage" }, 0);

			this.table = new JTable(this.model);


			JPanel p = new JPanel(new BorderLayout());
			JScrollPane scroller = new JScrollPane(this.table);
			p.add(scroller);
			p.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Selected Items: "), new EmptyBorder(4, 4, 4, 4)));

			split.add(p);
			statsPanel.add(split);
			long end = System.nanoTime();
			System.out.println("Stats panel" + (end - start));
			return statsPanel;
		}

		/**
		 * Creates a JPanel containing the legend for the plots.  The color is first with the taxonomy following, listed left to right in descending span order.  
		 * @return a JPanel containing the taxa with check boxes next to each taxa to hide/show the taxa in next to real time
		 * @see JCheckBox 
		 * @see createTabedControl()
		 */
		private JPanel createTaxonomyPanel()
		{
			long start = System.nanoTime();
			JPanel taxPanel = new JPanel();

			taxPanel.setLayout(new BoxLayout(taxPanel, BoxLayout.Y_AXIS));
			JPanel titlePanel = new JPanel();
			Label title = new Label("Taxa as ordered from largest to smallest spans \n Uncheck/check to hide/show specific taxa");
			titlePanel.add(title);
			taxPanel.add(titlePanel);

			//Un-select checkbox
			unselect = new JCheckBox ("Unselect all", false);

			unselect.addActionListener(new ActionListener()
			{
				XYItemRenderer renderer = ((XYPlot) mainChart.getPlot()).getRenderer();
				StackedXYBarRenderer x = (StackedXYBarRenderer) ((XYPlot)xSubChart.getPlot()).getRenderer();
				StackedXYBarRenderer y = (StackedXYBarRenderer) ((XYPlot)ySubChart.getPlot()).getRenderer();
				public void actionPerformed(ActionEvent ae)
				{

					for(int i = 0; i < checkBoxes.size(); i ++)
					{
						checkBoxes.get(i).setSelected(false);
						this.renderer.setSeriesVisible(i, false);
						this.x.setSeriesVisible(i, false);
						this.y.setSeriesVisible(i, false);
					}
				}
			});
			taxPanel.add(unselect);

			checkBoxPanel = new JPanel();
			JScrollPane statsScroller = new JScrollPane(checkBoxPanel);
			int half = taxaForDisplay.size()/3+1;
			GridLayout grid = new GridLayout(half, 2);

			checkBoxPanel.setLayout(grid);
			checkBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


			//Loop through taxa, associating color with taxa and adding checkbox
			for(int i = taxaForDisplay.size()-1; i >= 0; i --)
			{
				String name = taxaForDisplay.get(i);
				JCheckBox box = new JCheckBox(name, true);
				final int series = updateCount(i); //Trick to make sure series appears as final or effectively final  
				FillComponent component = new FillComponent(colors.get(taxaForDisplay.get(i)));
				checkBoxPanel.add(component);
				box.setActionCommand(name);


				box.addActionListener(new ActionListener()
				{
					XYItemRenderer renderer = ((XYPlot) mainChart.getPlot()).getRenderer();
					StackedXYBarRenderer x = (StackedXYBarRenderer) ((XYPlot)xSubChart.getPlot()).getRenderer();
					StackedXYBarRenderer y = (StackedXYBarRenderer) ((XYPlot)ySubChart.getPlot()).getRenderer();

					//Switches visibility of contig based on user action
					public void actionPerformed(ActionEvent e)
					{
						{

							boolean visible = this.renderer.getItemVisible(series, 0);
							this.renderer.setSeriesVisible(series, Boolean.valueOf(!visible));
							this.x.setSeriesVisible(series, Boolean.valueOf(!visible));
							this.y.setSeriesVisible(series, Boolean.valueOf(!visible));

							if(!visible)
								unselect.setSelected(false);
						}
					}

				});
				checkBoxes.add(box);
				checkBoxPanel.add(box);
			}


			taxPanel.add(statsScroller, BorderLayout.CENTER);
			long end = System.nanoTime();
			System.out.println("tabbed panel" + (end - start));
			return taxPanel;
		}

		/**
		 * Helper method to ensure that the current series in the for loop is "final or effectively final"
		 * 
		 * Created in response to change in Java SE 8
		 * 
		 * @param current integer position of for loop, allowing creation of ActionListeners for each element in loop
		 * @return input - 1
		 * @see update
		 * @see createTaxonomyPanel
		 */
		private int updateCount(int current)
		{
			return current --;
		}

		/**
		 * Splits the coverage domain into logspace and returns and array containing equally spaced bins
		 * Code modified from: http://www.codeproject.com/Questions/188926/Generating-a-logarithmically-spaced-numbers
		 * @param minY2 minimum coverage value in dataset or default minimum coverage
		 * @param maxY2 maximum coverage value in dataset plus padding
		 * @param logBins number of bins needed to split dataset
		 * @return an array containing evenly 
		 */
		private static double[] GenerateLogSpace(double minY2, double maxY2, int logBins)
		{
			long start = System.nanoTime();
			double logMin = Math.log10(minY2);
			double logMax = Math.log10(maxY2);
			double delta = (logMax - logMin) / logBins;
			System.out.println(delta);
			double accDelta = 0;
			double [] v = new double[logBins+1];
			for (int i = 0; i <= logBins; ++i)
			{
				v[i] = (double) Math.pow(10, logMin + accDelta);
				accDelta += delta;// accDelta = delta * i
			}
			long end = System.nanoTime();
			System.out.println("generate log space " + (end - start));
			return v;
		}

		/**
		 * Creates and returns the panel used for contig filtering.  Contains JSliders for minimum contig length, maximum E-Value and minimum coverage setting.
		 *  <p>
		 * Additionally, contains two drop-down menus for taxonomic rank selection and coverage library selection and two checkboxes allowing the user to change if the range of the subplots displays the GC and COV values as percentages or as absolute calculations. 
		 *<p>
		 *All changes, minus the sub-chart axis changes, are executed upon pressing the "Submit" button.  Alternatively, the charts can be reset by clicking the "Relaod" button.  
		 * @return a JPanel containing all filtering controls for Blobsplorer
		 */
		private JPanel createFilterPanel()
		{
			//Min length cutoff 
			long start = System.nanoTime();
			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
			JPanel length = new JPanel();
			Label lengthSliderLabel = new Label ("Minimum contig length:");
			length.add(lengthSliderLabel);
			this.lengthJSlider = new JSlider(0, 20000, 0);
			this.lengthJSlider.setMajorTickSpacing(1000);
			this.lengthJSlider.setMinorTickSpacing(500);
			this.lengthJSlider.setPaintTicks(true);
			this.lengthJSlider.setPaintLabels(true);
			this.lengthJSlider.setPreferredSize(new Dimension (800, 80));
			length.add(this.lengthJSlider);
			filterPanel.add(length, BorderLayout.CENTER);

			//Max E-Value cutof
			JPanel eValue = new JPanel();
			Label eValueSliderLabel = new Label ("Maximum E-Value (0:1] 1Ex:");
			this.eValueJSlider = new JSlider(-100, 0, 0);
			this.eValueJSlider.setMajorTickSpacing(10);
			this.eValueJSlider.setMinorTickSpacing(1);
			this.eValueJSlider.setPaintTicks(true);
			this.eValueJSlider.setPaintLabels(true);
			this.eValueJSlider.setPreferredSize(new Dimension(800, 80));
			eValue.add(eValueSliderLabel);
			eValue.add(this.eValueJSlider);
			filterPanel.add(eValue, BorderLayout.CENTER);

			JPanel covPanel = new JPanel();
			Label covSliderLabel = new Label ("Minimum coverage level:");
			this.covJSlider = new JSlider ((int)Math.round(defaultMinCov), (int)Math.round(maxCov), (int)Math.round(defaultMinCov));
			this.covJSlider.setMajorTickSpacing(1000);
			this.covJSlider.setMinorTickSpacing(500);
			this.covJSlider.setPaintTicks(true);
			this.covJSlider.setPaintLabels(true);
			this.covJSlider.setPreferredSize(new Dimension(800, 80));
			covPanel.add(covSliderLabel);
			covPanel.add(covJSlider);
			filterPanel.add(covPanel, BorderLayout.CENTER);


			JPanel pullDown = new JPanel();
			JLabel taxPullDownLabel = new JLabel("Select taxa level: ");
			pullDown.add(taxPullDownLabel);

			DefaultComboBoxModel<String> taxModel = new DefaultComboBoxModel<String>();
			for(int i = 0; i < taxaNames.length; i ++)
			{
				taxModel.addElement(taxaNames[i]);
			}
			final JComboBox<String> taxComboBox = new JComboBox<String>(taxModel);
			taxComboBox.setSelectedIndex(taxaIndex);
			pullDown.add(taxComboBox);

			JLabel covPullDownLabel = new JLabel("Select coverage library:");
			pullDown.add(covPullDownLabel);

			DefaultComboBoxModel<String> covModel = new DefaultComboBoxModel<String>();
			for(int i = 0; i < covLibraryNames.length; i ++)
			{
				covModel.addElement(covLibraryNames[i]);
			}
			final JComboBox<String> covComboBox = new JComboBox<String>(covModel);
			covComboBox.setSelectedIndex(covLibraryIndex);
			pullDown.add(covComboBox);

			filterPanel.add(pullDown);

			JPanel buttonPanel = new JPanel();
			JButton submit = new JButton("Submit");
			submit.setMnemonic(KeyEvent.VK_ENTER);
			submit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(taxComboBox.getSelectedIndex() != taxaIndex)
					{
						taxaIndex = taxComboBox.getSelectedIndex();
						history.add( "Taxa index changed to: " + taxaNames[taxaIndex] + "at index: " + taxaIndex);
						//updateClassifications();
					}
					if(covComboBox.getSelectedIndex() != covLibraryIndex)
					{
						covLibraryIndex = covComboBox.getSelectedIndex();
						history.add( "Coverage library changed to:  "+ covLibraryNames[covLibraryIndex] + "at index: " + covLibraryIndex);
					}

					update();

				}
			});

			JButton reload = new JButton("Reload");
			reload.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{

					reLoad();

				}
			});
			buttonPanel.add(reload);
			buttonPanel.add(submit);

			//Two check buttons to change how side panels are viewed
			JPanel checkBoxSubChartPanel = new JPanel();
			JCheckBox yPercentage = new JCheckBox("Show coverage graph as numerical count");
			yPercentage.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					StackedXYBarRenderer plotRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();
					plotRenderer.setBase(10);
					boolean isYPrecentage = yPercentage.isSelected();
					plotRenderer.setRenderAsPercentages(!isYPrecentage);
					((XYPlot) ySubChart.getPlot()).setRenderer(plotRenderer);
					ValueAxis y = ((XYPlot) ySubChart.getPlot()).getRangeAxis();
					if(isYPrecentage)
						y.setLabel("Count");
					else
						y.setLabel("Percentage");
				}
			});

			JCheckBox xPercentage = new JCheckBox("Show GC content as numerical counts");
			xPercentage.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					StackedXYBarRenderer plotRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();
					boolean isXPrecentage = xPercentage.isSelected();
					plotRenderer.setRenderAsPercentages(!isXPrecentage);
					((XYPlot)xSubChart.getPlot()).setRenderer(plotRenderer);
					ValueAxis x = ((XYPlot) xSubChart.getPlot()).getRangeAxis();
					if(isXPrecentage)
						x.setLabel("Count");
					else
						x.setLabel("Percentage");

				}
			});
			checkBoxSubChartPanel.add(xPercentage);
			checkBoxSubChartPanel.add(yPercentage);
			filterPanel.add(checkBoxSubChartPanel);
			filterPanel.add(buttonPanel);

			long end = System.nanoTime();
			System.out.println("Filter panel " + (end - start));
			return filterPanel;
		}

		/**
		 * Forces all taxa in taxaForDisplay to be visible.
		 */
		private void resetVisible()
		{
			long start = System.nanoTime();
			XYItemRenderer renderer = ((XYPlot) this.mainChart.getPlot()).getRenderer();
			StackedXYBarRenderer xRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();
			StackedXYBarRenderer yRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{

				renderer.setSeriesVisible(i, Boolean.valueOf(true));
				xRenderer.setSeriesVisible(i, Boolean.valueOf(true));
				yRenderer.setSeriesVisible(i, Boolean.valueOf(true));
			}
			long end = System.nanoTime();
			System.out.println("reset visible " + (end - start));
		}

		/**
		 * Resets Blobsplorer.  Method resets global variables, re-reads file, recalculates taxaForDisplay, re-makes the control window, recalculates colors and finally re-drawing plots.   
		 */
		private void reLoad()
		{
			long start = System.nanoTime();
			maxEValue = 1;
			minContigLength = 0;
			minCov = defaultMinCov;
			taxaIndex = 3;
			previousTaxa = 3;
			readFile();
			getTaxaForDisplay();
			//Re draw control panel
			controlFrame = null;
			createTabbedControl();

			// Fix colors
			colors = createColorHashMap();

			//Update chart data
			XYSeriesCollection newScatterData = createDataset();
			((XYPlot) this.mainChart.getPlot()).setDataset(newScatterData);
			DefaultTableXYDataset newXDataset = createXDataset();
			((XYPlot) this.xSubChart.getPlot()).setDataset(newXDataset);
			DefaultTableXYDataset newYDataset = createYDataset();
			((XYPlot) this.ySubChart.getPlot()).setDataset(newYDataset);
			long end = System.nanoTime();
			System.out.println("reload minus reset and stats " + (end - start)); 
			resetVisible();
			statistics(contigSet, this.stats);

		}


		/**
		 * Controls the exports for Blobsplorer.  Users are given the option of exporting four SVGs, a new blobplot.txt file containing visible contigs, contig IDs grouped by visible and hidden into two files and a filtering history file.
		 * If files already exist, they will not be overwritten.  
		 * @return the export panel
		 * @see reateTabbedControl
		 * @see Batik 1.7
		 */
		private JPanel createExportPanel() 
		{
			long start = System.nanoTime();
			JPanel exportPanel = new JPanel();
			final Text errorMessage = new Text();
			exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.Y_AXIS));

			JPanel svgPanel = new JPanel();
			JLabel svgLabel = new JLabel("Group SVG file path/name:");
			svgPanel.add(svgLabel);
			TextField svgField = new TextField(20);
			svgPanel.add(svgField);
			JButton create = new JButton ("Create SVG");
			create.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{					
					String svgName = svgField.getText();
					if(svgName == null)
						JOptionPane.showMessageDialog(null,"Please enter a new file name");

					Writer outMain = null;
					Writer outX = null;
					Writer outY = null;
					Writer outLegend = null;
					try 
					{

						Path fileSet = Paths.get(svgName);
						Path fileX = Paths.get(svgName+"CG");
						Path fileY = Paths.get(svgName +"COV");
						Path fileLegend = Paths.get(svgName+"LEGEND");
						//file already exists in location
						if(Files.exists(fileSet) || Files.exists(fileX) || Files.exists(fileY)|| Files.exists(fileLegend))
						{
							JOptionPane.showMessageDialog(null,"File already exists");
							return;
						}
						else
						{
							//Code modified from JFreeChart 1.0.17 documentation
							DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
							Document document = domImpl.createDocument(null, "svg", null);
							SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
							svgGenerator.getGeneratorContext().setPrecision(6);

							//folliwng three lines are for legend
							LegendTitle legend= new LegendTitle(mainChart.getPlot());
							legend.setPosition(RectangleEdge.BOTTOM);
							legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
							mainChart.addSubtitle(legend);
							mainChart.draw(svgGenerator, new Rectangle2D.Double(0,0,700,500), null);
							outMain = new java.io.OutputStreamWriter(new FileOutputStream(new File(svgName+".svg")));
							svgGenerator.stream(outMain, false);
							mainChart.removeLegend();


							legend.draw(svgGenerator, new Rectangle2D.Double(0,0, 100, 100), null);
							outLegend = new java.io.OutputStreamWriter(new FileOutputStream(new File(svgName+"LEGEND.svg")));
							svgGenerator.stream(outLegend, false);


							xSubChart.draw(svgGenerator, new Rectangle2D.Double(0,0,700,300), null);
							outX = new java.io.OutputStreamWriter(new FileOutputStream(new File(svgName + "GC.svg")));
							svgGenerator.stream(outX, false);


							ySubChart.draw(svgGenerator, new Rectangle2D.Double(0,0,500,300), null);
							outY = new java.io.OutputStreamWriter(new FileOutputStream(new File(svgName + "COV.svg")));
							svgGenerator.stream(outY, false);

						}
					} 

					catch (FileNotFoundException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SVGGraphics2DIOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (IOException e2) 
					{
						errorMessage.setText("IO exception");
						e2.printStackTrace();
					}
					catch (SecurityException e1)
					{
						e1.printStackTrace();
					}
					finally
					{
						try {
							if(outMain != null && outY != null && outX != null && outLegend != null)
							{
								outMain.close();
								outY.close();
								outX.close();
								outLegend.close();
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});

			svgPanel.add(create);
			exportPanel.add(svgPanel);

			JPanel filePanel = new JPanel();
			JLabel fileNameLabel = new JLabel ("File name for Blobplot document:");
			TextField fileField = new TextField(20);
			filePanel.add(fileNameLabel);
			filePanel.add(fileField);
			JButton export = new JButton("Export visible contigs");
			export.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("action performed in submit");
					BufferedWriter writer = null;
					try
					{
						String export = fileField.getText();

						if(export == null)
							JOptionPane.showMessageDialog(null,"Please enter a new file name");

						Path file = Paths.get(export);
						//file already exists in location
						if(Files.exists(file))
						{
							JOptionPane.showMessageDialog(null,"File already exists");
							return;
						}
						//file does not exist
						else
						{
							java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;
							writer = Files.newBufferedWriter(file, charset);
							writer.write(header);
							writer.newLine();
							for(int i = 0; i < taxaForDisplay.size(); i ++)
							{
								XYItemRenderer renderer = ((XYPlot) mainChart.getPlot()).getRenderer();

								if(renderer.getItemVisible(i, 0))
								{
									ArrayList<Contig> current = contigByTaxa.get(taxaForDisplay.get(i));
									for(int j = 0; j < current.size(); j++)
									{
										String contigLine= "";

										if(current.get(j).isVisible())
										{
											contigLine = current.get(j).getID() + "\t";
											contigLine += current.get(j).getLen() + "\t";
											contigLine += current.get(j).getGC() + "\t";
											double [] cov = current.get(j).getCov();
											for(int l = 0; l < cov.length; l ++)
											{
												contigLine += covLibraryNames[l] + "=" + cov[l] + ";";
											}

											contigLine += "\t";
											String [] tax = current.get(j).getTax();
											for (int k = 0; k < tax.length; k++)
											{
												contigLine += taxaNames[k]  + "=" + tax[k] + ";";
											}
											contigLine += "\t";
											contigLine += current.get(j).getEValue();

											if(i < taxaForDisplay.size() || (j < current.size()-1))
											{
												contigLine += "\n";
											}
											writer.write(contigLine, 0, contigLine.length());
											writer.flush();
										}
									}
								}
							}
						}
					}
					catch(InvalidPathException ip)
					{
						errorMessage.setText("Invalid path error");
						ip.printStackTrace();
					}
					catch(SecurityException se)
					{
						errorMessage.setText("Incorrect security permissions");
						se.printStackTrace();
					} catch (IOException e1) 
					{
						errorMessage.setText("IO exception");
						e1.printStackTrace();
					}
					finally
					{
						if(writer != null )
						{
							try
							{
								writer.close();
							}
							catch (IOException ioe)
							{
								ioe.printStackTrace();
							}
						}
					}
				}
			});

			filePanel.add(export);
			exportPanel.add(filePanel);

			JPanel IDPanel = new JPanel();
			JLabel IDPanelLabel = new JLabel ("Export visible contig IDs:");
			TextField contigField = new TextField(20);
			IDPanel.add(IDPanelLabel);
			IDPanel.add(contigField);
			JButton exportContig = new JButton("Export visible contigs");
			exportContig.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("action performed in submit");
					BufferedWriter writer = null;
					BufferedWriter excludeWriter = null;
					try
					{
						String export = contigField.getText();

						if(export == null)
							JOptionPane.showMessageDialog(null,"Please enter a new file name");

						Path file = Paths.get(export + "INCLUDED.txt");
						Path contamFile = Paths.get(export + "EXCLUDED.txt");
						//file already exists in location
						if(Files.exists(file) || Files.exists(contamFile))
						{
							JOptionPane.showMessageDialog(null,"File already exists");
							return;
						}
						//file does not exist
						else
						{
							java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;
							writer = Files.newBufferedWriter(file, charset);
							excludeWriter = Files.newBufferedWriter(contamFile, charset);
							String line = "";

							line = "# Included taxa at level " + taxaNames[taxaIndex] + ": ";
							writer.write(line, 0, line.length());
							writer.newLine();
							excludeWriter.write(line,0, line.length());
							excludeWriter.newLine();

							XYItemRenderer rendererVisible = ((XYPlot)mainChart.getPlot()).getRenderer();
							for(int i = 0; i < taxaForDisplay.size(); i ++)
							{
								line = "#" + taxaForDisplay.get(i);
								if(rendererVisible.getItemVisible(i,0))
								{
									writer.write(line, 0, line.length());
									writer.newLine();
								}
								else
								{
									excludeWriter.write(line,0, line.length());
									excludeWriter.newLine();
								}
							}

							line = "# Filtering criteria";
							writer.write(line, 0, line.length());
							excludeWriter.write(line, 0, line.length());
							line = "# Max E-Value: " + maxEValue + "\n";
							line += "# Min contig length: " + minContigLength+ "\n";
							line += "# Minimum coverage: " + minCov;
							writer.write(line, 0, line.length());
							writer.newLine();
							excludeWriter.write(line,0, line.length());
							excludeWriter.newLine();

							XYItemRenderer renderer;
							for(int i = 0; i < taxaForDisplay.size(); i ++)
							{
								renderer = ((XYPlot) mainChart.getPlot()).getRenderer();

								ArrayList<Contig> current = contigByTaxa.get(taxaForDisplay.get(i));

								if(renderer.getItemVisible(i, 0)) // taxa visible, class as subject
								{
									for(int j = 0; j < current.size(); j++)
									{
										String contigLine = "";

										contigLine = current.get(j).getID();
										if(j <current.size()  && i < taxaForDisplay.size())
										{
											contigLine += "\n";
										}

										if(current.get(j).isVisible()) // contig classed as subject 
										{
											writer.write(contigLine, 0, contigLine.length());
											writer.flush();
										}

										else //Contig classed as contaminant  
										{
											excludeWriter.write(contigLine, 0, contigLine.length());
											excludeWriter.flush();
										}
									}
								}
								else // taxa not visible, class as contaminant 
								{
									String contigLine = "";
									for(int j = 0; j < current.size(); j ++)
									{
										contigLine = current.get(j).getID();
										if(j <current.size()  && i < taxaForDisplay.size())
										{
											contigLine += "\n";
										}
										excludeWriter.write(contigLine, 0, contigLine.length());
										excludeWriter.flush();
									}
								}
							}
						}
					}
					catch(InvalidPathException ip)
					{
						errorMessage.setText("Invalid path error");
						ip.printStackTrace();
					}
					catch(SecurityException se)
					{
						errorMessage.setText("Incorrect security permissions");
						se.printStackTrace();
					} catch (IOException e1) 
					{
						errorMessage.setText("IO exception");
						e1.printStackTrace();
					}
					finally
					{
						if(writer != null && excludeWriter != null)
						{
							try
							{
								writer.close();
								excludeWriter.close();
							}
							catch (IOException ioe)
							{
								ioe.printStackTrace();
							}
						}
					}
				}
			});

			IDPanel.add(exportContig);
			exportPanel.add(IDPanel);

			JPanel historyPanel = new JPanel();
			JLabel historyFileName = new JLabel ("History file name:");
			TextField historyField = new TextField(20);
			historyPanel.add(historyFileName);
			historyPanel.add(historyField);
			JButton historyButton = new JButton("Create History");
			historyButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("action performed in submit");
					BufferedWriter writer = null;
					try
					{
						String historyExport = historyField.getText();

						if(historyExport == null)
							JOptionPane.showMessageDialog(null,"Please enter a new file name");

						Path file = Paths.get(historyExport);
						//file already exists in location
						if(Files.exists(file))
						{
							JOptionPane.showMessageDialog(null,"File already exists");
							return;
						}
						//file does not exist
						else
						{
							java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;
							writer = Files.newBufferedWriter(file, charset);
							for(int i = 0; i < history.size(); i ++)
							{
								writer.write(history.get(i), 0, history.get(i).length());
								writer.newLine();
							}
						}
					}
					catch(InvalidPathException ip)
					{
						errorMessage.setText("Invalid path error");
						ip.printStackTrace();
					}

					catch(SecurityException se)
					{
						errorMessage.setText("Incorrect security permissions");
						se.printStackTrace();
					} 
					catch (IOException e1) 
					{
						errorMessage.setText("IO exception");
						e1.printStackTrace();
					}
					finally
					{
						if(writer != null )
						{
							try
							{
								writer.close();

							}
							catch (IOException ioe)
							{
								ioe.printStackTrace();
							}
						}
					}
				}
			});

			historyPanel.add(historyButton);
			exportPanel.add(historyPanel);

			return exportPanel;
		}

		/**
		 * Creates the panel for handling mouse actions, data extension and user selections for the TAGC plot.  
		 * @return the central panel containing the TAGC plot
		 */
		public JPanel createMainPanel()
		{
			XYDataset xydataset = createDataset();
			DatasetSelectionExtension<XYCursor> datasetExtension = new XYDatasetSelectionExtension(xydataset);

			datasetExtension.addChangeListener(this);

			this.mainChart = createChart(xydataset, datasetExtension);
			this.mainChart.addChangeListener(this);
			XYPlot plot = (XYPlot) this.mainChart.getPlot();

			this.dataset = (XYSeriesCollection) plot.getDataset();
			ChartPanel panel = new ChartPanel(this.mainChart);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);

			RegionSelectionHandler selectionHandler = new RectangularRegionSelectionHandler();
			panel.addMouseHandler(selectionHandler);
			panel.addMouseHandler(new MouseClickSelectionHandler());
			panel.removeMouseHandler(panel.getZoomHandler());

			DatasetExtensionManager dExManager = new DatasetExtensionManager();
			dExManager.registerDatasetExtension(datasetExtension);
			panel.setSelectionManager(new EntitySelectionManager(panel, new Dataset[] {xydataset}, dExManager));

			
			return panel;
		}

		/**
		 * Changes X and Y subplots to zoom with the main plot.   
		 */
		public void chartChanged(ChartChangeEvent event)
		{
						XYPlot plot = (XYPlot) this.mainChart.getPlot();
			if(!plot.getDomainAxis().getRange().equals(this.lastXRange))
			{
				this.lastXRange = plot.getDomainAxis().getRange();
				XYPlot plotX = (XYPlot) this.xSubChart.getPlot();
				plotX.getDomainAxis().setRange(this.lastXRange);
			}
			if(!plot.getRangeAxis().getRange().equals(this.lastYRange))
			{
				this.lastYRange = plot.getRangeAxis().getRange();
				XYPlot plotY = (XYPlot) this.ySubChart.getPlot();
				plotY.getDomainAxis().setRange(this.lastYRange);
			}
			
		}

		/**
		 * Returns a JFreeChart chart containing a scatter plot with the GC value on a numbered x axis and COV on a log scale y axis. The plot responds to zooming and panning, while allowing the dataset to respond to user selection of contigs.
		 *  
		 * @param dataset an XYSeriesCollection containing data to create scatter plot
		 * @param event the data set extension mechanism to allow user selection
		 * @return the JFreeChart scatter plot with selection listener
		 * @see JFreeChart
		 * @see ChartFactory
		 */
		private JFreeChart createChart(XYDataset dataset, DatasetSelectionExtension<XYCursor> event)
		{
			System.out.println("In createChart");
			long start = System.nanoTime();
			this.dataset = (XYSeriesCollection) dataset;
			JFreeChart chart = ChartFactory.createScatterPlot("", "GC", "COV", this.dataset);

			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setNoDataMessage("No data available");
			plot.setDomainPannable(true);
			plot.setRangePannable(true);
			NumberAxis xAxis = new NumberAxis("GC");
			xAxis.setLowerMargin(0.0);
			xAxis.setUpperMargin(0.0);
			xAxis.setRange(0.0, 1.0);
			plot.setDomainAxis(xAxis);
			LogAxis yAxis = new LogAxis("COV");
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			yAxis.setLowerMargin(0.0);
			yAxis.setUpperMargin(0.0);
			plot.setRangeAxis(yAxis);

			event.addChangeListener(plot);
			long end = System.nanoTime();
			System.out.println("create chart " + (end - start));
			return chart;
		}

		/**
		 * Controls the how Blobsplorer responds to filtering actions requested by the user.  The update() achieves this by re-populating the central dataset accordingly according to the new parameters and by updating all references to this data. 
		 * The user initiated changes are registered to the rest of the program through the global variables, minContigLength, maxEValue and minCov.  
		 * Updates minContigLength, maxEValue and minCov are initiated in response to options available in  {@link createFilterPanel()}.  
		 * Changes are retrieved from JSliders and used to alter which contigs are displayed and how they are classified (e.g. as Not annotated or as a given taxa provided by Blast).  For minContigLength and minContigLength, the new values are taken directly from the JSlider.  
		 * In the case of the eValueJSlider, the integer number corresponds to the exponent value and the actual maximum E-Value accepted is calculated by raising 10 to the power of the number given from the JSlider.
		 * In order to allow the program to promptly respond to changes in classification parameters, contigs are re-read in from the stored file and classified and/or excluded based on the values of minContigLength, minCov, and maxEValue.
		 *
		 * {@link reReadFile is called to re read in and parse the contigs from the file stored in memory, taking into account the new classifications factors provided by the user. {@link getTaxaForDisplay()} is called to sort the newly altered data set based on span. 

		 * <p>
		 *  Other updates include adding a record of all changes to the  history ArrayList, ensuring that the color of each taxa remains the same post re-classification and/or exclusion of taxa (i.e. the 'Not annotated' series that was colored as light grey before updates is still light grey after updates).
		 *  Additionally, the 'Visible Statistics' panel in {@link createStatsPanel()}
		 * 

		 * @see JSlider  
		 * @see JFreeChart
		 * @see XYPlot
		 * @see XYSeriesCollection
		 * @see DefaultTableXYDataset
		 * @see XYItemRenderer
		 * @see StackedXYBarRenderer
		 * @see DatasetSelectionExtension
		 * @see ChartPanel
		 * @see JCheckBox
		 * 
		 */
		public void update ()
		{
			System.out.println("Update");
			long start = System.nanoTime();
			minContigLength = this.lengthJSlider.getValue();
			System.out.println("**** min contig length: " + minContigLength);
			history.add( "Minimum contig length changed to: " + minContigLength);

			double exponent = this.eValueJSlider.getValue();
			maxEValue = 1* Math.pow(10,-exponent);

			history.add( "Maximum E-Value changed to: " + maxEValue);

			minCov = this.covJSlider.getValue();

			history.add( "Minumum coverage changed to: " + minCov);

			reAllocate();
			XYItemRenderer oldRenderer = ((XYPlot) this.mainChart.getPlot()).getRenderer();;
			HashMap<String, Boolean> previouslyVisible = new HashMap<String, Boolean>();
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				boolean visible = true;
				if(!oldRenderer.getItemVisible(i, 0))
					visible = false;
				previouslyVisible.put(taxaForDisplay.get(i), visible);
			}
			getTaxaForDisplay();

			XYSeriesCollection newScatterData = createDataset();
			((XYPlot) this.mainChart.getPlot()).setDataset(newScatterData);
			DefaultTableXYDataset newXDataset = createXDataset();
			((XYPlot) this.xSubChart.getPlot()).setDataset(newXDataset);
			DefaultTableXYDataset newYDataset = createYDataset();
			((XYPlot) this.ySubChart.getPlot()).setDataset(newYDataset);

			if(previousTaxa != taxaIndex)
			{
				colors = createColorHashMap();
				resetVisible();
				unselect.setSelected(false);
			}

			//Ensure color changes wiht data
			XYItemRenderer r = ((XYPlot) this.mainChart.getPlot()).getRenderer();
			StackedXYBarRenderer yRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();
			StackedXYBarRenderer xRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				Color paintMe = colors.get(taxaForDisplay.get(i));
				r.setSeriesPaint(i, paintMe);
				yRenderer.setSeriesPaint(i, paintMe);
				xRenderer.setSeriesPaint(i, paintMe);
				boolean visible = true;
				if(previouslyVisible.containsKey(taxaForDisplay.get(i)))
					visible =  previouslyVisible.get(taxaForDisplay.get(i));

				r.setSeriesVisible(i, visible);
				yRenderer.setSeriesVisible(i, visible);
				xRenderer.setSeriesVisible(i, visible);

			}
			((XYPlot) xSubChart.getPlot()).setRenderer(xRenderer);
			((XYPlot) ySubChart.getPlot()).setRenderer(yRenderer);

			//ensure selection changes with change in data 
			((XYPlot) this.mainChart.getPlot()).setRenderer(r);
			this.dataset = (XYSeriesCollection) ((XYPlot) this.mainChart.getPlot()).getDataset();
			DatasetSelectionExtension<XYCursor> datasetExtension = new XYDatasetSelectionExtension(newScatterData);

			datasetExtension.addChangeListener(this);
			DatasetExtensionManager dExManager = new DatasetExtensionManager();
			dExManager.registerDatasetExtension(datasetExtension);
			chartPanel.setSelectionManager(new EntitySelectionManager(chartPanel, new Dataset[] { newScatterData }, dExManager));


			statistics(contigSet, this.stats);

			//ensure taxonomy checkboxes change with data
			checkBoxPanel.removeAll();
			//reset layout
			int half = taxaForDisplay.size()/+1;
			GridLayout grid = new GridLayout(half, 2);
			checkBoxPanel.setLayout(grid);
			checkBoxes.clear();
			//XYItemRenderer renderer = ((XYPlot) this.mainChart.getPlot()).getRenderer();
			for(int i = taxaForDisplay.size()-1; i >= 0; i --)
			{
				JCheckBox box;
				String name = taxaForDisplay.get(i);
				boolean visible = true;
				if(previouslyVisible.containsKey(name))
					visible = previouslyVisible.get(name);
				box = new JCheckBox(name, visible);

				final int series = updateCount(i);
				FillComponent component = new FillComponent(colors.get(taxaForDisplay.get(i)));

				checkBoxPanel.add(component);
				box.setActionCommand(name);
				//set action listener to respond to changes is JCheckBox selection.  
				box.addActionListener(new ActionListener()
				{
					XYItemRenderer renderer = ((XYPlot) mainChart.getPlot()).getRenderer();
					StackedXYBarRenderer rendererX = (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();
					StackedXYBarRenderer rendererY = (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();;
					public void actionPerformed(ActionEvent e)
					{
						if(e.getActionCommand().equals(name))
						{
							boolean visible = this.renderer.getItemVisible(series, 0);
							this.renderer.setSeriesVisible(series, Boolean.valueOf(!visible));
							this.rendererX.setSeriesVisible(series, Boolean.valueOf(!visible));
							this.rendererY.setSeriesVisible(series, Boolean.valueOf(!visible));

						}
					}

				});
				checkBoxes.add(box);
				checkBoxPanel.add(box);
			}
			previousTaxa = taxaIndex;
		}

		/**
		 * Orders taxa based on taxa's span 
		 */
		private static void getTaxaForDisplay()
		{
			System.out.println("in getTaxaForDisplay");
			long start = System.nanoTime();
			ArrayList<String> temp = new ArrayList<String>();
			temp.addAll(contigByTaxa.keySet());
			taxaForDisplay = sortBySpan(temp);
			long end = System.nanoTime();
			System.out.println("get top taxa " + (end - start));
		}


		/**
		 * Creates a DefaultTableXYDataset for COV subplot
		 * @return  dataset for COV subplot 
		 */
		private static DefaultTableXYDataset createYDataset()
		{
			System.out.println("in createYDataset");
			long start = System.nanoTime();
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			double total = maxY+10;
			double [] bins;
			//if (minY < defaultEValue)
			bins = GenerateLogSpace(1E-5, maxY, 500);
			//else
			//bins = GenerateLogSpace(minY, maxY, 500);
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);
				double [] segregated = new double [501];
				for(int j = 0; j < contigByTaxa.get(taxaForDisplay.get(i)).size(); j ++)
				{
					double cov = contigByTaxa.get(taxaForDisplay.get(i)).get(j).getCov()[covLibraryIndex];
					for(int k = 0; k < bins.length; k ++)
					{
						if(cov < bins[k])
						{
							segregated[k] += 1;
							break;
						}
					}
				}

				for(int l = 0; l< bins.length; l ++)
				{
					s.add(bins[l], segregated[l]);
				}
				dataset.addSeries(s);
			}
			long end = System.nanoTime();
			System.out.println("createY " + (end - start));
			return dataset;

		}

		
		/**
		 * Creates the dataset to be used by the plot on the X axis, the GC subplot
		 * @return the dataset for the X axis plot 
		 */
		private static DefaultTableXYDataset createXDataset()
		{
			System.out.println("In createXDataset");
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);

				double [] bins = segregateByBucket(contigByTaxa.get(taxaForDisplay.get(i)), 100, .01);
				for(int j = 0; j < bins.length; j ++)	
				{
					s.add(j*.01, bins[j]);
				}
				dataset.addSeries(s);
			}
			return dataset;
		}



		/**
		 * Sorts taxa names by their total spans in descending order 
		 * @param unsorted unsorted ArrayList of taxa names
		 * @return an ArrayList of taxa sorted by span 
		 */
		private static ArrayList<String> sortBySpan (ArrayList<String>unsorted)
		{
			ArrayList<String> sorted = new ArrayList<String>();
			for(int i = 0; i < unsorted.size(); i ++)
			{
				sorted = sortSpan(sorted, unsorted.get(i));
			}
			Collections.reverse(sorted); //First in last out in terms of rendering, 
			return sorted;
		}

		/**
		 * Takes a sorted ArrayList and the element to be added, placing the new element in the sorted ArrayList
		 * A helper method for sortBySpan(ArrayList<String> unsorted. 
		 * @param addToMe sorted ArrayList of taxa names 
		 * @param addMe name of taxa to be placed in addToMe based on total span
		 * @return 
		 */
		private static ArrayList<String> sortSpan(ArrayList<String> addToMe, String addMe)
		{
			System.out.println("in sortSpan");
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




		/***************************************
		 *Display statistics for selection of contigs
		 *
		 ***************************************/

		/**
		 * Sums the lengths of the contigs
		 * @param selection an ArrayList of contigs
		 * @return the sum of contigs' lengths
		 */
		public int getSpan (ArrayList<Contig> selection)
		{
			System.out.println("in getSpan");
			long start = System.nanoTime();
			int span = 0;
			for (int i = 0; i < selection.size(); i ++)
			{
				span += selection.get(i).getLen();
			}
			long end = System.nanoTime();
			System.out.println("get span" + (end - start));
			return span;
		}

		/**
		 * Calculates the mean length of the contigs
		 * @param selection an ArrayList of contigs
		 * @return mean length of the contigs
		 */
		public double getMeanLength (ArrayList<Contig> selection)
		{
			System.out.println("in getMeanLength");
			double total = 0;
			for(int i = 0; i < selection.size(); i++)
			{
				total += selection.get(i).getLen();
			}

			return total/selection.size();
		}

		/**
		 * Calculates the median length of the contigs
		 * @param selection ArrayList of contigs
		 * @return the median length of the contigs
		 */
		public double getMedianLength(ArrayList<Contig> selection)
		{
			System.out.println("in getMedianLength");
			long start = System.nanoTime();
			if(selection.size() == 0)
				return -1.0;

			Collections.sort(selection, new ContigComparator());

			if(selection.size() == 1)
				return selection.get(0).getLen()*1.0;
			else if (selection.size() == 2)
				return (selection.get(0).getLen() + selection.get(1).getLen())/2.0;
			else if(selection.size() > 3)
			{
				if(selection.size()%2 != 0)
				{
					int index = (selection.size()/2) + 1;
					return selection.get(index).getLen()*1.0;
				}
				else
				{
					int index1 = (selection.size()/2);
					int index2 = index1 + 1;
					return (selection.get(index1).getLen() + selection.get(index2).getLen())/2.0;
				}
			}

			return -10; // check that All possible medians are caught
		}

		/**
		 * Calculates the mean GC content value of the contigs
		 * @param selection ArrayList of contigs
		 * @return mean GC content
		 */
		public double getMeanGC(ArrayList<Contig> selection)
		{
			double total = 0;
			for (int i = 0; i < selection.size(); i ++)
			{
				total += selection.get(i).getGC();
			}
			return total/selection.size();
		}


		/**
		 * 
		 * @param selection
		 * @return
		 */
		public int calculateN50(ArrayList<Contig> selection)
		{
			Collections.sort(selection, new ContigComparator().reversed());
			int total = 0;
			for(int i = 0; i < selection.size(); i ++)
			{
				total += selection.get(i).getLen();
			}
			double border = (total)/2.0;
			int con = 0;
			for(int i = 0; i < selection.size(); i ++)
			{
				if(con > border)
				{
					return selection.get(i).getLen();
				}
				con += selection.get(i).getLen();
			}

			return -1;
		}

		/**
		 * Separates a mixed group of contigs by their Taxa and returns a 2D array with the taxa name and the span of that taxa
		 * @param selected ArrayList of contigs which need to be classified by taxa
		 * @return a 2D array containing taxa name and the taxa's span as an integer
		 */
		private String [] [] separateByTaxa(ArrayList<Contig> selected) 
		{
			HashMap<String, ArrayList<Contig>> grouped = new HashMap<String, ArrayList<Contig>>();
			HashMap<String, Integer> groupedSpan = new HashMap<String, Integer>();
			String [] [] groupedSpanTotalsByTaxa;

			for(int i = 0; i < selected.size(); i ++)
			{
				if(grouped.containsKey(selected.get(i).getTax()[taxaIndex]))//Key already exists in Map
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
				groupedSpanTotalsByTaxa[count][1] = Integer.toString(groupedSpan.get(taxa));
				count ++;
			}
			return groupedSpanTotalsByTaxa;
		}

		/**
		 * The control method for calculating and writing to the Stats Panel
		 * @param selected ArrayList of contigs selected by user
		 * @param display The table to which the statistics will be displayed
		 */
		private void statistics(ArrayList<Contig> selected, DefaultTableModel display)
		{
			int number = selected.size();
			DecimalFormat df = new DecimalFormat("#.##");
			DecimalFormat spanDf = new DecimalFormat("#.####");
			DecimalFormat wholeNumber = new DecimalFormat("#,###.###");
			while(display.getRowCount() > 0)
			{
				display.removeRow(0);
			}

			int n50 = calculateN50(selected);
			double meanGC = getMeanGC(selected);
			double medianLen = getMedianLength(selected);
			double meanLen = getMeanLength(selected);
			int span = getSpan(selected);
			String selectedSpan = wholeNumber.format(span) + "/" + wholeNumber.format(totalLength);//Integer.toString(span) + "/" + Integer.toString(totalLength);
			display.addRow(new Object[] {"Mean length: ", df.format(meanLen), ""});
			display.addRow(new Object[] {"Median length: ", df.format(medianLen), ""});
			display.addRow(new Object[] {"Mean GC: ", df.format(meanGC), ""});
			display.addRow(new Object[] {"Span: ", selectedSpan, df.format(((span*1.0)/totalLength)*100)});
			display.addRow(new Object[] {"No. of contigs displayed/ Total: ", wholeNumber.format(number) + "/" + wholeNumber.format(totalNumberOfContigs), df.format(((number*1.0)/totalNumberOfContigs)*100) });
			display.addRow(new Object[] {"N50: ", new Integer(n50), ""}); 
			display.addRow(new Object[] {"", }); 
			display.addRow(new Object[] {"Span breakdown: ", "Taxa/Selection", "Percentage"}); 

			String[][] selectedContigByTaxa = separateByTaxa(selected);
			for(int i = 0; i < selectedContigByTaxa.length; i ++)
			{
				int selectedSpan1 = Integer.parseInt(selectedContigByTaxa[i][1]);
				display.addRow(new Object[] {selectedContigByTaxa[i][0], new String(wholeNumber.format(selectedSpan1)+"/"+wholeNumber.format(span)), spanDf.format((Double.parseDouble(selectedContigByTaxa[i][1])/span)*100)}); 
			}

		}

		/**
		 * Re-allocates contigs based on filtering criterai and repopualates HashMaps accordingly
		 */
		public static void reAllocate()
		{
			String tax;
			contigByTaxa.clear();
			taxLevelSpan.clear();
			contigSet.clear();
			for(int i = 0; i < contigMaster.size(); i ++)
			{
				Contig addMe = contigMaster.get(i);
				if(addMe.getLen() >= minContigLength && addMe.getCov()[covLibraryIndex] >= minCov && addMe.getEValue() < maxEValue)
				{
					tax = addMe.getTax()[taxaIndex];
					addMe.setVisibility(true);
				}
				else if (addMe.getLen() >= minContigLength && addMe.getCov()[covLibraryIndex] >= minCov && addMe.getEValue() >=  maxEValue)
				{
					tax = "Not annotated";
					addMe.setVisibility(true);
				}
				else
				{
					tax = "";
					addMe.setVisibility(false);
				}


				if(addMe.isVisible())
				{

					contigSet.add(addMe); //add Contig to Arraylist
					if(tax.equals(""))
						System.out.println("Error in sorting");
					totalLength += addMe.getLen();
					//find smallest evalue in data
					if(minFoundEValue > addMe.getEValue())
					{
						minFoundEValue= addMe.getEValue();
					}

					if(maxCov < addMe.getCov()[covLibraryIndex])
					{
						maxCov = addMe.getCov()[covLibraryIndex];
					}
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


		/*
		 * 
		 */
		/**
		 * Reads file passed in from JavaFX scene in Test.java
		 * parses each line of the file to generate a Contig
		 * Passes on replacement value for "N/A" result, E values to the user entered eValue, to parseContig(String text, double eValue) 
		 * Adds new contig to ArrayList of existing contigs 
		 * Builds HashMap<String, ArrayList<Contig>> contigByTaxa, where Contigs are grouped by taxa
		 * Builds HashMap <String, Integer> taxLevelSpan, where the span of each taxa is calculated 
		 * @return false if error occures and true otherwise
		 */
		public static boolean readFile()
		{
			history = new ArrayList<String>();
			BufferedReader bufferedReader = null;
			boolean correct = true;
			try 
			{
				bufferedReader = new BufferedReader(new FileReader(file));
				header = bufferedReader.readLine();

				String text;
				while ((text = bufferedReader.readLine()) != null) 
				{
					Contig addMe = parseContig(text, defaultEValue);
					if (addMe == null)
					{
						System.out.println("Unable to parseContig");

					}
					else
					{	
						contigMaster.add(addMe);
						contigSet.add(addMe); //add Contig to Arraylist
						String tax = addMe.getTax()[taxaIndex];
						totalLength += addMe.getLen();
						//find smallest evalue in data
						if(minFoundEValue > addMe.getEValue())
						{
							minFoundEValue= addMe.getEValue();
						}

						if(maxCov < addMe.getCov()[covLibraryIndex])
						{
							maxCov = addMe.getCov()[covLibraryIndex];
						}
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
				if(contigMaster.size() == 0)
				{
					System.out.println("Unable to detect any contigs, exiting program");
					System.exit(0);
				}
				history.add("Initial default minimum coverage: " + defaultMinCov);
				history.add("Initial default E-Value: " + defaultEValue);
				history.add("Initial taxonomic level: " + taxaNames[taxaIndex]);
				history.add("Initial coverage library: " + covLibraryNames[covLibraryIndex]);
				totalNumberOfContigs = contigSet.size();
			} 
			catch (FileNotFoundException ex) 
			{
				correct = false;
				Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
			} 
			catch (IOException ex) 
			{
				correct = false;
				Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
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
					Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
				}
			} 
			return correct;
		}

	
		/**
		 * Parses out Contrig from sinle line of text and replaces evalue "N/A" results with the user defined double
		 * Parsing structure based on https://github.com/blaxterlab/blobology/tree/master/dev format as of 18/06/14
		 * @param newEntry the current line from the file which needs parsing
		 * @param userDefinedEValue the default E-value from the user
		 * @return a Contig Object or null if the Contig cannot be parsed
		 */
		private static Contig parseContig(String newEntry, double userDefinedEValue)
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
			Contig contigToAdd = null;
			String key;
			String tempString;
			double value;
			int count = 0;
			int taxCount = 0;
			StringTokenizer parse;
			try{
			st = new StringTokenizer(newEntry, "\t");
			id = st.nextToken();
			len = Integer.parseInt(st.nextToken());
			gc = Double.parseDouble(st.nextToken());
			covString = st.nextToken();
			covST = new StringTokenizer(covString, ";");
			double [] cov = new double [covST.countTokens()];
			covLibraryNames = new String [covST.countTokens()];
				while(covST.hasMoreTokens())
				{
					tempString = covST.nextToken();
					parse = new StringTokenizer(tempString, "=");
					if(parse.countTokens() % 2 == 0  && parse.countTokens() != 0)
					{
						key = parse.nextToken();
						covLibraryNames[count] = key;
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
				String [] tax = new String [taxST.countTokens()];
				taxaNames = new String [taxST.countTokens()];
				String keyValue;
				while(taxST.hasMoreTokens())
				{
					tempString = taxST.nextToken();
					parse = new StringTokenizer(tempString, "=");
					if(parse.countTokens() % 2 == 0)
					{
						key = parse.nextToken();
						taxaNames[taxCount] = key;
						keyValue = parse.nextToken().replace(";","");
						tax[taxCount] = keyValue;
						taxCount ++;
					}
					else
					{
						JOptionPane.showMessageDialog(null,"Incorrect number of key value pair entries");
						return null;
					}
				}
				temp = st.nextToken();
				if (temp.contains("N/A"))
					eValue = userDefinedEValue;
				else
				{
					eValue = Double.parseDouble(temp);
				}

				contigToAdd = new Contig(id, len, gc, cov, tax, eValue);
			}
			catch(NumberFormatException ne)
			{
				ne.printStackTrace();
				return null;
			}
			catch (NoSuchElementException nsee)
			{
				System.out.println("Unable to parse contig");
				return null;
			}

			return contigToAdd;
		}

		/**
		 * Associates taxa with color
		 * @return a HashMap associating a taxa with a color
		 */
		private HashMap<String, Color> createColorHashMap()
		{
			HashMap<String, Color> taxaByColor = new HashMap<String, Color>();
			Color [] paint;

			if (basicColors.length >= taxaForDisplay.size())
				paint = Arrays.copyOfRange(basicColors, 0, taxaForDisplay.size());
			else
			{
				paint = new Color[taxaForDisplay.size()];
				for(int i = 0; i < taxaForDisplay.size(); i ++)
				{
					int index = i%basicColors.length;
					paint[i] = basicColors[index];
				}
			}
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				if(taxaForDisplay.get(i).equals("Not annotated"))
				{
					taxaByColor.put("Not annotated", Color.LIGHT_GRAY);
				}
				else
				{
					taxaByColor.put(taxaForDisplay.get(i), paint[i]);
				}
			}
			return taxaByColor;
		}

		/**
		 * Creates array containing counts of how many contigs fall into each bin
		 * @param series Group of contigs which need to be binned
		 * @param numBins number of bins
		 * @param splitFactor distance between bins
		 * @return
		 */
		private static double [] segregateByBucket(ArrayList<Contig> series, int numBins, double splitFactor)
		{
			double [] collection = new double [numBins];
			for(int i = 0; i < series.size(); i ++)
			{
				double value = -1;

				value = series.get(i).getGC();

				for(int j = 0; j < collection.length; j ++)
				{
					if (value <= j*splitFactor)
					{
						collection[j] += 1;
						break;
					}

				}
				if (value > numBins*splitFactor)
					System.out.println("Out of bounds");
			}
			return collection;
		}

		/**
		 * Creates dataset for the TAGC plot using 
		 * @return dataset for TAGC plot 
		 * 
		 */
		private static XYSeriesCollection createDataset() 
		{

			// ArrayList containing top taxa which need to be displayed
			XYSeriesCollection dataset = new XYSeriesCollection();

			for(int i = 0; i < taxaForDisplay.size(); i ++) //loop through arrayLists associated with top taxa by span
			{
				String taxa = taxaForDisplay.get(i);
				ArrayList<Contig> taxaSet = contigByTaxa.get(taxa);
				XYSeries series = new XYSeries(taxa, false); 
				for(int j = 0; j < taxaSet.size(); j ++)
				{	
					//as 0 cannot be displayed on log scale, set libraries where coverage is 0 to minCov until
					if ((double)taxaSet.get(j).getCov()[covLibraryIndex] == 0)
					{
						taxaSet.get(j).setCovAtPos(covLibraryIndex, defaultMinCov);
					}

					series.add(taxaSet.get(j).getGC(), taxaSet.get(j).getCov()[covLibraryIndex]);



					//Calculate max and min of X (GC)
					if (taxaSet.get(j).getGC() > maxX)
						maxX = taxaSet.get(j).getGC();
					else if (taxaSet.get(j).getGC() < minX)
						minX = taxaSet.get(j).getGC();
					//Calculate max and min of Y (COV)
					if (taxaSet.get(j).getCov()[covLibraryIndex] > maxY)
						maxY = taxaSet.get(j).getCov()[covLibraryIndex];
					else if (taxaSet.get(j).getCov()[covLibraryIndex] < minY)
						minY = taxaSet.get(j).getCov()[covLibraryIndex];
				}
				dataset.addSeries(series);
			}
			return dataset;

		}


		@Override
		public void keyTyped(KeyEvent e) 
		{
			System.out.println("key typed");
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
				update();
		}


		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub

		}


		@Override
		public void keyReleased(KeyEvent e) 
		{
			System.out.println("key released");
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
				update();
		}


		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub

		}



		/**
		 * Colelcts Contigs selected within user areas and sends the list to <code>statistics </code> for stats calculations.  
		 * @param event List of XYCursor points in selected area
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

			ArrayList<Contig> selected = new ArrayList<Contig>();
			while(itr.hasNext())
			{
				XYCursor dc = (XYCursor)itr.next();
				Comparable seriesKey = this.dataset.getSeriesKey(dc.series);

				ArrayList<Contig> taxa = contigByTaxa.get(seriesKey);

				selected.add(taxa.get(dc.item));
			}
			if(selected.size() > 0)
				statistics(selected, this.model);
		}
	}
	
	/**
	 * 
	 * @param file input file in form of blobplot.text
	 * @param covNum default coverage value if set to 0
	 * @param eValue default E-Value if set to N/A
	 */
	public Charts(File file, int covNum, double eValue)
	{
		super("Blobsplorer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = createDemoPanel(file, covNum, eValue);
		setContentPane(content);
	}


	/**
	 * Creates the main Panel for Blobsplorer. 
	 * @param file the input file 
	 * @param covLevel the default coverage if set to 0
	 * @param eValue the default E-Value if N/A
	 * @return the JPanel containing the scatter plot and two sub-plots
	 */
	public static  JPanel createDemoPanel(File file, int covLevel, double eValue) 
	{
		return new BlobPanel(file, covLevel, eValue);
	}

}







