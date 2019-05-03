import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class Magic extends PApplet {
    private static double wScale = 1.920, hScale = 2.16;
    private static double viewWidth = 1920, viewHeight = 2160,scl=1, res = 3, dscl = 2;
    //    private BigDecimal scl = new BigDecimal(1);
    private static double maxIter = 200;
    private static double rg1 = 255, rg2 = 255, rg3 = 255;
    private static double hue = 1, sat = 1, bri = 1, renderTime = 0, cxVal, cyVal;
    private static double rendPX = viewWidth/2, rendPY = viewHeight/2;
    private static double juX = 0, juY = 0, mnX=0,mnY=0;
    private static boolean drawMandel = true,moveImage=false,wheelie=false;
    private static PImage moutput,joutput;
    private static float imgScale =1,imgRX=0,imgRY=0,oldMX=0,oldMY=0;
    public static void main(String[] args) {
        if(args.length>0) {

            fromFile(args[0]);
        }
        PApplet.main("Magic", args);
    }
    public void settings() {
//        size((int) viewWidth, (int) viewHeight);
        fullScreen();
    }

    public void setup() {
        background(0);
//        colorMode(HSB, (int) maxIter);
        colorMode(HSB, 255);
        noStroke();
        render(imagX(rendPX), imagY(rendPY), scl,!drawMandel);
        render(imagX(rendPX), imagY(rendPY), scl,true,false,1920,0);

    }

    public void draw() {
        if(moveImage){
            juX=newX(mouseX);
            juY=newY(mouseY);
            render(0,0,1,true,false,1920,0);
        }
        noFill();
        stroke(255);
        if(drawMandel){
            rect(0,0,(float)viewWidth,(float)viewHeight);
        }else{
            rect(1920,0,(float)viewWidth,(float)viewHeight);
        }

        if(keyPressed) {
            colorMode(RGB, 255);
            fill(115, 150);
            rect(0, 0, 400, 600);
            fill(255);
            text(updateStats(), 10, 20);
            colorMode(HSB, 255);
        }
        colorMode(RGB,255);
/*        stroke(0,255,0);
        line(0,height/2,width,height/2);
        line(width/2,0,width/2,height);
        stroke(0,0,255);
        line(0,(float)rendPY,(float)viewWidth,(float)rendPY);
        line((float)rendPX,0,(float)rendPX,(float)viewHeight);*/
/*        stroke(255,0,0);
        line(0,(float)realY(0),(float)viewWidth,(float)realY(0));
        line((float)realX(0),0,(float)realX(0),(float)viewHeight);
        noStroke();*/
    }
    public void mouseReleased(){
        oldMX=0;
        oldMY=0;
    }
    public void mouseWheel(MouseEvent e){
        if(imgScale>0) {
            imgScale += e.getCount();
            wheelie=true;
        }
    }
    public void mouseClicked() {
//        if (drawMandel) {
//            rendPX = transX(mouseX);
//            rendPY = transY(mouseY);
//            juX = imagX(rendPX);
//            juY = imagY(rendPY);
//            render(juX, juY, scl, false);
//        } else {

        if(mouseX>1920 && !drawMandel) {
            rendPX = transX(mouseX-viewWidth);
            rendPY = transY(mouseY);
            mnX=imagX(rendPX);
            mnY=imagY(rendPY);

        }else if(mouseX<1920 && drawMandel){
            rendPX = transX((mouseX));
            rendPY = transY(mouseY);
            juX = imagX(rendPX);
            juY = imagY(rendPY);

        }
        render(mnX,mnY, scl, true, false, 1920, 0);
        render(juX, juY, scl, false);
//        }


    }
    private void render(double cx, double cy, double scale){
        render(cx,cy,scale,true);
    }
    private void render(double cx, double cy, double scale, boolean julia) {
        render(cx,cy,scale,julia,false,0,0);
    }
    private void render(double cx, double cy, double scale, boolean julia,boolean justSave) {
        render(cx,cy,scale,julia,justSave,0,0);
    }
    private void render(double cx, double cy, double scale, boolean julia,boolean justSave,double rpx,double rpy) {

        colorMode(HSB, 255);
        println("---rendering---");
        int startTimer = millis();
        int iter;
        double nXPix = (int)(viewWidth / res), nYPix = (int)(viewHeight / res);
        PImage output = createImage((int) (nXPix), (int) (nYPix), HSB);

        int loaded=0;
        for (double i = -nXPix / 2; i < nXPix / 2; i++) {
            for (double j = -nYPix / 2; j < nYPix / 2; j++) {
                if(julia){
                    iter = iterate(cx + (i / (nXPix / 2)) * (wScale / scale / 2), cy + (j / (nYPix / 2)) * (hScale / scale / 2));
                }else {
                    iter = iterateM(cx + (i / (nXPix / 2)) * (wScale / scale / 2), cy + (j / (nYPix / 2)) * (hScale / scale / 2));
                }

                if(iter >0) {
                    output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] =
                            //color((float)((255%(iter))),(float)(150*sat),(float)(150*bri));
                            color((float) (hue*113*(sin(radians(iter*2))+1)), (float)(sat*150 * (iter / Math.abs(iter))), (float)(bri*150 * (iter / Math.abs(iter))));
                }else{
                    output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] =
                            color(0);
                }
//                output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] = iter;
//                rect((float) ((i + nXPix / 2) * res), (float) ((j + nYPix / 2) * res), (float) res, (float) res)
            }

            if(i%(nXPix/20)==0){
                loaded++;
                String tmp = 100*loaded/20+"%|";
                for(int k=0;k<20;k++) {
                    if (k < loaded) {
                        tmp = tmp.concat("-");
                    }else{
                        tmp = tmp.concat(" ");
                    }
                }
                tmp=tmp.concat("|\r");
                print(tmp);
            }
        }
        println();
