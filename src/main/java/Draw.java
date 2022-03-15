import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.io.IOException;

import static com.raylib.Jaylib.*;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.GetMouseX;
import static com.raylib.Raylib.GetMouseY;

public class Draw {

    int eqSize;
    int sizeBetweenEqAndMap;
    int cell;
    int sizeBetweenMaps;
    int startEnemyMapLocation;

    byte n;
    int height;
    int width;
    int sizeText;
    Raylib.Color textColor;
    Raylib.Color mapLineColor;
    Raylib.Color mapAttackMiss;
    Jaylib.Color mapAttackHit;
    Jaylib.Color mapAttackHit2;
    Jaylib.Color mapAttackHit3;
    Jaylib.Color shipColorContour;
    Jaylib.Color shipColor;
    Jaylib.Color shipColorContour2;
    Jaylib.Color shipColor2;

    Draw() {
        textColor= BLACK;
        mapLineColor= BLACK;
        mapAttackMiss= DARKBLUE;
        mapAttackHit= new Jaylib.Color(255,0,0,255);
        mapAttackHit2= new Jaylib.Color(255,0,0,100);
        mapAttackHit3= new Jaylib.Color(255,0,255,100);
        shipColorContour=new Jaylib.Color(55,55,255,100);
        shipColor=new Jaylib.Color(55,55,255,25);
        shipColorContour2=new Jaylib.Color(55,255,255,100);
        shipColor2=new Jaylib.Color(55,255,255,25);
    }

    Draw(byte n,int width,int height) {
        this();
        this.n=n;
        this.width=width;
        this.height=height;
        this.eqSize=100;
        this.sizeBetweenEqAndMap=50;
        this.cell=20;
        this.sizeBetweenMaps=200;
        this.sizeText=20;
        this.startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
    }

    Draw(byte n,int width,int height,int eqSize,int sizeBetweenEqAndMap,int cell,int sizeBetweenMaps,int sizeText,int startEnemyMapLocation) {
        this();
        this.n=n;
        this.width=width;
        this.height=height;
        this.eqSize=eqSize;
        this.sizeBetweenEqAndMap=sizeBetweenEqAndMap;
        this.cell=cell;
        this.sizeBetweenMaps=sizeBetweenMaps;
        this.sizeText=sizeText;
        this.startEnemyMapLocation=startEnemyMapLocation;
    }

