import com.raylib.Raylib;
import java.util.Random;
import java.io.IOException;
import java.util.Objects;

import static com.raylib.Jaylib.*;
import static java.lang.Thread.sleep;

public class MainGameCore {

    Draw draw;
    Communication communication;
    int placeShipTime=60*10;
    int numberOfShipToPlace;
    int numberOfShipAlive;
    boolean isPlacingShipTime=true;
    boolean isOpponentAttack=false;
    boolean isOpponentLeft=false;
    boolean isProgramEnd=false;
    boolean isSomeoneWin=false;
    boolean isMyMove;
    boolean lost=false;
    Random rand=new Random();
    byte[][] myMap;
    byte[][] enemyMap;
    byte n;
    int eqSize=100;
    int sizeBetweenEqAndMap=50;
    int cell=20;
    int sizeBetweenMaps=200;
    int startEnemyMapLocation;
    byte attackMode=1;
    int numberOfAttack=0;
    int enemyX=-1;
    int enemyY=-1;
    int enemyAttackMode;
    Multithreading multithreading;
    int [][]attack;
    int numberOfShot=0;
    boolean continiuFlag=true;
    int raid=3;
    int[][] raidmap;
    boolean waitingFlag=false;

    MainGameCore() {
        n=24;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        numberOfShipToPlace=10;
        numberOfShipAlive=10;
        clear();
    }

    MainGameCore(byte mapSize,int numberOfShip) {
        n=mapSize;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        numberOfShipToPlace=numberOfShip;
        numberOfShipAlive=numberOfShip;
        clear();
    }

    void clear() {
        for(byte i=0;i<n;i++)
        {
            for(byte j=0;j<n;j++)
            {
                myMap[i][j]=0;
                enemyMap[i][j]=0;
            }
        }

    }

    public void main(String[] args) throws IOException {
        int port= Integer.parseInt(args[1]);
        String ip="";

        if(Objects.equals(args[0], "server"))
        {
            isMyMove= Boolean.TRUE;
        }
        else if(Objects.equals(args[0], "client"))
        {
            isMyMove= Boolean.FALSE;
            ip=args[2];
        }

        int width=1280,height=720;

        communication=new Communication(args[0],port,ip);

        draw=new Draw(n,width,height);

        InitWindow(width, height, "Statki "+args[0]);
        SetTargetFPS(60);

        multithreading=new Multithreading(communication,isOpponentLeft,isMyMove,isProgramEnd,10);

        multithreading.start();



        while (!WindowShouldClose()&&isOpponentLeft==false&&isProgramEnd==false&&isSomeoneWin==false)
        {
            gameLoop();
            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw.draw(myMap,enemyMap,isPlacingShipTime,isMyMove,placeShipTime,numberOfShipToPlace,numberOfShipAlive,raid,attackMode,numberOfShot,raidmap);
            EndDrawing();

        }

        if(isOpponentLeft==false&&isSomeoneWin==false)
        {
            isProgramEnd=true;
            multithreading.setIsProgramEnd(true);
        }
        else
        {
            if(isOpponentLeft)
            {
                lost=false;
            }
            else
            {
                lost=!multithreading.getWin();
            }

        }

        if(isProgramEnd==false) {
            while (!WindowShouldClose())
            {
                BeginDrawing();
                ClearBackground(RAYWHITE);
                draw.draw(myMap,enemyMap,isPlacingShipTime,isMyMove,placeShipTime,numberOfShipToPlace,numberOfShipAlive,raid,attackMode,numberOfShot,raidmap);
                draw.gameEnd(isOpponentLeft,isSomeoneWin,lost);
                EndDrawing();
            }
        }


        CloseWindow();
    }

