

import java.io.Serializable;
import java.util.*;

public class Rapp_file implements Serializable{

	private static final long serialVersionUID = 1L;
	private String nome;
	private ArrayList<String> sequenza;	// ArrayList di Stringhe hash dei rispettivi pezzi in cui è diviso il file
	private boolean[] presenze;
	private int mancanti;
	
	
	public Rapp_file(String nome, ArrayList<String> hash_array, boolean is_seeder ) {
	
		// QUESTO E' IL COSTRUTTORE PER IL FILE PRESENTE IN MEMORIA	
		
		int dimensione=hash_array.size();
				
		this.sequenza = hash_array;
		
		this.presenze= new boolean[dimensione];	
		
		if(is_seeder)
			this.mancanti = 0; // nel caso il file sia completamente in memoria	
			else
				this.mancanti=dimensione;		// nel caso il file sia da scaricare	
		// INIZIALIZZO L'ARRAY PRESENZE
		if(is_seeder)
			{
			// nel caso il file sia completamente in memoria	
			for(int i=0;i< dimensione;i++ )
				this.presenze[i]=true;
			}else{
				// nel caso il file sia da scaricare	
				for(int i=0;i< dimensione;i++ )
					this.presenze[i]=false;
				}
			
		/*System.out.println("Alla fine sequenza è :");
		 
		for(byte[] i:sequenza)
			System.out.println("| :"+Peer.hash_leggibile(i));*/
		
		this.nome=nome;
	
	}
		
	public void inserisci(int off){
		
		//segnalo l'inserimento nell'array sequenza
		System.out.println(" @@ entrato in inserisci ");
		this.presenze[off]=true;
		System.out.println(" @@ setto l'indice:  "+off+" a true");
		this.mancanti--;
		
		return;
		
	}
	    
    public int get_mancanti(){
		return this.mancanti;
	}
    
    public ArrayList<String> getSequenza(){
		return this.sequenza;
    }
   
    public boolean[] getPresenze(){
		return this.presenze;
	}
   
    public String getHashFromIndex(int indice){
	   System.out.println("Estraggo indice "+indice+" della sequenza:" +this.sequenza.get(indice));
	   //for(String i:this.sequenza)
	   //System.out.println("|"+i);
	      
	   return this.sequenza.get(indice);
				
	}
   
    public String getNome(){
	   return this.nome;
    }
	
}
