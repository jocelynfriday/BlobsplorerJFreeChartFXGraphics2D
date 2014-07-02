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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.panel.selectionhandler.EntitySelectionManager;
import org.jfree.chart.panel.selectionhandler.FreePathSelectionHandler;
import org.jfree.chart.panel.selectionhandler.MouseClickSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RectangularRegionSelectionHandler;
import org.jfree.chart.panel.selectionhandler.RegionSelectionHandler;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
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
import org.jfree.data.Range;
import org.jfree.data.extension.DatasetIterator;
import org.jfree.data.extension.DatasetSelectionExtension;
import org.jfree.data.extension.impl.DatasetExtensionManager;
import org.jfree.data.extension.impl.XYCursor;
import org.jfree.data.extension.impl.XYDatasetSelectionExtension;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.SelectionChangeEvent;
import org.jfree.data.general.SelectionChangeListener;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

public class Charts extends ApplicationFrame{
	static class BlobPanel extends DemoPanel implements ChangeListener, ChartChangeListener
	{
		//private XYDataset dataset;
		private XYSeriesCollection dataset;
		private JFreeChart mainChart;
		private JFreeChart ySubChart;
		private JFreeChart xSubChart;
		private JSlider eValueJSlider;
		private JSlider lengthJSlider;
		private Range lastXRange;
		private Range lastYRange;
		private DefaultTableModel model;
		private JTable table;

		private static ArrayList <Contig> contigSet = new ArrayList<Contig>();
		private static int numberOfTaxaDisplayed;
		private static int taxaIndex = 2;
		private static int covLibraryIndex = 0; // which cov library to use //** needs to be added to UI
		private static HashMap<String, ArrayList<Contig>> contigByTaxa = new HashMap<String, ArrayList<Contig>>();
		private static HashMap <String, Integer> taxLevelSpan = new HashMap <String, Integer>();
		private static int totalLength = 0;
		private static double maxEValue = 1.0;
		private static int minContigLength = 0;
		private static double minX = 0;
		private static double maxX = 0;
		private static double minY = 0;
		private static double maxY = 0;
		private static final int numOfBuckets = 100;
		private static ArrayList<String> taxaForDisplay;

		public BlobPanel(File file, int taxLevel, double eValue, String title)
		{
			super(new BorderLayout());
			System.out.println("in blobPanel");
			numberOfTaxaDisplayed = taxLevel;
			readFile(file, eValue); // might add boolean check later
			getTaxaForDisplay();
			
			ChartPanel chartPanel = (ChartPanel) createMainPanel();
			chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
			add(chartPanel);

			JPanel minEvaluePanel = new JPanel(new BorderLayout());			
			DefaultTableXYDataset yDataset = createYDataset(taxaForDisplay);
			//this.ySubChart = ChartFactory.createXYStackedBarChart("Domain count", "COV", "Count", yDataset, PlotOrientation.HORIZONTAL, false, false, false);
			StackedXYBarRenderer stackedR = new StackedXYBarRenderer();
			 stackedR.setBarPainter(new StandardXYBarPainter());
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
			plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			plot.setOrientation(PlotOrientation.HORIZONTAL);
			this.ySubChart = new JFreeChart("Range count", plot);

			ChartPanel ySubChartPanel = new ChartPanel(ySubChart);
			ySubChartPanel.setMinimumDrawWidth(0);
			ySubChartPanel.setMinimumDrawHeight(0);

			ySubChartPanel.setPreferredSize(new Dimension (200, 150));
			this.eValueJSlider = new JSlider(0, 100, 100);
			this.eValueJSlider.addChangeListener(this);
			this.eValueJSlider.setOrientation(JSlider.VERTICAL);
			

			minEvaluePanel.add(ySubChartPanel);
			minEvaluePanel.add(this.eValueJSlider, BorderLayout.WEST);
			System.out.println("***FINISHED Y Panel");
			
			//Add X dataset <- GC
			JPanel minLengthPanel = new JPanel(new BorderLayout());
			DefaultTableXYDataset xDataset = createXDataset(taxaForDisplay);
			StackedXYBarRenderer stackedD = new StackedXYBarRenderer(.995);
			stackedD.setBarPainter(new StandardXYBarPainter());
			stackedD.setDrawBarOutline(true);
			 stackedD.setShadowVisible(false);
			NumberAxis xRangeAxis = new NumberAxis("Count"); 
			//xRangeAxis.setRange(-1, 1E5);
			xRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			NumberAxis domainAxis = new NumberAxis("GC");
			domainAxis.setRange(0.0, 1.0);
			XYPlot plot1 = new XYPlot(xDataset, domainAxis, xRangeAxis,stackedD);
			plot1.setOrientation(PlotOrientation.VERTICAL);
			//plot1.getDomainAxis().setLowerMargin(0.0);
			//plot1.getDomainAxis().setUpperMargin(0.0);
			plot1.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			this.xSubChart = new JFreeChart("Domain count", plot1);

			ChartPanel lengthPanel = new ChartPanel(xSubChart);
			lengthPanel.setMinimumDrawWidth(0);
			lengthPanel.setMinimumDrawHeight(0);
			lengthPanel.setPreferredSize(new Dimension(200,150));

			//panel for layout
			JPanel blank = new JPanel();
			blank.setPreferredSize(new Dimension(200,10));
			lengthPanel.add(blank, BorderLayout.EAST);

			this.lengthJSlider = new JSlider(0, 20000, 0);
			this.lengthJSlider.setBorder(BorderFactory.createEmptyBorder(0,0,0,200));
			this.lengthJSlider.addChangeListener(this);
			minLengthPanel.add(lengthPanel);
			minLengthPanel.add(lengthJSlider, BorderLayout.NORTH);
			add(minEvaluePanel, BorderLayout.EAST);
			add(minLengthPanel, BorderLayout.SOUTH);
			this.mainChart.setNotify(true);
			System.out.println("***FINISHED Blob");
		}

