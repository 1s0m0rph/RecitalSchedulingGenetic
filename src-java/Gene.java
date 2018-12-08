import java.util.Random;

/*
Class to handle all the gene functions
 */
public class Gene
{
	Random rn;//random number generator. We want to use this so we can guarantee that we always generate the same roster
	int[] g;//the gene's ordering of the classes, each number here is an index into the main roster
	double mutationProbability = 0.001;//how likely a swap is to happen PER TRIAL
	double fitness = 0;//how fit is the gene? Store this for constant time access

	/*
	@param ordering What order are the classes in in this gene? Used when breeding into new genes
	@param rn the number generator
	@param mp how likely are we to mutate at any given step?
	 */
	public Gene(int[] ordering, Random rn, double mp)
	{
		this.g = ordering;
		this.rn = rn;
		this.mutationProbability = mp;
	}

	/*
	Construct the gene, but randomly assign its ordering. Used only when initializing the population

	@param r the roster to pick from
	@param rn the number generator
	@param mp how likely are we to mutate at any given step?
	 */
	public Gene(Roster r, Random rn, double mp)
	{
		g = new int[r.numClasses];
		for(int i = 0; i < g.length; i++)
			g[i] = i;
		this.rn = rn;
		this.mutationProbability = mp;
		randomizeGene();
	}

	/*
	helper method to shuffle an int array
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
	shuffle the ordering of this gene. Used only by the constructor. Once we make the gene we never want to change it, except to mutate
	 */
	private void randomizeGene()
	{
		g = shuffle(g);
	}

	/*
	randomly swap certain parts of the ordering, each time with probability mutationProbability.
	This method makes the probability that a given daughter has no mutations equal to (1-mutationProbability)^(number of classes)
	so for a roster of size 40 classes and a nutation probability of 5%, each daughter has a probability of 12.85%
	 */
	void mutate()
	{
		for(int i = 0; i < g.length; i++)
		{
			if(rn.nextDouble() < mutationProbability)
			{
				//"mutate," or swap this class and another
				int ri = rn.nextInt(g.length);
				int temp = g[i];
				g[i] = g[ri];
				g[ri] = temp;
			}
		}
	}
}
