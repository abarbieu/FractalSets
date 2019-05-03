import processing.core.PApplet;

public class Rendering extends PApplet {

    Viewport mainView,view,mother;
    Point start=new Point(0,0),end=new Point(0,0),mouse=new Point(0,0);
    boolean newPress=true,mothered=false;

    public static void main(String[] args) {
        PApplet.main("Rendering", args);
    }
    @Override
    public void settings() {
        size(1920,1080);
    }
    public void setup(){
        background(0);
        mainView = new Viewport(width,height,new Point(width/2,height/2),this);
        view=new Viewport(mainView);
        mother=new Viewport(mainView);
        mainView.setMother(mainView);
        mother.setMother(mainView);
        view.setMother(mother);
    }
    public void draw(){
        background(0);
        mouse.update(mouseX,mouseY);
        if(mousePressed){
            if(newPress){
                start.update(mother.putIn(mouse));
                newPress=false;
            }else {
                end.update(mother.putIn(mouse));
                stroke(255);
                fill(0,255,0,50);
                start.rect(end,this);
            }
        }/*else{
            stroke(255,0,0);
            ellipse(mouseX, mouseY, 50, 50);
            stroke(0,255,0);
            mother.putIn(mouse).circle(50,this);
            stroke(0,0,255);
            view.putIn(mother.takeOut(mother.putIn(mouse))).circle(50,this);
        }
        view.drawBounds(color(0,0,255));
        mother.drawBounds(color(255,0,0));*/
        mother.drawBounds(color(140,20,50));

    }
    public void mouseClicked(){

    }
    public void mouseReleased(){
        newPress=true;
        if(!mothered) {
            mother.resize(start.xDist(end),start.yDist(end));
            mother.moveTo(start.center(end));
            mothered=true;
        }
        mother = mother.devineBirth(start,end);
        mother.setMother(mainView);
    }
    public void keyPressed(){
        switch(key){
            case('a'):
                mother.scale(0.8);
                break;
            case('q'):
                mother.scale(1.2);
            default:
                break;
        }
    }
}
