import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Reducer extends UnicastRemoteObject implements iReducer{
    private String ip;
    private iMaster master;
    private String key;
    private int wordCount;

    public Reducer(String ip) throws RemoteException {
        Registry reg = LocateRegistry.getRegistry(ip);
        this.ip = ip;
        reg.rebind("reduce_manager", this);
        System.out.println("Reduce manager created.");
    }

    public Reducer(String key, String ip, iMaster master) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.getRegistry(ip);
        this.ip = ip;
        this.key = key;
        reg.bind(key, this);
        this.master = master;
        System.out.println("Reduce task created.");
    }
    @Override
    public iReducer createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        return new Reducer(key, ip, master);
    }

    @Override
    public void receiveValues(int value) throws RemoteException {
        // called by the mapper task, receives a word count.
        System.out.println("\"" + this.key + "\" reducer value received: " + value);
        wordCount = wordCount + value;
    }

    @Override
    public int terminate() throws IOException {
        // tell the reducer to stop reducing
        master.receiveOutput(key, wordCount);
        return 0;
    }
}
