function v = AitkenNeville(x, fx, z)
% x Stuetzstellen
% fx Funktionswerte der Stuetzstellen
% z Auswertungsstelle

% n ist die Anzahl der Stuetzstellen
n = size(x,2);

% Initialwerte fuer alle p_11
for i = 1:n,
    p(i,i) = fx(i)
end

% Nach dem Schema von Aitken
% Berechnung der restlichen Terme
for i = 1:n,
    for j = 1:n-i,
      p(j,j+i) = 1/(x(j+i)-x(j)) * ((z-x(j))*p(j+1,j+i) - (z-x(j+i))*p(j,j+i-1))
    end
end

% Rueckgabe ist p0n(x)
v = p(1,n);
