package com.m1sar;

import java.net.Socket;
import java.util.Vector;

public class ThreadCourtier extends Thread {

	private Socket sCourtier; //socket pour communiuqer avec courtier
	private Vector<Socket> sClient;//socket de communication avec les clients
	private Bourse bourse;//la bourse qui a créé le Threadcourtier 
	public ThreadCourtier(Socket ssv) {
		this.sCourtier=ssv;
		//récupere la bourse
		start();
	}
    @Override
    public void run() {
    	
    	//récupérer les in et out de courtier
    	while (true)//ici quand tout les courtiers ou clients (pas sur) se déco on sort du while 
    	{
    		//s'il y'a un client dans notre liste on commence par traiter ce client
    		if(sClient.size()>0) {
    			//récupérer les in et out du 1er client car on traite les clients séquentiellement et on supprime la socjet du sClient pour dire qu'on va travailer avec ce client
    			//while(rep!=bye){prendre les ordres des clients ;les transmettre à bourse.... les différents èchages}
    			//dans ce cas cela ne sert à rien d'avoir la socket courtier ;toutes les méthodes de courtiers on les implémente ici
    		}
    	}
    	
    	
    }
}
