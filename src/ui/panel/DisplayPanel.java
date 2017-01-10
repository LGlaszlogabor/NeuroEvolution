package ui.panel;

import event.InputEvent;
import listener.InputListener;
import neat.Gene;
import neat.Genome;
import neat.Neuron;
import neat.Pool;
import thread.CommunicationThread;
import util.Cell;
import util.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class DisplayPanel extends JPanel implements InputListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private CommunicationThread reader;
    private int[] surroundings = new int[169];
    private Pool pool;
    private boolean displayNetwork;
	private JButton loadButton, saveButton;
    

    public DisplayPanel(){
		loadButton = new JButton("Load");
		saveButton = new JButton("Save");
    	loadButton.setBounds(400,500,100,30);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pool.setRunning(false);
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Save files", "save");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(DisplayPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getName());
					pool.importFromFile(chooser.getSelectedFile().getName());
				}
				pool.setRunning(true);
			}
		});
    	saveButton.setBounds(400,550,100,30);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pool.exportToFile("custom"+pool.getGeneration()+".save");
			}
		});
		displayNetwork = false;
        setLayout(null);
        setBounds(0,0,680,680);
       // addKeyListener(this);
        reader = new CommunicationThread();
        reader.addInputListener(this);
        reader.start();

		add(saveButton);

		add(loadButton);
		setFocusable(true);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.white);
        g.fillRect(0,0,680,450);

        g.setColor(Color.black);
        g.drawRect(50, 50, 260, 260);
        
    	if(displayNetwork){
    		Genome toDisplay = pool.getSpecies().get(pool.getCurrentSpecies()).getGenomes().get(pool.getCurrentGenome());
	        HashMap<Integer, Neuron> network = toDisplay.getNetwork();
	        HashMap<Integer, Cell> cells = new HashMap<>();
	        int radius = 6;
	        int i = 1;
	        for(int dy = -1*radius; dy <= radius; dy++){
	        	for(int dx = -1*radius; dx <= radius; dx++){
	            	cells.put(i, new Cell(180 + 20*dx, 180 + 20*dy, network.get(i).getValue()));
	            	i++;
	            }
	        }
	        cells.put(Constants.INPUTS, new Cell(280, 280, network.get(Constants.INPUTS).getValue()));
	        String[] buttonNames = {"tmp","A", "B", "X", "Y", "Up", "Down", "Left", "Right"};
	        for(int o = 1; o <= Constants.OUTPUTS; o++){
	        	cells.put(Constants.MAX_NODES + o, new Cell(600, 50 + 30 * o, network.get(Constants.MAX_NODES + o).getValue()));
	        	if(network.get(Constants.MAX_NODES + o).getValue() > 0){
	        		g.setColor(Color.red);
	        	}else{
	        		g.setColor(new Color(255, 0, 0, 200));
	        	}
	        	g.drawChars(buttonNames[o].toCharArray(), 0, buttonNames[o].length(), 625, 55 + 30 * o);
	        }
	        for (Entry<Integer, Neuron> entry : network.entrySet()) {
	           if(entry.getKey() > Constants.INPUTS && entry.getKey() <= Constants.MAX_NODES){
	        	   cells.put(entry.getKey(), new Cell(330, 70, entry.getValue().getValue()));
	           }
	        }
	        Cell c1, c2;
	        for (int n = 1; n <= 4; n++){
	        	for(Gene gene:toDisplay.getGenes()){
	        		if(gene.isEnabled()){
	        			c1 = cells.get(gene.getInto());
	        			c2 = cells.get(gene.getOut());
		        		if(gene.getInto() > Constants.INPUTS && gene.getInto() <= Constants.MAX_NODES){
		        			c1.setX((int) (0.75 * c1.getX() + 0.25 * c2.getX()));
		        			if(c1.getX() >= c2.getX()){
			        			c1.setX(c1.getX() - 30);
			        		}
			        		if(c1.getX() > 620){
			        			c1.setX(620);
			        		}
			        		if(c1.getX() < 330){
			        			c1.setX(330);
			        		}
			        		c1.setY((int) (0.75 * c1.getY() + 0.25 * c2.getY()));
		        		}
		        		if(gene.getOut() > Constants.INPUTS + 1 && gene.getOut() <= Constants.MAX_NODES){
		        			c2.setX((int) (0.25 * c1.getX() + 0.75 * c2.getX()));
		        			if(c1.getX() >= c2.getX()){
			        			c2.setX(c2.getX() + 30);
			        		}
			        		if(c2.getX() < 330){
			        			c2.setX(330);
			        		}
			        		if(c2.getX() > 620){
			        			c2.setX(620);
			        		}
			        		c2.setY((int) (0.25 * c1.getY() + 0.75 * c2.getY()));
		        		}
	        		}
	        	}
	        }
	       /// g.drawRect(50 - radius*20 - 3, 70 - radius*20 -3, 2*radius*5 + 5, 2*radius*5 + 5);
	        for (Entry<Integer, Cell> entry : cells.entrySet()) {
	            if(entry.getKey() > Constants.INPUTS || entry.getValue().getValue() != 0){
	            	int color = (int) Math.floor((entry.getValue().getValue() + 1)/2*256);
	            	if (color > 255)
	            		color = 255;
	            	if (color < 0)
	            		color = 0;
	            	int opacity = 0xFF000000;
	            	if (entry.getValue().getValue() == 0)
	            		opacity = 0x50000000;
	            	color = opacity + color*0x10000 + color*0x100 + color;
	            	g.setColor(Color.BLACK);
	            	g.drawRect(entry.getValue().getX()-10,entry.getValue().getY()-10,20,20);
	            }
	         }
	        Color c;
	        for(Gene gene : toDisplay.getGenes()){
	        	if(gene.isEnabled()){
	        		c1 = cells.get(gene.getInto());
	        		c2 = cells.get(gene.getOut());
	        		if (c1.getValue() == 0){
	        			if (gene.getWeight() > 0){ 
		        			c = new Color(255,0,0,255);
		        		}else{
		        			c = new Color(255,0,0, 80);
		        		}
	        		}
	        		else{
	        			if (gene.getWeight() > 0){ 
		        			c = new Color(0,255,0,255);
		        		}else{
		        			c = new Color(0,255,0,80);
		        		}
	        		}
					g.setColor(c);
	        		g.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
	        	}
	        }
	        g.setColor(new Color(0,0,0,60));
	    	g.drawLine(0,360,680,360);
	    	g.setColor(Color.black);
	    	String tmp = "Generation: " + pool.getGeneration();
	    	g.drawChars(tmp.toCharArray(), 0, tmp.length(), 50, 380);
	    	tmp = "Species: " + (pool.getCurrentSpecies() + 1);
	    	g.drawChars(tmp.toCharArray(), 0, tmp.length(), 200, 380);
	       	tmp = "Genome: " + (pool.getCurrentGenome() + 1);
	    	g.drawChars(tmp.toCharArray(), 0, tmp.length(), 50, 400);
	       	tmp = "Current Fitness: " + pool.getCurrentFitness();
	    	g.drawChars(tmp.toCharArray(), 0, tmp.length(), 50, 420);
	    	tmp = "Max Fitness: " + pool.getMaxFitness();
	    	g.drawChars(tmp.toCharArray(), 0, tmp.length(), 200, 420);
	    	// g.drawRect(49,71,2,7);
    	}
    	for(int i=0;i<13;i++) {
			for (int j = 0; j < 13; j++) {
				if (surroundings[i * 13 + j] == 1) {
					g.setColor(Color.black);
					g.drawRect(50 + j * 20, 50 + i * 20, 20, 20);
					g.fillRect(50 + j * 20 + 8, 50 + i * 20 + 8, 4, 4);
				} else if (surroundings[i * 13 + j] == -1) {
					g.setColor(Color.red);
					g.drawRect(50 + j * 20, 50 + i * 20, 20, 20);
					g.fillRect(50 + j * 20 + 8, 50 + i * 20 + 8, 4, 4);
				}
				if (j == 6 && (i == 7)) {
					g.setColor(Color.blue);
					g.drawRect(50 + j * 20, 50 + i * 20, 20, 20);
					g.fillRect(50 + j * 20 + 8, 50 + i * 20 + 8, 4, 4);
				} else {
					g.setColor(new Color(0, 0, 0, 20));
					g.drawRect(50 + j * 20, 50 + i * 20, 20, 20);
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
        pool = ie.getPool();
        displayNetwork = true;
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

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
