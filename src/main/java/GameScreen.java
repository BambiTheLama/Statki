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
    Raylib.Color shipColorContour=new Raylib.Color();
    Raylib.Color shipColor=new Raylib.Color();
    int sizeBetweenMaps=200;

    GameScreen(byte x)
    {
        n=x;
        mapShip=new byte[n][n];
        mapAttack=new byte[n][n];
        shipColorContour.r((byte)55);
        shipColorContour.g((byte)55);
        shipColorContour.b((byte)255);
        shipColorContour.a((byte)100);
        shipColor.r((byte)55);
        shipColor.g((byte)55);
        shipColor.b((byte)255);
        shipColor.a((byte)25);
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
    public void draw()
    {
        drawMap(sizeBetweenEqAndMap,mapAttack,"Mapa Wroga");
        drawMap(n*cell+sizeBetweenEqAndMap+sizeBetweenMaps,mapShip,"Twoja Mapa");

    }
    private void drawMap(int x,byte[][] usedMap,String name)
    {

        for(byte i=0;i<=n;i++)
        {
            Raylib.DrawLine(x,i*cell+sizeBetweenEqAndMap+eqSize,x+n*cell,i*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=""+(i+1);
                Raylib.DrawText(tmp,x-sizeBetweenEqAndMap/2,i*cell+cell/4+sizeBetweenEqAndMap+eqSize,sizeText,textColor);
            }
        }
        for(byte i=0;i<=n;i++)
        {
            Raylib.DrawLine(x+i*cell,sizeBetweenEqAndMap+eqSize,x+i*cell,n*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=(char)('a'+i)+"";
                Raylib.DrawText(tmp,i*cell+cell*4/9+x,sizeBetweenEqAndMap/2+eqSize,sizeText,textColor);
            }
        }
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
            {
                switch(usedMap[i][j]) {
                    case 0:
                        break;
                    case 1:
                        Raylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell/2,mapAttackMiss);
                        Raylib.DrawCircle(x+j*cell+cell/2,sizeBetweenEqAndMap+eqSize+i*cell+cell/2,cell*5/11,WHITE);
                        break;
                    case 2:
                        Raylib.Vector2 start=new Raylib.Vector2();
                        start.x(x+cell*j);
                        start.y(sizeBetweenEqAndMap+eqSize+i*cell);
                        Raylib.Vector2 end =new Raylib.Vector2();
                        end.x(x+cell*(j+1));
                        end.y(sizeBetweenEqAndMap+eqSize+(i+1)*cell);
                        Raylib.DrawLineEx(start,end,3,mapAttackHit);
                        start.x(x+cell*(j+1));
                        end.x(x+cell*(j));
                        Raylib.DrawLineEx(start,end,3,mapAttackHit);
                        break;
                    case 3:
                        Raylib.Rectangle rec=new Raylib.Rectangle();
                        rec.x(x+cell*j);
                        rec.y(sizeBetweenEqAndMap+eqSize+i*cell);
                        rec.height(cell);
                        rec.width(cell);
                        Raylib.DrawRectangleRec(rec,shipColor);
                        Raylib.DrawRectangleLinesEx(rec,3,shipColorContour);
                        break;
                }
            }
        Raylib.DrawText(name,x,n*cell+eqSize+sizeBetweenEqAndMap,20,textColor);
    }
}
