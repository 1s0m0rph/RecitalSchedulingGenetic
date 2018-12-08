import java.util.ArrayList;
import java.util.Random;
public class Roster
{
	ArrayList<Integer>[] C;//set of classes
	Random rn;
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