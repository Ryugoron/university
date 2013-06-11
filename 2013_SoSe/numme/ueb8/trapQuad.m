function [S] = trapQuad(g,f)
% Gibt den Zahlenwert S der Quadratur zurueck
%
% g Gitter
% f zu intigrierende Funktion
%

% Gewichte jeweils aus dem Skript entnommen,
% sowie Berechnungsvorgehen

m = length(g);

%% Trapez-Berechnungsvorschrift
for k = 2:(m),
   h = g(k) - g(k-1);
   S = S + (f(g(k)) + f(g(k-1)))*.h/2;
end
return % Aufwand n+1
