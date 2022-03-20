import com.raylib.Jaylib;

import java.net.InetAddress;
import java.nio.charset.Charset;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.WindowShouldClose;

public class MenuStart {

    static String who;
    static String ip="";
    static int port;
    static int mapSize=0;
    static int startTime=0;
    static int moveTime=0;
    static int[][] ship=new int[3][5];
    static int[][] attackWhiteList=new int[2][6];
    static int startGold=0;
    static int menuStage=1;
    static int y=0;
    static float MouseWheel;
    static Connect connect=null;
    static Communication communication=null;



    public static void main(String[] arg) {


        for(int i=0;i<5;i++)
            ship[0][i]=i+1;
        InitWindow(1280,720,"MENU");

        DrawMenu menu=new DrawMenu(port,mapSize,startTime,moveTime,ship,attackWhiteList,startGold);
        while (!WindowShouldClose()&&menuStage>0&&menuStage!=69) {

            menuStage+=collision();

            BeginDrawing();
            ClearBackground(WHITE);
            menu.Draw(menuStage,y);
            DrawText(menuStage+"",0,0,20,BLACK);
            EndDrawing();
            System.gc();
        }
        menu.clear();
        menu=null;
        CloseWindow();
        if(menuStage==69)
        {
            try{
                communication=connect.getCommunication();
                if(who.equals("server"))
                    sendGameInformation();
                else if(who.equals("client"))
                    getGameInformation();
                MainGameCore start=new MainGameCore(communication,who, (byte) mapSize,ship,attackWhiteList,moveTime,startTime,startGold);
                start.main();
                System.out.println("koniec");
                return;
            }
            catch (Exception e)
            {
            }
        }
        System.gc();
    }

    static void sendGameInformation() {
        communication.sendInformation(mapSize+"");
        communication.sendInformation(moveTime+"");
        communication.sendInformation(startTime+"");
        communication.sendInformation(startGold+"");
        for(int i=0;i<5;i++)
        {

            communication.sendInformation(ship[0][i]+"");
            communication.sendInformation(ship[1][i]+"");
            communication.sendInformation(ship[2][i]+"");
        }
        for(int i=0;i<6;i++)
        {
            communication.sendInformation(attackWhiteList[0][i]+"");
            communication.sendInformation(attackWhiteList[1][i]+"");
        }
    }

    static void getGameInformation() {

        String tmp=communication.getInformation();
        mapSize=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        moveTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startGold=Integer.parseInt(tmp);

        ship=new int[3][5];
        for(int i=0;i<5;i++)
        {
            tmp=communication.getInformation();
            ship[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[1][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[2][i]=Integer.parseInt(tmp);
        }
        for(int i=0;i<6;i++)
        {
            tmp=communication.getInformation();
            attackWhiteList[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            attackWhiteList[1][i]=Integer.parseInt(tmp);
        }
    }

    static int collision()
    {
        int tmp=0;

        switch (menuStage){
            case 1:
                if(IsMouseButtonPressed(0))
                    tmp=collisionStart();
                if(tmp==1)
                    MouseWheel=GetMouseWheelMove();
                break;
            case 2:

                tmp=collisionHostSettings();
                if(tmp==1)
                {
                    if(port<49152)
                        port=49152;
                    else if(port>65535)
                        port=65535;
                    DrawMenu.setPort(port);
                    connect=new Connect("server",port,ip);
                    connect.start();
                }
                break;
            case 3:

                tmp=collisionHostIP();
                if(tmp==-1)
                {
                    MouseWheel=GetMouseWheelMove();
                    connect.stop();
                    connect=null;
                }

                break;
            case 4:

                tmp=collisionClientMenu();
                break;
            case 5:
                if(IsMouseButtonPressed(0))
                    tmp=collisionSettings();
                break;

        }
        return tmp;
    }
    static  boolean collision(int StartX,int StartY,int SizeX,int SizeY)
    {
        int x=GetMouseX();
        int y=GetMouseY();
        if(x>StartX && x<SizeX+StartX && y>StartY && y<StartY+SizeY)
            return true;
        return false;
    }
    static int collisionStart()
    {

        if(collision(1202,22,56,56))
            return -1;
        if(collision(415,165,450,100))
            return 1;
        if(collision(415,295,450,100))
            return 3;
        if(collision(415,425,450,100))
            return 4;

        return 0;
    }
    static int collisionHostSettings()
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


            if(collision(25,885+y,350,80))
            {

                try{
                    ip=InetAddress.getLocalHost()+"";

                    int index = ip.indexOf( '/' );
                    ip=ip.substring(index+1,ip.length());
                    System.out.println(ip);
                    DrawMenu.setIp(ip);
                    who="server";
                    connect=new Connect(who,port,ip);
                    connect.start();
                }
                catch (Exception e)
                {

                }
                return 1;

            }
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
            if(collision(145+i*110,365+60*1+y,110,60))
            {
                ship[1][i]=textCtrInt(ship[1][i]+"",1);
                DrawMenu.setShip(ship);
            }
        for(int i=0;i<5;i++)
            if(collision(145+i*110,365+60*2+y,110,60))
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
            if(collision(145+i*110,634+y,110,64)) {

                attackWhiteList[1][i]=textCtrInt(attackWhiteList[1][i]+"",4);
                DrawMenu.setAttackWhiteList(attackWhiteList);
            }

        }



        if(collision(365,25+y,60,60))
        {
            mapSize=textCtrInt(mapSize+"",2);
            DrawMenu.setMapSize(mapSize);
        }
        if(collision(550,110+y,60,60))
        {
            startTime=textCtrInt(startTime+"",2);
            DrawMenu.setStartTime(startTime);
        }
        if(collision(275,195+y,60,60))
        {
            moveTime=textCtrInt(moveTime+"",2);
            DrawMenu.setMoveTime(moveTime);
        }
        if(collision(375,715+y,160,60))
        {
            startGold=textCtrInt(startGold+"",5);
            DrawMenu.setStartGold(startGold);
        }
        if(collision(160,800+y,160,60))
        {
            port=textCtrInt(port+"",5);
            DrawMenu.setPort(port);
        }
        return 0;
    }
    static int collisionHostIP()
    {
        if(IsMouseButtonPressed(0))
            if(collision(1202,22,56,56))
            {
                ip="";
                DrawMenu.setIp(ip);
                connect.stop();
                return -1;
            }

        if(connect.getConnected())
        {
            System.out.println("dziala");
            communication=connect.getCommunication();
            System.out.println("dziala2");
            return 66;
        }


        else
        {
            connect=new Connect(who,port,ip);
            connect.start();
        }




        return 0;
    }
    static int collisionClientMenu()
    {
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                if(connect!=null)
                {
                    try{
                        connect.stop();
                    }
                    catch (Exception e)
                    {

                    }
                    connect=null;
                    DrawMenu.setIsLoading(false);
                }
                return -3;
            }

            if(collision(440,420,400,60))
            {
                connect=null;
                who="client";
                connect=new Connect(who,port,ip);
                connect.start();
                DrawMenu.setIsLoading(true);

            }

        }
        if(connect!=null)
        {

            if(connect.getConnected())
                return 65;

        }

        if(collision(440,240,400,60))
        {
            ip=textCtrString(ip,15);
            DrawMenu.setIp(ip);
        }
        if(collision(440,330,400,60))
        {
            port=textCtrInt(port+"",5);
            DrawMenu.setPort(port);
        }


        return 0;
    }
    static int collisionSettings()
    {
        if(collision(1202,22,56,56))
            return -4;
        return 0;
    }
    static int textCtrInt(String what,int size)
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
    static String textCtrString(String what,int size)
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
}
