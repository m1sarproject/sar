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
	private Vector<Socket> sClient=new Vector<Socket>();//socket de communication avec les clients
	private Socket currentClient;
	private int nbCustomer=0;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	private BufferedReader in; 
	private PrintWriter out;
	private ObjectOutputStream outObject;
	private ObjectInputStream inObject;
	private String nomCourtier;
	private static int timeLimit=30000;//le temps qu'un courtier attend avant de se deconnecter
	
	
	public ThreadCourtier(Bourse b) {
		this.bourse=b;
		start();
		//a revoir 
	}
	
    @Override
    public void run() {

    	OutputStream outS;
		InputStream inS;
		String rep="",req="";
		
    	while (true)//ici quand tout ou clients (pas sur) se deco on sort du while  
    	{

    		//s'il y'a un client dans notre liste on commence par traiter ce client
    		if(sClient.size()>0) {
    			currentClient=sClient.firstElement();
    			System.out.println(currentClient);
    
    			//System.out.println(currentClient);
    			try {
		    			inS=currentClient.getInputStream();
		    			outS=currentClient.getOutputStream();
		    			out=new PrintWriter(outS,true);
		    			in =new BufferedReader(new InputStreamReader(inS));
		    			System.out.println("client connecte a ce courtier");
		    		    req=in.readLine();
		    			System.out.println("le client dit a "+nomCourtier+req);
		    			out.println("bienvenu cher client,vous pouvez envoyez vos ordre"+req);
		    			//envoie d'ordre
		    			System.out.println("h1");
		    			while (true)
		    			{System.out.println("h2");
							req=in.readLine();
							System.out.println("h3");
		    				System.out.println("le client me dit (courtier)"+req);
		    				if(req.equals("bye")) {
		    				//supprimer le client et fermer sa socket et decremente nbcustumer
		    					System.out.println("je susi dans le if du bye");
		    					majClient();
		    					break;
		    				}
		    			}
		    			
    			}
    			catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    		
    	}
    		if(nbCustomer==0) {
	    		try {
	    			    System.out.println("j'attend qu'un client se connect");
						Thread.sleep(timeLimit);
					} 
	    		catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
		    
				if(nbCustomer==0) {
					System.out.println("j'ai fini y'a pas de clients");
					bourse.removeBroker(this);
					break;//sortir du while(true)
					
				}
				
    }
    	//envoyer un message � bourse
}
	
    	
    public void incNbClient() {
    	if (estDispo()) nbCustomer++;
    
    	throw new UnsupportedOperationException("Le courtier a déja deux clients en charge");
    }
    public boolean estDispo() {
		return (nbCustomer<2);
	}

	public void addClient(Socket client) throws IOException {
    	this.sClient.add(client);
    	incNbClient();
    	
    }
	
	void majClient() throws IOException {
		
		currentClient.close();
		sClient.remove(currentClient);
		nbCustomer--;
		
	}
}
