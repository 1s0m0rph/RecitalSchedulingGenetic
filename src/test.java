import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class test
{
	public static void main(String[] args)
	{
		//generate the original roster randomly, but according to the same seed so we can test multiple configurations on the same roster
		Random rn = new Random(123);
		ArrayList<Integer>[] C = new ArrayList[4];
		for(int i = 0; i < C.length; i++)
			C[i] = new ArrayList();
		//testing roster, much easier to solve
//		C[0].add(1);
//		C[1].add(0);
//		C[1].add(1);
//		C[1].add(3);
//		C[2].add(0);
//		C[2].add(2);
//		C[2].add(4);
//		C[3].add(2);
//		C[3].add(4);
//		Roster r = new Roster(C,5,rn);
		Roster r = new Roster(41, 80, rn);
		rn = new Random();//use one seed to generate the roster so it's always the same, then use a random one
		Gene bestByFitnessRaw = null;
		int qcbfr = -1;
		Gene bestByQC = null;
		int qcbqc = -1;
		double tolerance = 1e-1;//tolerance for when we stop trying to breed genes
		double epochsUntilConvergenceSum = 0;//for calculating average
		int trials = 100;
		for(int t = 0; t < trials; t++)
		{
			Genetic G = new Genetic(r, rn, 0.05, 140, "quickChanges");//change fitnessMetric to "fitness" for the optimization problem
			double avgFitnessLast = G.avgFitness();
			int i = 0;
			for(; i < 200; i++)
			{
//				if(i % 5 == 0)
//					System.out.println("Average fitness @ epoch\t" + i + ":\t" + G.avgFitness() + "; population:\t" + G.L.size());
				G.heat();
				double currentFitness = G.avgFitness();
				if(i != 0 && (avgFitnessLast > currentFitness-tolerance && avgFitnessLast < currentFitness+tolerance))
					break;
				avgFitnessLast = currentFitness;
			}
			epochsUntilConvergenceSum += i;
			if(t % 10 == 0)
				System.out.println("Average fitness in trial\t" + t + ":\t" + G.avgFitness() + "; population:\t" + G.L.size());
			if(bestByFitnessRaw == null || G.bestGene.fitness > bestByFitnessRaw.fitness)
			{
				bestByFitnessRaw = G.bestGene;
				qcbfr = (int)G.fitnessOnlyNegative(bestByFitnessRaw);
			}
			if(bestByQC == null || G.fitnessOnlyNegative(G.bestGene) < qcbqc)
			{
				bestByQC = G.bestGene;
				qcbqc = (int)G.fitnessOnlyNegative(G.bestGene);
			}
		}
		Genetic sol = new Genetic(r,rn,0.01,1);
//		System.out.println("\nBest solution using fitness score (fitness: " + bestByFitnessRaw.fitness + "; quick changes: " + qcbfr + "):");
//		sol.showSolution(bestByFitnessRaw);
		System.out.println("\nBest solution using number of quick changes (fitness: " + bestByQC.fitness + "; quick changes: " + qcbqc + "):");
		sol.showSolution(bestByQC);
//		System.out.println("\nBest solution using fitness score (fitness: " + bestByFitnessRaw.fitness + "; quick changes: " + qcbfr + "):");
//		System.out.println("\nBest solution using number of quick changes (fitness: " + bestByQC.fitness + "; quick changes: " + qcbqc + "):");
		System.out.println("\nAverage epochs until convergence:\t" + (epochsUntilConvergenceSum / trials));
	}
}
