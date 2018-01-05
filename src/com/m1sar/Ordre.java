package com.m1sar;

import java.io.Serializable;

public abstract class Ordre implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874466353411594169L;
	private String entreprise;
	private String client;
	private double prixUnitaire; //prix proposé par le client
	private int quantite;
	private int id;
	public boolean estFini=false;
	private static int nb=0;
	

	public Ordre(String entreprise, String client, double prix_Propose_par_Client,int quantite){
		this.prixUnitaire=prix_Propose_par_Client;
		this.entreprise = entreprise;
		this.quantite = quantite;
		id=++nb;
		
		
	}


	public String getEntreprise() {
		return entreprise;
	}

	public void setEntreprise(String entreprise) {
		this.entreprise = entreprise;
	}
	

	public String getEntrepriseName() {
		return entreprise;
	}

	public double getPrixUnitaire() {
		return prixUnitaire;
	}


	public int getQuantite() {
		return quantite;
	}
	
	
	public void setEstFini() {
		this.estFini = true;
	}
	
	 

}
