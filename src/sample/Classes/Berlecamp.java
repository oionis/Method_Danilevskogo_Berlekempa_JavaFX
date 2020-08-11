package sample.Classes;

import java.util.ArrayList;
import java.util.Arrays;

public class Berlecamp {

    public static String printResault(PolyF2 p) {

        String tempStr= "";

        ArrayList<Integer> r = p.get_coefficients(-1);
        //System.out.print("PolyF2: ");
        if (p.is_zero()) {
            tempStr+="0";
           // System.out.print("0");
        } else {
            ArrayList<Integer> powers = new ArrayList<>();
            for (int i = r.size(); i > 0; --i) {
                if (r.get(i - 1) == 1) {
                    powers.add(i - 1);
                }
            }
            if (powers.size() > 1) {
                for (int i = 0; i < powers.size() - 1; ++i) {

                    tempStr+="X^" + powers.get(i) + " + ";
                   // System.out.print("X^" + powers.get(i) + " + ");
                }
            }
            if (powers.get(powers.size() - 1) > 0) {
                tempStr+="X^" + powers.get(powers.size() - 1);
               // System.out.print("X^" + powers.get(powers.size() - 1));
            } else {
                tempStr+="1";
               // System.out.print("1");
            }
        }
        return tempStr;
    }

    public static ArrayList<PolyF2> factor(PolyF2 p) {

        if (p.is_zero() || p.equalsTo(new PolyF2(new ArrayList<Integer>(Arrays.asList(1))))) {
            return new ArrayList<PolyF2>(Arrays.asList(p));
        }

        PolyF2 pdiv = new PolyF2();
        PolyF2.copyFrom(pdiv, p.derivative(p));

        if (pdiv.is_zero()) {
            ArrayList<PolyF2> factors = factor(p.extract_even_terms());
            int len = factors.size();
            for (int i = 0; i < len; ++i) {
                factors.add(factors.get(i));
            }
            return factors;
        }

        PolyF2 d = new PolyF2();
        PolyF2.copyFrom(d, p.gcd(pdiv, p));

        if (d.equalsTo(new PolyF2(new ArrayList<Integer>(Arrays.asList(new Integer[]{1}))))) {
            return berlekamp(p);
        }

        ArrayList<PolyF2> factors = factor(d);
        ArrayList<PolyF2> factors1 = factor(p.divide(d, p));
        for (PolyF2 x : factors1) {
            factors.add(x);
        }

        return factors;
    }

    public static SquareMatrixF2 berlekamp_Q_matrix(PolyF2 u) {
        int num_coeffs = u.get_degree();
        SquareMatrixF2 Q = new SquareMatrixF2(num_coeffs);
        PolyF2 p = new PolyF2(new ArrayList<Integer>(Arrays.asList(1)));
        ArrayList<Integer> coeffs = p.get_coefficients(num_coeffs);
        for (int i = 0; i < num_coeffs; ++i) {
            Q.set(0, i, coeffs.get(i));
        }
        for (int k = 1; k < num_coeffs; ++k) {

            PolyF2.copyFrom(p, p.multiply(new PolyF2(new ArrayList<Integer>(Arrays.asList(0, 0, 1))), p));
            PolyF2.copyFrom(p, p.modulus(u, p));

            coeffs = p.get_coefficients(num_coeffs);
            for (int i = 0; i < num_coeffs; ++i) {
                Q.set(k, i, coeffs.get(i));
            }
        }
        return Q;
    }

    public static ArrayList<ArrayList<Integer>> berlekamp_null(SquareMatrixF2 Q) {
        SquareMatrixF2 A = Q.subtract(SquareMatrixF2.identity(Q.get_size())).get_transpose();

        int identity_index = 0;
        ArrayList<Integer> copy_id = new ArrayList<Integer>();
        for (int colindex = 0; colindex < Q.get_size(); ++colindex) {

            int rowindex = identity_index;
            while (rowindex < Q.get_size() && A.get(rowindex, colindex) == 0) {
                rowindex += 1;
            }
            if (rowindex == Q.get_size()) {
                continue;
            }

            A.swap_rows(rowindex, identity_index);
            for (int row = 0; row < Q.get_size(); ++row) {
                if (row != identity_index && A.get(row, colindex) == 1) {
                    A.add_rows(row, identity_index);
                }
            }

            copy_id.add(colindex);
            ++identity_index;
        }

        ArrayList<ArrayList<Integer>> basis = new ArrayList<ArrayList<Integer>>();
        for (int k = 0; k < Q.get_size(); ++k) {
            if (copy_id.contains(k)) {
                continue;
            }
            ArrayList<Integer> vec = new ArrayList<Integer>(Q.get_size());
            for (int i = 0; i < Q.get_size(); i++) {
                vec.add(0);
            }
            vec.set(k, 1);
            for (int i = 0; i < copy_id.size(); ++i) {
                vec.set(copy_id.get(i), A.get((int) i, k));
            }
            basis.add(vec);
        }
        return basis;
    }


    public static ArrayList<PolyF2> berlekamp(PolyF2 u) {
        if (u.get_degree() <= 1) {
            return new ArrayList<PolyF2>(Arrays.asList(u));
        }
        SquareMatrixF2 Q = berlekamp_Q_matrix(u);
        ArrayList<ArrayList<Integer>> basis = berlekamp_null(Q);
        ArrayList<PolyF2> nullspace = new ArrayList<PolyF2>();
        for (ArrayList<Integer> vec : basis) {
            nullspace.add(new PolyF2(vec));
        }

        ArrayList<PolyF2> factors = new ArrayList<PolyF2>(Arrays.asList(u));
        int k = 1;
        while (factors.size() < nullspace.size()) {
            ArrayList<PolyF2> newfactors = new ArrayList<PolyF2>();
            for (int s = 0; s < 2; s++) {
                for (PolyF2 w : factors) {


                    PolyF2 ww = new PolyF2();
                    PolyF2.copyFrom(ww, w.gcd(nullspace.get(k).subtract(new PolyF2(new ArrayList<Integer>(Arrays.asList(s)))), w));

                    if (ww.notEqualsTo(new PolyF2(new ArrayList<Integer>(Arrays.asList(new Integer[]{1}))), ww)) {
                        newfactors.add(ww);
                    }
                }
            }
            factors = new ArrayList<PolyF2>(newfactors);
            k += 1;
        }
        return factors;
    }

}