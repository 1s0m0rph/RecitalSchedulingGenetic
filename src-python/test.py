from roster import *
from gene import *
from genetic import *
import numpy as np
from util import *
np.random.seed(40)#make the random numbers predictable


#easily solved roster set up
C = [[1],
	 [0,1,3],
	 [0,2,4],
	 [2,4]]
r = Roster(5,C=C)

g = Genetic(r,0.01,25,fitnessMetric='quickChanges')
g.show()

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