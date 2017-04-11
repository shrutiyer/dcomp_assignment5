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
    private Map<String, iReducer> reducers;
    private Map<String, Integer> wordCountMap;
    private boolean processWordFile;

    int reducerIndex; // We use this variable to assign Reducer tasks to Workers evenly. In getReducers, whenever a new
    // Reducer task is created, it increments, so the next Reducer is placed on a different IP
    int mapTaskIndex; // It increases whenever there is a map task assigned and reduces when the map task is done.

    public Master(String myIp, String path, List<String> IPs) throws RemoteException {
        filePath = path;
        IPList = IPs;
        reducers = new HashMap<>();
        wordCountMap = new HashMap<>();
        Registry reg = LocateRegistry.getRegistry(myIp);
        reg.rebind("master", this);
    }

    @Override
    public iReducer[] getReducers(String[] keys) throws RemoteException, AlreadyBoundException, NotBoundException,
            InterruptedException {
        // iMapper object calls this function, sends its list of keys.
        // Returns array of corresponding reducers to the mapper
        // If a key is received without a corresponding reducer, then create the reducer using createReduceTask
        iReducer[] reducers = new iReducer[keys.length];
        int i = 0;
        System.out.println("Request for reducers: " + Arrays.toString(keys));
        for (String k : keys) {
            // if there is no reducer associated with a key, create a new reducer
            if (!this.reducers.containsKey(k)) {
                mutex.acquire();
                if (reducerIndex == IPList.size())
                    reducerIndex = 0;
                Registry reg = LocateRegistry.getRegistry(IPList.get(reducerIndex));
                iReducer factory = (iReducer) reg.lookup("reduce_manager");
                this.reducers.put(k, factory.createReduceTask(k, this));
                reducerIndex++;
                mutex.release();
            }
            // else, send the reducer we've already created.
            reducers[i] = this.reducers.get(k);
            i++;
        }
        return reducers;
    }

    @Override
    public void markMapperDone() throws IOException {
        // keeps track of how many mappers need to be still processed
        this.mapTaskIndex--;
        System.out.println("A mapper task just finished! Tasks left to execute: " + mapTaskIndex + " still processing word file: " + processWordFile);
        if (this.mapTaskIndex <= 0 && !processWordFile) {
            System.out.println("No mapper tasks left to execute. THIS SHOULD ONLY HAPPEN ONCE");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String ip : IPList) {
                            System.out.println("Terminating reduce tasks for IP: " + ip);
                            ((iReducer) LocateRegistry.getRegistry(ip).lookup("reduce_manager")).terminateReducingTasks();
                        }
                        writeWordCountToFile();
                    } catch (IOException | NotBoundException e) {
                        System.out.println("An error occurred when terminating reducing tasks.");
                        e.printStackTrace();
                    }
                }
            }, 5000);
        }
    }

    @Override
    public void receiveOutput(String key, int value) throws IOException {
        // reducers call this function when they are done counting
        this.wordCountMap.put(key, value);
    }

    private void writeWordCountToFile() throws IOException {
        System.out.println("WRITING WORD COUNTS TO FILE YOLO");
        FileWriter fileStream = new FileWriter("values.txt");
        BufferedWriter out = new BufferedWriter(fileStream);
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            out.write(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    public void startWordCount() throws IOException, NotBoundException, AlreadyBoundException, InterruptedException {
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
            iMapper factory = (iMapper) reg.lookup("map_manager");
            mapTaskIndex++;
            String nextLine = reader.readLine();
            processWordFile = nextLine != null;
            factory.createMapTask("map_task_" + j).processInput(line, this); // TODO: this line is blocking
            j++;
            i++;
            line = nextLine;
        }
        processWordFile = false;
        for (String ip : IPList)
            LocateRegistry.getRegistry(ip).unbind("map_manager");

        System.out.println("Finished sending lines to Mappers.");
    }

}
