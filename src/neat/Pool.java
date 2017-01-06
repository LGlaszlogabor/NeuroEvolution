package neat;

import util.Constants;

import java.util.*;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Pool {
    private List<Species> species;
    private int generation;
    private static int innovation;
    private int currentSpecies;
    private int currentGenome;
    private int currentFrame;
    private double maxFitness;

    public Pool(){
        species = new ArrayList<>();
        generation = 0;
        currentSpecies = 0;
        currentGenome = 0;
        currentFrame = 0;
        maxFitness = 0;
        innovation = Constants.OUTPUTS;
    }

    public void initializePool(String inputs){
        for(int i = 0; i< Constants.POPULATION ;i++){
            addToSpecies(Genome.BasicGenome());
        }
        initializeRun(inputs);
    }

    private void addToSpecies(Genome g){
        boolean foundSpecies = false;
        for(Species s : species){
            if(!foundSpecies && Genome.sameSpecies(g, s.getGenomes().get(0))){
                s.getGenomes().add(g);
                foundSpecies = true;
            }
        }
        if(!foundSpecies){
            Species s = new Species();
            s.getGenomes().add(g);
            species.add(s);
        }
    }

    public void initializeRun(String inputs){
        currentFrame = 0;
        Genome g = species.get(currentSpecies).getGenomes().get(currentGenome);
        g.generateNetwork();
        evaluateCurrent(inputs);
    }

    public String evaluateCurrent(String inputs){
        return species.get(currentSpecies).getGenomes().get(currentGenome).evaluateNetwork(inputs);
    }

    public void nextGenome(){
        currentGenome++;
        if(currentGenome >= species.get(currentSpecies).getGenomes().size()){
            currentGenome = 0;
            currentSpecies++;
            if(currentSpecies >= species.size()){
                newGeneration();
                currentSpecies = 0;
            }
        }
    }

    private void rankGlobally(){
        List<Genome> global = new ArrayList<>();
        for(Species s : species){
            for(Genome g : s.getGenomes()){
                global.add(g);
            }
        }
        Comparator<Genome> comparator = new Comparator<Genome>() {
            @Override
            public int compare(Genome o1, Genome o2) {
                return ((Double)o1.getFitness()).compareTo(o2.getFitness());
            }
        };
        Collections.sort(global, comparator);
        for(int g = 0; g < global.size(); g++){
            global.get(g).setGlobalRank(g);
        }
    }

    public void newGeneration(){
        cullSpecies(false);
        rankGlobally();
        removeStaleSpecies();
        rankGlobally();
        for(Species s : species){
            s.calculateAverageFitness();
        }
        removeWeakSpecies();
        double sum = totalAverageFitness();
        double breed;
        List<Genome> children = new ArrayList<>();
        for(Species s : species){
            breed = Math.floor(s.getAverageFitness() / sum * Constants.POPULATION) - 1;
            for(int i = 0; i < breed; i++){
                children.add(s.breedChild());
            }
        }
        cullSpecies(true);
        while(children.size() + species.size() < Constants.POPULATION){
            children.add(species.get((int) (Math.random()*species.size())).breedChild());
        }
        for(Genome g : children){
            addToSpecies(g);
        }
        generation++;
    }

    public boolean fitnessAreadyMeasured(){
        return species.get(currentSpecies).getGenomes().get(currentGenome).getFitness() != 0;
    }

    public double totalAverageFitness(){
        double total = 0;
        for(Species s : species){
            total += s.getAverageFitness();
        }
        return total;
    }

    public void cullSpecies(boolean toOne){
        Comparator<Genome> comparator = new Comparator<Genome>() {
            @Override
            public int compare(Genome o1, Genome o2) {
                return ((Double)o2.getFitness()).compareTo(o1.getFitness());
            }
        };
        double remaining;
        for(Species s : species){
            Collections.sort(s.getGenomes(), comparator);
            remaining = Math.ceil(s.getGenomes().size()/2);
            if(toOne){
                remaining = 1;
            }
            while(s.getGenomes().size() > remaining){
                s.getGenomes().remove(0);
            }
        }
    }

    public void removeStaleSpecies(){
        Comparator<Genome> comparator = new Comparator<Genome>() {
            @Override
            public int compare(Genome o1, Genome o2) {
                return ((Double)o2.getFitness()).compareTo(o1.getFitness());
            }
        };
        List<Species> survived = new ArrayList<>();
        for(Species s : species){
            Collections.sort(s.getGenomes(), comparator);
            if(s.getGenomes().get(0).getFitness() > s.getTopFitness()){
                s.setTopFitness(s.getGenomes().get(0).getFitness());
                s.setStaleness(0);
            }
            else{
                s.setStaleness(s.getStaleness() + 1);
            }
            if(s.getStaleness() < Constants.STALE_SPECIES || s.getTopFitness() >= maxFitness){
                survived.add(s);
            }
        }
        species = survived;
    }

    public void removeWeakSpecies(){
        List<Species> survived = new ArrayList<>();
        double sum = totalAverageFitness();
        double breed;
        for(Species s : species){
            breed = Math.floor(s.getAverageFitness() / sum * Constants.POPULATION);
            if(breed >= 1){
                survived.add(s);
            }
        }
        species = survived;
    }

    public static int getInnovation(){
        innovation++;
        return innovation;
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
