import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ReduceTask extends UnicastRemoteObject implements iReduceTask {
    private iMaster master;
    private String key;
    private int wordCount;
    private Registry reg;
    private List<iReduceTask> reducerTasks; // List of tasks on a specific machine. This variable is only used by the manager

    public ReduceTask(Registry r) throws RemoteException {
        reg = r;
        this.reducerTasks = new ArrayList<>();
        reg.rebind("reduce_manager", this);
        System.out.println("Reduce manager created.");
    }

    public ReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        this.key = key;
        this.master = master;
    }

    @Override
    public void terminateReducingTasks() throws IOException, NotBoundException {
        System.out.println("Terminating all managed reduced tasks");
        for (iReduceTask r : reducerTasks) {
            r.terminate();
        }
        reducerTasks.clear();
    }

    @Override
    public iReduceTask createReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        ReduceTask r = new ReduceTask(key, master);
        reducerTasks.add(r);
        return r;
    }

    @Override
    public void receiveValues(int value) throws RemoteException {
        // called by the mapper task, receives a word count.
        System.out.println("ReduceTask with key " + key + " received value " + value);
        wordCount = wordCount + value;
    }

    @Override
    public int terminate() throws IOException {
        // tell the reducer to stop reducing
        master.receiveOutput(key, wordCount);
        return 0;
    }
}
