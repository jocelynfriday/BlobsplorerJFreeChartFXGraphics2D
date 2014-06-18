package org.jfree.chart.demo;
import org.jfree.data.extension.DatasetIterator;
import org.jfree.data.extension.IterableSelection;
import org.jfree.data.extension.impl.AbstractDatasetSelectionExtension;
import org.jfree.data.extension.impl.XYCursor;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.SelectionChangeListener;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;

import java.util.ArrayList;
public class XYZDatasetSelectionExtension extends AbstractDatasetSelectionExtension<XYCursor, XYZDataset> implements IterableSelection<XYCursor>
	{
		private static final long serialVersionUID = 4859712483757720877L;
		private DefaultXYZDataset dataset;
		private ArrayList<Boolean>[] selectionData;

		public XYZDatasetSelectionExtension(DefaultXYZDataset dataset)
		{
			super(dataset);
			this.dataset = dataset;
			this.selectionData = new ArrayList[dataset.getSeriesCount()];
			initSelection();
		}

		public XYZDatasetSelectionExtension(XYZDataset dataset, SelectionChangeListener<XYCursor> initialListener)
		{
			super(dataset);
			addChangeListener(initialListener);
		}

		public void datasetChanged(DatasetChangeEvent event)
		{
			initSelection();
		}

		public boolean isSelected(XYCursor cursor)
		{
			return ((Boolean)this.selectionData[cursor.series].get(cursor.item)).booleanValue();
		}

		public void setSelected(XYCursor cursor, boolean selected)
		{
			this.selectionData[cursor.series].set(cursor.item, Boolean.valueOf(selected));

			notifyIfRequired();
		}

		public void clearSelection()
		{
			initSelection();
		}

		private void initSelection()
		{
			for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
				this.selectionData[i] = new ArrayList(this.dataset.getItemCount(i));
				for (int j = 0; j < this.dataset.getItemCount(i); j++) {
					this.selectionData[i].add(Boolean.FALSE);
				}
			}
			notifyIfRequired();
		}

		public DatasetIterator<XYCursor> getIterator()
		{
			return new XYZDatasetSelectionIterator();
		}

		public DatasetIterator<XYCursor> getSelectionIterator(boolean selected)
		{
			return new XYZDatasetSelectionIterator(selected);
		}
	
		private class XYZDatasetSelectionIterator implements DatasetIterator<XYCursor>
		{
			private static final long serialVersionUID = 125607273863837608L;
			private int series = 0;

			private int item = -1;

			private Boolean filter = null;

			public XYZDatasetSelectionIterator()
			{
			}

			public XYZDatasetSelectionIterator(boolean selected)
			{
				this.filter = Boolean.valueOf(selected);
			}

			public boolean hasNext()
			{
				if (nextPosition()[0] != -1) {
					return true;
				}
				return false;
			}

			public XYCursor next()
			{
				int[] newPos = nextPosition();
				this.series = newPos[0];
				this.item = newPos[1];
				return new XYCursor(this.series, this.item);
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			private int[] nextPosition()
			{
				int pSeries = this.series;
				int pItem = this.item;

				while (pSeries < XYZDatasetSelectionExtension.this.selectionData.length) {
					if (pItem + 1 >= XYZDatasetSelectionExtension.this.selectionData[pSeries].size()) {
						pSeries++;
						pItem = -1;
					}
					else if ((this.filter != null) && 
							(!this.filter.equals(XYZDatasetSelectionExtension.this.selectionData[pSeries].get(pItem + 1)))) {
						pItem++;
					}
					else
					{
						return new int[] { pSeries, pItem + 1 };
					}
				}
				return new int[] { -1, -1 };
			}
		}
	}

