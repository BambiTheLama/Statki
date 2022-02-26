import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static com.raylib.Jaylib.*;

public class MainGameCore {

    ServerSocket serverSocket;
    BufferedReader Input = null;
    PrintWriter toSend = null;
    Socket server;
    Socket player;
    InputStreamReader InputStream;
    int placeShipTime=60*10;
    int numberOfShipToPlace;
    int numberOfShipAlive;
    boolean isPlacingShipTime=true;
    boolean isOpponentAttack=false;
    boolean isOpponentLeft=false;
    boolean isProgramEnd=false;
    boolean isSomeoneWin=false;
    boolean isMyMove;
    boolean enemyLost=false;
    boolean lost=false;
    boolean win=false;

    byte[][] myMap;
    byte[][] enemyMap;
    byte n;
    int eqSize=100;
    int sizeBetweenEqAndMap=50;
    int sizeText=20;
    Raylib.Color textColor= BLACK;
    int cell=40;
    Raylib.Color mapLineColor= BLACK;
    Raylib.Color mapAttackMiss= DARKBLUE;
    Raylib.Color mapAttackHit= RED;
    Jaylib.Color shipColorContour=new Jaylib.Color(55,55,255,100);
    Jaylib.Color shipColor=new Jaylib.Color(55,55,255,25);
    int sizeBetweenMaps=200;
    int startEnemyMapLocation;
    int enemyX=-1;
    int enemyY=-1;

    public void reset(byte mapSize,int numberOfShip)
    {
        n=mapSize;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        numberOfShipToPlace=numberOfShip;
        numberOfShipAlive=numberOfShip;
        clear();
    }
    public void clear()
    {
        for(byte i=0;i<n;i++)
        {
            for(byte j=0;j<n;j++)
            {
                myMap[i][j]=0;
                enemyMap[i][j]=0;
            }
        }
    }


    public void main(String[] args) throws IOException {
        int port= Integer.parseInt(args[1]);
        reset((byte) 12,10);

        if(Objects.equals(args[0], "server"))
        {
            serverSocket=new ServerSocket(port);
            player =serverSocket.accept();
            InputStream=new InputStreamReader(player.getInputStream());
            Input=new BufferedReader(InputStream);
            toSend=new PrintWriter(player.getOutputStream());
            isMyMove=true;
        }
        else if(Objects.equals(args[0], "client"))
        {
            String ip=args[2];
            if(Objects.equals(ip, ""))
                server=new Socket("localhost",port);
            else
                server=new Socket(ip,port);
            toSend=new PrintWriter(server.getOutputStream());
            InputStream=new InputStreamReader(server.getInputStream());
            Input=new BufferedReader(InputStream);
            isMyMove=false;
        }

        int width=1280,height=720;

        InitWindow(width, height, "Statki "+args[0]);
        SetTargetFPS(60);
        while (!WindowShouldClose()&&isOpponentLeft==false&&isProgramEnd==false&&isSomeoneWin==false)
        {
            gameLoop(args);
            if(isOpponentLeft==false&&isProgramEnd==false&&isSomeoneWin==false)
            {
                BeginDrawing();
                ClearBackground(RAYWHITE);
                draw(width);
                EndDrawing();
            }

        }
        while (!WindowShouldClose())
        {
            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw(width);
            gameEnd(args,height);
            EndDrawing();
        }

        CloseWindow();
    }
    void gameLoop(String[] args) throws IOException {
        if(isSomeoneWin==false) {
            if (args[0] == "server") {
                isClientClose();
            }
            else if (args[0] == "client") {
                isServerClose();
            }
        }
        if(isOpponentLeft==false&&isProgramEnd==false&&isSomeoneWin==false)
        {

            if(collision()&&isMyMove)
            {
                if(args[0]=="server")
                {
                    toSend.println(true);
                    toSend.flush();
                }
                else if(args[0]=="client")
                {
                    isOpponentAttack=Boolean.parseBoolean(Input.readLine());
                    toSend.println(true);
                    toSend.flush();
                }
                int x=enemyX;
                toSend.println(x);
                toSend.flush();
                int y=enemyY;
                toSend.println(y);
                toSend.flush();
                boolean tmp=Boolean.parseBoolean(Input.readLine());
                setShoot(tmp);
                isMyMove=false;
                String tmp2=Input.readLine();
                if(Objects.equals(tmp2, "WIN"))
                {
                    isSomeoneWin=true;
                    win=true;
                }
            }
            else if(isPlacingShipTime==false)
            {

                if(args[0]=="server")
                {
                    toSend.println(false);
                    toSend.flush();
                    isOpponentAttack=Boolean.parseBoolean(Input.readLine());

                }
                else if(args[0]=="client")
                {
                    isOpponentAttack=Boolean.parseBoolean(Input.readLine());
                    if(isOpponentAttack==false)
                    {
                        toSend.println(false);
                        toSend.flush();
                    }

                }
                if(isOpponentAttack)
                {
                    int x=Integer.parseInt(Input.readLine());
                    int y=Integer.parseInt(Input.readLine());
                    boolean tmp=enemyShot(x,y);
                    toSend.println(tmp);
                    toSend.flush();
                    isOpponentAttack=false;
                    isMyMove=true;
                    if(numberOfShipAlive==0)
                    {
                        toSend.println("WIN");
                        toSend.flush();
                        isSomeoneWin=true;
                        lost=true;
                    }
                    else
                    {
                        toSend.println("NOT");
                        toSend.flush();
                    }
                }
            }
        }

    }

