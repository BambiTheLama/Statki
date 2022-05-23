import com.raylib.Jaylib;

public class GameCore {
    Map map;
    CommunicationInterface communication;
    Collision collision;
    DrawBoard draw;
    boolean placeShipStage=true;
    boolean flagKristi;
    int gold;
    int[] ship;
    int[][] attackWhiteList;
    int[] bombs;
    int shipUse=0;
    int bombUse=0;
    boolean wait=false;
    boolean rotate=false;
    boolean myMove=false;
    String time;
    boolean canAttack=true;
    boolean end=false;
    boolean win=false;
    boolean lost=false;
    DrawEndScrean endGame;

    GameCore(CommunicationInterface communication, int n, int []ship,int[] goldFromShip,
             int[][] attackWhiteList, int startGold, boolean flagKristi,String who,int[] bombs)
    {
        this.communication = communication;
        this.ship=ship;
        this.attackWhiteList=attackWhiteList;
        gold=startGold;
        this.flagKristi=false;
        MapParameters parameters=new MapParameters(n);
        collision=new Collision(parameters);

        int numberOfShip=0;
        int numberOfShipParts=0;
        for(int i=0;i<5;i++)
        {
            numberOfShip+=ship[i];
            numberOfShipParts+=ship[i]*(i+1);
        }

        map=new Map(n,goldFromShip,numberOfShipParts);
        Jaylib.InitWindow(1280,720,"Statki "+who);
        communication.startC();

        this.flagKristi=flagKristi;
        this.bombs =bombs;
        draw=new DrawBoard(parameters,numberOfShip,attackWhiteList,flagKristi,this.bombs);
        endGame = new DrawEndScrean();

    }

    boolean startGame()
    {
        while(!Jaylib.WindowShouldClose())
        {
            updata();
            collision();
            Jaylib.BeginDrawing();
            draw();
            Jaylib.EndDrawing();
            if(end && endGame.newGame == true)
            {
                break;
            }
            if(end && endGame.endGame == true)
                break;


        }
        Jaylib.CloseWindow();
        return false;
    }

    void draw()
    {
        Jaylib.ClearBackground(Jaylib.WHITE);
        draw.draw(map,placeShipStage,myMove);
        draw.drawTime(time);
        draw.drawGold(gold);
        if(placeShipStage)
        {
            draw.drawShip(ship,shipUse,rotate);
            draw.endTurn(myMove);
            draw.drawShipMouse(collision.getMouseX(), collision.getMouseY(), shipUse,rotate);
            if(flagKristi)
                draw.drawBombsShop();
        }
        else
        {
            draw.drawBombs(bombUse);
            draw.drawHowManyShip(map.getNumberOfShip());
            if(myMove)
            {
                draw.drawBombsMouse(collision.getMouseX(), collision.getMouseY(), bombUse);
                if(bombUse==4)
                    draw.drawPlaceAttack(SetAttack.getAttack(),3-SetAttack.getShot());
            }
        }
        if(end)
        {
            endGame.draw(win,lost);
        }
    }

    void updata()
    {
        time = communication.getTime();
        end  = communication.getEnd();
        if(end)
            win=communication.getWin();
        if(placeShipStage)
        {
            placeShipStage= !communication.isAttackStage();
            if(!placeShipStage)
            {
                map.randShip(ship,draw);
                return;
            }
            if(!myMove)
                myMove = collision.endPlaceShip();
            if(myMove)
            {
                map.randShip(ship,draw);
                communication.setToSend("Ready");
                placeShipStage=false;
                return;
            }
            return;
        }

        if(map.getNumberOfShip() <= 0)
        {
            communication.setLost();
            lost=true;
        }

        myMove=communication.getMyMove();

        if(canAttack == myMove==false)
            canAttack=true;

        if(wait==myMove==true)
            wait=false;

        if(!myMove && !wait)
            setRes();
        if(myMove)
            waitingForRes();
        if(!end)
            return;

    }

    void collision()
    {
        collision.upData();

        if(end)
            endGame.collison(collision.getMouseX(),collision.getMouseY(),collision.getMousePress());

        if(placeShipStage)
        {
            shipPlaceStage();
            return;
        }
        setBombUse();

        if(canAttack && myMove)
        {
            if(attack())
                canAttack=false;
        }
    }

    boolean attack()
    {
        String posEnemyMap = collision.attackCollision();

        if(!posEnemyMap.equals("") && bombUse==5)
        {
            communication.setToSend("Shot");
            return true;
        }

        if(posEnemyMap.equals(""))
            return false;

        String []pos=SetAttack.checkAttack(posEnemyMap,bombUse,map);
        if(pos==null)
            return false;

        communication.setAttackPos(pos,SetAttack.numberOfAttack);

        return true;
    }

    void shipPlaceStage()
    {
        String posMyMap = collision.placeShipCollision();

        if(map.placeShip(shipUse,rotate,posMyMap,draw))
            ship[shipUse]--;

        shipUse= collision.checkShipCollision(shipUse);
        if(shipUse >= 5)
        {
            shipUse-=5;
            rotate=!rotate;
        }
        if(shipUse >=0 && ship[shipUse]<=0)
            shipUse=-1;
        if(!flagKristi)
            return;
        int buy=collision.buyAttackCollision();
        if(buy<=0)
            return;
        if(attackWhiteList[0][buy-1]==1)
            return;
        if(gold-attackWhiteList[1][buy-1]>=0)
        {
            bombs[buy-1]++;
            gold-=attackWhiteList[1][buy-1];
        }

    }

    void checkSzpieg()
    {
        if(!communication.getSzpieg())
            return;
        if(wait==true)
            return;
        String pos=map.szpieg();
        int gold=map.getGold();
        communication.setSzpieg(pos,gold);
        wait=true;
        if(map.getNumberOfShip()<=0)
        {
            communication.setLost();
        }

    }

    void waitingForRes()
    {
        int []res=communication.getAttackRes();
        if(res==null)
            return;
        int size=communication.getSize();
        String []pos= communication.getAttackPos();
        if(pos==null)
            return;
        gold+=communication.getGold();
        map.shotRes(pos,res,size);
        communication.def();
        communication.setToSend("Accepted");
        if(bombUse == 0 && res[0] == 2)
            communication.setMyMove(myMove);
        if(flagKristi)
        {
            if(bombUse>0)
                bombs[bombUse-1]--;
        }
        else
        {
            if(bombUse > 0)
                gold-=attackWhiteList[1][bombUse-1];
        }

        setBombUse();
        canAttack=true;
    }

    void setRes()
    {
        String []pos= communication.getAttackPos();
        int n=communication.getSize();
        if(pos==null)
        {
            checkSzpieg();
            return;
        }
        int[] res=map.shot(pos,n);
        int gold=map.getGold();
        communication.setAttackRes(res,n,gold);
        if(res==null)
            return;
        if(map.getNumberOfShip()<=0)
        {
            communication.setLost();
            communication.end();
        }
    }

    void setBombUse()
    {
        bombUse=collision.attackModeCollision(bombUse);

        if(bombUse<0)
            bombUse=0;
        if(bombUse==0)
            return;
        if(flagKristi)
        {
            if(bombs[bombUse-1]<=0)
                bombUse=0;
            return;
        }
        if(attackWhiteList[0][bombUse-1]==1)
        {
            bombUse=0;
            return;
        }
        if(gold-attackWhiteList[1][bombUse-1]<0)
            bombUse=0;
    }
}
