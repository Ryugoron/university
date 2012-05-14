function [y] = aitken(x, fx, z)
% x Stuetzstellen
% fx Funktionswerte der Stuetzstellen
% z Aussertungsstelle

n = size(x,2);
%y = p0n(z)

for i = 1:n,
    p(i:i) = fx(i)
end
for i = 1:n,
    for j = 1:n-i,
%        if (i == j)
 %           continue;
 %       else
            p(j,j+i) = 1/(x(k)-x(i)) * ((z-x(i)*p(i+1,j) - (z-x(i))*p(i,j-1))
  %      end
    end
end
y= p(0,n);

%pii(z) = fx(i)
%pik(z) = 1/(x(k)-x(i)) * ((z-x(i))p(i+1,k)(x) - (z-x(k))p(i,k-1)(z))

