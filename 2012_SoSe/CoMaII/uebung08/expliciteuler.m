function [ xk ] = expliciteuler(T, tau, lambda, f, x0 )
%EXPLICITEULER berechnet das Eulersche Polygonzugverfahren
%fuer das AWP x' = lambda*x+f, x(0) = x0 mit einem
%aequidistanten Gitter 0 = t0 < t1 < ... < tn = T

xk(1) = x0;
range = 0:tau:T; %% Gitter mit Abstand tau

%% Berechnungsvorschrift aus dem Skript in einer Schleife
for k = 1:size(range,2)-1,
    xk(k+1) = xk(k) + tau*(lambda*xk(k)+f((k-1)*tau));
end
end