    void gameLoop() throws IOException {
        upData();
        if(continiuFlag&&collision()&&isMyMove&&!isPlacingShipTime)
        {
            shoot();
            multithreading.setNumberOfAttack(numberOfShot);
            multithreading.setAttack(attack);
            multithreading.setAttackType(attackMode);
            multithreading.setIsAttack(true);
            continiuFlag=false;

        }
        else if(!continiuFlag&&isMyMove&&!isPlacingShipTime)
        {
            if(setShootRes())
            {
                continiuFlag=true;
            }

        }
        else if(continiuFlag&&!isPlacingShipTime&&!isMyMove&&!waitingFlag)
        {
            if(isOpponentAttack)
            {
                setShoot();
                continiuFlag=false;
            }
        }


    }

    void upData() {
        if(isPlacingShipTime)
        {
            if(placeShipTime>0)
                placeShipTime=multithreading.getPlaceShipTime();
            else
            {
                isPlacingShipTime=false;
                numberOfShipAlive=numberOfShipAlive-numberOfShipToPlace;
            }

        }
        else
        {

        }
        multithreading.setNumberOfShip(numberOfShipAlive);
        boolean tmp=isMyMove;
        isMyMove=multithreading.getIsMyMove();
        isOpponentLeft=multithreading.getIsOpponentLeft();
        if(continiuFlag&&!isMyMove)
        {
            isOpponentAttack=multithreading.getIsAttack();
            if(isOpponentAttack)
            {

                enemyAttackMode=multithreading.getAttackType();
                if(enemyAttackMode<0)
                    waitingFlag=true;
                else
                {
                    numberOfAttack=multithreading.getNumberOfAttack();
                    if(numberOfAttack<0)
                        waitingFlag=true;
                    else
                    {
                        attack=multithreading.getAttack();
                        if(attack==null)
                            waitingFlag=true;
                        else
                        {
                            multithreading.setAttack(null);
                            waitingFlag=false;
                        }
                    }
                }
            }
        }
        else
        {
            if(tmp != isMyMove && isMyMove == true)
                continiuFlag=true;
        }
        isSomeoneWin=multithreading.getEndGame();
    }

    boolean collision() {

        if(Raylib.IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            int x=Raylib.GetMouseX();
            int y=Raylib.GetMouseY();
            x=x-startEnemyMapLocation;
            y=y-(eqSize+sizeBetweenEqAndMap);
            if(isPlacingShipTime)
            {
                x=Raylib.GetMouseX();
                x=x-sizeBetweenEqAndMap;

                if(x>=0&&x<=n*cell&&y>=0&&y<=n*cell)
                {
                    x=x/cell;
                    y=y/cell;
                    if(myMap[y][x]==0 && numberOfShipToPlace>0)
                    {
                        myMap[y][x]=3;
                        numberOfShipToPlace--;
                    }
                    else if(myMap[y][x]==3)
                    {
                        myMap[y][x]=0;
                        numberOfShipToPlace++;
                    }
                }
            }
            else if(isMyMove)
            {
                if(x >= 0 && x <= n*cell && y >= 0 && y <= n*cell)
                {
                    x=x/cell;
                    y=y/cell;



                    if((enemyMap[y][x]==0 || attackMode==5 ||attackMode==6) && attackMode!=4)
                    {
                        enemyX=x;
                        enemyY=y;
                        return true;
                    }
                    else if(attackMode==4 && raid==1)
                    {
                        enemyX=x;
                        enemyY=y;
                        setRaidMap();
                        raid=3;
                        return true;
                    }
                    else if(attackMode==4 && raid == 3)
                    {

                        numberOfShot=0;
                        enemyX=x;
                        enemyY=y;
                        setRaidMap();
                        raid--;

                    }
                    else if(attackMode==4)
                    {
                        enemyX=x;
                        enemyY=y;
                        setRaidMap();
                        raid--;
                    }

                }
                x=GetMouseX();
                y=GetMouseY();
                for(int i=0;i<=6;i++)
                {
                    if(x>= startEnemyMapLocation-20+i*80 && x<= startEnemyMapLocation+44+i*80  && y >= 8 && y <= 72)
                    {
                        if(attackMode==4&&raid!=3)
                        {

                        }
                        else
                        {
                            attackMode=(byte)i;
                        }

                    }
                }
            }
        }
        return false;
    }

