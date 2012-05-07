function [p] = monomialcoefficients(xi, f)
% xi bezeichnet den Stuetzstellenvektor
% f bezeichnet die zu interpolierende Funktion

% Welchen Grad wird das Polynom haben?
% Grad ist size(xi,2) - 1, also haben
% wir size(xi,2) Stuetzstellen
grad = size(xi,2);

% Berechne Lagrange-Polynome L_k(x),
% fuer jedes 1 <= k <= grad
for k = 1:grad,
    % temp enthaelt das neutrale Element
    % der Polynommultiplikation
    % 
    temp = [1];
  
    % Konstruiere Vektor der Indizes,
    % ueber die das Produkt der Monome
    % berechnet wird. Hier wird jeder
    % Term mit i != k ausgewaehlt.
    range = 1:grad;
    indices = ones(1,grad);
    indices(k) = 0;
    % Multipliziere Polynome
    for i = range(logical(indices)),
      temp = conv(temp, [1/(xi(k)-xi(i)),
                   -xi(i)/(xi(k)-xi(i))]);
    end
    % Speichere k-tes Lagrange-Polynom
    L(k,:) = temp;
end

% Nun sind alle Lagrange-Polynome
% berechnet.
% Erstelle Matrix mit Funktionswerten
% von f auf der Diagonalen.
fks = eye(grad);
for i = 1:grad,
    fks(i,i) = f(xi(i));
end
% Multipliziere Funktionswert von f
% auf die jeweiligen Polynome.
L = fks * L;
% Bilde die zeilenweise Summe
% der Polynome.
p = L' * ones(grad,1); 
p = p';
% p enthaelt nun die Koeffizienten
% der interpolierten Funktion
