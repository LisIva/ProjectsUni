import numpy as np
from solid_nodes import epsilon, Nx1, Ny1, epsilons, indexes
# from solid_nodes_image import epsilon, Nx1, Ny1

Q = 9
Nx = Nx1 - 1
Ny = Ny1 - 1

cx = np.array([0, 1, 0, -1, 0, 1, -1, -1, 1])
cy = np.array([0, 0, 1, 0, -1, 1, 1, -1, -1])
w = np.array([4.0 / 9, 1.0 / 9, 1.0 / 9, 1.0 / 9, 1.0 / 9, 1.0 / 36, 1.0 / 36, 1.0 / 36, 1.0 / 36])
rc = np.array([0, 3, 4, 1, 2, 7, 8, 5, 6])  # index of reversed velocity for BB scheme
cs_2 = 1 / 3 #cs^2
cs_minus2 = 3 #cs^(-2)
#########################    Initialization    ###############################
rho0 = 4.0
ux0 = 0
uy0 = -0.1

rho = np.array([[rho0] * Nx1] * Ny1)
ux = np.array([[ux0] * Nx1] * Ny1)
uy = np.array([[uy0] * Nx1] * Ny1)
f = np.array([[[0.0] * Nx1] * Ny1] * Q)
f_post = np.array([[[0.0] * Nx1] * Ny1] * Q)

############################   TRT vars:  #######################
magic_parameter = 1 / 6
v0 = np.array([-0.3] * Nx1)  # top inlet velocity y
u0 = np.array([0.0] * Nx1)  # top inlet velocity x
omega_plus = 1.1
omega_min = 1.0 / (magic_parameter / (1.0 / omega_plus - 0.5) + 0.5)


def init_eq(ux, uy, rho):
    for k in range(Q):
        f[k] = feq(rho, ux, uy, k)
    return f


def feq(rho1, u, v, k):
    cu = cx[k] * u + cy[k] * v
    u2 = u * u + v * v
    return w[k] * rho1 * (1.0 + 3.0 * cu + 4.5 * cu * cu - 1.5 * u2)


def coll_trt(rho, ux, uy):
    f_post[0] = f[0] - omega_plus * (f[0] - feq(rho, ux, uy, 0))
    for k in range(1, Q):
        f_post[k] = f[k] - omega_plus * (
                0.5 * (f[k] + f[rc[k]]) - 0.5 * (feq(rho, ux, uy, k) + feq(rho, ux, uy, rc[k]))) - omega_min * (
                0.5 * (f[k] - f[rc[k]]) - 0.5 * (feq(rho, ux, uy, k) - feq(rho, ux, uy, rc[k])))
    return f_post


def force_term(f_post, rho):
    f_g = f_post
    for k in range(1, Q):
        f_g[k] = f_g[k] + gi(rho, k)
    return f_g


def gi(rho, k):
    g = 1.102 * 10**(-3)
    force = w[k] * rho * g * cy[k] / cs_2  #cs_2 = cs^2
    return force


def streaming():
    for k in range(1, Q):
        for jd in range(Ny1):
            for id in range(Nx1):
                jc = jd - cy[k]
                ic = id - cx[k]
                if 0 <= jc <= Ny:
                    if ic == Nx + 1:
                        ic = 0
                    eps_jdid = epsilon[jd, id]
                    eps_jcic = epsilon[jc, ic]

                    f[rc[k]][jd, id] = eps_jcic * f_post[k][jd][id] + (1 - eps_jdid) * f_post[rc[k]][jc, ic]

    return f


def top_inlet_bottom_outlet(v0, u0):
    v0 = v0 * solids[Ny, :]
    u0 = u0 * solids[Ny, :]
    rho_north = (1 / (1 + v0)) * (f[0][Ny, :] + f[1][Ny, :] + f[3][Ny, :] +
                                  2 * (f[5][Ny, :] + f[2][Ny, :] + f[6][Ny, :]))

    f[4][Ny, :] = f[2][Ny, :] - 2 / 3 * v0 * rho_north
    f[7][Ny, 1:Nx1] = f[5][Ny, 1:Nx1] + 0.5 * (f[1][Ny, 1:Nx1] - f[3][Ny, 1:Nx1]) -\
                      (v0[1:Nx1] / 6 + u0[1:Nx1] / 2) * rho_north[1:Nx1]
    f[8][Ny, 0:Nx] = f[6][Ny, 0:Nx] - 0.5 * (f[1][Ny, 0:Nx] - f[3][Ny, 0:Nx]) -\
                     (v0[0:Nx] / 6 + u0[0:Nx] / 2) * rho_north[0:Nx]

    f[6][0, 1:Nx] = f[6][1, 1:Nx] * solids[0, 1:Nx]  # Nx - избегая края, Nx1 - включая край
    f[2][0, :] = f[2][1, :] * solids[0, :]
    f[5][0, 1:Nx] = f[5][1, 1:Nx] * solids[0, 1:Nx]
    return f


