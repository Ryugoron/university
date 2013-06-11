function [S] = simpQuad(g,f)
% Gibt den Zahlenwert S der Quadratur zurueck.
%
% g Gitter
% f zu intigrierende Funktion
% 

m = length(g)

%% Simpson-Berechnungsvorschrift
% (Algorithmisches) Vorgehen analog zu Trapez
S = 0;
for k = 2:m,
   h = g(k) - g(k-1);
   m = (g(k) + g(k-1)) / 2;
   S = S + h/6 * (f(g(k)) + 4*f(m) + f(g(k-1)));
end
return % Aufwand 3n
