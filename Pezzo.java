

import java.io.*;

public class Pezzo implements Serializable {

	/**TODO questo metodo di creazione dle codice è puramente di default
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String hash;
	protected int offset;
	
	public Pezzo(byte [] d,String h, int o ){
		
		this.hash=h;
		this.offset=o;
	}
		
}
