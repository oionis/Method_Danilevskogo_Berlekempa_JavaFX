package sample;

import java.util.ArrayList;
import java.util.List;

public class DM {
    String debugLog = "";
    double[][] A;
    double[] LambdaK;
    int n;

    public DM(List<Double> listA, int n) {
        this.A = new double[n][n];
        for (int i = 0; i < listA.size(); i++) {
            A[i / n][i % n] = listA.get(i);
        }
        debugAdd("Получение матрицы размером " + n + "x" + n + ":\n");
        debugADump();
        this.n = n;

        this.makeK();
    }

    public DM(double[][] A) {
        this.A = A;
        int n = A.length;
        debugAdd("Получение матрицы размером " + n + "x" + n + ":\n");
        debugADump();
        this.n = A.length;
        this.makeK();
    }

    private void debugAdd(String msg) {
        debugLog += msg;
    }

    private void debugADump() {
        debugLog += "A = {\n";
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                debugLog += "\t" + (int)A[i][j];
            }
            debugLog += "\n";
        }
        debugLog += "}\n";
    }

    private void debugDump(double[] X) {
        debugLog += "[";
        int xn = X.length;
        for (int i = 0; i < xn; i++) {
            if (i > 0) debugLog += " ";
            debugLog += X[i];
        }
        debugLog += "]";
    }

    private boolean step(int k) {
        debugAdd("Выполняем шаг " + (A.length - k - 1) + ".\n");
        boolean enable = true;
        if (this.A[k + 1][k] == 0) {
            // Find A[index] != 0
            int index = k - 1;
            while (index >= 0 && this.A[k + 1][index] == 0)
                index--;
            if (index < 0) {
                enable = false;
                double[][] D_ = new double[k + 1][k + 1];
                double[][] D__ = new double[n - k - 1][n - k - 1];
                for (int i = 0; i < k + 1; i++) {
                    for (int j = 0; j < k + 1; j++) {
                        D_[i][j] = this.A[i][j];
                    }
                }
                for (int i = k + 1; i < n; i++) {
                    for (int j = k + 1; j < n; j++) {
                        D__[i - k - 1][j - k - 1] = this.A[i][j];
                    }
                }
                double[] D__K = this.makeLambda(D__);
                DM DanContinue = new DM(D_);
                debugAdd(DanContinue.getDebug());
                this.LambdaK = this.multiplyPol(DanContinue.getKLambda(), D__K);
            } else {
                debugAdd("Переставляем строки и столбцы " + index + " и " + k + ". Получаем матрицу:\n");
                for (int l = 0; l < n; l++) {
                    double t = this.A[l][k];
                    this.A[l][k] = this.A[l][index];
                    this.A[l][index] = t;
                }
                for (int l = 0; l < n; l++) {
                    double t = this.A[k][l];
                    this.A[k][l] = this.A[index][l];
                    this.A[index][l] = t;
                }
                debugADump();
            }
        }

        if (enable) {
            double[][] M = identityMatrix(), rM = identityMatrix();
            for (int j = 0; j < n; j++) {
                if (j == k) {
                    M[k][j] = 1. / this.A[k + 1][k];
                } else {
                    M[k][j] = -this.A[k + 1][j] / this.A[k + 1][k];
                }
                rM[k][j] = this.A[k + 1][j];
            }
            this.A = multiplyMatrix(rM, this.A);
            this.A = multiplyMatrix(this.A, M);
        }
        debugAdd("После выполнения шага получаем следующую матрицу:\n");
        debugADump();
        return enable;
    }

    private void makeK() {
        int k = this.n - 2;
        debugAdd("Начинаем построение матрицы Фробениуса (количество итераций: " + (k + 1) + ").\n");
        boolean cont = true;
        for (; k >= 0; k--) {
            cont = this.step(k);
            if (!cont) {
                debugAdd("Процесс построения матрицы Фробениуса прерван.\n");
                break;
            }
        }
        debugAdd("Процесс построения матрицы Фробениуса закончен. \n");
        if (cont) {
            this.LambdaK = this.makeLambda(A);
        }
    }

    private double[][] identityMatrix() {
        double[][] I = new double[n][n];
        for (int k = 0; k < n; k++)
            I[k][k] = 1;
        return I;
    }

    private double[][] multiplyMatrix(double[][] X, double[][] Y) {
        double[][] Z = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Z[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    Z[i][j] += X[i][k] * Y[k][j];
                    Z[i][j] %= 2;
                }
            }
        }
        return Z;
    }

    public String formatTex(boolean reverse) {
        String tex = "";
        int kn = LambdaK.length;
        if (reverse) {
            for (int i = kn - 1; i >= 0; i--) {
                if (i < kn - 1 && LambdaK[i] != 0) tex += "+";
                if (LambdaK[i] != 0) {
                    double abs = Math.abs(LambdaK[i]);
                    tex += (abs == 1 && i > 0 ? "" : String.format("%.0f", abs)) + (i > 0 ? "X" + (i != 1 ? "^" + i : "") : "");
                }
            }
        } else {
            for (int i = 0; i < kn; i++) {
                if (i > 0 && LambdaK[i] != 0) tex += " + ";
                if (LambdaK[i] != 0) {
                    double abs = Math.abs(LambdaK[i]);
                    tex += (abs == 1 && i > 0 ? "" : String.format("%.0f", abs)) + (i > 0 ? "X" +
                            (i != 1 ? "^" + i : "") : "");
                }
            }
        }
        return tex;
    }

    private double[] multiplyPol(double[] P, double[] Q) {
        debugAdd("Находим произведение полиномов ");
        debugDump(P);
        debugAdd(" * ");
        debugDump(Q);
        debugAdd(" = ");
        int size = P.length + Q.length - 1;
        double[] S = new double[size];
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < Q.length; j++) {
                S[i + j] += P[i] * Q[j];
                S[i+j]%=2; //----------------------------------------------------------
            }
        }
        debugDump(S);
        debugAdd("\n");
        return S;
    }

    private double[] makeLambda(double[][] M) {
        int size = M.length + 1;
        int n = M.length;
        double[] LM = new double[size];
        int k = M.length % 2 == 0 ? 1 : -1;
        for (int i = n; i > 0; i--) {
            LM[n - i] = -k * M[0][i - 1];
        }
        LM[n] = k;
        return LM;
    }

    public double[][] getA() {
        return this.A;
    }

    public double[] getKLambda() {
        return this.LambdaK;
    }

    public String toString() {
        return formatTex(false);
    }

    public String getDebug() {
        return debugLog + "\n";
    }
}
