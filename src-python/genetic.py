from gene import *
from util import *

#for constant population, BREED_PROBABILITY * BREEDING_POPULATION_PROPORTION * (DAUGHTERS_PER_PAIR / (parents per daughter)) must equal DIE_PROBABILITY * MORTALITY_POPULATION_PROPORTION
#this simplifies to BREED_PROBABILITY * BREEDING_POPULATION_PROPORTION = DIE_PROBABILITY * MORTALITY_POPULATION_PROPORTION when each pair of parents exactly replaces themselves

DAUGHTERS_PER_PAIR = 2
BREEDING_POPULATION_PROPORTION = 1./5.#how much of the population will actually breed?
MORTALITY_POPULATION_PROPORTION = 2./5.#how much of the population will die w/ probability DIE_PROBABILITY each heat?
BREED_PROBABILITY = 0.5
DIE_PROBABILITY = 0.475

#E[|L|] after heat = ~|L|

class Genetic:
	def __init__(self, roster, mutationProbability, initialPopulation, fitnessMetric = 'distance'):
		if fitnessMetric not in ['distance','quickChanges']:
			raise InputException("fitnessMetric for the genetic class must be either 'distance' or 'quickChanges'")

		self.roster = roster
		self.mutationProbability = mutationProbability
		self.L = []
		self.fitnessMetric = fitnessMetric
		self.bestGene = None
		self.worstGene = None
		self.initGenes(initialPopulation)

	def avgFitness(self):
		return np.mean(np.array([self.L[i].fitness for i in range(len(self.L))]))

	"""
	precondition: self.L is empty list
	"""
	def initGenes(self,initialPopulation):
		for i in range(initialPopulation):
			newGene = Gene(self.mutationProbability,self.roster,fitnessMetric=self.fitnessMetric)
			self.L.append(newGene)
			if (self.bestGene is None) or (newGene.fitness > self.bestGene.fitness):
				self.bestGene = newGene
			if (self.worstGene is None) or (newGene.fitness < self.worstGene.fitness):
				self.worstGene = newGene

	def show(self):
		for gene in self.L:
			print("Ordering:",end='\t')
			gene.show(newline=False)
			print("\tFitness:\t" + str(gene.fitness))

	"""
	Combine parent1 and parent2 to produce DAUGHTERS_PER_PAIR daughter genes, then mutate them
	"""
	def breed(self,parent1,parent2):
		#select uniformly at random half the stuff from parent 1, and the complement from the other so that each daughter is a complete solution
		#add each daughter to the population when done
		for i in range(DAUGHTERS_PER_PAIR):
			parent1g = list(parent1.g.copy())
			parent2g = set(parent2.g.copy())
			daughterIndicesList = [0 for _ in range(self.roster.numClasses)]
			shuffledNats = list(range(self.roster.numClasses))
			np.random.shuffle(shuffledNats)
			numIndexFromParent1 = self.roster.numClasses >> 1
			k = 0
			while k < numIndexFromParent1:
				daughterIndicesList[shuffledNats[k]] = parent1g[shuffledNats[k]]
				#somehow remove class parent1g[shuffledNats[k]] from parent2g
				# del parent2g[shuffledNats[k]]
				parent2g.remove(parent1g[shuffledNats[k]])
				k+=1
			parent2g = list(parent2g)
			for kp2 in range(len(parent2g)):
				daughterIndicesList[shuffledNats[k]] = parent2g[kp2]
				k+=1

			daughter = Gene(self.mutationProbability,self.roster,ordering=daughterIndicesList,fitnessMetric=self.fitnessMetric)
			daughter.mutate()
			if daughter.fitness > self.bestGene.fitness:
				self.bestGene = daughter
			if daughter.fitness < self.worstGene.fitness:
				self.worstGene = daughter
			daughter.parents = [parent1,parent2]
			daughter.generation = max(parent1.generation,parent2.generation) + 1
			self.L.append(daughter)

	"""
	breed the whole population together, probability of an individual being allowed to breed is proportional to their fitness
	bais towards high fitness individuals breeding with other high fitness individuals
	"""
	def heat(self):
		breedingPopulation = []
		tempShuffledL = self.L.copy()
		np.random.shuffle(tempShuffledL)
		partition(tempShuffledL,descending=True)
		i = 0
		while (len(breedingPopulation) < int(len(self.L) * BREEDING_POPULATION_PROPORTION)) and (i < len(self.L)):
			if np.random.choice([True,False],p=[BREED_PROBABILITY,1-BREED_PROBABILITY]):
				#this gene got lucky and gets to breed
				breedingPopulation.append(tempShuffledL[i])
			i+=1

		#randomly pair selected individuals
		np.random.shuffle(breedingPopulation)#maybe cut this step?
		i = 0
		while(i + 1 < len(breedingPopulation)):
			parent1 = breedingPopulation[i]
			parent2 = breedingPopulation[i+1]
			self.breed(parent1,parent2)
			i+=2

		self.select()

	"""
	kill off some of the population, with stochastic priority going to the least fit members. i.e. kill off the less fit genes with higher probability; probability is inversely proportional to fitness
	"""
	def select(self):
		partition(self.L,descending=False)
		newL = []
		for i in range(int(len(self.L) * MORTALITY_POPULATION_PROPORTION)):
			if not (np.random.choice([True,False],p=[DIE_PROBABILITY,1-DIE_PROBABILITY])):
				newL.append(self.L[i])
		for i in range(int(len(self.L) * MORTALITY_POPULATION_PROPORTION),len(self.L)):
			newL.append(self.L[i])
		self.L = newL