package com.m1sar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
/**@author Ouerdia
 * 
 */
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Courtier extends Thread{


private String name;
/**
 * nb is used to ensure that the name of the broker is unique 
 */
private static int nb=1;
private Vector<Client> customers= new Vector<Client>();
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int id;
private int nbCustomer;
public static double tauxCommission=0.1; //un taux de 10% pour tous les courtiers
private double accountBalance=0.;
private int port;
private InetAddress hote;
Socket sc;
BufferedReader in; 
PrintWriter out;

public Courtier(String name,int port,InetAddress hte) {
	
	id=nb;
	this.name=name+nb++;
	this.port = port;
	this.hote = hte;
}

/**
 * Send to all his cutomers the share prices that have been updated by the stock market 
 */
public void sendUpdatedPrices() {//quand est ce que s'est fait? au début de la journée avant qu'un client ne soit déco ;il faut ajouter un 
								//nombre pour représenter les jours
	
}


/**
 * the brocker sends to his customers the information about the share parices of each company in the stock market  
 */
public void sendInfoCompanies() {
	
}
/**
 * @param sellOrder:the order passed by  the  customer 
 * send to the stock market the sell order  
 */
public void sell() {
	
}
/**
 * * @param buyOrder:the order passed by  the  customer
 * send to the stock market the buy order 
 */
public void buy() {
	
}
/**
 * Calcule la commission et l'envoi au client 
 */
public void CalculCommission() {
	
}
/**
 * informe le client de la transaction (accord) effectuï¿½e pour qu'ils mettent ï¿½ jours leurs portefeuilles
 */
public void SendAccordInformation() {
	
}



public double getTauxCommission() {
	return tauxCommission;
}

public void connexion(){
	ObjectOutputStream oos;;
	try {
	sc= new Socket(hote,port);
	System.out.println("Courtier "+name+" bien connecte a la Bourse");
	oos = new ObjectOutputStream(sc.getOutputStream());
	oos.writeObject(new String("Courtier"));
	}
	catch (Exception e) {
		
	}
}


public static void main(String[] args) throws UnknownHostException {
	int nport = Integer.parseInt(args[0]);
	InetAddress hote = InetAddress.getByName(args[1]);
	Courtier c=new Courtier("courtier",nport,hote);
	Courtier c1=new Courtier("courtier",nport,hote);
	Courtier c2=new Courtier("courtier",nport,hote);
	c.connexion();
	c1.connexion();
	c2.connexion();
}

}
