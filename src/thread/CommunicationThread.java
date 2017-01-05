package thread;

import event.InputEvent;
import listener.InputListener;

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
            while(!"END".equals(input)){
                input = reader.readLine();
                notifyInputEvent(input);
                System.out.println("Output:" + output);
                writer.println(output);
                writer.flush();
                output = "";
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