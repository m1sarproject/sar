package com.m1sar;

import java.util.ArrayList;

public class OrdreAchat extends Ordre{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7365828540245851085L;

	public OrdreAchat(String entreprise, String client, double prix_Propose_par_Client, int quantite,String nom){
		
		super(entreprise,client, prix_Propose_par_Client,  quantite, nom);
	}
	

}
