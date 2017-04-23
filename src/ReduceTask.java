import java.io.IOException;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ReduceTask implements iReduceTask, Serializable {
    private iMaster master;
    private String key;
    private int wordCount;

    public ReduceTask(String key, iMaster master) throws RemoteException, AlreadyBoundException {
        this.key = key;
        this.master = master;
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
