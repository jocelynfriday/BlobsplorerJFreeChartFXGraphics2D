package org.jfree.chart.demo;


public class Contig 
{
	private String ID;
	private int len;
	private float gc;
	private float [] cov = new float[3];
	private String [] tax = new String[4];
	private double eValue;
	
	public Contig (String ID, int len, float gc, float [] cov, String [] tax, double eVal)
	{
		this.ID = ID;
		this.len = len;
		this.gc = gc;
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
	
	public float getGC()
	{
		return gc;
	}
	
	public float [] getCov()
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

}
