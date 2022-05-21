import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private int[][] myMap;
    private int[][] enemyMap;
    private int n;
    private int gold=0;
    private int[] goldFromShip;
    private int numberOfShip;

    Map(int n,int[] goldFromShip,int numberOfShip) {
        this.n=n;
        myMap=reset(n);
        enemyMap=reset(n);
        this.goldFromShip = goldFromShip;
        this.numberOfShip=numberOfShip;
    }

    int[][] reset(int n) {
        int [][]map=new int[n][n];
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                map[i][j]=0;
        return map;
    }

    int[] shot(String[] pos,int k) {
        gold=0;
        if(pos==null)
            return null;
        if(k<0)
            return null;
        int[] res=new int[k];
        for(int i=0;i<k;i++)
        {
            int x=getX(pos[i]);
            int y=getY(pos[i]);
            if(myMap[y][x]== 0 || myMap[y][x]== 8)
                myMap[y][x]=1;
            else if(myMap[y][x] > 2  && myMap[y][x] < 8)
            {
                gold+=goldFromShip[myMap[y][x]-3]/(myMap[y][x]-2);
                myMap[y][x]=2;
                numberOfShip--;
            }

            res[i]=myMap[y][x];
        }
        return res;
    }

    void shotRes(String[] pos,int[] res,int k) {
        if(pos==null)
            return;
        if(res==null)
            return;
        for(int i=0;i<k;i++)
        {
            int x=getX(pos[i]);
            int y=getY(pos[i]);
            enemyMap[y][x]=res[i];
        }
    }

    boolean placeShip(int usedShip,boolean rotate,String pos,DrawBoard draw) {
        if(pos.isEmpty())
            return false;
        int x=getX(pos);
        int y=getY(pos);
        return placeShip(usedShip, rotate, x, y,draw);
    }

    boolean placeShip(int usedShip,boolean rotate,int x,int y,DrawBoard draw) {
        if(!canBeShipPlace(usedShip,rotate,x,y))
            return false;

        int start = usedShip / 2;
        int parts = usedShip + 1;
        int deltaX = rotate ? 1 : 0;
        int deltaY = rotate ? 0 : 1;
        for(int i = 0; i < parts; i++)
        {
            int mapX=x + (i - start) * deltaX;
            int mapY=y + (i - start) * deltaY;
            myMap[mapY][mapX] = 3+usedShip;
            shipNonPlaceShip(mapX,mapY);
        }
        draw.addShip(x-start*deltaX,y-start*deltaY,usedShip,rotate);
        return true;
    }

    boolean canBeShipPlace(int usedShip,boolean rotate,String pos) {

        int x=getX(pos);
        int y=getY(pos);
        return canBeShipPlace(usedShip, rotate, x, y);
    }

    boolean canBeShipPlace(int usedShip,boolean rotate,int x,int y) {
        if(usedShip<0)
            return false;
        int start = usedShip / 2;
        int parts = usedShip + 1;
        int deltaX = rotate ? 1 : 0;
        int deltaY = rotate ? 0 : 1;
        for(int i = 0; i < parts; i++)
        {
            int tmpX=x + (i - start) * deltaX;
            int tmpY=y + (i - start) * deltaY;
            if(!canBePartShipPlace(tmpX,tmpY))
                return false;
        }

        return true;
    }

    boolean canBePartShipPlace(int x,int y) {
        if(x < 0 || x >= n || y < 0 || y >=n)
            return false;
        if(myMap[y][x]==0)
            return true;
        return false;
    }

    void shipNonPlaceShip(int x,int y) {
        if(y + 1 < n && myMap[y + 1][x]==0)
            myMap[y + 1][x]=8;
        if(y - 1 >= 0 && myMap[y - 1][x]==0)
            myMap[y - 1][x]=8;
        if(x + 1 < n && myMap[y][x + 1]==0)
            myMap[y][x + 1]=8;
        if(x - 1 >= 0 && myMap[y][x - 1]==0)
            myMap[y][x - 1]=8;
    }

    static int getX(String pos) {
        int x;
        int index=0;
        while((pos.charAt(index) - '0') <= 9)
        {
            index++;
        }
        x=pos.charAt(index)-'a';

        return x;
    }

    static int getY(String pos) {
        int y=0;
        int index=0;

        while((pos.charAt(index) - '0') <= 9)
        {
            int i=pos.charAt(index) - '0';
            y*=10;
            index++;
            y+=i;
        }
        return y;
    }

    int[][] getMyMap()
    {
        return myMap;
    }

    int[][] getEnemyMap()
    {
        return enemyMap;
    }

    boolean canShot(int x,int y) {
        if(x < 0 || x >= n || y < 0 || y >=n)
            return false;
        if(enemyMap[y][x]==0)
            return true;
        return false;
    }

    List<String> getFreeSpace()
    {
        List<String> freeSpaceList=new ArrayList<>();
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                if(enemyMap[i][j]==0)
                    freeSpaceList.add(SetAttack.convartPos(j,i));
        return freeSpaceList;
    }

    List<String> getShipPos()
    {
        List<String> shipList=new ArrayList<>();
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                if(myMap[i][j]>2 && myMap[i][j]<8)
                    shipList.add(SetAttack.convartPos(j,i));


        return shipList;
    }

    String szpieg()
    {
        List<String> shipList=getShipPos();
        Random random=new Random();
        int ship=random.nextInt(shipList.size());
        int x=getX(shipList.get(ship));
        int y=getY(shipList.get(ship));
        gold+=goldFromShip[myMap[y][x]-3]/(myMap[y][x]-2);
        myMap[y][x]=2;
        String pos=SetAttack.convartPos(x,y);
        shipList.clear();
        numberOfShip--;
        return  pos;
    }



    void randShip(int[] ship,DrawBoard draw)
    {
        Random random=new Random();
        for(int i=0;i<5;i++)
        {
            while(ship[i]>0)
            {
                int x=random.nextInt(n);
                int y=random.nextInt(n);
                boolean rotate= random.nextBoolean();
                if(canBeShipPlace(i,rotate,x,y))
                {
                    placeShip(i,rotate,x,y,draw);
                    ship[i]--;
                }
            }
        }
    }

    int getGold()
    {
        int tmp=gold;
        gold=0;
        return tmp;
    }

    int getNumberOfShip()
    {
        return numberOfShip;
    }
}
