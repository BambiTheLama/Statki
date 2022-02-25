import com.raylib.Jaylib;
import com.raylib.Raylib;
import org.bytedeco.javacpp.BytePointer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.*;
import static com.raylib.Raylib.WindowShouldClose;

public class StartMenu {
    public static boolean main(String[] arg) {
        int width=400,height=400;
        boolean f=false;
        InitWindow(width, height, "Statki Menu Glowne");
        SetTargetFPS(60);
        byte end=0;
        Jaylib.SetExitKey(KEY_ESCAPE);
        while (!WindowShouldClose()&&end==0) {
            if(IsKeyPressed(KEY_ESCAPE))
            {
                end=3;
            }
            BeginDrawing();
            ClearBackground(RAYWHITE);
            draw();
            end=colison();

            EndDrawing();
        }
        CloseWindow();

        if(end==1)
        {
            String[] a=new String[2];
            a[0]="server";
            a[1]="49999";
            try{
                MainGameCore.main(a);
            }
            catch (Exception b){

            }
        }
        else if(end==2)
        {

            String[] a=new String[2];
            a[0]="client";
            a[1]="49999";
            try{
                MainGameCore.main(a);
            }
            catch (Exception b){

            }
        }else
        {
            return false;
        }
        return true;
    }
    public static void draw()
    {
        Jaylib.DrawRectangle(100,100,200,100,BLUE);
        Jaylib.DrawText("Hostuj",100,125,50,BLACK);
        Jaylib.DrawRectangle(100,250,200,100,BLUE);
        Jaylib.DrawText("Dolacz",100,275,50,BLACK);
    }
    public static byte colison()
    {
        if(Raylib.IsMouseButtonPressed(MOUSE_BUTTON_LEFT))
        {
            int x=Raylib.GetMouseX();
            int y=Raylib.GetMouseY();
            if(x>100&&x<300&&y>100&&y<200)
            {
                return 1;
            }
            if(x>100&&x<300&&y>250&&y<350)
            {
                return 2;
            }

        }
        return 0;
    }
}
