function [S] = trapQuad(a,b,f,n)
% Gibt den Zahlenwert S der Quadratur zurueck
%
% [a,b] Integrationsintervall
% f zu intigrierende Funktion
% n anzahl der teilintervalle

ab = [a,b];
% Bezeichnungen ab hier wie im Skript
% h ist der Abstand zwischen den Stellen
h = (ab(2) - ab(1))/n;
% z sind die Stuetzstellen
z = ab(1) + (1:n).*h;

% Gewichte jeweils aus dem Skript entnommen,
% sowie Berechnungsvorgehen

%% Trapez-Berechnungsvorschrift
x = @(k) z(k);
S = h/2 * (f(ab(1)) + f(ab(2)));
for k = 2:(n),
   S = S + h*f(x(k)); % Siehe Skript
end
return % Aufwand n+1