package com.m1sar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class BourseClient extends Thread {
	
	private int nport;
	private ServerSocket serveurClient=null;
	private Socket clientConnecte = null;
	private List listcourtiers;

	public BourseClient(int nport,List courtiers) {
		super();
		this.nport = nport;
		listcourtiers = courtiers;
		start();
	}
	
	
	
	
	public int getFreeCourtier() {
		
	
		return 0;
		
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
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}




	
}
