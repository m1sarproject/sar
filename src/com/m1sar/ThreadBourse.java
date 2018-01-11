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

	/** Socket to communicate with the broker*/
    private Socket sCourtier; //socket pour communiuqer avec courtier
	/** State of the market */
	private Map<String,Double> prixParEntreprise=new HashMap<String,Double>();
	private int nbCustomer=0;
	/** The market */
	private Bourse bourse;  //la bourse qui a cree le Threadcourtier 
	private OutputStream outS;
	private InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	/** Name of the Broker which was created */
	private String nomCourtier;
	private int nport;
	
	
	
	
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
				System.out.println("J'envoi le numero de port au courtier ");
				outObject.writeInt(nport);
				outObject.flush();
				outObject.writeObject(bourse.getPrixParEntreprise());
				outObject.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
	  }
    public String getNomCourtier() {
		return nomCourtier;
	}


	public void setNomCourtier(String nomCourtier) {
		this.nomCourtier = nomCourtier;
	}


	
	@Override
    public void run() {
    	
    	connexionCourtier();
    	prixParEntreprise=bourse.getPrixParEntreprise();
   
    	Ordre ordre_client;
    	int nbOrdres=0;
    	int cpt=0;
    	while(true){
    		try {	
					Object req=inObject.readObject();
					if(req instanceof Integer ) nbOrdres=(int) req;
					if(req instanceof String) {
						String info=(String)req;
						if(info.equals("decreClient")) {
							nbCustomer--;
							outObject.writeInt(nbCustomer);
							outObject.flush();
						}
						if(info.equals("bye")) {
							
							bourse.removeBroker(this);
							break;
						}
						if(req.equals("null")){
	   					
    					    nbOrdres--;
    					}
					}
					
					
					if(req instanceof Ordre){
						cpt++;
						nbOrdres--;
						ordre_client= (Ordre)req;
						System.out.println(" ordres reçu: "+ordre_client.getEntrepriseName());
						SurReceptionDe(ordre_client);

					}

					if(cpt==2) {


						for (int i = 0; i < 2; i++) {
							

							bourse.accord(nomCourtier);
							
						}
						cpt=0;
					}
					if(cpt<2 && nbOrdres==0){
						for (int i = 0; i < cpt; i++) {
							
						    bourse.accord(nomCourtier);
							
						}
						cpt=0;
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
    public void sendPriceCompanies() throws IOException { 
    	outObject=new ObjectOutputStream(outS);
    	outObject.writeObject(prixParEntreprise);
    	outObject.flush();						
    	
    }
    /**
     * @param ordre the order passed by  the  customer 
     * send the order to the stock market  
     */

    public void SurReceptionDe(Ordre ordre) {
    	bourse.getOrdres().add(ordre);
    	Entreprise concerned =bourse.getByName(ordre.getEntrepriseName());
    	if (ordre instanceof OrdreVente) {
			
			concerned.addOrder(ordre);
			concerned.incDemandesVentes();			
		}
    	else {
    		concerned.addOrder(ordre);
    		concerned.incDemandesAchat();
    	}

    }
    
    /**
     * Answers to the broker if the order is ok or not  
     * @param id : the id of the broker
     * @rep : the answere : true/false
     */
    public void envoyerRep(int id,boolean rep) throws IOException {
    	outObject.writeObject(id);
		outObject.writeObject(rep);
    }
    
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
		
}