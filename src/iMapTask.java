import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iMapTask extends Remote {
    iMapTask createMapTask(String name) throws RemoteException, AlreadyBoundException;
    void processInput(String input, iMaster theMaster) throws IOException, AlreadyBoundException,
            NotBoundException, InterruptedException;
}
