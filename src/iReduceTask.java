import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iReduceTask extends Remote {

    iReduceTask createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException;
    void receiveValues(int value) throws RemoteException;
    int terminate() throws IOException;
    void terminateReducingTasks() throws IOException, NotBoundException;

}
