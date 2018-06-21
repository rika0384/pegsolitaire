
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PegSolitaire {

    private List<Stone> stoneList = new ArrayList<Stone>();

    public static void main(String[] args) {

        PegSolitaire pegsolitaire = new PegSolitaire();
        pegsolitaire.init();

    }

    public void init(){
        JFrame frame = new JFrame("peg-solitaire");

        Container contentPane = frame.getContentPane();

        Canvas canvas = new Canvas(this);
        contentPane.add(canvas);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();                                   // 配置を調整
        frame.setVisible(true);

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                if(Math.min(i,6-i) < 2 && Math.min(j,6-j) < 2)continue;
                if(i == 3 && j == 3){
                    stoneList.add(new Stone(getPosX(i),getPosY(j),0));
                }else{
                    stoneList.add(new Stone(getPosX(i),getPosY(j),2));
                }

            }
        }

        Thread thread = new Thread(canvas);  // スレッドを生成し，割り当て
        thread.start();                  // スレッドを開始

    }




    public List<Stone> getList(){
        return stoneList;
    }

    public int getPosX(int x){
        return 50+70*x;
    }
    public int getPosY(int y){
        return 80+70*y;
    }

}



class Canvas extends JPanel implements MouseListener, Runnable{
    Font font;
    final static int RADIUS = 20;               // 半径の設定(定数)
    private int pressPosX = -1;                 // マウスポインタのX座標
    private int pressPosY = -1;                 // マウスポインタのY座標

    private PegSolitaire pegsolitaire;
    private int gameState = 0;

    /*
    gameState

        -1: gameOver
         - 残りの石の数を画面下部に表示

        0:通常
         - 動かすことのできる石の周囲に色がつく

        1:ドラッグ中
         - ドラッグの始点の色がうすくなる
         - 行き先候補の石の周囲に色がつく
    */



    Canvas(PegSolitaire p) {
        pegsolitaire = p;
        setBackground(Color.white);                    // 背景色を設定
        setPreferredSize(new Dimension(600, 600));     // 画面サイズを設定
        font = new Font("SanSerif", Font.ITALIC, 25);  // フォントを作成
        addMouseListener(this);           // 自分自身をイベントリスナとして登録
    }

    public void mousePressed(MouseEvent e) {   // マウスボタンを押したとき
        int x = e.getX();                           // マウスポインタのX座標を取得
        int y = e.getY();                           // マウスポインタのX座標を取得

        List<Stone> stoneList = pegsolitaire.getList();

        for (int i = 0; i < stoneList.size(); i++) {
            int posX = stoneList.get(i).getX();
            int posY = stoneList.get(i).getY();
            if((x-posX)*(x-posX) + (y-posY)*(y-posY) <= RADIUS*RADIUS && stoneList.get(i).getState() == 2){
                pressPosX = posX;
                pressPosY = posY;
                stoneList.get(i).setState(1);
                gameState = 1;
                break;
            }
        }

        repaint();
    }

    public void mouseReleased(MouseEvent e) {  // マウスボタンを放したとき

        //JFrame frame = new JFrame();
        //JOptionPane.showMessageDialog(frame, pressPosX + " " + pressPosY);

        if(pressPosX == -1 || pressPosY == -1)return;

        int x = e.getX();
        int y = e.getY();

        boolean disappear = false;

        List<Stone> stoneList = pegsolitaire.getList();

        Stone preStone = null;

        for (Stone stone : stoneList) {
            if(stone.getX() == pressPosX && stone.getY() == pressPosY){
                preStone = stone;
                break;
            }
        }


        for (Stone nowStone : stoneList) {
            int posX = nowStone.getX();
            int posY = nowStone.getY();
            if((x-posX)*(x-posX) + (y-posY)*(y-posY) <= RADIUS*RADIUS && nowStone.getState() == 0){
                if(getIndexX(pressPosX) == getIndexX(posX) && Math.abs(getIndexY(pressPosY) - getIndexY(posY)) == 2){
                    for(Stone tmpStone : stoneList){
                        int X = tmpStone.getX();
                        int Y = tmpStone.getY();
                        if(getIndexX(posX) == getIndexX(X) && getIndexY(pressPosY) + getIndexY(posY) == 2 * getIndexY(Y)){
                            if(tmpStone.getState() == 2){
                                disappear = true;
                                tmpStone.setState(0);
                                preStone.setState(0);
                                nowStone.setState(2);
                            }
                            break;
                        }
                    }
                }
                if(Math.abs(getIndexX(pressPosX) - getIndexX(posX)) == 2 && getIndexY(pressPosY) == getIndexY(posY)){
                    for(Stone tmpStone : stoneList){
                        int X = tmpStone.getX();
                        int Y = tmpStone.getY();
                        if(getIndexX(pressPosX) + getIndexX(posX) == 2 * getIndexX(X) && getIndexY(posY) == getIndexY(Y)){
                            if(tmpStone.getState() == 2){
                                disappear = true;
                                tmpStone.setState(0);
                                preStone.setState(0);
                                nowStone.setState(2);
                            }
                            break;
                        }
                    }
                }

            }
        }
        if(disappear == false){
            for (int i = 0; i < stoneList.size(); i++) {
                if(stoneList.get(i).getX() == pressPosX && stoneList.get(i).getY() == pressPosY){
                    stoneList.get(i).setState(2);
                }
            }
        }



        pressPosX = -1;
        pressPosY = -1;
        preStone = null;
        gameState = 0;
        repaint();
    }


