import  java.net.*;
import  java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket p1 = new Socket("localhost",66699);
        PrintWriter toSend=new PrintWriter(p1.getOutputStream());
        System.out.println("elo tu ja klient");
        toSend.println("elo tu ja klient");
        toSend.flush();
        InputStreamReader inPutStreamServer= new InputStreamReader(p1.getInputStream());
        BufferedReader inPutServer=new BufferedReader(inPutStreamServer);
        String a=inPutServer.readLine();
        System.out.println(a);

    }
}