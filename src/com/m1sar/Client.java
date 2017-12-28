package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
La classe Client qui peut acheter ou vendre des actions
*/
public class Client {

	public static int cpt=0;

	private int port=4999;
	static InetAddress hote;
	Socket sc;
	
	BufferedReader in; 
	PrintWriter out;
	private String nameClient;// il faut mettre final?
	private int idClient;
	private List<Ordre> ordres;
	private Map<String,Integer> portefeuille;
	private double solde;
	private Courtier courtier;
	private double depensesEventuelles;
	
	
	public Client(String nameClient, double solde, Courtier courtier) {
		
		this.nameClient = nameClient;
		this.solde = solde;
		this.courtier = courtier;
		portefeuille=new HashMap<>();
		ordres =new ArrayList<>();
		idClient=cpt++;
		
	}
	
	public void connexion(){
		
			try {
				
				sc= new Socket(hote,port);
		
			in =new BufferedReader(new InputStreamReader(sc.getInputStream()));
			out=new PrintWriter(sc.getOutputStream(),true);
			out.println("Client "+nameClient+" veut se connecter");
			String reponse;
			reponse=in.readLine();
			System.out.println("Courtier "+courtier+" repond "+reponse);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void acheter (double prix, int quantite, String entreprise){
		
		if (! achatLegal(prix*quantite)) return;
		
		depensesEventuelles+= prix * quantite;
		boolean existe=false;
		for (Entry<String, Integer> e : portefeuille.entrySet()){
			if(e.getKey().equals(entreprise))existe=true;
		}
		if(existe){
			int i=portefeuille.get(entreprise);
			portefeuille.replace(entreprise, quantite+i);
		}
		else{
			portefeuille.put(entreprise, quantite);
		}
		solde-=prix;
	}
	
	public void vendre (double prix, int quantite, String entreprise){
		
		if ( ! venteLegal(entreprise,quantite) ) return;
		 
		solde+=prix;
		for (Entry<String, Integer> e : portefeuille.entrySet()){
			if(e.getKey().equals(entreprise)){
				e.setValue(e.getValue()-quantite);
			}
		}
	}
	
	
	
	public boolean venteLegal(String entreprise,int quantite){
		
		return portefeuille.get(entreprise)<quantite;
	}
	
	
	public boolean achatLegal(double prix){
		double prixReel = prix + Courtier.tauxCommission * prix ;
		return solde - depensesEventuelles  > prixReel;
	}
	
	
	public void deconnexion(){
		//a completer 
	}
	
	public void majPortefeuille(){
		
	}
	
	
	
	

}