    public void mouseClicked(MouseEvent e) { }   // マウスボタンをクリックしたとき
    public void mouseEntered(MouseEvent e) { }  // ウインドウに入ったとき
    public void mouseExited(MouseEvent e) { }   // ウインドウから出たとき


    public int getIndexX(int x){
        return (x-50)/70;
    }
    public int getIndexY(int y){
        return (y-80)/70;
    }


    public void paintComponent(Graphics g) { // paintComponent()の再定義
        super.paintComponent(g);             // JPanelにより背景を描画

        g.setColor(Color.black);                 // 描画色を設定
        g.setFont(font);                         // フォントを設定
        g.drawString("Peg Solitaire", 180, 30);      // 文字を描画

        List<Stone> stoneList = pegsolitaire.getList();

        for (Stone stone : stoneList) {
            int x = stone.getX();
            int y = stone.getY();
            g.setColor(Color.blue);
            g.drawRect(x-30, y-30, 60, 60);
            if(stone.getAround()){
                g.setColor(Color.yellow);
                g.fillOval(x-28,y-28,RADIUS*3-4,RADIUS*3-4);
            }
            if(stone.getState() == 2){
                g.setColor(Color.red);
                g.fillOval(x-20,y-20,RADIUS*2,RADIUS*2);
            }else if(stone.getState() == 1){
                g.setColor(Color.red);
                g.drawOval(x-20,y-20,RADIUS*2,RADIUS*2);
            }

        }
        repaint();
    }


    /*
    -1: gameOver
     - 残りの石の数を画面下部に表示

    0:通常
     - 動かすことのできる石の周囲に色がつく

    1:ドラッグ中
     - ドラッグの始点の色がうすくなる
     - 行き先候補の石の周囲に色がつく
    */

    public void run() {                 // スレッド処理を再定義

        List<Stone> stoneList = pegsolitaire.getList();

        while(true){

            while(true){

                if(gameState == 0){
                    int cnt = 0;

                    for(Stone stone : stoneList) {
                        stone.setAround(false);
                        if(stone.getState() == 2){
                            for(Stone tmp : stoneList){
                                if(tmp.getIndexX() == stone.getIndexX() && tmp.getIndexY() == stone.getIndexY() - 1 && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() && next.getIndexY() == stone.getIndexY() - 2 && next.getState() == 0){
                                            stone.setAround(true);
                                            cnt++;
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() && tmp.getIndexY() == stone.getIndexY() + 1 && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() && next.getIndexY() == stone.getIndexY() + 2 && next.getState() == 0){
                                            stone.setAround(true);
                                            cnt++;
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() - 1 && tmp.getIndexY() == stone.getIndexY() && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() - 2 && next.getIndexY() == stone.getIndexY() && next.getState() == 0){
                                            stone.setAround(true);
                                            cnt++;
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() + 1 && tmp.getIndexY() == stone.getIndexY() && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() + 2 && next.getIndexY() == stone.getIndexY() && next.getState() == 0){
                                            stone.setAround(true);
                                            cnt++;
                                            break;
                                        }
                                    }

                                }

                            }
                        }

                    }
                    if(cnt == 0){
                        gameState = -1;
                        break;
                    }

                }else{

                    for(Stone stone : stoneList){
                        stone.setAround(false);
                        if(stone.getState() == 1){
                            for(Stone tmp : stoneList){
                                if(tmp.getIndexX() == stone.getIndexX() && tmp.getIndexY() == stone.getIndexY() - 1 && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() && next.getIndexY() == stone.getIndexY() - 2 && next.getState() == 0){
                                            next.setAround(true);
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() && tmp.getIndexY() == stone.getIndexY() + 1 && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() && next.getIndexY() == stone.getIndexY() + 2 && next.getState() == 0){
                                            next.setAround(true);
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() - 1 && tmp.getIndexY() == stone.getIndexY() && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() - 2 && next.getIndexY() == stone.getIndexY() && next.getState() == 0){
                                            next.setAround(true);
                                            break;
                                        }
                                    }

                                }
                                if(tmp.getIndexX() == stone.getIndexX() + 1 && tmp.getIndexY() == stone.getIndexY() && tmp.getState() == 2){
                                    for(Stone next : stoneList){
                                        if(next.getIndexX() == stone.getIndexX() + 2 && next.getIndexY() == stone.getIndexY() && next.getState() == 0){
                                            next.setAround(true);
                                            break;
                                        }
                                    }

                                }

                            }

                        }
                    }

                }

                repaint();

            }

            int num = 0;

            for(Stone stone : stoneList){
                if(stone.getState() != 2){
                    num++;
                }
            }
            JFrame frame2 = new JFrame();
            JOptionPane.showMessageDialog(frame2, "game over \n score : " + num);


            for(Stone stone : stoneList){
                if(stone.getIndexX() == 3 && stone.getIndexY() == 3){
                    stone.setState(0);
                }else{
                    stone.setState(2);
                }
                stone.setAround(false);
            }
            gameState = 0;
            repaint();
        }
    }

}
