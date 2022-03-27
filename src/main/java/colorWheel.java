import com.raylib.Jaylib;
import org.bytedeco.javacpp.BytePointer;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.EndDrawing;

public class colorWheel{

    public static Jaylib.Color setColor(Jaylib.Color Color)
    {

        Jaylib.Color[] color=new Jaylib.Color[6];
        color[0]=new Jaylib.Color(255,0,0,255);
        color[1]=new Jaylib.Color(255,255,0,255);
        color[2]=new Jaylib.Color(0,255,0,255);
        color[3]=new Jaylib.Color(0,255,255,255);
        color[4]=new Jaylib.Color(0,0,255,255);
        color[5]=new Jaylib.Color(255,0,255,255);
        Jaylib.Rectangle rec=new Jaylib.Rectangle(50,50,200,200);
        float mix2 = 0;
        float mix3=0;
        int cursorx = 0;
        int cursory=0;
        int colorX=-1;
        Jaylib.Color color1 = new Jaylib.Color(0,0,0,255);
        Jaylib.Color color2 = new Jaylib.Color(0,0,0,255);
        Jaylib.Color color3=Color;
        {
            color3.r(Color.r());
            color3.g(Color.g());
            color3.b(Color.b());


            int r=(color3.r()<0?255+color3.r()+1:color3.r());
            int g=(color3.g()<0?255+color3.g()+1:color3.g());
            int b=(color3.b()<0?255+color3.b()+1:color3.b());
            int max=Math.max(r,Math.max(g,b));
            float contrast=(255.0f-max)/255.0f;
            cursory=(int)(50+contrast*200);
            contrast=1-contrast;
            mix3=contrast;


            r=(255-max+r);
            g=(255-max+g);
            b=(255-max+b);

            color2.r((byte) r);
            color2.g((byte) g);
            color2.b((byte) b);


            int min=Math.min(r,Math.min(g,b));
            contrast=(min)/255.0f;
            mix2=contrast;
            contrast=1-contrast;

            cursorx=(int)(50+contrast*200);

            r=(r!=255?r-min:255);
            g=(g!=255?g-min:255);
            b=(b!=255?b-min:255);
            color1.r((byte) r);
            color1.g((byte) g);
            color1.b((byte) b);
            if(r==255)
            {
                if(b==0&&g==0)
                {
                    colorX=0;
                }
                else
                {
                    if(b>0)
                    {
                        colorX=(int)(250+((255.0f-b)/255.0f)*50);
                    }
                    else
                    {
                        colorX=(int)(((g)/255.0f)*50);
                    }
                }
            }
            else if(b==255)
            {
                if(r==0&&g==0)
                {
                    colorX=200;
                }
                else
                {
                    if(g>0)
                    {
                        colorX=(int)(150+((255.0f-g)/255.0f)*50);
                    }
                    else
                    {
                        colorX=(int)(200+((r)/255.0f)*50);
                    }
                }

            }
            else if(g==255)
            {
                if(b==0&&r==0)
                {
                    colorX=100;
                }
                else
                {
                    if(r>0)
                    {
                        colorX=(int)(50+((255.0f-r)/255.0f)*50);
                    }
                    else
                    {
                        colorX=(int)(100+((b)/255.0f)*50);
                    }
                }
            }
        }

        InitWindow(400,400,"Kolorki");
        while(!WindowShouldClose())
        {
            if(IsMouseButtonDown(0))
            {
                int x=GetMouseX();
                int y=GetMouseY();
                y=y-(300);
                if(y >= 0 && y<= 50 && x>=0 && x<=300)
                {
                    int tmp=x/50;
                    float tmp2=x%50;
                    tmp2=tmp2/50;
                    color1=mix(color[(tmp+1)%6],color[(tmp)%6],tmp2);
                    color2=mix2(color1,mix2);
                    color3=mix3(color2,mix3);
                    colorX=x;
                }
                y=y+250;
                if(y>=0 && y<=200 && x >=50 && x<=250)
                {

                    mix2=(x-50)/200.0f;
                    mix2=1-mix2;
                    mix3=y/200.0f;
                    mix3=1-mix3;


                    color2=mix2(color1,mix2);

                    color3=mix3(color2,mix3);

                    cursory=50+y;
                    cursorx=x;
                }
            }
            BeginDrawing();
            ClearBackground(WHITE);
            DrawRectangle((int) (rec.x()-2), (int) (rec.y()-2), (int) (rec.width()+4), (int) (rec.height()+4),BLACK);
            DrawRectangle(300,0,100,400,color3);
            DrawRectangleGradientEx( rec, WHITE, BLACK, BLACK, color1);
            for(int i=0;i<6;i++)
            {
                DrawRectangleGradientH(50*i,300,50,50,color[i],color[(1+i)%6]);
            }
            if(cursorx!=-1)
            {
                DrawRectangle(cursorx-10,cursory-3,8,6,BLACK);
                DrawRectangle(cursorx+2,cursory-3,8,6,BLACK);
                DrawRectangle(cursorx-3,cursory-10,6,8,BLACK);
                DrawRectangle(cursorx-3,cursory+2,6,8,BLACK);

            }
            if(colorX!=-1)
            {
                Jaylib.Vector2 tmp1=new Jaylib.Vector2(colorX-5,290);
                Jaylib.Vector2 tmp2=new Jaylib.Vector2(colorX+5,290);
                Jaylib.Vector2 tmp3=new Jaylib.Vector2(colorX,300);
                DrawTriangle(tmp1,tmp3,tmp2,BLACK);
                tmp1.y(360);
                tmp2.y(360);
                tmp3.y(350);
                DrawTriangle(tmp1,tmp2,tmp3,BLACK);
            }
            EndDrawing();
            System.gc();

        }
        CloseWindow();
        return color3;
    }

    public static Jaylib.Color mix(Jaylib.Color color1,Jaylib.Color color2,float mix)
    {
        float r= (((float) color1.r())*mix+((float)color2.r())*(1.0f-mix))*(-255);
        float b= (((float) color1.b())*mix+((float)color2.b())*(1.0f-mix))*(-255);
        float g= (((float) color1.g())*mix+((float)color2.g())*(1.0f-mix))*(-255);
        float a= 255;
        return new Jaylib.Color((int)r, (int) g, (int) b, (int) a);
    }

    public static Jaylib.Color mix2(Jaylib.Color color1,float mix)
    {
        //
        int add=(int)(mix*255);
        if(add<0)
            add=add*(-1);
        float r2= (color1.r()<0?255+color1.r()+1:color1.r())+add;

        if(r2>255)
            r2=255;
        float b2= (color1.b()<0?255+color1.b()+1:color1.b())+add;
        if(b2>255)
            b2=255;
        float g2= (color1.g()<0?255+color1.g()+1:color1.g())+add;
        if(g2>255)
            g2=255;
        float a= 255;

        return new Jaylib.Color((int)r2, (int) g2, (int) b2, (int) a);
    }
    public static Jaylib.Color mix3(Jaylib.Color color1,float mix)
    {
        float r3= (color1.r()<0?255+color1.r()+1:color1.r())*mix;
        float b3= (color1.b()<0?255+color1.b()+1:color1.b())*mix;
        float g3= (color1.g()<0?255+color1.g()+1:color1.g())*mix;
        float a= 255;

        return new Jaylib.Color((int)r3, (int) g3, (int) b3, (int) a);
    }

}
