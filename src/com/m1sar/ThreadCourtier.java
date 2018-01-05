package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;




public class ThreadCourtier extends Thread {

//	private Socket sCourtier; //socket pour communiuqer avec courtier
	//contient la liste d'ordre en attente des differents clients
	private HashMap<String,ArrayList<Ordre>> listeOrdre=new HashMap<>();
	private Vector<Socket> sClient=new Vector<Socket>();//socket de communication avec les clients
	private List<String> clients= new ArrayList<String>();
	private HashMap<String,Double> prixParEntreprise=new HashMap<String,Double>();
	private Socket currentClient;
	private int nbCustomer=0;
	public static double tauxCommission=0.1; //un taux de 10% pour tous les courtiers
	private double accountBalance=0.;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	//private BufferedReader in; 
	//private PrintWriter out;
	private OutputStream outS;
	private InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nomCourtier;
	private static int timeLimit=15000;//le temps qu'un courtier attend avant de se deconnecter
	
	
	public ThreadCourtier(Bourse b,String nom) {
		this.bourse=b;
		this.nomCourtier=nom;
		start();
		//a revoir 
	}
	
    @Override
    public void run() {

		String rep="",req="",nomclient="";
		int nb = 1;

		ArrayList<Ordre> lordre=new ArrayList<>();

		//r�cuperer la liste des prix 
		prixParEntreprise=bourse.getPrixParEntreprise();
		
    	while (true) { //ici quand tout ou clients (pas sur) se deco on sort du while  
   
    		if(sClient.size()>0) { //s'il y'a un client dans notre liste on commence par traiter ce client
    			
    			currentClient=sClient.firstElement();
    			System.out.println(currentClient);
    
    			try {
		    			inS=currentClient.getInputStream();
		    			outS=currentClient.getOutputStream();
		    			inObject = new ObjectInputStream(inS);
		    			outObject = new ObjectOutputStream(outS);
		    			//out=new PrintWriter(outS,true);
		    			//in =new BufferedReader(new InputStreamReader(inS));
		    			System.out.println("client numéro "+nb+" connecte a ce courtier");
		    			nomclient=(String)inObject.readObject(); //Le premier message doit etre le nom du client
		    			clients.add(nomclient);
		    			System.out.println("Je suis "+nomCourtier+" le client "+ nomclient+" vient de s'inscrire");
		    			outObject.writeObject(new String("Bienvenu cher client, vous pouvez envoyez vos ordre"));
		    			outObject.flush();

		    			//out.println("Bienvenu cher client, vous pouvez envoyez vos ordre");
		    			//envoyer la liste des prix au client
		    			sendPriceCompanies();

		    				    		
		    			while (true)  		    			//ici on mettra le traitement des ordres reçu par le client
		    			{
		    				
		    				
		    				System.out.println("JE SUIS DANS 2 VAL");
		    				if(req.equals("bye")) {
		    				//supprimer le client et fermer sa socket et decremente nbcustumer
		    					System.out.println("je suis dans le if du bye");
		    					//a modifier mettre le put quand le courtier re�oit un accord pas ici
		    					listeOrdre.put(nomclient, lordre);
		    					majClient();
		    					break;
		    				}

		    				else {
		    				Ordre ordre = (Ordre) inObject.readObject(); //Le serveur doit connaitre la classe, et doit faire un cast
			    			System.out.println("Object received = " + ordre.getEntrepriseName());
			    			transmettreOrdreABourse(ordre);
			    			lordre.add(ordre);
			    			//enregistrer l'ordre pour ce client
		    				}
		    				req=(String)inObject.readObject();
							

		    			}
		    		
    			}
    			catch (IOException e) {

    				e.printStackTrace();
				} 
    			catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    		
    	}
    		System.out.println("nbClinet = "+nbCustomer);
    		if(nbCustomer==0) {
	    		try {
	    			    System.out.println(prefixe() + "Je n'ai aucun client, J'attend si un client me contacte");
						Thread.sleep(timeLimit); //Le sleep a des défauts : si un client se connecte pendant le sleep, il ne le réveille pas du sleep; à revoir
					} 
	    		catch (InterruptedException e) {
						e.printStackTrace();
					}
    		}
	    		
    		if(nbCustomer==0) {	
	    			System.out.println(prefixe() + "Je n'ai plus de clients, je me déconnecte de la bourse");
	    			bourse.removeBroker(this);
	    			break;//sortir du while(true)
    		}
	    		
	    							
			nb++;	
			
				
    }
    	//envoyer un message a la bourse
    	
    	System.out.println(prefixe() + "Le threadCourtier sort du while");
}
    /**
     * the brocker sends to his customers the information about the share parices of each company in the stock market  
     */
    public void sendPriceCompanies() throws IOException {//quand est ce que s'est fait? au d�but de la journ�e avant qu'un client ne soit d�co ;il faut ajouter un 
    	outObject=new ObjectOutputStream(outS);
    	outObject.writeObject(prixParEntreprise);
    	outObject.flush();						//nombre pour repr�senter les jours
    	
    }
    /**
     * @param ordre the order passed by  the  customer 
     * send to the stock market the order  
     */
    public void transmettreOrdreABourse(Ordre ordre) {
    	Entreprise e=bourse.getByName(ordre.getEntrepriseName());
		e.addOrder(ordre);//ajouter l'ordre dans entreprise
    }
    
    /**
     * Calcule la commission 
     */
    public void CalculCommission(String nomClient) {
    	ArrayList<Ordre>l=listeOrdre.get(nomClient);
    	for(Ordre o:l) {
    		if(o.estAccepte) {
    		accountBalance+=o.getPrixUnitaire()*o.getQuantite()*tauxCommission;
    		}
    	}
    }
    
    public void incNbClient() {
    	if (estDispo()) {nbCustomer++; return;}
    
    	throw new UnsupportedOperationException("Le courtier a déja deux clients en charge");
    }
    public boolean estDispo() {
		return (nbCustomer<2);
	}

	public void addClient(Socket client) throws IOException {
    	this.sClient.add(client);
    	incNbClient();
    	
    }
	
	
	public String prefixe() {
		return nomCourtier+" : ";
	}

	void majClient() throws IOException {
		
		currentClient.close();
		sClient.remove(currentClient);
		nbCustomer--;
		
	}
}