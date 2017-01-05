package ui.panel;

import event.InputEvent;
import listener.InputListener;
import thread.CommunicationThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class DisplayPanel extends JPanel implements InputListener, KeyListener {
    CommunicationThread reader;
    int[] surroundings = new int[169];

    public DisplayPanel(){
        setLayout(null);
        setBounds(0,0,680,680);
        addKeyListener(this);
        reader = new CommunicationThread();
        reader.addInputListener(this);
        reader.start();
        setFocusable(true);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,680,680);
        for(int i=0;i<13;i++){
            for(int j=0;j<13;j++){
               if(surroundings[i*13+j] == 1){
                   g.setColor(Color.black);
                   g.fillRect(100+j*20, 100+i*20, 20, 20);
               }
               else if(surroundings[i*13+j] == -1){
                    g.setColor(Color.red);
                    g.fillRect(100+j*20, 100+i*20, 20, 20);
                }
               if(j==6 && (i==7)){
                   g.setColor(Color.blue);
                   g.fillRect(100+j*20, 100+i*20, 20, 20);
               }
            }
        }

    }

    @Override
    public void input(InputEvent ie) {
        String[] poss = ie.getSurroundings().split(" ");
        for(int i=0;i<169;i++){
            surroundings[i] = Integer.parseInt(poss[i]);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            reader.setOutput(7);
        }
        if (key == KeyEvent.VK_RIGHT) {
            reader.setOutput(8);
        }
        if (key == KeyEvent.VK_UP) {
            reader.setOutput(5);
        }
        if (key == KeyEvent.VK_DOWN) {
            reader.setOutput(6);
        }
        if(key == KeyEvent.VK_A){
            reader.setOutput(1);
        }
        if(key == KeyEvent.VK_S){
            reader.setOutput(2);
        }
        if(key == KeyEvent.VK_Z){
            reader.setOutput(3);
        }
        if(key == KeyEvent.VK_X){
            reader.setOutput(4);
        }
        System.out.print(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
