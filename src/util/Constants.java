package util;

/**
 * Created by Laszlo Gabor on 05.01.2017.
 */
public class Constants {
    public static final int INPUTS = 170;
    public static final int OUTPUTS = 8;
    public static final int POPULATION = 300;  //300
    public static final double DELTA_DISJOINT = 2.0;
    public static final double DELTA_WEIGHTS = 0.4;
    public static final double DELTA_THRESHOLD = 1.0;

    public static final double PERTIRB_CHANCE = 0.90;//0.90
    public static final double CROSSOVER_CHANCE = 0.85;
    public static final double MUTATE_CONNECTIONS_CHANCE = 0.25; //0.25
    public static final double MUTATE_LINK_CHANCE = 2.0; //2
    public static final double MUTATE_BIAS_CHANCE = 0.4;//0.4
    public static final double MUTATE_NODE_CHANCE = 0.5; //0.5
    public static final double MUTATE_ENABLE_CHANCE = 0.2;//0.2
    public static final double MUTATE_DISABLE_CHANCE = 0.4;//0.4
    public static final double STEP_SIZE = 0.1;
    public static final int STALE_SPECIES = 13; //15

    public static final int TIMEOUT = 20;

    public static final int MAX_NODES = 1000000;
}
