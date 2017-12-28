package com.m1sar;

public abstract class Ordre{
	
	private String id;        // MOFIFIE PAR RAPPORT AU DIAGRAMME DE CLASSES
	private double taux_com;
	//private double prix_Max;
	
	public Ordre( String id, double taux_com){
		this.id=id;
		this.taux_com=taux_com;
		
		
		
	}
	 
	public abstract int calculComission();

}