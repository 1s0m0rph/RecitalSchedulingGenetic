from util import *

"""
This class keeps track of the classes we have to pick from. Ideally, this is an external sort of class for interfacing with some kind of hypothetical database
"""
class Roster:

	"""
	C is a set containing all of the classes (lists of student indexes)
	randomState is necessary to make sure that everything uses the same generator. This way we get predictable results
	"""
	def __init__(self, numStudents, C = None, numClasses=None):
		if (C is None) and (numClasses is None):
			raise InputException("Roster class requires the full set of extant classes the number of classes")

		self.C = []
		self.numClasses = None
		self.numStudents = numStudents

		if C is not None:
			self.C = C
			self.numClasses = len(C)
		else:
			self.numClasses = numClasses
			self.randClasses()


	"""
	Generate numClasses random classes to populate the roster. Each class has a uniform number of students chosen
	from the interval [1,sqrt(numStudents)]. This is to avoid massive classes that are both unrealistic and very hard to solve.
	"""
	def randClasses(self):
		stlist = np.array(list(range(self.numStudents)))
		np.random.shuffle(stlist)
		for i in range(self.numClasses):
			classSize = np.random.choice(range(1,int(np.sqrt(self.numStudents)+1)))
			newClass = stlist[:classSize]
			np.random.shuffle(stlist)
			self.C.append(newClass)

	def show(self):
		for cls in self.C:
			print("{",end='')
			for j in range(len(cls)-1):
				print(str(cls[j]) + ", ",end='')
			print(str(cls[-1]) + "}")