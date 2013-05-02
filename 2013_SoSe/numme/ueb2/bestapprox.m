function [p] = bestapprox(n) 
%% Eingabe: n = Grad des Zielpolynoms
%% Ausgabe: p = Ergebnis der Bestapproximation
  f = @(x) exp(x);
  x = sym('x'); // variable des polynoms pn

  // Stelle LGS A*c = b auf
  //  Berechne Matrix A
  A = zeros(n+1);
  for i = 1:(n+1)
   for j = 1:(n+1)
    // a_ij = (x^i, x^j) <=>
    // a_ij = int_0^1 x^i * x^j = 2^(i+k+1)/(i+j+1)
    // mit i,j = 0,...,n
    // Da Indizierung mit 1 statt 0 beginnt, jeweils 1 abziehen
    A(i,j) = 2^(i+k+1)/((i-1)+(j-1)+1);
   end
  end
  // Zielvektor b aufstellen als
  // b_i = (f, x^i), i = 0,...,n
  b = zeros(n+1);
  for i = 1:(n+1)
   b(i) = integral(@(x)f(x)*x^(i-1),0,2);
  end

c = sym('c');

// p ist Koeffizientenvektor des Polynoms
// und Loesung des LGS
p = solve(A*c = b,c);

 hold on;
// Beide plotten
ezplot(f,[0,2]);
ezplot(p,[0,2]);
