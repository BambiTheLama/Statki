import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import static com.raylib.Jaylib.*;
import java.io.File;
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
    boolean KristiFlag;
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
    int moveTime;
    int startTime;
    int[][] ship;
    int[][] attackWhiteList;
    int startGold;
    boolean rotate=false;
    File file;
    PrintWriter toFile;
    MainGameCore(Communication communication,String who,byte mapSize,int[][] ship,int[][] attackWhiteList,int moveTime,int startTime,int startGold, Jaylib.Color[] colors,boolean KristiFlag)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String data=dtf.format(now)+"_"+who;
        file=new File("logi/gra_nr_"+data+".txt");

        try
        {
            toFile = new PrintWriter(file);
            saveToFile(dtf.format(now));
        }catch (Exception ignored)
        {

        }
        n=mapSize;
        this.ship = ship;
        this.attackWhiteList = attackWhiteList;
        this.moveTime = moveTime;
        this.startTime = startTime;
        this.startGold = startGold;
        this.communication=communication;
        this.KristiFlag=KristiFlag;
        cell=(22*20)/n;
        myMap=new byte[n][n];
        enemyMap=new byte[n][n];
        startEnemyMapLocation=n*cell+sizeBetweenEqAndMap+sizeBetweenMaps;
        clear();



        multithreading=new Multithreading(communication,isOpponentLeft,isMyMove,isProgramEnd,moveTime,startTime);

        InitWindow(width, height, "Statki "+who);
        SetTargetFPS(60);

        draw=new Draw(n,width,height,eqSize,sizeBetweenEqAndMap,cell,sizeBetweenMaps,20,startEnemyMapLocation,colors,attackWhiteList,KristiFlag);
        int tmp=0;

        for(int i=0;i<5;i++)
        {
            numberOfShipToPlace+=ship[1][i]*(i+1);
            tmp+=ship[1][i];
        }
        draw.setShipOnMap(tmp);

        for(int i=0;i<6;i++)
            numberOfBombs[i]=0;

        numberOfShipAlive=numberOfShipToPlace;

        saveToFile("Map size :"+mapSize+" NumberOfShip "+ tmp+" Start gold "+startGold+" gamemode "+(KristiFlag?1:0));
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

    public boolean main(){

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
            saveToFile("YOU CLOSE WINDOW");
            endFile();
            return false;
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
        saveToFile("YOU "+(lost?"LOST":"WIN")+(isOpponentLeft?" Oponet left":""));
        DrawMenu menu=new DrawMenu(0,null,null,null);
        while (!isProgramEnd &&!WindowShouldClose() )
        {
            boolean press=(GetMouseX()>430 && GetMouseX()<830 && GetMouseY()>375 && GetMouseY()<425);

            if(IsKeyPressed(KEY_ENTER) || (press && IsMouseButtonPressed(0)))
            {
                CloseWindow();

                endFile();
                return true;
            }

            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw.draw(myMap,enemyMap,raid,attackMode,numberOfShot,raidmap,ship,rotate,shipType);
            draw.gameEnd(isOpponentLeft,isSomeoneWin,lost);

            DrawMenu.DrawButton(430,375,400,50,40,"Zagraj ponownie",press);
            EndDrawing();
            System.gc();
        }
        draw.clear();
        draw=null;

        CloseWindow();
        endFile();
        return false;

    }

    void gameLoop(){
        upData();
        drawUpData();
        if(collision() && continiuFlag && isMyMove && !isPlacingShipTime)
        {
            shoot();
            multithreading.setNumberOfAttack(numberOfShot);
            multithreading.setAttack(attack);
            multithreading.setAttackType(attackMode);
            multithreading.setIsAttack(true);
            continiuFlag=false;


        }
        else if(!continiuFlag && isMyMove && !isPlacingShipTime)
        {
            if(setShootRes())
            {
                continiuFlag=true;
                enemyAttackMode=-1;
                if(attack!=null)
                    attack=null;

                numberOfShot=0;

                if((attackMode>0 && KristiFlag  && numberOfBombs[attackMode-1]<=0 )||(attackMode>0 && !KristiFlag && startGold-attackWhiteList[1][attackMode-1]<0))
                    attackMode=0;
            }

        }
        else if(continiuFlag && !isPlacingShipTime && !isMyMove && !waitingFlag)
        {
            if(isOpponentAttack)
            {
                setShoot();

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
                shipType=4;
                random();
                numberOfShipAlive=numberOfShipAlive-numberOfShipToPlace;
                shipType=-1;
                if(KristiFlag)
                {
                    for(int i=0;i<6;i++)
                    {
                        saveToFile("YOU HAVE "+numberOfBombs[i]+" bombs type "+(i+1));
                    }
                    saveToFile("YOU HAVE "+startGold+" gold");
                }
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
                    setToDraw(mouseX,mouseY,tmp);
                    saveToFile("YOU PLACE SHIP ("+(tmp+1)+") AT "+((char)('a'+mouseX))+" "+(mouseY+1)+" Rotate "+rotate);
                }
            }

        }
        if(KristiFlag)
        {
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
                    if(attackMode>0 && KristiFlag)
                        numberOfBombs[attackMode-1]--;
                    else if(!KristiFlag && attackMode>0)
                    {
                        startGold-=attackWhiteList[1][attackMode-1];
                    }

                    return true;
                }
                else if(attackMode==4 && raid==1)
                {
                    enemyX=x;
                    enemyY=y;
                    setRaidMap();
                    raid=3;
                    if(KristiFlag)
                        numberOfBombs[attackMode-1]--;
                    else
                        startGold-=attackWhiteList[1][attackMode-1];
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
            if((numberOfBombs[i]>0 &&KristiFlag)||(startGold-attackWhiteList[1][i]>=0 && !KristiFlag))
                if(x>= startEnemyMapLocation+60+i*80 && x<= startEnemyMapLocation+104+i*80  && y >= 8 && y <= 72)
                {
                    if(attackMode==4&&raid!=3)
                    {

                    }
                    else
                    {
                        if(attackMode!=(i+1))
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }
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
                    {
                        return false;
                    }

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

    void setToDraw(int mouseX,int mouseY,int ship)
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
            switch (ship)
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
            switch (ship)
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
        tmph=cell*(ship+1);
        draw.setShipOnMapPosition(ship,tmpx,tmpy,tmpw,tmph,r);
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
        saveToFile("\nMY MOVE");
        saveToFile("Attack Mode "+attackMode+" "+(attackMode==4||attackMode==5?"":(char)('a'+enemyX)+""+(1+enemyY)));
        if(attackMode!=4)
            numberOfShot=0;
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
            if(res==null)
            {
                return false;
            }

            startGold+=multithreading.getGold();
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
            multithreading.setAttack(attack);
            saveToFile("RES FROM ATTACK Number OF SHOT : "+numberOfShot);
            for(int i=0;i<numberOfShot;i++) {
                saveToFile("RES = "+res[i]+" "+((char)('a'+attack[i][1]))+ (1+attack[i][0]));
            }
            saveToFile("Gold :"+startGold);
        }
        else
        {
            if(multithreading.getAttack()!=null)
            {
                try{
                    attack= multithreading.getAttack();
                    enemyMap[attack[0][0]][attack[0][1]]=2;
                    saveToFile("RES = "+2+" "+((char)('a'+attack[0][1]))+ (1+attack[0][0]));
                    startGold+=multithreading.getGold();
                    saveToFile("Gold :"+startGold);
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


        }
        if(attack!=null)
        {
            attack=null;
        }
        return true;

    }

    void setShoot(){
        saveToFile("\nENEMY MOVE \nattack type"+enemyAttackMode);
        if(enemyAttackMode!=5)
        {

            byte[] attackRes=new byte[numberOfAttack];
            int goldToSend=0;
            for(int i=0;i<numberOfAttack;i++)
            {

                if(myMap[attack[i][0]][attack[i][1]] == 0 || myMap[attack[i][0]][attack[i][1]] == 8 )
                    myMap[attack[i][0]][attack[i][1]]=1;
                else if(myMap[attack[i][0]][attack[i][1]] >= 3  && myMap[attack[i][0]][attack[i][1]] <= 7)
                {
                    int type=myMap[attack[i][0]][attack[i][1]]-3;
                    goldToSend+=ship[2][type]/(type+1);
                    myMap[attack[i][0]][attack[i][1]]=2;
                    numberOfShipAlive--;
                }
                attackRes[i]=myMap[attack[i][0]][attack[i][1]];

            }
            for(int i=0;i<numberOfAttack;i++)
            {
                saveToFile("Res = "+attackRes[i]+" "+(char)('a'+attack[i][0])+attack[i][0]);
            }
            saveToFile("Gold to send "+goldToSend);
            multithreading.setGold(goldToSend);
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
                    if(myMap[i][j]>=3 && myMap[i][j]<=7)
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
            int type=myMap[attack[0][0]][attack[0][1]]-3;
            int goldToSend=(this.ship[2][type])/(type+1);
            myMap[attack[0][0]][attack[0][1]]=2;
            numberOfShipAlive--;


            saveToFile("Res = "+2+" "+(char)('a'+ship[tmp][1])+ship[tmp][0]);

            saveToFile("Gold to send "+goldToSend);
            multithreading.setGold(goldToSend);
            multithreading.setAttack(attack);
            attack=null;

        }
    }

    boolean isOnEnemyMap(int x,int y) {
        return x >= 0 && x < n && y >= 0 && y < n && enemyMap[y][x] == 0;
    }

    void random() {
        saveToFile("Random Ships :");
        while(shipType>=0)
        {
            if(shipType >=0 &&ship[1][shipType]>0)
            {
                int a=rand.nextInt(n);
                int b=rand.nextInt(n);
                int tmp=shipType;
                rotate=rand.nextBoolean();
                if(placeShip(a,b))
                {
                    setToDraw(a,b,tmp);
                    saveToFile("ShipType = "+(tmp+1)+" ("+((char)('a'+a))+" "+(1+b)+") isRotate "+rotate);
                }
                shipType=tmp;

            }
            else if(shipType >=0)
            {
                shipType--;

            }

        }
    }

    int[] getNumberOfBombs()
    {
        return numberOfBombs;
    }

    void setNumberOfBombs(int[] bombs) {
        numberOfBombs=bombs;
        draw.setNumberOfBombs(bombs);
    }

    void saveToFile(String s)
    {
        if(toFile==null) return;
        toFile.println(s);
        toFile.flush();
    }
    void endFile()
    {
        if(toFile==null)return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        saveToFile(dtf.format(now));
        saveToFile("END");
        toFile.close();
    }

}