import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SetAttack {
    static int numberOfAttack=0;
    static int shot=0;
    static String[] res;
    static int[][] attack;
    static String[] checkAttack(String pos,int attackMode,Map map)
    {
        if(pos.isEmpty())
            return null;
        int x=Map.getX(pos);
        int y=Map.getY(pos);
        if(attackMode != 4 && shot != 0)
        {
            shot=0;
            res=null;
            attack=null;
        }
        switch (attackMode)
        {
            case 0:
                return attack(x,y,pos,map);
            case 1:
                return attack1(x,y,map);
            case 2:
                return attack2(x,y,map);
            case 3:
                return attack3(x,y,map);
            case 4:
                return attack4(x,y,map);
            case 5:
                return null;
            case 6:
                return attack6(map);
        }

        return null;
    }

    static String[] attack(int x,int y,String pos,Map map)
    {
        numberOfAttack=0;
        if(!map.canShot(x,y))
            return null;
        String []shotPos=new String[1];
        shotPos[0]=pos;
        numberOfAttack=1;
        return shotPos;
    }

    static String[] attack1(int x,int y,Map map)
    {
        numberOfAttack=0;
        for(int i=0;i<9;i++)
            if(map.canShot(x-1+i/3,y-1+i%3))
                numberOfAttack++;
        String[] shotPos=new String[numberOfAttack];
        numberOfAttack=0;
        for(int i=0;i<9;i++)
            if(map.canShot(x-1+i/3,y-1+i%3))
            {
                shotPos[numberOfAttack]=convartPos(x-1+i/3,y-1+i%3);
                numberOfAttack++;
            }
        return shotPos;
    }

    static String[] attack2(int x,int y,Map map)
    {
        numberOfAttack=0;
        for(int i=0;i<5;i++)
            if(map.canShot(x-2+i,y-2+i))
                numberOfAttack++;

        for(int i=0;i<5;i++)
            if(i!=2 && map.canShot(x+2-i,y-2+i))
                numberOfAttack++;

        String []shotPos=new String[numberOfAttack];
        numberOfAttack=0;
        for(int i=0;i<5;i++)
            if(map.canShot(x-2+i,y-2+i))
            {
                shotPos[numberOfAttack]=convartPos(x-2+i,y-2+i);
                numberOfAttack++;
            }
        for(int i=0;i<5;i++)
            if(i!=2 && map.canShot(x+2-i,y-2+i))
            {
                shotPos[numberOfAttack]=convartPos(x+2-i,y-2+i);
                numberOfAttack++;
            }
        return shotPos;
    }

    static String[] attack3(int x,int y,Map map)
    {
        numberOfAttack=0;
        for(int i=0;i<5;i++)
            if(map.canShot(x-2+i,y))
                numberOfAttack++;

        for(int i=0;i<5;i++)
            if(i!=2 && map.canShot(x,y-2+i))
                numberOfAttack++;

        String []shotPos=new String[numberOfAttack];
        numberOfAttack=0;
        for(int i=0;i<5;i++)
            if(map.canShot(x-2+i,y))
            {
                shotPos[numberOfAttack]=convartPos(x-2+i,y);
                numberOfAttack++;
            }
        for(int i=0;i<5;i++)
            if(i!=2 && map.canShot(x,y-2+i))
            {
                shotPos[numberOfAttack]=convartPos(x,y-2+i);
                numberOfAttack++;
            }
        return shotPos;
    }

    static String[] attack4(int x,int y,Map map)
    {
        if(shot==0)
        {
            shot=3;
            res=new String[15];
            numberOfAttack=0;
            attack=new int[3][2];
        }

        for(int i=0;i<3;i++)
            if(map.canShot(x-1+i,y))
            {
                res[numberOfAttack]=convartPos(x-1+i,y);
                numberOfAttack++;
            }

        if(map.canShot(x,y-1))
        {
            res[numberOfAttack]=convartPos(x,y-1);
            numberOfAttack++;
        }

        if(map.canShot(x,y+1))
        {
            res[numberOfAttack]=convartPos(x,y+1);
            numberOfAttack++;
        }
        attack[3-shot][0]=x;
        attack[3-shot][1]=y;
        shot--;

        if(shot!=0)
            return null;
        attack=null;
        return res;
    }

    static String[] attack6(Map map)
    {
        List <String> pos;
        pos=map.getFreeSpace();
        Random random=new Random();
        int size= pos.size();

        if(size<10)
            return listToTable(size,pos);

        String[] posRes=new String[10];
        numberOfAttack=10;
        for (int i=0;i<10;i++)
        {
            int listpos= random.nextInt(size);
            posRes[i]=pos.get(listpos);
            pos.remove(listpos);
            size--;
        }
        return posRes;
    }

    static String []listToTable(int n,List<String> pos)
    {
        String[] posRes=new String[n];
        numberOfAttack=n;
        for(int i=0;i<n;i++)
            posRes[i]=pos.get(i);
        return posRes;
    }

    static String convartPos(int x,int y)
    {
        String tmp=y+"";
        tmp+=(char)(x+'a');
        return tmp;
    }

    static int getShot()
    {
        return shot;
    }
    static int[][] getAttack()
    {
        return attack;
    }


}
