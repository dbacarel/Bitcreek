/**
 * 
 */

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @author frankie
 *
 */
public class PersonalServer implements Runnable{
	
	private FileAccess sorgente;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean[] lista_partner;
	private int ruolo; // 0 aspetta, 1 inizia
	
	
	public PersonalServer(FileAccess roba ,Socket partner, int comincia){
		
		this.ruolo=comincia;
		this.sorgente=roba;
		
		if(this.sorgente!=null)
			System.out.println("@ Sorgente è "+roba.getNome()+"lista: " );
			else
				System.out.println("@ Costruttore di PersonalServer: Sorgente è null" );
			
		//for(String i :sorgente.getSequenza())
		//	System.out.println(i+"|");
		
		try {
			System.out.println("@ Partito il costruttore di personal Server, con ruolo "+ comincia);
			
			if(this.ruolo==1)	// dovrà iniziare la comunicazione
			{				
			OutputStream out_stream = partner.getOutputStream();
			out = new ObjectOutputStream(out_stream);
			System.out.println("@ fatto out !!");
			InputStream in_stream = partner.getInputStream();
			in = new ObjectInputStream(in_stream);
			System.out.println("@ fatto in !!");
			}else{
					InputStream in_stream = partner.getInputStream();
					in = new ObjectInputStream(in_stream);	
					System.out.println("@ fatto in !!");
					OutputStream out_stream = partner.getOutputStream();
					out = new ObjectOutputStream(out_stream);	
					System.out.println("@ fatto out !!");
					}
						
			System.out.println("@ PersonalServer di file: "+roba.getNome()+" Primi streams creati !!");
			
			// nel caso il thread debba non sia creat dal dispatcher in risposta ad una connessione deve effettuare l'handshake con il dispatcher del partner
			if(this.ruolo==1) // allora devo contattare prima il dispatcher
				{
				out.writeObject(new MessP2P(5,roba.getNome()));
				System.out.println("@ messaggio al dispatcher inviato  !!");
				}
				
		} catch (IOException e) {
			System.out.println("# Personal Server: Problemi con la creazione dei socket di comunicazione per il file  "+ sorgente.getNome()+" con il server "+partner.getInetAddress().getHostAddress());
			e.printStackTrace();
		} 
				
		System.out.println("@ costruttore Persona Server terminato !");
	}
	
public PersonalServer(FileAccess roba ,ObjectOutputStream o, ObjectInputStream i, int comincia){
		
		this.ruolo=comincia;
		this.sorgente=roba;
		
		System.out.println("@ Sorgente è "+roba.getNome()+"lista: " );
		//for(String j :sorgente.getSequenza())
		//	System.out.println(j+"|");
		
		this.in=i;
		this.out=o;
						
		System.out.println("@ Primi stream creati !!");
			
		System.out.println("@ costruttore Persona Server terminato !");
	}
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("@ partito il Personal Server.run()  !");
		// variabili d'appoggio per i messaggi entranti ed uscenti
		MessP2P nuova_richiesta = null ;
		MessP2P nuova_risposta = null ;
		
		// ########  questo è il messaggio di starter 
		if(ruolo==1)
			{
			boolean[] lista_mandata=sorgente.getPresenze();
			nuova_risposta=new MessP2P(0,lista_mandata);
			
				try {
					out.writeObject(nuova_risposta);
					System.out.println("@ ho mandato il primo messaggio 0 (ecco la mia lista, mandami la tua)! con lista :");
					//for(boolean i : lista_mandata)
					//	System.out.print(i+"|");
				} catch (IOException e) {
					System.out.println("# Personal Server: Problemi con l'invio della lista per il file  "+ this.sorgente.getNome());
					e.printStackTrace();
					System.out.println( "#### chiudo il Personal Server! " + this.sorgente.getNome());
					
					Thread.yield();
				}		
			}
		
		// ########  questo è il resto della conversazione 
		
