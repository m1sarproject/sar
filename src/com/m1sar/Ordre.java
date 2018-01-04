package com.m1sar;

import java.io.Serializable;

public abstract class Ordre implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874466353411594169L;
	private String entreprise;
	private Client client;
	private double prix_Propose_par_Client;

	private int id;
	private static int nb=0;
	
	public Ordre(String entreprise, Client client, double prix_Propose_par_Client){
		this.prix_Propose_par_Client=prix_Propose_par_Client;
		this.entreprise = entreprise;
		id=++nb;
		
		
	}

	public String getEntrepriseName() {
		return entreprise;
	}

	public double getPrix_Propose_par_Client() {
		return prix_Propose_par_Client;
	}
	
	
	 

}
