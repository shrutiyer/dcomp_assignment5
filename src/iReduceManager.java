import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iReduceManager extends Remote {
    iReduceTask createReduceTask(String key, iMaster master, int count) throws RemoteException, AlreadyBoundException;
}
