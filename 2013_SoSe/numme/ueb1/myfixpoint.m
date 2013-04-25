function [x] = myfixpoint (f, lambda, start, error)
%% Fixpunktiteration fuer
%% Funktion f mit Lipschitzkonstante lambda
%% vom Startwert start und Abbruchfehler error.

%% Initialisierung der ersten beiden Folgenelementee
lastx = start;
x = f(start);

%% Iteration  mit Abbruchbedingung der a posteori-Abschaetzung
while lambda / (1 - lambda) * norm(lastx - x, inf) > error
    lastx = x;
    x = f(x);
end;
