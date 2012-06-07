function y = gauss(f, n, a, b)
% Diese Funktion berechnet die Integral
% von f von a bis b mittels
% Gauss-Quadratur und
% sog. Mittelpunktregel
% f Funktion
% n Anzahl der Stuetzstellen
% a, b Grenzen

% Breite des Gitters
h = (b-a)/n;
% Schritte im Intervall
xi = a:h:b;
% Berechne Funktionswerte an den geg. Stellen
for i = 1:n,
   fx(i) = f((xi(i)+xi(i+1))/2);
end
% Aufsummieren
y = h * sum(fx);
