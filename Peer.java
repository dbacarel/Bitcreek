/**.
 * 
 */


import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author Francesco
 *
 */
public class Peer {

	/**
	 * 
	 */
	
	protected static int porzione = 4096;
	protected static Thread_Manager threads;
	protected static PollingManager poll_man;
	protected static Dispatcher disp_server;
	protected static GestoreFiles gest_files;
	protected int porta;
	protected static InetAddress server;
	private static final int max_tentativi=10; // corrisponde alle istanze di Bit_Creek possibili in una stessa macchina
	private static final int porta_iniziale = 4000;
	
	// variabili di prova
	protected static int prova;//TODO togliere
	protected static ArrayList<String> hash_babbo;
	
	
	public Peer() {	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		
		inzializza();
		
		/*
		if(args[0].equals("up"))
			upload("babbo"); // TODO il server sovrascrive il descrittore con uno con lo stesso nome
		
		if(args[0].equals("down"))
			{//leggiComandi();
			//caricaStato();	
			download("babbo");
			}
		if(args[0].equals("prova1"))  // scaricatore
		{
				prova=1;
				
			    ArrayList<Pezzo> chunks = scomposizione("babbo");
				int dim=chunks.size();
				hash_babbo= hash_from_pezzi(chunks,dim) ;
				System.out.println("simulazione 1 completata: lista Hash:");
				for(Pezzo j :chunks)
					System.out.println(j.hash+"|");
									
				
				download("babbo");
				
								
		}
				
		if(args[0].equals("prova2"))  // dispatcher
				{
					int dim;
					
					ArrayList<Pezzo> chunks = scomposizione("babbo");
					dim=chunks.size();
					ArrayList<String> hash_array= hash_from_pezzi(chunks,dim) ;
					
					//inserisco la rappresentazione file in memoria
					Descrittore	nuovo_desc= new Descrittore("babbo", hash_array, dim)	;
					FileAccess file_acc=null;
					try {
						file_acc = new FileAccess(new Rapp_file(nuovo_desc.nome,chunks));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println("Problemi con la creazione del file");
						e.printStackTrace();
					}
					gest_files.aggiungi(file_acc);
					
					System.out.println("simulazione 2 completata:lista hash:");
					//for(String j :hash_array)
					//	System.out.println(j+"|");
					
					prova=2;
				}
		*/
		
	}

