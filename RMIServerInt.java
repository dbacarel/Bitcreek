import java.net.InetAddress;
import java.rmi.*;

public interface RMIServerInt extends Remote {
	
	/**
	 * Richiede la pubblicazione di un descrittore al server
	 * @param descriptor
	 * @return
	 * @throws RemoteException
	 */
	public Integer[] publish(Descrittore descriptor,InetAddress ipPeer) throws RemoteException;
	
	/**
	 * Richiede la ricerca di un dato file
	 * @param thethingheislookingfor
	 * @return
	 * @throws RemoteException
	 */
	public Descrittore lookup(String thethingheislookingfor, InetAddress i) throws RemoteException;

}
