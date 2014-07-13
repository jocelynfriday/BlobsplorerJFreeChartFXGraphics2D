package org.jfree.chart.demo;


public class Contig 
{
	private String ID;
	private int len;
	private double gc;
	private double [] origCov;
	private double [] cov;
	private String [] origTax;
	private String [] tax;
	private double eValue;
	private boolean visible;
	private boolean isNotAnnotated;
	
	public Contig (String ID, int len, double gc2, double[] cov, String [] tax, double eVal)
	{
		this.ID = ID;
		this.len = len;
		this.gc = gc2;
		this.origCov = cov;
		this.cov = cov;
		this.origTax = tax;
		this.tax = tax;
		this.eValue = eVal;
		visible = true;
		isNotAnnotated = false;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public int getLen()
	{
		return len;
	}
	
	public double getGC()
	{
		return gc;
	}
	
	public double [] getOrigCov()
	{
		return origCov;
	}
	
	public double [] getCov()
	{
		return cov;
	}
	
	public String []  getOrigTax()
	{
		return origTax;
	}
	
	public String [] getTax()
	{
		return tax;
	}
	
	public double getEValue()
	{
		return eValue;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setCovAtPos(int i, double value)
	{
		cov[i] = value;
	}
	
	public boolean setVisibility (boolean isVisible)
	{
		boolean temp = visible;
		visible = isVisible; 
		return temp;
	}
	
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
	
	public boolean getIsNotAnnotated()
	{
		return isNotAnnotated;
	}
	
	public boolean restoreTaxa(int pos)
	{
		try
		{
			tax[pos] = origTax[pos];
			return true;
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	public boolean restoreCov(int pos)
	{
		try
		{
			cov[pos] = origCov[pos];
			return true;
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	public void setIsNotAnnotated (boolean update)
	{
		isNotAnnotated = update;
	}

}
