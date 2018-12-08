import numpy as np
from roster import *
from gene import *

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