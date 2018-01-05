package com.m1sar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	this.name=name;
	this.id = nb++;
	this.port = port;
	this.hote = hte;
}

/**
 * Send to all his cutomers the share prices that have been updated by the stock market 
 */
public void sendUpdatedPrices() {//quand est ce que s'est fait? au d�but de la journ�e avant qu'un client ne soit d�co ;il faut ajouter un 
	
}


/**
 * the brocker sends to his customers the information about the share parices of each company in the stock market  
 */
public void sendPriceCompanies() {
	
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
 * informe le client de la transaction (accord) effectu�e pour qu'ils mettent � jours leurs portefeuilles
 */
public void SendAccordInformation() {
	
}


public double getTauxCommission() {
	return tauxCommission;
}

public void connexion(){
	
	try {
	sc= new Socket(hote,port);
	inscription(sc);
	}
	catch (Exception e) {
		System.out.println("Erreur de connexion");
	}
}


//Permet simplement de s'inscrire auprès de la bourse en donnant son nom
public void inscription(Socket sc) throws IOException {
	
	OutputStream outS=sc.getOutputStream();
	out=new PrintWriter(outS,true);
	out.println(name);
	
}

public static void main(String[] args) throws UnknownHostException {
	
	int nport = Integer.parseInt(args[0]);
	InetAddress hote = InetAddress.getByName(args[1]);
	
	Courtier b=new Courtier("George Soros",nport,hote);
	b.connexion();
	
	System.out.println("Le courtier s'est connecté à la bourse");
	
	/*Courtier c=new Courtier("Warren Buffet",nport,hote);
	c.connexion();*/

	}

}
