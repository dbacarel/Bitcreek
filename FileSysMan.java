import java.util.ArrayList;
import java.io.*;

/* Classe collezione di metodi per la ricerca dei file all'interno della cartella */

public class FileSysMan {
	
	public static ArrayList<File> cerca_part(String nome_dir){
		
		ArrayList<File> files = new ArrayList<File>();
	    
		File dir=new File(nome_dir);
	    System.out.println("è partito comunque il costruttore : "+dir);
	    if(dir.canExecute() && dir.isDirectory())
			aggiungi(files,dir);
	    	else
	    		System.out.println("### La directory "+nome_dir+" non è stata trovata");
	   	    
	    return files;
	}

	//TODO cambiare questa versione, è presa on.line
	private static void aggiungi(ArrayList<File> dest, File f){
	    final File[] rami = f.listFiles();
	    if (rami != null) 
	        for (File att :rami ) 
	            {
	            dest.add(att);
	            aggiungi(dest,att);
	            }
	    	
	}
	
	/*  // TODO in teoria questo non ha più senso
	public static ArrayList<FileAccess> leggi_parziali(String path){
	
		ArrayList<FileAccess> parziali= new ArrayList<FileAccess>() ;
		ArrayList<File> temp_presenti=cerca_part(path);
		for(File file:temp_presenti)
			{
			System.out.println("aggiungo "+ file);
			    
			ArrayList<String> chunks=Peer.scomposizione(file);
			if(chunks == null)
				System.out.println("Scomposizione di"+ file+" è ritornata null");
				else{
					FileAccess vecchio_FileAccess=null;
					try {
						vecchio_FileAccess = leggi_torrent();
					} catch (FileNotFoundException e) {
						// TODO Se modifico il costruttore di Rapp FIle questo si può anche togliere
						System.out.println("Scomposizione di"+ file+" ha ritornato File Not Found Exception");
						e.printStackTrace();
					}
					if(vecchio_FileAccess != null)
						parziali.add(vecchio_FileAccess);
					else 
						System.out.println("### File di rappresentazione di"+ file+" non trovato nella cartella torrents");	
				}
			}
		
			return parziali;
				
			
	}
	*/
		
		public static ArrayList<FileAccess> leggi_torrents(){
			// dovra
			ArrayList<FileAccess> parziali= new ArrayList<FileAccess>() ;
			ArrayList<File> temp_presenti=cerca_part("./torrents");
			for(File file:temp_presenti)
				{
				System.out.println("aggiungo "+ file);
				// leggo l'oggetto salvato dal file						
				ObjectInputStream in;
				Rapp_file nuovo_rapp=null;
				try {
					in = new ObjectInputStream(new FileInputStream(file));
					nuovo_rapp=(Rapp_file)in.readObject();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//TODO mettere i controlli per null qui
				FileAccess nuovo_accesso=null;
				try {
					nuovo_accesso = new FileAccess(nuovo_rapp);
					} catch (FileNotFoundException e) {
							// TODO Se modifico il costruttore di Rapp FIle questo si può anche togliere
						System.out.println("Caricamento di "+ file+" ha ritornato File Not Found Exception");
						e.printStackTrace();
						}
					if(nuovo_accesso != null)
						parziali.add(nuovo_accesso);
					
								
				}
			
		return parziali;
	}
		
	
		
		
	public static int Scrivi_torrents( ArrayList<FileAccess> collezione){
			// dovra
			
			for(FileAccess i:collezione)
			{
				ObjectOutputStream out;
				Rapp_file nuovo_rapp=i.getRapp();
				String nome=i.getNome();
				File file= new File("./torrents/", nome);
				
					try {
						out = new ObjectOutputStream(new FileOutputStream(file));
						out.writeObject(nuovo_rapp);
						System.out.println("@ Ho scritto il descrittore di "+ nome);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
			
			return 0;
	}
	


}
