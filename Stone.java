import java.awt.*;

public class Stone{

    private int posX;
    private int posY;

    // 0:invisible, 1:light, 2: normal
    private int state;

    //true: bright, false: none
    private boolean around;

    public Stone(int x, int y, int state){
        this.posX = x;
        this.posY = y;
        this.state = state;
        this.around = false;
    }

    public int getIndexX(){
        return (this.getX()-50)/70;
    }
    public int getIndexY(){
        return (this.getY()-80)/70;
    }


    public int getX(){
        return this.posX;
    }
    public int getY(){
        return this.posY;
    }
    public int getState(){
        return this.state;
    }
    public boolean getAround(){
        return this.around;
    }


    public void setState(int state){
        this.state = state;
    }

    public void setAround(boolean around){
        this.around = around;
    }

};
