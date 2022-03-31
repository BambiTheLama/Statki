import com.raylib.Jaylib;
import com.raylib.Raylib;
import java.util.Random;
import java.util.Objects;
import static com.raylib.Jaylib.*;

public class MainGameCore {

    Draw draw;
    Communication communication;
    int numberOfShipToPlace=0;
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
    int cell;
    int sizeBetweenMaps=256;
    int startEnemyMapLocation;
    byte attackMode=0;
    int numberOfAttack=0;
    int enemyX=-1;
    int enemyY=-1;
    int enemyAttackMode;
    Multithreading multithreading;
    int [][]attack;
    int []numberOfBombs=new int[6];
    int numberOfShot=0;
    boolean continiuFlag=true;
    int raid=3;
    int[][] raidmap;
    boolean waitingFlag=false;
    int width=1280,height=720;
    int time;
    int shipType=-1;
    int moveTime=10;
    int startTime=10;
    int[][] ship;
    int[][] attackWhiteList;
    int startGold;
    boolean rotate=false;

    MainGameCore(Communication communication,String who,byte mapSize,int[][] ship,int[][] attackWhiteList,int moveTime,int startTime,int startGold, Jaylib.Color[] colors)
    {
        n=mapSize;
        this.ship = ship;
        this.attackWhiteList = attackWhiteList;
        this.moveTime = moveTime;
        this.startTime = startTime;
        this.startGold = startGold;
        this.communication=communication;
        cell=(22*20)/n;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        clear();



        multithreading=new Multithreading(communication,isOpponentLeft,isMyMove,isProgramEnd,moveTime,startTime);

        InitWindow(width, height, "Statki "+who);
        SetTargetFPS(60);

        draw=new Draw(n,width,height,eqSize,sizeBetweenEqAndMap,cell,sizeBetweenMaps,20,startEnemyMapLocation,colors,attackWhiteList);

        try{
            int tmp=0;
            for(int i=0;i<5;i++)
            {
                numberOfShipToPlace+=ship[1][i]*(i+1);
                tmp+=ship[1][i];
            }
            draw.setShipOnMap(tmp);

            for(int i=0;i<6;i++)
                numberOfBombs[i]=0;
        }catch(Exception ignored)
        {

        }
        numberOfShipAlive=numberOfShipToPlace;


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

    public void main(){

        multithreading.start();

        while (!WindowShouldClose()&& !isOpponentLeft && !isProgramEnd && !isSomeoneWin)
        {
            gameLoop();
            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw.draw(myMap,enemyMap,raid,attackMode,numberOfShot,raidmap,ship,rotate,shipType);
            EndDrawing();
            System.gc();
        }
        if(!isOpponentLeft && !isSomeoneWin)
        {
            isProgramEnd=true;
            multithreading.setIsProgramEnd(true);
            CloseWindow();
            return;
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

        while (!isProgramEnd &&!WindowShouldClose() )
        {

            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw.draw(myMap,enemyMap,raid,attackMode,numberOfShot,raidmap,ship,rotate,shipType);
            draw.gameEnd(isOpponentLeft,isSomeoneWin,lost);
            EndDrawing();
            System.gc();
        }
        draw.clear();
        draw=null;

        CloseWindow();

    }

    void gameLoop(){
        upData();
        drawUpData();
        if(collision()&&continiuFlag&&isMyMove&&!isPlacingShipTime)
        {
            shoot();
            multithreading.setNumberOfAttack(numberOfShot);
            multithreading.setAttack(attack);
            multithreading.setAttackType(attackMode);
            multithreading.setIsAttack(true);
            continiuFlag=false;
            if(attackMode>0 && attackMode!=5 && numberOfBombs[attackMode-1]<=0)
                attackMode=0;

        }
        else if(!continiuFlag&&isMyMove&&!isPlacingShipTime)
        {
            if(setShootRes())
            {
                continiuFlag=true;
                enemyAttackMode=-1;
                if(attack!=null)
                    attack=null;

                numberOfShot=0;
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
            time = multithreading.getPlaceShipTime();
            isPlacingShipTime= multithreading.getisPlacingShipTime();

            if(!isPlacingShipTime)
            {
                numberOfShipAlive=numberOfShipAlive-numberOfShipToPlace;
                shipType=-1;
            }

        }
        else
        {
            time = multithreading.getMoveTime();
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
                else if(enemyAttackMode!=5)
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
                else
                {
                    waitingFlag=false;
                }
            }
        }
        else
        {
            if(tmp != isMyMove && isMyMove)
                continiuFlag=true;
        }
        isSomeoneWin=multithreading.getEndGame();
    }

    void drawUpData(){
        draw.setIsPlacingShipTime(isPlacingShipTime);
        draw.setNumberOfShipToPlace(numberOfShipToPlace);

        if(isPlacingShipTime)
            draw.setTime(time);
        else
            draw.setTime(time);
        draw.setMyMove(isMyMove);
        draw.setNumberOfShipAlive(numberOfShipAlive);
        draw.setGold(startGold);
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
                collisionOnPlaceShipStage(x,y);
            }
            else
            {
                return collisionInAttackingStage(x,y);
            }

        }

        return false;
    }

