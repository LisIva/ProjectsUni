import matplotlib.pyplot as plt
import numpy as np
from math import pi

cs_2 = 1 / 3  # cs^2
omega_plus = 1.1

dx = 7.3333 * 10 ** (-6)
viscosity = cs_2 * (1 / omega_plus - 0.5)
rho_FR = 1000
myu_FR = 0.001

dt = viscosity * rho_FR * dx * dx / myu_FR
dc = dx / dt



def non_zero_average(vector):
    vec2 = vector * vector
    vect_non_zero = (vec2 > 0.0) * 1.0
    count_non_zero = np.sum(vect_non_zero)
    return np.sum(vector) / count_non_zero


def new_Kl(rho, start, end):
    delta_rho = non_zero_average(rho[end, :]) - non_zero_average(rho[start, :])
    delta_p = cs_2 * delta_rho
    k = -viscosity * non_zero_average(uy[end, :]) / delta_p
    return k


def calc_k(rho):
    Kl_dinamics = np.array([0.0] * Ny)

    for i in range(Ny, 0, -1):
        Kl_dinamics[i - 1] = new_Kl(rho, i, i - 1)

    K_dinamics = Kl_dinamics * dx * dx
    K_dinamics_out = K_dinamics / Darcy

    summa = sum(K_dinamics_out) / len(K_dinamics_out)
    return summa


def sort_points(porosities, permeabs):
    for i in range(1, len(porosities)):
        key = porosities[i]
        permeab = permeabs[i]
        j = i - 1
        while j >= 0 and key < porosities[j]:
            porosities[j + 1] = porosities[j]
            permeabs[j + 1] = permeabs[j]
            j -= 1
        porosities[j + 1] = key
        permeabs[j + 1] = permeab
    return porosities, permeabs


Darcy = 9.869233 * 10 ** (-13)  # 75 85

# rates = [5, 10, 30, 45, 60, 70]

# rates = [15, 20, 25, 30, 35, 40, 43, 45, 55, 60, 70, 75]
# rates = [[0.28, 0.72], [0.29, 0.71], [0.31, 0.69], [0.32, 0.68], [0.48, 0.52], [0.49, 0.51], [0.58, 0.42], [0.62, 0.38], [0.64, 0.36], [0.65, 0.35]]
rates = [15, 22, 28, 40, 41, 46, 62, 70]

# rates = [5]
# rates = [7, 10, 15, 20, 30, 35, 40, 45, 60, 70]
ux_list = []
uy_list = []
rho_list = []
epsilon_list = []
permeabs = np.array([0.0] * len(rates))
porosities = np.array([0.0] * len(rates))

ux = np.load('ux/UXPorous1.npy')
uy = np.load('uy/UYPorous1.npy')
rho = np.load('rho/RHOPorous1.npy')
epsilon = np.load("epsilon/EpsilonPorous1.npy")

Ny1, Nx1 = ux.shape[0], ux.shape[1]
Ny, Nx = Ny1 - 1, Nx1 - 1
solids = 1 - epsilon
porosity_por = np.sum(solids) / (Ny1 * Nx1)

k_out_por = calc_k(rho)
print("Коэффициент проницаемости:", k_out_por)
print("Коэффициент пористости:", porosity_por)
print()

for j in range(len(rates)):
    # ux = np.load('ux25/UX' + str(rates[j]) + '.npy')
    # Ny1, Nx1 = ux.shape[0], ux.shape[1]
    # uy = np.load('uy' + str(Nx1) + '/UY' + str(rates[j]) + '.npy')
    # rho = np.load('rho' + str(Nx1) + '/RHO' + str(rates[j]) + '.npy')
    # epsilon = np.load("epsilon" + str(Nx1) + "/Epsilon" + str(rates[j]) + '.npy')

    ux = np.load('ux/UX' + str(rates[j]) + '.npy')
    Ny1, Nx1 = ux.shape[0], ux.shape[1]
    uy = np.load('uy' + '/UY' + str(rates[j]) + '.npy')
    rho = np.load('rho' + '/RHO' + str(rates[j]) + '.npy')
    epsilon = np.load("epsilon" + "/Epsilon" + str(rates[j]) + '.npy')

    ux = np.load('UX.npy')
    uy = np.load('UY.npy')
    rho = np.load('RHO.npy')
    epsilon = np.load("Epsilon.npy")

    Ny, Nx = Ny1 - 1, Nx1 - 1
    solids = 1 - epsilon
    porosity = np.sum(solids) / (Ny1 * Nx1)

    k_out = calc_k(rho)
    permeabs[j] = k_out
    porosities[j] = porosity
    print("Коэффициент проницаемости:", k_out)
    print("Коэффициент пористости:", porosity)
    print()


dm_lattice = 0.55
dm_2 = dm_lattice * dm_lattice * dx * dx / Darcy
CG = 16. / (9 * pi * np.sqrt(2)) #sqrt 2
m = np.linspace(0.216, 0.73, 100)
phi_c = 1-pi / 4#(2*np.sqrt(3))
K_analit = dm_2*CG*(np.sqrt((1-phi_c) / (1 - m)) - 1)**(2.5)

porosities, permeabs = sort_points(porosities, permeabs)

plt.plot(m, K_analit, label='Аналит. значения')
plt.plot(porosities[0:], permeabs[0:], label='Найденные значения')
plt.plot(porosities[0:], permeabs[0:], marker='o', markersize=7, linestyle='', zorder=4)

# plt.plot(porosity_por, k_out_por, marker='x', markersize=8)

plt.legend()
plt.xlabel('Пористость')
plt.ylabel('Проницаемость')
plt.grid()
plt.show()