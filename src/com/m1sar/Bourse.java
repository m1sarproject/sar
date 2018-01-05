package com.m1sar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public class Bourse {

	
	private Vector<Entreprise> entreprises = new Vector<Entreprise> ();
	private Vector<ThreadCourtier> courtiers = new Vector<ThreadCourtier> ();
	private HashMap<String,Double> prixParEntreprise;
	private int dayid; 
	
	public Bourse() {
		
		
	}
	
    /**@author Lyes
     * At the end of the day, update the price of every business action in each company
     * Follow this rule : new price = old prince + delta
     * where delta = (number of purchase orders -  number of sale orders ) / number of buisness actions
     */
	
	public HashMap<String,Double> updatePrice() {
		
			prixParEntreprise = new HashMap<String,Double> (); //Je recrée à chaque fois cette map, donc inutile  
																					  // d'appeller Replace () pour ma map
			for (Entreprise entreprise : entreprises) {
				
				int delta = ( entreprise.getNbDemandesAchats() - entreprise.getNbDemandeVentes() ) / entreprise.getNbActions(); 
				Double nouveauPrix =  entreprise.getPrixUnitaireAction()+delta;
				
				prixParEntreprise.put(entreprise.getName(),nouveauPrix);
				
			}
		 //Création de Map<String,Double> qui mets à jour les prix par nom d'entreprise, cet objet sera envoyé à tous les courtiers et à tous les clients
	
			
			
			dayid++;
			writeToFile(prixParEntreprise);
			return prixParEntreprise;
			
	}
	
	
	public void writeToFile(HashMap informations) {
	
		try {
	           FileOutputStream fos = new FileOutputStream("jour"+dayid);
	           ObjectOutputStream oos = new ObjectOutputStream(fos);
	           oos.writeObject(informations);
	           oos.close();
	           fos.close();
	           System.out.printf("Les informations de la journée ont bien été sauvegardées");
	     }catch(IOException ioe) {
	           ioe.printStackTrace();
	     }
  }
	
	
	public HashMap<String,Double> readFromFile(String filename) {
		
		HashMap<String,Double> informations = null;
	      try {
	         FileInputStream fis = new FileInputStream(filename);
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         informations = (HashMap<String,Double>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe) {
	         ioe.printStackTrace();
	      }catch(ClassNotFoundException c) {
	         System.out.println("Class not found");
	         c.printStackTrace();
	      }
	      
	      return informations;
		
	}
	
	
	public Entreprise getByName(String name) {
		
		
		for (Entreprise entreprise : entreprises) {
			
			if (entreprise.getName().equals(name)) return entreprise;
		}
		
		throw new NoSuchElementException("L'entreprise que vous cherchez n'existe pas");
	}
	
	
	
	
	public boolean agreeOrNot(Ordre o) {
		
		Entreprise concerned = this.getByName(o.getEntrepriseName());

		if (o instanceof OrdreVente) {
			
			
			concerned.addOrder(o);
			concerned.incDemandesVentes();
		}

		
		if (o instanceof OrdreAchat) {
		
		concerned.incDemandesAchat();
		
		int nbActionsDispo = concerned.getNbActions();
		int nbActionsVoulus = o.getQuantite();
		
		double prixEntreprise = concerned.getPrixUnitaireAction();
		double prixPropose = o.getPrixUnitaire();
		
		if ( nbActionsDispo > nbActionsVoulus && prixPropose >= prixEntreprise ) {
			
			o.setEstFini();
			concerned.DecreaseNbActions(nbActionsVoulus);
			concerned.addOrder(o); //Je stoque l'ordre dans l'entreprise
			return true;		
	
		}
		
		
		for (  Ordre ordre : concerned.getOrdres()) {	//Regarde si un vendeur existe
			
			if (ordre instanceof OrdreVente && ordre.estFini==false) {
				
				if (matching(o,ordre)) return true;
				
			}	
		
		}
				
	 }
		


	return false;
	
	}
		
	
	public boolean matching(Ordre achat,Ordre vente) {
		
		
		return achat.getQuantite() >= vente.getQuantite() && achat.getPrixUnitaire() >= vente.getPrixUnitaire();
				
	}

    /**@author Lyes
     * Appends the specified Broker to the end of the list (If he is not alredy in the list).
     * @param c : the element to be appended to this list
     * IllegalArgumentException if the Broker does exist
     * @return <tt>true</tt> 
     */
	boolean addBroker(ThreadCourtier c) {
		if ( courtiers.contains(c) ) throw new IllegalArgumentException("The Broker is already in the list.");

		courtiers.add(c);
		return true;

	}
	
	
    /**@author Lyes
     * Appends the collection of Brokers to the end of the list, using addBroker(C).
     * @param L : the collection of Brokers to be appended to this list
     * @return <tt>true</tt> 
     */
	boolean addAllBrokers(List<ThreadCourtier> L) {
		
		for( ThreadCourtier c : L ) {
			
			addBroker(c);
		}
		
		return true;

		
	}
	
	
    /**@author Lyes
     * Removes the specified Broker from the list (If he already is in the list).
     * @param c : the element to be removed
     * @exception : IllegalArgumentException if the Broker does not exist
     * @return <tt>true</tt> 
     */
	boolean removeBroker(ThreadCourtier c) {
		
		if ( ! courtiers.contains(c) ) throw new IllegalArgumentException("The Broker does not exist.");

		courtiers.remove(c);
		
		return true;
	}
	
	
    /**@author Lyes
     * Removes all Brokers from the list
     * @return <tt>true</tt> 
     */
	boolean removeAllBrokers() {
		
		courtiers.clear();
		return true;
		
	}
	
	

	
    /**@author Lyes
     * Add the specified Company  to the list (If it already is in the list).
     * @param e : the company to be added
     * @exception IllegalArgumentException if the Company does exist
     * @return <tt>true</tt> 
     */
	boolean addCompany(Entreprise e) {
		
		if ( entreprises.contains(e) ) throw new IllegalArgumentException("The Broker is already in the list.");

		entreprises.add(e);
		return true;
	}
	
	
    /**@author Lyes
     * Appends the collection of Companies to the end of the list, using addCompany(E).
     * @param L : the collection of Companies to be appended to this list
     * @return <tt>true</tt> 
     */
	boolean addAllCompanies(List<Entreprise> L) {
		
		for( Entreprise e : L ) {
			
			addCompany(e);
		}
		
		return true;
		
	}
	

	
    /**@author Lyes
     * Removes the specified Company from the list (If it already is in the list).
     * @param e : the company to be removed
     * @exception IllegalArgumentException if the Company does exist
     * @return <tt>true</tt> 
     */
	boolean removeCompany(Entreprise e) {
		
		if ( ! entreprises.contains(e) ) throw new IllegalArgumentException("The company does not exist.");
	
		entreprises.remove(e);
		
		return true;
	}
	
	
    /**@author Lyes
     * Removes all Companies from the list
     * @return <tt>true</tt> 
     */
	boolean removeAllCompanies() {
		
		entreprises.clear();
		return true;
		
	}	
	
	
	public void initCompanies() {
		
		// Creation des entreprises ...
		List<Entreprise> companies = new ArrayList<Entreprise>();
		
		Entreprise e1 = new Entreprise("Kerima Moda", 10,2);
		Entreprise e2 = new Entreprise("Microsoft", 20,10);
		Entreprise e3 = new Entreprise("Apple", 20,15);
		Entreprise e4 = new Entreprise("Ubisoft", 15, 6);
		
		companies.add(e1);
		companies.add(e2);
		companies.add(e3);
		companies.add(e4);
		
		this.addAllCompanies(companies);
	}
	
	public static void main(String[] args) throws IOException{
		
		
		Bourse bourse = new Bourse();
		bourse.initCompanies();
		
		ServerSocket serveurCourtier=null;
		int nport = Integer.parseInt(args[0]);
		
		try { 
			serveurCourtier= new ServerSocket(nport); //Socket d'écoute
		}
		
		catch (Exception e) {System.err.println("La création du serveur d'écoute a échoué");}
		
		System.out.println("Le serveur courtier est a l'ecoute sur le port "+nport);
		BourseClient bourseclient = new BourseClient (nport+1,bourse.courtiers); 
		 
		while(true) {		

		 		try{
		 		
		 		System.out.println("La bourse attend un courtier");
				Socket courtierConnecte = serveurCourtier.accept();			//Le courtier se connecte à  la socket de communication
				
				System.out.println("Connexion Courtier acceptée par Bourse");	
				
				BufferedReader in =new BufferedReader(new InputStreamReader(courtierConnecte.getInputStream()));
				String nomcourtier = in.readLine();
				
				System.out.println("Le nom du courtier est : "+nomcourtier);
				
				ThreadCourtier tc=new ThreadCourtier(bourse,nomcourtier); //Passer la map des prix en paramètre au courtier
				bourse.addBroker(tc);	

				}

				catch (Exception e) {
					System.out.println("Socket ferme dans Bourse de serveurCourtier");
					serveurCourtier.close();
					}		 	
					
		 }	

	}
	
}