//        output.updatePixels();
        if(julia) {
            joutput = output;
        }else{
            moutput = output;
        }
        if(!justSave) {
            image(output, (float)rpx, (float)rpy, (int) viewWidth, (int) viewHeight);
        }
        scl=scale;
        textSize(20);
        fill(255);

        renderTime = millis() - startTimer;
        noStroke();
        println(updateStats());
//        iterateRM(cx,cy);
    }
    private int iterateM(double cx, double cy) {
        double zx = 0, zy = 0;
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 5) {
                return i;
            }
            double zx1 = (zx * zx - zy * zy) + cx;
            double zy1 = (2 * zx * zy) + cy;
            zx = zx1;
            zy = zy1;
        }
        return -1;
    }
    private int iterateRM(double cx, double cy) {
        double zx = 0, zy = 0;
        stroke(255);
        noFill();
        ellipse((float)realX(cx),(float)realY(cy),(float)maxIter,(float)maxIter);
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 5) {
                return i;
            }
            double zx1 = (zx * zx - zy * zy) + cx;
            double zy1 = (2 * zx * zy) + cy;
            line((float)(realX(zx1)),(float)(realY(zy1)),(float)(realX(zx)),(float)(realY(zy)));
            ellipse((float)(realX(zx1)),(float)(realY(zy1)),(float)((maxIter/i)),(float)((maxIter/i)));
            zx = zx1;
            zy = zy1;
        }
        return -1;
    }
    /*private int iterateM(double cx, double cy) {
        double zx = 0, zy = 0;
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 5) {
                return i;
            }
            double zx1 = zy + cx;
            double zy1 = zx + cy;
            zx = zx1;
            zy = zy1;
        }
        return -1;
    }*//*
    private int iterateMR(double cx, double cy) {
        double zx = 0, zy = 0;
        stroke(255);
        noFill();
        ellipse((float)cx,(float)cy,100,100);
        println("---------------------",transX(realX(cx)),realY(cy)," vs ", transX(rendPX),rendPY);
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 5) {
                return i;
            }
            double zx1 = zy + cx;
            double zy1 = zx + cy;
            ellipse((float)transX(realX(cx)),realY(cy),100,100);
//            line((float)transX(realX(zx1)),(float)transY(realY(zy1)),(float)transX(realX(zx)),(float)transY(realY(zy)));
//            ellipse((float)transX(realX(zx1)),(float)transY(realY(zy1)),(float)(100+100*(i/maxIter)),(float)(100+100*(i/maxIter)));
            zx = zx1;
            zy = zy1;
        }
        noStroke();

        return -1;
    }*/
    private int iterate(double zx, double zy) {
        double cx = juX, cy = juY;
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 5) {
                return i;
            }
            double zx1 = (zx * zx - zy * zy) + cx;
            double zy1 = (2 * zx * zy) + cy;
            zx = zx1;
            zy = zy1;
        }
        return -1;

    }

    private double dist(double cx, double cy, double zx, double zy) {
        return Math.sqrt(
                Math.abs(zx - cx) * Math.abs(zx - cx) +
                        Math.abs(zy - cy) * Math.abs(zy - cy));

    }
    private String updateStats(){
        return updateStats(false);
    }
    private String updateStats(boolean accurate) {
        DecimalFormat df;
        if(accurate){
            df = new DecimalFormat("##################################.##########" +
                    "##################################################################");
        }else {
            df = new DecimalFormat("#####.#####");
        }
        String juul;
        int iter;
        if(drawMandel){
            juul = "Mandelbrot";
            iter = iterateM(imagX(rendPX),imagY(rendPY));
        }else{
            juul="Julia from: (" + df.format(juX) + " + " + df.format(juY) +"i)";
            iter = iterate(imagX(rendPX),imagY(rendPY));
        }
        return ("rendered in: " + df.format((renderTime) )+ "ms" + "\n" +
                "\n" +
                "(q,a)zoom: " + scl + "\n" +
                "(e,d)change speed: " + df.format(dscl) + "\n" +
                "(r,f)pixel size: " + df.format(res) + "\n" +
                "(w,s)max iter: " + df.format(maxIter) + "\n" +
                "\n" +
                "rendering: " + juul + "\n" +
                "\n" +
                "(1,z)H: "+ df.format(hue)+"\n"+
                "(2,x)S: "+ df.format(sat)+"\n"+
                "(3,c)B: "+ df.format(bri)+"\n"+
/*                "(5,b)rg1: "+ df.format(rg1)+"\n"+
                "(6,n)rg2: "+ df.format(rg2)+"\n"+
                "(7,m)rg3: "+ df.format(rg3)+"\n"+*/
                "renderPoint: (" + df.format(rendPX) + " + " + df.format(rendPY) + "i)" + "\n" +
                "chaos at RP : " + iter +"\n"+
                "-----------");
    }
    private String toFile(){
        String juul;
        if(drawMandel){
            juul = "true\n0\n0";
        }else{
            juul="false\n"+juX + "\n" + juY;
        }
        return (scl + "\n" +
                res + "\n" +
                maxIter + "\n" +
                juul + "\n" +
                hue+"\n"+
                sat+"\n"+
                bri+"\n"+
/*                "(5,b)rg1: "+ (rg1)+"\n"+
                "(6,n)rg2: "+ (rg2)+"\n"+
                "(7,m)rg3: "+ (rg3)+"\n"+*/
                rendPX + "\n" +
                rendPY + "\n");
    }
    private static void fromFile(String file){
        try{
            Scanner input = new Scanner(new File(System.getProperty("user.dir")+"\\Saved\\"+file));
            int cnt=0;
            while(input.hasNextLine()) {
                switch(cnt) {
                    case(0):
                        scl = Double.parseDouble(input.nextLine());
                        break;
                    case(1):
                        res = Double.parseDouble(input.nextLine());
                        break;
                    case(2):
                        maxIter = Double.parseDouble(input.nextLine());
                        break;
                    case(3):
                        drawMandel = Boolean.parseBoolean(input.nextLine());
                        break;
                    case(4):
                        juX = Double.parseDouble(input.nextLine());
                        break;
                    case(5):
                        juY = Double.parseDouble(input.nextLine());
                        break;
                    case(6):
                        hue = Double.parseDouble(input.nextLine());
                        break;
                    case(7):
                        sat = Double.parseDouble(input.nextLine());
                        break;
                    case(8):
                        bri = Double.parseDouble(input.nextLine());
                        break;
                    case(9):
                        rendPX = Double.parseDouble(input.nextLine());
                        break;
                    case(10):
                        rendPY = Double.parseDouble(input.nextLine());
                        break;
                    default:
                        input.nextLine();
                }
                cnt++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private double newX(double x){
        return ((rendPX+((x-viewWidth/2)/scl))-viewWidth/2)*(wScale/viewWidth);
    }
    private double newY(double y){
        return (((rendPY+(y-viewHeight/2)/scl))-viewHeight/2)*(hScale/viewHeight);
    }
    private double imagX(double x){
        return (x-viewWidth/2)*(wScale/viewWidth);
    }
    private double imagY(double y){
        return (y-viewHeight/2)*(hScale/viewHeight);
    }
    private double realX(double x){ return ((viewWidth/2)+(x/(wScale/viewWidth))) - scl*(rendPX-viewWidth/2);}
    private double realY(double y){ return ((viewHeight/2)+(y/(hScale/viewHeight))) - scl*(rendPY-viewHeight/2);}
    private double transX(double x) {
        return (rendPX+((x-viewWidth/2)/scl));
    }
    private double transY(double y) {
        return ((rendPY+(y-viewHeight/2)/scl));
    }

    public void keyPressed() {
        switch(keyCode){
            case(CONTROL):
                drawMandel = !drawMandel;
                if(drawMandel){
                    render(imagX(rendPX),imagY(rendPY),scl,false);
                }else{
                    render(imagX(rendPX),imagY(rendPY),scl,true,false,1920,0);
                }
                break;
            case(LEFT):
                rendPX-=viewWidth/dscl;
                break;
            case(RIGHT):
                rendPX+=viewWidth/dscl;
                break;
            case(UP):
                rendPY-=viewHeight/dscl;
                break;
            case(DOWN):
                rendPY+=viewHeight/dscl;
                break;



            default:
                break;
        }

        switch (key) {
            case('='):
                println("saving @res "+viewWidth/res+"x"+viewHeight/res+"........");
                if(drawMandel) {
                    render(imagX(rendPX), imagY(rendPY), scl, false, true);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl, true, true);
                }

                DateFormat dateFormat = new SimpleDateFormat("y.MM.dd_HH`mm`ss");
                String date = dateFormat.format(new Date());
                joutput.save(System.getProperty("user.dir")+"\\Saved\\"+date+"julia.tiff");
                moutput.save(System.getProperty("user.dir")+"\\Saved\\"+date+"mandel.tiff");
                List<String> lines = Arrays.asList(toFile());
                Path file = Paths.get(System.getProperty("user.dir")+"\\Saved\\"+date+".txt");
                try {
                    Files.write(file, lines, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                println("saved!\n"+System.getProperty("user.dir")+"\\Saved\\"+date+".tiff/.txt");
                break;
            case('p'):
                moveImage=!moveImage;
                break;
            case ('q'):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl * dscl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl * dscl,true,false,1920,0);
                }
                break;

            case ('a'):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl / dscl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl / dscl,true,false,1920,0);
                }
                break;


            case ('e'):
                dscl = dscl * dscl;
                break;
            case ('d'):
                dscl = Math.sqrt(dscl);
                break;


            case ('`'):
                hue = 1;
                sat = 1;
                bri = 1;
                rendPX = viewWidth / 2;
                rendPY = viewHeight / 2;
                imgRX = 0;
                imgRY = 0;
                scl = 1;
                imgScale=1;
                moveImage = false;
                dscl = 2;
                maxIter = 200;
                drawMandel =true;
                render(imagX(rendPX), imagX(rendPY), scl,false);
                break;


            case ('s'):
                maxIter /= dscl;
                break;
            case ('w'):
                maxIter *= dscl;
                break;


            case ('2'):
                sat *= dscl;
                break;
            case ('x'):
                sat /= dscl;
                break;

            case ('3'):
                bri *= dscl;
                break;
            case ('c'):
                bri /= dscl;
                break;

            case ('1'):
                hue = hue * dscl;
                break;
            case ('z'):
                hue /= dscl;
                break;

            case ('5'):
                rg1 += +0.1;
                break;
            case ('b'):
                rg1 -= 0.1;
                break;
            case ('6'):
                rg2 += +0.1;
                break;
            case ('n'):
                rg2 -= 0.1;
                break;
            case ('7'):
                rg3 += +0.1;
                break;
            case ('m'):
                rg3 -= 0.1;
                break;


            case ('r'):
                if(res>1) {
                    res -= 1;
                }else{
                    res/=2;
                }
                break;
            case ('f'):
                if(res>=1) {
                    res += 1;
                }else{
                    res*=2;
                }
                break;

            case (' '):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl,true,false,1920,0);
                }
                break;
            default:
                break;
        }
    }


}
//    public void mouseReleased(){
//        render(0,0,scl,res);
//    }
//


