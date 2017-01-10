package thread;

import event.InputEvent;
import listener.InputListener;
import neat.Genome;
import neat.Pool;
import neat.Species;
import util.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class CommunicationThread extends Thread {
    private LinkedList<InputListener> inputListeners = new LinkedList<>();
    private ServerSocket ss;
    private String output;

    public void run(){
        try {
            ss = new ServerSocket(2222);
            Socket client;
            InputStream in = null;
            OutputStream out = null;
            BufferedReader reader;
            PrintWriter writer;
            Pool pool = new Pool();
            String initialState = "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 ";
            pool.initializePool(initialState);
            notifyInputEvent(initialState, pool);
            int timeout = Constants.TIMEOUT;
            Species species;
            Genome genome;
            int rightmost = 0;
            String[] parts;
            int marioX;
            double fitness;
            int timeoutBonus;
            int measured;
            int total;
            String inputMap;

            client = ss.accept();
            output = "";
            try {
                out = client.getOutputStream();
                in = client.getInputStream();
            } catch (IOException e) {
                System.out.println("IO Error!!! - ID:"+client.getInetAddress().toString() +
                        " --- Date:"+  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            }
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(new OutputStreamWriter(out));
            String input = "";
            while(!"END".equals(input)){
                if(pool.isRunning()) {


                    input = reader.readLine();
                    parts = input.split(" ");
                    inputMap = input.substring(parts[0].length() + 1);

                    species = pool.getSpecies().get(pool.getCurrentSpecies());
                    genome = species.getGenomes().get(pool.getCurrentGenome());


                    notifyInputEvent(inputMap, pool);
                    // System.out.println(input);


                    if (pool.getCurrentFrame() % 5 == 0) {
                        output = pool.evaluateCurrent(inputMap);
                    }
                    timeout--;


                    marioX = Integer.parseInt(parts[0]);
                    if (marioX > rightmost) {
                        rightmost = marioX;
                        timeout = Constants.TIMEOUT;
                    }


                    pool.setCurrentFitness(rightmost - pool.getCurrentFrame() / 2);
                    timeoutBonus = pool.getCurrentFrame() / 4;
                    if (timeout + timeoutBonus <= 0) { // ha lejart egy run
                        fitness = rightmost - pool.getCurrentFrame() / 2;
                        if (rightmost > 4816) fitness += 1000;
                        if (fitness == 0) fitness = -1;
                        genome.setFitness(fitness);

                        if (fitness > pool.getMaxFitness()) {
                            pool.setMaxFitness(fitness);
                            pool.exportToFile("Save" + pool.getGeneration() + ".save");
                        }
                        pool.setCurrentSpecies(0);
                        pool.setCurrentGenome(0);
                        while (pool.fitnessAreadyMeasured()) {
                            pool.nextGenome();
                        }
                        pool.initializeRun(initialState);
                        rightmost = 0;
                    }
                    measured = 0;
                    total = 0;
                    for (Species s : pool.getSpecies()) {
                        for (Genome g : s.getGenomes()) {
                            total++;
                            if (g.getFitness() != 0) {
                                measured++;
                            }
                        }
                    }

                    pool.setCurrentFrame(pool.getCurrentFrame() + 1);
                    if (timeout + timeoutBonus <= 0) {
                        writer.println("Initialize");
                        writer.flush();
                        output = "";
                    } else {
                        // System.out.println(pool.getCurrentFrame()+ "<---->"+output);
                        writer.println(output);
                        writer.flush();
                        output = "";
                    }
                }
            }
            reader.close();
            writer.close();
            client.close();
        } catch (IOException e) {
        }
    }

    public synchronized void addInputListener(InputListener il){
        inputListeners.add(il);
    }

    public synchronized void removeInputListener(InputListener il){
        inputListeners.remove(il);
    }

    public synchronized void notifyInputEvent(String surroundings, Pool g) {
        InputEvent ie = new InputEvent(this, surroundings, g);
        for(InputListener il:inputListeners) {
            il.input(ie);
        }
    }

    public void setOutput(int output){
        this.output += output + " ";
    }
}