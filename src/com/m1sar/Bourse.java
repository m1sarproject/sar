package com.m1sar;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Bourse {

	
	private Vector<Entreprise> entreprises = new Vector<Entreprise> ();
	private Vector<ThreadCourtier> courtiers = new Vector<ThreadCourtier> ();
	
	
	public Bourse() {
		
		
	}
	
    /**@author Lyes
     * At the end of the day, update the price of every business action in each company
     * Follow this rule : new price = old prince + delta
     * where delta = (number of purchase orders -  number of sale orders ) / number of buisness actions
     */
	
	void updatePrice() {
		
		 //Création de Map<String,Double> qui mets à jour les prix par nom d'entreprise, cet objet sera envoyé à tous les courtiers et à tous les clients
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
	public static void main(String[] args){
		
		// Creation des entreprises ...
		List<Entreprise> compagnies = new ArrayList<Entreprise>();
		
		Entreprise e1 = new Entreprise("Kerima Moda", 10,2);
		Entreprise e2 = new Entreprise("Microsoft", 20,10);
		Entreprise e3 = new Entreprise("Apple", 20,15);
		Entreprise e4 = new Entreprise("Ubisoft", 15, 6);
		
		compagnies.add(e1);
		compagnies.add(e2);
		compagnies.add(e3);
		compagnies.add(e4);
		
		//Creation de la bourse ...
		
		Bourse bourse = new Bourse();
		bourse.addAllCompanies(compagnies);
		
		ServerSocket serveurCourtier=null;
		int nport = Integer.parseInt(args[0]);
		
		try { 
			serveurCourtier= new ServerSocket(nport); //Socket d'écoute
		}
		
		catch (Exception e) {}

		System.out.println("Le serveur courtier est à l'écoute sur le port "+nport);
		BourseClient bourseclient = new BourseClient (nport+1,bourse.courtiers); 
		 while(true) {		

		 		try{
				Socket courtierConnecte = serveurCourtier.accept();	//Le courtier se connecte à  la socket de communication
				System.out.println("Connexion acceptée");		
				ThreadCourtier tc=new ThreadCourtier(bourse);
				bourse.addBroker(tc); //à compléter avec une méthode obtenant un client à affecter

				

				}

				catch (Exception e) {}
			}		
		 }	

		
	
	
	
}
