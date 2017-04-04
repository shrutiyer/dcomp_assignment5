import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        // iMapper object calls this function, sends its list of keys.
        // Returns array of corresponding reducers to the mapper
        // If a key is received without a corresponding reducer, then create the reducer using createReduceTask
        return new iReducer[0];
    }

    @Override
    public void markMapperDone() throws RemoteException {
        // wtf does this do?
    }

    @Override
    public void receiveOutput(String key, int value) throws RemoteException {
        // reducers call this function when they are done counting
    }

    public void startWordCount() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {

        }
    }

}