def den_vel():
    rho = np.array([[0.0] * Nx1] * Ny1)
    ux1 = np.array([[0.0] * Nx1] * Ny1)
    uy1 = np.array([[0.0] * Nx1] * Ny1)

    for k in range(Q):
        rho += f[k]
        ux1 += cx[k] * f[k]
        uy1 += cy[k] * f[k]

    for j in range(Ny1):
        for i in range(Nx1):
            if rho[j, i] != 0:
                ux1[j, i] = ux1[j, i] / rho[j, i]
                uy1[j, i] = uy1[j, i] / rho[j, i]
            else:
                ux1[j, i] = 0
                uy1[j, i] = 0
    return ux1, uy1, rho


def check_conservations(f, rho, ux, uy):
    f_diff = np.array([[[0.0] * Nx1] * Ny1] * Q)
    momentumx = np.array([[0.0] * Nx1] * Ny1)
    momentumy = np.array([[0.0] * Nx1] * Ny1)
    mass_cons = np.array([[0.0] * Nx1] * Ny1)
    for k in range(Q):
        f_diff[k] += f[k] - feq(rho, ux, uy, k)
        mass_cons += f_diff[k]
        momentumx += f_diff[k] * cx[k]
        momentumy += f_diff[k] * cy[k]
    return mass_cons, momentumx, momentumy


def squre_sum(mass_cons, momentumx, momentumy):
    sum1 = np.linalg.norm(mass_cons)
    sum2 = np.linalg.norm(momentumx)
    sum3 = np.linalg.norm(momentumy)
    return np.array([sum1, sum2, sum3]).T

################################################# начало #####################################################
# rates = [1]
rates = indexes
num_iter = 5500
conserv_law = np.array([[0.0] * num_iter] * 3) # 2500 значений в строке, [2, :] - в пределах одного закона
stability = np.array([[0.0] * num_iter] * 3)

for j in range(len(rates)):
    rho = np.array([[rho0] * Nx1] * Ny1)
    ux = np.array([[ux0] * Nx1] * Ny1)
    uy = np.array([[uy0] * Nx1] * Ny1)
    epsilon = epsilons[j]
    solids = 1 - epsilon

    rho = rho * solids
    uy = uy * solids
    ux = ux * solids
    f = init_eq(ux, uy, rho)

    for i in range(num_iter):
        f_post = coll_trt(rho, ux, uy)
        f_post = force_term(f_post, rho)

        f = streaming()
        f = top_inlet_bottom_outlet(v0, u0)

        ux_prev, uy_prev, rho_prev = ux, uy, rho
        ux, uy, rho = den_vel()
        stability[:, i] = squre_sum(rho - rho_prev, ux - ux_prev, uy - uy_prev)
        if (i+1) % 500 == 0:
            print('Номер по rates:', j, 'из', len(rates)-1, '\nНомер по iter:', i+1, '\n')
            print("Стабильность: rho, ux, uy", stability[:, i])
            print()
        stability[:, i] = squre_sum(rho-rho_prev, ux - ux_prev, uy - uy_prev)
        mass_cons, momentumx, momentumy = check_conservations(f, rho, ux, uy)
        conserv_law[:, i] = squre_sum(mass_cons, momentumx, momentumy)

    print("rho:", stability[0, -1])
    print("ux:", stability[1, -1])
    print("uy:", stability[2, -1])
    # np.save('ux/UX' + 'Porous12', ux)
    # np.save('uy/UY' + 'Porous12', uy)
    # np.save('rho/RHO' + 'Porous12', rho)
    np.save('stability/ux' + str(Nx1) + '/UX' + str(rates[j]), ux)
    np.save('stability/uy' + str(Nx1) + '/UY' + str(rates[j]), uy)
    np.save('stability/rho' + str(Nx1) + '/RHO' + str(rates[j]), rho)
    np.save('stability/' + '/UX' + str(Nx1) + 'x' + str(Nx1), ux)
    np.save('stability/' + '/UY' + str(Nx1) + 'x' + str(Nx1), uy)
    np.save('stability/' + '/RHO' + str(Nx1) + 'x' + str(Nx1), rho)
    np.save('stability/Stability' + str(Nx1) + 'x' + str(Nx1), stability)

    np.save('UX', ux)
    np.save('UY', uy)

    names = ['mass_cons', 'momentumx', 'momentumy']
    arrays = [mass_cons, momentumx, momentumy]
    for i in range(3):
        print(names[i], ':', np.min(arrays[i]), '', np.max(arrays[i]))




np.save('Conserv_law', conserv_law)

print("END OF MAIN")
