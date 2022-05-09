public class Multithreading extends Thread {
    private boolean isOpponentLeft;
    private boolean isMyMove;
    private boolean isAttackTime=false;
    private int placeShipTime;
    private boolean isProgramEnd=false;
    private boolean isAttack;
    private int attackType;
    private int numberOfAttack;
    private int[][] attack = null;
    private byte[] attackRes = null;
    private int numberOfShip;
    Communication communication;
    private boolean endGame=false;
    private boolean win=false;
    private int moveTime;
    private int moveTimeTmp;
    private int startTime;
    private int gold=0;

    Multithreading(Communication communication, boolean isOpponentLeft, boolean isMyMove, boolean isProgramEnd,int moveTime,int startTime) {
        this.communication = communication;
        setMultithreading(isOpponentLeft,isMyMove,isProgramEnd,moveTime,startTime);
    }

    void setMultithreading(boolean isOpponentLeft, boolean isMyMove, boolean isProgramEnd,int moveTime,int startTime) {
        this.isOpponentLeft = isOpponentLeft;
        this.isMyMove = isMyMove;
        this.isProgramEnd = isProgramEnd;
        this.moveTime = moveTime*100;
        this.startTime = startTime;
        this.placeShipTime=startTime;
    }

    @Override
    public void run() {

        while (!isOpponentLeft && !isProgramEnd) {

            if(numberOfShip <= 0 && isAttackTime)
            {
                endGame=true;
            }
            if (isAttackTime) {
                try {
                    sleep(10);
                    moveTimeTmp--;
                    if(moveTimeTmp<0)
                    {
                        isMyMove=!isMyMove;
                        moveTimeTmp=moveTime;
                    }
                }
                catch (Exception ignored) {
                }
                isMyMove = communication.isMyMove(isMyMove);
                if (isMyMove) {
                    myMove();
                }
                else
                {
                    waiting();
                }

            }
            else
            {
                if(placeShipTime>0)
                {
                    placeShipTime--;
                    try {
                        sleep(1000);
                    }
                    catch (Exception ignored) {
                    }


                }
                else
                {
                    isAttackTime= true;
                    moveTimeTmp=moveTime;
                }

            }
            isOpponentLeft = communication.isClose(false);
            win = communication.isClose(endGame);
            if(win || endGame)
            {

            }
            if(win || isOpponentLeft || isProgramEnd ||endGame)
                break;

        }
        if(isProgramEnd)
        {
            isOpponentLeft = communication.isClose(true);
            communication.close();
        }
        endGame=true;
    }

    void myMove() {
        if (isAttack) {
            boolean move;
            attackRes=null;
            communication.sendInformation("attack");
            while (attackType < 0) {
                try {
                    sleep(10);
                }
                catch (Exception ignored) {
                }

            }
            communication.sendInformation(attackType + "");

            if (attackType != 5) {
                while (numberOfAttack < 0) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                communication.sendInformation(numberOfAttack + "");
                while (attack == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                for (int i = 0; i < numberOfAttack; i++) {
                    communication.sendInformation(attack[i][0] + "");
                    communication.sendInformation(attack[i][1] + "");
                }

                byte [] tmpAttackRes=new byte[numberOfAttack];
                for (int i = 0; i < numberOfAttack; i++) {
                    String tmp=communication.getInformation();
                    tmpAttackRes[i]=Byte.parseByte(tmp);
                }
                String tmp=communication.getInformation();
                gold=Integer.parseInt(tmp);
                move=checkTurn(tmpAttackRes[0]);
                attackRes=tmpAttackRes;

                while (attackRes != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
            }
            else
            {
                attack=null;
                int[][] tmpAttack=new int[1][2];
                String tmp = communication.getInformation();
                tmpAttack[0][0] = Integer.parseInt(tmp);
                tmp = communication.getInformation();
                tmpAttack[0][1] = Integer.parseInt(tmp);
                tmp = communication.getInformation();
                gold = Integer.parseInt(tmp);
                attack=tmpAttack;
                while (attack != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                move=checkTurn(0);

            }
            isMyMove=move;
            setDefult();
        }
        else
        {
            communication.sendInformation("wait");
        }
    }

    void waiting() {

        String tmp = communication.getInformation();
        if (tmp.equals("attack"))
        {
            boolean move=isMyMove;
            isAttack=true;
            tmp = communication.getInformation();
            attackType = Integer.parseInt(tmp);
            if (attackType != 5)
            {

                tmp = communication.getInformation();
                numberOfAttack = Integer.parseInt(tmp);

                int [][]tmpattack = new int[numberOfAttack][2];
                for (int i = 0; i < numberOfAttack; i++) {
                    tmp = communication.getInformation();
                    tmpattack[i][0] = Integer.parseInt(tmp);
                    tmp = communication.getInformation();
                    tmpattack[i][1] = Integer.parseInt(tmp);
                }

                attack=tmpattack;
                while (attackRes == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }

                }
                for (int i = 0; i < numberOfAttack; i++) {
                    communication.sendInformation(attackRes[i] + "");
                }
                communication.sendInformation(gold+"");
                move=checkTurn(attackRes[0]);
            }
            else
            {
                while (attack == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                move=checkTurn(0);
                communication.sendInformation(attack[0][0] + "");
                communication.sendInformation(attack[0][1] + "");
                communication.sendInformation(gold+"");
            }
            isMyMove=move;
            setDefult();
        }

    }

    void setDefult() {
        if(attack!=null)
            attack = null;
        if(attackRes!=null)
            attackRes=null;
        isAttack=false;
        attackType = -1;
        numberOfAttack = -1;
        moveTimeTmp=moveTime;
        gold=0;
    }

    void setIsProgramEnd(boolean isProgramEnd) {
        this.isProgramEnd = isProgramEnd;
    }

    void setAttack(int[][] attack) {
        this.attack = attack;
    }

    void setAttackType(int attackType) {
        this.attackType = attackType;
    }

    void setIsAttack(boolean isAttack) {
        this.isAttack = isAttack;
    }

    void setNumberOfAttack(int numberOfAttack) {
        this.numberOfAttack = numberOfAttack;
    }

    void setNumberOfShip(int numberOfShip)
    {
        this.numberOfShip = numberOfShip;
    }

    void setAttackRes(byte[] attackRes) {
        this.attackRes = attackRes;
    }

    void setGold(int gold){this.gold=gold;}

    boolean getIsOpponentLeft() {
        return isOpponentLeft;
    }

    boolean getIsMyMove() {
        return isMyMove;
    }

    int[][] getAttack() {
        return attack;
    }

    int getAttackType() {
        return attackType;
    }

    int getNumberOfAttack() {
        return numberOfAttack;
    }

    byte[] getAttackRes() {
        return attackRes;
    }

    boolean getIsAttack() {
        return isAttack;
    }

    int getMoveTime()
    {
        return moveTimeTmp;
    }

    int getPlaceShipTime()
    {
        return placeShipTime;
    }

    boolean getWin()
    {
        return win;
    }

    boolean getEndGame(){return endGame;}

    boolean getisPlacingShipTime(){return (!isAttackTime);}

    int getGold(){return gold;}

    boolean checkTurn(int Res)
    {
        if(attackType==0 && Res==2)
            return isMyMove;
        return !isMyMove;
    }
}
