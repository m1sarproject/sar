package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public class BourseClient extends Thread {
	
	private int nport;
	private ServerSocket serveurClient=null;
	private Socket clientConnecte = null;
	private Vector<ThreadCourtier> listcourtiers;

	public BourseClient(int nport,Vector<ThreadCourtier> courtiers) {
		super();
		this.nport = nport;
		listcourtiers = courtiers;
		start();
	}
	
	
	
	
	public ThreadCourtier getFreeCourtier() throws CourtierNotFoundException {
	
		for (ThreadCourtier courtier : listcourtiers) {
			if (courtier.estDispo()) return courtier;	
		}
		
		throw new CourtierNotFoundException ("Aucun courtier n'est disponible");	
	}
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("Le serveur client est à l'ecoute sur le port "+nport);
		
		
		while (true) {
			
			try {
				clientConnecte = serveurClient.accept();
				System.out.println("Connexion client accepter par BourseClient");	
				/*BufferedReader in =new BufferedReader(new InputStreamReader(clientConnecte.getInputStream()));
				PrintWriter out=new PrintWriter(clientConnecte.getOutputStream(),true);
				out.println("vita mechancete");*/
			    ThreadCourtier courtier=getFreeCourtier();
			    courtier.addClient(clientConnecte);
				//c.start();
				System.out.println("Le courtier a reçu son client par BourseClient");
				
			} 
			
			catch (CourtierNotFoundException e ) {
				System.out.println(e.getMessage());
				clientConnecte.close();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			
			/*catch (CourtierNotFoundException e) {				
					try {
						clientConnecte.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						}					
				}*/
			
	
	

	}

	
}
