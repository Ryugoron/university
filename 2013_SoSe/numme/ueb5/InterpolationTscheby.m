function [] = InterpolationAD(f,I,n)
%% Grenzen des Intervalls
a = I(1);
b = I(2);
%% Genaugkeit des Plots
genauigkeit = 0.1;
dist = min(genauigkeit, round((b-a)/n));

%% Erstelle Gitter:
%% Tschebyscheff-Gitter mit 
stuetzSt = zeros(1,n+1);
for i = 0:n
  stuetzSt(i+1) = a + ((b-a)/2) *(cos((2*i+1)/(2*(n+1)) * pi) + 1);
end;
%% Funktionswerte an den Stuetzstellen
fstuetzSt  = f(stuetzSt);

%% Erstelle Funktionswerte an allen Stellen
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
