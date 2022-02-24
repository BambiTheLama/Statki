import  java.net.*;
import  java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(66);
        Socket p1 = server.accept();

        InputStreamReader inPutStreamP1= new InputStreamReader(p1.getInputStream());
        BufferedReader inPutP1=new BufferedReader(inPutStreamP1);

        String a=inPutP1.readLine();
        System.out.println(a);
        PrintWriter toSend=new PrintWriter(p1.getOutputStream());
        toSend.println("elo klijent tu server ");
        toSend.flush();



    }
}
