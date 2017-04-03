import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Reducer implements iReducer{
    @Override
    public iReducer createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        return null;
    }

    @Override
    public void receiveValues(int value) throws RemoteException {

    }

    @Override
    public int terminate() throws RemoteException {
        return 0;
    }
}
