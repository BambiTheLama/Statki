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

    Multithreading(Communication communication, boolean isOpponentLeft, boolean isMyMove, boolean isProgramEnd, int placeShipTime) {
        this.communication = communication;
        this.isOpponentLeft = isOpponentLeft;
        this.isMyMove = isMyMove;
        this.isProgramEnd = isProgramEnd;

        this.placeShipTime = placeShipTime;
    }

    @Override
    public void run() {
        while (!isOpponentLeft && !isProgramEnd) {

            isOpponentLeft = communication.isClose(false);

            if(numberOfShip <= 0 && isAttackTime)
            {
                endGame=true;
            }

            win = communication.isClose(endGame);
            if (isProgramEnd || isOpponentLeft || endGame || win)
                break;
            if (isAttackTime) {
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
                    isAttackTime= true;
            }
        }
        System.out.println("WYSZLEM");
        if(isProgramEnd)
        {
            isOpponentLeft = communication.isClose(true);
        }
        endGame=true;


    }

    void myMove()
    {
        if (isAttack) {
            attackRes=null;
            communication.sendInformation("attack");
            System.out.println("MYMOVE");
            System.out.println("1");
            while (attackType < 0) {
                try {
                    sleep(10);
                }
                catch (Exception ignored) {
                }

            }
            System.out.println("mymove attack type "+attackType);
            communication.sendInformation(attackType + "");
            System.out.println("2");


            if (attackType != 5) {
                while (numberOfAttack < 0) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                System.out.println("3");
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
                System.out.println("4");

                byte [] tmpAttackRes=new byte[numberOfAttack];
                for (int i = 0; i < numberOfAttack; i++) {
                    String tmp=communication.getInformation();
                    tmpAttackRes[i]=Byte.parseByte(tmp);
                }
                attackRes=tmpAttackRes;

                System.out.println("5");
                while (attackRes != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                System.out.println("6");

            }
            else
            {
                attack=null;
                System.out.println("q3");
                int[][] tmpAttack=new int[1][2];
                String tmp = communication.getInformation();
                tmpAttack[0][0] = Integer.parseInt(tmp);
                tmp = communication.getInformation();
                tmpAttack[0][1] = Integer.parseInt(tmp);
                System.out.println("q4");
                attack=tmpAttack;
                System.out.println("q5");
                while (attack != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }
                }
                System.out.println("q6");
            }
            isMyMove = false;
            if(attack!=null)
                attack = null;
            if(attackRes!=null)
                attackRes=null;
            isAttack=false;
            attackType = -1;
            numberOfAttack = -1;
            System.out.println("7");

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
            isAttack=true;
            System.out.println("1");
            tmp = communication.getInformation();
            attackType = Integer.parseInt(tmp);
            System.out.println("2");
            System.out.println("waiting attack type "+attackType);
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
                System.out.println("3");
                while (attackRes == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception ignored) {
                    }

                }
                System.out.println("4");
                for (int i = 0; i < numberOfAttack; i++) {
                    communication.sendInformation(attackRes[i] + "");
                }
                System.out.println("5");

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
                communication.sendInformation(attack[0][0] + "");
                communication.sendInformation(attack[0][1] + "");
                System.out.println("4");
            }
            System.out.println("6");
            isMyMove = true;
            if(attack!=null)
                attack = null;
            if(attackRes!=null)
                attackRes=null;
            isAttack=false;
            attackType = -1;
            numberOfAttack = -1;
        }

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


}
