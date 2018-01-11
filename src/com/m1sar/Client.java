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
	private boolean connecte=false;
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
		if(connecte==true) {
			readStateStocks();
			echangeOrdresClientCourtier();
			
		} 
		else {
			System.out.println("Navre vous ne pouvez pas vous connect� aucun courtier n'est disponible");
		}
	}
	 /**@author Vitalina
     * 
     * make connection with ThreadBource which find for this client 
     * Courtier available and client obtain IP and host of this Courtier 
     * if any Courtier is available client is disconnected 
     * 
     */
	public void connexion() {
		
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
			Object o=inObject.readObject();
			if(!(o instanceof String)) {
				connecte=true;
				InetAddress host=(InetAddress)o;
				int portCourtier=inObject.readInt();
				sc.close();
				Socket connexionCourtier=new Socket(host, portCourtier);
				System.out.println("conenxion au courtier reussi");
				outObject=new ObjectOutputStream(connexionCourtier.getOutputStream());
				inObject = new ObjectInputStream(connexionCourtier.getInputStream());
				inscription(); 
				nameCourtier= (String) inObject.readObject();
				System.out.println("message du courtier "+nameCourtier+" : "+(String)inObject.readObject());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
	
	
/**@author Vitalina
     * @param clients price ,amount how much he wants to buy of stocks, name of company
     * @return OrdreAchat if it is legal or non return null
     *
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
     * 
     */
	public Ordre vendre (double prix, int quantite, String entreprise){
		if(portefeuille.size()==0){
			System.out.println("pas de vente P vide");
			return null;
		}
		if ( ! venteLegal(entreprise,quantite) ) {
			System.out.println("ce Vente n est pas legal");
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
     * 
     */
	public void echangeOrdresClientCourtier() {
		Scanner lect = new Scanner(System.in);
		Ordre ordre=null;
		try {
			System.out.println("Hello mon Courtier je vais t envoyer des ORDRES");
			System.out.print("Donnez le nbOrdres a creer : ");
			int nbOrdre=lect.nextInt();
			lect.nextLine();
			int cpt=0;//compte le nbordre qu on va envoyer au courtier
			//envoyer nbOrdre a traites au courtier
			outObject.writeObject(nbOrdre);
			outObject.flush();
			while(nbOrdre!=0){
			
					System.out.print("Donnez l Ordre a cree 'v'-Vente ou 'a'-Achat : ");
					String r=lect.nextLine();
					System.out.print("Donnez le prix : ");
					Double prix=lect.nextDouble();
					lect.nextLine();
					System.out.print("Donnez le nb actions a acheter ou vendre : ");
					int nbActions=lect.nextInt();
					lect.nextLine();
					System.out.print("Donnez le nom de l entreprise : ");
					String nom_entreprise=lect.nextLine();
					if(r.equals("a")){
						
						ordre=acheter(prix, nbActions, nom_entreprise);
						Produir(ordre);
						if(ordre!=null){
							System.out.println("OrdreAchat bien envoyer");
							}
						
						
					}
					if(r.equals("v")){
						ordre=vendre(prix, nbActions, nom_entreprise);
						Produir(ordre);
						if(ordre!=null){
						System.out.println("OrdreVente bien envoyer");
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
					System.out.println("J attends la reponse de la Bourse apres sortir de while");
					for(int j=0;j<cpt;j++){
						int idOrdre = (int) inObject.readObject();
						boolean yesOuNon=(boolean) inObject.readObject();
						getReponseBource(idOrdre,yesOuNon);
						System.out.println("Portefeille de Client : "+portefeuille);
						System.out.println("Solde de Client : "+solde);
					
					}
					cpt=0;
				}
				outObject.writeObject(new String("bye"));
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
		//System.out.println("PrixdeApple = "+prixBoursePourEntreprise.get(entreprise));
		boolean cond1=solde - depensesEventuelles  > prixReel;
		//System.out.println("solde-depenses = "+(solde - depensesEventuelles));
		//boolean cond2=prixReel>(quantite*prixBoursePourEntreprise.get(entreprise));
		//System.out.println("prixrplus garend de E = "+(quantite*prixBoursePourEntreprise.get(entreprise)));
		return cond1 ;//&&cond2;
	}
	
	/**@author Vitalina
     * 
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**@author Vitalina
     * @param id of Order
     * @return Order from list of orders
     */
	
	
	public Ordre getOrderById(int id) {
		Ordre res=null;
		for(Ordre t : ordres) {
			if(t.getId()==id)res=t;
		}
		return res;
	}
	/**@author Vitalina
     * @param id of Order, boolean if Order was accepted or no
     * deals answer of Bourse and update portefeille according to OrderVante or OrderAchat
     */
	public void getReponseBource(int idOrdre, boolean yesOuNon) {
		Ordre r=getOrderById(idOrdre);
		System.out.println("Ordre "+r.getPrixUnitaire());
		
		if(yesOuNon) {
			if(r instanceof OrdreAchat) {
				System.out.println("dans le if un ordreAchaat");
				majPortefeuilleAchat(r);
				depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantiteClient());
				
			}
			if(r instanceof OrdreVente) {
				majPortefeuilleVente(r);
				quantiteEventuelleVendu-=r.getQuantiteClient();
			}
		}
		if(r instanceof OrdreAchat) {
			depensesEventuelles-=(r.getPrixUnitaire()*r.getQuantiteClient());
		}
		if(r instanceof OrdreVente) {
			quantiteEventuelleVendu-=r.getQuantiteClient();
		}
		ordres.remove(r);
		
		
	}
	
	
	
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
     * 
     * 
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
		if(r==null){
			try {
				outObject.writeObject("null");
				outObject.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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
		Scanner lect = new Scanner(System.in);
		System.out.println("Donnez le nom du client :");
		String nom=lect.nextLine();
		Client client = new Client (nom,210.0,nport,hote);
		
		
	}
	
	
	
	

}
