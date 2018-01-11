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
private int nbCustomer=0;
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
private ArrayList<Ordre> listeOrdre=new ArrayList<>();
private Map<String,Double> prixParEntreprise=new HashMap<String,Double>();
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
		System.out.println("j essaye de recuperer le numero de port surlequel j'ecoute");
		outSB=sc.getOutputStream();
		outObjectB= new ObjectOutputStream(outSB);
		inSB=sc.getInputStream();//communication avec bourse
		inObjectB = new ObjectInputStream(inSB);
		this.portecoute=inObjectB.readInt();
		prixParEntreprise=(HashMap<String,Double>)inObjectB.readObject();
		//System.err.println(prixParEntreprise);
		
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
	connexion();//se connecte a� la bourse et recupere le numero de port surlequel il ecoute
	
	try {
		
		ecouteClient=new ServerSocket(portecoute);
		String nomclient="";
		int nb = 1;
    	while (true) { 
    		currentClient = ecouteClient.accept();
    		//if (nb==1)nbCustomer++;
    		//System.out.println("nombre client après accepte"+nbCustomer);
    		System.out.println("le client s'est connecte");

    			try {
		    			inSC=currentClient.getInputStream();
		    			outSC=currentClient.getOutputStream();
		    			inObjectC = new ObjectInputStream(inSC);
		    			outObjectC = new ObjectOutputStream(outSC);
		    			System.out.println("client numero "+nb+" s'est connecte a ce courtier");
		    			nomclient=(String)inObjectC.readObject(); //Le premier message doit etre le nom du client
		    			System.out.println(" le client "+ nomclient+" vient de s'inscrire");
		    			outObjectC.writeObject(name);
		    			outObjectC.flush();
		    			outObjectC.writeObject(new String("Bienvenu cher client "+nomclient+", vous pouvez envoyez vos ordre"));
		    			outObjectC.flush();
		    			//envoyer la liste des prix au client
		    			sendPriceCompanies();
		    			int nOrdre=0;
		    			int nbOrdres=0;
		    			nbOrdres=(int) inObjectC.readObject();
		    			outObjectB.writeObject(nbOrdres);
		    			outObjectB.flush();
		    			while (true)  		 
		    			{	
		    				
		    				//REPONSE DE CLIENT
		    				Object req=inObjectC.readObject(); 
		    				if(req instanceof String) {
		    					String rep=(String)req;
		    					if(rep.equals("bye")) {
				    				//supprimer le client et fermer sa socket et decremente nbcustumer
				    					System.out.println("je suis dans le if du bye");
				    					//envoyer a bourse la mise a jours du nombre de client
				    					outObjectB.writeObject("decreClient");
				    					majClient();
				    					break;
				    				}
		    					if(rep.equals("null")){
		    						System.out.println("Je suis null courtier");
		    					    nbOrdres--;
		    					   outObjectB.writeObject("null");
		    					   outObjectB.flush();
		    					}
		    				}
		    				
		    				
		    				if(req instanceof Ordre){
		    					    nOrdre++;
		    					    nbOrdres--;
		    					    Ordre ordre = (Ordre) req; 
					    			System.out.println("Object received = " + ordre.getEntrepriseName());
					    			transmettreOrdreABourse(ordre);
					    			System.out.println("j'ai transmis");
			    					//	a revoir cela 
			    					listeOrdre.add(ordre);
			    					System.out.println(" nOrdre : "+nOrdre);
									System.out.println(" nbOrdres : "+nbOrdres);
		    				}
		    				if(nOrdre==3) {
		    					
		    					//j'ai envoyé les 3 ordres j'attends les acceptations de la bourse
		    					for(int j=0;j<3;j++) {
		    						System.out.println("j'attend réponse bourse");
				    				int idrecu=(Integer)inObjectB.readObject();
				    				boolean rep=(boolean)inObjectB.readObject();
				    				//commission 
				    				Ordre r=getOrderById(idrecu);
				    				CalculCommission(rep, r);
				    				System.out.println("mon solde après ordre qui est "+rep+" est "+accountBalance);
		    						outObjectC.writeObject(idrecu);
		    						outObjectC.writeObject(rep);
		    						
		    					}
		    					nOrdre=0;
		    				}
		    				if(nOrdre<3 && nbOrdres==0){
		    					for(int j=0;j<nOrdre;j++) {
		    						System.out.println("j'attend réponse bourse dans nbO==0");
				    				int idrecu=(Integer)inObjectB.readObject();
				    				boolean rep=(boolean)inObjectB.readObject();
				    				//commission 
				    				Ordre r=getOrderById(idrecu);
				    				CalculCommission(rep, r);
				    				System.out.println("mon solde après ordre qui est "+rep+" est "+accountBalance);
		    						outObjectC.writeObject(idrecu);
		    						outObjectC.writeObject(rep);
		    						
		    					}
		    					nOrdre=0;
		    				}	


		    			} 
		    		
    			}
    			catch (IOException e) {

    				e.printStackTrace();
				} 
    			catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
    			
    			
    		
    	//} celui du if size
    		System.out.println("nbClinet = "+nbCustomer);
    		if(nbCustomer<=0) {
	    		try {
	    			    System.out.println(prefixe() + "Je n'ai aucun client, J'attend si un client me contacte");
						Thread.sleep(timeLimit); //Le sleep a des de�aufauts : si un client se connecte pendant le sleep, il ne le reveille pas du sleep; sinon� revoir
						System.out.println("avant de recevori nbCustumer");
    					nbCustomer=inObjectB.readInt();
					} 
	    		catch (InterruptedException e) {
						e.printStackTrace();
					}
    		}
	    		
    		if(nbCustomer<=0) {	
	    			System.out.println(prefixe() + "Je n'ai plus de clients, je me deconnecte de la bourse"); 
	    			
	    			outObjectB.writeObject("bye");
	    			break;
    		}
	    		
	    							
			nb++;	
			
				
    }
    
    	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	
	
}
public Ordre getOrderById(int id) {
	Ordre res=null;
	for(Ordre t : listeOrdre) {
		if(t.getId()==id)res=t;
		
	}
	return res;
}
void majClient() throws IOException {
	nbCustomer--;
	currentClient.close();
	
}
public void sendPriceCompanies() throws IOException {//quand est ce que s'est fait? au dï¿½but de la journï¿½e avant qu'un client ne soit dï¿½co ;il faut ajouter un 

	outObjectC.writeObject(prixParEntreprise);
	outObjectC.flush();						//nombre pour reprï¿½senter les jours
	
}
/**
 * Calcule la commission 
 */
//a revoir une fois qu'on a fait les acceptations
public void CalculCommission(boolean rep,Ordre o) {
	if(rep) {
		accountBalance+=o.getPrixUnitaire()*o.getQuantiteClient()*tauxCommission;
		}
	
}

public void transmettreOrdreABourse( Ordre ordre) throws IOException {
	outObjectB.writeObject(ordre);
	outObjectB.flush();
}

public String prefixe() {
	return name+" : ";
}
public static void main(String[] args) throws UnknownHostException {
	
	int nport = Integer.parseInt(args[0]);
	InetAddress hote = InetAddress.getByName(args[1]);
	Scanner lect = new Scanner(System.in);
	System.out.println("Donnez le nom du courtier :");
	String nom=lect.nextLine();
	Courtier b=new Courtier(nom,nport,hote);
	
	System.out.println("Le courtier s'est connecté à la bourse");
	
	}

}