import java.net.*;
import java.util.Enumeration;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class test {
    public void main(String[] args) throws UnknownHostException, SocketException, InterruptedException {

        InetAddress IP=InetAddress.getLocalHost();
        System.out.println(IP.toString());
    }

}
