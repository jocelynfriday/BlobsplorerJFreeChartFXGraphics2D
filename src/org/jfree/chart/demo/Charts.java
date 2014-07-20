package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Paint;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
//import org.jfree.chart.panel.selectionhandler.EntitySelectionManager;
//import org.jfree.chart.panel.selectionhandler.FreePathSelectionHandler;
//import org.jfree.chart.panel.selectionhandler.MouseClickSelectionHandler;
//import org.jfree.chart.panel.selectionhandler.RectangularRegionSelectionHandler;
//import org.jfree.chart.panel.selectionhandler.RegionSelectionHandler;

import org.jfree.chart.panel.selectionhandler.EntitySelectionManager;
import org.jfree.chart.panel.selectionhandler.MouseClickSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RectangularRegionSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RegionSelectionHandler;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.item.IRSUtilities;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.NumberCellRenderer;
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

public class Charts extends ApplicationFrame{
	static class BlobPanel extends DemoPanel implements ItemListener, ChangeListener, ChartChangeListener, KeyListener , SelectionChangeListener<XYCursor>
	{
		//private XYDataset dataset;
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
		private JTable table;
		private LegendTitle legend;
		private static ArrayList<String> history;
		private HashMap<String, Color>  colors;


		private static File file;
		private static double defaultEValue;
		private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
		private static int taxaIndex = 2;
		private static int covLibraryIndex = 0; // which cov library to use //** needs to be added to UI
		private static HashMap<String, ArrayList<Contig>> contigByTaxa = new HashMap<String, ArrayList<Contig>>();
		private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
		private static int totalLength = 0;
		private static double maxEValue = 1.0;
		private static int minContigLength = 0;
		private static double defaultMinCov = 1E-5;
		private static double minCov;
		private static double maxCov = 0;
		private static double minX = 0;
		private static double maxX = 0;
		private static double minY = 0;
		private static double maxY = 0;
		private static int totalNumberOfContigs = 0;
		private static double minFoundEValue= 0;
		private static final int numOfBuckets = 200;
		private static String header = "";
		private static String[] taxaNames;
		private static String [] covLibraryNames;
		private static final Color [] basicColors = {Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GREEN, Color.PINK, Color.ORANGE, new Color(199,21,133), new Color(72,209,204), new Color(46,139,87), 
			new Color(0,128,128), new Color(128,0,128), new Color(47,79,79), new Color(0,0,128), new Color(138,43,226), new Color(199,21,133), new Color(0,255,0), new Color(220,20,60), new Color(216,191,216), new Color(255,215,0), new Color(0,100,0), new Color(186,85,211), new Color(255,140,0) };

		private static ArrayList<String> taxaForDisplay;

		public BlobPanel(File file, int covLevel, double eValue)
		{
			super(new BorderLayout());
			System.out.println("in blobPanel");
			defaultMinCov= covLevel;
			this.file = file;
			this.defaultEValue = eValue;
			readFile(); // might add boolean check later
			getTaxaForDisplay();
			colors = createColorArray();

			chartPanel = (ChartPanel) createMainPanel();
			chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));


			add(chartPanel);



			JPanel minEvaluePanel = new JPanel(new BorderLayout());			
			DefaultTableXYDataset yDataset = createYDataset();
			//this.ySubChart = ChartFactory.createXYStackedBarChart("Domain count", "COV", "Count", yDataset, PlotOrientation.HORIZONTAL, false, false, false);
			StackedXYBarRenderer stackedR = new StackedXYBarRenderer();
			stackedR.setBarPainter(new StandardXYBarPainter());
			stackedR.setRenderAsPercentages(true);
			stackedR.setDrawBarOutline(false);
			stackedR.setShadowVisible(false);
			LogAxis yDomainAxis = new LogAxis("COV"); 
			//yRangeAxis.setRange(-1, 1E5);
			NumberAxis yRange = new NumberAxis("Count");
			yRange.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			//yDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			XYPlot plot = new XYPlot(yDataset, yDomainAxis, yRange,stackedR);
			plot.getDomainAxis().setLowerMargin(0.0);
			plot.getDomainAxis().setUpperMargin(0.0);
			plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
			plot.setOrientation(PlotOrientation.HORIZONTAL);
			this.ySubChart = new JFreeChart(plot);
			this.ySubChart.removeLegend();
			ChartPanel ySubChartPanel = new ChartPanel(ySubChart);
			ySubChartPanel.setMinimumDrawWidth(0);
			ySubChartPanel.setMinimumDrawHeight(0);

