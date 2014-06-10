package org.jfree.chart.demo;


public class Contig 
{
	private String ID;
	private int len;
	private double gc;
	private double [] cov = new double[3];
	private String [] tax = new String[4];
	private double eValue;
	
	public Contig (String ID, int len, double gc2, double[] cov, String [] tax, double eVal)
	{
		this.ID = ID;
		this.len = len;
		this.gc = gc2;
		this.cov = cov;
		this.tax = tax;
		this.eValue = eVal;
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
	
	public double [] getCov()
	{
		return cov;
	}
	
	public String []  getTax()
	{
		return tax;
	}
	
	public double getEValue()
	{
		return eValue;
	}
	
	public void setCovAtPos(int i, double value)
	{
		cov[i] = value;
	}

}