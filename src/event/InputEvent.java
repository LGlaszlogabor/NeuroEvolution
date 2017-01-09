package event;

import java.util.EventObject;

import neat.Pool;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class InputEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private String surroundings;
    private Pool g;

    public InputEvent(Object source, String surroundings, Pool g){
        super(source);
        this.surroundings = surroundings;
        this.g = g;
    }

    public String getSurroundings(){
        return surroundings;
    }
    
    public Pool getPool(){
        return g;
    }
}