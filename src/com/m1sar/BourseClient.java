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
			if (courtier.estDispo())  
				{
				courtier.incNbClient();//incrémenter nombre de client
				return courtier;	
				}
		}
		
		throw new CourtierNotFoundException ("Aucun courtier n'est disponible");	
	}
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("Le serveur client est Ã  l'ecoute sur le port "+nport);
		
		
		while (true) {
			
			try {
				clientConnecte = serveurClient.accept();
				System.out.println("Connexion client accepter par BourseClient");	
			    ThreadCourtier courtier=getFreeCourtier();
			    OutputStream outS=clientConnecte.getOutputStream();
			    ObjectOutputStream outObject=new ObjectOutputStream(outS);
			  //envoyer le numero de port et  @ip inetAddress du courtier au client
			    outObject.writeObject(courtier.getInetAddress());
			    outObject.flush();
			    outObject.writeInt(courtier.getNport());
			    outObject.flush();
			    System.out.println("envoie des paramètres à client terminé");
				
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
