import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.IsKeyDown;
import static com.raylib.Raylib.KEY_TAB;

public class DrawOnMap {

    static int endMapY;
    static int endMapX;
    static int startMapX;
    static int startMapY;

    public static void drawShipWhereMouse(int x, int y,int cell,int size,boolean rotate, Raylib.Texture ship)
    {
        Jaylib.Rectangle rec=new Jaylib.Rectangle(x,y,cell,cell*size);
        Raylib.Vector2 vec=new Jaylib.Vector2(0,0);
        Jaylib.Rectangle texture=new Jaylib.Rectangle(0,0,ship.width(),ship.height());
        Jaylib.DrawTexturePro(ship,texture,rec,vec,rotate?90:0,Jaylib.WHITE);

    }

    public static void drawBombWhereMouse(int x, int y,int cell,int type)
    {
        drawX(x,y,cell,Colors.mapAttackHit2);
        if(type < 1 || type > 4)
            return;
        switch (type)
        {
            case 1:
                drawSide(x,y,cell,1);
                break;

            case 2:
                drawCorners(x,y,cell,2);
                return;

            case 3:
                drawSide(x,y,cell,2);
                return;

            case 4:
                drawSide(x,y,cell,1);
                return;
        }
        drawCorners(x,y,cell,1);
    }

    static void drawCorners(int x,int y,int cell,int times)
    {
        for(int i=1;i<times+1;i++)
        {
            int temp=i*cell;
            drawX(x-temp,y-temp,cell,Colors.mapAttackHit2);
            drawX(x-temp,y+temp,cell,Colors.mapAttackHit2);
            drawX(x+temp,y-temp,cell,Colors.mapAttackHit2);
            drawX(x+temp,y+temp,cell,Colors.mapAttackHit2);
        }
    }

    static void drawSide(int x,int y,int cell,int times)
    {
        for(int i=1;i<times+1;i++)
        {
            int temp=i*cell;
            drawX(x-temp,y,cell,Colors.mapAttackHit2);
            drawX(x+temp,y,cell,Colors.mapAttackHit2);
            drawX(x,y-temp,cell,Colors.mapAttackHit2);
            drawX(x,y+temp,cell,Colors.mapAttackHit2);
        }
    }

    static void drawSign(int value,int x,int y,int cell)
    {
        switch(value) {
            case 0:
                break;
            case 1:
                Jaylib.DrawCircle(x + cell / 2,y + cell / 2,cell / 2,Colors.mapAttackMiss);
                Jaylib.DrawCircle(x + cell / 2,y + cell / 2,cell / 2 - 3,WHITE);
                break;
            case 2:
                Jaylib.Vector2 start=new Jaylib.Vector2(x,y);
                Jaylib.Vector2 end =new Jaylib.Vector2(x + cell,y + cell);
                Jaylib.DrawLineEx(start,end,3,Colors.mapAttackHit);
                start.x(x + cell);
                end.x(x);
                Jaylib.DrawLineEx(start,end,3,Colors.mapAttackHit);
                break;
            case 8:
                if(IsKeyDown(KEY_TAB))
                {
                    Jaylib.DrawCircle(x + cell / 2,y + cell / 2,cell / 2,GREEN);
                    Jaylib.DrawCircle(x + cell / 2,y + cell / 2,cell / 2 - 3,DARKGREEN);
                }
                break;
        }
    }

    static void drawX(int x,int y,int cell,Raylib.Color color)
    {
        if(x < startMapX || y < startMapY)
            return;
        if(x + cell >endMapX || y + cell > endMapY)
            return;

        Jaylib.Vector2 start=new Jaylib.Vector2(x,y);
        Jaylib.Vector2 end =new Jaylib.Vector2(x + cell,y + cell);
        Jaylib.DrawLineEx(start,end,3,color);
        start.x(x + cell);
        end.x(x);
        Jaylib.DrawLineEx(start,end,3,color);
    }


    static void drawAttack(int x,int y,int cell)
    {
        drawX(x,y,cell,Colors.mapAttackHit2);
        drawSide(x,y,cell,1);
    }

    static void mapPos(int startMapX,int startMapY,int size)
    {
        DrawOnMap.startMapX = startMapX;
        DrawOnMap.startMapY = startMapY;
        DrawOnMap.endMapX = startMapX + size;
        DrawOnMap.endMapY = startMapY + size;
    }
}
