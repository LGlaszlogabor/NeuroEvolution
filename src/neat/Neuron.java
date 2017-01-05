package neat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Neuron {
    private List<Neuron> incoming;
    private double value;

    public Neuron(){
        incoming =  new ArrayList<>();
        value = 0;
    }

    public double getValue(){
        return value;
    }

    public void addIncomingNeuron(Neuron n){
        incoming.add(n);
    }
}
