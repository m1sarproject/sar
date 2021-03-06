package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;




public class AnnuaireClient extends Thread {
	
	/** Port number of AnnuaireClient */
	private int nport;
	/** Listening socket (ServerSocket) */
	private ServerSocket serveurClient=null;
	/** Socket of the current client */
	private Socket clientConnecte = null;
	/** List of all brokers */
	private Vector<ThreadBourse> listcourtiers;

	public AnnuaireClient(int nport,Vector<ThreadBourse> courtiers) {
		super();
		this.nport = nport;
		listcourtiers = courtiers;
		start();
	}
	
	
	
	/**@author Lyes
     * Gets an avialable broker and sets it to the client
	 * @throws CourtierNotFoundException 
     */ 
	public ThreadBourse getFreeCourtier()   {
		int n =100;
		ThreadBourse t=null;
		for (ThreadBourse courtier : listcourtiers) {
			if(n>courtier.getNbCustomer()) {
				if(courtier.estDispo()) {
					t=courtier;
					n=courtier.getNbCustomer();
				}
				
			}
		}
		if(t!=null)t.incNbClient();
		return t;
		
	}
	
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("L'annuaire client  est a l'ecoute sur le port "+nport);
		
		
		while (true) {
			
			clientConnecte = serveurClient.accept();
			System.out.println("Le client s'est connecte et recupere les informations de son courtier");	
			ThreadBourse tcourtier=null;
			tcourtier = getFreeCourtier();
			OutputStream outS=clientConnecte.getOutputStream();
			ObjectOutputStream outObject=new ObjectOutputStream(outS);
			if(tcourtier!=null) {
			    outObject.writeObject(tcourtier.getInetAddress());
			    outObject.flush();
			    outObject.writeInt(tcourtier.getNport());
			    outObject.flush();
			}
			else {
				outObject.writeObject("Aucun Courtier n'est disponible");
				outObject.flush();
			}
			

		  }
		}
		catch( IOException e) {
			e.getMessage();
			
		}
		finally {
			try {
				serveurClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
			
	}

	
}
