function [x] = ausgleich ( A,b )

dim = size(A);
%% Anzahl der iteration bis wir eine obere Dreiecksmatrix haben
t = min([dim(1)-1,dim(2)]);

for k = 1 : t
  e = [1;zeros(dim(1)-k,1)];
  v = A(k:dim(1),k);
  v = v + (-1*sign(v(1)))*norm(v,2) * e;
  v = v/norm(v,2);
  %% Baut die HousholdrMatrix zur j ten Spalte ab dem j ten Eintrag
  q = eye(dim(1)-k+1) - 2*v*v';
  q = [eye(k-1) zeros(k-1,dim(1)-k+1);zeros(dim(1)-k+1,k-1) q];
  %% Iteriert Q_i...Q_1A, was am Ende Q^TA ergibt
  A = q*A;
  %% Iteriert Q_i ... Q_1b, was am Ende Q^Tb ergibt.
  b = q*b;
end;
  %% Trennt die rechte obere Dreiecksmatrix vom Nullteil.
  R = A(1:dim(2),:)
  %% Berechnet die Lösung des linearen Ausgleichsproblems
  x = inv(R)*b(1:dim(2));
