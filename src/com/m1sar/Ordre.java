package com.m1sar;

public abstract class Ordre{
	

	private Entreprise entreprise;
	private Client client;
	private double prix_Propos�_par_Client;

	private int id;
	private static int nb=1;
	
	public Ordre(Entreprise entreprise, Client client, double prix_Propos�_par_Client){
		this.prix_Propos�_par_Client=prix_Propos�_par_Client;
		this.entreprise = entreprise;
		id=++nb;
		
		
	}
	 

}