package com.m1sar;

public class OrdreVente extends Ordre{


	/**
	 * 
	 */
	private static final long serialVersionUID = -1383902362187268176L;

	public OrdreVente(String entreprise, String client, double prix_Propose_par_Client, int quantite){
		
		super(entreprise,client, prix_Propose_par_Client,  quantite);
	}
	 
}
 