    void gameEnd(String[] args,int height) throws IOException{
        if(isOpponentLeft)
        {
            Jaylib.DrawText("WYGRALES",sizeBetweenEqAndMap,1/3*height,200,RED);
        }

        if(isSomeoneWin) {
            if (lost) {
                Jaylib.DrawText("PRZEGRALES", sizeBetweenEqAndMap, 1 / 3 * height, 200, RED);
            } else if (win) {
                Jaylib.DrawText("WYGRALES", sizeBetweenEqAndMap, 1 / 3 * height, 200, RED);
            }
        }

    }

    void isServerClose() throws IOException {

        isOpponentLeft=Boolean.parseBoolean(Input.readLine());
        if(isOpponentLeft==false)
        {
            toSend.println(isProgramEnd);
            toSend.flush();
        }

    }
    void isClientClose() throws IOException {

        toSend.println(isProgramEnd);
        toSend.flush();
        if(isProgramEnd==false)
        {
            isOpponentLeft=Boolean.parseBoolean(Input.readLine());
        }
    }


    public boolean collision()
    {

        if(placeShipTime>0)
        {
            placeShipTime--;
        }

        else if(placeShipTime==-1)
        {

        }
        else
        {
            placeShipTime=-1;
            numberOfShipAlive-=numberOfShipToPlace;
            isPlacingShipTime=false;
        }

        if(Raylib.IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            int x=Raylib.GetMouseX();
            int y=Raylib.GetMouseY();
            x=x-startEnemyMapLocation;
            y=y-(eqSize+sizeBetweenEqAndMap);
            if(isPlacingShipTime)
            {
                x=Raylib.GetMouseX();
                x=x-sizeBetweenEqAndMap;

                if(x>=0&&x<=n*cell&&y>=0&&y<=n*cell)
                {
                    x=x/cell;
                    y=y/cell;
                    if(myMap[y][x]==0 && numberOfShipToPlace>0)
                    {
                        myMap[y][x]=3;
                        numberOfShipToPlace--;
                    }
                    else if(myMap[y][x]==3)
                    {
                        myMap[y][x]=0;
                        numberOfShipToPlace++;
                    }
                }
            }
            else
            {
                if(x>=0&&x<=n*cell&&y>=0&&y<=n*cell)
                {

                    x=x/cell;
                    y=y/cell;
                    if(enemyMap[y][x]==0)
                    {
                        enemyX=x;
                        enemyY=y;
                    }

                    return true;
                }
            }


        }
        return false;
    }


    boolean enemyShot(int x,int y)
    {
        if(myMap[y][x]==3)
        {
            myMap[y][x]=2;
            numberOfShipAlive--;
            return true;
        }
        myMap[y][x]=1;
        return false;
    }

    void setShoot(boolean a)
    {
        if(enemyMap[enemyY][enemyX]==0)
        {
            if(a)
                enemyMap[enemyY][enemyX]=2;
            else
                enemyMap[enemyY][enemyX]=1;
        }

    }

    void cleanXY()
    {
        enemyY=-1;
        enemyX=-1;
    }

    public void draw(int width)
    {
        Raylib.DrawLine(0,eqSize,width,eqSize,BLACK);

        drawMap(sizeBetweenEqAndMap,myMap,"Twoja Mapa");
        drawMap(startEnemyMapLocation,enemyMap,"Mapa Wroga");
        if(isPlacingShipTime)
        {
            Jaylib.DrawText("Pozostaly czas ustawiania :"+placeShipTime/60+" s",0,0,20,BLACK);
            Jaylib.DrawText("Statki :"+numberOfShipToPlace,0,20,20,BLACK);
        }
        else
        {
            if(isMyMove)
            {
                Jaylib.DrawText("Moja Tura",0,0,20,BLACK);
            }
            else
            {
                Jaylib.DrawText("Przeciwnika Tura",0,0,20,BLACK);
            }
            Jaylib.DrawText("Statki :"+numberOfShipAlive,0,20,20,BLACK);
        }

    }

    private void drawMap(int x,byte[][] usedMap,String name)
    {

        for(byte i=0;i<=n;i++)
        {
            Jaylib.DrawLine(x,i*cell+sizeBetweenEqAndMap+eqSize,x+n*cell,i*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=""+(i+1);
                Jaylib.DrawText(tmp,x-sizeBetweenEqAndMap/2,i*cell+cell/4+sizeBetweenEqAndMap+eqSize,sizeText,textColor);
            }
        }
        for(byte i=0;i<=n;i++)
        {
            Jaylib.DrawLine(x+i*cell,sizeBetweenEqAndMap+eqSize,x+i*cell,n*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=(char)('a'+i)+"";
                Jaylib.DrawText(tmp,i*cell+cell*4/9+x,sizeBetweenEqAndMap/2+eqSize,sizeText,textColor);
            }
        }
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
            {
                switch(usedMap[i][j]) {
                    case 0:
                        break;
                    case 1:
                        Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2,mapAttackMiss);
                        Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell*5/11,WHITE);
                        break;
                    case 2:
                        Jaylib.Vector2 start=new Jaylib.Vector2(x+cell*j,sizeBetweenEqAndMap+eqSize+i*cell);
                        Jaylib.Vector2 end =new Jaylib.Vector2(x+cell*(j+1),sizeBetweenEqAndMap+eqSize+(i+1)*cell);
                        Jaylib.DrawLineEx(start,end,3,mapAttackHit);
                        start.x(x+cell*(j+1));
                        end.x(x+cell*(j));
                        Jaylib.DrawLineEx(start,end,3,mapAttackHit);
                        break;
                    case 3:
                        Jaylib.Rectangle rec=new Jaylib.Rectangle(x+cell*j,sizeBetweenEqAndMap+eqSize+i*cell,cell,cell);
                        Jaylib.DrawRectangleRec(rec,shipColor);
                        Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour);
                        break;
                }
            }
        Jaylib.DrawText(name,x,n*cell+eqSize+sizeBetweenEqAndMap,20,textColor);
    }
}