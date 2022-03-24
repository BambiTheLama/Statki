import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Communication extends Thread{
    String who;
    ServerSocket serverSocket;
    BufferedReader Input = null;
    PrintWriter toSend = null;
    Socket server;
    Socket player;
    InputStreamReader InputStream;
    boolean connect=false;
    int port;
    boolean end=false;
    boolean work=false;

    boolean tryConnect(String who,int port,String ip) {
        this.who=who;
        if(Objects.equals(who, "server"))
        {
            if(work && !connect && !end)
                return false;
            try{
                System.out.println("server1");
                this.port=port;
                if(connect)
                {
                    System.out.println("server2a");
                    InputStream=new InputStreamReader(player.getInputStream());
                    Input=new BufferedReader(InputStream);
                    toSend=new PrintWriter(player.getOutputStream());
                    work=false;
                    return true;
                }


                System.out.println("server2");

                System.out.println("server3");
                if(!work)
                {
                    System.out.println("server4");
                    start();
                    work=true;
                    return false;
                }
            }
            catch (Exception e)
            {
                return false;
            }


        }

        else if(Objects.equals(who, "client"))
        {
            try{
                if(Objects.equals(ip, ""))
                    server=new Socket("localhost",port);
                else
                    server=new Socket(ip,port);
                toSend=new PrintWriter(server.getOutputStream());
                InputStream=new InputStreamReader(server.getInputStream());
                Input=new BufferedReader(InputStream);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }


        }

        return false;
    }

    void End()
    {
        end=true;
    }

    @Override
    public void run()
    {
        try {
            serverSocket=new ServerSocket(port);
            System.out.println("Server stawiam");
            player =serverSocket.accept();
            System.out.println("Server dolaczam");
            connect=true;

        } catch (IOException e) {
            work=false;
            end=true;

            }




    }
    boolean isClose(boolean isProgramEnd) {
        boolean tmp=true;
        try{
            if(Objects.equals(who, "server"))
                tmp=isClientClose(isProgramEnd);
            else if(Objects.equals(who, "client"))
                tmp=isServerClose(isProgramEnd);
        }
        catch (Exception e)
        {
            return true;
        }

        return tmp;
    }

    boolean isMyMove(boolean isMyMove) {
        if(Objects.equals(who, "server"))
        {
            String enemymove=getInformation();
            if(enemymove.equals(isMyMove+""))
            {
                sendInformation("zmien");
            }
            else
            {
                sendInformation("zostaw");
            }


        }
        else if(Objects.equals(who, "client"))
        {
            sendInformation(isMyMove+"");
            String tmp=getInformation();
            if(tmp.equals("zmien"))
            {
                return (!isMyMove);
            }

        }
        return isMyMove;

    }

    boolean isServerClose(boolean isProgramEnd) throws IOException {

        boolean isOpponentLeft=Boolean.parseBoolean(Input.readLine());
        if(!isOpponentLeft)
        {
            sendInformation(isProgramEnd+"");
        }
        return isOpponentLeft;

    }

    boolean isClientClose(boolean isProgramEnd) throws IOException {

        toSend.println(isProgramEnd);
        toSend.flush();

        if(!isProgramEnd)
        {
            return Boolean.parseBoolean(Input.readLine());
        }
        return false;
    }

    boolean isOpponentAttack(Boolean myAttack) {
        boolean isOpponentAttack=false;
        try{
            if(Objects.equals(who, "server"))
            {
                toSend.println(myAttack);
                toSend.flush();
                if(!myAttack)
                {
                    isOpponentAttack=Boolean.parseBoolean(Input.readLine());
                }
            }
            else if(Objects.equals(who, "client"))
            {
                isOpponentAttack=Boolean.parseBoolean(Input.readLine());
                if(!isOpponentAttack)
                {
                    toSend.println(myAttack);
                    toSend.flush();
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return isOpponentAttack;
    }

    String getInformation() {
        try{
            return Input.readLine();
        }
        catch (Exception e)
        {

        }
        return " \n";
    }

    void sendInformation(String massage){
        toSend.println(massage);
        toSend.flush();
    }

}
