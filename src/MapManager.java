import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MapManager extends UnicastRemoteObject implements iMapManager {

    private Registry reg;

    public MapManager(Registry r) throws RemoteException {
        reg = r;
        reg.rebind("map_manager", (iMapManager) this);
        System.out.println("Map manager created.");
    }

    @Override
    public iMapTask createMapTask(String name) throws RemoteException, AlreadyBoundException {
        // As far as we know, the name isn't actually necessary. We're just using it to differentiate b/w the MapTask
        // manager (which has no name) and an actual task (which has a name)
        System.out.println("Creating map task for name: " + name);
        return new MapTask(name);
    }
}