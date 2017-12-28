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
private static int nb=0;
private ArrayList<Client> customers= new ArrayList<Client>();
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int id;
private int nbCustomer;
private double tauxCommission=ThreadLocalRandom.current().nextDouble(0.05, 0.1); //un taux alÃ©atoire entre 5% et 10%
private double accountBalance=0.;

public Courtier(String name) {
	nb++;
	this.name=name+nb;
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
 * informe le client de la transaction (accord) effectuée pour qu'ils mettent à jours leurs portefeuilles
 */
public void SendAccordInformation() {
	
}


public double getTauxCommission() {
	return tauxCommission;
}



}
