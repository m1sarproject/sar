package com.m1sar;

import java.io.IOException;
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
	
		int index = -1;
	
		for (ThreadCourtier courtier : listcourtiers) {
			
			if (courtier.estDispo()) return courtier;
			
		}
		
		
		throw new CourtierNotFoundException ("Aucun courtier n'est disponible");
		
	}
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("Le serveur client est à l'écoute sur le port "+nport);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		while (true) {
			
			try {
				clientConnecte = serveurClient.accept();
				System.out.println("Connexion client acceptée");		
				getFreeCourtier().addClient(clientConnecte);
				
			} catch (IOException e) {
				e.printStackTrace();
				} catch (CourtierNotFoundException e) {				
					try {
						clientConnecte.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						}					
				}
			
		}
		
		
	}




	
}
