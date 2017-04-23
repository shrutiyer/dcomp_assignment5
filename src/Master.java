import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Semaphore;

public class Master extends UnicastRemoteObject implements iMaster {

    static Semaphore mutex = new Semaphore(1);

    private List<String> IPList;
    private String filePath;
    private Map<String, iReduceTask> reducers;
    private Map<String, Integer> wordCountMap;
    private boolean processWordFile;

    private int reducerIndex; // We use this variable to assign ReduceTask tasks to Workers evenly. In getReducers, whenever a new
    // ReduceTask task is created, it increments, so the next ReduceTask is placed on a different IP
    private int mapTaskIndex; // It increases whenever there is a map task assigned and reduces when the map task is done.

    public Master(String myIp, String path, List<String> IPs, Registry r) throws RemoteException {
        filePath = path;
        IPList = IPs;
        reducers = new HashMap<>();
        wordCountMap = new HashMap<>();
        r.rebind("master", this);
    }

    @Override
    public iReduceTask[] getReducers(String[] keys) throws RemoteException, AlreadyBoundException, NotBoundException,
            InterruptedException {
        // iMapTask object calls this function, sends its list of keys.
        // Returns array of corresponding reducers to the mapper
        // If a key is received without a corresponding reducer, then create the reducer using createReduceTask
        iReduceTask[] reducers = new iReduceTask[keys.length];
        int i = 0;
        for (String k : keys) {
            // if there is no reducer associated with a key, create a new reducer
            mutex.acquire();
            if (!this.reducers.containsKey(k)) {
                if (reducerIndex == IPList.size())
                    reducerIndex = 0;
                Registry reg = LocateRegistry.getRegistry(IPList.get(reducerIndex));
                iReduceManager factory = (iReduceManager) reg.lookup("reduce_manager");
                this.reducers.put(k, factory.createReduceTask(k, this));
                reducerIndex++;
            }
            mutex.release();
            // else, send the reducer we've already created.
            reducers[i] = this.reducers.get(k);
            i++;
        }
        return reducers;
    }

    @Override
    public void markMapperDone() throws IOException, InterruptedException {
        // keeps track of how many mappers need to be still processed
        mutex.acquire();
        this.mapTaskIndex--;
        mutex.release();
        if (this.mapTaskIndex <= 0 && !processWordFile) {
            System.out.println("All mapper tasks have completed. Waiting 3 seconds.");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String ip : IPList) {
                            System.out.println("Terminating reduce tasks for IP: " + ip);
                            ((iReduceManager) LocateRegistry.getRegistry(ip).lookup("reduce_manager")).terminateReducingTasks();
                        }
                        writeWordCountToFile();
                    } catch (IOException | NotBoundException e) {
                        System.out.println("An error occurred when terminating reducing tasks.");
                        e.printStackTrace();
                    }
                }
            }, 3000);
        }
    }

    @Override
    public void receiveOutput(String key, int value) throws IOException {
        // reducers call this function when they are done counting
        System.out.println("Master received value " + value + " for key: " + key);
        this.wordCountMap.put(key, value);
    }

    private void writeWordCountToFile() throws IOException {
        System.out.println("Writing to File...");
        PrintWriter writer = new PrintWriter("counts.txt", "UTF-8");
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            String s = entry.getKey() + ": " + entry.getValue();
            writer.println(s);
        }
        writer.close();
        System.out.println("File write successful.");
        System.exit(0);
    }

    public void startWordCount() throws IOException, NotBoundException, AlreadyBoundException, InterruptedException {
        System.out.println("Starting to read file and create MapTask tasks for each line.");
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int i = 0;
        int j = 0;
        processWordFile = true;
        line = reader.readLine();
        while (line != null) {
            if (i == IPList.size())
                i = 0;
            Registry reg = LocateRegistry.getRegistry(IPList.get(i));
            iMapManager factory = (iMapManager) reg.lookup("map_manager");
            mutex.acquire();
            mapTaskIndex++;
            mutex.release();
            String nextLine = reader.readLine();
            processWordFile = nextLine != null;
            factory.createMapTask("map_task_" + j).processInput(line, this);
            j++;
            i++;
            line = nextLine;
        }
        processWordFile = false;
        System.out.println("Finished sending lines to Mappers.");

    }

}
