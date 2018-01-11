package com.m1sar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;



public class Bourse {

	/** list of all companies available in the market  */
	private Vector<Entreprise> entreprises = new Vector<Entreprise> ();
	/** list of all brokers working in the market  */
	private Vector<ThreadBourse> courtiers = new Vector<ThreadBourse> ();
	/** A map that shows the price for each company available in the market  */
	private HashMap<String,Double> prixParEntreprise=new HashMap<String,Double>() ;
	/** list of prices for each company, for each day, this is used to draw the evolution of prices  */
	private static ArrayList<HashMap<String,Double>> listeGraphe=new ArrayList<HashMap<String,Double>>();
	/** list of orders that the market have to check  */
	private Vector<Ordre> ordres=new Vector<Ordre>();
	/** number of the current day  */
	private int dayid=0; 
	
	public Bourse() {
		
		
	}
	
    /**@author Lyes
     * At the end of the day, update the price of every business action in each company
     * Follow this rule : new price = old prince + delta
     * where delta = (number of purchase orders -  number of sale orders ) / number of buisness actions
     */
	
	public HashMap<String,Double> updatePrice() {
		
			prixParEntreprise = new HashMap<String,Double> (); 
																					 
			for (Entreprise entreprise : entreprises) {
				
				double delta = (( entreprise.getNbDemandesAchats() - entreprise.getNbDemandeVentes() )*1.0 / entreprise.getNbActions()); 
				
				
				Double nouveauPrix =  (entreprise.getPrixUnitaireAction()*1.0)*(1.0+delta);
				
				prixParEntreprise.put(entreprise.getName(),nouveauPrix);
				
			}	
			
			listeGraphe.add(prixParEntreprise);
			dayid++;
			writeToFile(prixParEntreprise);
			return prixParEntreprise;
			
	}
	
	
    /**@author Lyes
     * At the end of the day, saves the informations serialized to a file
     */
	public void writeToFile(HashMap informations) {
	
		try {
	           FileOutputStream fos = new FileOutputStream("jour"+dayid);
	           ObjectOutputStream oos = new ObjectOutputStream(fos);
	           oos.writeObject(informations);
	           oos.close();
	           fos.close();
	           System.out.printf("Les informations de la journnee ont bien ete sauvegardees");
	     }catch(IOException ioe) {
	           ioe.printStackTrace();
	     }
  }
	
	
    /**@author Lyes
     * Reads from file the history
     */
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
	
	/**@author Lyes
     * Gets and returns the Company matching with the name passed as argument.
     * @param name : the name of the company
     * @return <tt>Entreprise</tt> 
     * @throws NoSuchElementException if the name does not match with all companies
     */
	
	public Entreprise getByName(String name) {
			
		for (Entreprise entreprise : entreprises) {
			
			if (entreprise.getName().equals(name)) return entreprise;
		}
		
		throw new NoSuchElementException("L'entreprise que vous cherchez n'existe pas");
	}

	/**
	 * 
	 * @return le premier ordre appertenant au client que traite le courtier nomCourtier
	 */
public Ordre consommer(String nomCourtier) {
	Ordre ordre=null;

	for (Ordre o:ordres) {
		if(o.getNomCourtier().equals(nomCourtier)) {
			ordre=o;
			break;
		}
	}
	if(ordre!=null) {
		ordres.remove(ordre);
	}
	return ordre;
}

	

	
	
	/**@author Lyes
     * review the orderes received from the broker nomCourtier and send the answer for this broker if it's a buy order from a company
     * but if this order is matched with another (buy and sell orders) we return the answer to the two brokers
     * @param nomCourtier : the name of broker
     */
	
