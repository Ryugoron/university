function [x] = ausgleich ( A,b )

dim = size(A);
t = min([dim(1)-1,dim(2)]);

for k = 1 : t
  e = [1;zeros(dim(1)-k,1)];
  v = A(k:dim(1),k);
  v = v + (-1*sign(v(1)))*norm(v,2) * e;
  v = v/norm(v,2);
  q = eye(dim(1)-k+1) - 2*v*v';
  q = [eye(k-1) zeros(k-1,dim(1)-k+1);zeros(dim(1)-k+1,k-1) q];
  A = q*A;
  b = q*b;
end;
  R = A(1:dim(2),:)
  x = inv(R)*b(1:dim(2));
