clear ; close all; clc; format shortG
c1 = -0.01;c2 = -5;c3 = -3.8;c4 = 8*10^(-4);c5 = -0.5;c6 = 0.5;c7 = -1.3;c8 = 5*10^(-4);c9 = 0.8;c10 = -0.8;c11 = -4;c12 = 9;iv = 2.2;Tcn = 0.15;
 
A=[c1 c2  0    c3      0;
   c4 c5  0    c6     c7;
   c8 c9  0    c10    c11;
    0  0  1    0       0;
    0  0 c12 iv/Tcn -1/Tcn];
B=[0; 0; 0; 0; iv/Tcn];
C =[0 1 0 0 0]; D = 0;

yct=0.05;tmax=30;
Qy=zeros(2);
Qy(1,1)=1/yct^2;
Qy(2,2)=Qy(1,1)*(tmax/3)^2;
H=[C;C*A];
Q=H'*Qy*H
[P,Lam,Kopt,pogr]=care(A,B,Q)
Az=A-B*Kopt;
Kopt_m = -Kopt;
K = -inv(C/Az*B);
s1=ss(Az,B,C,D);
x0=[1;1;1;1;1];
%initial(s1,x0),grid on

