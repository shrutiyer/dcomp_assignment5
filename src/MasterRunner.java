import java.io.*;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.Scanner;

public class MasterRunner {

    public static void main(String[] argv) throws IOException, AlreadyBoundException, NotBoundException,
            InterruptedException {
        String localIP = argv[0];
        System.setProperty("java.rmi.server.hostname", localIP);
        Registry localReg;
        try {
            localReg = LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            System.out.println("Local RMI registry already is running. Using existing registry.");
            localReg = LocateRegistry.getRegistry(localIP, 1099);
        }
        Scanner sc = new Scanner(System.in);
        File f = null;
        while (f == null || !f.exists()) {
            System.out.print("Enter a book filename --> ");
            String book = sc.next();
            f = new File(Paths.get("books", book).toString());
        }
        Master master = new Master(argv[0], f.getCanonicalPath(), Arrays.asList(argv).subList(1, argv.length),
                localReg);
        master.startWordCount();
    }

}
