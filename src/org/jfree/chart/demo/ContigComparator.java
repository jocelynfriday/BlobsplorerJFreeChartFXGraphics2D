package org.jfree.chart.demo;

import java.util.Comparator;

/**
 * A Comparator which sorts contigs based on span in ascending order
 * @author jocelynfriday
 * @date 30 July 2014
 */
public class ContigComparator implements Comparator<Contig>
{
	/**
	 * Compares two contigs based on span.  
	 * Returns a negative integer, zero, or a positive integer as the first contig's span is less than, equal to, or greater than the second contig's span
	 * @param c1 first contig to order
	 * @param c2 second contig to order
	 * @return a negative integer, zero, or a positive integer as the first contig's span is less than, equal to, or greater than the second's
	 */
	@Override
	public int compare(Contig c1, Contig c2) {
		Integer s1 = c1.getLen();
		Integer s2 = c2.getLen();
		return s1.compareTo(s2);
	}

}
