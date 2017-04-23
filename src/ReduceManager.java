import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ReduceManager extends UnicastRemoteObject implements iReduceManager {
    private List<iReduceTask> reducerTasks; // List of tasks on a specific machine. This variable is only used by the manager
    private Registry reg;


    public ReduceManager(Registry r) throws RemoteException {
        reg = r;
        this.reducerTasks = new ArrayList<>();
        reg.rebind("reduce_manager", this);
        System.out.println("Reduce manager created.");
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
}
