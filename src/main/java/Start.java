import static java.lang.Thread.sleep;

public class Start {
    
    static int mapSize=0;
    static int startTime=0;
    static int moveTime=0;
    static int[][] ship=new int[3][5];
    static int[][] attackWhiteList=new int[2][6];
    static int startGold=0;
    static Communication communication=new Communication();

    
    public static void main(String[] args) {
        MenuStart start=new MenuStart();
        start.start();
        String who = null;
        boolean End=false;
        while(!start.getEnd())
        {
            //System.out.println("pentla "+start.getMenuStage());
            if(start.getMenuStage()==3)
            {
                who=new String("server");
                System.out.println("JESTES W TWORZENIu SERVERA "+start.getWho() +" ip "+start.getIp()+" port "+start.getPort());
                communication=new Communication();
                while(!communication.tryConnect(start.getWho(),start.getPort(),start.getIp()) && start.getMenuStage()==3 &&!start.getEnd())
                {

                }
                if(start.getMenuStage()!=3 ||start.getEnd()) {
                    communication.tryConnect("client",start.getPort(),start.getIp());
                    start.port++;
                }
                if(start.getMenuStage()==3)
                {
                    System.out.println("ELO2");
                    mapSize=start.mapSize;
                    startTime=start.startTime;
                    moveTime=start.moveTime;
                    ship= start.ship;
                    attackWhiteList=start.attackWhiteList;
                    startGold=start.startGold;
                    if(communication.connect)
                    {
                        start.setEnd(true);
                        End=true;
                    }


                }
                else
                {
                    End=false;
                }
            }
            else if(start.getMenuStage()==4 && start.tryConnect)
            {
                who=new String("client");
                System.out.println("JESTES W DOLACZANIA KLIENTA"+start.getWho() +" ip "+start.getIp()+" port "+start.getPort());

                if(communication.tryConnect(start.getWho(),start.getPort(),start.getIp()))
                {
                    End=true;
                    start.setEnd(true);
                }

            }
            try{
                sleep(100);
            }
            catch (Exception e)
            {

            }
        }
        if(!End)
            return;

        System.out.println("Koniec");
        if(who.equals("server"))
            sendGameInformation();
        else if(who.equals("client"))
            getGameInformation();
        System.out.println("Koniec2");
        MainGameCore mainGameCore=new MainGameCore(communication,who, (byte) mapSize,ship,attackWhiteList,moveTime,startTime,startGold);
        System.out.println("Koniec3");
        mainGameCore.main();


    }
    static void sendGameInformation() {
        communication.sendInformation(mapSize+"");
        communication.sendInformation(moveTime+"");
        communication.sendInformation(startTime+"");
        communication.sendInformation(startGold+"");
        for(int i=0;i<5;i++)
        {

            communication.sendInformation(ship[0][i]+"");
            communication.sendInformation(ship[1][i]+"");
            communication.sendInformation(ship[2][i]+"");
        }
        for(int i=0;i<6;i++)
        {
            communication.sendInformation(attackWhiteList[0][i]+"");
            communication.sendInformation(attackWhiteList[1][i]+"");
        }
    }

    static void getGameInformation() {

        String tmp=communication.getInformation();
        mapSize=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        moveTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startTime=Integer.parseInt(tmp);

        tmp=communication.getInformation();
        startGold=Integer.parseInt(tmp);

        ship=new int[3][5];
        for(int i=0;i<5;i++)
        {
            tmp=communication.getInformation();
            ship[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[1][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            ship[2][i]=Integer.parseInt(tmp);
        }
        for(int i=0;i<6;i++)
        {
            tmp=communication.getInformation();
            attackWhiteList[0][i]=Integer.parseInt(tmp);
            tmp=communication.getInformation();
            attackWhiteList[1][i]=Integer.parseInt(tmp);
        }
    }
}
