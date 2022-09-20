package Chords;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Evolution {
    static double CHORD_TYPE_MUTATION_FREQUENCY = 0.05;
    static double INVERSION_MUTATION_FREQUENCY = 0.1;
    static double ELITE_PERCENTAGE = 0.1;
    public List<ChordObject> initialGeneration ;
    double wantedAverageFitness;
    Random generator;
    public Evolution(List<ChordObject> initialGeneration, double wantedAverageFitness) {
        this.initialGeneration = initialGeneration;
        this.wantedAverageFitness = wantedAverageFitness;
        generator = new Random();
    }

    private void performElitism(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        int size = oldGeneration.size();
        oldGeneration.sort((chord1, chord2) -> (int) Math.signum(chord1.fitnessFunction() - chord2.fitnessFunction()));
        for (int i = 0 ; i < ELITE_PERCENTAGE * size ; i++) {
            // remove the elite elements from the oldGeneration to the new Ones
            newGeneration.add(oldGeneration.remove(oldGeneration.size() - 1));
        }
    }
    private void crossOver(List<ChordObject> oldGeneration, List<ChordObject> newGeneration) {
        while(oldGeneration.size() >= 2) {
            ChordObject parent1 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            ChordObject parent2 = oldGeneration.remove(generator.nextInt(oldGeneration.size()));
            ChordObject betterParent = parent1.fitnessValue > parent2.fitnessValue ? parent1 : parent2;
            ChordObject offspring = ChordsReproduction.crossOver(parent1, parent2);

            double mutationDeterminative = generator.nextDouble();
            if (mutationDeterminative < INVERSION_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordInversion(offspring);
            }

            if (mutationDeterminative < CHORD_TYPE_MUTATION_FREQUENCY) {
                ChordsReproduction.mutateChordType(offspring);
            }
            newGeneration.add(betterParent);
            newGeneration.add(offspring);
        }
        // in case one element is left in the oldGeneration list
        newGeneration.addAll(oldGeneration);
    }
    private double evaluateTotalFitness() {
        return initialGeneration.stream().mapToDouble(ChordObject::fitnessFunction).sum();
    }

    public void simulateEvolution() {
        double totalFitness = 0;
        int populationSize = initialGeneration.size();

        while (totalFitness < populationSize * wantedAverageFitness) {
            List<ChordObject> newGeneration = new ArrayList<>();
            // pass the best parents directly to the next generation
            performElitism(initialGeneration, newGeneration);
            // perform crossOver to (possibly) obtain better offspring
            crossOver(initialGeneration, newGeneration);
            // set the corresponding variables
            initialGeneration = newGeneration;
            // evaluate the total performance
            totalFitness = evaluateTotalFitness();
        }
    }
}



