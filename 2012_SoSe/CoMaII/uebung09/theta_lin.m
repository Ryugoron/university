function [ x, t ] = theta_lin(theta, lambda,f, x0, T, tau )
%theta_lin approximiert das AWP
%AWP x' = lambda*x+f, x(0) = x0 mit einem
%aequidistanten Gitter 0 = t0 < t1 < ... < tn = T
%mit Hilfe des Theta-Verfahrens für Eingabe theta.

x(1) = x0;
t = 0:tau:T; %% Gitter mit Abstand tau

for k = 1:size(t,2)-1,
    x(k+1) = ((x(k)*lambda*tau)*(1 - theta) + x(k) + (f((k-1)*tau)*tau)*(1+theta)+ theta*tau*f(k*tau))/(1-tau*lambda*theta);
% f(k*tau) ist f(t_k+1)...
end
end

