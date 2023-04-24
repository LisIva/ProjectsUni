clear ; close all; clc; format shortG
c1 = -0.01;c2 = -5;c3 = -3.8;c4 = 8*10^(-4);c5 = -0.5;c6 = 0.5;c7 = -1.3;c8 = 5*10^(-4);c9 = 0.8;c10 = -0.8;c11 = -4;c12 = 9;iv = 2.2;Tcn = 0.15;
 
A=[c1 c2  0    c3      0;
   c4 c5  0    c6     c7;
   c8 c9  0    c10    c11;
    0  0  1    0       0;
    0  0 c12 iv/Tcn -1/Tcn];
B=[0; 0; 0; 0; iv/Tcn];
C =[0 1 0 0 0]; D = 0;

sn=ss(A,B,C,D);
wn=tf(sn);
[qw,pw]=tfdata(wn);
 
p=pw{1};
a=[p(6);p(5);p(4);p(3);p(2)];
b=B;

T = [a(2) a(3) a(4) a(5) 1;
     a(3) a(4) a(5) 1    0;
     a(4) a(5)   1  0    0;
     a(5)    1   0  0    0;
     1      0    0 0     0];
Su=ctrb(A,b);
Q=Su*T;
Q1=inv(Q);
Af=Q\A*Q,bf=Q\b
Lz=[-1; -0.8; -8.6; -4.3; -3.5]
pz=poly(Lz);
az=[pz(6);pz(5);pz(4);pz(3);pz(2)];
k=Q'\(a-az)



Lz=1.1*[-1; -0.8; -8.6; -4.3; -3.5];
pz=poly(Lz);
az_nab=[pz(6);pz(5);pz(4);pz(3);pz(2)]
Sn=obsv(A,C);
L=-((T'*Sn')^(-1))*(az-az_nab)


An = A - L*C;
s1=ss(An,b,C,D);
% x0=[1;1;1;1;1];
% initial(s1,x0)



% s2=ss(Az,B,C,D);
% figure
% step(s2, 10)


% 
% 
