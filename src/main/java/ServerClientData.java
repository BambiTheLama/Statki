public class ServerClientData {
    int attackRes[];
    String attack[];
    int gold;
    boolean win=false;
    boolean lost=false;
    int size;
    boolean szpieg=false;

    ServerClientData()
    {
        defoliate();
    }

    int readOrder(CheckOrder  order)
    {
        int myOrder=order.readOrder();

        if(szpieg && myOrder!=3)
            szpieg=false;
        switch (myOrder)
        {

            case 1:
                setAttackRes(order.readAttackResPosition(), order.getSize(), order.readGold());
                break;

            case 2:
                setAttackPos(order.readAttackPosition(),order.getSize());
                break;
            case 3:
                szpieg=true;
                break;
            case 4:
                setSzpiegPos(order);
                break;
            case 5:
                defoliate();
                break;
            case 7:
                win=true;
                break;
        }
        return myOrder;
    }

    void setAttackPos(String[] attack,int size)
    {
        this.attack=attack;
        this.size=size;
    }

    void setAttackRes(int []attackRes,int size,int gold)
    {
        this.attackRes=attackRes;
        this.size=size;
        this.gold=gold;
    }

    void setSzpiegPos(CheckOrder order)
    {
        String tmp=order.readSzpiegPos();
        if(tmp.isEmpty())
            return;
        attackRes=new int[1];
        attackRes[0]=2;
        size=1;
        attack=new String[1];
        attack[0] = tmp;
        gold = order.readGold();
    }

    String[] getAttackPos()
    {
        return attack;
    }

    int[] getAttackRes()
    {
        return attackRes;
    }

    int getSize()
    {
        return size;
    }

    boolean getWin()
    {
        return win;
    }

    int getGold() {return gold;}

    void defoliate()
    {
        attackRes=null;
        attack=null;
        size=-1;
        gold=0;
    }

    boolean haveData()
    {
        if(attackRes==null)
            return false;
        if(attack==null)
            return false;
        if(size<1)
            return false;
        return true;
    }

    boolean getSzpieg()
    {
        return szpieg;
    }

}
