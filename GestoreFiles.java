import java.util.*;
/**
 * @author frankie
 *
 */
public class GestoreFiles {
	
	private Hashtable<Integer,FileAccess> database; 
	
	public GestoreFiles(){
		this.database = new Hashtable<Integer,FileAccess>();
		System.out.println("@ Gestore creato !");
		
	}
	
	/**metodo che ritorna true se la rappresentazione del file è già contenuta in memoria
	 * @param nome
	 * @return true se il file è contenuto
	 */
	public boolean contiene(String nome){
		synchronized(this){
		return this.database.containsKey(nome);
		}
		
	}
	
	public int aggiungi(FileAccess nuovo){
		// TODO AGGIUNGO SOLO SE NON E' GIA' ESISTENTE!!!!!
				
		if(this.database.containsKey((int)nuovo.getNome().hashCode()))
			{
			System.out.println("@ Il FileAccess di "+ nuovo.getNome()+" esisteva già");
			return 1;
			}else{	
				this.database.put((Integer) nuovo.getNome().hashCode(),nuovo);
				System.out.println("@ Il FileAccess di "+ nuovo.getNome()+" è stato aggiunto");
				
				return 0;
				}
	}
	
	public void rimuovi(FileAccess goodbye){
		
		this.database.remove(goodbye.hashCode());
		return;
	}
	
	public FileAccess cerca(String nome){
		
	System.out.println("@ gestore files - cerco la chiave stringa "+ nome);	
	return this.database.get(nome.hashCode());
		
	}
	

}
