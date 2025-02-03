package avancada.application.reconciliacao;

import java.util.ArrayList;
import java.util.List;

public class Rec {
    private List<Double> reconciliacao = new ArrayList<>();

    interface S { public void segunda();}
    interface T { public void segunda();}
    interface Q extends S, T { public void segunda();}
    public void recDados(double[] dados){

        double[] y = dados;

        //double[] v = new double[] { 0.089, 0.349, 0.322, 0.283, 0.103, 0.110, 0.223, 0.174, 0.167, 0.208, 0};

        // Matriz V para a rota 2
        double[] v = new double[] { 0.095, 0.115, 0.208, 0.100,	0.294, 0.113, 0.262, 0.197, 0.280, 0.192, 0};

        double[][] A = new double[][] { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1 }};

        System.out.println("Y_hat:");
        Reconciliation rec = new Reconciliation(y, v, A);
        rec.printMatrix(rec.getReconciledFlow());
    }
}
