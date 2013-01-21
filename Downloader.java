

 import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Downloader implements Runnable{

	private ArrayList<InetAddress> lista_peers;
	private String nomefile;
	private PollingManager polling_man;
	private InetAddress server;
	
	
	public Downloader(String nome, PollingManager pol_man, InetAddress server){
		this.nomefile=nome;
		this.polling_man=pol_man;
		this.server=server;
		
	}	
	
	private Descrittore contatta_server(String nome) {
		
		// variabili per l'RMI
		RMIServerInt serv=null;				//server
		Descrittore descr_rit=null;			//descrittore ritornato
		
		if(nome==null)
			{
			System.out.println("## contatta_server di Download ha ricevuto parametro null ! ");
			return null;
			}
		
		System.out.println("@ provo a contattare il server RMI ");
		// ################  RMI ################
		if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new SecurityManager());
	     }
		
		Object o=null;
		try {
			//o = Naming.lookup("rmi://192.168.0.10:1099/srmi");
			Registry registry = LocateRegistry.getRegistry(server.getHostAddress());
            o = registry.lookup("srmi");
            
		} catch (RemoteException e) {
			System.out.println("## Problemi nell'RMI di Download - contatta_server di "+ nome);
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("## Problemi nell'RMI di Download - contatta_server di "+ nome);
			e.printStackTrace();
		}
		
		if(o == null)
			{
			System.out.println("## l'RMI di Download - contatta_server di "+ nome + " ha ritornato l'oggetto o null");
			return null;
			}
		
		serv=(RMIServerInt)o;
			
		try {
			descr_rit=serv.lookup(nome, InetAddress.getLocalHost());
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("## Problemi con Lookup di "+ nome );
			return null;
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("## Problemi con Lookup di "+ nome );
			return null;
		}	
		
		return descr_rit;
				
	}	
	