    void draw(byte[][] myMap,byte[][] enemyMap,boolean isPlacingShipTime,boolean isMyMove,int placeShipTime,int numberOfShipToPlace,int numberOfShipAlive,int numberOfAttack,int attackMode,int numberOfShoot,int [][]raidMap) {
        Raylib.DrawLine(0,eqSize,width,eqSize,BLACK);

        drawMap(sizeBetweenEqAndMap,myMap,"Twoja Mapa");
        drawMap(startEnemyMapLocation,enemyMap,"Mapa Wroga");
        if(isPlacingShipTime)
        {
            Jaylib.DrawText("Pozostaly czas ustawiania :"+placeShipTime+" s",0,0,20,BLACK);
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

        for(int i=0;i<=6;i++)
        {
            if(i==attackMode)
                DrawRectangle(startEnemyMapLocation-20+i*80,8,64,64,GREEN);
            else
                DrawRectangle(startEnemyMapLocation-20+i*80,8,64,64,RED);

            DrawText(""+i,startEnemyMapLocation+12+i*80,40,20,BLACK);
        }

        if(isPlacingShipTime)
        {
            int x=GetMouseX();
            int y=GetMouseY();
            x-=sizeBetweenEqAndMap;
            y-=(sizeBetweenEqAndMap+eqSize);
            x/=cell;
            y/=cell;
            if(x>=0 && x<n && y>=0 && y<n)
            {
                x=x*cell+sizeBetweenEqAndMap;
                y=y*cell+sizeBetweenEqAndMap+eqSize;
                Jaylib.Rectangle rec=new Jaylib.Rectangle(x,y,cell,cell);
                Jaylib.DrawRectangleRec(rec,shipColor2);
                Jaylib.DrawRectangleLinesEx(rec,3,shipColorContour2);
            }
        }
        else if(isMyMove)
        {
            int x=GetMouseX();
            int y=GetMouseY();
            x-=startEnemyMapLocation;
            y-=(sizeBetweenEqAndMap+eqSize);
            x/=cell;
            y/=cell;
            if(isOnEnemyMap(x,y))
            {
                int startX=x*cell+startEnemyMapLocation;
                int startY=y*cell+sizeBetweenEqAndMap+eqSize;
                drawX(startX,startY,mapAttackHit2);
                switch(attackMode)
                {
                    case 1:
                        for(int i=0;i<3;i++)
                            for(int j=0;j<3;j++)
                            {


                                if(isOnEnemyMap(x-1+i,y-1+j))
                                    drawX(startX+cell*(i-1),startY+cell*(j-1),mapAttackHit3);

                            }
                        break;
                    case 2:
                        for(int i=0;i<5;i++)
                        {

                            if(isOnEnemyMap(x-2+i,y-2+i))
                                drawX(startX+cell*(i-2),startY+cell*(i-2),mapAttackHit3);
                            if(isOnEnemyMap(x-2+i,y+2-i))
                                drawX(startX+cell*(i-2),startY+cell*(2-i),mapAttackHit3);

                        }
                        break;
                    case 3:
                        for(int i=0;i<5;i++)
                        {

                            if(isOnEnemyMap(x-2+i,y))
                                drawX(startX+cell*(-2+i),startY,mapAttackHit3);
                            if(isOnEnemyMap(x,y-2+i))
                                drawX(startX,startY+cell*(-2+i),mapAttackHit3);

                        }
                        break;
                    case 4:
                        DrawText(""+numberOfAttack,startX+cell*2,startY-cell*2,20,BLACK);
                        for(int i=0;i<3;i++)
                        {
                            if(isOnEnemyMap(x-1+i,y))
                                drawX(startX+cell*(-1+i),startY,mapAttackHit3);
                            if(isOnEnemyMap(x,y-1+i))
                                drawX(startX,startY+cell*(-1+i),mapAttackHit3);
                        }
                        if(raidMap!=null)
                        {
                            Jaylib.Color tmp= new Jaylib.Color(255,255,0,200);
                            startX=startEnemyMapLocation;
                            startY=sizeBetweenEqAndMap+eqSize;
                            for(int i=0;i<numberOfShoot;i++)
                            {
                                if(isOnEnemyMap(raidMap[i][1],raidMap[i][0]))
                                    drawX(startX+cell*raidMap[i][1],startY+cell*raidMap[i][0],tmp );
                            }
                        }
                        break;
                    case 5:

                        break;
                    case 6:
                        break;
                }
            }
        }

    }

    void drawX(int x, int y, Jaylib.Color color) {
        Jaylib.Vector2 start=new Jaylib.Vector2(x,y);
        Jaylib.Vector2 end =new Jaylib.Vector2(x+cell,y+cell);
        Jaylib.DrawLineEx(start,end,3,color);
        start.x(x+cell);
        end.x(x);
        Jaylib.DrawLineEx(start,end,3,color);
    }

    void drawMap(int x,byte[][] usedMap,String name) {

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
                        Jaylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2-3,WHITE);
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

    void gameEnd(boolean isOpponentLeft,boolean isSomeoneWin,boolean lost) {
        if(isOpponentLeft)
        {
            Jaylib.DrawText("WYGRALES",sizeBetweenEqAndMap,(int)((1.0/3.0)*height),150,RED);
        }

        if(isSomeoneWin) {
            if (lost) {
                Jaylib.DrawText("PRZEGRALES", sizeBetweenEqAndMap, (int)((1.0/3.0)*height), 150, RED);
            }
            else
            {
                Jaylib.DrawText("WYGRALES",sizeBetweenEqAndMap,(int)((1.0/3.0)*height),150,RED);
            }
        }
    }

    boolean isOnEnemyMap(int x,int y) {
        if(x>=0 && x<n && y>=0 && y<n)
            return true;
        return false;
    }


    public void drawButton(int x,int sizeX,int y,int sizeY,String text,int textSize,Boolean isMouseOn)
    {
        Jaylib.DrawRectangle(x,y,sizeX,sizeY,BLUE);
        if(isMouseOn)
        {
            Jaylib.Rectangle tmp=new Jaylib.Rectangle(x,y,sizeX,sizeY);
            Jaylib.DrawRectangleLinesEx(tmp,10,SKYBLUE);
        }
        Jaylib.DrawText(text,x+25,y+25,textSize,BLACK);
    }
    public void drawMenu(String port,String ip,boolean isMouseOnHost,boolean isMouseOnJoin,boolean isPortTyping,boolean isIpTyping)
    {
        drawButton(50,225,50,100,"Hostuj",50,isMouseOnHost);
        drawButton(50,225,250,100,"Dolacz",50,isMouseOnJoin);
        drawButton(325,325,50,100,"Port:"+port,50,isPortTyping);
        drawButton(325,325,250,100,"IP:"+ip,30,isIpTyping);

    }



}
