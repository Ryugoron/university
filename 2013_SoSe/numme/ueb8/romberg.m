function [S] = romberg(a,b,f,n)
% Gibt den Zahlenwert S der Quadratur zurueck
%
% [a,b] Integrationsintervall
% f zu intigrierende Funktion
% n anzahl der teilintervalle

%% h(1) = h0
h = 2.^(-1.*[0:j])*b

t = zeros(n,1);
for i = 1:n
  
  t(i) = trapez
  for k = i:-1:1
    t(k) = t(k+1) + (t(k+1)-t(k))/((h(k)/h(i))^2 - 1)
  end
end
