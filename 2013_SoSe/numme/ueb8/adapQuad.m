function [S] = adapQuad (g, f, tol, n, maxN)

%
% g         - Gitter
% f         - Funktion die zu Integieren ist
%
% tol       - Abstand zwischen Simpson und Trapez
% n         - Schwellwert für das verfeinern.
% maxN      - MaxN, maximale Gitterpunkt

m = length(g);

sim = simpQuad(g, f);
trap = trapQuad(g, f);

% Solange wir über dem tolleranzwert sind,
% oder wir noch nicht genügend Stützstellen haben
while (abs(sim - trap) > tol) && m < maxN,
    gs = [g(1)];
    %% Unser Schwellwert bezeichnet einen relativen Unterschied zur durchschnittlichen Grenze
    sch = n * (tol / m);
    for k = 2:m,
        %%
        %% Testen wir für alle Intervalle, ob wir verfeinern müssen
        %%
        if (abs(simpQuad( g(k-1:k), f) - trapQuad(g(k-1:k), f))) > sch
           gs = [gs ((g(k) + g(k-1))/2) g(k)];
        else
           gs = [gs g(k)]
        end
    end
    g = gs;
    m = length(gs);
    sim = simpQuad(g, f);
    trap = trapQuad(g, f);
end
S = trap;
return
