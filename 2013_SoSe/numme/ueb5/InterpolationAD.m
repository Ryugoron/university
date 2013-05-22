function [] = InterpolationAD(f,I,n)
%% Grenzen des Intervalls
a = I(1);
b = I(2);
%% Genaugkeit des Plots
genauigkeit = 0.1;
dist = min(genauigkeit, round((b-a)/n));

%% Erstelle Gitter:
%% Aequidistanter Gitter a = x0 < x1 < ... < xn = b
%% mit Distanz (b-a)/n
stuetzSt = [a:(b-a)/n:b];
%% Funktionswerte an den Stuetzstellen
fstuetzSt  = f(stuetzSt);

%% Dazu: erstelle Funktionswerte an allen Stellen
%% der gewaehlten Genauigkeit
%% unter Nutzung von Aitken-Neville
x  = [a:dist:b];
fx = zeros(1,length(x));
for i = 1:length(x)
  fx(i) = AitkenNeville(stuetzSt,fstuetzSt,x(i));
end;

%% Plot
h = plot(x,fx);
title('Interpolation durch Polynom n-ten Grades');
xlabel('x');
ylabel('p(x)');

saveas(h,'plot.png');
