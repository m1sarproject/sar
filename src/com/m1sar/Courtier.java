package com.m1sar;
/**@author Ouerdia
 * 
 */
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Courtier extends Thread{


private String name;
/**
 * nb is used to ensure that the name of the broker is unique 
 */
private static int nb=1;
private ArrayList<Client> customers= new ArrayList<Client>();
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int id;
private int nbCustomer;
public static double tauxCommission=0.1; //un taux de 10% pour tous les courtiers
private double accountBalance=0.;

public Courtier(String name) {
	
	id=nb;
	this.name=name+nb++;
}

/**
 * Send to all his cutomers the share prices that have been updated by the stock market 
 */
public void sendUpdatedPrices() {
	
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
 * informe le client de la transaction (accord) effectu�e pour qu'ils mettent � jours leurs portefeuilles
 */
public void SendAccordInformation() {
	
}


public double getTauxCommission() {
	return tauxCommission;
}



}
