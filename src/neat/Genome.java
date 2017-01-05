package neat;

import util.Constants;

import java.util.*;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Genome {
    private List<Gene> genes;
    private double fitness;
    private double adjustedFitness;
    private HashMap<Integer, Neuron> network;
    private int maxNeuron;
    private int globalRank;

    private HashMap<String, Double> mutationRates;

    public Genome(){
        genes = new ArrayList<>();
        fitness = 0d;
        adjustedFitness = 0d;
        network = new HashMap<>();
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

    public static  Genome BasicGenome(){
        Genome g = new Genome();
        int innovation = 1;
        g.setMaxNeuron(Constants.INPUTS);
        g.mutate();
        return g;
    }

    public Genome copy(){
        Genome cp = new Genome();
        for(Gene g:genes) {
            cp.getGenes().add(g.copy());
        }
        cp.setMaxNeuron(maxNeuron);
        cp.getMutationRates().replace("connections", mutationRates.get("connections"));
        cp.getMutationRates().replace("link", mutationRates.get("link"));
        cp.getMutationRates().replace("bias", mutationRates.get("bias"));
        cp.getMutationRates().replace("node", mutationRates.get("node"));
        cp.getMutationRates().replace("enable", mutationRates.get("enable"));
        cp.getMutationRates().replace("disable", mutationRates.get("disable"));
        cp.getMutationRates().replace("step", mutationRates.get("step"));
        return cp;
    }


    private void mutate(){
        for (Map.Entry<String, Double> entry : mutationRates.entrySet()) {
            int random = (int )(Math.random() * 51 + 1);
            if (random % 2 == 0) {
                mutationRates.replace(entry.getKey(), 0.95 * entry.getValue());
            }else {
                mutationRates.replace(entry.getKey(), 1.05263 * entry.getValue());
            }
        }
        if((Math.random() < mutationRates.get("connection"))){
            pointMutate();
        }
        double tmp = mutationRates.get("link");
        while(tmp > 0){
            if((Math.random() < tmp)){
                linkMutate(false);
            }
            tmp--;
        }
        tmp = mutationRates.get("bias");
        while(tmp > 0){
            if((Math.random() < tmp)){
                linkMutate(true);
            }
            tmp--;
        }
        tmp = mutationRates.get("node");
        while(tmp > 0){
            if((Math.random() < tmp)){
                nodeMutate();
            }
            tmp--;
        }
        tmp = mutationRates.get("enable");
        while(tmp > 0){
            if((Math.random() < tmp)){
                enableDisableMutate(true);
            }
            tmp--;
        }
        tmp = mutationRates.get("disable");
        while(tmp > 0){
            if((Math.random() < tmp)){
                enableDisableMutate(false);
            }
            tmp--;
        }
    }

    private void pointMutate(){
        double step = mutationRates.get("step");
        for(Gene g:genes){
            if(Math.random() < Constants.PERTIRB_CHANCE) {
                g.setWeight(g.getWeight() + Math.random() * step * 2 - step);
            }else{
                g.setWeight(Math.random() * 4 - 2);
            }
        }
    }

    private void linkMutate(boolean bias){
        int neuron1 = randomNeuron(false);
        int neuron2 = randomNeuron(true);
        Gene newLink = new Gene();
        if(neuron1 < Constants.INPUTS && neuron2 < Constants.INPUTS){
            return;
        }
        int tmp;
        if(neuron2 < Constants.INPUTS){
            tmp = neuron1;
            neuron1 = neuron2;
            neuron2 = tmp;
        }

        newLink.setInto(neuron1);
        newLink.setOut(neuron2);
        if(bias){
            newLink.setInto(170);
        }
        if(containsLink(newLink)){
            return;
        }
        newLink.setInnovation(Pool.getInnovation());
        newLink.setWeight(Math.random()*4 - 2);
        genes.add(newLink);
    }

    private void nodeMutate(){
        if(genes.isEmpty()) return;
        maxNeuron++;
        Gene gene = genes.get((int) (Math.random()*genes.size()));
        if(!gene.isEnabled()){
            return;
        }
        gene.setEnabled(false);
        Gene gene1 = gene.copy();
        gene1.setOut(maxNeuron);
        gene1.setWeight(1.0);
        gene1.setInnovation(Pool.getInnovation());
        gene1.setEnabled(true);
        genes.add(gene1);

        Gene gene2 = gene.copy();
        gene2.setOut(maxNeuron);
        gene2.setInnovation(Pool.getInnovation());
        gene2.setEnabled(true);
        genes.add(gene2);
    }

    private void enableDisableMutate(boolean enable){
        List<Gene> candidates = new ArrayList<>();
        for(Gene g:genes){
            if(g.isEnabled() == !enable){
                candidates.add(g);
            }
        }
        if(candidates.isEmpty()) return;
        Gene g = candidates.get((int) (Math.random() * candidates.size()));
        g.setEnabled(!g.isEnabled());
    }

    private boolean containsLink(Gene link){
        for(Gene g:genes){
            if(g.getInto() == link.getInto() && g.getOut() == link.getOut()){
                return true;
            }
        }
        return false;
    }

    private int randomNeuron(boolean isNonInput){
        HashMap<Integer, Boolean> neurons = new HashMap<>();
        if(!isNonInput){
            for(int i=1; i < Constants.INPUTS + 1; i++){
                neurons.put(i, true);
            }
        }
        for(int o = 1; o < Constants.OUTPUTS + 1 ; o++){
            neurons.put(Constants.MAX_NODES + o, true);
        }
        for(Gene g: genes){
            if(!isNonInput || g.getInto() > Constants.INPUTS){
                neurons.put(g.getInto(), true);
            }
            if(!isNonInput || g.getOut() > Constants.INPUTS){
                neurons.put(g.getOut(), true);
            }
        }
        int n = (int) (Math.random() * neurons.size() + 1);
        for(int k :neurons.keySet()){
            n--;
            if(n == 0) return k;
        }
        return 0;
    }

    public void generateNetwork(){
        for(int i=1; i< Constants.INPUTS + 1;i++){
            network.put(i, new Neuron());
        }
        for(int i=1; i< Constants.OUTPUTS + 1;i++){
            network.put(Constants.MAX_NODES + i, new Neuron());
        }
        Arrays.sort(genes.toArray());
        Collections.sort(genes, new Comparator<Gene>() {
            @Override
            public int compare(Gene o1, Gene o2) {
                return ((Integer)o1.getOut()).compareTo(o2.getOut());
            }
        });
        for(Gene g : genes){
            if(g.isEnabled()){
                if(!network.containsKey(g.getOut())){
                    network.put(g.getOut(), new Neuron());
                }
                Neuron tmp = network.get(g.getOut());
                tmp.getIncoming().add(g);
                if(!network.containsKey(g.getInto())){
                    network.put(g.getInto(), new Neuron());
                }
            }
        }
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

    public HashMap<Integer, Neuron> getNetwork() {
        return network;
    }

    public void setNetwork(HashMap<Integer, Neuron> network) {
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
