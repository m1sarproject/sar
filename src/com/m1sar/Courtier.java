package com.m1sar;
/**@author Ouerdia
 * 
 */
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Courtier extends Thread{
	
private String name;
private ArrayList<Client> customers= new ArrayList<Client>();
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int nbCustomer;
private double tauxCommission=ThreadLocalRandom.current().nextDouble(0.05, 0.1); //un taux aléatoire entre 5% et 10%
private double accountBalance=0.;

/**
 * Send to all his cutomers the updated price of the auction 
 */
public void sendAllPrices() {
	
}

public double getTauxCommission() {
	return tauxCommission;
}





}