			ySubChartPanel.setPreferredSize(new Dimension (200, 200));

			minEvaluePanel.add(ySubChartPanel);
			//minEvaluePanel.setPreferredSize(new Dimension(200,250));
			System.out.println("***FINISHED Y Panel");

			//Add X dataset <- GC
			JPanel minLengthPanel = new JPanel(new BorderLayout());
			DefaultTableXYDataset xDataset = createXDataset();
			StackedXYBarRenderer stackedD = new StackedXYBarRenderer(.995);
			stackedD.setBarPainter(new StandardXYBarPainter());
			stackedD.setRenderAsPercentages(true);
			stackedD.setDrawBarOutline(false);
			stackedD.setShadowVisible(false);
			NumberAxis xRangeAxis = new NumberAxis("Count"); 
			//xRangeAxis.setRange(-1, 1E5);
			xRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			NumberAxis domainAxis = new NumberAxis("GC");
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
			lengthPanel.setPreferredSize(new Dimension(200,200));


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
			spacing.setPreferredSize(new Dimension(200,10));
			minLengthPanel.add(spacing, BorderLayout.EAST);

			minLengthPanel.add(lengthPanel);

			add(minEvaluePanel, BorderLayout.EAST);
			add(minLengthPanel, BorderLayout.NORTH);
			this.mainChart.setNotify(true);
			System.out.println("***FINISHED Blob");
			createTabbedControl();


		}


		public void createTabbedControl()
		{
			JFrame controlFrame = new JFrame("Control Panel");
			controlFrame.setSize(800, 200);
			JTabbedPane control = new JTabbedPane();
			control.addTab("Stats", createStatsPanel());
			control.addTab("Taxonomy", createTaxonomyPanel());
			control.addTab("Filters", createFilterPanel());
			control.addTab("Export", createExportPanel());
			controlFrame.add(control);
			controlFrame.pack();
			controlFrame.setVisible(true);
		}



		private JPanel createStatsPanel()
		{
			JPanel statsPanel = new JPanel(new BorderLayout());


			this.stats = new DefaultTableModel(new String [] {"Statistic", "Value"}, 0);
			JTable statsTable = new JTable(this.stats);
			JPanel visibleStats = new JPanel();
			JScrollPane statsScroller = new JScrollPane(statsTable);
			visibleStats.add(statsScroller);
			//statsTable.setPreferredSize(new Dimension(300,300));
			visibleStats.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Visible contigs: "), new EmptyBorder(4,4,4,4)));
			//stable.add(statsTable, BorderLayout.CENTER);
			JSplitPane split = new JSplitPane(1);
			split.add(visibleStats);
			statistics(contigSet, this.stats);



			this.model = new DefaultTableModel(new String[] { "S. Statistic:", "S. Value:" }, 0);

			this.table = new JTable(this.model);


			JPanel p = new JPanel(new BorderLayout());
			JScrollPane scroller = new JScrollPane(this.table);
			p.add(scroller);
			p.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Selected Items: "), new EmptyBorder(4, 4, 4, 4)));

			split.add(p);
			statsPanel.add(split);
			return statsPanel;
		}

		private JPanel createTaxonomyPanel()
		{
			JPanel grandTaxPanel = new JPanel();
			JPanel taxPanel = new JPanel();
			taxPanel.setLayout(new BoxLayout(taxPanel, BoxLayout.Y_AXIS));
			JPanel titlePanel = new JPanel();
			Label title = new Label("Taxa as ordereed from largest to smallest spans");
			titlePanel.add(title);
			taxPanel.add(titlePanel);

			JPanel checkBoxPanel = new JPanel();
			int half = taxaForDisplay.size()/2+1;
			GridLayout grid = new GridLayout(half, 1);

			checkBoxPanel.setLayout(grid);
		
			for(int i = taxaForDisplay.size()-1; i >= 0; i --)
			{
				String name = taxaForDisplay.get(i);
				JCheckBox box = new JCheckBox(name, true);
				final int series = updateCount(i);
				box.setActionCommand(name);
			
				box.addActionListener(new ActionListener()
				{
					XYItemRenderer renderer = ((XYPlot) mainChart.getPlot()).getRenderer();

					public void actionPerformed(ActionEvent e)
					{
						if(e.getActionCommand().equals(name))
						{
							System.out.println("In checked: " +  name);
							
							boolean visible = this.renderer.getItemVisible(series, 0);
							this.renderer.setSeriesVisible(series, Boolean.valueOf(!visible));
						}
					}
					
				});
				checkBoxPanel.add(box);
			}


			taxPanel.add(checkBoxPanel);
			JSplitPane split = new JSplitPane(1);
			split.add(taxPanel);
			JPanel legendPanel = new JPanel();
			legend.setFrame(new BlockBorder());
			//legendPanel.add(legend);

			/*
			JPanel buttonPanel = new JPanel();
			JButton submit = new JButton("Submit changes");
			submit.setMnemonic(KeyEvent.VK_ENTER);
			submit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{

				}
			});
			buttonPanel.add(submit);
			taxPanel.add(buttonPanel);
			 */


			return taxPanel;
		}

		private int updateCount(int current)
		{
			return current --;
		}

		public void itemStateChanged(ItemEvent ie)
		{
			System.out.println("in itemStateChanged");
			int series = -1;
			Object source = ie.getItemSelectable();
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				//if(source ==.equals(taxaForDisplay.get(i)))
				{

				}
			}
		}


		private JPanel createFilterPanel()
		{
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

			JPanel eValue = new JPanel();
			Label eValueSliderLabel = new Label ("Maximum E-Value:");
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
					System.out.println("action performed ");
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

			JButton reload = new JButton("Restart");
			reload.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{

					restart();

				}
			});
			buttonPanel.add(reload);
			buttonPanel.add(submit);

			//Two check buttons to change how side panels are viewed
			JPanel checkBoxPanel = new JPanel();
			JCheckBox yPercentage = new JCheckBox("Show coverage graph as numerical count");
			yPercentage.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					StackedXYBarRenderer plotRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();
					plotRenderer.setRenderAsPercentages(!yPercentage.isSelected());
					((XYPlot) ySubChart.getPlot()).setRenderer(plotRenderer);
				}
			});
			checkBoxPanel.add(yPercentage);
			JCheckBox xPercentage = new JCheckBox("Show GC content as numerical counts");
			xPercentage.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					StackedXYBarRenderer plotRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();
					plotRenderer.setRenderAsPercentages(!xPercentage.isSelected());
					((XYPlot)xSubChart.getPlot()).setRenderer(plotRenderer);
				}
			});
			checkBoxPanel.add(xPercentage);
			filterPanel.add(checkBoxPanel);
			filterPanel.add(buttonPanel);
			return filterPanel;
		}

		private void restart()
		{
			maxEValue = 1;
			minContigLength = 0;
			minCov = defaultMinCov;
			readFile();
			getTaxaForDisplay();
			XYSeriesCollection newScatterData = createDataset();
			//createMainPanel();
			((XYPlot) this.mainChart.getPlot()).setDataset(newScatterData);
			DefaultTableXYDataset newXDataset = createXDataset();
			((XYPlot) this.xSubChart.getPlot()).setDataset(newXDataset);
			DefaultTableXYDataset newYDataset = createYDataset();
			((XYPlot) this.ySubChart.getPlot()).setDataset(newYDataset);
			statistics(contigSet, this.stats);
		}



		private JPanel createExportPanel() 
		{

			JPanel exportPanel = new JPanel();
			final Text errorMessage = new Text();
			exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.Y_AXIS));

			JPanel svgPanel = new JPanel();
			JLabel svgLabel = new JLabel("SVG file naem:");
			svgPanel.add(svgLabel);
			TextField svgField = new TextField(20);
			svgPanel.add(svgField);
			JButton create = new JButton ("Create SVG");
			create.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String svgName = svgField.getText();



				}
			});
			svgPanel.add(create);
			JPanel filePanel = new JPanel();
			JLabel fileNameLabel = new JLabel ("Export file name:");
			TextField fileField = new TextField(20);
			filePanel.add(fileNameLabel);
			filePanel.add(fileField);
			JButton export = new JButton("Export visible contigs");
			export.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("action performed in submit");
					Writer fileWriter = null;
					BufferedWriter bufferedWriter = null;
					try
					{
						String export = fileField.getText();

						Path file = Paths.get(export);
						//file already exists in location
						if(Files.exists(file))
						{
							errorMessage.setText("File already exists");
							System.out.println("FILE ALREADy exists");
							return;
						}
						//file does not exist
						else
						{
							java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;
							BufferedWriter writer = Files.newBufferedWriter(file, charset);
							writer.write(header);
							writer.newLine();
							for(int i = 0; i < contigSet.size(); i ++)
							{
								String contigLine = "";
								if(contigSet.get(i).isVisible())
								{
									contigLine = contigSet.get(i).getID() + "\t";
									contigLine += contigSet.get(i).getLen() + "\t";
									contigLine += contigSet.get(i).getGC() + "\t";
									double [] cov = contigSet.get(i).getCov();
									for(int j = 0; j < cov.length; j ++)
									{
										contigLine += covLibraryNames[j] + "=" + cov[j] + ";";
									}

									contigLine += "\t";
									String [] tax = contigSet.get(i).getTax();
									for (int j = 0; j < tax.length; j++)
									{
										contigLine += taxaNames[j]  + "=" + tax[j] + ";";
									}
									contigLine += "\t";
									contigLine += contigSet.get(i).getEValue();
									writer.write(contigLine, 0, contigLine.length());
									writer.newLine();

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
						if(bufferedWriter != null && fileWriter != null)
						{
							try
							{
								bufferedWriter.close();
								fileWriter.close();
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
					Writer fileWriter = null;
					BufferedWriter bufferedWriter = null;
					try
					{
						String historyExport = historyField.getText();

						Path file = Paths.get(historyExport);
						//file already exists in location
						if(Files.exists(file))
						{
							errorMessage.setText("File already exists");
							System.out.println("FILE ALREADy exists");
							return;
						}
						//file does not exist
						else
						{
							java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.US_ASCII;
							BufferedWriter writer = Files.newBufferedWriter(file, charset);
							for(String line: history)
							{
								writer.write(line, 0, line.length());
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
					} catch (IOException e1) 
					{
						errorMessage.setText("IO exception");
						e1.printStackTrace();
					}
					finally
					{
						if(bufferedWriter != null && fileWriter != null)
						{
							try
							{
								bufferedWriter.close();
								fileWriter.close();
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


		public JPanel createMainPanel()
		{
			System.out.println("in createMainPanel");
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

		public void printToFile(File file)
		{

		}

		public void chartChanged(ChartChangeEvent event)
		{
			System.out.println("in chartChanged");
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

		private JFreeChart createChart(XYDataset dataset, DatasetSelectionExtension<XYCursor> event)
		{
			System.out.println("In createChart");
			this.dataset = (XYSeriesCollection) dataset;
			JFreeChart chart = ChartFactory.createScatterPlot("", "GC", "COV", this.dataset);

			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setNoDataMessage("No data available");
			plot.setDomainPannable(true);
			plot.setRangePannable(true);
			NumberAxis xAxis = new NumberAxis("GC");
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			xAxis.setLowerMargin(0.0);
			xAxis.setUpperMargin(0.0);
			xAxis.setRange(0.0, 1.0);
			plot.setDomainAxis(xAxis);
			LogAxis yAxis = new LogAxis("COV");
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			yAxis.setLowerMargin(0.0);
			yAxis.setUpperMargin(0.0);
			plot.setRangeAxis(yAxis);


			legend = new LegendTitle(plot);

			event.addChangeListener(plot);
			return chart;
		}

		public void update ()
		{
			System.out.println("Update");
			minContigLength = this.lengthJSlider.getValue();
			history.add( "Minimum contig length changed to: " + minContigLength);

			double exponent = this.eValueJSlider.getValue();
			maxEValue = 1* Math.pow(10,-exponent);

			history.add( "Maximum E-Value changed to: " + maxEValue);

			minCov = this.covJSlider.getValue();

			history.add( "Minumum coverage changed to: " + minCov);
			reReadFile();
			//updateDataset();
			getTaxaForDisplay();
			XYSeriesCollection newScatterData = createDataset();
			((XYPlot) this.mainChart.getPlot()).setDataset(newScatterData);
			DefaultTableXYDataset newXDataset = createXDataset();
			((XYPlot) this.xSubChart.getPlot()).setDataset(newXDataset);
			DefaultTableXYDataset newYDataset = createYDataset();
			((XYPlot) this.ySubChart.getPlot()).setDataset(newYDataset);

			XYItemRenderer r = ((XYPlot) this.mainChart.getPlot()).getRenderer();
			StackedXYBarRenderer yRenderer =  (StackedXYBarRenderer) ((XYPlot) ySubChart.getPlot()).getRenderer();
			StackedXYBarRenderer xRenderer =  (StackedXYBarRenderer) ((XYPlot) xSubChart.getPlot()).getRenderer();

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				Color paintMe = colors.get(taxaForDisplay.get(i));

				r.setSeriesPaint(i, paintMe);
				yRenderer.setSeriesPaint(i, paintMe);
				xRenderer.setSeriesPaint(i, paintMe);
			}

			((XYPlot) this.mainChart.getPlot()).setRenderer(r);
			this.dataset = (XYSeriesCollection) ((XYPlot) this.mainChart.getPlot()).getDataset();
			DatasetSelectionExtension<XYCursor> datasetExtension = new XYDatasetSelectionExtension(newScatterData);

			datasetExtension.addChangeListener(this);
		    DatasetExtensionManager dExManager = new DatasetExtensionManager();
		    dExManager.registerDatasetExtension(datasetExtension);
		    chartPanel.setSelectionManager(new EntitySelectionManager(chartPanel, new Dataset[] { newScatterData }, dExManager));

			((XYPlot) xSubChart.getPlot()).setRenderer(xRenderer);
			((XYPlot) ySubChart.getPlot()).setRenderer(yRenderer);
			statistics(contigSet, this.stats);

		}

		private static void getTaxaForDisplay()
		{
			System.out.println("in getTaxaForDisplay");
			//ArrayList<String> topTaxa = getTopTaxa();
			ArrayList<String> temp = new ArrayList<String>();
			temp.addAll(contigByTaxa.keySet());
			taxaForDisplay = sortBySpan(temp);

			System.out.println("Check order");
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				System.out.println(taxaForDisplay.get(i) + "\t" + taxLevelSpan.get(taxaForDisplay.get(i)));
			}
		}

		/*
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
		 */
		private static DefaultTableXYDataset createYDataset()
		{
			System.out.println("in createYDataset");
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			double total = maxY+1000;
			System.out.println("total" + total);
			double binSortFactor = total/800;
			System.out.println("bin factor: " + binSortFactor);

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);
				double [] bins = segregateByBucket(contigByTaxa.get(taxaForDisplay.get(i)), 800, binSortFactor, 1);
				for(int j = 0; j < bins.length; j ++)
				{
					s.add(j*binSortFactor, bins[j]);
				}
				dataset.addSeries(s);
			}
			return dataset;

		}

		private static DefaultTableXYDataset createXDataset()
		{
			System.out.println("In createXDataset");
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			System.out.println(dataset.getIntervalWidth());
			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);

				double [] bins = segregateByBucket(contigByTaxa.get(taxaForDisplay.get(i)), 100, .01, 0);
				for(int j = 0; j < bins.length; j ++)	
				{
					s.add(j*.01, bins[j]);
				}
				dataset.addSeries(s);
			}
			return dataset;
		}



		/*
		 * Returns an ArrayList of Taxa sorted by their span
		 */
		private static ArrayList<String> sortBySpan (ArrayList<String>unsorted)
		{
			System.out.println("in sortBySpan");
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

		private static ArrayList<String> sortTaxa(String addMe,  ArrayList<String> sortedTaxa) 
		{
			System.out.println("in sortTaxa");
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
			System.out.println("in getSpan");
			int span = 0;
			for (int i = 0; i < selection.size(); i ++)
			{
				span += selection.get(i).getLen();
			}
			return span;
		}

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


		public double getMedianLength(ArrayList<Contig> selection)
		{
			System.out.println("in getMedianLength");
			if(selection.size() == 0)
				return -1.0;

			ArrayList<Integer> sorted = new ArrayList<Integer>(selection.size());
			for(int i = 0; i < selection.size(); i ++)
			{
				sorted.add(selection.get(i).getLen());
			}
			Collections.sort(sorted);
			/*
			for(int i = 0; i < sorted.size(); i ++)
			{
				System.out.println(sorted.get(i));
			}
			 */
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
			System.out.println("in getMeanGC");
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
			System.out.println("in calculateN50");
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
			System.out.println("In separateByTaxa");
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
				groupedSpanTotalsByTaxa[count][1] = groupedSpan.get(taxa).toString();
				count ++;
			}
			return groupedSpanTotalsByTaxa;
		}

		private void statistics(ArrayList<Contig> selected, DefaultTableModel display)
		{
			System.out.println("in statistics");
			int number = selected.size();
			while(display.getRowCount() > 0)
			{
				display.removeRow(0);
			}


			int n50 = calculateN50(selected);
			double meanGC = getMeanGC(selected);
			double medianLen = getMedianLength(selected);
			double meanLen = getMeanLength(selected);
			int span = getSpan(selected);
			String selectedSpan = Integer.toString(span) + "/" + Integer.toString(totalLength);
			display.addRow(new Object[] {"Mean length: ", new Double(meanLen)});
			display.addRow(new Object[] {"Median length: ", new Double(medianLen)});
			display.addRow(new Object[] {"Mean GC: ", new Double(meanGC)});
			display.addRow(new Object[] {"Span: ", selectedSpan});
			display.addRow(new Object[] {"N0. of contigs displayed/ Total: ", number + "/" + totalNumberOfContigs });
			display.addRow(new Object[] {"N50: ", new Integer(n50)}); 
			display.addRow(new Object[] {"", }); 
			display.addRow(new Object[] {"Span breakdown: ", "Taxa/Selection"}); 

			String[][] selectedContigByTaxa = separateByTaxa(selected);
			for(int i = 0; i < selectedContigByTaxa.length; i ++)
			{
				display.addRow(new Object[] {selectedContigByTaxa[i][0], new String(selectedContigByTaxa[i][1]+"/"+span)}); 
			}


		}




		/*
		private void rePopulateHashMaps()
		{
			contigByTaxa.clear();
			taxLevelSpan.clear();
			ArrayList<Contig> temp;
			for(int i = 0; i < contigSet.size(); i ++)
			{
				if(contigSet.get(i).isVisible() && contigSet.get(i).getLen() >= minContigLength && contigSet.get(i).getCov()[covLibraryIndex] >= minCov )
				{
					if(contigSet.get(i).getEValue() < maxEValue)
					{
						//still annotated as being part of original taxa
						if(!contigSet.get(i).getIsNotAnnotated())
						{
							if(contigByTaxa.containsKey(contigSet.get(i).getTax()[taxaIndex]))
							{
								temp = contigByTaxa.get(contigSet.get(i).getTax()[taxaIndex]);
								temp.add(contigSet.get(i));
								contigByTaxa.put(contigSet.get(i).getTax()[taxaIndex], temp);
								Integer tempInt = taxLevelSpan.get(contigSet.get(i).getTax()[taxaIndex]);
								tempInt += contigSet.get(i).getLen();
								taxLevelSpan.put(contigSet.get(i).getTax()[taxaIndex], tempInt);
							}
							else
							{
								temp = new ArrayList<Contig>();
								temp.add(contigSet.get(i));
								contigByTaxa.put(contigSet.get(i).getTax()[taxaIndex], temp);
								taxLevelSpan.put(contigSet.get(i).getTax()[taxaIndex], contigSet.get(i).getLen());
							}
						}
						//move to annotated
						else
						{
							contigSet.get(i).setIsNotAnnotated()
						}
						{

						}
					}

				}


			}
		}
		 */
		public static boolean reReadFile()
		{
			System.out.println("in re-readfile");
			long start = System.nanoTime();
			contigByTaxa.clear();
			taxLevelSpan.clear();
			contigSet.clear();
			BufferedReader bufferedReader = null;
			boolean correct = true;
			try 
			{
				bufferedReader = new BufferedReader(new FileReader(file));
				header = bufferedReader.readLine();

				String text;
				while ((text = bufferedReader.readLine()) != null) 
				{
					String tax = "";

					Contig addMe = parseContig(text, defaultEValue);
					if (addMe == null)
					{
						System.out.println("Unable to parseContig");

					}
					else
					{	

						if(addMe.getLen() >= minContigLength && addMe.getCov()[covLibraryIndex] >= minCov && addMe.getEValue() < maxEValue)
						{
							tax = addMe.getTax()[taxaIndex];
							System.out.println("in first else");
						}
						else if (addMe.getLen() >= minContigLength && addMe.getCov()[covLibraryIndex] >= minCov && addMe.getEValue() >=  maxEValue)
						{
							tax = "Not annotated";
							System.out.println("In ifelse");
						}
						else
						{
							tax = "";

							addMe.setVisibility(false);
						}
					}

					if(addMe.isVisible())
					{
						System.out.println("********adding contigs");
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
				System.out.println("******Size of contigSet: " + contigSet.size());
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
			long end = System.nanoTime();
			long difference = end - start;
			System.out.println("re-read time: " +(difference));
			return correct;
		}


		/*
		 * Reads file passed in from JavaFX scene in Test.java
		 * parses each line of the file to generate a Contig
		 * Passes on replacement value for "N/A" result, E values to the user entered eValue, to parseContig(String text, double eValue) 
		 * Adds new contig to ArrayList of existing contigs 
		 * Builds HashMap<String, ArrayList<Contig>> contigByTaxa, where Contigs are grouped by taxa
		 * Builds HashMap <String, Integer> taxLevelSpan, where the span of each taxa is calculated 
		 * Returns false if error occures
		 */
		public static boolean readFile()
		{
			System.out.println("in readfile");

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
				history.add("Initial default minimum coverage: " + defaultMinCov);
				history.add("Initial default E-Value: " + defaultEValue);
				history.add("Initial taxonomic level: " + taxaNames[taxaIndex]);
				history.add("Initial coverage library: " + covLibraryNames[covLibraryIndex]);
				totalNumberOfContigs = contigSet.size();
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
					System.out.println("Incorrect number of key value pair entries");
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

			return contigToAdd;
		}

		private HashMap<String, Color> createColorArray()
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

		private static double [] segregateByBucket(ArrayList<Contig> series, int numBins, double splitFactor, int category)
		{
			System.out.println("in segregateByBucket");
			double [] collection = new double [numBins];
			for(int i = 0; i < series.size(); i ++)
			{
				double value = -1;
				if(category == 0)
					value = series.get(i).getGC();
				else if (category == 1)
					value = series.get(i).getCov()[covLibraryIndex];
				else
				{
					System.out.println("Incorrect category value, set to GC");
					value = 0;
				}
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


		private static XYSeriesCollection createDataset() 
		{
			System.out.println("in createDataset");
			// ArrayList containing top taxa which need to be displayed
			XYSeriesCollection dataset = new XYSeriesCollection();
			//getTaxaForDisplay();

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



		/*
		 * (non-Javadoc)
		 * @see org.jfree.data.general.SelectionChangeListener#selectionChanged(org.jfree.data.general.SelectionChangeEvent)
		 */

		@Override
		public void selectionChanged(SelectionChangeEvent<XYCursor> event) 
		{
			System.out.println("In selection Changed");
			long start = System.nanoTime();
			while(this.model.getRowCount() > 0)
			{
				this.model.removeRow(0);
			}

			XYDatasetSelectionExtension ext = (XYDatasetSelectionExtension)event.getSelectionExtension();
			DatasetIterator itr = ext.getSelectionIterator(true);

			ArrayList<Contig> selected = new ArrayList<Contig>();
			while(itr.hasNext())
			{
				System.out.println("In itr.hasNext()");	
				XYCursor dc = (XYCursor)itr.next();
				Comparable seriesKey = this.dataset.getSeriesKey(dc.series);
				ArrayList<Contig> taxa = contigByTaxa.get(seriesKey);
				selected.add(taxa.get(dc.item));
				System.out.println("series key " + seriesKey);
			}
			System.out.println("size of selected: " + selected.size());
			if(selected.size() > 0)
				statistics(selected, this.model);
			long end = System.nanoTime();
			System.out.println("Elapsed time of selection and statistics: " + (end - start));

		}



	}
	public Charts(File file, int covNum, double eValue)
	{
		super("Blobsplorer");
		System.out.println("in Charts");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = createDemoPanel(file, covNum, eValue);
		setContentPane(content);
	}



	public static  JPanel createDemoPanel(File file, int covLevel, double eValue) 
	{
		System.out.println("in createDemoPanel");
		return new BlobPanel(file, covLevel, eValue);
	}

}







