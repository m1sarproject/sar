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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Courtier extends Thread{


private String name;
/**
 * nb is used to ensure that the name of the broker is unique 
 */
private static int nb=1;
private Vector<Client> customers= new Vector<Client>();
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int id;
private int nbCustomer;
public static double tauxCommission=0.1; //un taux de 10% pour tous les courtiers
private double accountBalance=0.;
private int port;
private int portecoute;//sur lequel il ecoute les clients
private InetAddress hote;
private Socket sc;
private ServerSocket ecouteClient;
//stream de com avec la bourse
private InputStream inSB;
private OutputStream outSB;
private ObjectInputStream inObjectB;
private ObjectOutputStream outObjectB;
//stream de com avec le client
private OutputStream outSC;
private InputStream inSC;
private ObjectInputStream inObjectC;
private ObjectOutputStream outObjectC;
private Socket currentClient;
private static int timeLimit=15000;
/**
 * listeOrdre contient la liste d'ordre en attente du client courant
 */
private HashMap<String,ArrayList<Ordre>> listeOrdre=new HashMap<>();
private Map<String,Double> prixParEntreprise=new HashMap<String,Double>();
//private Vector<Socket> sClient=new Vector<Socket>();//socket de communication avec les clients
private List<String> clients= new ArrayList<String>();
BufferedReader in; 
PrintWriter out;

public Courtier(String name,int port,InetAddress hte) {
	
	id=nb;
	this.name=name;
	this.id = nb++;
	this.port = port;
	this.hote = hte;
	start();
}


/**
 * informe le client de la transaction (accord) effectuï¿½e pour qu'ils mettent ï¿½ jours leurs portefeuilles
 */
public void SendAccordInformation() {
	
}



public double getTauxCommission() {
	return tauxCommission;
}

public void connexion(){
	
	try {
	    sc= new Socket(hote,port);
	    inscription(sc);
	    //il écoute les clients sur un numéro de port 
		//accepte les connexions et échange avec eux
		System.out.println("j'essaye de récuperer le numéro de port surlequel j'écoute");
		outSB=sc.getOutputStream();
		outObjectB= new ObjectOutputStream(outSB);
		inSB=sc.getInputStream();//communication avec bourse
		inObjectB = new ObjectInputStream(inSB);
		this.portecoute=inObjectB.readInt();
		prixParEntreprise=(HashMap<String,Double>)inObjectB.readObject();
		System.err.println(prixParEntreprise);
		
	}
	catch (Exception e) {
		System.out.println("Erreur de connexion");
	}
}


//Permet simplement de s'inscrire auprès de la bourse en donnant son nom
public void inscription(Socket sc) throws IOException {
	
	OutputStream outS=sc.getOutputStream();
	out=new PrintWriter(outS,true);
	out.println(name);
	
}
public void run() {
	connexion();//se connecte à la bourse et recupere le numero de port surlequel il ecoute
	
	try {
		
		ecouteClient=new ServerSocket(portecoute);
		String nomclient="";
		int nb = 1;
    	while (true) { 
    		currentClient = ecouteClient.accept();
    		nbCustomer++;
    		ArrayList<Ordre> lordre=new ArrayList<>();
    		System.out.println("le client s'est connecte");

    			try {
		    			inSC=currentClient.getInputStream();
		    			outSC=currentClient.getOutputStream();
		    			inObjectC = new ObjectInputStream(inSC);
		    			outObjectC = new ObjectOutputStream(outSC);
		    			System.out.println("client numero "+nb+" s'est connecte a ce courtier");
		    			nomclient=(String)inObjectC.readObject(); //Le premier message doit etre le nom du client
		    			clients.add(nomclient);
		    			System.out.println(" le client "+ nomclient+" vient de s'inscrire");
		    			outObjectC.writeObject(new String("Bienvenu cher client, vous pouvez envoyez vos ordre"));
		    			outObjectC.flush();
		    			//envoyer la liste des prix au client
		    			sendPriceCompanies();
	
		    			while (true)  		 
		    			{	
		    				//reponse au threadcourtier
		    				System.out.println("Envoi en cours");
		    				String tosend = "e";
		    				outObjectB.writeObject(tosend);
		    				//REPONSE DE CLIENT
		    				Object req=inObjectC.readObject(); 
		    				if(req instanceof String) {
		    					String rep=(String)req;
		    					if(rep.equals("bye")) {
				    				//supprimer le client et fermer sa socket et decremente nbcustumer
				    					System.out.println("je suis dans le if du bye");
				    					//a modifier mettre le put quand le courtier reï¿½oit un accord pas ici
				    					transmettreOrdreABourse(lordre);
				    					//	a revoir cela 
				    					listeOrdre.put(nomclient, lordre);
				    					majClient();
				    					//est ce que c'est ici qu'on attend les réponses
				    					break;
				    				}
		    				}
		    				
		    				else {
		    				Ordre ordre = (Ordre) req; 
			    			System.out.println("Object received = " + ordre.getEntrepriseName());
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
    			
    			
    		
    	//} celui du if size
    		System.out.println("nbClinet = "+nbCustomer);
    		if(nbCustomer==0) {
	    		try {
	    			    System.out.println(prefixe() + "Je n'ai aucun client, J'attend si un client me contacte");
						Thread.sleep(timeLimit); //Le sleep a des dÃ©fauts : si un client se connecte pendant le sleep, il ne le rÃ©veille pas du sleep; Ã  revoir
					} 
	    		catch (InterruptedException e) {
						e.printStackTrace();
					}
    		}
	    		
    		if(nbCustomer==0) {	
	    			System.out.println(prefixe() + "Je n'ai plus de clients, je me deconnecte de la bourse");
	    			//envoyer à la bourse un message pour me deconnecter 
	    			//outObjectB.writeObject("deco");
	    			break;//sortir du while(true)
    		}
	    		
	    							
			nb++;	
			
				
    }
    	//envoyer un message a la bourse
    	
    	//System.out.println(prefixe() + "Le threadCourtier sort du while");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	
	
}
void majClient() throws IOException {
	
	currentClient.close();
	nbCustomer--;
	//avertir la bourse  que le nombre de client a été decrémenté 
	//outObject.writeObject("nbclient");//faire un if dans threadCourtier pour qu'il decremente
	//outObject.writeInt(nbCustomer);
	
}
public void sendPriceCompanies() throws IOException {//quand est ce que s'est fait? au dï¿½but de la journï¿½e avant qu'un client ne soit dï¿½co ;il faut ajouter un 

	outObjectC.writeObject(prixParEntreprise);
	outObjectC.flush();						//nombre pour reprï¿½senter les jours
	
}
/**
 * Calcule la commission 
 */
//a revoir une fois qu'on a fait les acceptations
public void CalculCommission(String nomClient) {
	ArrayList<Ordre>l=listeOrdre.get(nomClient);
	for(Ordre o:l) {
		if(o.estAccepte) {
		accountBalance+=o.getPrixUnitaire()*o.getQuantite()*tauxCommission;
		}
	}
}

public void transmettreOrdreABourse(ArrayList<Ordre> lordre) throws IOException {
	outObjectB.writeObject(lordre);
}

public String prefixe() {
	return name+" : ";
}
public static void main(String[] args) throws UnknownHostException {
	
	int nport = Integer.parseInt(args[0]);
	InetAddress hote = InetAddress.getByName(args[1]);
	
	Courtier b=new Courtier("George Soros",nport,hote);
	
	System.out.println("Le courtier s'est connecté à la bourse");
	
	/*Courtier c=new Courtier("Warren Buffet",nport,hote);
	c.connexion();*/

	}

}