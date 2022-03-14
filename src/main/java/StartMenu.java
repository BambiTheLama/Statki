import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.WindowShouldClose;

public class StartMenu {

    static  boolean isPortTyping=false;
    static  String port=new String();
    static  boolean isIpTyping=false;
    static  String ip=new String();
    static  boolean isMouseOnHost=false;
    static  boolean isMouseOnJoin=false;
    public static boolean main(String[] arg) {
        Draw draw=new Draw();
        int width=800,height=400;
        port="";
        ip="";
        boolean f=false;
        InitWindow(width, height, "Statki Menu Glowne");
        SetTargetFPS(60);
        byte end=0;
        Jaylib.SetExitKey(KEY_ESCAPE);
        while (!WindowShouldClose()&&end==0) {
            if(IsKeyPressed(KEY_ESCAPE))
            {
                end=3;
            }
            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw.drawMenu(port,ip,isMouseOnHost,isMouseOnJoin,isPortTyping,isIpTyping);
            end=collision();

            EndDrawing();
        }
        CloseWindow();

        MainGameCore game=new MainGameCore((byte)24,10);
        if(end==1)
        {
            String[] a=new String[2];
            a[0]="server";
            a[1]=port;
            try{
                game.main(a);
            }
            catch (Exception b){

            }
            return true;
        }
        else if(end==2)
        {

            String[] a=new String[3];
            a[0]="client";
            a[1]=port;
            a[2]=ip;
            try{
                game.main(a);
            }
            catch (Exception b){

            }
            return true;
        }
        return false;
    }

    public static boolean mouseCollision(int x,int startX,int endX,int y,int startY,int endY,boolean state)
    {
        if(state==false)
        {
            if(x > startX && x < endX && y > startY && y < endY)
            {
                return true;
            }
        }
        else
        {
            if(x < startX || x > endX || y < startY || y > endY)
            {
                return false;
            }
        }
        return state;
    }
    public static byte collision()
    {
        int x= GetMouseX();
        int y= GetMouseY();
        isMouseOnHost=mouseCollision(x,50,275,y,50,150,isMouseOnHost);
        isMouseOnJoin=mouseCollision(x,50,275,y,250,400,isMouseOnJoin);
        isPortTyping=mouseCollision(x,325,650,y,50,150,isPortTyping);
        isIpTyping=mouseCollision(x,325,650,y,250,400,isIpTyping);
        if(isPortTyping)
        {
            int tmp= Raylib.GetKeyPressed();
            if(tmp>=KEY_ZERO&&tmp<=KEY_NINE)
            {
                if(port.length()<5)
                {
                    tmp=tmp-48;
                    port=port+tmp;
                }
            }
            if(tmp==KEY_BACKSPACE)
            {
                if(port.length()>0)
                    port=port.substring(0,port.length()-1);
            }
        }

        if(isIpTyping)
        {
            int tmp= Raylib.GetKeyPressed();
            if(tmp>=KEY_ZERO&&tmp<=KEY_NINE)
            {
                if(ip.length()<15)
                {
                    tmp=tmp-48;
                    ip=ip+tmp;
                }
            }
            if(tmp==KEY_PERIOD)
            {
                if(ip.length()<15)
                {
                    ip=ip+".";
                }
            }
            if(tmp==KEY_BACKSPACE)
            {
                if(ip.length()>0)
                    ip=ip.substring(0,ip.length()-1);
            }
        }

        if(IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            if(x > 50 && x < 275 && y > 50 && y < 150)
            {
                return 1;
            }
            if(x > 50 && x < 275 && y > 250 && y < 350)
            {
                return 2;
            }
        }
        return 0;
    }
}
