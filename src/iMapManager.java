import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface iMapManager extends Remote {
    iMapTask createMapTask(String name) throws RemoteException, AlreadyBoundException;
}
