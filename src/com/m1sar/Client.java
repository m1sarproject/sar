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


/**@author Vitalina
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
	
	
	public Client(String nameClient, double solde) {
		
		
		this.solde = solde;
		portefeuille=new HashMap<>();
		ordres =new ArrayList<>();
		idClient=cpt++;
		
	}
	
	 /**@author Vitalina
     * 
     * 
     */
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
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void acheter (double prix, int quantite, Entreprise entreprise){
		
		solde-=prix+(prix*courtier.getTauxCommission());
		Ordre r =new OrdreAchat(entreprise, this, 11);
		ordres.add(r);
		
	}
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void vendre (double prix, int quantite, Entreprise entreprise){
		solde+=prix;// enlever les commissions?
		Ordre r =new OrdreVente(entreprise, this, 11);
		ordres.add(r);
	}
	
	
	/**@author Vitalina
     * 
     * 
     */
	public boolean venteLegal(String entreprise,int quantite){
		if(!portefeuille.containsKey(entreprise))return false;
		return portefeuille.get(entreprise)<quantite;
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public boolean achatLegal(double prix){
		
		return solde> courtier.getTauxCommission();
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public void deconnexion(){
		//a completer 
	}
	/**@author Vitalina
     * 
     * 
     */
	public void majPortefeuilleAchat(String entreprise, int quantite){
		if(true){//si ok pour aquis de reception
			if(portefeuille.containsKey(entreprise)){
				int i=portefeuille.get(entreprise);
				portefeuille.replace(entreprise, quantite+i);
			}
			else{
				portefeuille.put(entreprise, quantite);
			}
		}
	}
		
	
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void majPortefeuilleVente(String entreprise, int quantite){
		if(true){
			if(!portefeuille.containsKey(entreprise))return;
			int i=portefeuille.get(entreprise);
			if(i==quantite)portefeuille.remove(entreprise);
			else{
			portefeuille.replace(entreprise, i-quantite);
			}
		}
		
	}
	
	public void setCourtier(Courtier c){
		courtier=c;
	}
	
	public Courtier getCourtier(){
		return courtier;
	}
	
	
	

}
