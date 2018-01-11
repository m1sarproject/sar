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

import com.exceptions.CourtierNotFoundException;



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
	public ThreadBourse getFreeCourtier() throws CourtierNotFoundException {
		
		for (ThreadBourse courtier : listcourtiers) {
			if (courtier.estDispo())  
				{
				courtier.incNbClient();
				return courtier;	
				}
		}
		
		throw new CourtierNotFoundException("Pas de courtier disponible");
	}
	
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("Le serveur client est a� l'ecoute sur le port "+nport);
		
		
		while (true) {
			
			clientConnecte = serveurClient.accept();
			System.out.println("Connexion client accepter par BourseClient");	
			ThreadBourse tcourtier=null;
			try {
				tcourtier = getFreeCourtier();
			} catch (CourtierNotFoundException e) {
				System.out.println("Pas de courtier disponible");
				e.printStackTrace();
			}
			OutputStream outS=clientConnecte.getOutputStream();
			ObjectOutputStream outObject=new ObjectOutputStream(outS);
			if(tcourtier!=null) {
			    outObject.writeObject(tcourtier.getInetAddress());
			    outObject.flush();
			    outObject.writeInt(tcourtier.getNport());
			    outObject.flush();
			    System.out.println("envoie des parametres au client reussi");
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
