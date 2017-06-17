package neat;

import util.Constants;

/**
 * Created by Laszlo Gabor on 06.01.2017.
 */
public class Trainer {
    private Pool p;
    private int timeout;
    public Trainer(){
        p = new Pool();
        p.initializePool("");
        timeout = Constants.TIMEOUT;
    }

    public void runSilmulation(){
        if(p.getCurrentFrame() %5 == 0){
            p.evaluateCurrent("");
        }
        timeout--;
        int timeoutBonus = p.getCurrentFrame() / 4;
        if(timeout + timeoutBonus <= 0){

        }
    }
}
