import com.raylib.Jaylib;
import java.net.InetAddress;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.WindowShouldClose;


public class MenuStart extends Thread{
    int t=0;
    String who;
    String ip="";
    int port=25565;
    int mapSize=0;
    int startTime=0;
    int moveTime=0;
    int[][] ship=new int[3][5];
    int[][] attackWhiteList=new int[2][6];
    int startGold=0;
    int menuStage=1;
    int y=0;
    float MouseWheel;
    boolean end=false;
    boolean tryConnect=false;
    DrawMenu menu=null;
    Jaylib.Color[] shipColor=new Jaylib.Color[5];


    public void run() {

        tryConnect=false;
        for(int i=0;i<5;i++)
            ship[0][i]=i+1;

        InitWindow(1280,720,"MENU");
        menu=new DrawMenu(port,ship,attackWhiteList);
        while (!WindowShouldClose() && menuStage>0 && !end) {


            BeginDrawing();
            ClearBackground(WHITE);
            menuStage+=collision();
            menu.Draw(menuStage,y);

            DrawText(menuStage+"",0,0,20,BLACK);

            EndDrawing();
            System.gc();

        }
        Draw.setShipColor(shipColor);
        menu.clear();
        CloseWindow();
        end=true;
    }

    boolean getEnd()
    {
        return end;
    }

    void setEnd(boolean end)
    {
        this.end=end;
    }

    int getMenuStage()
    {
        return menuStage;
    }

    String getWho()
    {
        return who;
    }

    String getIp()
    {
        return ip;
    }

    int getPort()
    {
        return port;
    }

    int collision()
    {
        int tmp=0;

        switch (menuStage){
            case 1:

                tmp=collisionStart();
                if(tmp==1)
                    MouseWheel=GetMouseWheelMove();
                break;
            case 2:

                tmp=collisionHostSettings();
                menu.setPort(port);
                if(tmp==1)
                {
                    try{
                        ip=InetAddress.getLocalHost()+"";

                        int index = ip.indexOf( '/' );
                        ip=ip.substring(index+1,ip.length());
                        System.out.println(ip);
                        DrawMenu.setIp(ip);
                        who="server";
                    }
                    catch (Exception e)
                    {

                    }
                    if(port<25565)
                        port=25565;
                    else if(port>65535)
                        port=65535;
                    DrawMenu.setPort(port);
                }
                break;
            case 3:

                tmp=collisionHostIP();
                if(tmp==-1)
                {
                    MouseWheel=GetMouseWheelMove();
                }


                break;
            case 4:

                tmp=collisionClientMenu();
                if(tmp==-1)
                {
                    tryConnect=false;
                }
                break;
            case 5:

                tmp=collisionSettings();
                break;

        }
        return tmp;
    }

    int collisionStart()
    {

        if(collision(1202,22,56,56) && IsMouseButtonPressed(0))
            return -1;

        if(button(415,165,450,100,75,"Hostuj"))
            return 1;

        if(button(415,295,450,100,75,"Dolacz"))
            return 3;
        if(button(415,425,450,100,75,"Ustawienia"))
            return 4;

        return 0;
    }
    int collisionHostSettings()
    {
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
                return -1;

            if(y<0&&collision(1205,100,50,50))
            {
                if(y+100<=0)
                    y+=100;
                else
                    y=0;
            }

            if(y>=-300&&collision(1205,645,50,50))
            {
                int tmp=y-=100;
                if(tmp<=-300)
                {
                    y=(-300);
                }
                else
                {
                    y-=100;
                }
            }
        }

        if(button(25,885+y,350,80,80,"START"))
        {
            return 1;
        }



        float wheel=GetMouseWheelMove()+MouseWheel;
        MouseWheel=GetMouseWheelMove();
        if(wheel!=0)
        {
            if(wheel*20+y<=-300)
            {
                y=(-300);
            }
            else if(wheel*20+y>-300 && wheel*20+y<0)
            {
                y+=(wheel*20);

            }
            else if(wheel*20+y>=0)
            {
                y=0;
            }

        }

        for(int i=0;i<5;i++)
            if(textButton(145+i*110,425+y,110,60,40,""+ship[1][i],1))
            {
                ship[1][i]=textCtrInt(ship[1][i]+"",1);
                DrawMenu.setShip(ship);
            }

        for(int i=0;i<5;i++)
            if(textButton(145+i*110,485+y,110,60,40,""+ship[2][i],4))
            {
                ship[2][i]=textCtrInt(ship[2][i]+"",4);
                DrawMenu.setShip(ship);
            }



        for(int i=0;i<6;i++)
        {
            if(IsMouseButtonPressed(0) && collision(145+i*110,570+y,110,64))
            {
                if(attackWhiteList[0][i]==1)
                    attackWhiteList[0][i]=0;
                else
                    attackWhiteList[0][i]=1;
                DrawMenu.setAttackWhiteList(attackWhiteList);
            }
            if(textButton(145+i*110,634+y,110,64,40,""+attackWhiteList[1][i],4))
            {

                attackWhiteList[1][i]=textCtrInt(attackWhiteList[1][i]+"",4);
                DrawMenu.setAttackWhiteList(attackWhiteList);
            }

        }

        if(textButton(365,25+y,60,60,40,""+mapSize,2))
        {
            mapSize=textCtrInt(mapSize+"",2);
        }

        if(textButton(550,110+y,60,60,40,""+startTime,2))
        {
            startTime=textCtrInt(startTime+"",2);
        }

