from roster import *
from genetic import *
from util import *
np.random.seed(40)#make the random numbers predictable

epochs = 100
initialPopulation = 25
numClasses = 4
numStudents = 5
mutationProbability = 0.01
numHeats = 50

#easily solved roster set up
C = [[1],
	 [0,1,3],
	 [0,2,4],
	 [2,4]]
r = Roster(numStudents,C=C)

# g.show()

bestByFitnessRaw = None
bestByQC = None

for epoch in range(epochs):
	g = Genetic(r, mutationProbability, initialPopulation, fitnessMetric='quickChanges')


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