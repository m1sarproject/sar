package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;




public class ThreadBourse extends Thread {

    private Socket sCourtier; //socket pour communiuqer avec courtier
	private Map<String,Double> prixParEntreprise=new HashMap<String,Double>();
	private int nbCustomer=0;
	private Bourse bourse;  //la bourse qui a cree le Threadcourtier 
	private OutputStream outS;
	private InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nomCourtier;
	private int nport;
	//le temps qu'un courtier attend avant de se deconnecter
	
	
	
	
	  public ThreadBourse(Socket sCourtier,int nport, Bourse b,String nom) {
			super();
			this.bourse=b;
			this.nomCourtier=nom;
			this.sCourtier = sCourtier;
			this.nport=nport;
			start();
		}
	  
	  
	  public void connexionCourtier(){
		  try {
				outS=sCourtier.getOutputStream();
				inS =sCourtier.getInputStream();
				outObject = new ObjectOutputStream(outS);
				inObject = new ObjectInputStream(inS);
				System.out.println("j'envoi le numero de port au courtier ");
				outObject.writeInt(nport);
				outObject.flush();
				outObject.writeObject(bourse.getPrixParEntreprise());
				outObject.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
	  }
    @Override
    public void run() {
    	
    	connexionCourtier();
    	//recuperer la liste des prix 
    	prixParEntreprise=bourse.getPrixParEntreprise();
   
    	Ordre ordre_client;
    	try {
			outObject.writeObject("Demande de service : etat du marche : 'm', envoyer ordres : 'e'" );
			outObject.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
    	while(true){
    		try {

					System.out.println("Bourse recoit un message de courtier");
					Object req=inObject.readObject();
					if(req instanceof String) {
						String info=(String)req;
						if(info.equals("decreClient")) {
							nbCustomer--;
						}
						if(info.equals("bye")) {
							//courtier se deconnecte  on enleve le threadCourtier de la liste
							bourse.removeBroker(this);
							break;//sortir du while
						}
					}
					else {
						ordre_client= (Ordre)req;
						System.out.println(" ordres recu: "+ordre_client.getEntrepriseName());
						//SurReceptionDe(ordre_client);
						bourse.Consommer(ordre_client);
					}			

					
				
    		} catch (IOException e) {
				e.printStackTrace();
			}
    		catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
    	}
			
    	
		
}
    /**
     * the brocker sends to his customers the information about the share parices of each company in the stock market  
     */
    public void sendPriceCompanies() throws IOException {//quand est ce que s'est fait? au debut de la journee avant qu'un client ne soit deco ;il faut ajouter un 
    	outObject=new ObjectOutputStream(outS);
    	outObject.writeObject(prixParEntreprise);
    	outObject.flush();						//nombre pour representer les jours
    	
    }
    /**
     * @param ordre the order passed by  the  customer 
     * send to the stock market the order  
     */

    public void SurReceptionDe(Ordre ordre) {
    	
		Transmettre(ordre);
    }
    
    
    public void Transmettre(Ordre o) {
    	
		bourse.getOrdres().add(o);
    }
    
    /**
     * Calcule la commission 
     */
   /* public void CalculCommission(String nomClient) {
    	ArrayList<Ordre>l=listeOrdre.get(nomClient);
    	for(Ordre o:l) {
    		if(o.estAccepte) {
    		accountBalance+=o.getPrixUnitaire()*o.getQuantite()*tauxCommission;
    		}
    	}
    }
    */

    public void incNbClient() {
    	if (estDispo()) {nbCustomer++; return;}
    
    	throw new UnsupportedOperationException("Le courtier a deja deux clients en charge");
    }
    public boolean estDispo() {
		return (nbCustomer<2);
	}
    public int getNport() {
		return nport;
	}
    public InetAddress getInetAddress() {
		return sCourtier.getInetAddress();
	}
	

	/*public void addClient(Socket client) throws IOException {
    	this.sClient.add(client);
    	incNbClient();
    	
    }*/
	
    /*
	ArrayList<Ordre> lordre=new ArrayList<>();

	
	
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
	    				Object req=inObject.readObject(); 
	    				if(req instanceof String) {
	    					String rep=(String)req;
	    					if(rep.equals("bye")) {
			    				//supprimer le client et fermer sa socket et decremente nbcustumer
			    					System.out.println("je suis dans le if du bye");
			    					//a modifier mettre le put quand le courtier re�oit un accord pas ici
			    					listeOrdre.put(nomclient, lordre);
			    					majClient();
			    					break;
			    				}
	    				}
	    				
	    				else {
	    				Ordre ordre = (Ordre) req; 
		    			System.out.println("Object received = " + ordre.getEntrepriseName());
		    			transmettreOrdreABourse(ordre);
		    			lordre.add(ordre);
		    			//enregistrer l'ordre pour ce client
	    				}
	    				//req=(String)inObject.readObject();
						

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
		
			
}*/
	//envoyer un message a la bourse
	
	//System.out.println(prefixe() + "Le threadCourtier sort du while");
	

	
}