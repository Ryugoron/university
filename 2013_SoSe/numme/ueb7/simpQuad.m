function [S] = simpQuad(a,b,f,n)
% Gibt den Zahlenwert S der Quadratur zurueck.
%
% [a,b] Integrationsintervall
% f zu intigrierende Funktion
% n anzahl der teilintervalle

ab = [a,b];
%% Simpson-Berechnungsvorschrift
% (Algorithmisches) Vorgehen analog zu Trapez
x = @(k,j) z(k/2) + j*h/2;
S = 0;
for k = 1:(n),
   S = S + h/6 * (f(x(2*k,0)) + 4*f(x(2*k,1)) + f(x(2*k,2)));
end
return % Aufwand 3n