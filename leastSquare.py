import numpy as np
import random
import math

f1 = open('label.txt', 'r')
f2 = open('featureMatrix.txt', 'r')

featuresize = 8
x = []
finalx = []
for i in range(0, featuresize):
   x.append([])  

sample = 0
z = []
for word in f1:
  sample += 1
  w = word[0:-1]
  z.append(w)
  
for line in f2:
  line = line[0:-2]
  parts = line.split('\t')
  #print len(parts)
  for i in range(0, featuresize):
     x[i].append(float(parts[i]))

"""for i in range(0, featuresize):
    maxele = max(x[i])
    x[i] = [f/float(maxele) for f in x[i]]
"""
"""x1 = [1,2,1, 1]
x2 = [2,2,4, 2]
x3 = [1,1,1,2]
print [x1, x2, x3, np.ones(4, float)]
print x
A = np.column_stack([x1, x2, x3, np.ones(4, float)])
"""

x.append(np.ones(sample, float))
A = np.column_stack(x)

print np.linalg.lstsq(A, z)[0]
