import com.raylib.Jaylib;
import com.raylib.Raylib;
import static com.raylib.Jaylib.*;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.GetMouseX;
import static com.raylib.Raylib.GetMouseY;

public class DrawMenu {
    private static Jaylib.Color buttonColor=new Jaylib.Color(128,128,255,255);
    private static Jaylib.Color buttonPressColor=new Jaylib.Color(64,64,128,255);
    private static Jaylib.Color textColor=new Jaylib.Color(0,0,0,255);
    private static Jaylib.Color xColor=new Jaylib.Color(255,0,0,255);
    private static Jaylib.Color oColor=new Jaylib.Color(0,0,255,255);
    private static int port;
    private static String ip;

    private static int[][] ship;
    private static int[][] attackWhiteList;

    private static int time=0;
    private static int timeMax=120;
    private static float rotate=0;
    private static boolean Btime=false;
    private static Texture loading;
    private static Font font=GetFontDefault();
    private static boolean isLoading=false;
    private static float textSpace=1;
    static Texture[] shipTexture=new Texture[5];


    DrawMenu(int port,int[][] ship,int[][] attackWhiteList)
    {
        this.port=port;
        this.ship = ship;
        this.attackWhiteList = attackWhiteList;
        loading=LoadTexture("resources/load.png");
        font=LoadFont("resources/czciaki/comici.ttf");
        for(int i=0;i<5;i++)
        {
            shipTexture[i]=LoadTexture("resources/"+(i+1)+".png");
        }


    }
    void reLoad()
    {
        loading=LoadTexture("resources/load.png");
        font=LoadFont("resources/czciaki/comici.ttf");
        for(int i=0;i<5;i++)
        {
            shipTexture[i]=LoadTexture("resources/"+(i+1)+".png");
        }
    }

    void clear()
    {
        UnloadTexture(loading);
        UnloadFont(font);
        for(int i=0;i<5;i++)
            UnloadTexture(shipTexture[i]);
    }
    public static void setPort(int port) {
        DrawMenu.port = port;
    }

    public static void setShip(int[][] ship) {
        DrawMenu.ship = ship;
    }

    public static void setAttackWhiteList(int[][] attackWhiteList) {
        DrawMenu.attackWhiteList = attackWhiteList;
    }

    public static void setIp(String ip) {
        DrawMenu.ip = ip;
    }

    public static void setIsLoading(boolean isLoading)
    {
        DrawMenu.isLoading =isLoading;
    }


    void Draw(int menuStage, int StartDrawingY,int buttonUse)
    {
        Jaylib.Rectangle rec=new Jaylib.Rectangle(1205,25,50,50);
        DrawRectangleRec(rec,RED);


        rec=new Jaylib.Rectangle(1202,22,56,56);
        DrawRectangleLinesEx(rec,5,BLACK);
        Jaylib.Vector2 start = new Jaylib.Vector2(1205,25);
        Jaylib.Vector2 end = new Jaylib.Vector2(1255,75);
        DrawLineEx(start,end,5,BLACK);
        start.y(75);
        end.y(25);
        DrawLineEx(start,end,5,BLACK);
        if(buttonUse==0)
        {
            rec=new Jaylib.Rectangle(1203,23,54,54);
            DrawRectangleLinesEx(rec,3,RED);
        }
        switch (menuStage)
        {

            case 2:
                DrawHostSettings(StartDrawingY);
                break;
            case 3:
                DrawHostIP();
                break;
            case 4:
                DrawClientMenu();
                break;
            case 5:
                DrawSettings();
                break;

        }
    }

