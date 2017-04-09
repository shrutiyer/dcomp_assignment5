import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class Worker {

    public static void main(String[] argv) throws RemoteException {
        String localIP = argv[0];
        System.setProperty("java.rmi.server.hostname", localIP);
        try {
            LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            System.out.println("Local RMI registry already is running. Using existing registry.");
        }
        iMapper mapManager = new Mapper(localIP);
        iReducer reduceManager = new Reducer(localIP);
    }
}
