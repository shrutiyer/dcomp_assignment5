import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Reducer extends UnicastRemoteObject implements iReducer{
    private String ip;
    private iMaster master;
    private String key;
    private int wordCount;
    private List<String> reducerTasks; // List of tasks on a specific machine. This variable is only used by the manager

    public Reducer(String ip) throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(ip);
        this.ip = ip;
        this.reducerTasks = new ArrayList<>();
        reg.rebind("reduce_manager", this);
        System.out.println("Reduce manager created.");
    }

    public Reducer(String key, String ip, iMaster master) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.getRegistry(ip);
        this.ip = ip;
        this.key = key;
        reg.bind(key, this);
        this.master = master;
    }

    @Override
    public void terminateReducingTasks() throws IOException, NotBoundException {
        for (String s : reducerTasks) {
            Registry reg = LocateRegistry.getRegistry(this.ip);
            iReducer r = (iReducer) reg.lookup(s);
            r.terminate();
            reg.unbind(s);
        }
        LocateRegistry.getRegistry(this.ip).unbind("reduce_manager");
    }

    @Override
    public iReducer createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        reducerTasks.add(key);
        return new Reducer(key, ip, master);
    }

    @Override
    public void receiveValues(int value) throws RemoteException {
        // called by the mapper task, receives a word count.
        System.out.println("Reducer with key " + key + "received value " + value);
        wordCount = wordCount + value;
    }

    @Override
    public int terminate() throws IOException {
        // tell the reducer to stop reducing
        master.receiveOutput(key, wordCount);
        return 0;
    }
}
