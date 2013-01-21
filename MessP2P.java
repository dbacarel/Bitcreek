import java.io.*;
import java.util.*;

public class MessP2P implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int codice; // codice messaggio, definito dal protocollo
	ArrayList<String> lista_arr; 
	byte[] data_arr;
	boolean[] lista_presenze;
	String data_str;
	int intero;
	Pezzo p;
	
	public MessP2P(int cod, ArrayList<String> l) {
		
		this.codice=cod;
		this.lista_arr=l;
		
	}
	
	public MessP2P(int cod, String data) {
		
		this.codice=cod;
		this.data_str=data;
		
	}
	
	public MessP2P(int cod, String data, int i) {
		
		this.codice=cod;
		this.data_str=data;
		this.intero=i;
		
	}
	
	public MessP2P(int cod, int porta) {
		
		this.codice=cod;
		this.intero=porta;
		
	}
	
	public MessP2P(int cod, byte[] data) {
		
		this.codice=cod;
		this.data_arr=data;
		
	}
	
	public MessP2P(int cod, byte[] data, int offset) {
		
		this.codice=cod;
		this.data_arr=data;
		this.intero=offset;
	}
	
	
	public MessP2P(int cod, boolean[] data) {
		
		this.codice=cod;
		this.lista_presenze=data;
		
	}
	
	public MessP2P(int cod, Pezzo pezzo) {
		
		this.codice=cod;
		this.p=pezzo;
		
	}
	
	

}
