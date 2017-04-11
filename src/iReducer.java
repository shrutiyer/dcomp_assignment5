import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.RemoteException;

import java.util.*;

public interface iReducer extends Remote {

    iReducer createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException;
    void receiveValues(int value) throws RemoteException;
    int terminate() throws IOException;
    void terminateReducingTasks() throws IOException, NotBoundException;

}
