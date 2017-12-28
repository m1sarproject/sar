package com.m1sar;

public abstract class Ordre{
	

	private Entreprise entreprise;
	private Client client;
	private double prix_Proposé_par_Client;

	private int id;
	private static int nb=1;
	
	public Ordre(Entreprise entreprise, Client client, double prix_Proposé_par_Client){
		this.prix_Proposé_par_Client=prix_Proposé_par_Client;
		this.entreprise = entreprise;
		id=++nb;
		
		
	}
	 

}