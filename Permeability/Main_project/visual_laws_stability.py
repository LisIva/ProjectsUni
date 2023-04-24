import matplotlib.pyplot as plt
import numpy as np
from matplotlib import cm
import matplotlib.colors as mcolors


def plot_law_stable(cons_stabl, type, inner_type, end, start=0):
    titles = [['||mass_cons||F', '||momentumx||F', '||momentumy||F'],
              ['||Delta{rho}||F', '||Delta{ux}||F', '||Delta{uy}||F']]

    fig = plt.figure(figsize=(13, 9))

    plt.plot(cons_stabl[type][inner_type, start:end])
    plt.grid()
    plt.xlabel('№ итерации')
    plt.ylabel(titles[type][inner_type])
    plt.show()


conserv_law = np.load("Conserv_law.npy") # 3 на 10000, для 8 ^ 2
stability = np.load("stability/Stability20x20.npy") # для 25 ^ 2
cons_stabl = [conserv_law, stability]
plot_law_stable(cons_stabl, type=0, inner_type=0,
                                        start=0, end=15999)

print('rho:', stability[0, -1]/4.0)
print("ux:", stability[1, -1]/1.)
print("uy:", stability[2, -1]/0.2)
for i in range(30000):
    if np.abs(stability[2, i])/0.05 < 3.0e-6:
        print(i)
        break

epsilon = np.load("stability/Epsilon20x20.npy")
solids = 1 - epsilon
porosity_por = np.sum(solids) / (20 * 20)
print(porosity_por)
print(np.abs(stability[0, 19999]))