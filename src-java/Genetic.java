import java.util.ArrayList;
import java.util.Random;
public class Genetic
{
	ArrayList<Gene> L;//living population
	Roster r;
	int DAUGHTERS_PER_PAIR = 2;
	Random rn;
	double mutationProbability = 0.001;//how likely a swap is to happen PER TRIAL
	double BREEDING_POPULATION_PROPORTION = 1./5.;//how much of the population will actually breed?
	double POPULATION_MORTALITY_PROPORTION = 2./5.;//how much of the population will die w/ probability DIE_PROBABILITY each heat? For a constant population, this should be (DAUGHTERS_PER_PAIR*BREEDING_POPULATION_PROPORTION*BREED_PROBABILITY)/DIE_PROBABILITY
	Gene bestGene = null;
	Gene worstGene = null;
	double DIE_PROBABILITY = 0.5;
	double BREED_PROBABILITY = 0.5;
//	double HIGH_FITNESS_BIAS_DROP_PROBABILITY = 0.01;//how likely are we to drop an arbitrary individual, regardless of fitneses
	
	public Genetic(Roster r, Random rn, double mp, int initialPopulation)
	{
		L = new ArrayList();
		this.r = r;
		this.rn = rn;
		mutationProbability = mp;
		initGenes(initialPopulation);
	}
	
	void show()
	{
		for(int i = 0; i < L.size(); i++)
		{
			System.out.print("Ordering:\t");
			for(int k = 0; k < L.get(i).g.length-1; k++)
			{
				System.out.print(L.get(i).g[k] + ",");
			}
			System.out.println(L.get(i).g[L.get(i).g.length-1] + ";\tFitness:\t" + L.get(i).fitness);
		}
	}
	
	void initGenes(int pop)
	{
		for(int i = 0; i < pop; i++)
		{
			L.add(new Gene(r,rn,mutationProbability));
			L.get(i).fitness = fitness(L.get(i));
			if(bestGene == null || L.get(i).fitness > bestGene.fitness)
				bestGene = L.get(i);
			if(worstGene == null || L.get(i).fitness < worstGene.fitness)
				worstGene = L.get(i);
		}
	}
	
	double avgFitness()
	{
		double sum = 0;
		for(int i = 0; i < L.size(); i++)
		{
			sum += L.get(i).fitness;
		}
		return sum / (double)L.size();
	}
	
	void showSolution(Gene g)
	{
		ArrayList<Integer>[] s = geneToSolution(g);
		for(int i = 0; i < s.length; i++)
		{
			System.out.print("{");
			for(int j = 0; j < s[i].size()-1; j++)
			{
				System.out.print(s[i].get(j) + ", ");
			}
			System.out.println(s[i].get(s[i].size()-1) + "}");
		}
	}
	
	void showBestSolution()
	{
		showSolution(bestGene);
	}
	
	ArrayList<Integer>[] geneToSolution(Gene gene)
	{
		ArrayList<Integer>[] ordering = new ArrayList[r.numClasses];
		for(int i = 0; i < r.numClasses; i++)
		{
			ordering[i] = r.C[gene.g[i]];
		}
		return ordering;
	}
	
	double fitnessOnlyNegative(Gene gene)
	{
		//gives the value of the fitness function for gene gene
		ArrayList<Integer>[] ordering = geneToSolution(gene);
		int[] lastSeen = new int[r.numStudents];
		for(int i = 0; i < lastSeen.length; i++)
			lastSeen[i] = -1;//we haven't seen it yet
		double rs = 0;
		for(int i = 0; i < ordering.length; i++)
		{
			for(int j = 0; j < ordering[i].size(); j++)
			{
				if(lastSeen[ordering[i].get(j)] != -1 && (lastSeen[ordering[i].get(j)] - i) == -1)
					rs++;
				lastSeen[ordering[i].get(j)] = i;
			}
		}
		return rs;
	}
	
	double fitness(Gene gene)
	{
		//gives the value of the fitness function for gene gene
		ArrayList<Integer>[] ordering = geneToSolution(gene);
		int[] lastSeen = new int[r.numStudents];
		for(int i = 0; i < lastSeen.length; i++)
			lastSeen[i] = -1;//we haven't seen it yet
		double rs = 0;
		for(int i = 0; i < ordering.length; i++)
		{
			for(int j = 0; j < ordering[i].size(); j++)
			{
				double add = 0;
				if(lastSeen[ordering[i].get(j)] != -1)
					add = i - lastSeen[ordering[i].get(j)] - 4;
				rs += add;
				lastSeen[ordering[i].get(j)] = i;
			}
		}
		return rs;
	}
	
	int[] nats(int n)
	{
		int[] r = new int[n];
		for(int i = 0; i < n; i++)
			r[i] = i;
		return r;
	}
	
	int[] shuffle(int[] a)
	{
		for(int i = 0; i < a.length; i++)
		{
			int ri = rn.nextInt(a.length);
			int temp = a[i];
			a[i] = a[ri];
			a[ri] = temp;
		}
		return a;
	}
	