//            ellipse(oldX(zx),oldY(zy),30,30);
//            ellipse(oldX(zx1),oldY(zy1),30,30);
//            line(oldX(zx),oldY(zy),oldX(zx1),oldY(zy1));







/*
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class Magic extends PApplet {
    private static float wScale = (float)3.840, hScale = (float)2.160;
    private static float viewWidth = 3840, viewHeight = 2160,scl=1, res = 3, dscl = 2;
    //    private BigDecimal scl = new BigDecimal(1);
    private static float maxIter = 200;
    private static float rg1 = 255, rg2 = 255, rg3 = 255;
    private static float hue = 1, sat = 1, bri = 1, renderTime = 0, cxVal, cyVal;
    private static float rendPX = viewWidth/2, rendPY = viewHeight/2;
    private static float juX = 0, juY = 0;
    private static boolean drawMandel = true,moveImage=false,wheelie=false;
    private static PImage output;
    private static float imgScale =1,imgRX=0,imgRY=0,oldMX=0,oldMY=0;
    public static void main(String[] args) {
        PApplet.main("Magic", args);
    }
    public void settings() {
//        size((int) viewWidth, (int) viewHeight);
        fullScreen();
    }

    public void setup() {
        background(0);
        stroke(255);
//        render(imagX(rendPX), imagY(rendPY), scl,!drawMandel);

    }

    public void draw() {
        background(0);
        stroke(0,255,0);
        line(0,height/2,width,height/2);
        line(width/2,0,width/2,height);
        stroke(0,0,255);
        line(0,rendPY,viewWidth,rendPY);
        line(rendPX,0,rendPX,viewHeight);
        stroke(255,0,0);
        line(0,realY(0),viewWidth,realY(0));
        line(realX(0),0,realX(0),viewHeight);
        fill(115, 150);
        rect(0, 0, 400, 600);
        fill(255);
        textSize(20);
        text(updateStats(false), 10, 20);
    
    }
    public void mouseReleased(){
        oldMX=0;
        oldMY=0;
    }
    public void mouseWheel(MouseEvent e){
        if(imgScale>0) {
            imgScale += e.getCount();
            wheelie=true;
        }
    }
    public void mouseClicked() {
        rendPX = transX(mouseX);
        rendPY = transY(mouseY);
//        ellipse(juX, juY, scl*200,scl*200);
    }

    private float newX(float x){
        return ((rendPX+((x-viewWidth/2)/scl))-viewWidth/2)*(wScale/viewWidth);
    }
    private float newY(float y){
        return (((rendPY+(y-viewHeight/2)/scl))-viewHeight/2)*(hScale/viewHeight);
    }
    private float imagX(float x){
        return (x-viewWidth/2)*(wScale/viewWidth);
    }
    private float imagY(float y){
        return (y+viewHeight/2)*(hScale/viewHeight);
    }
    private float realX(double x){ return (float)((viewWidth/2)+(x/(wScale/viewWidth))) - (rendPX-viewWidth/2);}
    private float realY(double y){ return (float)((viewHeight/2)+(y/(hScale/viewHeight))) - (rendPY-viewHeight/2);}
    private float transX(float x) {
        return (rendPX+((x-viewWidth/2)/scl));
    }
    private float transY(float y) { return ((rendPY+(y-viewHeight/2)/scl));}
    private String updateStats(boolean accurate) {
        DecimalFormat df;
        if(accurate){
            df = new DecimalFormat("##################################.##########" +
                    "##################################################################");
        }else {
            df = new DecimalFormat("#####.#####");
        }
        String juul="";
        int iter;
        return ("renderPoint in i: " + imagX(rendPX) + ", "+ imagY(rendPY) + "\n" +
                "renderPoint: " + df.format(rendPX) + " , " + df.format(rendPY) + "\n" +
                "\n" +
                "(q,a)zoom: " + scl + "\n" +
                "(e,d)change speed: " + df.format(dscl) + "\n" +
                "(r,f)pixel size: " + df.format(res) + "\n" +
                "(w,s)max iter: " + df.format(maxIter) + "\n" +
                "\n" +
                "rendering: " + juul + "\n" +
                "\n" +
                "(1,z)H: "+ df.format(hue)+"\n"+
                "(2,x)S: "+ df.format(sat)+"\n"+
                "(3,c)B: "+ df.format(bri)+"\n"+
*/
/*                "(5,b)rg1: "+ df.format(rg1)+"\n"+
                "(6,n)rg2: "+ df.format(rg2)+"\n"+
                "(7,m)rg3: "+ df.format(rg3)+"\n"+*//*


                "-----------");
    }
    public void keyPressed() {
        switch(keyCode){
            case(LEFT):
                rendPX-=viewWidth/dscl;
                break;
            case(RIGHT):
                rendPX+=viewWidth/dscl;
                break;
            case(UP):
                rendPY-=viewHeight/dscl;
                break;
            case(DOWN):
                rendPY+=viewHeight/dscl;
                break;



            default:
                break;
        }

        switch (key) {
            case ('q'):
                   scl *= dscl;

                break;

            case ('a'):
                scl /= dscl;

                break;


            case ('e'):
                dscl = dscl * dscl;
                break;
            case ('d'):
                dscl = (float)Math.sqrt(dscl);
                break;


            case ('`'):
                hue = 1;
                sat = 1;
                bri = 1;
                rendPX = viewWidth / 2;
                rendPY = viewHeight / 2;
                imgRX = 0;
                imgRY = 0;
                scl = 1;
                imgScale=1;
                moveImage = false;
                dscl = 2;
                maxIter = 200;
                drawMandel =true;
//                render(imagX(rendPX), imagX(rendPY), scl,false);
                break;


            case ('s'):
                maxIter /= dscl;
                break;
            case ('w'):
                maxIter *= dscl;
                break;


            case ('2'):
                sat *= dscl;
                break;
            case ('x'):
                sat /= dscl;
                break;

            case ('3'):
                bri *= dscl;
                break;
            case ('c'):
                bri /= dscl;
                break;

            case ('1'):
                hue = hue * dscl;
                break;
            case ('z'):
                hue /= dscl;
                break;

            case ('5'):
                rg1 += +0.1;
                break;
            case ('b'):
                rg1 -= 0.1;
                break;
            case ('6'):
                rg2 += +0.1;
                break;
            case ('n'):
                rg2 -= 0.1;
                break;
            case ('7'):
                rg3 += +0.1;
                break;
            case ('m'):
                rg3 -= 0.1;
                break;


            case ('r'):
                if(res>1) {
                    res -= 1;
                }else{
                    res/=2;
                }
                break;
            case ('f'):
                if(res>=1) {
                    res += 1;
                }else{
                    res*=2;
                }
                break;
            default:
                break;
        }
    }


}
//    public void mouseReleased(){
//        render(0,0,scl,res);
//    }
//


//            ellipse(oldX(zx),oldY(zy),30,30);
//            ellipse(oldX(zx1),oldY(zy1),30,30);
//            line(oldX(zx),oldY(zy),oldX(zx1),oldY(zy1));*/
