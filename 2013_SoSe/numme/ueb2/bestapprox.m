function [p] = bestapprox(n) 
f = @(x) exp(x);

x = sym('x'); // variable des polynoms pn
base = x.^(0:n); // [1,x,x^2,x^3,...,x^n] Monombasis des pn

//A = zeros(n+1);
//for i = 1:(n+1)
// for j = 1:(n+1)
//  A(i,j) = base(i),base(1:n+1);
// end
//end
A = eye(n+1);
b = zeros(n+1);
for i = 1:(n+1)
 b(i) = skalarprodukt(f,base(i));
end

c = sym('c');
p = solve(A*c = b,c);
 hold on;
ezplot(f,[0,2]);
ezplot(p,[0,2]);