	ArrayList shuffle(ArrayList a)
	{
		for(int i = 0; i < a.size(); i++)
		{
			int ri = rn.nextInt(a.size());
			Object temp = a.get(i);
			a.set(i,a.get(ri));
			a.set(ri,temp);
		}
		return a;
	}
	
	double breedProbability(Gene gene)
	{
		double fit = gene.fitness;
		double scale = Math.sqrt((r.numClasses - 1) * r.numStudents);
		return scale * (1/(fit + scale)) + 1;
	}
	
	/*
	breed the whole population together, probability of an individual being allowed to breed is proportional to their fitness
	bais towards high fitness individuals breeding with other high fitness individuals
	 */
	void heat()
	{
		ArrayList<Gene> breedingPopulation = new ArrayList();
		ArrayList<Gene> tmpsh = new ArrayList(L.size());
		for(int i = 0; i < L.size(); i++)
			tmpsh.add(L.get(i));
		tmpsh = shuffle(tmpsh);
		tmpsh = partitionDesc(tmpsh);
		for(int i = 0; breedingPopulation.size() < (int)(r.numClasses * BREEDING_POPULATION_PROPORTION) && i < L.size(); i++)
		{
			if(rn.nextDouble() < BREED_PROBABILITY)
				//include this one
				breedingPopulation.add(tmpsh.get(i));
		}
		
		
		//for now just randomly pair individuals
		for(int i = 0; i+1 < breedingPopulation.size(); i += 2)
		{
			Gene p1 = breedingPopulation.get(i);
			Gene p2 = breedingPopulation.get(i+1);
			breed(p1,p2);
		}
		
		select();
	}
	
	void breed(Gene parent1, Gene parent2)
	{
		//2 daughters from each pair of parents for now
		//select uniformly at random half the stuff from parent 1, and the complement from the other
		//add each daughter to the population when done
		for(int i = 0; i < DAUGHTERS_PER_PAIR; i++)
		{
			int[] dauo = new int[r.numClasses];
			int[] nsh = shuffle(nats(r.numClasses));
			int idxp1 = r.numClasses>>1;//what indexes do we take from parent 1?
			int idxp2 = r.numClasses - (r.numClasses >> 1);//what indexes do we take from parent 2?
			for(int k = 0; k < idxp1; k++)
			{
				dauo[nsh[k]] = parent1.g[nsh[k]];
			}
			for(int k = idxp1; k < idxp1 + idxp2; k++)
			{
				dauo[nsh[k]] = parent1.g[nsh[k]];
			}
			Gene daughter = new Gene(dauo,rn,mutationProbability);
			daughter.mutate();
			daughter.fitness = fitness(daughter);
			if(bestGene == null || daughter.fitness > bestGene.fitness)
				bestGene = daughter;
			if(worstGene == null || daughter.fitness < worstGene.fitness)
				worstGene = daughter;
			L.add(daughter);
		}
	}
	
	/*
	how likely is gene to die given its fitness?
	 */
	double dieProbability(Gene gene)
	{
		double avg = avgFitness();
		double prob = 1. - ((gene.fitness - avg - worstGene.fitness)/(bestGene.fitness - worstGene.fitness) + worstGene.fitness/(2*(bestGene.fitness-worstGene.fitness)));
		return prob;//-|S|(|C|-1) is the worst possible fitness score
	}
	
	ArrayList<Gene> swap(ArrayList<Gene> a, int idx1, int idx2)
	{
		Gene tmp = a.get(idx1);
		a.set(idx1,a.get(idx2));
		a.set(idx2,tmp);
		return a;
	}
	
	ArrayList<Gene> partitionAsc(ArrayList<Gene> a)
	{
		int pivIdx = rn.nextInt(a.size());
		Gene piv = a.get(pivIdx);
		a = swap(a,pivIdx,a.size()-1);
		int j = 0;
		for(int i = 0; i < a.size()-1; i++)
		{
			if(a.get(i).fitness <= piv.fitness)//possibly change to <
				a = swap(a,i,j++);
		}
		a = swap(a,j,a.size()-1);
		return a;
	}
	
	ArrayList<Gene> partitionDesc(ArrayList<Gene> a)
	{
		int pivIdx = rn.nextInt(a.size());
		Gene piv = a.get(pivIdx);
		a = swap(a,pivIdx,a.size()-1);
		int j = 0;
		for(int i = 0; i < a.size()-1; i++)
		{
			if(a.get(i).fitness > piv.fitness)//possibly change to >=
				a = swap(a,i,j++);
		}
		a = swap(a,j,a.size()-1);
		return a;
	}
	
	void select()
	{
		//kill off some of the population, with stochastic priority going to the least fit members. i.e. kill off the less fit genes with higher probability; probability is inversely proportional to fitness
		L = partitionAsc(L);
		for(int i = 0; i < L.size()*POPULATION_MORTALITY_PROPORTION; i++)
		{
			if(rn.nextDouble() < DIE_PROBABILITY)
				L.remove(i);
		}
	}
}