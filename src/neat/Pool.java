package neat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Pool {
    private List<Species> species;
    private int generation;
    private List<Integer> innovation;
    private int currentSpecies;
    private int currentGenome;
    private int currentFrame;
    private double maxFitness;

    public Pool(){
        species = new ArrayList<>();
        generation = 0;
        currentSpecies = 1;
        currentGenome = 1;
        currentFrame = 0;
        maxFitness = 0;
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        innovation = Arrays.asList(arr);
    }

    public List<Species> getSpecies() {
        return species;
    }

    public void setSpecies(List<Species> species) {
        this.species = species;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public List<Integer> getInnovation() {
        return innovation;
    }

    public void setInnovation(List<Integer> innovation) {
        this.innovation = innovation;
    }

    public int getCurrentSpecies() {
        return currentSpecies;
    }

    public void setCurrentSpecies(int currentSpecies) {
        this.currentSpecies = currentSpecies;
    }

    public int getCurrentGenome() {
        return currentGenome;
    }

    public void setCurrentGenome(int currentGenome) {
        this.currentGenome = currentGenome;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public double getMaxFitness() {
        return maxFitness;
    }

    public void setMaxFitness(double maxFitness) {
        this.maxFitness = maxFitness;
    }
}
