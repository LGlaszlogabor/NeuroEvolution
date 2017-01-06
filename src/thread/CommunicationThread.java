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
    private boolean isRunning;
    private String output;

    public void run(){
        isRunning = true;
        try {
            ss = new ServerSocket(2222);
            Socket client;
            InputStream in = null;
            OutputStream out = null;
            BufferedReader reader;
            PrintWriter writer;
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

            Pool pool = new Pool();
            pool.initializePool("0 0 0 0 0 0 0 0 0 0 0 0 0 " +
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
                                "0 0 0 0 0 0 0 0 0 0 0 0 0 ");
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
            while(!"END".equals(input)){
                input = reader.readLine();
                notifyInputEvent(input);

                species = pool.getSpecies().get(pool.getCurrentSpecies());
                genome = species.getGenomes().get(pool.getCurrentGenome());

                parts = input.split(" ");

                if(pool.getCurrentFrame() % 5== 0){
                    output = pool.evaluateCurrent(input.substring(parts[0].length() + 1));
                }
                timeout--;


                marioX = Integer.parseInt(parts[0]);
                if (marioX > rightmost) {
                    rightmost = marioX;
                    timeout = Constants.TIMEOUT;
                }





                timeoutBonus = pool.getCurrentFrame() / 4;
                if(timeout + timeoutBonus <= 0){ // ha lejart egy run
                    fitness = rightmost - pool.getCurrentFrame() / 2;
                    if(rightmost > 4816) fitness += 1000;
                    if(fitness == 0) fitness = -1;
                    genome.setFitness(fitness);
                    if(fitness > pool.getMaxFitness()){
                        pool.setMaxFitness(fitness);
                    }
                    pool.setCurrentSpecies(0);
                    pool.setCurrentGenome(0);
                    while(pool.fitnessAreadyMeasured()){
                       pool.nextGenome();
                    }
                    pool.initializeRun("0 0 0 0 0 0 0 0 0 0 0 0 0 " +
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
                                       "0 0 0 0 0 0 0 0 0 0 0 0 0 ");
                    rightmost = 0;
                }
                measured = 0;
                total = 0;
                for(Species s : pool.getSpecies()){
                    for(Genome g : s.getGenomes()){
                        total++;
                        if(g.getFitness() != 0){
                            measured++;
                        }
                    }
                }

                pool.setCurrentFrame(pool.getCurrentFrame() + 1);
                if(timeout + timeoutBonus <= 0) {
                    writer.println("Initialize");
                    writer.flush();
                    output = "";
                }
                else {
                   // System.out.println(pool.getCurrentFrame()+ "<---->"+output);
                    writer.println(output);
                    writer.flush();
                    output = "";
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

    public synchronized void notifyInputEvent(String surroundings) {
        InputEvent ie = new InputEvent(this, surroundings);
        for(InputListener il:inputListeners) {
            il.input(ie);
        }
    }

    public void setOutput(int output){
        this.output += output + " ";
    }
}