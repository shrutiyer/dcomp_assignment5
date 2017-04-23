import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iReduceTask {
    void receiveValues(int value) throws RemoteException;
    int terminate() throws IOException;

}
