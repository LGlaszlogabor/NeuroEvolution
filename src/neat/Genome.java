package neat;

import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Genome {
    private List<Gene> genes;
    private double fitness;
    private double adjustedFitness;
    private List<Neuron> network;
    private int maxNeuron;
    private int globalRank;



    private HashMap<String, Double> mutationRates;

    public Genome(){
        genes = new ArrayList<>();
        fitness = 0d;
        adjustedFitness = 0d;
        network = new ArrayList<>();
        maxNeuron = 0;
        globalRank = 0;
        mutationRates = new HashMap<>();
        mutationRates.put("connections", Constants.MUTATE_CONNECTIONS_CHANCE);
        mutationRates.put("link", Constants.MUTATE_LINK_CHANCE);
        mutationRates.put("bias", Constants.MUTATE_BIAS_CHANCE);
        mutationRates.put("node", Constants.MUTATE_NODE_CHANCE);
        mutationRates.put("enable", Constants.MUTATE_ENABLE_CHANCE);
        mutationRates.put("disable", Constants.MUTATE_DISABLE_CHANCE);
        mutationRates.put("step", Constants.STEP_SIZE);
    }

    public Genome copy(){
        Genome cp = new Genome();
        for(Gene g:genes) {
            cp.getGenes().add(g.copy());
        }
        cp.setMaxNeuron(maxNeuron);
        cp.getMutationRates().put("connections", mutationRates.get("connections"));
        cp.getMutationRates().put("link", mutationRates.get("link"));
        cp.getMutationRates().put("bias", mutationRates.get("bias"));
        cp.getMutationRates().put("node", mutationRates.get("node"));
        cp.getMutationRates().put("enable", mutationRates.get("enable"));
        cp.getMutationRates().put("disable", mutationRates.get("disable"));
        cp.getMutationRates().put("step", mutationRates.get("step"));
        return cp;
    }

    public HashMap<String, Double> getMutationRates() {
        return mutationRates;
    }

    public void setMutationRates(HashMap<String, Double> mutationRates) {
        this.mutationRates = mutationRates;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getAdjustedFitness() {
        return adjustedFitness;
    }

    public void setAdjustedFitness(double adjustedFitness) {
        this.adjustedFitness = adjustedFitness;
    }

    public List<Neuron> getNetwork() {
        return network;
    }

    public void setNetwork(List<Neuron> network) {
        this.network = network;
    }

    public int getMaxNeuron() {
        return maxNeuron;
    }

    public void setMaxNeuron(int maxNeuron) {
        this.maxNeuron = maxNeuron;
    }

    public int getGlobalRank() {
        return globalRank;
    }

    public void setGlobalRank(int globalRank) {
        this.globalRank = globalRank;
    }
}
