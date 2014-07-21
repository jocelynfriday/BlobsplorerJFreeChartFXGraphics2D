package org.jfree.chart.demo;


public class Contig 
{
	private String ID;
	private int len;
	private double gc;
	private double [] cov;
	private String [] tax;
	private double eValue;
	private boolean visible;

	
	/**
	 * 
	 * @param ID unique string identifier
	 * @param len number of bases
	 * @param gc percentage of Gs and Cs in a given sequence
	 * @param cov array of coverages, each value linked to one library
	 * @param tax array of taxonomic level values
	 * @param eVal E-Value associated with taxonomic classification
	 */
	public Contig (String ID, int len, double gc, double[] cov, String [] tax, double eVal)
	{
		this.ID = ID;
		this.len = len;
		this.gc = gc;
		this.cov = cov;
		this.tax = tax;
		this.eValue = eVal;
		visible = true;
	}
	
	/**
	 * Returns the contig identifier. 
	 * @return contig identifier
	 */
	public String getID()
	{
		return ID;
	}
	
	/**
	 * Returns the length of contig (i.e. number of bases). 
	 * @return length of contig
	 */
	public int getLen()
	{
		return len;
	}
	
	/**
	 * Returns the ratio of Gs and Cs to the composition of the contig as a whole
	 * @return GC content value
	 */
	public double getGC()
	{
		return gc;
	}
	
	/**
	 * Returns the coverage values associated with given libraries
	 * @return array of coverage values
	 */
	public double [] getCov()
	{
		return cov;
	}
	
	/**
	 * Returns the array of taxonomic annotations
	 * @return array of taxonomic annotations
	 */
	public String [] getTax()
	{
		return tax;
	}
	
	/**
	 * Returns the E-Value, either based on default or as calculated by BLAST
	 * @return E-Value 
	 */
	public double getEValue()
	{
		return eValue;
	}
	
	/**
	 * Returns true if the contig is visible and false otherwise
	 * @return visibility of a contig
	 */
	public boolean isVisible()
	{
		return visible;
	}
	
	/**
	 * Updates the coverage value at a given position, i.  
	 * @param i index position to change
	 * @param value new coverage value 
	 */
	public void setCovAtPos(int i, double value)
	{
		cov[i] = value;
	}
	
	/**
	 * Sets visibility of contig and returns previous value
	 * @param isVisible updated visibility value
	 * @return previous value of visibility 
	 */
	public boolean setVisibility (boolean isVisible)
	{
		boolean temp = visible;
		visible = isVisible; 
		return temp;
	}
	
	/**
	 * Updates taxa at a given position and returns true of successful or returns false if update was unsuccessful. 
	 * @param taxa new taxa value
	 * @param pos index of taxa to be changed 
	 * @return true if able to complete change and false otherwise
	 */
	public boolean setTaxaAtPosition (String taxa, int pos)
	{
		try
		{
			tax[pos] = taxa;
			return true;
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
	}

}
