function [ xk ] = impliciteuler(T, tau, lambda, f, x0 )
%impliciteuler berechnet das implizite Euler-Verfahren
%fuer das AWP x' = lambda*x+f, x(0) = x0 mit einem
%aequidistanten Gitter 0 = t0 < t1 < ... < tn = T

xk(1) = x0;
range = 0:tau:T; %% Gitter mit Abstand tau

%% Berechnungsvorschrift aus dem Skript in einer Schleife
for k = 1:size(range,2)-1,
    %% S bezeichnet die nach x_k (also xk(k)) umgestellte
    %% Gleichung der Berechnungsvorschrift
    S = (f(k*tau)*tau+xk(k))/(1-tau*lambda);
    xk(k+1) = S;
end
end

