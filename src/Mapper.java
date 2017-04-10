import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Mapper extends UnicastRemoteObject implements iMapper {

    public Map<String, Integer> counts;
    private String ip, name;

    public Mapper(String ip) throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(ip);
        this.ip = ip;
        reg.rebind("map_manager", (iMapper) this);
        System.out.println("Map manager created.");
    }

    public Mapper(String name, String ip) throws RemoteException, AlreadyBoundException {
        System.out.println("Map task created with name: " + name);
        Registry reg = LocateRegistry.getRegistry(ip);
        counts = new HashMap<>();
        this.ip = ip;
        this.name = name;
        reg.bind(name, this);
    }

    @Override
    public iMapper createMapTask(String name) throws RemoteException, AlreadyBoundException {
        // As far as we know, the name isn't actually necessary. We're just using it to differentiate b/w the Mapper
        // manager (which has no name) and an actual task (which has a name)
        return new Mapper(name, ip);
    }

    @Override
    public void processInput(String input, iMaster theMaster) throws RemoteException, AlreadyBoundException, NotBoundException {
        // Count the words in String input
        // Create list of keys (words in the string)
        // call getReducers() on theMaster
        // send locally stored word count for each word to corresponding reducer task
        System.out.println("Reducing: " + input);
        String[] words = input.split("\\W+");
        for (String w : words)
            counts.put(w, counts.getOrDefault(w, 0)+1);
        String[] keys = counts.keySet().toArray(new String[counts.keySet().size()]);
        iReducer[] reducers = theMaster.getReducers(keys);
        for (int i = 0; i < keys.length; i++)
            reducers[i].receiveValues(counts.get(keys[i]));
        LocateRegistry.getRegistry(this.ip).unbind(this.name);
        theMaster.markMapperDone();
    }
}