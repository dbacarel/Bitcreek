

import java.util.ArrayList;
import java.io.*;

public class FileAccess {

	private Rapp_file file ;
	RandomAccessFile file_fisico;
	
	
	
	
    public FileAccess(Rapp_file f) throws FileNotFoundException {
		
    	// TODO Gestire questa eccezione
    	this.file=f;
    	file_fisico = new RandomAccessFile(file.getNome(), "rwd");
			
	}
   
    public byte[] leggi(String hash){
    	
    	synchronized (this) {
    	   	int posizione=this.getSequenza().indexOf(hash);
    	   	try {
				
    	   		this.file_fisico.seek(Peer.porzione*posizione);
		
				byte[] data= new byte[Peer.porzione];
				
				System.out.println("@@@@@@@@@@@@@ sono in leggi: seek : "+Peer.porzione*posizione);
	    		
				this.file_fisico.read(data, 0, Peer.porzione)    	;
				//System.out.println("@@@@@@@@@@@@@ sono in leggi: letto "+ new String(data));
	    		return data;
    	   	
    	   	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("@@@@@@@@@@@@@ problemi con le read");
	    		
			}
    	  return null;
		}
    	
    } 
      
   public Rapp_file getRapp(){
   	
   	synchronized (this) {
   	   	return this.file;
		}
   	
   }
    
    public void scrivi(byte[] data, int off){
       synchronized (this) {	
    	   this.file.inserisci(off);
    	   //TODO dopo un pò trasmetti la lista agli altri peer
    	   try {
    		   file_fisico.seek(Peer.porzione*off);
    	   	   file_fisico.write(data);
    	       } catch (IOException e) {
    	   			// TODO Auto-generated catch block
    	   			System.out.println("La Print è fallita !! ");
    	   			e.printStackTrace();
    	   			}
    	   		
       }
       
       return;
    }    
    
    public ArrayList<String> getSequenza(){
    	synchronized (this) {	
        	return this.file.getSequenza();
    	}
    }
    
    public boolean[] getPresenze(){
    	synchronized (this) {
		return this.file.getPresenze();
    	}		
	}
    
    public int getIndex(String hash){
    	synchronized (this) {
		return this.file.getSequenza().indexOf(hash);
    	}		
	}
    
    public String getNome(){
    	synchronized (this) {	
        	return this.file.getNome();
    	}
    }
    
      
    public int indice_prossimo_pezzo(){
    	synchronized (this) {
    			// TODO per ora scorro l'array cercando il prossimo pezzo mancante
    			
    			System.out.println(" @@ Sono in crea_richiesta_personale");
			
    			int lunghezza=(getPresenze().length)-1;
    			int indice=0;
    			boolean[] pres=this.getPresenze();
    			
    			//System.out.println(" @@ itero con indice "+indice+" e lunghezza "+lunghezza+" , pres[indice] è :"+pres[indice]);
    			while( (indice<lunghezza) &&  (pres[indice] == true))
    			{
    				//System.out.println(" @@ letto un true, quindi itero e vado avanti ");
        			
    				indice++;
    			}	
    			
    			//System.out.println(" @@ ritorno : "+indice+"che è "+pres[indice]);
    			
        		return indice;
    	}
    }
    
    public boolean isSeeder(){
    	synchronized (this) {
    		return (this.file.get_mancanti()==0);
    	}
    	
    }
    
    public String crea_richiesta_personale(boolean[] lista_partner){
    	synchronized (this) {
    			// TODO per ora scorro l'array cercando il prossimo pezzo mancante
    			int prox;
    			
    			System.out.println(" @ Sono in crea_richiesta_personale, mancanti "+this.file.get_mancanti());
    			
    			if(this.file.get_mancanti()!=0)
    			{
    				do{ 
    					prox=this.indice_prossimo_pezzo();
    					//System.out.println("analizzo l'indice "+prox+"che è"+lista_partner[prox]);
        			   }while(lista_partner[prox]==false);
    			
    			System.out.println("Richiedo indice "+prox+" della sequenza");
    		
    			return this.file.getHashFromIndex(prox); 
    			
    			}else 
    				return null;
    			
    			
        		
    	}
    }
    
    
}
