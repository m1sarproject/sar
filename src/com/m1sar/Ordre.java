package com.m1sar;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unused")

public abstract class Ordre implements Serializable {
	

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874466353411594169L;
	/** Name of the company concerned by the order */
	private String entreprise;
	/** Name of the client concerned by the order */
	private String client; //prix proposé par le client
	/** Quantity wanted by the client */
	private int quantite;
	/** Unit Price */
	private double prixUnitaire; //prix proposé par le client
	
	/** Price suggested  by the client */
	private double prixPropose;
	/** Quantity wanted by the client */
	private int quantiteClient;
	/** Name (id) of the broker who works with this client*/
	private String nomCourtier;
	/** id of the order */
	private int id;
	/** Tells if the order is finished */
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
		this.prixPropose=prix_Propose_par_Client;
		this.entreprise = entreprise;
		this.client=client;
		id=++nb;
		this.quantiteClient = quantite;
		this.nomCourtier = nom;
		this.client=client;
		
		
	}

	public String getEntrepriseName() {
		return entreprise;
	}

	public double getPrixUnitaire() {
		return prixPropose;
	}


	
	/**@author Lyes
     * Sets the order as an accomplished order
     */ 
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
		return "Ordre [entreprise=" + entreprise + ", client=" + client + ", prixUnitaire=" + prixUnitaire
				+ ", prix_Propose_par_Client=" + prixPropose + ", quantiteClient=" + quantiteClient
				+ ", nomCourtier=" + nomCourtier + ", estAccepte=" + estAccepte + "]";

	}
	

	public int getQuantiteClient() {
		return quantiteClient;
	}

	public void setQuantiteClient(int quantiteClient) {
		this.quantiteClient = quantiteClient;
	}

	public String getClientName() {
		return client;
	}

	public void setClientName(String client) {
		this.client = client;
	}
	
	
}