	private static int leggiComandi(){
		// per i file completi devo soltanto aggiungere la rappresentazione in memoria 
		
		// apro il file comandi che deve essere nella cartella
		FileReader com=null;
		try {
			com = new FileReader("./comandi");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("### File dei comandi NON trovato ! ###");
			System.exit(1);
			e.printStackTrace();
		}
		
		// creo il Buffered Reader per la lettura comandi
		BufferedReader comandi=new BufferedReader(com);
		String nuovo_com = null;
		
		// per ogni linea viene chiamata la funzione analizza
		int conta_righe=0; 	// per fornire un output di errore dettagliato
		boolean analisi_ok=false;	// risultato di analizza
		try {
			while((nuovo_com=comandi.readLine()) != null){
				System.out.println("@ Leggo la linea "+ nuovo_com);
				if(nuovo_com != null)
					{
					// analizzo e effettuo il comando letto
					analisi_ok=analizza(nuovo_com);
									
					if(!analisi_ok)
						System.out.println("### Errore nel parising del comando alla linea "+conta_righe );
					
					}else
						{
						// nel caso venga letto null
						if(conta_righe==0)
							System.out.println("### il file è vuoto" );
							else
								System.out.println("### Letta riga vuota !" );
						}
			conta_righe++;	
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("### errore nella lettura della riga"+conta_righe );
		}
			
		return 0;
	}
	
	/**Effettua il parsing dei comandi
	 * @param s la stringa del comando
	 * @return
	 */
	private static boolean analizza(String s){
		
		 // tokenizer utilizzato per il parsing 
		
		 StringTokenizer tokenizer = new StringTokenizer(s);
		 String comando=null;
			 
		 // estraggo la prima parola dell'istruzione sarà la classe del comando
	     if(tokenizer.hasMoreTokens() )
	       	comando = tokenizer.nextToken();   
	       	else
	       		{
	       		System.out.println("## Letta riga vuota");
	       		return false;
	       		}
		
	     // caso download, il primo parametro sarà il file
	     if( comando.equals("download"))
	        	{
	        	String nome_file=null;
	        	if(tokenizer.hasMoreTokens())
		        	{
	        		nome_file = tokenizer.nextToken();      // get first word
	        		download(nome_file);
		        	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Ho lanciato il thread download di "+ nome_file);
		        	return true;
		        	}
	        		else{
		        		// in caso di errore viene stampata una stringa e si passa alla riga successiva
		        		System.out.println("## Manca il nome file in un comando");
	        			return false;
	        			}
	        	}
	     
	     // il caso upload è simile
	     if( comando.equals("upload"))
        	{
        	String nome_file=null;
        	if(tokenizer.hasMoreTokens())
	        	 {
        			nome_file = tokenizer.nextToken();
        			upload(nome_file);
                	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Ho lanciato l'upload di "+ nome_file);
                	return true;
	        	 }
	        	else{
	        		System.out.println("## Manca il nome file in upload");
	        		return false;
	        		}
        	       	
        	}
	
	     // DEFAULT :
	        System.out.println("## Classe comando non riconosciuta ");
	        return false;
	        		
		
	}
	
	/**Carica in memoria gli oggetti FileAccess corrispondenti a file completamente o parzialmente scaricati eventualmente presenti in memoria
	 * 
	 * @return ritorna un intero maggiore di 0 in caso di errore
	 */
	private static int caricaStato(){
		
		// le rappresentazioni dei file che fanno parte dello stato sono salvate nella cartella torrents, vengono inserite in memoria
		ArrayList<FileAccess> rapp=FileSysMan.leggi_torrents();
				
		for(FileAccess i:rapp)
			{
			// aggiungo i descrittori
			gest_files.aggiungi(i);
			if(i.isSeeder())
				{
				download(i.getNome());  // che comunque ricontatta server e tracker
				System.out.println("@ ora farei ripartire il download di"+ i.getNome());
				}
			}
			
		return 0;
	}
	
	/**Metodo che effettua l'upload del file su server di indirizzo noto 
	 * 
	 * @param nome nome del file
	 * @return TODO
	 */
	private static int upload( String nome){
		
		
		
		System.out.println("@ entro in upload !!");
		
		// effettua la scomposizione del file in pezzi
		ArrayList<String> hash_array= scomposizione(nome);
		
		//creo da questa scomposizione un oggetto di tipo Descrittore e uno di tipo FileAccess
		Descrittore	nuovo_desc= new Descrittore(nome, hash_array, hash_array.size())	;
		FileAccess file_acc=null;
		try {
			file_acc = new FileAccess(new Rapp_file(nuovo_desc.nome,hash_array, true));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			System.out.println("# Problemi con la creazione del file");
			e2.printStackTrace();
			return 1;
		}
		
		// inserisco la rappresentazione file in memoria
		if(file_acc != null)
			gest_files.aggiungi(file_acc);
		
		
		System.out.println("descrittore creato");
		System.out.println("il nuovo descrittore:\nnome : "+nuovo_desc.nome +"\nnumpezzi : "+nuovo_desc.num_pezzi+"\nhash array:\n"+nuovo_desc.hash_array.get(1));
		System.out.println("____________________________________");
				
		// ################  RMI ################
		
		// creazione del security manager necessario all'RMI
		if (System.getSecurityManager() == null) 
	            System.setSecurityManager(new SecurityManager());
	     		
		Object o=null;
		InetAddress my_addr=null;				// mio inetaddress
		
		// TODO vedere qui con daniele
				
		Integer[] porte=null;
		try {
			//o = Naming.lookup("rmi://192.168.0.10:1099/srmi"); TODO qui deve essere non statico, però deve conoscere il server
			Registry registry = LocateRegistry.getRegistry("192.168.0.10");
            o = registry.lookup("srmi");
			my_addr = InetAddress.getLocalHost();
			System.out.println("@ Il mio ip è: "+ my_addr.getHostAddress());
			RMIServerInt bcs=null;
			if(o != null)
				{
				bcs=(RMIServerInt)o;						// oggetto di tipo RMIServerInt che userò
				porte=bcs.publish(nuovo_desc, my_addr );	//
				}else{
					System.out.println("# Problemi con l'RMI in upload");
					return 1;
					}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("# Problemi con l'RMI in upload");
			return 1;
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("# Problemi con l'RMI in upload");
			return 1;
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("# Problemi con l'RMI in upload");
			return 1;
		}
		
		// questo serve per contattare il tracker e essere aggiunto allo swarm
		download(nome);
				
		System.out.println("Porte: tcp:"+porte[0]+" udp: "+porte[1]);
		System.out.println("____________________________________");
				
		return 0;
	}
	
	/** Effettua la spawn di un thread con task Downloader
	 * @param nome nome del file da scaricare
	 * @return
	 */
	private static int download( String nome ){
				
		threads.aggiungi_down(new Downloader(nome, poll_man, server));
				
	return 0;
	}
	
	/** Effettua la scomposizione di un file ed effettua l'hash dei pezzi
	 * 
	 * @param input
	 * @return ritorna un'arraylist di stringhe rappresentanti gli hash dei singoli pezzi
	 * 
	 * 	 */
	private static ArrayList<String> leggi_e_dividi(FileInputStream input) {
		
		int letti=0;										// variabile d'appoggio per il risultato della read
		ArrayList<String> temp = new ArrayList<String>(); 	// l'array da restituire
		byte[] nuova_porzione;								// variabile d'appoggio
		
		MessageDigest md = null;
		try {
			md=MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			// TODO questo non succederà mai finche l'algoritmo sha sara supportato da java 
			System.out.println("! L'algoritmo SHA non è supportato !");
			e1.printStackTrace();
		}
		
		do{			
			nuova_porzione = new byte[porzione]; 	// resetto il buffer
									
			try {
				// estraggo i prossimi "porzione" dati 
				letti = input.read(nuova_porzione, 0, porzione);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(letti != -1)
			{	
		    //effettuo lo sha dei dati	
			md.update(nuova_porzione);				
			
			byte[] risultato= md.digest();
			
			// converto l'array in una stringa 			
			String h_l=hash_leggibile(risultato);
			//System.out.println("letti "+ letti + "caratteri, porzione letta " + nuova + "hash è :"+h_l); 
								
			temp.add(h_l);	// e lo aggiungo all'arraylist
			}
			
		}while(letti != -1);
			
		return temp;
	}

	
	
	/**
	 * @param file TODO può essere sia String che FIle, attenzione a questo !!
	 * @return
	 */
	public static ArrayList<String> scomposizione(Object file) {	
		
		// @@@@@  Fase di apertura del file
		FileInputStream in = null;
		
		// il parametro è generico, testo il tipo per effettuare i giusti cast
		try {
			if(file.getClass()==String.class)
				in=new FileInputStream((String)file);
				else
					if(file.getClass()==File.class)
					in=new FileInputStream((File)file);
					else 
						{
						System.out.println("### Tipo di paramentro per scomposizione errato");
						return null;
						}

		} catch (FileNotFoundException e1) {
			// errore nell'apertura file
			System.out.println("problemi con l'apertura di "+ file);
			e1.printStackTrace();
		}
		
	    ArrayList<String> hash_arr = leggi_e_dividi(in);  
			    
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// chiude il file 
	    System.out.println("letto tutto il file !");		
			    
	    return hash_arr;	    
	}
	
	
	/**trasforma un array di caratteri (hash) in una stringa leggibile	 * 
	 * 
	 * @param array l'array di ingresso
	 * @return una stringa
	 */
	public static String hash_leggibile(byte []array){
		
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<array.length;i++) {
		hexString.append(Integer.toHexString(0xFF & array[i]));
		}
		String normale= hexString.toString();
		
		return normale;
		
	}
	
	private static int inzializza(){
		threads= new Thread_Manager();
		prova=0;//TODO temporanea, solo per far comunicare i due client
				
		//TODO questo deve diventare paramentrico
		try {
			server= InetAddress.getByName("192.168.0.10");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// inisializzo le classi di TOOL
		poll_man= new PollingManager(server);
		gest_files=new GestoreFiles();
		
		int tentativi=0;
		ServerSocket sock= null;
		
		while(tentativi < max_tentativi && sock == null)
		{
				try {
					sock= new ServerSocket(porta_iniziale+tentativi);
					System.out.println("@ Dispatcher Creato !");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("## Dispatcher: problemi nella creazione del socket, la porta, porta_temp è occupata !!");
					tentativi ++;
					}
		}
		
		// nel caso perssimo devo terminare il programma
		if(sock==null)
			{
			System.out.println("IL numero di porte occupate è eccessivo, impossibile creare un dispatcehr server, il programma dovrà essere terminato. ");
			System.exit(1);  // chiudo il programma
			}
		
		disp_server= new Dispatcher(gest_files, sock);
		
		threads.aggiungi_tool(disp_server);
		threads.aggiungi_tool(poll_man);
		
		leggiComandi();
		caricaStato();
		
		return 0;
		
	}
	
}


