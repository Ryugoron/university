package ueb4;


public class Matrix {  
  private double[][] internalMatrix;
  
  /**
   * Erstellt ein Matrix aus M(m,n,Double)
   * 
   * @param m Zeilenlänge der Matrix
   * @param n Spaltenlänge der Matrix
   */
  public Matrix(int m, int n) {
    internalMatrix = new double[m][n];
  }
  
  /**
   * 
   * @return Länge einer Spalte
   */
  public int getColumnDimension() {
    return internalMatrix.length;
  }
  
  /**
   * 
   * @return Länge einer Zeile
   */
  public int getRowDimension() {
    return internalMatrix[0].length;
  }
  
  /**
   * Gibt ein Eintrag der Matrix zurück
   * @param i Zeile
   * @param j Spalte
   * @return
   */
  public double get(int i, int j) {
    return internalMatrix[i][j];
  }
  
  /**
   * Setzt einen Eintrag der Matrix
   * @param i Zeile
   * @param j Spalte
   * @param s neuer Wert
   */
  public void set(int i, int j, double s) {
    internalMatrix[i][j] = s;
  }

  /**
   * Multipliziert 2 Matrizen, sollten
   * die Dimensionen nicht stimmen, wird
   * eine ArrayOutOfBoundsException 
   * geworfen.
   * @param a erste Matrix
   * @param b zweite matrix
   * @return
   */
  public static Matrix mult(Matrix a, Matrix b) {
    
    int m = a.getColumnDimension();
    int l = a.getRowDimension();
    int n = b.getRowDimension();
    
    Matrix c = new Matrix(m, n);
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        c.internalMatrix[i][j] = 0.0;
        for (int k = 0; k < l; k++) {
          c.internalMatrix[i][j] += a.internalMatrix[i][k] * b.internalMatrix[k][j];
        }
      }
    }
    
    return c;
  }
  
  
  /**
   * Gibt die Matrix formatiert aus.
   */
  public String toString() {
    
    String s = "";
    for (int i = 0; i < getColumnDimension(); i++) {
      for (int j = 0; j < getRowDimension(); j++) {
        s += internalMatrix[i][j] + ", ";
      }
      s += "\n";
    }
    return s;
  }
}