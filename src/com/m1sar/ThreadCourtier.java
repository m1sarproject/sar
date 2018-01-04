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
import java.util.List;
import java.util.Vector;

public class ThreadCourtier extends Thread {

//	private Socket sCourtier; //socket pour communiuqer avec courtier
	private Vector<Socket> sClient=new Vector<Socket>();//socket de communication avec les clients
	private List<String> clients= new ArrayList<String>();
	private Socket currentClient;
	private int nbCustomer=0;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	private BufferedReader in; 
	private PrintWriter out;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nomCourtier;
	private static int timeLimit=30000;//le temps qu'un courtier attend avant de se deconnecter
	
	
	public ThreadCourtier(Bourse b,String nom) {
		this.bourse=b;
		this.nomCourtier=nom;
		start();
		//a revoir 
	}
	
    @Override
    public void run() {

    	OutputStream outS;
		InputStream inS;
		String rep="",req="",nomclient="";
		int nb = 1;
		
    	while (true) { //ici quand tout ou clients (pas sur) se deco on sort du while  
   
    		if(sClient.size()>0) { //s'il y'a un client dans notre liste on commence par traiter ce client
    			
    			currentClient=sClient.firstElement();
    			System.out.println(currentClient);
    
    			try {
		    			inS=currentClient.getInputStream();
		    			outS=currentClient.getOutputStream();
		    			out=new PrintWriter(outS,true);
		    			in =new BufferedReader(new InputStreamReader(inS));
		    			System.out.println("client numéro "+nb+" connecte a ce courtier");
		    		    
		    			nomclient=in.readLine(); //Le premier message doit etre le nom du client
		    			clients.add(nomclient);
		    			System.out.println("Je suis "+nomCourtier+" le client "+ nomclient+" vient de s'inscrire");
		    			out.println("Bienvenu cher client, vous pouvez envoyez vos ordre");
		    			System.out.println("h1");
		    			
		    			while (true)  		    			//ici on mettra le traitement des ordres reçu par le client

		    			{
		    				System.out.println("h2");
							req=in.readLine();
							System.out.println("h3");
		    				System.out.println("le client me dit (courtier)"+req);
		    				
		    				if(req.equals("bye")) {
		    				//supprimer le client et fermer sa socket et decremente nbcustumer
		    					System.out.println("je suis dans le if du bye");
		    					majClient();
		    					break;
		    				}
		    			}
		    			
    			}
    			catch (IOException e) {

    				e.printStackTrace();
				}
    			
    			
    		
    	}
    		if(nbCustomer==0) {
	    		try {
	    			    System.out.println(prefixe() + "Je n'ai aucun client, J'attend si un client me contacte");
						Thread.sleep(timeLimit); //Le sleep a des défauts : si un client se connecte pendant le sleep, il ne le réveille pas du sleep; à revoir
					} 
	    		catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		
	    			
	    			System.out.println(prefixe() + "Je n'ai plus de clients, je me déconnecte de la bourse");
	    			bourse.removeBroker(this);
	    			break;//sortir du while(true)
	    		
	    		}
		    
					
			nb++;		
				
    }
    	//envoyer un message a la bourse
    	
    	System.out.println(prefixe() + "Le threadCourtier sort du while");
}
	
    	
    public void incNbClient() {
    	if (estDispo()) nbCustomer++;
    
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
