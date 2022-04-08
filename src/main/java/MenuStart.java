import com.raylib.Jaylib;
import java.io.File;
import java.net.InetAddress;
import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.WindowShouldClose;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

public class MenuStart extends Thread{
    String who;
    String ip="";
    int port=25565;
    int mapSize=23;
    int startTime=30;
    int moveTime=10;
    int[][] ship=new int[3][5];
    int[][] attackWhiteList=new int[2][6];
    int startGold=2555;
    int menuStage=1;
    int y=0;
    float MouseWheel;
    boolean end=false;
    boolean tryConnect=false;
    DrawMenu menu=null;
    int buttonUse=-1;
    boolean EndAndDontContet=false;
    boolean KristiFlag =false;
    Jaylib.Color[] colors;
    Texture []background;
    int usedBackground=0;
    int maxBackground=2;

    public void run() {
        File file = new File("Colors.txt");
        setStartData(file);
        InitWindow(1280,720,"MENU");
        SetTargetFPS(60);
        setBackdround();
        menu=new DrawMenu(port,ship,attackWhiteList,colors);
        numberOfShip();
        while (!WindowShouldClose() && menuStage>0 && !end) {

            BeginDrawing();
            ClearBackground(WHITE);
            DrawTexture(background[usedBackground],0,0,WHITE);
            menuStage+=collision();
            menu.Draw(menuStage,y,buttonUse);
            EndDrawing();
            System.gc();
        }
        if(!end)
        {
            EndAndDontContet=true;
        }
        System.out.println(""+mapSize);
        menu.clear();
        CloseWindow();
        end=true;
        saveColors(file);
    }

    void setStartData(File file)
    {
        try {
            Scanner readFile = new Scanner(file);
            usedBackground=readFile.nextInt();
            colors=new Jaylib.Color[5];
            for(int i=0;i<5;i++)
            {
                byte r=readFile.nextByte();
                byte g=readFile.nextByte();
                byte b=readFile.nextByte();
                byte a=readFile.nextByte();
                colors[i]=new Jaylib.Color(r,g,b,a);
            }
            readFile.close();
        }
        catch (Exception ignored)
        {

        }

        tryConnect=false;
        for(int i=0;i<5;i++)
            ship[0][i]=i+1;
        attackWhiteList[1][0]=100;
        attackWhiteList[1][1]=125;
        attackWhiteList[1][2]=125;
        attackWhiteList[1][3]=250;
        attackWhiteList[1][4]=500;
        attackWhiteList[1][5]=200;
        ship[2][0]=400;
        ship[2][1]=350;
        ship[2][2]=300;
        ship[2][3]=250;
        ship[2][4]=200;
    }
    void setBackdround()
    {
        background=new Texture[maxBackground];
        for(int i=0;i<maxBackground;i++)
        {
            background[i]=LoadTexture("resources/tla/tlo"+(i+1)+".png");
        }
    }
    void saveColors(File file)
    {

        try {
            PrintWriter in = new PrintWriter(file);
            in.println(usedBackground+" ");
            colors= menu.Color();
            for(int i=0;i<5;i++)
            {
                in.println(colors[i].r()+" "+colors[i].g()+" "+colors[i].b()+" "+colors[i].a());
            }
            in.close();
        }
        catch (Exception ignored)
        {

        }

    }
    int collision()
    {
        int tmp=0;
        if(IsKeyPressed(KEY_DOWN))
        {
            buttonUse++;
        }
        if(IsKeyPressed(KEY_UP)&&buttonUse>0)
        {
            buttonUse--;
        }
        if(IsKeyPressed(KEY_ENTER))
        {
            if(buttonUse==0)
            {
                switch (menuStage){
                    case 1:
                    case 2:
                        y=0;
                    case 3:
                        buttonUse=-1;
                        return -1;
                    case 4:
                        return -3;
                    case 5:
                        return -4;
                }
            }
        }
        switch (menuStage) {
            case 1 -> {
                tmp = collisionStart();
                if (tmp == 1)
                    MouseWheel = GetMouseWheelMove();
            }
            case 2 -> {
                tmp = collisionHostSettings();
                DrawMenu.setPort(port);
                if (tmp == 1) {
                    y = 0;
                    try {
                        ip = InetAddress.getLocalHost() + "";

                        int index = ip.indexOf('/');
                        ip = ip.substring(index + 1);
                        System.out.println(ip);
                        DrawMenu.setIp(ip);
                        who = "server";
                    } catch (Exception e) {

                    }
                    if (port < 25565)
                        port = 25565;
                    else if (port > 65535)
                        port = 65535;
                    DrawMenu.setPort(port);
                }
            }
            case 3 -> {
                tmp = collisionHostIP();
                if (tmp == -1) {
                    MouseWheel = GetMouseWheelMove();
                }
            }
            case 4 -> {
                tmp = collisionClientMenu();
                if (tmp == -1) {
                    tryConnect = false;
                }
            }
            case 5 -> tmp = collisionSettings();
        }
        if(IsKeyPressed(KEY_ENTER))
        {
            buttonUse=-1;
        }
        if(tmp!=0)
            buttonUse=-1;
        return tmp;
    }
    int collisionStart()
    {
        int buttonNumber=1;
        if(collision(1202,22,56,56) && IsMouseButtonPressed(0))
            return -1;

        if(button(415,165,450,100,75,"Hostuj",buttonNumber))
            return 1;
        buttonNumber++;
        if(button(415,295,450,100,75,"Dolacz",buttonNumber))
            return 3;
        buttonNumber++;
        if(button(415,425,450,100,75,"Ustawienia",buttonNumber))
            return 4;
        if(buttonNumber<buttonUse)
            buttonUse=-1;
        return 0;
    }
    int collisionHostSettings()
    {
        int buttonNumber=1;
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                y=0;
                return -1;
            }

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

