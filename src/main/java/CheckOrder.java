import java.util.Scanner;

public class CheckOrder {
    String order;
    int shot=-1;
    CheckOrder(String order)
    {
        this.order=order;
    }
    int readOrder()
    {
        if(order.isEmpty())
            return 0;
        if(order.equals("Knock"))
            return 0;
        System.out.println(order);
        if(order.indexOf("AttackRes")==0)
            return 1;
        if(order.indexOf("Attack")==0)
            return 2;
        if(order.equals("Shot"))
            return 3;
        if(order.indexOf("Pos")==0)
            return 4;
        if(order.equals("Accepted"))
            return 5;
        if(order.equals("Knock Knock"))
            return 6;
        if(order.equals("YOU WIN"))
            return 7;
        if(order.equals("Ready"))
            return 8;
       return 0;
    }
    String[] readAttackPosition()
    {
        Scanner scanner;

        try {
            scanner=new Scanner(order);
            String tmp=scanner.next();
            tmp=scanner.next();
            shot=Integer.parseInt(tmp);
        }
        catch (Exception e)
        {
            shot=-1;
            return null;
        }

        String[]position=new String[shot];
        for(int i=0;i<shot;i++)
            position[i]=scanner.next();
        return position;
    }
    int[] readAttackResPosition()
    {
        Scanner scanner;
        try {
            scanner=new Scanner(order);
            String tmp=scanner.next();
            tmp=scanner.next();
            shot=Integer.parseInt(tmp);
        }
        catch (Exception e)
        {
            shot=-1;
            return null;
        }

        int[]position=new int[shot];
        for(int i=0;i<shot;i++)
            position[i]=Integer.parseInt(scanner.next());
        return position;
    }

    String readSzpiegPos()
    {
        Scanner scanner;
        try {
            scanner=new Scanner(order);
            String tmp=scanner.next();
            tmp=scanner.next();
            return tmp;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    int readGold()
    {
        int index=order.indexOf("gold:");
        if(index<0)
            return 0;
        int gold=0;
        index+=6;
        while(order.length() >index && order.charAt(index)!=' ')
        {
            gold*=10;
            gold+=order.charAt(index)-'0';
            index++;
        }

        return gold;
    }

    int getSize()
    {
        return shot;
    }
}
