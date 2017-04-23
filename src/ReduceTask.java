import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class ReduceTask extends UnicastRemoteObject implements iReduceTask {
    private iMaster master;
    private String key;
    private int wordCount;
    private Timer timer;
    private TimerTask timerTask;

    public ReduceTask(String key, iMaster master, int count) throws RemoteException, AlreadyBoundException {
        this.key = key;
        this.master = master;
        this.timer = new Timer();
        this.wordCount = count;
        createTimerTask();
    }

    @Override
    public void receiveValues(int value) throws RemoteException {
        // called by the mapper task, receives a word count.
        timerTask.cancel();
        createTimerTask();
        timer.schedule(this.timerTask, 5000);
        wordCount = wordCount + value;
    }

    private void createTimerTask () {
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    master.receiveOutput(key, wordCount);
                    master.markReducerDone(key);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
