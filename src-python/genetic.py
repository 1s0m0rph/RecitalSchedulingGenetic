# import numpy as np
# from roster import *
from gene import *
from util import *

DAUGHTERS_PER_PAIR = 2
BREEDING_POPULATION_PROPORTION = 1./5.#how much of the population will actually breed?
POPULATION_MORTALITY_PROPORTION = 2./5.#how much of the population will die w/ probability DIE_PROBABILITY each heat? For a constant population, this should be (DAUGHTERS_PER_PAIR*BREEDING_POPULATION_PROPORTION*BREED_PROBABILITY)/DIE_PROBABILITY
BREED_PROBABILITY = 0.5
DIE_PROBABILITY = 0.5



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
			daughterIndicesList = []
			shuffledNats = list(range(len(self.roster.numClasses)))
			np.random.shuffle(shuffledNats)
			indexFromParent1 = [


	def heat(self):
		breedingPopulation = []
		tempShuffledL = self.L.copy()
		np.random.shuffle(tempShuffledL)
		partition(tempShuffledL,descending=True)
		i = 0
		while (len(breedingPopulation) < int(self.roster.numClasses * BREEDING_POPULATION_PROPORTION)) and (i < len(self.L)):
			if np.random.choice([True,False],p=[BREED_PROBABILITY,1-BREED_PROBABILITY]):
				#this gene got lucky and gets to breed
				breedingPopulation.append(tempShuffledL[i])
			i+=1

		#randomly pair selected individuals
		np.random.shuffle(breedingPopulation)
		i = 0
		while(i + 1 < len(breedingPopulation)):
			parent1 = breedingPopulation[i]
			parent2 = breedingPopulation[i+1]

			i+=2