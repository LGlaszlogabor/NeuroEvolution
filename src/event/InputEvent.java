package event;

import java.util.EventObject;

/**
 * Created by Laszlo Gabor on 04.01.2017.
 */
public class InputEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private String surroundings;

    public InputEvent(Object source, String surroundings){
        super(source);
        this.surroundings = surroundings;
    }

    public String getSurroundings(){
        return surroundings;
    }
}