package ueb4;

/**
 * Die Matriximplementierung haben wir von
 * 
 * http://www.peter-junglas.de/fh/vorlesungen/algorithmen/html/s_matrix.html
 * 
 * und nur ein paar Fehler verbessert um uns auf die Kettenmultiplikation
 * konzentrieren zu können.
 * 
 * @author Peter Junglas
 *
 */
public class Matrix {
  // mathematische Matrix
  
  private double[][] arr;
  
  public Matrix(int m, int n) {
    // erzeuge nxm-Matrix, mit 0 vorbesetzt
    arr = new double[m][n];
  }
  
  public Matrix(Matrix a) {
    // erzeuge Matrix als Kopie von a
    
    int m = a.getColumnDimension();
    int n = a.getRowDimension();
    arr = new double[m][n];
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        arr[i][j] = a.arr[i][j];
      }
    }
  }
  
  public int getColumnDimension() {
    // Spaltenlänge
    return arr.length;
  }
  
  public int getRowDimension() {
    // Zeilenlänge
    return arr[0].length;
  }
  
  public double get(int i, int j) {
    // hole Array-Element A_ij
    return arr[i][j];
  }
  
  public void set(int i, int j, double s) {
    // setze Array-Element A_ij
    arr[i][j] = s;
  }
  
  public Matrix getMatrix(int i0, int i1, int j0, int j1) {
    // erzeugt eine Teilmatrix A(i0..i1, j0..j1)
    
    int m = i1 - i0 + 1;
    int n = j1 - j0 + 1;
    
    Matrix teil = new Matrix(m, n);
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        teil.arr[i][j] = arr[i + i0][j + j0];
      }
    }
    return teil;
  }
  
  void setMatrix(int i0, int j0, Matrix teil) {
    // belegt die Matrix mit einer Teilmatrix
    
    int m = teil.getColumnDimension();
    int n = teil.getRowDimension();
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        arr[i+ i0][j + j0] = teil.arr[i][j];
      }
    }
  }
  
  public static Matrix matmult(Matrix a, Matrix b) {
    // einfache Matrix-Multiplikation
    
    // Achtung: Dies ist eine Version zum Zweck der Zeitmessung, daher
    //   verzichtet sie auf Dimensions-Überprüfungen.
    
    int m = a.getColumnDimension();
    int l = a.getRowDimension();
    int n = b.getRowDimension();
    
    Matrix c = new Matrix(m, n);
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        c.arr[i][j] = 0.0;
        for (int k = 0; k < l; k++) {
          c.arr[i][j] += a.arr[i][k] * b.arr[k][j];
        }
      }
    }
    
    return c;
  }
  
  public static Matrix matmultStrassen(Matrix a, Matrix b) {
    return matmultStrassenMitCutoff(a, b, 1);
  }
  
  public static Matrix matmultStrassenMitCutoff(Matrix a, Matrix b, int cutoff) {
    // Matrix-Multiplikation nach dem Strassen-Algorithmus
    // für Matrizen mit Dimension >= cutoff wird normal multipliziert
    
    // Achtung: Dies ist eine Version zum Zweck der Zeitmessung, daher
    //   - verzichtet sie auf Dimensions-Überprüfungen,
    //    - geht sie von quadratischen Matrizen mit einer Zweierpotenz als Dimension aus
    
    int m = a.getColumnDimension();
    
    Matrix c = new Matrix(m, m);
    
    // Abbruchbedingung der Rekursion: m <= cutoff
    if (m <= cutoff) {
      // normal multiplizieren
      c = matmult(a, b);
      return c;
    }
    
    // ansonsten rekursiv a la Strassen
    
    // Teilmatrizen herausschneiden
    int dim = m/2;     // sollte ohne Rest aufgehen, da m Zweierpotenz
    Matrix a11 = a.getMatrix(  0, dim-1,   0, dim-1);
    Matrix a12 = a.getMatrix(  0, dim-1, dim,   m-1);
    Matrix a21 = a.getMatrix(dim,   m-1,   0, dim-1);
    Matrix a22 = a.getMatrix(dim,   m-1, dim,   m-1);
    Matrix b11 = b.getMatrix(  0, dim-1,   0, dim-1);
    Matrix b12 = b.getMatrix(  0, dim-1, dim,   m-1);
    Matrix b21 = b.getMatrix(dim,   m-1,   0, dim-1);
    Matrix b22 = b.getMatrix(dim,   m-1, dim,   m-1);
    
    // Matrizen m1 .. m7 berechnen
    // dazu zwei Hilfsmatrizen d1, d2 für Zwischenwerte
    Matrix d1 = a12.minus(a22);
    Matrix d2 = b21.plus(b22);
    Matrix m1 = matmultStrassen(d1, d2);
    
    d1 = a11.plus(a22);
    d2 = b11.plus(b22);
    Matrix m2 = matmultStrassen(d1, d2);
    
    d1 = a11.minus(a21);
    d2 = b11.plus(b12);
    Matrix m3 = matmultStrassen(d1, d2);
    
    d1 = a11.plus(a12);
    Matrix m4 = matmultStrassen(d1, b22);
    
    d1 = b12.minus(b22);
    Matrix m5 = matmultStrassen(a11, d1);
    
    d1 = b21.minus(b11);
    Matrix m6 = matmultStrassen(a22, d1);
    
    d1 = a21.plus(a22);
    Matrix m7 = matmultStrassen(d1, b11);
    
    // Teilmatrizen c11 .. c22 bestimmen
    Matrix c11 = new Matrix(m1);
    c11.plusGleich(m2);
    c11.minusGleich(m4);
    c11.plusGleich(m6);
    
    Matrix c12 = new Matrix(m4);
    c12.plusGleich(m5);
    
    Matrix c21 = new Matrix(m6);
    c21.plusGleich(m7);
    
    Matrix c22 = new Matrix(m2);
    c22.minusGleich(m3);
    c22.plusGleich(m5);
    c22.minusGleich(m7);
    
    // Gesamtmatrix zusammensetzen
    c.setMatrix(  0,   0, c11);
    c.setMatrix(  0, dim, c12);
    c.setMatrix(dim,   0, c21);
    c.setMatrix(dim, dim, c22);
    
    return c;
  }
  
  public  Matrix minus(Matrix a) {
    // gibt Differenz von aktueller Matrix und a zurück
    
    int m = getColumnDimension();
    int n = getRowDimension();
    Matrix c = new Matrix(m, n);
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        c.arr[i][j] = arr[i][j] - a.arr[i][j];
      }
    }
    return c;
  }
  
  public  Matrix plus(Matrix a) {
    // gibt Summe von aktueller Matrix und a zurück
    
    int m = getColumnDimension();
    int n = getRowDimension();
    Matrix c = new Matrix(m, n);
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        c.arr[i][j] = arr[i][j] + a.arr[i][j];
      }
    }
    return c;
  }
  
  public void plusGleich(Matrix a) {
    // addiert a zur aktuellen Matrix
    
    for (int i = 0; i < getColumnDimension(); i++) {
      for (int j = 0; j < getRowDimension(); j++) {
        arr[i][j] += a.arr[i][j];
      }
    }
  }
  
  public void minusGleich(Matrix a) {
    // subtrahiert a von der aktuellen Matrix
    
    for (int i = 0; i < getColumnDimension(); i++) {
      for (int j = 0; j < getRowDimension(); j++) {
        arr[i][j] -= a.arr[i][j];
      }
    }
  }
  
  public String toString() {
    // Ausgabestring: Zeilen durch Newline getrennt, Werte durch Komma
    
    String s = "";
    for (int i = 0; i < getColumnDimension(); i++) {
      for (int j = 0; j < getRowDimension(); j++) {
        s += arr[i][j] + ", ";
      }
      s += "\n";
    }
    return s;
  }
}