		public JPanel createMainPanel()
		{
			System.out.println("in createMainPanel");
			this.mainChart = createChart(new XYSeriesCollection());
			this.mainChart.addChangeListener(this);
			ChartPanel panel = new ChartPanel(this.mainChart);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);
			return panel;
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
		
		private JFreeChart createChart(XYDataset dataset)
		{
			System.out.println("In createChart");
			this.dataset = createDataset();
			JFreeChart chart = ChartFactory.createScatterPlot("BlobSplorer Selection Demo", "GC", "COV", this.dataset);
		
			XYPlot plot = (XYPlot) chart.getPlot();
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
			return chart;
		}

		public void stateChanged (ChangeEvent e)
		{
			System.out.println("In stateChanged");
			if(e.getSource() == this.lengthJSlider)
			{
				minContigLength = this.lengthJSlider.getValue() - this.lengthJSlider.getMinimum();
				System.out.println("minContigLength "+ minContigLength);
			}
			else if (e.getSource() == this.eValueJSlider)
			{
				maxEValue = this.eValueJSlider.getValue() - this.eValueJSlider.getMinimum();
				System.out.println("maxEValue " + maxEValue);
			}
			updateVisible();
			getTaxaForDisplay();
			XYSeriesCollection newData = createDataset();
			((XYPlot) this.mainChart.getPlot()).setDataset(newData);
			
		}

		private static void getTaxaForDisplay()
		{
			System.out.println("in getTaxaForDisplay");
			ArrayList<String> topTaxa = getTopTaxa();
			taxaForDisplay = sortBySpan(topTaxa);
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
		private static DefaultTableXYDataset createYDataset(ArrayList<String> taxaForDisplay)
		{
			System.out.println("in createYDataset");
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			double total = maxY+100;
			System.out.println("total" + total);
			double binSortFactor = total/500;
			System.out.println("bin factor: " + binSortFactor);

			for(int i = 0; i < taxaForDisplay.size(); i ++)
			{
				XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);
				double [] bins = segregateByBucket(contigByTaxa.get(taxaForDisplay.get(i)), 500, binSortFactor, 1);
				for(int j = 0; j < bins.length; j ++)
				{
					s.add(j*binSortFactor, bins[j]);
				}
				dataset.addSeries(s);
			}
			return dataset;

		}

