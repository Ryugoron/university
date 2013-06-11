function [S] = romberg(a,b,f,h)
% Gibt den Zahlenwert S der Quadratur zurueck
%
% [a,b] Integrationsintervall
% f zu intigrierende Funktion
% h Liste der Schrittweiten
n = length(h);

t = zeros(n,1);
for i = 1:n
  t(i) = trapQuad(a:h(i):b,f);
  for k = i:-1:1
    t(k) = t(k+1) + (t(k+1)-t(k))/((h(k)/h(i))^2 - 1);
  end
end
S = t(1);
