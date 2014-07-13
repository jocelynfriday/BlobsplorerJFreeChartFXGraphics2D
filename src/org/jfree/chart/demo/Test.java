package org.jfree.chart.demo;
//package org.jfree.chart.demo.selection;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import java.util.logging.Level;
import java.util.logging.Logger;



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
//import javafx.scene.text.Text;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import org.jfree.chart.JFreeChart;

import org.jfree.chart.ui.WindowUtils;

import org.jfree.fx.FXGraphics2D;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.SimpleType;

/**
 * A demo scatter plot.
 */
public class Test extends Application 
{
	private static File file;
	private static final Stage primaryStage = null;

	public static class ChartCanvas extends Canvas
	{
		JFreeChart chart;

		private FXGraphics2D g2;

		public ChartCanvas(JFreeChart chart) 
		{
			this.chart = chart;
			this.g2 = new FXGraphics2D(this.getGraphicsContext2D());
			// Redraw canvas when size changes. 
			widthProperty().addListener(evt -> draw()); 
			heightProperty().addListener(evt -> draw()); 
		}  

		private void draw() 
		{ 
			double width = getWidth(); 
			double height = getHeight();  
			this.chart.draw(this.g2, new Rectangle2D.Double(0, 0, width, 
					height));
		} 

		@Override 
		public boolean isResizable() 
		{ 
			return true;
		}  

		@Override 
		public double prefWidth(double height) 
		{ 
			return getWidth(); 
		}  

		@Override 
		public double prefHeight(double width)
		{ 
			return getHeight(); 
		} 
	}// end of ChartCanvas

	public static void main(String[] args) 
	{
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		startWindow(primaryStage);
	}

	public static void startWindow(Stage stage)
	{
		//Group root = new Group();
		stage.setTitle("Welcome");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(30,30,30,30));
		Scene scene = new Scene (grid, 500, 300);
		stage.setScene(scene);

		Text sceneTitle = new Text("Welcome to Blobsplorer");
		//sceneTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 40.0));		
		grid.add(sceneTitle,0, 0, 2,1);

		Label fileSource = new Label ("File Source");
		grid.add(fileSource, 0, 1);


		Button buttonLoad = new Button ("Load Resouce");
		buttonLoad.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle (ActionEvent arg0) 
			{

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Blobplot.txt files (*.blobplot.txt)", "*.blobplot.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(stage);
				if (file != null)
				{
					boolean attempt = readFile(file);
					if (attempt == false)
						System.out.println("Something wrong with file");
				}
			}
				});
		grid.add(buttonLoad, 1, 1);

		Label cov = new Label("Default coverage");
		grid.add(cov, 0, 2);
		TextField covValue = new TextField();
		covValue.setPromptText("Value if cov is 0");
		grid.add(covValue, 1, 2);

		Label eValue = new Label("Default EValue");
		grid.add(eValue, 0, 3);
		TextField eValueNumber = new TextField();
		eValueNumber.setPromptText("Unannotated default");
		grid.add(eValueNumber, 1, 3);

		Button clear = new Button("Clear");
		HBox hbtn2 = new HBox(10);
		hbtn2.setAlignment(Pos.BOTTOM_RIGHT);
		hbtn2.getChildren().add(clear);
		grid.add(hbtn2, 1, 5);

		Button submit = new Button("Submit");
		HBox hbtn = new HBox(10);
		hbtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbtn.getChildren().add(submit);
		grid.add(hbtn, 2, 5);


		final Text errorMessage = new Text();
		//errorMessage.setFill(Color.RED);
		grid.add(errorMessage, 1, 7, 2, 1);

		submit.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle(ActionEvent e)
			{
				boolean run = true;
				String cov = covValue.getText();
				String eVal = eValueNumber.getText();
				int covNum = 0;
				double eValue = 1;
				//check for if file populated memory
				//*$* This might be taking time, check for a way to speed up
				if (file == null)
				{
					errorMessage.setText("Enter resource file");
					run = false;
				}

				//check if user entered valid taxa number
				if(cov == null)
				{
					errorMessage.setText("Enter default coverage");
					run = false;
				}
				else
				{
					try
					{
						covNum = Integer.parseInt(cov);
						if (covNum == 0)
						{
							errorMessage.setText("Enter a number other than 0");
							run = false;
						}

					}
					catch(NumberFormatException nft)
					{
						errorMessage.setText("Enter a valid number (e.g. 1e-5, -8, etc)");
						run = false;
					}
				}
				//Make sure default eVal added
				if(eVal == null)
				{
					errorMessage.setText("Enter an double value (e.g. 1.0, 2.0, etc)");
					run = false;
				}
				else
				{
					try
					{
						eValue = Double.parseDouble(cov);
						if (eValue == 0)
						{
							errorMessage.setText("Enter a number other than 0");
							run = false;
						}

					}
					catch(NumberFormatException nft)
					{
						errorMessage.setText("Enter a valid double (e.g. 1.0, 2.0, etc)");
						run = false;
					}
				}

				if(run)
				{
					Test.setup(covNum, eValue);
					//stage.close();
				}

			}
				}
				);
		clear.setOnAction(new EventHandler<ActionEvent>()
				{
			public void handle (ActionEvent e)
			{
				covValue.clear();
				eValueNumber.clear();
				clear();
				errorMessage.setText(null);
			}
				}
				);

		stage.show();

	}



	public static boolean readFile(File test)
	{
		BufferedReader bufferedReader = null;
		boolean correct = true;

		try 
		{
			bufferedReader = new BufferedReader(new FileReader(test));
			file = test;
			
		}
		catch (FileNotFoundException ex) 
		{
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
			correct = false;
		} 
		finally 
		{
			try 
			{
				bufferedReader.close();
			} 
			catch (IOException ex)
			{
				Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
				correct = false;
			}
		} 

		return correct;
	}

	private static void clear()
	{
		file = null;
	}
	

	//*$* need to incorporate eValue
	private static void setup(int covNum,  double eValue)
	{	
		Charts scatter = new Charts(file, covNum,eValue);
		scatter.pack();
		WindowUtils.centerFrameOnScreen(scatter);
		scatter.setVisible(true);
	}

	

	public static void drawGraph(Stage stage, Charts scatter)
	{
	
		//final SwingNode swingNode = new SwingNode();
		
		
		//StackPane stackPane = new StackPane(); 
		//stackPane.getChildren().add(canvas);  
		// Bind canvas size to stack pane size. 
		//canvas.widthProperty().bind( stackPane.widthProperty()); 
		//canvas.heightProperty().bind( stackPane.heightProperty());  
		//stage.setScene(new Scene(stackPane)); 
		//stage.setTitle("FXGraphics2DDemo1.java"); 
		//stage.setWidth(700);
		//stage.setHeight(390);
		//stage.show(); 
	}



}