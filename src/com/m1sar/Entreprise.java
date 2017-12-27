public class Entreprise{

	private String name;
	private int nbActions;
	private ArrayList<Ordre> ordres;
	private double prixUnitaireAction;
	
	
	public Entreprise(String name, int nbActions,double prixUnitaireAction){
		this.name=name;
		this.nbActions=nbActions;
		this.prixUnitaireAction=prixUnitaireAction;
		ordres = new ArrayList<Ordre>();
	}
	
	public int getNbActions(){
		return nbActions;
	}
	
	public double getPrixUnitaireAction(){
		return prixUnitaireAction;
	}
	
	public void setNbActions(int n){
		nbActions=n;
	}
	
	public void setPrixUnitaireAction(double n){
		prixUnitaireAction=n;
	}
	public String toString(){
		return "Entreprise : "+ name +" a mis à disposition : "+ nbActions + " d'actions sur le marché au prix unitaire suivant :"+prixUnitaireAction;
	}
	
}

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

public class OrdreAchat extends Ordre{

	public OrdreAchat(String id, double taux_com){
		
		super(id,taux_com);
	}
	public int calculComission(){
		...........
	}

}

public class OrdreVente extends Ordre{


	public OrdreVente(String id, double taux_com){
		
		super(id,taux_com);
	}
	public int calculComission(){
		...................	
	}
}	

}
