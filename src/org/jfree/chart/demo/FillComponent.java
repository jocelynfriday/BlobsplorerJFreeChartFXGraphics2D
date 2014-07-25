package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;


public class FillComponent extends JComponent
{
	Color paint;
	public FillComponent(Color c)
	{
		paint = c;
	}
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		Ellipse2D.Double circle = new Ellipse2D.Double(1,1, 10, 10);
		g2.setPaint(paint);
		g2.fill(circle);
	}
}