	public void accord(String nomCourtier) throws IOException { 
		

		Ordre o=consommer(nomCourtier);
		System.out.println(o);
		if(o!=null) {
			Entreprise concerned = this.getByName(o.getEntrepriseName());
			if (o instanceof OrdreAchat) {
				System.out.println("Bource traite OrdreAchat de "+o.getClientName()+" : "+o.getEntrepriseName()+", quantite : "+o.getQuantiteClient());		
				
				int nbActionsDispo = concerned.getNbActions();
				int nbActionsVoulus = o.getQuantiteClient();
			

				double prixEntreprise = concerned.getPrixUnitaireAction();
				double prixPropose = o.getPrixUnitaire();
				if ( nbActionsDispo > nbActionsVoulus && prixPropose >= prixEntreprise ) {
					o.setEstFini();
					concerned.DecreaseNbActions(nbActionsVoulus);	
					o.setEstAccepte(true);
					ThreadBourse th=getThreadByName(nomCourtier);
					if(th!=null) {
						th.envoyerRep(o.getId(), o.estAccepte);
					//avertir le courtier dont le nom est nomCourtier
					}
					
							
				}
				else {
					System.out.println("Bource traite OrdreVente de "+o.getClientName()+" : "+o.getEntrepriseName()+", quantite : "+o.getQuantiteClient());		
					for ( Ordre ordre : concerned.getOrdres()) {	//Regarde si un vendeur existe
						
						if (ordre instanceof OrdreVente && ordre.estAccepte==false && !(ordre.getNomCourtier().equals(nomCourtier))) {
							
							if (matching(o,ordre)) {
								//avertir les deux courtiers
								o.setEstAccepte(true);
								ThreadBourse th1=getThreadByName(nomCourtier);
								ThreadBourse th2=getThreadByName(ordre.getNomCourtier());
								if(th1!=null && th2!=null) {
									th1.envoyerRep(o.getId(), o.estAccepte);
									th2.envoyerRep(ordre.getId(), true);
									break;
								}
							}
							
							
						}	
					
					}
				}
			if(o.estAccepte==false) {
				ThreadBourse th=getThreadByName(nomCourtier);
				if(th!=null) {
					th.envoyerRep(o.getId(), o.estAccepte);
				}
				
			}
					
		 }
		else {
			for (  Ordre ordre : concerned.getOrdres()) {	//Regarde si un acheteur existe
				
				if (ordre instanceof OrdreAchat && ordre.estAccepte==false && !(ordre.getNomCourtier().equals(nomCourtier))) {
					
					if (matching(o,ordre)) {
						o.setEstAccepte(true);
						ThreadBourse th1=getThreadByName(nomCourtier);
						ThreadBourse th2=getThreadByName(ordre.getNomCourtier());
						if(th1!=null && th2!=null) {
							th1.envoyerRep(o.getId(), o.estAccepte);
							th2.envoyerRep(ordre.getId(), true);
							break;
						}
					}
					
					
				}	
			
			}
			if(o.estAccepte==false) {
				ThreadBourse th=getThreadByName(nomCourtier);
				if(th!=null) {
					th.envoyerRep(o.getId(), o.estAccepte);
				//avertir le courtier dont le nom est nomCourtier
				}
				
			}	
			}
		}
		else {

			System.out.println("Pas d'ordre pour ce Courtier");

		}
	
	}
		
    /**@author Lyes
     * Checks if the buying order matchs with the selling order, returns true if so.
     * @param achat : the buying order
     * @param vente : the selling order
     * @return <tt>boolean</tt> 
     */
	public boolean matching(Ordre achat,Ordre vente) {
		
		return (achat.getQuantiteClient() == vente.getQuantiteClient() && achat.getPrixUnitaire() == vente.getPrixUnitaire() && vente.getEntrepriseName().equals(achat.getEntrepriseName()));
				
	}

