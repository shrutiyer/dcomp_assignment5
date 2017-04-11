import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.RemoteException;

import java.util.*;

public interface iMapper extends Remote {
    iMapper createMapTask(String name) throws RemoteException, AlreadyBoundException;
    void processInput(String input, iMaster theMaster) throws IOException, AlreadyBoundException,
            NotBoundException, InterruptedException;
}
