package com.m1sar;

import java.util.ArrayList;

public class Entreprise{

	private String name;
	private int nbActions;
	private ArrayList<Ordre> ordres;
	private double prixUnitaireAction;
	
	 
	public Entreprise(String name, int nbActions,double prixUnitaireAction){
		this.name=name;
		this.nbActions=nbActions;
		this.prixUnitaireAction=prixUnitaireAction;
		ordres = new ArrayList<Ordre>();
	}
	
	public int getNbActions(){
		return nbActions;
	}
	
	public double getPrixUnitaireAction(){
		return prixUnitaireAction;
	}
	
	public void setNbActions(int n){
		nbActions=n;
	}
	public String getName(){
		return name;
	}
	
	public void setPrixUnitaireAction(double n){
		prixUnitaireAction=n;
	}
	public String toString(){
		return "Entreprise : "+ name +" a mis à disposition : "+ nbActions + " d'actions sur le marché au prix unitaire suivant :"+prixUnitaireAction;
	}
	
}