        float wheel=GetMouseWheelMove()+MouseWheel;
        MouseWheel=GetMouseWheelMove();
        if(wheel!=0)
        {
            buttonUse=-1;
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

        if(textButton(365,25+y,60,60,40,""+mapSize,2,buttonNumber))
        {
            mapSize=textCtrInt(mapSize+"",2);
            numberOfShip();
        }
        buttonNumber++;
        if(button(500,25+y,350,60,40,"GameMode : "+(KristiFlag ?"1":"0"),buttonNumber))
        {
            if(IsMouseButtonPressed(0))
            {
                KristiFlag =!KristiFlag;
            }
        }
        buttonNumber++;
        if(textButton(550,110+y,60,60,40,""+startTime,2,buttonNumber))
        {
            startTime=textCtrInt(startTime+"",2);
        }
        buttonNumber++;

        if(textButton(275,195+y,60,60,40,""+moveTime,2,buttonNumber))
        {
            moveTime=textCtrInt(moveTime+"",2);
        }
        buttonNumber++;

        for(int i=0;i<5;i++)
        {
            textButton(145+i*110,425+y,110,60,40,""+ship[1][i],1,-8);
            /*if()
            {
                ship[1][i]=textCtrInt(ship[1][i]+"",1);
                DrawMenu.setShip(ship);
            }
            buttonNumber++;

            */
        }


        for(int i=0;i<5;i++)
        {
            if(textButton(145+i*110,485+y,110,60,40,""+ship[2][i],4,buttonNumber))
            {
                ship[2][i]=textCtrInt(ship[2][i]+"",4);
                DrawMenu.setShip(ship);
            }
            buttonNumber++;
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
            if(textButton(145+i*110,634+y,110,64,40,""+attackWhiteList[1][i],4,buttonNumber))
            {

                attackWhiteList[1][i]=textCtrInt(attackWhiteList[1][i]+"",4);
                DrawMenu.setAttackWhiteList(attackWhiteList);
            }
            buttonNumber++;

        }

        if(textButton(375,715+y,160,60,40,""+startGold,5,buttonNumber))
        {
            startGold=textCtrInt(startGold+"",5);
        }
        buttonNumber++;
        if(textButton(160,800+y,160,60,40,""+port,5,buttonNumber))
        {
            port=textCtrInt(port+"",5);
            DrawMenu.setPort(port);
        }
        buttonNumber++;
        if(button(25,885+y,350,80,80,"START",buttonNumber))
        {
            checkAndSetGameParamaters();

            return 1;
        }
        buttonNumber++;
        if(buttonNumber<buttonUse)
            buttonUse=-1;
        return 0;
    }
    void numberOfShip()
    {
        ship[1][4]=mapSize/12;
        ship[1][3]=mapSize/8;
        ship[1][2]=mapSize/6;
        ship[1][1]=mapSize/5;
        ship[1][0]=mapSize/4;
        DrawMenu.setShip(ship);
    }
    int collisionHostIP()
    {
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                ip="";
                DrawMenu.setIp(ip);
                buttonUse=-1;
                return -1;
            }
        }

        return 0;
    }
    int collisionClientMenu()
    {
        int buttonNumber=1;
        if(IsMouseButtonPressed(0))
        {
            if(collision(1202,22,56,56))
            {
                DrawMenu.setIsLoading(false);
                return -3;
            }
        }

        if(textButton(440,240,400,60,40,"IP:"+ip,18,buttonNumber))
        {
            String t=ip;
            ip=textCtrString(ip,15);
            tryConnect=false;
            if(!Objects.equals(ip, t))
            {
                DrawMenu.setIsLoading(false);
                tryConnect=false;
            }

        }
        buttonNumber++;

        if(textButton(440,330,400,60,40,"Port:"+port,10,buttonNumber))
        {
            int t=port;
            port=textCtrInt(port+"",5);
            if(port!=t)
            {
                DrawMenu.setIsLoading(false);
                tryConnect=false;
            }

        }
        buttonNumber++;
        if(button(440,420,400,60,60,"Dolacz",buttonNumber))
        {
            who="client";
            DrawMenu.setIsLoading(true);
            tryConnect=true;
        }

        if(buttonNumber<buttonUse)
            buttonUse=-1;

        return 0;
    }
    int collisionSettings()
    {
        int buttonNumber=1;
        if(collision(1202,22,56,56) && IsMouseButtonPressed(0))
            return -4;

        if(collision(350,25,60,60) && IsMouseButtonDown(0))
        {
            menu.setTextColor(colorWheel.setColor(menu.getTextColor()));
            InitWindow(1280,720,"MENU");
            setBackdround();
            menu.reLoad();
        }

        if(collision(350,110,60,60) && IsMouseButtonDown(0))
        {
            menu.setButtonColor(colorWheel.setColor(menu.getButtonColor()));
            InitWindow(1280,720,"MENU");
            setBackdround();
            menu.reLoad();
        }
        if(collision(435,110,60,60) && IsMouseButtonDown(0))
        {
            menu.setButtonPressColor(colorWheel.setColor(menu.getButtonPressColor()));
            InitWindow(1280,720,"MENU");
            setBackdround();
            menu.reLoad();
        }
        if(collision(435,195,60,60) && IsMouseButtonDown(0))
        {
            menu.setxColor(colorWheel.setColor(menu.getxColor()));
            InitWindow(1280,720,"MENU");
            setBackdround();
            menu.reLoad();
        }
        if(collision(625,195,60,60) && IsMouseButtonDown(0))
        {
            menu.setoColor(colorWheel.setColor(menu.getoColor()));
            InitWindow(1280,720,"MENU");
            setBackdround();
            menu.reLoad();
        }
        if(button(25,280,400,60,40,"Tlo:"+usedBackground,buttonNumber))
        {
            usedBackground++;
            if(usedBackground>=maxBackground)
                usedBackground=0;
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
    boolean button(int x,int y,int sizeX,int sizeY,int textSize,String Text,int buttonId)
    {
        boolean collision = collision(x, y, sizeX, sizeY);
        boolean press=buttonId==buttonUse;
        if(press)
        {
            if(y<0)
                this.y=this.y-y;
            if(y+sizeY>GetScreenHeight())
            {
                this.y-=20;
            }
        }
        if(IsMouseButtonPressed(0))
        {
            DrawMenu.DrawButton(x,y,sizeX,sizeY,textSize,Text,collision);

            return collision;
        }
        else if(press)
        {
            DrawMenu.DrawButton(x,y,sizeX,sizeY,textSize,Text,press);
            if(IsKeyPressed(KEY_ENTER))
                return true;
            return false;
        }
        else
        {
            DrawMenu.DrawButton(x,y,sizeX,sizeY,textSize,Text,collision);
        }
        return false;
    }
    boolean textButton(int x,int y,int sizeX,int sizeY,int textSize,String Text,int stringSize,int buttonId)
    {
        DrawMenu.DrawCtrButton(x,y,sizeX,sizeY,textSize,Text,stringSize,buttonId==buttonUse);
        if(buttonId==buttonUse && IsMouseButtonPressed(0))
        {
            buttonUse=-1;
        }
        if(buttonId==buttonUse)
        {
            if(y<0)
                this.y=y;
            if(y+sizeY>GetScreenHeight())
            {
                this.y-=20;
            }
        }
        boolean collision = collision(x, y, sizeX, sizeY);
        if(IsMouseButtonPressed(0) && collision && buttonId>=0)
        {
            if(!(buttonId==buttonUse))
                buttonUse=buttonId;
        }
        else if(buttonId==buttonUse)
        {
            return true;
        }
        return buttonId==buttonUse;

    }

    void checkAndSetGameParamaters()
    {
        if(mapSize<6)
        {
            mapSize=6;
            numberOfShip();
        }
        else if(mapSize>26) {
            mapSize = 26;
            numberOfShip();
        }
        if(moveTime<10)
            moveTime=10;
        else if(moveTime>30)
            moveTime=30;

        if(startTime<30)
            startTime=30;
        else if(startTime>90)
            startTime=90;

        int s=0;
        for(int i=0;i<5;i++)
        {
            s=ship[1][i];
        }
        if(s==0)
        {
            for(int i=0;i<5;i++)
            {
                ship[1][i]++;
            }
        }
    }

    Jaylib.Color[] getColors()
    {
        return colors;
    }

    boolean getEnd()
    {
        return end;
    }

    boolean getEndAndDontContet()
    {
        return EndAndDontContet;
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

}