    void collisionOnPlaceShipStage(int x,int y) {
        int startX=startEnemyMapLocation-sizeBetweenMaps+sizeBetweenEqAndMap;
        int startY=eqSize+sizeBetweenEqAndMap+n*cell/2-192;
        int mouseX=GetMouseX();
        int mouseY=GetMouseY();
        mouseY=mouseY-startY;
        mouseX=mouseX-startX;
        if(mouseX >= 0 && mouseX <= 128 && mouseY >= 0 &&mouseY <=320)
        {
            mouseY=mouseY/64;
            shipType=mouseY;
            if(ship[1][shipType]>0)
            {

            }
            else
            {
                shipType=-1;
            }
        }
        else if(mouseX >= 64 && mouseX <= 128 && mouseY >= 320 &&mouseY <=384)
        {
            rotate=!rotate;
        }
        else if(shipType!=-1)
        {
            mouseX=GetMouseX();
            mouseY=GetMouseY();
            mouseY=mouseY-(eqSize+sizeBetweenEqAndMap);
            mouseX=mouseX-(sizeBetweenEqAndMap);
            mouseX/=cell;
            mouseY/=cell;
            if(mouseX >= 0 && mouseX < n && mouseY >= 0 && mouseY < n && myMap[mouseY][mouseX] == 0)
            {
                int tmp=shipType;
                if(placeShip(mouseX,mouseY))
                {
                    int tmpx=mouseX*cell+sizeBetweenEqAndMap;
                    int tmpy=mouseY*cell+eqSize+sizeBetweenEqAndMap;
                    int tmph;
                    int tmpw;
                    int r=0;
                    if(rotate)
                    {
                        tmpx+=cell;
                        tmpy+=cell;
                        r=90;
                        switch (tmp)
                        {
                            case 1:
                            case 2:
                                tmpx+=cell;
                                break;
                            case 3:

                            case 4:
                                tmpx+=2*cell;
                                break;
                        }
                    }
                    else
                    {
                        switch (tmp)
                        {
                            case 2:
                            case 3:
                                tmpy-=cell;
                                break;
                            case 4:
                                tmpy-=2*cell;
                                break;
                        }
                    }
                    tmpw=cell;
                    tmph=cell*(tmp+1);
                    draw.setShipOnMapPosition(tmp,tmpx,tmpy,tmpw,tmph,r);
                }
            }

        }

        x=GetMouseX();
        y=GetMouseY();
        int size=100;
        for(int i=0;i<6;i++)
        {
            int tmpX=startEnemyMapLocation+i%3*160;
            int tmpY=eqSize+sizeBetweenEqAndMap+i/3*160;

            if(0==attackWhiteList[0][i] && x>tmpX && x<tmpX+size && y>tmpY && y<tmpY+size && startGold >=attackWhiteList[1][i])
            {
                startGold-=attackWhiteList[1][i];
                numberOfBombs[i]++;
                draw.setNumberOfBombs(numberOfBombs);
            }
        }
    }

