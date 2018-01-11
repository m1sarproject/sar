package com.m1sar;

import java.util.ArrayList;
import java.util.Vector;

public class Entreprise {

	private String name;
	private int nbActions;
	private Vector<Ordre> ordres;
	private double prixUnitaireAction;
	private int nbDemandesAchats;
	private int nbDemandesVentes;
	 
	public int getNbDemandesAchats() {
		return nbDemandesAchats;
	}

	public void setNbDemandesAchats(int nbDemandesAchats) {
		this.nbDemandesAchats = nbDemandesAchats;
	}

	public int getNbDemandeVentes() {
		return nbDemandesVentes;
	}

	public void setNbDemandeVentes(int nbDemandeVentes) {
		this.nbDemandesVentes = nbDemandeVentes;
	}

	public Entreprise(String name, int nbActions,double prixUnitaireAction){
		this.name=name;
		this.nbActions=nbActions;
		this.prixUnitaireAction=prixUnitaireAction;
		ordres = new Vector<Ordre>();
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
	
	
	public void addOrder(Ordre o) {
		
		ordres.add(o);
	}
	
	public void DecreaseNbActions(int n) {
		
		nbActions -= n;
		
	}

	public Vector<Ordre> getOrdres() {
		return ordres;
	}
	
	
	
	public void decreaseNbAction(int n) {
		
		nbActions -= n;
	}
	
	public void incDemandesAchat() {
		
		nbDemandesAchats++;
		
	}

	public void decDemandesAchat() {
		
		nbDemandesAchats--;
	}
	
	
	public void incDemandesVentes() {
		
		nbDemandesVentes++;
		
	}

	public void decDemandesVentes() {
		
		nbDemandesVentes--;
	}
	
}
