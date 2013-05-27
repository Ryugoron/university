function YPos = cubSpline (X , Y)
    n = lenght(X);
    %% Wir wollen zunächst der als Lösung von Mc = b darstellbar ist
    b = zeros(1,n);

    %% Hier hab ich die Natürlichen Randbedingungen gewählt, kann wahlweise noch ergänzt werden
    %% f'(x_0) = 0 und f'(x_n) = 0. Damit sind alle Freiheitsgerade aufgebraucht.
    b(1) = 6 * (Y(2)-Y(1))/(X(2)-X(1));
    b(n) = 6 * (-Y(n) - Y(n-1))/(X(n) - X(n-1));

    for i = 2:(n-1)
        %% Aitken-Neville für 6 * f[x_i-1 x_i x_i+1] = beta_i
        b(i) = 6 * ((Y(i+1)- Y(i))/(X(i+1) - X(i) + (Y(i) - Y(i-1))/(X(i) - X(i-1))))/(X(i+1) - X(i-1));
    end
    M = 2 * eye(n);
   
    %% lambda_0 = h_{1} / (h_{0} + h_{1}) = (x_1 - x_0) / (x_0 - x_-1 + x_1 - x_0)
    %% Und x_-1 = x_0 definiert
    M(1,2) = 1 
    for i = 2:(n-1)
        %% lambda_i rechts von M(i,i)
        M(i,i+1) = (X(i+1)-X(i))/(X(i)-X(i-1) + X(i+1) - X(i));
    end
    %% Ebenso ist h_{n+2} = 0 da es so definiert wurde
    M(n,n-1) = 1
    for i = 1:(n-2)
        %% mu_i unter M(i,i)
        M(i+1,i) = (X(i+1)-X(i))/(X(i+1)-X(i) + X(i+2) - X(i+1));
    end
    
    %% Standard Matlab Function to solve a system of linear equations
    c = linsolve(M,b);
    
    %% Create a new Vector of size n
    YPos = zeros(1,n);
    %% Stabile rekursion aus dem Skript
    for i = 1:(n-1)
        YPos(i) = (Y(i+1) - Y(i))/(X(i+1) - X(i)) - (X(i+1) - X(i))*(2*c(i)+c(i+1))/6;
    end
    return;
