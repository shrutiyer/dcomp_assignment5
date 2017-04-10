import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.RemoteException;

import java.util.*;

public interface iMaster extends Remote {

    public iReducer[] getReducers(String [] keys) throws RemoteException, AlreadyBoundException, NotBoundException;
    public void markMapperDone() throws IOException;
    public void receiveOutput(String key, int value) throws IOException;

}
