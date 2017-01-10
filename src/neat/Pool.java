package neat;

import util.Constants;

import java.io.*;
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
    private double currentFitness;
    private boolean running;
    
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
        running = true;
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
        	
        	System.out.println("--------Uj faj----------------------------------------------("+species.size()+")----------------");
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
        if(currentGenome > species.get(currentSpecies).getGenomes().size() - 1){
            currentGenome = 0;
            currentSpecies++;
            if(currentSpecies > species.size() - 1){
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
    	System.out.println("elso cull elott size" + species.size());
        cullSpecies(false);
        System.out.println("elso cull utan size" + species.size());
        rankGlobally();
        removeStaleSpecies();
        System.out.println(" stale remove utan size" + species.size());
        rankGlobally();
        for(Species s : species){
            s.calculateAverageFitness();
        }
        removeWeakSpecies();
        System.out.println("weak remove utan" + species.size());
        double sum = totalAverageFitness();
        double breed;
        List<Genome> children = new ArrayList<>();
        for(Species s : species){
            breed = Math.floor(s.getAverageFitness() / sum * Constants.POPULATION) - 1;
            for(int i = 0; i < breed; i++){
                children.add(s.breedChild());
            }
        }
        System.out.println("breed utan" + species.size());
        cullSpecies(true);
        System.out.println("msodik cull utan" + species.size());
       
        while(children.size() + species.size() < Constants.POPULATION){
            children.add(species.get((int) (Math.random()*(species.size()-1))).breedChild());
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
                return ((Double)o1.getFitness()).compareTo(o2.getFitness());
            }
        };
        double remaining;
        int size;
        int i=0;
        for(Species s : species){
            Collections.sort(s.getGenomes(), comparator);
            remaining = Math.ceil(s.getGenomes().size()/2);
            if(toOne){
                remaining = 1;
            }
            size = s.getGenomes().size();
            System.out.println(i++ + "species cull , genmsize before" + size);
            while(size-1 > remaining){
                s.getGenomes().remove(0);
                size = s.getGenomes().size();
            }
            System.out.println(i++ + "species cull , genmsize after" + size);
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
           // System.out.println(species.size() + "Genome size" + s.getGenomes().size());
            if(s.getGenomes().get(0).getFitness() > s.getTopFitness()){
                s.setTopFitness(s.getGenomes().get(0).getFitness());
                s.setStaleness(0);
            }
            else{
                s.setStaleness(s.getStaleness() + 1);
            }
            if(s.getStaleness() < Constants.STALE_SPECIES || s.getTopFitness() > maxFitness){
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

    public void exportToFile(String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(generation + "\n");
            fw.write(maxFitness + "\n");
            fw.write(species.size() + "\n");
            for(Species s : species){
                fw.write(s.getTopFitness() + "\n");
                fw.write(s.getStaleness() + "\n");
                fw.write(s.getGenomes().size() + "\n");
                for(Genome g : s.getGenomes()){
                    fw.write(g.getFitness() + "\n");
                    fw.write(g.getMaxNeuron() + "\n");
                    for (Map.Entry<String, Double> entry : g.getMutationRates().entrySet()) {
                        fw.write(entry.getKey() + "\n");
                        fw.write(entry.getValue() + "\n");
                    }
                    fw.write(g.getGenes().size() + "\n");
                    for(Gene gene : g.getGenes()) {
                        fw.write(gene.getInto() + "\n");
                        fw.write(gene.getOut() + "\n");
                        fw.write(gene.getWeight() + "\n");
                        fw.write(gene.getInnovation() + "\n");
                        if(gene.isEnabled()){
                            fw.write("1\n");
                        }
                        else{
                            fw.write("0\n");
                        }
                    }
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromFile(String filename){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            generation = Integer.parseInt(br.readLine());
            maxFitness = Double.parseDouble(br.readLine());
            species = new ArrayList<>();
            List<Genome> tmpGenomes = new ArrayList<>();
            List<Gene> tmpGenes = new ArrayList<>();
            int speciesSize = Integer.parseInt(br.readLine()), genomeSize, geneSize;
            Species tmpSpecies;
            Genome tmpGenome;
            Gene tmpGene;
            for(int i=0;i<speciesSize;i++){
                tmpSpecies = new Species();
                tmpSpecies.setTopFitness(Double.parseDouble(br.readLine()));
                tmpSpecies.setStaleness(Double.parseDouble(br.readLine()));
                genomeSize = Integer.parseInt(br.readLine());
                for(int j=0;j<genomeSize;j++){
                    tmpGenome = new Genome();
                    tmpGenome.setFitness(Double.parseDouble(br.readLine()));
                    tmpGenome.setMaxNeuron(Integer.parseInt(br.readLine()));
                    for (Map.Entry<String, Double> entry : tmpGenome.getMutationRates().entrySet()) {
                        tmpGenome.getMutationRates().replace(br.readLine(), Double.parseDouble(br.readLine()));
                    }
                    geneSize = Integer.parseInt(br.readLine());
                    for(int k=0;k<geneSize;k++){
                        tmpGene = new Gene();
                        tmpGene.setInto(Integer.parseInt(br.readLine()));
                        tmpGene.setOut(Integer.parseInt(br.readLine()));
                        tmpGene.setWeight(Double.parseDouble(br.readLine()));
                        tmpGene.setInnovation(Integer.parseInt(br.readLine()));
                        if(1 == Integer.parseInt(br.readLine())){
                            tmpGene.setEnabled(true);
                        }else{
                            tmpGene.setEnabled(false);
                        }
                        tmpGenes.add(tmpGene);
                    }
                    tmpGenome.setGenes(tmpGenes);
                    tmpGenomes.add(tmpGenome);
                }
                tmpSpecies.setGenomes(tmpGenomes);
                species.add(tmpSpecies);
            }
            br.close();
            while(fitnessAreadyMeasured()){
                nextGenome();
            }
            initializeRun("0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 " +
                          "0 0 0 0 0 0 0 0 0 0 0 0 0 ");
            currentFrame++;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    public double getCurrentFitness() {
  		return currentFitness;
  	}

  	public void setCurrentFitness(double fitness) {
  		this.currentFitness = fitness;
  	}

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
