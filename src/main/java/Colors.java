import com.raylib.Jaylib;
import com.raylib.Raylib;

public class Colors {
    static Jaylib.Color textColor;
    static Raylib.Color mapLineColor;
    static Jaylib.Color mapAttackMiss;
    static Jaylib.Color mapAttackHit;
    static Jaylib.Color mapAttackHit2;
    static Jaylib.Color mapAttackHit3;
    static Jaylib.Color shipColorContour;
    static Jaylib.Color shipColorContour2;
    static Jaylib.Color shipColor2;
    static Jaylib.Font font;
    static Raylib.Color screanColor;
    static int delta=2;
    static Raylib.Color buttonColor;
    static Jaylib.Color buttonPressColor;
    static void defolt()
    {
        textColor = new Jaylib.Color(0,0,0,255);
        mapLineColor = new Jaylib.Color(0,0,0,255);
        mapAttackMiss = new Jaylib.Color(69,69,255,255);
        mapAttackHit = new Jaylib.Color(255,69,69,255);
        mapAttackHit2 =new Jaylib.Color(255,69,69,128);
        mapAttackHit3 = new Jaylib.Color(255,255,69,128);
        shipColorContour = new Jaylib.Color(0,69,255,128);
        shipColorContour2 = new Jaylib.Color(0,0,200,128);
        shipColor2 = new Jaylib.Color(0,255,0,255);
        screanColor = new Jaylib.Color(0,0,0,255);
        buttonColor = new Jaylib.Color(69,69,255,255);
        buttonPressColor = new Jaylib.Color(20, 20, 69, 155);
        font=Jaylib.LoadFont("resources/czciaki/comici.ttf");
    }
    static void updata(boolean placeShipStage,boolean myMove)
    {
        if(placeShipStage)
        {
            if(screanColor.b()!=(byte)255)
            {
                screanColor.r((byte)0);
                screanColor.g((byte)0);
                screanColor.b((byte)255);
            }

            screanColor.r((byte)(screanColor.r()+delta));
            screanColor.g((byte)(screanColor.g()+delta));
            if(screanColor.g()==(byte)255)
                delta=-1;
            if(screanColor.g()==0)
                delta=1;
            return;
        }

        if(myMove)
        {
            if(screanColor.g()!=(byte)255)
            {
                screanColor.r((byte)0);
                screanColor.b((byte)0);
                screanColor.g((byte)255);
            }

            screanColor.r((byte)(screanColor.r()+delta));
            screanColor.b((byte)(screanColor.b()+delta));
            if(screanColor.b()==(byte)255)
                delta=-1;
            if(screanColor.b()==0)
                delta=1;
            return;
        }

        if(screanColor.r()!=(byte)255)
        {
            screanColor.r((byte)255);
            screanColor.g((byte)0);
            screanColor.b((byte)0);
        }

        screanColor.g((byte)(screanColor.g()+delta));
        screanColor.b((byte)(screanColor.b()+delta));
        if(screanColor.b()==(byte)255)
            delta=-1;
        if(screanColor.b()==0)
            delta=1;
        return;
    }

}
