package com.m1sar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class ThreadCourtier extends Thread {

//	private Socket sCourtier; //socket pour communiuqer avec courtier
	private Vector<Socket> sClient;//socket de communication avec les clients
	private Vector<String>nomClient;//noms des clients que ce courtier traite 
	private Socket currentClient;
	private int nbCustomer=0;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	BufferedReader in; PrintWriter out;
	ObjectOutputStream outObject;ObjectInputStream inObject;
	private String nomCourtier;
	public ThreadCourtier() {
		start();//a revoir 
	}
	
    @Override
    public void run() {
    	
    	while (true)//ici quand tout ou clients (pas sur) se d�co on sort du while 
    	{
       		//s'il y'a un client dans notre liste on commence par traiter ce client
    		if(sClient.size()>0) {
    			currentClient=sClient.firstElement();
    			OutputStream outS=null;
    			InputStream inS=null;
    			try {
    			inS=currentClient.getInputStream();
    			outS=currentClient.getOutputStream();
    			in =new BufferedReader(new InputStreamReader(inS));
    			out=new PrintWriter(outS,true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			try {
					String rep=in.readLine();//au premier �change le client envoie son nom
					nomClient.add(rep);
					while (rep!="fin de journee")
					{ 
						//envoyer un ordre 
						outObject=new ObjectOutputStream(outS);
						inObject=new ObjectInputStream(inS);
						outObject.writeObject(new String(" je suis courtier envoyer un ordre"));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//r�cup�rer le nom du client  
    			
    			
    //r�cup�rer les in et out du 1er client car on traite les clients s�quentiellement et on supprime la socjet du sClient pour dire qu'on va travailer avec ce client
    //while(rep!=bye){prendre les ordres des clients ;les transmettre � bourse.... les diff�rents �chages}
    //dans ce cas cela ne sert � rien d'avoir la socket courtier ;toutes les m�thodes de courtiers on les impl�mente ici
    		}
    	}
    	
    	
    }
    public boolean estDispo() {
		return dispo;
	}

	public void addClient(Socket client) {
    	this.sClient.add(client);
    }
}
