import java.lang.StringBuilder;
import java.util.*;
/***
author:@Akshay
 */

class Knap{

    static Scanner sc;
    private int crossover_count = 0;
    private int clone_count = 0;
    private int no_items = 0;
    private int population_size = 0;
    private int maximum_generations = 0;
    private int generation_counter = 1;
    private double knapsack_capacity = 0;
    private double prob_crossover = 0;
    private double prob_mutation = 0;
    private double total_fitness_of_generation = 0;
    private ArrayList<Integer> value = new ArrayList<Integer>();
    private ArrayList<Integer> weight = new ArrayList<Integer>();
    private ArrayList<Double> fitness = new ArrayList<Double>();
    private ArrayList<Double> generation_fitness = new ArrayList<Double>();
    private ArrayList<String> population = new ArrayList<String>();
    private ArrayList<String> next_gen = new ArrayList<String>();
    private ArrayList<String> generation_solution = new ArrayList<String>();

    Knap(){
        no_items = sc.nextInt();
        for(int i = 0; i < no_items; i++) {
            System.out.println("Enter the value of item "+(i+1));
            value.add(sc.nextInt());
            System.out.println("Enter the weigth of item "+(i+1));
            weight.add(sc.nextInt());
        }

        // Capacity of knapsack
        System.out.println("Enter the capacity of knapsack");
        knapsack_capacity = sc.nextInt();
        // Population size
        System.out.println("Enter the population size ");
        population_size = sc.nextInt();
        
        // Crossover probability
        System.out.println("Enter crossover probability");
        prob_crossover = sc.nextDouble();
        
        // Mutation probability
        System.out.println("Enter mutation probability ");
        prob_mutation = sc.nextDouble();
             
        
        maximum_generations = population_size;
        //
        this.generatePopulation();
        
        //Fitness
        this.evaluateFitness();
        System.out.println("\nFitness:");
        for(int i = 0; i < this.population_size; i++) {
            System.out.println((i + 1) + " - " + this.fitness.get(i));
        }
        
        while(maximum_generations>=0){
            

            this.evaluateFitness();
            for(int i=0;i<population_size/2;i++){
                
                if(population_size % 2 == 1) {
                    next_gen.add(generation_solution.get(generation_counter - 1));
                }
                int gene1=selectGene();
                int gene2=selectGene();

                crossoverGenes(gene1,gene2);
            }

            this.evaluateFitness();
            for(int i=0;i<population_size;i++){
                System.out.println("#"+(i+1)+" "+next_gen.get(i));
                population.add(i,next_gen.get(i));
            }
            
            System.out.println("\nFitness:");
            for(int m = 0; m < this.population_size; m++) {
               System.out.println((m + 1) + " - " + this.fitness.get(m));
            } 
            next_gen.clear();
            fitness.clear();

           

            System.out.println("Crossover occurred " + this.crossover_count + " times");
            System.out.println("Cloning occurred " + this.clone_count + " times");
            if(clone_count==0) {
                System.out.println("Mutation did not occur\n");
            }
            else{
                System.out.println("Mutation did occur\n");
            }   

            maximum_generations--;
        }
        


    }

  
    private int selectGene() {

        double rand = Math.random() * total_fitness_of_generation;
        for(int i = 0; i < population_size; i++) {
            if(rand <= fitness.get(i)) {
                return i;
            }
            rand = rand - fitness.get(i);
        }
	return 0;
    }

    private void crossoverGenes(int gene_1, int gene_2) {
      
        String new_gene_1;
        String new_gene_2;

        double rand_crossover = Math.random();
        if(rand_crossover <= prob_crossover) {
            // Perform crossover
            crossover_count = crossover_count + 1;
            Random generator = new Random(); 
            int cross_point = generator.nextInt(no_items) + 1;

            new_gene_1 = population.get(gene_1).substring(0, cross_point) + population.get(gene_2).substring(cross_point);
            new_gene_2 = population.get(gene_2).substring(0, cross_point) + population.get(gene_1).substring(cross_point);

            next_gen.add(new_gene_1);
            next_gen.add(new_gene_2);
        }
        else {
            clone_count = clone_count + 1;
            next_gen.add(population.get(gene_1));
            next_gen.add(population.get(gene_2));
        }

        mutateGene();
    }

    private void mutateGene() {
       
        double rand_mutation = Math.random();
        if(rand_mutation <= prob_mutation) {

            String mut_gene;
            String new_mut_gene;
            Random generator = new Random();
            int mut_point = 0;
            double which_gene = Math.random() * 1;

	    // Mutate gene
            if(which_gene <= 0.5) {
                mut_gene = next_gen.get(next_gen.size() - 1);
                mut_point = generator.nextInt(no_items);
                if(mut_gene.substring(mut_point, mut_point + 1).equals("1")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "0" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
                if(mut_gene.substring(mut_point, mut_point + 1).equals("0")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "1" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
            }
            if(which_gene >0.5) {
                mut_gene = next_gen.get(next_gen.size() - 2);
                mut_point = generator.nextInt(no_items);
                if(mut_gene.substring(mut_point, mut_point + 1).equals("1")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "0" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
                if(mut_gene.substring(mut_point, mut_point + 1).equals("0")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "1" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 2, new_mut_gene);
                }
            }           
        }
    }


    void evaluateFitness(){
        total_fitness_of_generation = 0;
        double fitest = 0;
        int fitest_index=0;

        for(int j = 0; j < population_size; j++) {
            double total_weight = 0;
            double total_value = 0;
            double fitness_value = 0;
            double diff = 0;
            char c = '1';

            for(int i = 0; i < no_items; i++) {
                c = population.get(j).charAt(i);
                //chromosome value 1
                if(c == '1') {
                    total_weight = total_weight + weight.get(i);
                    total_value = total_value + value.get(i);
                }
            }

            diff = knapsack_capacity - total_weight;
            if(diff >= 0) {
                fitness_value = total_value;
            }
            
            fitness.add(fitness_value);
            if(fitness_value>fitest){
                fitest=fitness_value;
                fitest_index=j;
            }
            total_fitness_of_generation = total_fitness_of_generation + fitness_value;
            
            }
            System.out.println("Total Generation Fitness "+total_fitness_of_generation);
            System.out.print("The fittest chromosome of this generation is "+population.get(fitest_index));
            System.out.println(" And its fitness is "+fitness.get(fitest_index));
            generation_solution.add(population.get(fitest_index));
           
    }

    void generatePopulation(){
        System.out.println("Population:");
        for(int i = 0; i < population_size; i++) {
            String gene = "";

            char c;
            for(int j = 0; j < no_items; j++) {
                                
                if(Math.random() > 0.5) {
                    gene+="1";
                }
                else{
                    gene+="0";
                }
            }
            System.out.println("#"+(i+1)+" "+gene);
            population.add(gene);      
        }
    }

    public static void main(String args[]){
        
        sc=new Scanner(System.in);
        System.out.println("Enter no of items");
        Knap k=new Knap();
        
    }
}