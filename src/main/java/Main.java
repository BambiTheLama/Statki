import com.raylib.Raylib;
import org.bytedeco.javacpp.BytePointer;
import  java.net.*;
import  java.io.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;

public class Main {

    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket;
        Socket server;
        Socket player;
        InputStreamReader InputStream;
        BufferedReader Input = null;
        PrintWriter toSend = null;
        boolean getFlag=false;
        int port= Integer.parseInt(args[1]);
        if(args[0]=="server")
        {
            serverSocket=new ServerSocket(port);
            player =serverSocket.accept();
            InputStream=new InputStreamReader(player.getInputStream());
            Input=new BufferedReader(InputStream);
            toSend=new PrintWriter(player.getOutputStream());
        }
        else if(args[0]=="client")
        {
            server=new Socket("localhost",port);
            toSend=new PrintWriter(server.getOutputStream());
            InputStream=new InputStreamReader(server.getInputStream());
            Input=new BufferedReader(InputStream);
        }
        int width=1280,height=720;

        InitWindow(width, height, "Statki "+args[0]);
        SetTargetFPS(60);
        GameScreen game=new GameScreen((byte) 12);
        while (!WindowShouldClose()) {
            BeginDrawing();
            ClearBackground(RAYWHITE);
            game.draw(width);

            EndDrawing();

            if(game.collision())
            {
                int x=game.getX();
                int y=game.getY();
                if(x!=-1&&y!=-1)
                {
                    if(args[0]=="client")
                    {
                        getFlag=Boolean.parseBoolean(Input.readLine());
                    }
                    toSend.println(true);
                    toSend.flush();
                    toSend.println(x);
                    toSend.flush();
                    toSend.println(y);
                    toSend.flush();

                    String a=Input.readLine();
                    boolean tmp=Boolean.parseBoolean(a);
                    game.setShoot(tmp);
                    game.cleanXY();


                }

            }
            else
            {
                if(getFlag)
                {
                    String a=Input.readLine();
                    String b=Input.readLine();
                    int x=Integer.parseInt(a);
                    int y=Integer.parseInt(b);
                    boolean tmp=game.enemyShot(x,y);
                    toSend.println(tmp);
                    toSend.flush();
                    getFlag=false;
                }

            }
            if(args[0]=="server")
            {

                toSend.println(getFlag);
                toSend.flush();
                if(getFlag==false)
                {
                    getFlag=Boolean.parseBoolean(Input.readLine());
                }


            }
            else if(args[0]=="client")
            {
                getFlag=Boolean.parseBoolean(Input.readLine());
                if(getFlag==false)
                {
                    toSend.println(getFlag);
                    toSend.flush();
                }

            }



        }
        CloseWindow();
    }
}