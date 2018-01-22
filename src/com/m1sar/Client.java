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

@SuppressWarnings("unused")
public class Client {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	public static int cpt=0;

	
	private int port=4999;
	static InetAddress hote;
	Socket sc;
	
	
	OutputStream outS;
	InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	/** Name of the current client */
	private String nameClient;
	/** id of the current client */
	private int idClient;
	/** List of orders that belongs to the current client */
	private List<Ordre> ordres;
	/** Wallet of the current client */
	private Map<String,Integer> portefeuille;
	/** Current amount of the current client */
	private double solde;
	/** % of his Broker */
	private double tauxDeComission=0.1;
	/** What the client knows of the state of the market */
	private Map<String,Double> prixBoursePourEntreprise;
	/** When the client orders, he keeps his money until his brokers says that's OK, we use this double to compute it  */
	private double depensesEventuelles;
	/** Same logic but for selling orders  */
	private int quantiteEventuelleVendu;
	private boolean connecte=false;
	/** Name of his broker */
	private String nameCourtier;

	
	public Client(String nameClient, double solde,int port,InetAddress hte) {
		
		this.nameClient = nameClient+idClient;
		this.solde = solde;
		portefeuille=new HashMap<>();
		ordres =new ArrayList<>(); 
		idClient=cpt++;
		this.port = port;
		this.hote = hte;
		connexion();
		if(connecte==true) {
			readStateStocks();
			echangeOrdresClientCourtier();
			
		} 
		else {
			System.out.println(ANSI_RED+"Aucun courtier n'est disponible"+ANSI_RESET);
		}
	}
	 /**@author Vitalina
     * make connection with ThreadBource which find for this client 
     * Courtier available and client obtain IP and host of this Courtier 
     * if any Courtier is available client is disconnected 
     */
	public void connexion() {
		
			try {
			sc= new Socket(hote,port);
			outS=sc.getOutputStream();
			inS=sc.getInputStream();
			outObject= new ObjectOutputStream(outS);
			inObject= new ObjectInputStream(inS);
			Object o=inObject.readObject();
			if(!(o instanceof String)) {
				connecte=true;
				InetAddress host=(InetAddress)o;
				int portCourtier=inObject.readInt();
				sc.close();
				Socket connexionCourtier=new Socket(host, portCourtier);
				System.out.println("connexion au courtier reussi");
				outObject=new ObjectOutputStream(connexionCourtier.getOutputStream());
				inObject = new ObjectInputStream(connexionCourtier.getInputStream());
				inscription(); 
				nameCourtier= (String) inObject.readObject();
				System.out.println(nameCourtier+" : "+(String)inObject.readObject());
			  }
					
			}

			catch (Exception e) {
				
			}
	}
	
	/**@author Vitalina
     * sends the Clients name to the Courtier
     */
	
