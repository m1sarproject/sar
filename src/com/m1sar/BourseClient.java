package com.m1sar;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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
	
	
	
	
	public ThreadCourtier getFreeCourtier() {
	
		int index = -1;
	
		for (ThreadCourtier courtier : listcourtiers) {
			
			if (courtier.estDisp()) return courtier;
			
		}
		
		
		return null;
		
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
				//incr�menter nbCustumers qu'il y'a dans ThreadCourtier au moment de l'affectation du client
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}




	
}
