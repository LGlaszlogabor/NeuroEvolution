package neat;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Gene {
    private double into;
    private double out;
    private double weight;
    private boolean enabled;
    private double innovation;

    public Gene(){
        into = 0d;
        out = 0d;
        weight = 0d;
        enabled = true;
        innovation = 0d;
    }

    public Gene copy(){
        Gene cp = new Gene();
        cp.setInto(into);
        cp.setOut(out);
        cp.setWeight(weight);
        cp.setEnabled(enabled);
        cp.setInnovation(innovation);
        return cp;
    }

    public double getInto() {
        return into;
    }

    public void setInto(double into) {
        this.into = into;
    }

    public double getOut() {
        return out;
    }

    public void setOut(double out) {
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
