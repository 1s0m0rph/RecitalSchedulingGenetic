
# from roster import *
# from gene import *
# from genetic import *
# from util import *

"""
a is little endian. This basically bincounts a
"""
def incrementArray(a):
	i = 0
	while (i < len(a)) and (a[i] == 1):
		a[i] = 0
		i+=1
	if(i < len(a)):
		a[i] = 1

def floatRange(start,stop,inc):
	size = int(abs((stop - start) / inc))
	r = [0. for _ in range(size)]
	val = start
	for i in range(len(r)):
		r[i] = val
		val += inc
	return r

a = [0 for _ in range(10)]
evsize = 100
EV = [0 for _ in range(evsize)]
# b = 0.25
D = 2

for t,b in enumerate(floatRange(1./evsize,1+(1./evsize),1./evsize)):
	for _ in range(pow(2,len(a))):
		breedswitch = False
		val = 0
		prob = 1.
		for i,e in enumerate(a):
			#probability of 1 is not the same as that of 0. 1 means breed, 0 means no breed
			if e == 1:
				prob *= b
				val += 1
				if(breedswitch):
					#2 parents necessary for breed
					val += D
				breedswitch = not breedswitch
			else:
				prob *= (1-b)
		EV[t] += val*prob
		incrementArray(a)

	print("{:.2f}\t{:.3f}\t{:.3f}".format(b,EV[t],10.*(2.*int((10.*b)/2.))))

print()
for i in range(evsize-1):
	print(EV[i+1]-EV[i])