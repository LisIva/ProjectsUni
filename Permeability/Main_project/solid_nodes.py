import numpy as np

Ny1 = 25
Nx1 = 25

epsilons = []
rgen = np.random.RandomState(5) # обычно 3!!!!
# 5, 15, 20, 25, 30, 35, 40, 45, 60, 70
# rates = [[0.65, 0.35], [0.75, 0.25]]
# rates = [[0.05, 0.95], [0.10, 0.9], [0.15, 0.85], [0.2, 0.8], [0.25, 0.75],  [0.3, 0.7], [0.35, 0.65], [0.37, 0.63], [0.40, 0.6],  [0.43, 0.57], [0.45, 0.55], [0.60, 0.40], [0.70, 0.30]]
rates = [[0.20, 0.80]]
# rates = [[0.10, 0.9], [0.15, 0.85], [0.2, 0.8],  [0.3, 0.7], [0.35, 0.65], [0.40, 0.6], [0.45, 0.55], [0.60, 0.40], [0.70, 0.30]]

indexes = []


for rate in rates:
    print(str(round(rate[0]*100)))
for rate in rates:
    structure = rgen.choice(2, (Ny1, Nx1), p=rate)
    # structure[0, :] = 0                #возможно, можно закомментить
    # structure[Ny1-1, :] = 0

    # Q_multipl = rgen.choice(2, (Ny1, Nx1), p=rate)
    Q = rgen.normal(loc=0.4, scale=0.3, size=(Ny1, Nx1))
    # Q = rgen.normal(loc=0.9, scale=0.03, size=(Ny1, Nx1))
    # Q = Q*Q_multipl + (1-Q_multipl)

    multip_one = (Q > 1) * 1.0
    Q_delta = Q*multip_one - multip_one
    Q = Q - Q_delta

    multip_zero = (Q < 0) * 1.0
    Q_delta = Q*multip_zero
    Q = Q - Q_delta

    # Q = rgen.random_sample((Ny1, Nx1))   #закоментить после!!!!!
    epsilon = structure*Q


    solids = 1 - epsilon
    epsilon_solid_nodes = (epsilon == 1.) * 1
    porosity = np.sum(solids) / (Ny1*Nx1)
    np.save('epsilon' + str(Nx1) + '/Epsilon' + str(int(rate[0]*100)), epsilon)
    np.save('stability/Epsilon' + str(Nx1) + 'x' + str(Nx1), epsilon)
    # np.save('Epsilon', epsilon)
    epsilons.append(epsilon)
    indexes.append(int(rate[0]*100))



