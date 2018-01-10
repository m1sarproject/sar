package com.m1sar;

import java.io.Serializable;

public abstract class Ordre implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874466353411594169L;
	private String entreprise;
	private String client; //prix proposé par le client
	private int quantite;
	private double prix_Propose_par_Client;
	private int quantiteClient;
	private String nomCourtier;
	private int id;
	public boolean estAccepte=false;
	private static int nb=0;
	
	public boolean isEstAccepte() {
		return estAccepte;
	}

	public void setEstAccepte(boolean estAccepte) {
		this.estAccepte = estAccepte;
	}

	public String getNomCourtier() {
		return nomCourtier;
	}

	public void setNomCourtier(String nomCourtier) {
		this.nomCourtier = nomCourtier;
	}
	

	public Ordre(String entreprise, String client, double prix_Propose_par_Client,int quantite,String nom){
		this.prix_Propose_par_Client=prix_Propose_par_Client;
		this.entreprise = entreprise;
		id=++nb;
		this.quantiteClient = quantite;
		this.nomCourtier = nom;
		this.client=client;
		
		
	}

	public String getEntrepriseName() {
		return entreprise;
	}

	public double getPrixUnitaire() {
		return prix_Propose_par_Client;
	}


	public int getQuantite() {
		return quantite;
	}
	
	
	public void setEstFini() {
		this.estAccepte= true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Ordre [entreprise=" + entreprise + ", client=" + client +  quantite + ", prix_Propose_par_Client=" + prix_Propose_par_Client
				+ ", quantiteClient=" + quantiteClient + ", nomCourtier=" + nomCourtier + "]";
	}
	

	
	 

}
