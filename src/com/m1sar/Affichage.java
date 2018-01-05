package com.m1sar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class Affichage extends Application {
	
	
	private ArrayList <HashMap<String,Double>> prixParEntreprise  = new  ArrayList <HashMap<String,Double>> ();
	private XYChart.Series[] montab = new XYChart.Series[20]; //On se limite à 20 entreprises maximum

  public Affichage() {
		super();
		
		HashMap<String,Double> m1 = new HashMap<String,Double>();
		HashMap<String,Double> m2 = new HashMap<String,Double>();
		HashMap<String,Double> m3 = new HashMap<String,Double>();
		HashMap<String,Double> m4 = new HashMap<String,Double>();



		m1.put("Apple",250d);
		m1.put("Microsoft",230d);
		m1.put("Mercedes",164d);
		m1.put("SpaceX",500d);


		m2.put("Apple",280d);
		m2.put("Microsoft",250d);
		m2.put("Mercedes",264d);
		m2.put("SpaceX",400d);



		m3.put("Apple",350d);
		m3.put("Microsoft",130d);
		m3.put("Mercedes",364d);
		m3.put("SpaceX",700d);


		m4.put("Apple",750d);
		m4.put("Microsoft",530d);
		m4.put("Mercedes",464d);
		m4.put("SpaceX",600d);


		prixParEntreprise.add(m1);
		prixParEntreprise.add(m2);
		prixParEntreprise.add(m3);
		prixParEntreprise.add(m4);
		
		
		System.out.println("ajout avec succes");
	}
  
  
  


	public Affichage(ArrayList<HashMap<String, Double>> prixParEntreprise) { //La bourse lui transmet son ArrayList pour faire l'affichage
		super();
		this.prixParEntreprise = prixParEntreprise;
	}





	@Override public void start(Stage stage) {
  	
      stage.setTitle("Evolution des prix");
      final CategoryAxis xAxis = new CategoryAxis();
      final NumberAxis yAxis = new NumberAxis();
       xAxis.setLabel("Jours");
       yAxis.setLabel("Prix");
       xAxis.autosize();
       yAxis.autosize();
      final LineChart<String,Number> lineChart =  new LineChart<String,Number>(xAxis,yAxis);
      lineChart.setTitle("Evolution des prix");
        
      Set<String> nomEntreprises = prixParEntreprise.get(0).keySet();

      XYChart.Series series = null;
      int index=0;
		
      for (String nom : nomEntreprises) {
			
			 series = new XYChart.Series();
			 series.setName(nom);
			 
			 
	       for (int i=0; i < prixParEntreprise.size(); i++)  {
		 		
	      		series.getData().add( new XYChart.Data(i+"-"+nom, prixParEntreprise.get(i).get(nom)));
	      		
	      	}
	        
	       montab[index++]=series;
	        
		}
      

		for(int i=0;i<index;i++)
		  lineChart.getData().add(montab[i]);

      Scene scene  = new Scene(lineChart,800,600);              
      stage.setScene(scene);
      stage.show();
  }


  public static void main(String[] args) {
      launch(args);
  }
}