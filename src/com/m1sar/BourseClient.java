package com.m1sar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class BourseClient extends Thread {
	
	private int nport;
	private ServerSocket serveurClient=null;
	Socket clientConnecte = null;
	List listclients;

	public BourseClient(int nport,List clients) {
		super();
		this.nport = nport;
		List listclients = clients;
		start();
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
				listclients.add(clientConnecte);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}




	
}
