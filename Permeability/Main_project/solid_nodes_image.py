import numpy as np
from PIL import Image

image = Image.open('Porous1.jpg')
width = image.size[0]
height = image.size[1]

img_mx = image.convert('L')
img_np = np.array(img_mx.getdata()).reshape(img_mx.size[1], img_mx.size[0])
img_np = img_np / 255

ostw = width % 5
osth = height % 5

if ostw != 0:
    begin = ostw // 2
    end = ostw - begin
    img_np = img_np[:, begin:width-end]
if osth != 0:
    begin = osth // 2
    end = osth - begin
    img_np = img_np[begin:height-end, :]
img_np = img_np[:, 3:width-2]

deltax = 10 # 7: 595
deltay = 10 #10: 600
Ny1 = img_np.shape[0] // deltay
Nx1 = img_np.shape[1] // deltax
solids = np.array([[0.0] * Nx1] * Ny1)
for j in range(Ny1):
    for i in range(Nx1):
        sum = 0.0
        for jb in range(deltay):
            for ib in range(deltax):
                 sum += img_np[j*deltay + jb, i*deltax + ib]
        solids[j, i] = sum / (deltax*deltay)

solids[0, :] = 1                #возможно, можно закомментить
solids[Ny1-1, :] = 1
epsilon = 1 - solids
porosity = np.sum(solids) / (Ny1*Nx1)
np.save('epsilon/Epsilon' + 'Porous12', epsilon)
image.show()
