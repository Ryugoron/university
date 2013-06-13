function S = adapQuadError (g, f, tol, n, maxN)

%
% g     - Gitter
% f     - Funktion die zu Integieren ist
%
% tol   - Abstand zwischen Simpson und Trapez
% n     - Schwellwert für das verfeinern.
% maxN  - maximale Gitterpunkte
%
% return - Matrix mit (Werten, #Gitterpunkte, gemessener Fehler) als
%           Spalten
%
m = length(g);

sim = simpQuad(g, f);
trap = trapQuad(g, f);

%% Speichert am Ende
%% #Stützstellen , Wert über dem Gitter, geschätzter Fehler
e = [m trap (abs(sim - trap))];

% Solange wir über dem tolleranzwert sind,
% oder wir noch nicht genügend Stützstellen haben
while (abs(sim - trap) > tol) && m < maxN,
    gs = [g(1)];
    sch = n * (tol / m);
    for k = 2:m
        %%
        %% Testen wir für alle Intervalle, ob wir verfeinern müssen
        %%
        if (abs(simpQuad( g(k-1:k), f) - trapQuad( g(k-1:k), f))) > sch
            gs = [gs ((g(k) + g(k-1))/2) g(k)];
        else
            gs = [gs g(k)];
        end
    end
    g = gs;
    m = length(g);
    sim = simpQuad(g, f);
    trap = trapQuad(g, f);
    e = [e; m trap (abs(sim - trap))];
end
S = e
end
