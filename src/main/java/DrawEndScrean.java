import com.raylib.Jaylib;

public class DrawEndScrean {
    boolean newGame = false;
    boolean endGame = false;
    Jaylib.Vector2 textPos=new Jaylib.Vector2(500,310);
    Jaylib.Vector2 textPos2=new Jaylib.Vector2(500,355);
    Jaylib.Vector2 textPos3=new Jaylib.Vector2(500,455);
    Jaylib.Vector2 textPos4=new Jaylib.Vector2(500,555);
    Jaylib.Rectangle pos=new Jaylib.Rectangle(490,300,300,100);
    Jaylib.Rectangle pos2=new Jaylib.Rectangle(490,450,300,50);
    Jaylib.Rectangle pos3=new Jaylib.Rectangle(490,550,300,50);
    int button=0;



    void draw(boolean win,boolean lost)
    {
        Jaylib.DrawRectangleRec(pos,Colors.buttonColor);

        Jaylib.DrawTextEx(Colors.font,"Koniec Gry ",textPos,45,6,Colors.textColor);
        Jaylib.DrawTextEx(Colors.font,(win?"Wygrales":"")+(lost?"Przegrales":""),textPos2,45,6,Colors.textColor);

        Jaylib.DrawRectangleRec(pos2,Colors.buttonColor);
        if(button==1)
            Jaylib.DrawRectangleLinesEx(pos2,5,Colors.buttonPressColor);
        Jaylib.DrawTextEx(Colors.font,"Zagraj ponownie ",textPos3,36,6,Colors.textColor);

        Jaylib.DrawRectangleRec(pos3,Colors.buttonColor);
        if(button==2)
            Jaylib.DrawRectangleLinesEx(pos3,5,Colors.buttonPressColor);
        Jaylib.DrawTextEx(Colors.font,"Wyjdz ",textPos4,36,6,Colors.textColor);


    }
    void collison(int x,int y,boolean press)
    {
        if(x < pos2.x() || x > pos2.x() + pos2.width())
            return;
        if(y > pos2.y() && y < pos2.y() + pos2.height())
            button=1;
        else if(y > pos3.y() && y < pos3.y() + pos3.height())
            button=2;
        else
            button=0;
        if(press)
        {
            if(button==1)
                newGame=true;
            if(button==2)
                endGame=true;
        }
    }

}
