from roster import *
from genetic import *
from util import *
np.random.seed(40)#make the random numbers predictable

epochs = 100
initialPopulation = 140
numClasses = 41
numStudents = 80
mutationProbability = 0.05
MAX_HEATS = 200
fitnessMetric = 'quickChanges'

"""
#easily solved roster set up
C = [[1],
	 [0,1,3],
	 [0,2,4],
	 [2,4]]
r = Roster(numStudents,C=C)
"""

r = Roster(numStudents,numClasses=numClasses)

# g.show()

bestByFitnessRaw = None
bestByQC = None
tolerance = 1e-1#tolerance for when we stop trying to breed genes
epochsUntilConvergenceSum = 0#for calculating averages

for epoch in range(epochs):
	g = Genetic(r, mutationProbability, initialPopulation, fitnessMetric=fitnessMetric)
	avgFitnessLast = g.avgFitness()
	h = 0
	while(h < MAX_HEATS):
		g.heat()
		currentFitness = g.avgFitness()
		if (h != 0) and ((avgFitnessLast > (currentFitness - tolerance)) and (avgFitnessLast < (currentFitness+tolerance))):
			#convergence
			break
		avgFitnessLast = currentFitness
		h+=1
	epochsUntilConvergenceSum += h
	if(epoch % 10 == 0):
		print("Average fitness @ epoch\t" + str(epoch) + ":\t{:.5f}".format(g.avgFitness()) + "; population:\t" + str(len(g.L)))
	if (bestByFitnessRaw is None) or (g.bestGene.fitness > bestByFitnessRaw.fitness):
		bestByFitnessRaw = g.bestGene
	if (bestByQC is None) or (g.bestGene.fitness > bestByQC.fitness):
		bestByQC = g.bestGene

print("\nBest solution by number of quick changes, optimized with " + fitnessMetric + " (fitness: {:.5f}".format(bestByQC.fitnessDistance()) + "; quick changes: {:.0f}".format(-bestByQC.fitnessOnlyNegative()) + "):")
# bestByQC.show()

print("\nAverage epochs until convergence:\t{:.5f}".format(float(epochsUntilConvergenceSum)/float(epochs)))


#testing stuff
"""
r.show()

G = Gene(0.01,roster=r,fitnessMetric='quickChanges')
print("Before Mutation")
G.show()
print(G.fitness)
G.mutate()
print("After Mutation")
G.show()
print(G.fitness)
"""