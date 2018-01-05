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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;


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
	OutputStream outS;
	InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nameClient;
	private int idClient;
	private List<Ordre> ordres;
	private Map<String,Integer> portefeuille;
	private double solde;
	private Courtier courtier;
	private Map<String,Double> prixBoursePourEntreprise;
	private double depensesEventuelles;
	
	private boolean yesOuNon;
	
	public Client(String nameClient, double solde,int port,InetAddress hte) {
		
		this.nameClient = nameClient+idClient;
		this.solde = solde;
		portefeuille=new HashMap<>();
		ordres =new ArrayList<>(); //Inutile de le changer en vector, la liste des ordres est propre au client, donc pas d'accès concurrent à cet attribut
		idClient=cpt++;
		this.port = port;
		this.hote = hte;
		connexion();
	}
	
	 /**@author Vitalina
     * 
     * 
     */
	public void connexion(){
		
			try {
			sc= new Socket(hote,port);
			 outS=sc.getOutputStream();
			 inS=sc.getInputStream();
			 in =new BufferedReader(new InputStreamReader(inS));
			 out=new PrintWriter(outS,true);
			 outObject= new ObjectOutputStream(outS);
			 //inObject= new ObjectInputStream(inS);
				
			
			inscription(); //Envoi son nom au courtier
			
			cpt=0;
			System.out.println("Client "+nameClient+" veut se connecter");
			String reponse,req;
			reponse=in.readLine();
			System.out.println("Courtier  repond : "+reponse);
			System.out.println("AVANT while ");
			 Scanner lect = new Scanner(System.in);
			 //l'exception venait du fait que le client se deconnecte alors que dans threadCourtier on 
			 //essaye de lire ce qu'on voit le client
			 
			 while(cpt <3) {
				 System.out.println("Envoyer Ordre au Courtier ou bye : ");
				 	reponse=lect.nextLine();
				 	out.println(reponse);
				    System.out.println("Donnez l'ordre a creer a ou v: ");
				    req=lect.nextLine();
					//out.println(req);
				    if(req.equals("a")) {
				    	System.out.println("Donnez le nom de l Entreprise");
				    	req=lect.nextLine();
				    	outObject.writeObject(new OrdreAchat(req, this.nameClient, 12.0, 50));
				    }
				    if(req.equals("v")) {
				    	System.out.println("Donnez le nom de l Entreprise");
				    	req=lect.nextLine();
				    	outObject.writeObject(new OrdreVente(req, this.nameClient, 12.0, 50));
				    }
				    reponse=in.readLine();
				    System.out.println("le courtier a repondu "+reponse);
				    cpt++;
				    //in.readLine();				   
			 	}
			 
			  out.println("bye");//mettre fin aux echanges
			 
			} 
			
			catch (Exception e) {
				
			}
	}
	
	
	
	public void inscription() {
		
		out.println(nameClient);
		
	}
	
	
/**@author Vitalina
     * 
     * 
     */
	public void acheter (double prix, int quantite, String entreprise){
		if (! achatLegal(entreprise,prix*quantite,quantite)) {
			System.out.println("cet Achat n est pas legal");
			return;
		}
		double prixR=prix*quantite;
		solde-=(prixR+(prixR*courtier.getTauxCommission()));
		Ordre r =new OrdreAchat(entreprise, this.nameClient, prix,quantite);
		ordres.add(r);
		
		
		System.out.println("Client "+nameClient+" envoie un Ordre d Achat au courtier");
		//ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			outObject.writeObject(r);
			outObject.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String reponse;
		try {
			reponse=in.readLine();
			if(reponse.equals("y")) {
				yesOuNon=true;
				ordres.remove(r);
				System.out.println("Ordre Achat est accepte par la Bourse");
			}
			else {
				yesOuNon=false;
				System.out.println("Ordre Achat est refuse par la Bourse");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void vendre (double prix, int quantite, String entreprise){
		if ( ! venteLegal(entreprise,quantite) ) {
			System.out.println("ce Vente n est pas legal");
			return;
		}
		double prixR=prix*quantite;
		solde+=(prixR-(prixR*courtier.getTauxCommission()));
		Ordre r =new OrdreVente(entreprise, this.nameClient, prix,quantite);
		ordres.add(r);
		System.out.println("Client "+nameClient+" envoie un Ordre de Vente au courtier");
		//ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			outObject.writeObject(r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reponse;
		try {
			reponse=in.readLine();
			if(reponse.equals("y")) {
				yesOuNon=true;
				ordres.remove(r);
				System.out.println("Ordre Vente est accepte par la Bourse");
				
			}
			else {
				yesOuNon=false;
				System.out.println("Ordre Vente est refuse par la Bourse");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public boolean achatLegal(String entreprise,double prix,int quantite){
		double prixReel = prix + courtier.getTauxCommission() * prix ;
		boolean cond1=solde - depensesEventuelles  > prixReel;
		boolean cond2=prixReel>(quantite*prixBoursePourEntreprise.get(entreprise));
		return cond1 &&cond2;
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public void deconnexion(){
		out.println("bye");
		System.out.println("Client "+nameClient+" se deconnecte");
		prixBoursePourEntreprise.clear();
		try {
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**@author Vitalina
     * 
     * 
     */
	public void majPortefeuilleAchat(String entreprise, int quantite){
		if(yesOuNon){//si ok pour aquis de reception
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
		if(yesOuNon){
			if(!portefeuille.containsKey(entreprise))return;
			int i=portefeuille.get(entreprise);
			if(i==quantite)portefeuille.remove(entreprise);
			else{
			portefeuille.replace(entreprise, i-quantite);
			}
		}
		
	}
	/**@author Vitalina
     * 
     * 
     */
	public void setCourtier(Courtier c){
		courtier=c;
	}
	/**@author Vitalina
     * 
     * 
     */
	public Courtier getCourtier(){
		return courtier;
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public void readStateStocks(){
		
		Vector<Entreprise> entreprises ;
		try {
			out.println("Client "+nameClient+" veut savoit l etat du marche");
			System.out.println("Client "+nameClient+" veut savoit l etat du marche");
			//ByteArrayInputStream bis = new ByteArrayInputStream(bytesFromSocket);
			//ObjectInputStream ois = new ObjectOutputStream(bis);
			//recupère le vecteur eavec des entreprise de Bourse
			entreprises =  (Vector<Entreprise>) inObject.readObject();
			System.out.println("Entreprises avec des prix recu par Client");
			for(Entreprise e: entreprises) {
				prixBoursePourEntreprise.put(e.getName(), e.getPrixUnitaireAction());
			}
			
		}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException{

	
		int nport = Integer.parseInt(args[0]);
		InetAddress hote = InetAddress.getByName(args[1]);
		Client client = new Client ("vitalinka",21d,nport,hote);
		
		
	}
	
	
	
	

}
