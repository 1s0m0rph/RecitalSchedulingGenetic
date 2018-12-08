import java.util.Random;

public class Gene
{
	Random rn;
	int[] g;//the gene's ordering of the classes, each number here is an index into the main roster
	double mutationProbability = 0.001;//how likely a swap is to happen PER TRIAL
	double fitness = 0;
	
	public Gene(int[] ordering, Random rn, double mp)
	{
		this.g = ordering;
		this.rn = rn;
		this.mutationProbability = mp;
	}
	
	public Gene(Roster r, Random rn, double mp)
	{
		g = new int[r.numClasses];
		for(int i = 0; i < g.length; i++)
			g[i] = i;
		this.rn = rn;
		this.mutationProbability = mp;
		randomizeGene();
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
	
	void randomizeGene()
	{
		g = shuffle(g);
	}
	
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