    void DrawHostSettings(int StartDrawingY)
    {
        Jaylib.Vector2 tmp1 = new Jaylib.Vector2(1205,150);
        Jaylib.Vector2 tmp2 = new Jaylib.Vector2(1255,150);
        Jaylib.Vector2 tmp3 = new Jaylib.Vector2(1230,100);
        DrawTriangle(tmp1,tmp2,tmp3,buttonColor);

        tmp1 = new Jaylib.Vector2(1205,645);
        tmp2 = new Jaylib.Vector2(1230,695);
        tmp3 = new Jaylib.Vector2(1255,645);
        DrawTriangle(tmp1,tmp2,tmp3,buttonColor);
        DrawTextButton(25,25+StartDrawingY,315,60,40,"Wielkosc Mapy");




        DrawTextButton(25,110+StartDrawingY,500,60,40,"Czas stawiania statkow");


        DrawTextButton(25,195+StartDrawingY,225,60,40,"Czas tury");


        DrawTextButton(25,280+StartDrawingY,140,60,40,"Statki");
        DrawShipConfig(StartDrawingY);
        DrawBulletCongig(StartDrawingY);
        DrawTextButton(25,715+StartDrawingY,325,60,40,"Startowe Zloto");


        DrawTextButton(25,800+StartDrawingY,110,60,40,"Port");



    }
    void DrawShipConfig(int StartDrawingY)
    {
        DrawTextButton(25,StartDrawingY+365,120,60,40,"Typ");

        try{
            for(int i=0;i<5;i++)
                DrawTextButton(145+i*110,StartDrawingY+365,110,60,40,""+ship[0][i]);
        }
        catch (Exception e)
        {
            ship=new int[3][5];
            for(int i=0;i<5;i++)
            {
                ship[0][i]=i+1;
                ship[1][i]=0;
                ship[2][i]=0;
            }
        }


        DrawTextButton(25,425+StartDrawingY,120,60,40,"Ilosc");
        DrawTextButton(25,485+StartDrawingY,120,60,40,"Zloto");
        for(int i=0;i<4;i++)
        {
            DrawLine(25,365+60*i+StartDrawingY,695,365+60*i+StartDrawingY,BLACK);
        }
        DrawLine(25,365+StartDrawingY,25,545+StartDrawingY,BLACK);
        for(int i=1;i<7;i++)
        {
            DrawLine(35+110*i,365+StartDrawingY,35+110*i,545+StartDrawingY,BLACK);
        }

    }
    void DrawBulletCongig(int StartDrawingY)
    {
        DrawTextButton(25,570+StartDrawingY,120,64,40,"Typ");
        DrawTextButton(25,634+StartDrawingY,120,64,40,"Cena");
        DrawLine(25,570+StartDrawingY,25,694+StartDrawingY,BLACK);

        try{
            for(int i=0;i<6;i++)
            {
                DrawTextButton(145+i*110,570+StartDrawingY,110,64,0,"");
                if(attackWhiteList[0][i]==1)
                {

                    DrawLine(145+i*110,570+StartDrawingY,255+i*110,634+StartDrawingY,RED);
                    DrawLine(255+i*110,570+StartDrawingY,145+i*110,634+StartDrawingY,RED);
                }

            }
        }
        catch (Exception e){
            attackWhiteList=new int[2][6];
            for(int i=0;i<6;i++)
            {
                attackWhiteList[0][i]=1;
                attackWhiteList[1][i]=0;
            }
        }


        for(int i=1;i<8;i++)
        {
            DrawLine(35+110*i,570+StartDrawingY,35+110*i,694+StartDrawingY,BLACK);
        }
        for(int i=0;i<3;i++)
        {
            DrawLine(25,570+64*i+StartDrawingY,805,570+64*i+StartDrawingY,BLACK);
        }



    }
    void DrawHostIP()
    {
        DrawTextButton(440,285,400,60,40,"IP:"+ip);
        DrawTextButton(440,375,400,60,40,"PORT:"+port);
        Jaylib.Rectangle rec=new Jaylib.Rectangle(0,0,loading.width(),loading.height());
        Jaylib.Rectangle rec2=new Jaylib.Rectangle(640,590,loading.width()/2,loading.width()/2);
        Jaylib.Vector2 vec=new Jaylib.Vector2(loading.width()/4,loading.height()/4);
        rotate+=0.1;
        rotate=rotate%360;
        DrawTexturePro(loading,rec,rec2,vec,rotate,WHITE);
    }
    void DrawClientMenu()
    {
        if(isLoading)
        {
            Jaylib.Rectangle rec=new Jaylib.Rectangle(0,0,loading.width(),loading.height());
            Jaylib.Rectangle rec2=new Jaylib.Rectangle(640,590,loading.width()/2,loading.width()/2);
            Jaylib.Vector2 vec=new Jaylib.Vector2(loading.width()/4,loading.height()/4);
            rotate+=0.1;
            rotate=rotate%360;
            DrawTexturePro(loading,rec,rec2,vec,rotate,WHITE);
        }
    }
    void DrawSettings()
    {
        DrawTextButton(25,25,300,60,40,"Kolor tekstu");
        Jaylib.Rectangle rec=new Jaylib.Rectangle(350,25,60,60);
        DrawRectangleRec(rec,textColor);
        DrawRectangleLinesEx(rec,2,BLACK);
        DrawTextButton(25,110,300,60,40,"Kolor przyciskow");
        rec.y(110);
        DrawRectangleRec(rec,buttonColor);
        DrawRectangleLinesEx(rec,2,BLACK);
        rec.x(rec.x()+85);
        DrawRectangleRec(rec,buttonPressColor);
        DrawRectangleLinesEx(rec,2,BLACK);
        rec.y(rec.y()+85);

        DrawTextButton(25,195,300,60,40,"Kolor znacznikow");
        rec.x(rec.x()-85);
        Jaylib.Vector2 start=new Jaylib.Vector2(rec.x(),rec.y());
        Jaylib.Vector2 end=new Jaylib.Vector2(rec.x()+rec.width(),rec.y()+rec.height());
        DrawLineEx(start,end,3,xColor);
        start.y(start.y()+rec.height());
        end.y(rec.y());
        DrawLineEx(start,end,3,xColor);
        DrawRectangleLinesEx(rec,2,BLACK);
        rec.x(rec.x()+85);
        DrawRectangleRec(rec,xColor);
        DrawRectangleLinesEx(rec,2,BLACK);
        rec.x(rec.x()+85);
        DrawCircle((int) (rec.x()+30), (int) (rec.y()+30),30,oColor);
        DrawCircle((int) (rec.x()+30), (int) (rec.y()+30),26,WHITE);
        DrawRectangleLinesEx(rec,2,BLACK);
        rec.x(rec.x()+85);
        DrawRectangleRec(rec,oColor);
        DrawRectangleLinesEx(rec,2,BLACK);

    }
    static boolean collision(int startX, int startY, int sizeX, int sizeY)
    {
        int x=GetMouseX();
        int y=GetMouseY();
        if(x>startX && x<startX+sizeX && y>startY && y<startY+sizeY)
            return true;
        return false;
    }
    static void DrawButton(int startX, int startY, int sizeX, int sizeY, int textSize, String Text,boolean pressed)
    {

        Jaylib.Rectangle rec=new Jaylib.Rectangle(startX,startY,sizeX,sizeY);
        DrawRectangleRec(rec,buttonColor);
        if(pressed)
            DrawRectangleLinesEx(rec,8,buttonPressColor);
        Raylib.Vector2 temp=MeasureTextEx(GetFontDefault(),Text,textSize,7);

        Jaylib.Vector2 tmp=new Jaylib.Vector2((int)(startX+(sizeX-temp.x())/2),(startY+(sizeY-temp.y())/2)+3);
        DrawTextEx(font,Text,tmp,textSize,(1f/10f)*textSize*textSpace,textColor);

    }
    static void DrawTextButton(int startX, int startY, int sizeX, int sizeY, int textSize, String Text)
    {
        Jaylib.Rectangle rec=new Jaylib.Rectangle(startX,startY,sizeX,sizeY);
        DrawRectangleRec(rec,buttonColor);
        Raylib.Vector2 temp=MeasureTextEx(GetFontDefault(),Text,textSize,7);
        Jaylib.Vector2 tmp=new Jaylib.Vector2(startX+10,(startY+(sizeY-temp.y())/2)+3);
        DrawTextEx(font,Text,tmp,textSize,(1f/10f)*textSize*textSpace,textColor);
    }
    static void DrawCtrButton(int startX, int startY, int sizeX, int sizeY, int textSize, String Text,int stringSize,boolean pressed)
    {
        Jaylib.Rectangle rec=new Jaylib.Rectangle(startX,startY,sizeX,sizeY);
        DrawRectangleRec(rec,buttonColor);
        if(pressed && stringSize>0)
        {
            DrawRectangleLinesEx(rec,8,buttonPressColor);
            if(Btime)
            {
                time--;
                if(time<1)
                    Btime=false;
            }
            else
            {
                time++;
                if(time>timeMax)
                    Btime=true;
            }

        }


        Raylib.Vector2 temp=MeasureTextEx(GetFontDefault(),Text,textSize,7);
        if(Btime&&stringSize>Text.length()&&pressed)
        {
            Jaylib.Vector2 tmp=new Jaylib.Vector2(startX+10,(startY+(sizeY-temp.y())/2)+3);
            DrawTextEx(font,Text+"_",tmp,textSize,(1f/10f)*textSize*textSpace,textColor);

        }
        else
        {
            Jaylib.Vector2 tmp=new Jaylib.Vector2(startX+10,(startY+(sizeY-temp.y())/2)+3);
            DrawTextEx(font,Text,tmp,textSize,(1f/10f)*textSize*textSpace,textColor);
        }

    }
    void setButtonColor(Jaylib.Color color)
    {
        buttonColor=color;
    }
    void setButtonPressColor(Jaylib.Color color)
    {
        buttonPressColor=color;
    }
    void setxColor(Jaylib.Color color)
    {
        xColor=color;
    }
    void setoColor(Jaylib.Color color)
    {
        oColor=color;
    }
    void setTextColor(Jaylib.Color color)
    {
        textColor=color;
    }
    Jaylib.Color getButtonColor()
    {
        return buttonColor;
    }
    Jaylib.Color getButtonPressColor()
    {
        return buttonPressColor;
    }
    Jaylib.Color getxColor()
    {
        return xColor;
    }
    Jaylib.Color getoColor()
    {
        return oColor;
    }
    Jaylib.Color getTextColor()
    {
        return textColor;
    }

}
