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

    private Socket sCourtier; //socket pour communiuqer avec courtier
	private Map<String,Double> prixParEntreprise=new HashMap<String,Double>();
	private int nbCustomer=0;
	private Bourse bourse;  //la bourse qui a cree le Threadcourtier 
	private OutputStream outS;
	private InputStream inS;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nomCourtier;
	private int nport;
	//le temps qu'un courtier attend avant de se deconnecter
	
	
	
	
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
				System.out.println("j'envoi le numero de port au courtier ");
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
    	//recuperer la liste des prix 
    	prixParEntreprise=bourse.getPrixParEntreprise();
   
    	Ordre ordre_client;
    	int nbOrdres=0;
    	int cpt=0;
    	while(true){
    		try {	
					System.out.println("Bourse recoit un message de courtier");
					Object req=inObject.readObject();
					if(req instanceof Integer ) nbOrdres=(int) req;
					if(req instanceof String) {
						String info=(String)req;
						if(info.equals("decreClient")) {
							nbCustomer--;
							outObject.writeInt(nbCustomer);
							outObject.flush();
							System.out.println("j'ai envoy� � courtier nbcustumer");
						}
						if(info.equals("bye")) {
							
							//courtier se deconnecte  on enleve le threadCourtier de la liste
							bourse.removeBroker(this);
							break;//sortir du while
						}
						if(req.equals("null")){
							System.out.println("Je suis null threadBourse");
    					
    					    nbOrdres--;
    					}
					}
					
					
					if(req instanceof Ordre){
						cpt++;
						nbOrdres--;
						ordre_client= (Ordre)req;
						System.out.println(" ordres recu: "+ordre_client.getEntrepriseName());
						SurReceptionDe(ordre_client);
						System.out.println(" nOrdre : "+cpt);
						System.out.println(" nbOrdres : "+nbOrdres);
					}

					if(cpt==2) {

						for (int i = 0; i < 2; i++) {
							System.out.println("je repond au courtier acceptation ");
							bourse.accord(nomCourtier);
							
						}
						cpt=0;
					}
					if(cpt<2 && nbOrdres==0){
						for (int i = 0; i < cpt; i++) {
							System.out.println("je repond au courtier acceptation ");
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
    public void sendPriceCompanies() throws IOException {//quand est ce que s'est fait? au debut de la journee avant qu'un client ne soit deco ;il faut ajouter un 
    	outObject=new ObjectOutputStream(outS);
    	outObject.writeObject(prixParEntreprise);
    	outObject.flush();						//nombre pour representer les jours
    	
    }
    /**
     * @param ordre the order passed by  the  customer 
     * send to the stock market the order  
     */

    public void SurReceptionDe(Ordre ordre) {
    	bourse.getOrdres().add(ordre);
    	System.out.println("je suis dans SRD");
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