    void setRaidMap() {
        int s=numberOfShot;
        for(int i=0;i<3;i++)
        {
            if(isOnEnemyMap(enemyX-1+i,enemyY))
                numberOfShot++;
            if(isOnEnemyMap(enemyX,enemyY-1+i))
                numberOfShot++;
        }
        int [][]tmpraidmap=new int[numberOfShot][2];
        for(int i=0;i<s;i++)
        {
            tmpraidmap[i][0]=raidmap[i][0];
            tmpraidmap[i][1]=raidmap[i][1];
        }
        for(int i=0;i<3;i++)
        {

            if(isOnEnemyMap(enemyX-1+i,enemyY))
            {
                tmpraidmap[s][0]=enemyY;
                tmpraidmap[s][1]=enemyX-1+i;
                s++;
            }
            if(isOnEnemyMap(enemyX,enemyY-1+i))
            {
                tmpraidmap[s][0]=enemyY-1+i;
                tmpraidmap[s][1]=enemyX;
                s++;
            }

        }
        raidmap=tmpraidmap;
    }

    void shoot(){
        if(attackMode!=4)
            numberOfShot=0;
        attack = new int[0][];
        int s=0;
        switch(attackMode)
        {
            case 0:
                numberOfShot++;
                attack=new int[numberOfShot][2];
                attack[0][0]=enemyY;
                attack[0][1]=enemyX;
                break;
            case 1:

                for(int i=0;i<3;i++)
                    for(int j=0;j<3;j++)
                        if(isOnEnemyMap(enemyX-1+i,enemyY-1+j))
                            numberOfShot++;

                attack=new int[numberOfShot][2];
                for(int i=0;i<3;i++)
                    for(int j=0;j<3;j++)
                        if(isOnEnemyMap(enemyX-1+i,enemyY-1+j))
                        {
                            attack[s][0]=enemyY-1+j;
                            attack[s][1]=enemyX-1+i;
                            s++;
                        }
                break;
            case 2:
                for(int i=0;i<5;i++)
                {
                    if(isOnEnemyMap(enemyX-2+i,enemyY-2+i))
                        numberOfShot++;
                    if(isOnEnemyMap(enemyX+2-i,enemyY-2+i))
                        numberOfShot++;
                }
                attack=new int[numberOfShot][2];
                for(int i=0;i<5;i++)
                {
                    if(isOnEnemyMap(enemyX-2+i,enemyY-2+i))
                    {
                        attack[s][0]=enemyY-2+i;
                        attack[s][1]=enemyX-2+i;
                        s++;
                    }
                    if(isOnEnemyMap(enemyX+2-i,enemyY-2+i))
                    {
                        attack[s][0]=enemyY-2+i;
                        attack[s][1]=enemyX+2-i;
                        s++;
                    }
                }
                break;
            case 3:
                for(int i=0;i<5;i++)
                {
                    if(isOnEnemyMap(enemyX-2+i,enemyY))
                        numberOfShot++;
                    if(isOnEnemyMap(enemyX,enemyY-2+i))
                        numberOfShot++;
                }
                attack=new int[numberOfShot][2];
                for(int i=0;i<5;i++)
                {
                    if(isOnEnemyMap(enemyX-2+i,enemyY))
                    {
                        attack[s][0]=enemyY;
                        attack[s][1]=enemyX-2+i;
                        s++;
                    }
                    if(isOnEnemyMap(enemyX,enemyY-2+i))
                    {
                        attack[s][0]=enemyY-2+i;
                        attack[s][1]=enemyX;
                        s++;
                    }
                }
                break;
            case 4:
                attack=raidmap;
                raidmap=null;
                break;
            case 5:

                break;
            case 6:
                byte[][] tmp=new byte[n*n][2];
                int k=0;
                for(int i=0;i<n;i++)
                    for (int j=0;j<n;j++)
                    {
                        if(enemyMap[i][j]==0)
                        {
                            tmp[k][0]=(byte)i;
                            tmp[k][1]=(byte)j;
                            k++;
                        }
                    }
                if(k<10)
                {
                    numberOfShot=k;
                }
                else
                {
                    numberOfShot=10;
                }
                attack=new int[numberOfShot][2];

                for(int i=0;i<numberOfShot;i++){
                    int temp=rand.nextInt(k);
                    attack[s][0]=tmp[temp][0];
                    attack[s][1]=tmp[temp][1];
                    s++;
                    if(temp==k-1)
                    {
                        k--;
                    }
                    else
                    {
                        k--;
                        tmp[temp][0]=tmp[k][0];
                        tmp[temp][1]=tmp[k][1];
                    }
                }
                break;

        }
    }

