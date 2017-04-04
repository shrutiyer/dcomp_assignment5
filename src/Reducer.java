import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Reducer implements iReducer{
    @Override
    public iReducer createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        return null;
    }

    @Override
    public void receiveValues(int value) throws RemoteException {
        // called by the mapper task, receives a word count.
    }

    @Override
    public int terminate() throws RemoteException {
        // tell the reducer to start reducing?
        return 0;
    }
}