private ArrayList<InetAddress> contatta_Tracker(Descrittore descr){
		
		InetAddress server=null;
		ObjectInputStream data_in;
		ObjectOutputStream data_out;
		InputStream in;
		OutputStream out;
		ArrayList<InetAddress> lista= null;
		/* socket per la comunicazione TCP */
		SSLSocketFactory factory=null;
		SSLSocket socket=null;
		
		// ###### preparo l'indirizzo
		try {		
			server = InetAddress.getByName("192.168.0.10");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			}
						
		// ###### creo il socket
		
		if(descr == null)
			{
			System.out.println("## contatta_tracker di Download ha ricevuto parametro null ! ");
			return null;
			}
		
		System.out.println("@ provo a contattare via TCP il Tracker a "+server.toString()+ " porta "+descr.getTcp());
			
		try {
			factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			socket = (SSLSocket)factory.createSocket(server, descr.getTcp());
		} catch (IOException e1) {
			System.out.println("# eccezione nella creazione socket verso il Tracker TCP");
			e1.printStackTrace();
		}

		System.out.println("@ socket creato ");
		
		if(factory == null || socket == null)
			{
			System.out.println("## la connessione ssl di Download ha ritornato null ! ");
			return null;
			}
		
		try {
			System.out.println("@ out_stream ");
			out = socket.getOutputStream();
			System.out.println("@ out ");
			data_out = new ObjectOutputStream(out);
			System.out.println("@ in_stream ");
			in = socket.getInputStream();
			System.out.println("@ in ");
			data_in = new ObjectInputStream(in);
				
			System.out.println("@ Downloader:  streams ok  ");
					
			data_out.writeObject(descr); // TODO Perchè ?
			data_out.flush();
			
			System.out.println("@ descrittore mandato  ");
					
			lista = (ArrayList<InetAddress>)data_in.readObject();
			if(lista==null)
				System.out.println("@ ritornata lista vuota dal tracker riguardo :  "+ descr.nome); // TODO sarebbe un caso plausibile
				else
					System.out.println("@ lista ricevuta :  "+ lista.toString());
			
			} catch (IOException e) {
				System.out.println("# eccezione I/O nella creazione socket verso il Tracker TCP");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("# eccezione C N F nella creazione socket verso il Tracker TCP");
				e.printStackTrace();
			}  
			
			return lista;
	}
	
	private int contatta_Peer (FileAccess file_acc, InetAddress partner, int porta){
		
		/* socket per la comunicazione TCP */
		Socket socket=null;	
		
		PersonalServer nuovo;
		
		if(file_acc == null || partner == null || porta==0)
		{
		System.out.println("## contatta_peer ha ricevuto almeno un parametro null o porta passata 0 ! ");
		return 1;
		}
		
		System.out.println("@ provo a contattare il Peer "+partner.getHostAddress());
		
		// creo il socket
		try {
			socket = new Socket(partner,porta); // TODO 4000 è default, diventerà paramentro
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("# eccezione nella creazione socket verso il peer:"+ partner.getHostAddress());
			e1.printStackTrace();
			return 1;
		}
		
		System.out.println("@ socket creato ");						
		System.out.println("@ streams ok, chiedo riguardo al file"+ file_acc.getNome());

		if(socket != null)
			{
			nuovo = new PersonalServer(file_acc,socket, 1);
			if(nuovo != null)
				Peer.threads.aggiungi_down(nuovo);
				else 
					{
					System.out.println("# Problemi con la creazione dell'oggetto del Personal Server per "+ file_acc.getNome());
					return 1;
					}
			}else 
				{
				System.out.println("# Problemi con la creazione del socket con "+ partner.getHostAddress());
				return 1;
				}
		
		return 0;
		
	}
	
	private int vita_sociale (FileAccess file_acc, ArrayList<InetAddress> l_peers){
		int contattato_ok=0;
		int problemi=0;
		System.out.println("@ vista sociale attivata con lista  !"+l_peers.toString() );
			
		for(InetAddress i:l_peers)								
				if(!i.getHostAddress().equals("192.168.0.10"))
					{
						System.out.println("@ confronto gli ip di: "+i.getHostAddress()+" con 192.168.0.10");
						contattato_ok=contatta_Peer(file_acc,i, 4000);
						if(contattato_ok != 0)
							{
							System.out.println("# il tentativo di creazione di connessione con il peer  "+i.getHostAddress()+" ha fallito");
							problemi=1;
							}
					}else
						System.out.println("Evito di contattare il mio stesso indirizzo");//TODO togliere questo 
			
		return problemi;
	}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Descrittore descr= null;
		
		
		System.out.println("@ Downloader partito !!");
		
		if(Peer.prova==0)
		{
		
		// contatto il server via RMI		
		descr=contatta_server(this.nomefile);
		
		if(descr == null)
			{
			System.out.println("# RMI non ha trovato il descrittore per "+ this.nomefile);
			return;
			}
			else
				if((this.lista_peers=contatta_Tracker(descr)) == null)
					{
					System.out.println("# Downloader : problemi contattando il tracker per "+ this.nomefile);
					return;
					}
		
		polling_man.aggiungi(descr.nome,(Integer)descr.getUdp());
		System.out.println("ho aggiunto la porta udp");
		
		if(Peer.gest_files.contiene(descr.nome))
			System.out.println("I metadati riguardo al file "+ descr.nome+ "sono già in memoria");
			else{
				FileAccess file_access=null;
				try {
					file_access = new FileAccess(new Rapp_file(descr.nome, descr.hash_array,false));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						System.out.println("# Problemi con la creazione del file fisico di "+ descr.nome+ " !! ");
						e.printStackTrace();
						return;
						}
				
				int is_seeder=0; // di default a 0 (falso)
				// gestore file ritorna l'eventuale condizione di seeder del peer riguardo al determinato file
				if(( is_seeder = Peer.gest_files.aggiungi(file_access) ) == 0 ) 
						{
						// se non è un seeder
						System.out.println("@ File access creato !");
						vita_sociale(file_access,lista_peers);
						System.out.println("@ Vita sociale superato !");
						}
			}	
		}
		/*
		if(Peer.prova==1) //TODO togliere poi questa
		{
			FileAccess file_access=null;
			try {
				file_access = new FileAccess(new Rapp_file("babbo1", Peer.hash_babbo,false));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				System.out.println("# Problemi con la creazione del file");
				e1.printStackTrace();
			}
			
			Peer.gest_files.aggiungi(file_access);
			
			System.out.println("@ File access creato !");
			
			InetAddress mio=null; 
			this.lista_peers=new ArrayList<InetAddress>();
			try {
				mio=InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(mio!=null)
				this.lista_peers.add(mio);
			else 
				System.out.println("# prendere il mio Indirizzo è andato male");
			
			vita_sociale(file_access,this.lista_peers);
			
			System.out.println("@ Vita sociale superato !");
			
			
		}*/
				
	}
	
	
}
