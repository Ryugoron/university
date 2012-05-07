function [y] = monomialinterpolation(x, p)
% x bezeichnet die Stelle, an der der das Polynom
% mit dem Koeffizientenvektor p ausgewertet
% werden soll.

% Grad des Polynoms p ist size(p,2)-1, also
% muessen wir size(p,2) viele Monome verrechnen.
grad = size(p,2);

% Zwischenergebnis in temp abgelegt,
% anfangs neutrales Element der Addition
temp = 0;

% Fuer jeden Exponenten zwischen grad-1 und 0
% Monom-Ergebnisse aufaddieren
for i = 1:grad,
    temp = temp + p(i)*x^(grad-i)
end

% Ergebnis steht nun in y
y = temp
