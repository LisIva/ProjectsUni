import matplotlib.pyplot as plt
import numpy as np
from matplotlib import cm
import matplotlib.colors as mcolors


# ux = np.load('ux/UXPorous1.npy')
# uy = np.load('uy/UYPorous1.npy')
# epsilon = np.load("epsilon/EpsilonPorous1.npy")
ux = np.load('UX.npy')
uy = np.load('UY.npy')
epsilon = np.load("Epsilon.npy")

Ny1, Nx1 = ux.shape[0], ux.shape[1]

x = [i for i in range(Nx1)]
y = [j for j in range(Ny1)]

X, Y = np.meshgrid(x, y)
i, j = np.where(uy == 0.0)
seek_points = np.array([j, i]).T

fig = plt.figure(figsize=(13, 9))
ax = fig.add_subplot(111)
ax.invert_yaxis()
ax.set(xlim=(0, Nx1-1), ylim=(0, Ny1-1))
plt.imshow(epsilon, cmap='Greys', aspect=1, zorder=0.0) #, vmin=0.185, vmax=0.225


u_mod = np.sqrt(uy*uy+ux*ux) #density= 7 или 17
strm = ax.streamplot(X, Y, ux, uy, color=u_mod, cmap=cm.brg, density=13, zorder=3.0)#, norm=mcolors.Normalize(0.185, 0.225))
plt.xlabel('№ узла решетки')
plt.ylabel('№ узла решетки')
fig.colorbar(strm.lines)

plt.plot(seek_points[:, 0], seek_points[:, 1], color=mcolors.CSS4_COLORS.get('indigo'), marker='o', markersize=30, linestyle='', zorder=4) # markersize=40
plt.tight_layout()
plt.show()

# Q = ax.quiver(X, Y, ux, uy, u_mod, cmap=cm.brg)#, scale=2, width=0.2, units='xy')#, color=cm.brg(u_mod), scale=500)#, angles='xy')
# plt.plot(seek_points[:, 0], seek_points[:, 1], color=mcolors.CSS4_COLORS.get('indigo'), marker='o', markersize=15, linestyle='', zorder=4) # markersize=40
# plt.tight_layout()
# plt.show()
