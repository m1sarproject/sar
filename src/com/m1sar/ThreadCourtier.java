package com.m1sar;

import java.net.Socket;
import java.util.Vector;

public class ThreadCourtier extends Thread {

//	private Socket sCourtier; //socket pour communiuqer avec courtier
	private Vector<Socket> sClient;//socket de communication avec les clients
	private Socket currentClient;
	private Bourse bourse;//la bourse qui a cr�� le Threadcourtier 
	private boolean dispo=true;
	public ThreadCourtier() {
		start();//a revoir 
	}
	
    @Override
    public void run() {
    	currentClient=sClient.firstElement();
    	while (true)//ici quand tout ou clients (pas sur) se d�co on sort du while 
    	{
       		//s'il y'a un client dans notre liste on commence par traiter ce client
    		if(sClient.size()>0) {
    			//r�cup�rer les in et out du 1er client car on traite les clients s�quentiellement et on supprime la socjet du sClient pour dire qu'on va travailer avec ce client
    			//while(rep!=bye){prendre les ordres des clients ;les transmettre � bourse.... les diff�rents �chages}
    			//dans ce cas cela ne sert � rien d'avoir la socket courtier ;toutes les m�thodes de courtiers on les impl�mente ici
    		}
    	}
    	
    	
    }
    public void addClient(Socket client) {
    	this.sClient.add(client);
    }
}
