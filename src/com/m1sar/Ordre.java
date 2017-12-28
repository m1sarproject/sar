package com.m1sar;

public abstract class Ordre{
	

	private String entreprise;
	private Client client;
	private double prix_Propose_par_Client;

	private int id;
	private static int nb=1;
	
	public Ordre(String entreprise, Client client, double prix_Propose_par_Client){
		this.prix_Propose_par_Client=prix_Propose_par_Client;
		this.entreprise = entreprise;
		id=++nb;
		
		
	}
	 

}
