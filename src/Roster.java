import java.util.ArrayList;
import java.util.Random;

/*
Keeps track of the classes we have to pick from. Really, this is an external sort of class for interfacing with some kind of hypothetical database
 */
public class Roster
{
	ArrayList<Integer>[] C;//set of classes
	Random rn;//universal random number generator; use this to guarantee the same roster every time given the same seed
	int numClasses, numStudents;
	
	public Roster(ArrayList<Integer>[] C, int numStudents, Random rn)
	{
		this.C = C;
		this.rn = rn;
		numClasses = C.length;
		this.numStudents = numStudents;
	}
	
	public Roster(int numClasses, int numStudents, Random rn)
	{
		C = new ArrayList[numClasses];
		this.rn = rn;
		this.numClasses = numClasses;
		this.numStudents = numStudents;
		randClasses();
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

	/*
	Generate numClasses random classes to populate the roster. Each class has a uniform number of students chosen
	from the interval [1,sqrt(numStudents)]. This is to avoid massive classes that are both unrealistic and very hard to solve.
	 */
	void randClasses()
	{
		int[] stlist = new int[numStudents];
		for(int i = 0; i < numStudents; i++)
			stlist[i] = i;
		stlist = shuffle(stlist);
		for(int i = 0; i < C.length; i++)
		{
			int classSize = rn.nextInt((int)Math.sqrt(numStudents)-1)+1;
			ArrayList<Integer> nClass = new ArrayList<Integer>(classSize);
			for (int j = 0; j < classSize; j++)
			{
				nClass.add(stlist[j]);
			}
			stlist = shuffle(stlist);
			C[i] = nClass;
		}
	}

	/*
	Print out the roster
	 */
	void show()
	{
		for(int i = 0; i < C.length; i++)
		{
			System.out.print("{");
			for(int j = 0; j < C[i].size()-1; j++)
			{
				System.out.print(C[i].get(j) + ", ");
			}
			System.out.println(C[i].get(C[i].size()-1) + "}");
		}
	}
}