import numpy as np

class InputException(Exception):
	def __init__(self, text):
		self.ermsg = text

	def __str__(self):
		return repr(self.ermsg)

def swap(a,index1,index2):
	temp = a[index1]
	a[index1] = a[index2]
	a[index2] = temp

"""
Partitions the array a as if it were doing quicksort. used to give stochastic preference to worse genes during selection and better genes during breeding.

Optional parameter useLEQGEQ will use <= in ascending and >= in descending when it is true, else it will use < and > respectively.
"""
def partition(a, descending=False, useLEQGEQ=True):
	pivotIdx = np.random.choice(list(range(len(a))))
	pivot = a[pivotIdx]
	swap(a,pivotIdx,-1)
	j = 0
	for i in range(len(a)-1):
		if descending:
			if useLEQGEQ:
				if a[i].fitness >= pivot.fitness:
					swap(a,i,j)
					j+=1
			else:
				if a[i].fitness > pivot.fitness:
					swap(a,i,j)
					j+=1
		else:
			if useLEQGEQ:
				if a[i].fitness <= pivot.fitness:
					swap(a,i,j)
					j+=1
			else:
				if a[i].fitness < pivot.fitness:
					swap(a,i,j)
					j+=1
	swap(a,j,-1)