public class Multithreading extends Thread {
    boolean isOpponentLeft;
    boolean isMyMove;
    boolean isAttackTime;
    int placeShipTime;
    boolean isProgramEnd;
    boolean isAttack;
    int attackType;
    int numberOfAttack;
    int[][] attack = null;
    byte[] attackRes = null;
    Communication communication;

    Multithreading(Communication communication, boolean isOpponentLeft, boolean isMyMove, boolean isProgramEnd, boolean isAttackTime, int placeShipTime) {
        this.communication = communication;
        this.isOpponentLeft = isOpponentLeft;
        this.isMyMove = isMyMove;
        this.isProgramEnd = isProgramEnd;
        this.isAttackTime = isAttackTime;
        this.placeShipTime = placeShipTime;
    }

    boolean isPlaceShipStage() {
        return (!isAttackTime);
    }

    void setIsProgramEnd(boolean isProgramEnd) {
        this.isProgramEnd = isProgramEnd;
    }

    boolean getIsOpponentLeft() {
        return isOpponentLeft;
    }

    boolean getIsMyMove() {
        return isMyMove;
    }

    void setAttack(int[][] attack) {
        this.attack = attack;
    }

    void setIsAttack(boolean isAttack) {
        this.isAttack = isAttack;
    }

    int[][] getAttack() {
        return attack;
    }

    void setAttackType(int attackType) {
        this.attackType = attackType;
    }

    int getAttackType() {
        return attackType;
    }

    void setNumberOfAttack(int numberOfAttack) {
        this.numberOfAttack = numberOfAttack;
    }

    int getNumberOfAttack() {
        return numberOfAttack;
    }

    void setAttackRes(byte[] attackRes) {
        this.attackRes = attackRes;
    }

    byte[] getAttackRes() {
        return attackRes;
    }

    boolean getIsAttack() {
        return isAttack;

    }

    public void run() {
        while (!isOpponentLeft && !isProgramEnd) {

            isOpponentLeft = communication.isClose(isProgramEnd);
            if (isProgramEnd)
                break;
            if (isAttackTime && !isOpponentLeft && !isProgramEnd) {
                isMyMove = communication.isMyMove(isMyMove);

                if (isMyMove) {
                    myMove();
                }
                else
                {
                    waiting();
                }
            }

            if (!isAttackTime)
            {
                if(placeShipTime>0)
                {
                    placeShipTime--;
                    try {
                        sleep(1000);
                    }
                    catch (Exception e) {
                    }


                }
                else
                    isAttackTime= Boolean.parseBoolean("true");
            }
        }
    }
    void myMove()
    {
        if (isAttack) {
            communication.sendInformation("attack");
            System.out.println("MYMOVE");
            System.out.println("1");
            while (attackType < 0) {

            }
            communication.sendInformation(attackType + "");
            System.out.println("2");


            if (attackType != 5) {
                while (numberOfAttack < 0) {
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
                    }
                }
                System.out.println("3");
                communication.sendInformation(numberOfAttack + "");
                while (attack == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
                    }
                }
                for (int i = 0; i < numberOfAttack; i++) {
                    communication.sendInformation(attack[i][0] + "");
                    communication.sendInformation(attack[i][1] + "");
                }
                System.out.println("4");
                String tmp;
                byte [] tmpattackRes=new byte[numberOfAttack];
                for (int i = 0; i < numberOfAttack; i++) {
                    tmp=communication.getInformation();
                    tmpattackRes[i]=Byte.parseByte(tmp);
                }
                attackRes=tmpattackRes;

                System.out.println("5");
                while (attackRes != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
                    }
                }
                System.out.println("6");

            }
            else
            {
                System.out.println("3");
                attack=new int[1][2];
                String tmp = communication.getInformation();
                attack[0][0] = Integer.parseInt(tmp);
                tmp = communication.getInformation();
                attack[0][1] = Integer.parseInt(tmp);
                System.out.println("4");
                while (attack != null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
                    }
                }
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
            if (attackType != 5)
            {
                tmp = communication.getInformation();
                numberOfAttack = Integer.parseInt(tmp);
                attack = new int[numberOfAttack][2];
                for (int i = 0; i < numberOfAttack; i++) {
                    tmp = communication.getInformation();
                    attack[i][0] = Integer.parseInt(tmp);
                    tmp = communication.getInformation();
                    attack[i][1] = Integer.parseInt(tmp);
                }
                System.out.println("3");
                while (attackRes == null) {
                    try {
                        sleep(10);
                    }
                    catch (Exception e) {
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
                    catch (Exception e) {
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
}
