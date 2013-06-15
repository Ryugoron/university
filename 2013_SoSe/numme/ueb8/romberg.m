function [S] = romberg(a,b,f,h)
% Gibt den Zahlenwert S der klassischen
% Romberg-Quadratur zurueck
%
% [a,b] Integrationsintervall
% f zu intigrierende Funktion
% h Liste der Schrittweiten
%  (absteigende Gitterbreite!)

% Anzahl der Gitterbreiten
n = length(h);

t = zeros(n,1);

%% Berechnung nach Aitken-Neville-Schema
%% mit Rekursion aus dem Skript
for i = 1:n
  %% summierte Trapezregel zur breite h_i
  t(i) = trapQuad([a:h(i):b],f);
  %% Rekursion
  for k = (i-1):-1:1
    t(k) = t(k+1) + (t(k+1)-t(k))/((h(k)/h(i))^2 - 1);
  end
end
S = t(1);
