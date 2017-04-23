import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ReduceManager extends UnicastRemoteObject implements iReduceManager {
    private Registry reg;


    public ReduceManager(Registry r) throws RemoteException {
        reg = r;
        reg.rebind("reduce_manager", this);
        System.out.println("Reduce manager created.");
    }

    @Override
    public iReduceTask createReduceTask(String key, iMaster master, int count) throws RemoteException, AlreadyBoundException {
        ReduceTask r = new ReduceTask(key, master, count);
        return r;
    }
}
