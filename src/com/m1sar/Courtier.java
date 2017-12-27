package com.m1sar;
/**@author Ouerdia
 * 
 */
import java.util.ArrayList;

public class Courtier extends Thread{
	
private String name;
private ArrayList<Client>customers;
/**
 * nbCustomer number of customers which are connected to this Broker
 */
private int nbCustomer;
private double tauxCommission;
private double accountBalance;

/**
 * Send to all his cutomers the updated price of the auction 
 */
public void sendAllPrices() {
	
}

}
