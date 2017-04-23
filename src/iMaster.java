import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iMaster extends Remote {

    public iReduceTask[] getReducers(String [] keys) throws RemoteException, AlreadyBoundException, NotBoundException,
            InterruptedException;
    public void markReducerDone(String key) throws IOException, InterruptedException;
    public void receiveOutput(String key, int value) throws IOException;

}
