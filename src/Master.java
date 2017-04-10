import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class Master extends UnicastRemoteObject implements iMaster {

    private List<String> IPList;
    private String filePath;
    private Map<String, iReducer> reducers;
    int reducerIndex; // We use this variable to assign Reducer tasks to Workers evenly. In getReducers, whenever a new
                      // Reducer task is created, it increments, so the next Reducer is placed on a different IP

    public Master(String myIp, String path, List<String> IPs) throws RemoteException {
        filePath = path;
        IPList = IPs;
        reducers = new HashMap<>();
        Registry reg = LocateRegistry.getRegistry(myIp);
        reg.rebind("master", this);
    }

    @Override
    public iReducer[] getReducers(String[] keys) throws RemoteException, AlreadyBoundException, NotBoundException {
        // iMapper object calls this function, sends its list of keys.
        // Returns array of corresponding reducers to the mapper
        // If a key is received without a corresponding reducer, then create the reducer using createReduceTask
        iReducer[] reducers = new iReducer[keys.length];
        int i = 0;
        System.out.println("Request for reducers: " + Arrays.toString(keys));
        for (String k : keys) {
            // if there is no reducer associated with a key, create a new reducer
            if (!this.reducers.containsKey(k)) {
                if (reducerIndex == IPList.size())
                    reducerIndex = 0;
                Registry reg = LocateRegistry.getRegistry(IPList.get(reducerIndex));
                iReducer factory = (iReducer) reg.lookup("reduce_manager");
                this.reducers.put(k, factory.createReduceTask(k, this));
                reducerIndex++;
            }
            // else, send the reducer we've already created.
            reducers[i] = this.reducers.get(k);
            i++;
        }
        return reducers;
    }

    // @Override
    // public void markMapperDone() throws RemoteException {
    //     // wtf does this do?
    // }

    @Override
    public void receiveOutput(String key, int value) throws RemoteException {
        // reducers call this function when they are done counting
    }

    public void startWordCount() throws IOException, NotBoundException, AlreadyBoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int i = 0;
        int j = 0;
        while ((line = reader.readLine()) != null) {
            if (i == IPList.size())
                i = 0;
            Registry reg = LocateRegistry.getRegistry(IPList.get(i));
            iMapper factory = (iMapper) reg.lookup("map_manager");
            factory.createMapTask("map_task_" + j).processInput(line, this);
            j++; i++;
        }
        System.out.println("Finished sending lines to Mappers.");
    }

}
