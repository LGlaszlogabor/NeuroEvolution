package neat;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Gene {
    private int into;
    private int out;
    private double weight;
    private boolean enabled;
    private double innovation;

    public Gene(){
        into = 0;
        out = 0;
        weight = 0d;
        enabled = true;
        innovation = 0d;
    }

    public Gene copy() {
        Gene cp = new Gene();
        cp.setInto(into);
        cp.setOut(out);
        cp.setWeight(weight);
        cp.setEnabled(enabled);
        cp.setInnovation(innovation);
        return cp;
    }

    public int getInto() {
        return into;
    }

    public void setInto(int into) {
        this.into = into;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getInnovation() {
        return innovation;
    }

    public void setInnovation(double innovation) {
        this.innovation = innovation;
    }
}
