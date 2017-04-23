import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iReduceTask extends Remote {
    void receiveValues(int value) throws RemoteException;
}