	public void inscription() {
		
		try {
			outObject.writeObject(nameClient);
			outObject.flush();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
	}
	
	
/**@author Vitalina
     * @param clients price ,amount how much he wants to buy of stocks, name of company
     * @return OrdreAchat if it is legal or non return null
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
     * @param clients price ,amount how much he wants to sale of stocks, name of company
     * @return OrdreVente if it is legal or non return null 
     */
	public Ordre vendre (double prix, int quantite, String entreprise){
		if(portefeuille.size()==0){
			System.out.println("Pas de Vente, car Portefeille est vide");
			return null;
		}
		if ( ! venteLegal(entreprise,quantite) ) {
			System.out.println("Cette vente n'est pas legal !");
			return null;
		}
		
		quantiteEventuelleVendu+=quantite;
		Ordre r =new OrdreVente(entreprise, this.nameClient, prix,quantite,nameCourtier);
		ordres.add(r);
		return r;
		
	}
	
	
	/**@author Vitalina
     *make exchanges between Client, Courtier and ThreadBourse and received notifications
     *if Orders were accepted or non and finally disconnection of this client 
     */
	public void echangeOrdresClientCourtier() {
		Scanner lect = new Scanner(System.in);
		Ordre ordre=null;
		try {
			System.out.println("Echange des ORDRES");
			System.out.print(ANSI_GREEN+"Donnez le nombres d'ordres a creer : "+ANSI_RESET);
			int nbOrdre=lect.nextInt();
			lect.nextLine();
			int cpt=0;//compte le nbordre qu on va envoyer au courtier
			
			//envoyer nbOrdre a traites au courtier
			outObject.writeObject(nbOrdre);
			outObject.flush();
			
			while(nbOrdre!=0){
			
					System.out.print(ANSI_GREEN+"Donnez l'ordre a creer 'v'pour Vente ou 'a'pour Achat : ");
					String r=lect.nextLine();
					System.out.print("Indiquez le nom de l'entreprise : ");
					String nom_entreprise=lect.nextLine();
					System.out.print("Indiquez le prix : ");
					Double prix=lect.nextDouble();
					lect.nextLine();
					System.out.print("Donnez le nombre d'actions que vous souhaitez: "+ANSI_RESET);
					int nbActions=lect.nextInt();
					lect.nextLine();
					
					if(r.equals("a") || r.equals("A")) {
						
						ordre=acheter(prix, nbActions, nom_entreprise);
						Produir(ordre);
						if(ordre!=null){
							System.out.println("OrdreAchat transmis avec succes");
							}
						
						
					}
					if(r.equals("v")){
						ordre=vendre(prix, nbActions, nom_entreprise);
						Produir(ordre);
						if(ordre!=null){
						System.out.println("OrdreVente transmis avec succes");
						}
					}
					if(ordre!=null){
						cpt++;
					}
					
					nbOrdre--;
					if(cpt==2){
						System.out.println("J attends la reponse de la Bourse");
						for(int j=0;j<2;j++){
							int idOrdre = (int) inObject.readObject();
							boolean yesOuNon=(boolean) inObject.readObject();
							getReponseBource(idOrdre,yesOuNon);
							System.out.println("Portefeille de Client : "+portefeuille);
							System.out.println("Solde de Client : "+solde);
						
						}
						cpt=0;
					}
				
				}
				if(cpt!=0){
					System.out.println("Jattends la reponse de la Bourse");
					for(int j=0;j<cpt;j++){
						int idOrdre = (int) inObject.readObject();
						boolean yesOuNon=(boolean) inObject.readObject();
						getReponseBource(idOrdre,yesOuNon);
						System.out.println(ANSI_GREEN+"Voici le Portefeille du Client : "+portefeuille);
						System.out.println("Solde actuel du client Client : "+solde+ANSI_RESET);
					
					}
					cpt=0;
				}
				outObject.writeObject(new String("bye"));
				outObject.flush();
				System.out.println("Client "+nameClient+" a fini de transmettre ses ordres");
				deconnexion();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**@author Vitalina
     * @param name of company and the number of Stocks to buy
     * @return true or false
     * check if the condition is legal to sell OrdreVente
     */
	public boolean venteLegal(String entreprise,int quantite){
		
		if(!portefeuille.containsKey(entreprise))return false;
		return (portefeuille.get(entreprise)-quantiteEventuelleVendu)>=quantite;
	}
	
	/**@author Vitalina
     * @param name of company and the price of all Stocks which Client want to buy
     * @return true or false
     * check if the condition is legal to buy OrdreAchat
     */
	public boolean achatLegal(String entreprise,double prix,int quantite){
		
		double prixReel = prix + tauxDeComission * prix ;
		boolean cond1=solde - depensesEventuelles  > prixReel;
		return cond1 ;
	}
	
	/**@author Vitalina
     * disconnection of Client
     */
	public void deconnexion(){
		
		System.out.println("Client "+nameClient+" se deconnecte");
		prixBoursePourEntreprise.clear();
		try {
			outObject.close();
			inObject.close();
			inS.close();
			outS.close();
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**@author Vitalina
     * @param id of Order
     * @return Order from list of orders
     */
	public Ordre getOrderById(int id) {
		
		for(Ordre t : ordres) {
			if(t.getId()==id)return t;
		}
		return null;
	}
	
	
	/**@author Vitalina
     * @param id of Order, boolean if Order was accepted or no
     * deals answer of Bourse and update portefeille according to OrderVante or OrderAchat
     */
	public void getReponseBource(int idOrdre, boolean yesOuNon) {
		Ordre r=getOrderById(idOrdre);
		
		if(yesOuNon) {
			if(r instanceof OrdreAchat) {
				
				
				majPortefeuilleAchat(r);
				depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantiteClient());
				
			}
			if(r instanceof OrdreVente) {
				majPortefeuilleVente(r);
				quantiteEventuelleVendu-=r.getQuantiteClient();
			}
		}
		if(r instanceof OrdreAchat) {
			System.out.println("OrdreAchat traite : "+r.getEntrepriseName()+", quantite : "+r.getQuantiteClient()+", reponse de la Bourse : "+yesOuNon);
			depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantiteClient());
		}
		if(r instanceof OrdreVente) {
			System.out.println("OrdreVente traite : "+r.getEntrepriseName()+", quantite : "+r.getQuantiteClient()+", reponse de la Bourse : "+yesOuNon);
			quantiteEventuelleVendu-=r.getQuantiteClient();
		}
		ordres.remove(r);
		
		
	}
	
	
	/**@author Vitalina
     *  Once the order is finished, updates the wallet of the current client only 
     *  @param Ordre : Buying order
     */
	public void majPortefeuilleAchat(Ordre r){
			
			double prixR=r.getPrixUnitaire()*r.getQuantiteClient();
			solde-=(prixR+(prixR*tauxDeComission));
			
			if(portefeuille.containsKey(r.getEntrepriseName())){
				int i=portefeuille.get(r.getEntrepriseName());
				portefeuille.replace(r.getEntrepriseName(), r.getQuantiteClient()+i);
			}
			else{
				portefeuille.put(r.getEntrepriseName(), r.getQuantiteClient());
			}
		
	}
		
	
	
	
	/**@author Vitalina
     *  Once the order is finished, updates the wallet of the current client only 
     *  @param Ordre : Selling order
     */
	public void majPortefeuilleVente(Ordre r){
		
		if(!portefeuille.containsKey(r.getEntrepriseName()))return;
		double prixR=r.getPrixUnitaire()*r.getQuantiteClient();
		solde+=(prixR-(prixR*tauxDeComission));
		int i=portefeuille.get(r.getEntrepriseName());
		if(i==r.getQuantiteClient())portefeuille.remove(r.getEntrepriseName());
			
		else{
			portefeuille.replace(r.getEntrepriseName(), i-r.getQuantiteClient());
		}
		
		
	}
	
	
	/**@author Vitalina
     * Shows the market status to the client
     */
	public void readStateStocks(){
		try {			
			System.out.println("Client "+nameClient+" veut savoit l'etat du marche");
			prixBoursePourEntreprise =  (HashMap<String, Double>) inObject.readObject();
			System.out.println("Voila l'etat du marche : ");
			System.out.println(prixBoursePourEntreprise);
			
		}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
		
	}
	
	/**@author Vitalina
     * Sends an order to the Broker
     * @param Ordre
     */
	public void Produir(Ordre r){
		if(r==null){
			try {
				outObject.writeObject("null");
				outObject.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		if(r instanceof OrdreAchat ){ 
			System.out.println(ANSI_GREEN+"Client "+nameClient+" envoie un Ordre d Achat au Courtier : "+r.getEntrepriseName()+", quantite : "+r.getQuantiteClient()+",  prix : "+r.getPrixUnitaire()+ANSI_RESET);
		}
		if(r instanceof OrdreVente){
			System.out.println(ANSI_GREEN+"Client "+nameClient+" envoie un Ordre de Vente au Courtier : "+r.getEntrepriseName()+", quantite : "+r.getQuantiteClient()+",  prix : "+r.getPrixUnitaire()+ANSI_RESET);
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
		Scanner lect = new Scanner(System.in);
		System.out.println(ANSI_GREEN+"Donnez le nom du client :"+ANSI_RESET);
		String nom=lect.nextLine();
		Client client = new Client (nom,100000.0,nport,hote);
		
		
	}
	
	
	
	

}
