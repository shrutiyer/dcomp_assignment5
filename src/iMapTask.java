import java.io.IOException;
import java.rmi.*;
import java.rmi.RemoteException;

public interface iMapTask {
    void processInput(String input, iMaster theMaster) throws IOException, AlreadyBoundException,
            NotBoundException, InterruptedException;
}
