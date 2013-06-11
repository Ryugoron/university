function [S] = adapQuad (g, f, tol, n)

%
% g - Gitter
% f - Funktion die zu Integieren ist
%
% tol - Abstand zwischen Simpson und Trapez
% n - Schwellwert für das verfeinern.

m = length(g);

sim = simpQuad(g, f);
trap = trapQuad(g, f);

% Solange wir über dem tolleranzwert sind,
% oder wir noch nicht genügend Stützstellen haben
while (abs(sim - trap) > tol) || m < 1000,
    g' = g;
    for k = 2:m
        %%
        %% Testen wir für alle Intervalle, ob wir verfeinern müssen
        %%
        if (simQuad( g(k-1:k), f) - trapQuad( g(k-1:k), f)) > n
           g' = [g'(1:k-1) ((g(k) + g(k-1))/2) g'(k:m)];
        end
    end
    g = g';
    m = length(g);
    sim = simQuad(g, f);
    trap = trapQuad(g, f);
end
S = trap;
return