    boolean setShootRes(){
        if(attackMode!=5)
        {
            byte []res= multithreading.getAttackRes();
            System.out.println("B1");
            if(res==null)
            {
                try {
                    sleep(10);
                }
                catch (Exception ignored) {
                }
                return false;
            }

            System.out.println("B2");
            multithreading.setAttackRes(null);
            for(int i=0;i<numberOfShot;i++)
            {
                if(res[i]==-1)
                {

                }
                else
                {
                    enemyMap[attack[i][0]][attack[i][1]]=res[i];
                }

            }
            System.out.println("B3");
            multithreading.setAttack(attack);
        }
        else
        {
            System.out.println("B1");

            if(multithreading.getAttack()!=null)
            {
                attack= multithreading.getAttack();
                enemyMap[attack[0][0]][attack[0][1]]=2;
                multithreading.setAttack(null);
            }
            else
            {
                return false;
            }


            System.out.println("B2");

        }
        if(attack!=null)
        {
            attack=null;
        }
        return true;

    }

    void setShoot(){
        if(enemyAttackMode!=5)
        {
            System.out.println("A1");
            System.out.println("numberOfAttack "+numberOfAttack);
            byte[] attackRes=new byte[numberOfAttack];
            for(int i=0;i<numberOfAttack;i++)
            {

                if(myMap[attack[i][0]][attack[i][1]] == 0 )
                    myMap[attack[i][0]][attack[i][1]]=1;
                else if(myMap[attack[i][0]][attack[i][1]] == 3)
                {
                    myMap[attack[i][0]][attack[i][1]]=2;
                    numberOfShipAlive--;
                }


                attackRes[i]=myMap[attack[i][0]][attack[i][1]];

            }
            System.out.println("A2");
            multithreading.setAttackRes(attackRes);
            isOpponentAttack=false;
        }
        else
        {
            int s=0;
            int[][]ship=new int[numberOfShipAlive][2];
            for(int i=0;i<n;i++)
            {
                for (int j=0;j<n;j++)
                {
                    if(myMap[i][j]==3)
                    {
                        ship[s][0]=i;
                        ship[s][1]=j;
                        s++;
                        if(s==numberOfShipAlive-1)
                            break;
                    }
                }
                if(s==numberOfShipAlive-1)
                    break;
            }
            int tmp=0;
            if(s>1)
            {
                tmp= rand.nextInt(s);
            }
            else
            {
                tmp=0;
            }

            attack=new int [1][2];
            attack[0][0]=ship[tmp][0];
            attack[0][1]=ship[tmp][1];
            myMap[attack[0][0]][attack[0][1]]=2;
            numberOfShipAlive--;
            multithreading.setAttack(attack);
            attack=null;
            while(multithreading.getAttack()!=null)
            {
                try {
                    sleep(10);
                }
                catch (Exception e) {
                }
            }

        }
    }

    boolean isOnEnemyMap(int x,int y) {
        if(x>=0 && x<n && y>=0 && y<n && enemyMap[y][x]==0)
            return true;
        return false;
    }

}