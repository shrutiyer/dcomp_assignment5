import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class MasterRunner {

    private static Master master;

    public static void main(String[] argv) throws IOException {
        Scanner sc = new Scanner(System.in);
        File f = null;
        while (f == null || !f.exists()) {
            System.out.print("Enter a book filename --> ");
            String book = sc.next();
            f = new File(Paths.get("books", book).toString());
        }
        master = new Master(f.getCanonicalPath(), Arrays.asList(argv));
    }

}
