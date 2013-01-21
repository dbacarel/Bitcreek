
import java.io.Serializable;
import java.util.ArrayList;

public class Descrittore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String nome;
	protected ArrayList<String> hash_array;
	protected byte[] hash_totale;
	protected int num_pezzi;
	protected long size;
	protected int[] porte;
	
	
	public Descrittore (String nome,ArrayList<String> hash_array, int num_pezzi){
		
		this.nome=nome;
		this.hash_array=hash_array;
		this.num_pezzi=num_pezzi;
		this.porte=new int[2];
		
		
	}
	
	public int getUdp(){
		return this.porte[0];
	}
	
	public void setUdp(int port){
		this.porte[0]=port;
	}
	
	public void setTcp(int port){
		this.porte[1]=port;
	}
	
	public int getTcp(){
		return this.porte[1];
	}
	
	public String get_name(){
		return this.nome;
	}
	
	public ArrayList<String> get_hashArray(){
		return this.hash_array;
	}
	
	public int get_nPezzi(){
		return num_pezzi;
	}
}
