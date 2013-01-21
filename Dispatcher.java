/**
 * 
 */


import java.net.*;
import java.io.*;

/**
 * @author frankie
 *
 */
public class Dispatcher implements Runnable {
		
	protected ServerSocket sock ;
	protected GestoreFiles gest;
		
	public Dispatcher(GestoreFiles gestore, ServerSocket socket){
		this.gest=gestore;  // TODO potrebbe anche usare quello static della classe Peer
		this.sock=socket;
						
	}
	
	public void run(){
		
		while(true){
		
		Socket s;
		PersonalServer nuovo_server = null;
		
		try {
			System.out.println("@ Dispatcher avviato !");
			
			// accetto la nuova connessione
			s = this.sock.accept();
			
			System.out.println("@ Dispatcher: connessione ricevuta !");
			
			InputStream presentazione = s.getInputStream();
			ObjectInputStream data_pres = new ObjectInputStream(presentazione);	
			// output
			OutputStream risposta = s.getOutputStream();
			ObjectOutputStream data_risp = new ObjectOutputStream(risposta);	
			
			System.out.println("@ Dispatcher: streams attivati ! !");
			
			// leggo il messaggio di richiesta 
			MessP2P richiesta = (MessP2P) data_pres.readObject();
						
			if(richiesta.codice==5 && richiesta != null && richiesta.data_str!=null){
				// spawn del nuovo server
				System.out.println("@ Dispatcher: ricevuto messaggio ! !");
				
				FileAccess accesso_file = this.gest.cerca(richiesta.data_str) ;
				if(accesso_file !=null)				
					{
						// creo il nuovo server personale e lo faccio partire
						
						nuovo_server = new PersonalServer(accesso_file,data_risp,data_pres,0);
						if(nuovo_server != null)
							{
							Peer.threads.aggiungi_share(nuovo_server);
							System.out.println("@ Dispatcher: fatta la spawn del nuovo server ! !");
							}else{
								System.out.println("##### la creazione del server personale per il messagio di richiesta riguardante il file "+richiesta.data_str+ " non ha avuto successo. ");
								}
						
						}else{
						System.out.println("## File richiesto"+richiesta.data_str+" non trovato !! ");
						data_risp.writeObject(new MessP2P(4,(byte[])null));
						}
				
				}else
					{
					System.out.println("## Ricevuto Messaggio di presentazione al Dispatcher con codice diverso da 5, o messaggio nullo!! ");
					data_risp.writeObject(new MessP2P(4,(byte[])null));
						
					}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("## Dispatcher: problemi nel ricevere la connessione ! !");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("## Dispatcher: problemi nel ricevere la connessione ! !");
			
		}
						
			
		}
		
	}
	
	

}
