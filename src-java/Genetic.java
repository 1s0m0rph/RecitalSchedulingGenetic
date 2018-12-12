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
	double DIE_PROBABILITY = 0.475;
	double BREED_PROBABILITY = 0.5;
	String fitnessMetric;

	/*
	Consturctor including the fitness metric elt {"fitness","quickChanges"}, see the other constructor
	 */
	public Genetic(Roster r, Random rn, double mp, int initialPopulation, String fitnessMetric)
	{
		L = new ArrayList();
		this.r = r;
		this.rn = rn;
		mutationProbability = mp;
		this.fitnessMetric = fitnessMetric;
		initGenes(initialPopulation);
	}

	/*
	Main class constructor

	@param r the roster to pick from
	@param rn the random number generator to use
	@parma mp the probability of a mutation happening to a given gene
	@param intialPopulation the initial population
	 */
	public Genetic(Roster r, Random rn, double mp, int initialPopulation)
	{
		L = new ArrayList();
		this.r = r;
		this.rn = rn;
		mutationProbability = mp;
		fitnessMetric = "fitness";
		initGenes(initialPopulation);
	}

	/*
	Display the current living population
	 */
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

	/*
	Initialize the genes for the population

	@param pop the initial population size
	 */
	private void initGenes(int pop)
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

	/*
	get the average fitness of the living population
	 */
	double avgFitness()
	{
		double sum = 0;
		for(int i = 0; i < L.size(); i++)
		{
			sum += L.get(i).fitness;
		}
		return sum / (double)L.size();
	}

	/*
	Print out a single solution
	 */
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

	/*
	Convert a gene from its representation as a Gene object to an array of integer vectors
	 */
	private ArrayList<Integer>[] geneToSolution(Gene gene)
	{
		ArrayList<Integer>[] ordering = new ArrayList[r.numClasses];
		for(int i = 0; i < r.numClasses; i++)
		{
			ordering[i] = r.C[gene.g[i]];
		}
		return ordering;
	}

	/*
	gets the fitness for Gene gene only using negative values. That is, returns the negative of the number of gap-0 quick changes in the gene

	@param gene the gene to evaluate the fitness of
	@return the negative of the number of quick changes (the only negative fitness)
	 */
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

	/*
	Alternative to fitnessOnlyNegative. Also considers how far apart individual's pieces are
	 */
	double fitnessOptimizer(Gene gene)
	{
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

	/*
	wrapper for fitness functions
	 */
	double fitness(Gene gene)
	{
		if(fitnessMetric.equals("quickChanges"))
			return -fitnessOnlyNegative(gene);
		else if (fitnessMetric.equals("fitness"))
			return fitnessOptimizer(gene);
		else
			return -1.;
	}

	/*
	Get n natural numbers, starting from 0, in an int array
	 */
	private int[] nats(int n)
	{
		int[] r = new int[n];
		for(int i = 0; i < n; i++)
			r[i] = i;
		return r;
	}

	/*
	Shuffle the array a in place in linear time

	@return the shuffled array
	 */
	private int[] shuffle(int[] a)
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

	/*
	Shuffle the ArrayList a in place in linear time

	@return the shuffled array
	 */
	private ArrayList shuffle(ArrayList a)
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
	
	/*
	breed the whole population together, probability of an individual being allowed to breed is proportional to their fitness
	bias towards high fitness individuals breeding with other high fitness individuals.
	 */
	void heat()
	{
		ArrayList<Gene> breedingPopulation = new ArrayList();
		ArrayList<Gene> tmpsh = new ArrayList(L.size());
		for(int i = 0; i < L.size(); i++)
			tmpsh.add(L.get(i));
		tmpsh = shuffle(tmpsh);
		tmpsh = partitionDesc(tmpsh);
		for(int i = 0; breedingPopulation.size() < (int)(L.size() * BREEDING_POPULATION_PROPORTION) && i < L.size(); i++)
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

	/*
	Combine parent1 and parent2 to produce DAUGHTERS_PER_PAIR daughter genes, then mutate them
	 */
	void breed(Gene parent1, Gene parent2)
	{
		//2 daughters from each pair of parents for now
		//select uniformly at random half the stuff from parent 1, and the complement from the other so that each daughter is a complete solution
		//add each daughter to the population when done
		for(int i = 0; i < DAUGHTERS_PER_PAIR; i++)
		{
			boolean[] inDaguhter = new boolean[r.numClasses];
			int[] dauo = new int[r.numClasses];
			for(int k = 0; k < r.numClasses; k++)
				dauo[k] = -1;
			int[] nsh = shuffle(nats(r.numClasses));
			int idxp1 = r.numClasses>>1;//what indexes do we take from parent 1?
			//this can duplicate. Fix?
			for(int k = 0; k < idxp1; k++)
			{
				dauo[nsh[k]] = parent1.g[nsh[k]];
				inDaguhter[parent1.g[nsh[k]]] = true; //class parent1.g[nsh[k]] is already present in the daughter
			}
			int lowestEmptyIndex = 0;//lowest index in the daughter gene that is not yet filled
			while(dauo[lowestEmptyIndex] != -1)
				lowestEmptyIndex++;
			for(int k = 0; k < r.numClasses; k++)
			{
				//is this class already in d? i.e. was it added by parent1?
				if(!inDaguhter[parent2.g[k]])
				{
					dauo[lowestEmptyIndex] = parent2.g[k];
					while(lowestEmptyIndex < dauo.length && dauo[lowestEmptyIndex] != -1)
						lowestEmptyIndex++;
				}
			}
			Gene daughter = new Gene(dauo,rn,mutationProbability);
			daughter.generation = parent1.generation>parent2.generation ? parent1.generation+1 : parent2.generation+1;
			daughter.mutate();//mutate the daughter
			if(!validGene(daughter))
			{
				System.out.println("INVALID GENE BRED!\n\n\n");
				System.exit(-2);
			}
			daughter.fitness = fitness(daughter);
			if(bestGene == null || daughter.fitness > bestGene.fitness)
				bestGene = daughter;
			if(worstGene == null || daughter.fitness < worstGene.fitness)
				worstGene = daughter;
			L.add(daughter);//add the daughter to the living population
		}
	}
	
	ArrayList<Gene> swap(ArrayList<Gene> a, int idx1, int idx2)
	{
		Gene tmp = a.get(idx1);
		a.set(idx1,a.get(idx2));
		a.set(idx2,tmp);
		return a;
	}

	/*
	quicksort partition function, used to give stochastic preference to worse genes during selection

	@param a the arraylist to partition
	@return the partitioned array
	 */
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

	/*
	quicksort partition function (descending), used to give stochastic preference to better genes during breeding

	@param a the arraylist to partition
	@return the partitioned array
	 */
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

	/*
	//kill off some of the population, with stochastic priority going to the least fit members. i.e. kill off the less fit genes with higher probability
	 */
	void select()
	{

		L = partitionAsc(L);
		for(int i = 0; i < L.size()*POPULATION_MORTALITY_PROPORTION; i++)
		{
			if(rn.nextDouble() < DIE_PROBABILITY)
				L.remove(i);
		}
	}
	
	/*
	Checks if a gene is valid. That is, if it satisfies the conditions:
	g = [c1,c2,...,cn]
	forall i, ci elt Nats(1,n)
	not exist i,j st i != j ^ (ci = cj)
	 */
	boolean validGene(Gene g)
	{
		boolean[] allPresentCheck = new boolean[r.numClasses];//makes sure that all classes are present and there are no duplicates
		for(int i = 0; i < r.numClasses; i++)
		{
			if(allPresentCheck[g.g[i]])
				//there exists a duplicate
				return false;
			allPresentCheck[g.g[i]] = true;
		}
		//check to make sure nothing is false
		for(int i = 0; i < r.numClasses; i++)
		{
			if(!allPresentCheck[i])
				//class i is missing
				return false;
		}
		return true;
	}
}