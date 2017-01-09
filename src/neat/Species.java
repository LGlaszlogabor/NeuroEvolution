package neat;

import util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Species {
    private double topFitness;
    private double staleness;
    private List<Genome> genomes;
    private double averageFitness;

    public Species(){
        topFitness = 0;
        staleness = 0;
        genomes = new ArrayList<>();
        averageFitness = 0;
    }

    public void calculateAverageFitness(){
        int total = 0;
        for(Genome g : genomes){
            total += g.getGlobalRank();
        }
        averageFitness = total / genomes.size();
    }

    public Genome breedChild(){
        Genome g1, g2, child;
        if(Math.random() < Constants.CROSSOVER_CHANCE){
            g1 = genomes.get((int) (Math.random()*(genomes.size()-1)));
            g2 = genomes.get((int) (Math.random()*(genomes.size()-1)));
            child = Genome.crossover(g1, g2);
        }
        else{
            g1 = genomes.get((int) (Math.random()*(genomes.size()-1)));
            child = g1.copy();
        }

        child.mutate();
        return child;
    }

    public double getTopFitness() {
        return topFitness;
    }

    public void setTopFitness(double topFitness) {
        this.topFitness = topFitness;
    }

    public double getStaleness() {
        return staleness;
    }

    public void setStaleness(double staleness) {
        this.staleness = staleness;
    }

    public List<Genome> getGenomes() {
        return genomes;
    }

    public void setGenomes(List<Genome> genomes) {
        this.genomes = genomes;
    }

    public double getAverageFitness() {
        return averageFitness;
    }

    public void setAverageFitness(double averageFitness) {
        this.averageFitness = averageFitness;
    }
}
