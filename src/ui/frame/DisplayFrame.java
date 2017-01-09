package ui.frame;

import ui.panel.DisplayPanel;

import javax.swing.*;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class DisplayFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public DisplayFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(new DisplayPanel());
        setBounds(100,100,680,680);
        setVisible(true);
    }
}
