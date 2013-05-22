function [] = InterpolationAD(f,I,n)
%% Grenzen des Intervalls
a = I(1);
b = I(2);
%% m ist die Anzahl der Gitterpunkte (Grad + 1)
m = n+1;
%% Genaugkeit des Plots
genauigkeit = 0.1;
dist = min(genauigkeit, ceil((b-a)/m));

%% Erstelle Gitter:
%% Aequidistanter Gitter a = x0 < x1 < ... < xm = b
%% mit Distanz (b-a)/m
stuetzSt = [a:(b-a)/m:b];
%% Funktionswerte an den Stuetzstellen
fstuetzSt  = f(stuetzSt);

%% Dazu: erstelle Funktionswerte an allen Stellen
%% der gewaehlten Genauigkeit
%% unter Nutzung von Aitken-Neville
x  = [a:dist:b];
fx = zeros(1,ceil((b-a)/dist));
for i = 1:(b-a)/dist
  fx(i) = AitkenNeville(stuetzSt,fstuetzSt,x(i));
end;

%% Plot
h = plot(x,fx);
title('Interpolation durch Polynom n-ten Grades');
xlabel('x');
ylabel('p(x)');

saveas(h,'plot.png');
