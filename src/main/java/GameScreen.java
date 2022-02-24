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
    Raylib.Color TextColor= BLACK;
    int cell=40;
    Raylib.Color mapLineColor= BLACK;

    int sizeBetweenMaps=200;

    GameScreen(byte x)
    {
        n=x;
        mapShip=new byte[n][n];
        mapAttack=new byte[n][n];
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
        drawMap(0);
        drawMap(n*cell+sizeBetweenEqAndMap+sizeBetweenMaps);

    }
    private void drawMap(int x)
    {

        for(byte i=0;i<=n;i++)
        {
            Raylib.DrawLine(x+sizeBetweenEqAndMap,i*cell+sizeBetweenEqAndMap+eqSize,x+n*cell+sizeBetweenEqAndMap,i*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=""+(i+1);
                Raylib.DrawText(tmp,sizeBetweenEqAndMap/2+x,i*cell+cell/4+sizeBetweenEqAndMap+eqSize,sizeText,TextColor);
            }

        }
        for(byte i=0;i<=n;i++)
        {
            Raylib.DrawLine(x+i*cell+sizeBetweenEqAndMap,sizeBetweenEqAndMap+eqSize,x+i*cell+sizeBetweenEqAndMap,n*cell+sizeBetweenEqAndMap+eqSize,mapLineColor);
            if(i!=n)
            {
                String tmp=(char)('a'+i)+"";
                Raylib.DrawText(tmp,i*cell+cell*4/9+sizeBetweenEqAndMap+x,sizeBetweenEqAndMap/2+eqSize,sizeText,TextColor);
            }

        }

    }

}
