import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapTask extends UnicastRemoteObject implements iMapTask {

    public Map<String, Integer> counts;
    private String name;
    private Registry reg;

    public MapTask(Registry r) throws RemoteException {
        reg = r;
        reg.rebind("map_manager", (iMapTask) this);
        System.out.println("Map manager created.");
    }

    public MapTask(String name) throws RemoteException, AlreadyBoundException {
        counts = new HashMap<>();
        this.name = name;
    }

    @Override
    public iMapTask createMapTask(String name) throws RemoteException, AlreadyBoundException {
        // As far as we know, the name isn't actually necessary. We're just using it to differentiate b/w the MapTask
        // manager (which has no name) and an actual task (which has a name)
        System.out.println("Creating map task for name: " + name);
        iMapTask m = new MapTask(name);
        return m;
    }

    @Override
    public void processInput(String input, iMaster theMaster) throws IOException, AlreadyBoundException, NotBoundException, InterruptedException {
        // Count the words in String input
        // Create list of keys (words in the string)
        // call getReducers() on theMaster
        // send locally stored word count for each word to corresponding reducer task
//        System.out.println("Reducing: " + input);
//        String[] words = input.split("\\W+");
//        for (String w : words)
//            counts.put(w, counts.getOrDefault(w, 0) + 1);
//        String[] keys = counts.keySet().toArray(new String[counts.keySet().size()]);
//        iReduceTask[] reducers = theMaster.getReducers(keys);
//        for (int i = 0; i < keys.length; i++)
//            reducers[i].receiveValues(counts.get(keys[i]));
//        LocateRegistry.getRegistry(ip).unbind(name);
//        theMaster.markMapperDone();
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String[] words = input.split("\\W+");
                    for (String w : words)
                        counts.put(w, counts.getOrDefault(w, 0) + 1);
                    String[] keys = counts.keySet().toArray(new String[counts.keySet().size()]);
                    iReduceTask[] reducers = theMaster.getReducers(keys);
                    for (int i = 0; i < keys.length; i++)
                        reducers[i].receiveValues(counts.get(keys[i]));
                    theMaster.markMapperDone();
                } catch (IOException | AlreadyBoundException | NotBoundException | InterruptedException e) {
                    System.out.println("An error occurred while processing input.");
                    e.printStackTrace();
                }
            }
        }, 0);
    }
}