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
	
	private int nport;
	private ServerSocket serveurClient=null;
	private Socket clientConnecte = null;
	private Vector<ThreadBourse> listcourtiers;

	public AnnuaireClient(int nport,Vector<ThreadBourse> courtiers) {
		super();
		this.nport = nport;
		listcourtiers = courtiers;
		start();
	}
	
	
	
	
	public ThreadBourse getFreeCourtier() {
		
		for (ThreadBourse courtier : listcourtiers) {
			if (courtier.estDispo())  
				{
				courtier.incNbClient();//incrementer nombre de client
				return courtier;	
				}
		}
		
		return null;
	}
	
	public void run () {
		
		try {
			serveurClient = new ServerSocket(nport);
			System.out.println("Le serveur client est a  l'ecoute sur le port "+nport);
		
		
		while (true) {
			
			clientConnecte = serveurClient.accept();
			System.out.println("Connexion client accepter par BourseClient");	
			ThreadBourse tcourtier=getFreeCourtier();
			OutputStream outS=clientConnecte.getOutputStream();
			ObjectOutputStream outObject=new ObjectOutputStream(outS);
			if(tcourtier!=null) {
			    //envoyer le numero de port et  @ip inetAddress du courtier au client
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
