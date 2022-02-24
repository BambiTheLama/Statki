import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;

public class GameScreen {
    byte[][] myMap;
    byte[][] enemyMap;
    final byte n;
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

    GameScreen(byte x)
    {
        n=x;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
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

    public boolean collision()
    {
        if(Raylib.IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            int x=Raylib.GetMouseX();
            int y=Raylib.GetMouseY();
            x=x-startEnemyMapLocation;
            y=y-(eqSize+sizeBetweenEqAndMap);
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
            x=Raylib.GetMouseX();
            x=x-sizeBetweenEqAndMap;
            if(x>=0&&x<=n*cell&&y>=0&&y<=n*cell)
            {
                x=x/cell;
                y=y/cell;
                if(myMap[y][x]==0)
                    myMap[y][x]=3;
                else
                    myMap[y][x]=0;

                return true;
            }
        }
        return false;
    }
    boolean enemyShot(int x,int y)
    {
        if(myMap[y][x]==3)
        {
            myMap[y][x]=2;
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
    int getX()
    {
        return enemyX;
    }
    int getY()
    {
        return enemyY;
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