		while(true)
		{	
			System.out.println("@ Loop di messaggi, in attesa di una richiesta  !");
			try {
				nuova_richiesta	= (MessP2P) in.readObject();
				System.out.println("@ Ricevuto messaggio !");
				if(nuova_richiesta != null)
					nuova_risposta = elabora(nuova_richiesta);
				if(nuova_risposta!=null){
					System.out.println("@ Nuovo messagio elaborato !");
					out.writeObject(nuova_risposta);
					}else{
						System.out.println("# Elabora ha ritornato null, interrompo il ciclo !");
						break;										
					}
						
			} catch (IOException e) {
				System.out.println("# errore durante l'invio e risposta di un messaggio !");
				e.printStackTrace();
				System.out.println( "#### chiudo il Personal Server! " + this.sorgente.getNome());
				break;
			} catch (ClassNotFoundException e) {
				System.out.println("# errore durante l'invio e risposta di un messaggio !");
				e.printStackTrace();
				System.out.println( "#### chiudo il Personal Server! " + this.sorgente.getNome());
				Thread.yield();
				break;
			}			
		
		
		}
		
	
	}
	
	
	private MessP2P elabora(MessP2P richiesta){
	
	MessP2P risposta;
	System.out.println("@ Entro in elabora, messaggio di tipo !!"+richiesta.codice);
	
		switch (richiesta.codice) {
			case 0:{
				System.out.println("@ Ho ricevuto 0 (dammi la tua lista) Inizio risposta: " );
				
				this.lista_partner=richiesta.lista_presenze;
				boolean[] lista=this.sorgente.getPresenze();
				System.out.println("@ Invio messaggio 1 (ecco la lista) ! lista: ");
				//for(boolean i : lista)
				//	System.out.print(i+"|");
				
				risposta = new MessP2P(1,lista);
				break;
			}
			
			case 1:{
				System.out.println("@ Ho ricevuto 1 (ecco la mia lista, dammi la tua richiesta)" );
				
				if(richiesta.lista_presenze!=null)
				{
					this.lista_partner=richiesta.lista_presenze;
					// ora che ho la lista posso iniziare a 
					String hash_richiesta=this.sorgente.crea_richiesta_personale(lista_partner);
					if(hash_richiesta != null)
						{
						int off=this.sorgente.getIndex(hash_richiesta);
						System.out.println("@ Invio messaggio di tipo 2 (ecco la mia richiesta) richiedendo il pezzo con hash : !"+hash_richiesta );
						
						risposta = new MessP2P(2,hash_richiesta,off);
						}else{
							risposta=null;
						}
					
				}else{
					byte[] vuoto=null;
					risposta = new MessP2P(4, vuoto);
					}
					
				break;
				
			}
			case 2:{
				System.out.println("@ Ho ricevuto 2 (ecco la richiesta) e la elaboro " );
				
				String hash_rich = richiesta.data_str;
				System.out.println("@ l'hash è"+hash_rich+ "lo cerco nella mia lista:" );
				//for(String i :sorgente.getSequenza())
				//	System.out.println(i+"|");
				
				byte[] nuovo= this.sorgente.leggi(hash_rich);
				if(nuovo != null)	
					{
					System.out.println("\n @ ricevuto codice con hash - Invio dati,  !"+hash_rich);
					risposta = new MessP2P(3,nuovo,richiesta.intero);//TODO TEMPORANEO
					}
					else
						{
						System.out.println("@ i dati letti sono NULL!" );
						risposta = null;
						}
				
				break;
			}
			case 3:{
				System.out.println("@ Ho ricevuto 3 (ecco il dato) e dovrei chiedere qualcosa.. " );
				//System.out.println("@ Ho ricevuto 3 (ecco il dato) e dovrei chiedere qualcosa.. "+ new String(richiesta.p.dati) );
				
				this.sorgente.scrivi(richiesta.data_arr,richiesta.intero);
				
				System.out.println("@ Scrivi superata" );
				
				// ora che ho la lista posso iniziare a fare richieste 
				String hash_richiesta=this.sorgente.crea_richiesta_personale(lista_partner);
				
				if(hash_richiesta != null)
				{
					int off=this.sorgente.getIndex(hash_richiesta);
					System.out.println("@ Invio messaggio di tipo 2 (ecco la mia richiesta) richiedendo il pezzo con hash : !"+hash_richiesta +" e offset :" + off);
					
					risposta = new MessP2P(2,hash_richiesta,off);
					
				break;
				}else 
					{
					System.out.println("@ Ho completato la completezza del file " );
					risposta=null;
					}
				break;
			}
			case 4:{
				System.out.println("# ho ricevuto un messaggio d'errore !");
				risposta=null;
				break;
			}
			
			default:{
				System.out.println("@ Inizio risposta default" );
				
				byte[] vuoto=null;
				risposta = new MessP2P(4, vuoto);
				break;
			}				
		}	
		
	return risposta;
		
	}

	
	

}