    /**@author Lyes
     * Appends the specified Broker to the end of the list (If he is not alredy in the list).
     * @param c : the element to be appended to this list
     * IllegalArgumentException if the Broker does exist
     * @return <tt>true</tt> 
     */
	boolean addBroker(ThreadBourse c) {
		if ( courtiers.contains(c) ) throw new IllegalArgumentException("The Broker is already in the list.");

		courtiers.add(c);
		return true;

	}
	
	
    /**@author Lyes
     * Appends the collection of Brokers to the end of the list, using addBroker(C).
     * @param L : the collection of Brokers to be appended to this list
     * @return <tt>true</tt> 
     */
	boolean addAllBrokers(List<ThreadBourse> L) {
		
		for( ThreadBourse c : L ) {
			
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
	boolean removeBroker(ThreadBourse c) {
		
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
	
    /**@author Lyes
     * Initializes with a list of companies, and the prices for each company
     * @return <tt>void</tt> 
     */
	public void initCompanies() {
		
		List<Entreprise> companies = new ArrayList<Entreprise>();
		
		Entreprise e1 = new Entreprise("Kerima Moda", 10,2);
		Entreprise e2 = new Entreprise("Microsoft", 20,10);
		Entreprise e3 = new Entreprise("Apple", 20,15);
		Entreprise e4 = new Entreprise("Ubisoft", 15, 6);
		
		companies.add(e1);
		companies.add(e2);
		companies.add(e3);
		companies.add(e4);
		
		HashMap<String,Double> m1 = new HashMap<String,Double>();
		HashMap<String,Double> m2 = new HashMap<String,Double>();
		HashMap<String,Double> m3 = new HashMap<String,Double>();
		HashMap<String,Double> m4 = new HashMap<String,Double>();



		m1.put("Apple",250d);
		m1.put("Microsoft",230d);
		m1.put("Mercedes",164d);
		m1.put("SpaceX",500d);


		m2.put("Apple",280d);
		m2.put("Microsoft",250d);
		m2.put("Mercedes",264d);
		m2.put("SpaceX",400d);



		m3.put("Apple",350d);
		m3.put("Microsoft",130d);
		m3.put("Mercedes",364d);
		m3.put("SpaceX",700d);


		m4.put("Apple",750d);
		m4.put("Microsoft",530d);
		m4.put("Mercedes",464d);
		m4.put("SpaceX",600d);


		listeGraphe.add(m1);
		listeGraphe.add(m2);
		listeGraphe.add(m3);
		listeGraphe.add(m4);
		
		this.addAllCompanies(companies);
		initPrixParEntreprise();
		
	}

	
    /**@author Lyes
     * Initializes the prices for each company
     * @return <tt>void</tt> 
     */
	public void initPrixParEntreprise() {
		for(Entreprise e:entreprises) {
			prixParEntreprise.put(e.getName(),e.getPrixUnitaireAction());
		}
		listeGraphe.add(prixParEntreprise);
	}
	
	
    /**@author Lyes
     * Returns the hashMap that shows the price of an action for each company 
     * @return <tt>void</tt> 
     */
	public HashMap<String, Double> getPrixParEntreprise() {
		return prixParEntreprise;
	}
	
    /**@author Lyes
     * Returns the vector of Orders
     * @return <tt>void</tt> 
     */
	public Vector<Ordre> getOrdres() {
		return ordres;
	}

    /**@author Lyes
     * Sets the orders for the market
     * @param the vector of orders to be set
     * @return <tt>void</tt> 
     */
	public void setOrdres(Vector<Ordre> ordres) {
		this.ordres = ordres;
	}
	
    /**@author Lyes
     * Returns the Broker from its name
     * @param the Name (id) of the Broker to be returned
     * @return <tt>ThreadBourse</tt> 
     */
	public ThreadBourse getThreadByName(String nomTH) {
		for(ThreadBourse t:courtiers) {
			if(t.getNomCourtier().equals(nomTH)) {
				return t;
			}
		}
		return null;
	}
	

	
	
	public int getDayid() {
		return dayid;
	}

	public void setDayid(int dayid) {
		this.dayid = dayid;
	}

	public Vector<ThreadBourse> getCourtiers() {
		return courtiers;
	}

	public void setCourtiers(Vector<ThreadBourse> courtiers) {
		this.courtiers = courtiers;
	}




	public static class Affichage extends Application {
		
		
		private ArrayList <HashMap<String,Double>> prixParEntreprise  = new  ArrayList <HashMap<String,Double>> ();
		private XYChart.Series[] montab = new XYChart.Series[20]; //On se limite à 20 entreprises maximum

	  public Affichage() {			
			
			for (int i = 0;i<listeGraphe.size();i++) {
			
			prixParEntreprise.add(listeGraphe.get(i));

			}
			
		}
	  


		public void start(Stage stage) {
	  		
	      stage.setTitle("Evolution des prix");
	      stage.setOnCloseRequest(e -> System.exit(0));
	      final CategoryAxis xAxis = new CategoryAxis();
	      final NumberAxis yAxis = new NumberAxis();
	       xAxis.setLabel("Jours");
	       yAxis.setLabel("Prix");
	       xAxis.autosize();
	       yAxis.autosize();
	      final LineChart<String,Number> lineChart =  new LineChart<String,Number>(xAxis,yAxis);
	      lineChart.setTitle("Evolution des prix");
	        
	      Set<String> nomEntreprises = prixParEntreprise.get(0).keySet();

	      XYChart.Series series = null;
	      int index=0;
			
	      for (String nom : nomEntreprises) {
				
				 series = new XYChart.Series();
				 series.setName(nom);
				 
				 
		       for (int i=0; i < prixParEntreprise.size(); i++)  {
			 		
		    	  if (prixParEntreprise.get(i).get(nom) != null)
		      		series.getData().add( new XYChart.Data(i+"-"+nom, prixParEntreprise.get(i).get(nom)));
		      		
		      	}
		        
		       montab[index++]=series;
		        
			}
	      

			for(int i=0;i<index;i++)
			  lineChart.getData().add(montab[i]);

	      Scene scene  = new Scene(lineChart,800,600);              
	      stage.setScene(scene);
	      stage.show();
	  }



		  public static void main(String[] args) {
		      launch(args);
		  }
}
	
	
	
	public void afficheGraphe (String [] args) {
		
        Affichage.main(args);
		
	}
	public static void main(String[] args) throws IOException{

		int nport=0;
		
		try {
			
			nport=Integer.parseInt(args[0]);

			
		}
		
		catch (ArrayIndexOutOfBoundsException e) {
			

			System.out.println("Veuillez entre un numero de port valable");
			Scanner in = new Scanner(System.in);
			nport = Integer.parseInt(in.nextLine());
		}
		
		Bourse bourse = new Bourse();
		bourse.initCompanies();
		//bourse.afficheGraphe(args);
		ServerSocket serveurCourtier=null;
		
		try { 
			
			serveurCourtier= new ServerSocket(nport); 
		}
		
		catch (Exception e) {System.err.println("La creation du serveur d'ecoute a echoue");}
		
		
		System.out.println("Bourse est ouverte ");
		System.out.println("Le jour numero : "+bourse.dayid);
		System.out.println("Le serveur courtier est a l'ecoute sur le port "+nport);
		AnnuaireClient bourseclient = new AnnuaireClient (++nport,bourse.courtiers); 
		while(true) {		
			

		 		try{
		 		
		 
		 		if ( bourse.courtiers.isEmpty() && bourse.dayid>0 ) {
		 				
			 			bourse.updatePrice();
			 			System.out.println("Le jour numero : "+bourse.dayid);
			 		}
		 		System.out.println("La bourse attend un courtier");
				Socket courtierConnecte = serveurCourtier.accept();			
				
				System.out.println("Connexion Courtier acceptee par Bourse");	
				
				BufferedReader in =new BufferedReader(new InputStreamReader(courtierConnecte.getInputStream()));
				String nomcourtier = in.readLine();
				
				System.out.println("Le nom du courtier est : "+nomcourtier);
				
				ThreadBourse tc=new ThreadBourse(courtierConnecte,++nport, bourse, nomcourtier);
				bourse.addBroker(tc);		

				}

				catch (Exception e) {
					System.out.println("Socket ferme dans Bourse de serveurCourtier");
					serveurCourtier.close();
				}
		 		
		 		
		 	
		 		
		 		
		 	 }
		 }
	}