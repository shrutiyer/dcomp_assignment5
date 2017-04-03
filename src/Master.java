import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class Master implements iMaster{

    private List<String> IPList;
    private String filePath;

    public Master(String path, List<String> IPs) {
        filePath = path;
        IPList = IPs;
    }

    @Override
    public iReducer[] getReducers(String[] keys) throws RemoteException, AlreadyBoundException {
        return new iReducer[0];
    }

    @Override
    public void markMapperDone() throws RemoteException {

    }

    @Override
    public void receiveOutput(String key, int value) throws RemoteException {

    }
    
}