    boolean collisionInAttackingStage(int x,int y) {
        if(isMyMove)
        {
            if(x >= 0 && x <= n*cell && y >= 0 && y <= n*cell)
            {
                x=x/cell;
                y=y/cell;

                if((enemyMap[y][x]==0 || attackMode==5 ||attackMode==6) && attackMode!=4)
                {
                    enemyX=x;
                    enemyY=y;
                    if(attackMode>0)
                        numberOfBombs[attackMode-1]--;
                    return true;
                }
                else if(attackMode==4 && raid==1)
                {
                    enemyX=x;
                    enemyY=y;
                    setRaidMap();
                    raid=3;
                    numberOfBombs[attackMode-1]--;
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
        }

        x=GetMouseX();
        y=GetMouseY();
        for(int i=0;i<6;i++)
        {
            if(x>= startEnemyMapLocation+60+i*80 && x<= startEnemyMapLocation+104+i*80  && y >= 8 && y <= 72 && numberOfBombs[i]>0)
            {
                if(attackMode==4&&raid!=3)
                {

                }
                else
                {
                    if(attackMode!=i)
                        attackMode=(byte)(i+1);
                    else
                        attackMode=0;
                }
            }
        }

        return false;
    }

    boolean placeShip(int mouseX,int mouseY) {
        int [][]tmp=new int[5][2];
        for(int i=0;i<5;i++)
        {
            tmp[i][0]=-1;
            tmp[i][1]=-1;
        }
        int i=0;
        switch (shipType) {
            case 4:
                if (!rotate)
                {
                    if(mouseY-2>=0 && myMap[mouseY-2][mouseX]==0)
                    {
                        tmp[i][0]=mouseY-2;
                        tmp[i][1]=mouseX;
                        i++;
                    }
                    else
                        break;
                }
                else
                {
                    if(mouseX-2>=0 && myMap[mouseY][mouseX-2]==0)
                    {
                        tmp[i][0]=mouseY;
                        tmp[i][1]=mouseX-2;
                        i++;
                    }
                    else
                        break;
                }
            case 3:
                if (!rotate)
                {
                    if(mouseY+2<n && myMap[mouseY+2][mouseX]==0)
                    {
                        tmp[i][0]=mouseY+2;
                        tmp[i][1]=mouseX;
                        i++;
                    }
                    else
                        break;
                }
                else
                {
                    if(mouseX+2<n && myMap[mouseY][mouseX+2]==0)
                    {
                        tmp[i][0]=mouseY;
                        tmp[i][1]=mouseX+2;
                        i++;
                    }
                    else
                        break;
                }
            case 2:
                if (!rotate)
                {
                    if(mouseY-1>=0 && myMap[mouseY-1][mouseX]==0)
                    {
                        tmp[i][0]=mouseY-1;
                        tmp[i][1]=mouseX;
                        i++;
                    }
                    else
                        break;
                }
                else
                {
                    if(mouseX-1>=0 && myMap[mouseY][mouseX-1]==0)
                    {
                        tmp[i][0]=mouseY;
                        tmp[i][1]=mouseX-1;
                        i++;
                    }
                    else
                        break;
                }
            case 1:
                if (!rotate)
                {
                    if(mouseY+1<n && myMap[mouseY+1][mouseX]==0)
                    {
                        tmp[i][0]=mouseY+1;
                        tmp[i][1]=mouseX;
                        i++;
                    }
                    else
                        break;
                }
                else
                {
                    if(mouseX+1<n && myMap[mouseY][mouseX+1]==0)
                    {
                        tmp[i][0]=mouseY;
                        tmp[i][1]=mouseX+1;
                        i++;
                    }
                    else
                        break;
                }
            case 0:
            {
                if(myMap[mouseY][mouseX]==0)
                {
                    tmp[i][0]=mouseY;
                    tmp[i][1]=mouseX;
                    i++;

                    for(int j=0;j<i;j++)
                    {
                        myMap[tmp[j][0]][tmp[j][1]]=(byte)(3+shipType);
                        placeShipColision(tmp[j][1],tmp[j][0]);

                    }
                    ship[1][shipType]--;
                    numberOfShipToPlace-=(shipType+1);
                    if (ship[1][shipType] <= 0)
                        shipType = -1;

                    return true;
                }

            }
        }
        return false;
    }

    void placeShipColision(int x,int y) {
        if(x-1>=0 && myMap[y][x-1]==0)
            myMap[y][x-1]=8;
        if(x+1<n && myMap[y][x+1]==0)
            myMap[y][x+1]=8;
        if(y-1>=0 && myMap[y-1][x]==0)
            myMap[y-1][x]=8;
        if(y+1<n && myMap[y+1][x]==0)
            myMap[y+1][x]=8;
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
        switch (attackMode) {
            case 0 -> {
                numberOfShot++;
                attack = new int[numberOfShot][2];
                attack[0][0] = enemyY;
                attack[0][1] = enemyX;
            }
            case 1 -> {
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        if (isOnEnemyMap(enemyX - 1 + i, enemyY - 1 + j))
                            numberOfShot++;
                attack = new int[numberOfShot][2];
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        if (isOnEnemyMap(enemyX - 1 + i, enemyY - 1 + j)) {
                            attack[s][0] = enemyY - 1 + j;
                            attack[s][1] = enemyX - 1 + i;
                            s++;
                        }
            }
            case 2 -> {
                for (int i = 0; i < 5; i++) {
                    if (isOnEnemyMap(enemyX - 2 + i, enemyY - 2 + i))
                        numberOfShot++;
                    if (isOnEnemyMap(enemyX + 2 - i, enemyY - 2 + i))
                        numberOfShot++;
                }
                attack = new int[numberOfShot][2];
                for (int i = 0; i < 5; i++) {
                    if (isOnEnemyMap(enemyX - 2 + i, enemyY - 2 + i)) {
                        attack[s][0] = enemyY - 2 + i;
                        attack[s][1] = enemyX - 2 + i;
                        s++;
                    }
                    if (isOnEnemyMap(enemyX + 2 - i, enemyY - 2 + i)) {
                        attack[s][0] = enemyY - 2 + i;
                        attack[s][1] = enemyX + 2 - i;
                        s++;
                    }
                }
            }
            case 3 -> {
                for (int i = 0; i < 5; i++) {
                    if (isOnEnemyMap(enemyX - 2 + i, enemyY))
                        numberOfShot++;
                    if (isOnEnemyMap(enemyX, enemyY - 2 + i))
                        numberOfShot++;
                }
                attack = new int[numberOfShot][2];
                for (int i = 0; i < 5; i++) {
                    if (isOnEnemyMap(enemyX - 2 + i, enemyY)) {
                        attack[s][0] = enemyY;
                        attack[s][1] = enemyX - 2 + i;
                        s++;
                    }
                    if (isOnEnemyMap(enemyX, enemyY - 2 + i)) {
                        attack[s][0] = enemyY - 2 + i;
                        attack[s][1] = enemyX;
                        s++;
                    }
                }
            }
            case 4 -> {
                attack = raidmap;
                raidmap = null;
            }
            case 5 -> numberOfShot++;
            case 6 -> {
                byte[][] tmp = new byte[n * n][2];
                int k = 0;
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++) {
                        if (enemyMap[i][j] == 0) {
                            tmp[k][0] = (byte) i;
                            tmp[k][1] = (byte) j;
                            k++;
                        }
                    }
                numberOfShot = Math.min(k, 10);
                attack = new int[numberOfShot][2];
                for (int i = 0; i < numberOfShot; i++) {
                    int temp = rand.nextInt(k);
                    attack[s][0] = tmp[temp][0];
                    attack[s][1] = tmp[temp][1];
                    s++;
                    if (temp == k - 1) {
                        k--;
                    } else {
                        k--;
                        tmp[temp][0] = tmp[k][0];
                        tmp[temp][1] = tmp[k][1];
                    }
                }
            }
        }
    }

    boolean setShootRes(){
        if(attackMode!=5)
        {
            byte []res= multithreading.getAttackRes();
            System.out.println("set Shoot Res 1 attackmode :"+attackMode);
            if(res==null)
            {
                return false;
            }

            System.out.println("set Shoot Res 2");
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
            System.out.println("set Shoot Res 3");
            multithreading.setAttack(attack);
        }
        else
        {
            System.out.println("set Shoot Res 1 attackmode :"+attackMode);

            if(multithreading.getAttack()!=null)
            {
                try{
                    attack= multithreading.getAttack();
                    enemyMap[attack[0][0]][attack[0][1]]=2;
                    multithreading.setAttack(null);
                    if(numberOfBombs[attackMode-1]<=0)
                        attackMode=0;
                }
                catch (Exception e)
                {
                    return false;
                }

            }
            else
            {
                return false;
            }


            System.out.println("set Shoot Res 2");

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
            System.out.println("set Shoot 1 numberOfAttack "+numberOfAttack+" enemyAttackMode "+enemyAttackMode);

            byte[] attackRes=new byte[numberOfAttack];
            for(int i=0;i<numberOfAttack;i++)
            {

                if(myMap[attack[i][0]][attack[i][1]] == 0 || myMap[attack[i][0]][attack[i][1]] == 8 )
                    myMap[attack[i][0]][attack[i][1]]=1;
                else if(myMap[attack[i][0]][attack[i][1]] == 3|| myMap[attack[i][0]][attack[i][1]] == 4|| myMap[attack[i][0]][attack[i][1]] == 5
                        || myMap[attack[i][0]][attack[i][1]] == 6|| myMap[attack[i][0]][attack[i][1]] == 7)
                {
                    myMap[attack[i][0]][attack[i][1]]=2;
                    numberOfShipAlive--;
                }


                attackRes[i]=myMap[attack[i][0]][attack[i][1]];

            }
            System.out.println("set Shoot 2 ");
            multithreading.setAttackRes(attackRes);
            isOpponentAttack=false;
        }
        else
        {
            System.out.println("set Shoot 1 enemy attack mode"+enemyAttackMode);
            int s=0;
            int[][]ship=new int[numberOfShipAlive][2];
            for(int i=0;i<n;i++)
            {
                for (int j=0;j<n;j++)
                {
                    if(myMap[i][j]==3||myMap[i][j]==4||myMap[i][j]==5||myMap[i][j]==6||myMap[i][j]==7)
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
            System.out.println("set Shoot 2 ");
            int tmp=0;
            if(s>1)
            {
                tmp= rand.nextInt(s);
            }
            else
            {
                tmp=0;
            }
            System.out.println("set Shoot 3 ");
            attack=new int [1][2];
            attack[0][0]=ship[tmp][0];
            attack[0][1]=ship[tmp][1];
            myMap[attack[0][0]][attack[0][1]]=2;
            numberOfShipAlive--;
            multithreading.setAttack(attack);
            attack=null;
            System.out.println("set Shoot 4 ");

        }
    }

    boolean isOnEnemyMap(int x,int y) {
        return x >= 0 && x < n && y >= 0 && y < n && enemyMap[y][x] == 0;
    }

}