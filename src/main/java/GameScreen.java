import com.raylib.Jaylib;
import com.raylib.Raylib;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;

public class GameScreen {
    byte[][] mapShip;
    byte[][] mapAttack;
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

    GameScreen(byte x)
    {
        n=x;
        mapShip=new byte[n][n];
        mapAttack=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        clear();
    }
    public void clear()
    {
        for(byte i=0;i<n;i++)
        {
            for(byte j=0;j<n;j++)
            {
                mapShip[i][j]=0;
                mapAttack[i][j]=0;
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
                mapAttack[y][x]=(byte)((mapAttack[y][x]+1)%4);

            }
        }
        return false;
    }

    public void draw()
    {
        drawMap(sizeBetweenEqAndMap,mapShip,"Twoja Mapa");
        drawMap(startEnemyMapLocation,mapAttack,"Mapa Wroga");
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
