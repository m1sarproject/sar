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
	private Socket currentClient;
	private int nbCustomer=0;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	BufferedReader in; PrintWriter out;
	ObjectOutputStream outObject;ObjectInputStream inObject;
	private String nomCourtier;
	private static int timeLimit=3000;//le temps qu'un courtier attend avant de se deconnecter
	
	
	public ThreadCourtier(Bourse b) {
		this.bourse=b;
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
    			//in =new BufferedReader(new InputStreamReader(inS));
    			//out=new PrintWriter(outS,true);
    			outObject=new ObjectOutputStream(outS);
				inObject=new ObjectInputStream(inS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			try {
					String rep=(String)inObject.readObject();//au premier �change le client envoie son nom
					
					//donner au  client la liste des prix des entreprises
					while (true)
					{ 
						//r�cuperer un ordre du client 
						Object o=inObject.readObject();
						if(o instanceof String) {
							rep=(String)o;
							if(rep.equals("bye")) break;
						}
						//envoyer a la bourse l'ordre
						//on attend
						//l'accord se transmet quand au client?
						//transmission des prix?
					}
					majClient();
				
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//r�cup�rer le nom du client 
    			
    			catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    //r�cup�rer les in et out du 1er client car on traite les clients s�quentiellement et on supprime la socjet du sClient pour dire qu'on va travailer avec ce client
    //while(rep!=bye){prendre les ordres des clients ;les transmettre � bourse.... les diff�rents �chages}
    //dans ce cas cela ne sert � rien d'avoir la socket courtier ;toutes les m�thodes de courtiers on les impl�mente ici
    		}
    		try {
		    		if(nbCustomer==0) {
						Thread.sleep(timeLimit);
		    		}
			} 
    		catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(nbCustomer==0) break;
    	}
    	//envoyer un message � bourse
    		
    }
	
    	
    public void incNbClient() {
    	nbCustomer++;
    }
    public boolean estDispo() {
		return (nbCustomer<2);
	}

	public void addClient(Socket client) {
    	this.sClient.add(client);
    }
	
	void majClient() throws IOException {
		
		currentClient.close();
		sClient.remove(currentClient);
		nbCustomer--;
		
	}
}
