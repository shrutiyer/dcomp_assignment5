import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class Worker {

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
        iMapper mapManager = new Mapper(localReg);
        iReducer reduceManager = new Reducer(localReg);
    }
}