        if(textButton(275,195+y,60,60,40,""+moveTime,2))
        {
            moveTime=textCtrInt(moveTime+"",2);
        }
        if(textButton(375,715+y,160,60,40,""+startGold,5))
        {
            startGold=textCtrInt(startGold+"",5);
        }
        if(textButton(160,800+y,160,60,40,""+port,5))
        {
            port=textCtrInt(port+"",5);
            DrawMenu.setPort(port);
        }
        return 0;
    }
    int collisionHostIP()
    {
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                ip="";
                DrawMenu.setIp(ip);

                return -1;
            }
        }

        return 0;
    }
    int collisionClientMenu()
    {
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                DrawMenu.setIsLoading(false);
                return -3;
            }
        }

        if(button(440,420,400,60,60,"Dolacz"))
        {
            who="client";
            DrawMenu.setIsLoading(true);
            tryConnect=true;
        }

        if(textButton(440,240,400,60,50,"IP:"+ip,18))
        {
            String t=ip;
            ip=textCtrString(ip,15);
            tryConnect=false;
            if(ip!=t)
            {
                DrawMenu.setIsLoading(false);
                tryConnect=false;
            }

        }
        if(textButton(440,330,400,60,60,"Port:"+port,10))
        {
            int t=port;
            port=textCtrInt(port+"",5);
            if(port!=t)
            {
                DrawMenu.setIsLoading(false);
                tryConnect=false;
            }

        }


        return 0;
    }
    int collisionSettings()
    {
        if(collision(1202,22,56,56) && IsMouseButtonPressed(0))
            return -4;

        if(collision(350,25,60,60) && IsMouseButtonDown(0))
        {
            menu.setTextColor(colorWheel.setColor(menu.getTextColor()));
            InitWindow(1280,720,"MENU");
            menu.reLoad();
        }

        if(collision(350,110,60,60) && IsMouseButtonDown(0))
        {
            menu.setButtonColor(colorWheel.setColor(menu.getButtonColor()));
            InitWindow(1280,720,"MENU");
            menu.reLoad();
        }
        if(collision(435,110,60,60) && IsMouseButtonDown(0))
        {
            menu.setButtonPressColor(colorWheel.setColor(menu.getButtonPressColor()));
            InitWindow(1280,720,"MENU");
            menu.reLoad();
        }
        if(collision(435,195,60,60) && IsMouseButtonDown(0))
        {
            menu.setxColor(colorWheel.setColor(menu.getxColor()));
            InitWindow(1280,720,"MENU");
            menu.reLoad();
        }
        if(collision(625,195,60,60) && IsMouseButtonDown(0))
        {
            menu.setoColor(colorWheel.setColor(menu.getoColor()));
            InitWindow(1280,720,"MENU");
            menu.reLoad();
        }


        return 0;
    }
    int textCtrInt(String what,int size)
    {

        int tmp=0;
        if(what.length()<size)
        {
            int x=GetCharPressed();

            if(x>=KEY_ZERO&&x<=KEY_NINE)
            {

                if(what.equals("0"))
                {
                    if(x>=KEY_ZERO&&x<=KEY_NINE)
                        what=(x-48)+"";
                }
                else
                {
                    if(x>=KEY_ZERO&&x<=KEY_NINE)
                        what=what+(x-48)+"";
                }
            }
        }else if(size==1)
        {
            int x=GetCharPressed();
            if(what.equals("0"))
            {
                if(x>=KEY_ZERO&&x<=KEY_NINE)
                    what=(x-48)+"";
            }
        }
        if(IsKeyPressed(KEY_BACKSPACE))
        {
            if(what.length()==1)
            {
                what=0+"";
            }
            else if(what.length()>1)
            {
                what=what.substring(0,what.length()-1);
            }

        }
        tmp=Integer.parseInt(what);

        return tmp;
    }
    String textCtrString(String what,int size)
    {
        if(what.length()<size)
        {
            int x=GetCharPressed();

            if(x>=KEY_ZERO&&x<=KEY_NINE)
            {

                if(what.equals("0"))
                {
                    if(x>=KEY_ZERO&&x<=KEY_NINE)
                        what=(x-48)+"";
                }
                else
                {
                    if(x>=KEY_ZERO&&x<=KEY_NINE)
                        what=what+(x-48)+"";
                }
            }
        }else if(size==1)
        {
            int x=GetCharPressed();
            if(what.equals("0"))
            {
                if(x>=KEY_ZERO&&x<=KEY_NINE)
                    what=(x-48)+"";
            }
        }
        if(IsKeyPressed(KEY_BACKSPACE))
        {
            if(what.length()>0)
            {
                what=what.substring(0,what.length()-1);
            }
        }
        if(IsKeyPressed(KEY_PERIOD))
        {

            if(what.length()>1)
            {
                what=what+".";
            }
        }

        return what;
    }

    boolean collision(int StartX,int StartY,int SizeX,int SizeY)
    {
        int x=GetMouseX();
        int y=GetMouseY();
        if(x>StartX && x<SizeX+StartX && y>StartY && y<StartY+SizeY)
            return true;
        return false;
    }

    boolean button(int x,int y,int sizeX,int sizeY,int textSize,String Text)
    {
        boolean collision = collision(x, y, sizeX, sizeY);
        DrawMenu.DrawButton(x,y,sizeX,sizeY,textSize,Text,collision);
        if(IsMouseButtonPressed(0))
            return collision;
        else
            return false;
    }

    boolean textButton(int x,int y,int sizeX,int sizeY,int textSize,String Text,int stringSize)
    {
        boolean collision = collision(x, y, sizeX, sizeY);
        DrawMenu.DrawCtrButton(x,y,sizeX,sizeY,textSize,Text,stringSize,collision);
        return collision;

    }

    Jaylib.Color[] getColors()
    {
        Jaylib.Color[] c=new Jaylib.Color[3];
        c[0]=menu.getTextColor();
        c[1]=menu.getxColor();
        c[2]=menu.getoColor();
        return c;
    }

}
