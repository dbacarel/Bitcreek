

import java.util.*;
import java.io.IOException;
import java.net.*;


public class PollingManager implements Runnable{

	Hashtable<String,Integer> tabella;
	
	InetAddress server;
	
	DatagramSocket socket_invio;
	
	DatagramPacket ping;
	

	public PollingManager(InetAddress server){
		this.server=server;
		this.tabella= new Hashtable<String,Integer>();
		
		try {
			this.socket_invio =	new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("@ Polling_manager Creato !");
		
			
		
	}
	
	public void aggiungi(String nome,Integer nuovo_ind){
		
		synchronized(this){ //TODO vedere se vale la pena di bloccare tutto
			this.tabella.put(nome, nuovo_ind);
			}
		
		return;
	}
	
	public void rimuovi(String nome){
		synchronized(this){
		this.tabella.remove(nome);
		}
		return;
	}

	
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("@ POLL @ Parto ");
		
		while(true)
			{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Enumeration<String> istanze= tabella.keys();
			while(istanze.hasMoreElements())
				{
				String nome_descr=istanze.nextElement();
				int porta=tabella.get(nome_descr);
				
				byte[] mess= nome_descr.getBytes(); 
						
				
				System.out.println("La lunghezza dell'array"+mess.length);
				ping=new DatagramPacket(mess,mess.length,this.server,porta );
				ping.setLength(mess.length);
				
				try {
					socket_invio.send(ping);
					System.out.println("@ POLL @ ho fatto il ping alla porta "+ porta + " di "+ this.server.getHostAddress());
					} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
					
				}
			}
		
		
	}
	
	
	
	
	
}