		private static DefaultTableXYDataset createXDataset(ArrayList<String> taxaForDisplay)
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
		//**Should I create a treeMap to ease sorting?
		/*
		 * Selects top x number (taxaLevel) of taxa to be displayed in chart.
		 * Iterates through contigByTaxa and sorts the keys based on the number of contigs belonging to a given taxa
		 * (i.e. sorts the keys of the map based on the length of the value's ArrayList) and truncates result if necessary
		 */
		private static ArrayList<String> getTopTaxa()
		{
			System.out.println("in getTopTaxa");
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
			System.out.println("in statistics");
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
		
		private static void updateVisible()
		{
			System.out.println("in updateVisible");
			boolean changedLength, changedEvalue;
			for(int i = 0; i < contigSet.size(); i ++)
			{
				//Update visibility based on contig length
				if (contigSet.get(i).getLen() < minContigLength) // contig below min lenght to be displayed
				{
					 changedLength = contigSet.get(i).setVisibility(false);
					 if(changedLength) //if visibility goes from visible to hidden, indicate positive removal
					 {
						 remove (contigSet.get(i));
					 }
				}
				else
				{
					changedLength = contigSet.get(i).setVisibility(true);
					if(!changedLength) //Contig made visible from change
					{
						add(contigSet.get(i));
					}
				}
				
				//update visibility based on E-Value cutoff 
				if(contigSet.get(i).getEValue() > maxEValue)
				{
					 changedEvalue = contigSet.get(i).setVisibility(false); //returns true if contig used to be visible and false otherwise
					 if(changedEvalue)
						 remove(contigSet.get(i));
				}
				else
				{
					changedEvalue = contigSet.get(i).setVisibility(true);
					if(!changedEvalue)
						add(contigSet.get(i));
				}
				
			}
		}
		
		private static void add(Contig add)
		{
			System.out.println("in add");
			 //update span calculation
			String taxa = add.getTax()[taxaIndex];
			 Integer updated = taxLevelSpan.get(taxa) + add.getLen();
			 taxLevelSpan.put(taxa, updated);
			 //add contig to contigByTaxa
			 ArrayList<Contig> temp = contigByTaxa.get(taxa);
			 temp.add(add);
			 contigByTaxa.put(taxa, temp);
		}
		
		private static void remove(Contig remove)
		{
			System.out.println("in remove");
			 String taxa = remove.getTax()[taxaIndex];
			 //Update taxLevelSpan to remove length of contig
			 Integer updated = taxLevelSpan.get(taxa) - remove.getLen();
			 taxLevelSpan.put(taxa, updated);
			 //loop through contigs associated to taxa and remove contig from hashMap
			 for(int j = 0; j < contigByTaxa.get(taxa).size(); j++)
			 {
				 if (contigByTaxa.get(taxa).get(j).getID().equals(remove.getID())) //based on assumption that contig IDs are unique
				 {
					 contigByTaxa.get(taxa).remove(j); // removes element from ArrayList associated with taxa key
					 break;
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
			System.out.println("in readfile");
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
				if (value > 500*splitFactor)
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
					//as 0 cannot be displayed on log scale, set libraries where coverage is 0 to small value
					if ((double)taxaSet.get(j).getCov()[covLibraryIndex] == 0)
					{
						taxaSet.get(j).setCovAtPos(covLibraryIndex, 1E-5);
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


	}
	public Charts(File file, int taxLevel, double eValue, String title)
	{
		super(title);
		System.out.println("in Charts");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = createDemoPanel(file, taxLevel, eValue, title);
		setContentPane(content);
	}



	public static  JPanel createDemoPanel(File file, int taxLevel, double eValue, String title) 
	{
		System.out.println("in createDemoPanel");
		return new BlobPanel(file, taxLevel, eValue, title);
	}






	/*
	private static XYSeriesCollection createXDataset(ArrayList<String> taxaForDisplay)
	{
		HistogramDataset dataset = new HistogramDataset(0);

		for(int i = 0; i < taxaForDisplay.size(); i ++) //loop through arrayLists associated with top taxa by span
		{
			String taxa = taxaForDisplay.get(i);
			ArrayList<Contig> taxaSet = contigByTaxa.get(taxa);
			double [] values = new double[taxaSet.size()];
			for(int j = 0; j < taxaSet.size(); j ++)
			{	
				values[j] = taxaSet.get(j).getGC();
			}
			dataset.addSeries(taxa, values, 100, 0, 1);
		}
		return dataset;

	}
	 */

	/*
	private static DefaultTableXYDataset createXDataset(ArrayList<String> taxaForDisplay)
	{
		DefaultTableXYDataset dataset = new DefaultTableXYDataset(true);
		//double xDifference = maxX - minX;
		for(int i = 0; i < taxaForDisplay.size(); i ++)
		{
			XYSeries s = new XYSeries(taxaForDisplay.get(i), true, false);
			double[] collection = segregateByBucket(contigByTaxa.get(taxaForDisplay.get(i)));
			System.out.println(Arrays.toString(collection));
			System.out.println(collection.length);
			for (int j = 0; j < collection.length; j ++)
			{
				s.add(j*.01, collection[j]);
			}
			dataset.addSeries(s);
		}
		return dataset;
	}
	 */





}
/*
 * (non-Javadoc)
 * @see org.jfree.data.general.SelectionChangeListener#selectionChanged(org.jfree.data.general.SelectionChangeEvent)
 */
/*
@Override
public void selectionChanged(SelectionChangeEvent<XYCursor> event) 
{
	long start = System.nanoTime();
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
		ArrayList<Contig> taxa = contigByTaxa.get(seriesKey);
		selected.add(taxa.get(dc.item));
	}
	statistics(selected);
	long end = System.nanoTime();
	System.out.println("Elapsed time of selection and statistics: " + (end - start));

}


}
 */
/*








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





}
 */