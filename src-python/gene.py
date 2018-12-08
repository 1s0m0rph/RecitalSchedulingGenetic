import numpy as np
import roster as rst

"""
Contains the gene representation of a class ordering (candidate solution), as well as all of the operations done on that gene involving *only* that gene
"""
class Gene:

	"""
	ordering specifies what order the classes come in. It's a permutation of the class indices
	"""
	def __init__(self,mutationProbability, roster,ordering=None, fitnessMetric='distance'):
		self.g = []
		self.mutationProbability = mutationProbability
		self.fitness = 0
		self.roster = roster
		self.fitnessMetric = fitnessMetric

		if ordering is not None:
			self.g = ordering
		else:
			self.g = np.array(list(range(roster.numClasses)))
			np.random.shuffle(self.g)
		self.fitness = self.getFitness()

	"""
	randomly swap certain parts of the ordering, each time with probability mutationProbability.
	This method makes the probability that a given daughter has no mutations equal to (1-mutationProbability)^(number of classes)
	so for a roster of size 40 classes and a nutation probability of 5%, each daughter has a probability of 12.85%
	"""
	def mutate(self):
		for i in range(len(self.g)):
			if(np.random.choice([True,False],p=[self.mutationProbability,1-self.mutationProbability])):
				randIdx = np.random.choice(range(len(self.g)))
				temp = self.g[i]
				self.g[i] = self.g[randIdx]
				self.g[randIdx] = temp

	def show(self,newline=True):
		print("[",end='')
		for i in range(len(list(self.g))-1):
			print("{",end='')
			for j in range(len(self.roster.C[self.g[i]])-1):
				print(str(self.roster.C[self.g[i]][j]) + ", ",end='')
			print(str(self.roster.C[self.g[i]][-1]) + "}",end=', ')
		print("{", end='')
		for j in range(len(self.roster.C[-1]) - 1):
			print(str(self.roster.C[self.g[-1]][j]) + ", ", end='')
		if newline:
			print(str(self.roster.C[self.g[-1]][-1]) + "}]")
		else:
			print(str(self.roster.C[self.g[-1]][-1]) + "}]",end='')

	"""
	gets the fitness for Gene gene only using negative values. That is, returns the negative of the number of gap-0 quick changes in the gene
	"""
	def fitnessOnlyNegative(self):
		lastSeen = [-1 for _ in range(self.roster.numStudents)]
		self.fitness = 0#fitness accumulator

		for i in range(len(list(self.g))):
			for j in range(len(self.roster.C[self.g[i]])):
				if (lastSeen[self.roster.C[self.g[i]][j]] != -1) and (lastSeen[self.roster.C[self.g[i]][j]] == i-1):
					self.fitness += 1
				lastSeen[self.roster.C[self.g[i]][j]] = i
		return -self.fitness #negative so we actually converge to a solution and not the worst possibility

	"""
	Alternative to fitnessOnlyNegative. Also considers how far apart individual's pieces are
	"""
	def fitnessDistance(self):
		lastSeen = [-1 for _ in range(self.roster.numStudents)]
		self.fitness = 0.  # fitness accumulator

		for i in range(len(list(self.g))):
			for j in range(len(self.roster.C[self.g[i]])):
				add = 0.
				if (lastSeen[self.roster.C[self.g[i]][j]] != -1):
					add = lastSeen[self.roster.C[self.g[i]][j]] - 4.
				self.fitness += add
				lastSeen[self.roster.C[self.g[i]][j]] = i
		return self.fitness

	def getFitness(self):
		if self.fitnessMetric is 'distance':
			return self.fitnessDistance()
		if self.fitnessMetric is 'quickChanges':
			return self.fitnessOnlyNegative()