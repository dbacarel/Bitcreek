import java.io.*;
import java.util.ArrayList;
public class Prova {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * RandomAccessFile nuovo=null;
		try {
			nuovo= new RandomAccessFile("ciao", "rwd");
			//nuovo.setLength(1000);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File non trovato");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] roba= "fawjnvfALÒKWSJBGF".getBytes();
		if(nuovo != null)
			try {
				nuovo.seek(30);
				nuovo.write(roba);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
		ArrayList<FileAccess> temp=FileSysMan.leggi_parziali("./temp");
		ArrayList<FileAccess> fin=FileSysMan.leggi_parziali("./scaricati");
		FileSysMan.Scrivi_torrents(fin);
		
		
	}

}
