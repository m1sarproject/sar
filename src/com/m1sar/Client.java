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
	private double tauxDeComission=0.1;
	private Map<String,Double> prixBoursePourEntreprise;
	private double depensesEventuelles;
	private int quantiteEventuelleVendu;
	private String nameCourtier;

	
	public Client(String nameClient, double solde,int port,InetAddress hte) {
		
		this.nameClient = nameClient+idClient;
		this.solde = solde;
		portefeuille=new HashMap<>();
		ordres =new ArrayList<>(); //Inutile de le changer en vector, la liste des ordres est propre au client, donc pas d'accès concurrent à cet attribut
		idClient=cpt++;
		this.port = port;
		this.hote = hte;
		connexion();
		readStateStocks();
		echangeOrdresClientCourtier();
	}
	
	 /**@author Vitalina
     * 
     * 
     */
	public void connexion(){
		
			try {
			//connexion � la bourse
			sc= new Socket(hote,port);
			outS=sc.getOutputStream();
			inS=sc.getInputStream();
			//in =new BufferedReader(new InputStreamReader(inS));
			//out=new PrintWriter(outS,true);
			outObject= new ObjectOutputStream(outS);
			inObject= new ObjectInputStream(inS);
			////recupere les numeros de port et @ip du courtier inetaddress et se connecte au courtier
			InetAddress host=(InetAddress)inObject.readObject();
			int portCourtier=inObject.readInt();
			sc.close();
			Socket connexionCourtier=new Socket(host, portCourtier);
			System.out.println("conenxion au courtier reussi");
			outObject=new ObjectOutputStream(connexionCourtier.getOutputStream());
			inObject = new ObjectInputStream(connexionCourtier.getInputStream());
			inscription(); 
			nameCourtier= (String) inObject.readObject();
			System.out.println("message du courtier "+nameCourtier+" : "+(String)inObject.readObject());
			
			//cpt=0;
			//System.out.println("Client "+nameClient+" veut se connecter");
			//String reponse,req;
			//reponse=(String) inObject.readObject();
			
			//System.out.println("Courtier  repond : "+reponse);
			//Scanner lect = new Scanner(System.in);
			}
			
			
			 //l'exception venait du fait que le client se deconnecte alors que dans threadCourtier on 
			 //essaye de lire ce qu'on voit le client
			 /*
			 while(cpt <3) {
				 System.out.println("Envoyer Ordre au Courtier ou bye : ");
				 	reponse=lect.nextLine();
				 	out.println(reponse);
				    System.out.println("Donnez l'ordre a creer a ou v: ");
				    req=lect.nextLine();
					//out.println(req);
				    outObject= new ObjectOutputStream(outS);
				    if(req.equals("a")) {
				    	System.out.println("Donnez le nom de l Entreprise");
				    	req=lect.nextLine();
				    	outObject.writeObject(new OrdreAchat(req, this.nameClient, 12.0, 50));
				    	outObject.flush();
				    	
				    }
				    if(req.equals("v")) {
				    	System.out.println("Donnez le nom de l Entreprise");
				    	req=lect.nextLine();
				    	outObject.writeObject(new OrdreVente(req, this.nameClient, 12.0, 50));
				    	outObject.flush();
				    }
				    reponse=in.readLine();
				    System.out.println("le courtier a repondu "+reponse);
				    cpt++;
				    //in.readLine();				   
			 	}
			 
			  out.println("bye");//mettre fin aux echanges
			 
			} */
			
			catch (Exception e) {
				
			}
	}
	
	
	
	public void inscription() {
		
		try {
			outObject.writeObject(nameClient);
			outObject.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
	
	
/**@author Vitalina
     * 
     * envoie un ordre achat qui on vientr de creer
     */
	public Ordre acheter (double prix, int quantite, String entreprise){
		if (! achatLegal(entreprise,prix*quantite,quantite)) {
			System.out.println("cet Achat n est pas legal");
			return null;
		}
		
		depensesEventuelles+=(prix*quantite);
		Ordre r =new OrdreAchat(entreprise, this.nameClient, prix,quantite,nameCourtier);
		ordres.add(r);
		return r;
		
		
		

	}
	
	
	/**@author Vitalina
     * 
     * envoie un ordre vente qui on vientr de creer
     */
	public Ordre vendre (double prix, int quantite, String entreprise){
		if(portefeuille.size()==0)return null;
		if ( ! venteLegal(entreprise,quantite) ) {
			System.out.println("ce Vente n est pas legal");
			return null;
		}
		
		quantiteEventuelleVendu+=quantite;
		Ordre r =new OrdreVente(entreprise, this.nameClient, prix,quantite,nameCourtier);
		ordres.add(r);
		return r;
		
	}
	
	//changement ajout de Produit et le traitement des reponses de la bourse avec nb=3 max ordres a envoyer 
	
	public void echangeOrdresClientCourtier() {
		Scanner lect = new Scanner(System.in);
		Ordre ordre;
		try {
			System.out.println("Hello mon Courtier je vais t envoyer des ORDRES");
			System.out.print("Donnez le nbOrdres a creer : ");
			int nbOrdre=lect.nextInt();
			lect.nextLine();
			for(int i=1; i<=nbOrdre;i++){
				if(i%4!=0 ){
					System.out.print("Donnez l Ordre a cree 'v'-Vente ou 'a'-Achat : ");
					String r=lect.nextLine();
					System.out.print("Donnez le prix : ");
					Double prix=lect.nextDouble();
					lect.nextLine();
					System.out.print("Donnez le nb actions a acheter : ");
					int nbActions=lect.nextInt();
					lect.nextLine();
					System.out.print("Donnez le nom de l entreprise : ");
					String nom_entreprise=lect.nextLine();
					if(r.equals("a")){
						
						ordre=acheter(prix, nbActions, nom_entreprise);
						Produir(ordre);
						System.out.println("OrdreAchat bien envoyer");
					}
					if(r.equals("v")){
						ordre=vendre(prix, nbActions, nom_entreprise);
						Produir(ordre);
						System.out.println("OrdreVente bien envoyer");
					}
				}
				if(i==3){
					System.out.println("J attends la reponse de la Bourse");
					for(int j=0;j<3;j++){
						int idOrdre = (int) inObject.readObject();
						boolean yesOuNon=(boolean) inObject.readObject();
						getReponseBource(idOrdre,yesOuNon);
						
					}
				if(i<=nbOrdre){
					System.out.print("Donnez l Ordre a cree 'v'-Vente ou 'a'-Achat : ");
					String r=lect.nextLine();
					System.out.print("Donnez le prix : ");
					Double prix=lect.nextDouble();
					System.out.print("Donnez le nb actions a acheter : ");
					int nbActions=lect.nextInt();
					System.out.print("Donnez le nom de l entreprise : ");
					String nom_entreprise=lect.nextLine();
					if(r.equals("a")){
						
						ordre=acheter(prix, nbActions, nom_entreprise);
						Produir(ordre);
						System.out.println("OrdreAchat bien envoyer");
					}
					if(r.equals("v")){
						ordre=vendre(prix, nbActions, nom_entreprise);
						Produir(ordre);
						System.out.println("OrdreVente bien envoyer");
					}
				}
					
				}
				
			}
		outObject.writeObject(new String("bye"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public boolean venteLegal(String entreprise,int quantite){
		if(!portefeuille.containsKey(entreprise))return false;
		return (portefeuille.get(entreprise)-quantiteEventuelleVendu)<quantite;
	}
	
	/**@author Vitalina
     * 
     * 
     */
	public boolean achatLegal(String entreprise,double prix,int quantite){
		
		double prixReel = prix + tauxDeComission * prix ;
		//System.out.println("PrixdeApple = "+prixBoursePourEntreprise.get(entreprise));
		boolean cond1=solde - depensesEventuelles  > prixReel;
		//System.out.println("solde-depenses = "+(solde - depensesEventuelles));
		//boolean cond2=prixReel>(quantite*prixBoursePourEntreprise.get(entreprise));
		//System.out.println("prixrplus garend de E = "+(quantite*prixBoursePourEntreprise.get(entreprise)));
		return cond1 ;//&&cond2;
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
	
	
	public Ordre getOrderById(int id) {
		Ordre res=null;
		for(Ordre t : ordres) {
			if(t.getId()==id)res=t;
		}
		return res;
	}
	
	public void getReponseBource(int idOrdre, boolean yesOuNon) {
		Ordre r=getOrderById(idOrdre);
		if(yesOuNon) {
			if(r instanceof OrdreAchat) {
				majPortefeuilleAchat(r);
				depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantite());
				
			}
			if(r instanceof OrdreVente) {
				majPortefeuilleVente(r);
				quantiteEventuelleVendu-=r.getQuantite();
			}
		}
		if(r instanceof OrdreAchat) {
			depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantite());
		}
		if(r instanceof OrdreVente) {
			quantiteEventuelleVendu-=r.getQuantite();
		}
		ordres.remove(r);
		
		
	}
	
	
	
	public void majPortefeuilleAchat(Ordre r){
			double prixR=r.getPrixUnitaire()*r.getQuantite();
			solde-=(prixR+(prixR*tauxDeComission));
			
			if(portefeuille.containsKey(r.getEntrepriseName())){
				int i=portefeuille.get(r.getEntrepriseName());
				portefeuille.replace(r.getEntrepriseName(), r.getQuantite()+i);
			}
			else{
				portefeuille.put(r.getEntrepriseName(), r.getQuantite());
			}
		
	}
		
	
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void majPortefeuilleVente(Ordre r){
		
		if(!portefeuille.containsKey(r.getEntrepriseName()))return;
		double prixR=r.getPrixUnitaire()*r.getQuantite();
		solde+=(prixR-(prixR*tauxDeComission));
		int i=portefeuille.get(r.getEntrepriseName());
		if(i==r.getQuantite())portefeuille.remove(r.getEntrepriseName());
			
		else{
			portefeuille.replace(r.getEntrepriseName(), i-r.getQuantite());
		}
		
		
	}
	
	
	/**@author Vitalina
     * 
     * 
     */
	public void readStateStocks(){
		
		
		try {
			
			System.out.println("Client "+nameClient+" veut savoit l etat du marche");
			
			//recupère le vecteur eavec des entreprise de Bourse
			prixBoursePourEntreprise =  (HashMap<String, Double>) inObject.readObject();
			System.out.println("Voila l etat du marche : ");
			System.out.println(prixBoursePourEntreprise);
			
		}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
	
	//produir envoie des ordres qui on vient de creer
	public void Produir(Ordre r){
		if(r instanceof OrdreAchat ){
			System.out.println("Client "+nameClient+" envoie un Ordre d Achat au courtier");
		}
		if(r instanceof OrdreVente){
			System.out.println("Client "+nameClient+" envoie un Ordre de Vente au courtier");
			
		}
		try {
			outObject.writeObject(r);
			outObject.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException{

	
		int nport = Integer.parseInt(args[0]);
		InetAddress hote = InetAddress.getByName(args[1]);
		Client client = new Client ("vitalinka",210.0,nport,hote);
		
		
	}
	
	
	
	

}
