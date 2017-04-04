import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Map;

public class Mapper implements iMapper {

    public Map<String, Integer> counts;

    public Mapper() {}

    public Mapper(String name) {
        counts = new HashMap<>();
    }

    @Override
    public iMapper createMapTask(String name) throws RemoteException, AlreadyBoundException {
        // As far as we know, the name isn't actually necessary. We're just using it to differentiate b/w the Mapper
        // manager (which has no name) and an actual task (which has a name)
        return new Mapper(name);
    }

    @Override
    public void processInput(String input, iMaster theMaster) throws RemoteException, AlreadyBoundException {
        // Count the words in String input
        // Create list of keys (words in the string)
        // call getReducers() on theMaster
        // send locally stored word count for each word to corresponding reducer task

        String[] words = input.split("\\W+");
        for (String w : words)
            counts.put(w, counts.getOrDefault(w, 0)+1);
        String[] keys = counts.keySet().toArray(new String[counts.keySet().size()]);
        iReducer[] reducers = theMaster.getReducers(keys);
        for (int i = 0; i < keys.length; i++)
            reducers[i].receiveValues(counts.get(keys[i]));
    }

    public static void main(String[] argv) throws RemoteException {
        String localIP = argv[0];
        System.setProperty("java.rmi.server.hostname", localIP);
        Registry localReg;
        try {
            localReg = LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            System.out.println("Local RMI registry already is running. Using existing registry.");
            localReg = LocateRegistry.getRegistry(localIP, 1099);
        }
        iMapper manager = new Mapper();
        localReg.rebind("map_manager", manager);
    }
}