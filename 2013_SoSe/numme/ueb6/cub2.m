%% Gibt ein Gitter X = {x_1, ..., x_n} vor mit Werten
%% Y = {y_1 = f(x_1) , .... , y_n = f(x_n)} und berechnet
%% die Interpolierte Funktion an den Punkten XPos = { t_1 ... t_m }
%% wobei wir t_i < t_(i+1) annehmen (wichtig!)
%% R ist ein Vektor mit Randbedingungen
%% in diesem Fall die Ableitung in x_0 und x_n
function YPos = cub2 (X , Y, R , XPos)
    %% Anzahl der Intervalle
    n = length(X) - 1;

    %% Losen der linearen Gleichung Mc = b
    beta = zeros(1,n+1);
    M = 2 * eye(n+1);
    
    %% Randbedingung (Hermite Gleichung
    beta(1) = 6 * ((Y(2) - Y(1))/(X(2)-X(1))-R(1))/(X(2) - X(1));
    beta(n+1) = 6 * (R(2) - (Y(n+1) - Y(n))/(X(n+1) - X(n)))/(X(n+1)-X(n));

    for i = 2:n
        fa = (Y(i) - Y(i-1))/(X(i) - X(i-1));
        fb = (Y(i+1) - Y(i))/(X(i+1) - X(i));
        beta(i) = 6*(fb - fa)/(X(i+1) - X(i-1));
    end

    %% Matrix M mit mu und lambda wie im skript
    M(1,2) = 1; %% lambda_0 ist immer 1
    for i = 1:(n-1)
        M(i+1,i+2) = (X(i+2)-X(i+1))/(X(i+2) - X(i));
    end

    M(n+1,n) = 1; %% mu_n ist immer 1
    for i = 1:(n-1)
        M(i+1,i) = (X(i+1) - X(i))/(X(i+2) - X(i));
    end
    c = linsolve(M, beta');
    
    %% Berechne b's
    b = zeros(1,n);
    for i = 1:n
        h2 = X(i+1) - X(i);
        f2 = (Y(i+1) - Y(i))/h2;
        b(i) = f2 - h2/6*(2 * c(i) + c(i+1));
    end

    %% Berechne d's
    d = zeros(1,n);
    for i = 1:n
        d(i) = (c(i+1) - c(i))/(X(i+1)-X(i));
    end
   
    %% Auswertung der Eingabestellen t_i
    %% Dazu: In welchem Intervall liegt t_i?
    %% Dann: Auswertung des kubisches Polynoms des Intervalls
    %% mit Taylor
    curPoly = 1;
    m = length(XPos);
    YPos = zeros(1,m);
    for i = 1:m
        %% XPos innerhalb des Intervalls
        while XPos(i) > X(curPoly+1)
            curPoly = curPoly + 1;
        end
        %% Auswertung mit Taylor
        YPos(i) = polyval([(d(curPoly)/6) (c(curPoly)/2) b(curPoly) Y(curPoly)], (XPos(i) - X(curPoly)));
    end
    return;
