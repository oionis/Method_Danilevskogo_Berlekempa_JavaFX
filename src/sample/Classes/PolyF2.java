package sample.Classes;

import java.util.ArrayList;

public class PolyF2 {

    public ArrayList<Integer> coefficients = new ArrayList<>();
    String debug = "";

    private void debugAdd(String msg) {
        debug += msg;
    }

    public PolyF2() {
        coefficients.add(0);
    }

    public PolyF2(final ArrayList<Integer> co) {

        for (int x : co) {
            coefficients.add(x & 1);
        }

        shrink_degree();
    }

    public PolyF2(PolyF2 co) {

        for (int x : co.coefficients) {
            coefficients.add(x & 1);
        }
        shrink_degree();
    }

    public void shrink_degree() {
        while (coefficients.size() > 1 && coefficients.get(coefficients.size() - 1) == 0) {
            coefficients.remove(coefficients.size() - 1);
        }
        if (coefficients.size() == 0) {
            coefficients.add(0);
        }
    }

    public int get_degree() {
        return coefficients.size() - 1;
    }

    public boolean is_zero() {
        return get_degree() == 0 && coefficients.get(0) == 0;
    }

    public boolean equalsTo(PolyF2 rhs) {
        if (get_degree() != rhs.get_degree()) {
            return false;
        }
        for (int i = 0; i < coefficients.size(); ++i) {
            if (coefficients.get(i) != rhs.coefficients.get(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean notEqualsTo(PolyF2 rhs, PolyF2 polynom) {
        return !(polynom.equalsTo(rhs));
    }

    public PolyF2 add(PolyF2 rhs) {
        PolyF2 p = new PolyF2(this);
        if (rhs.get_degree() > p.get_degree()) {
            int i = 0;
            while (i < p.coefficients.size()) {
                p.coefficients.set(i, p.coefficients.get(i) ^ rhs.coefficients.get(i));


                ++i;
            }
            while (i < rhs.coefficients.size()) {
                p.coefficients.add(rhs.coefficients.get(i));
                ++i;
            }
        } else {
            int i = 0;
            while (i < rhs.coefficients.size()) {
                p.coefficients.set(i, p.coefficients.get(i) ^ rhs.coefficients.get(i));
                ++i;
            }
        }
        p.shrink_degree();
        return p;
    }

    public PolyF2 subtract(PolyF2 rhs) {
        return this.add(rhs);
    }

    public PolyF2 multiply(PolyF2 rhs, PolyF2 polynom) {


        PolyF2 p = new PolyF2();
        VectorHelper.resize(p.coefficients, polynom.get_degree() + rhs.get_degree() + 1);
        for (int i = 0; i < polynom.coefficients.size(); ++i) {
            for (int j = 0; j < rhs.coefficients.size(); ++j) {
                if (p.coefficients.get(i + j) == null) {
                    p.coefficients.set(i + j, 0);
                }
                int temp = p.coefficients.get(i + j) ^ (polynom.coefficients.get(i) * rhs.coefficients.get(j));
                p.coefficients.set(i + j, temp);
            }
        }
        p.shrink_degree();
        return p;
    }

    public final Pair<PolyF2, PolyF2> divmod(PolyF2 rhs, final PolyF2 p) {

        PolyF2 quot = new PolyF2();
        PolyF2 rem = new PolyF2();
        if (rhs.is_zero()) {
            return new Pair<PolyF2, PolyF2>(quot, rem);
        }
        if (p.get_degree() < rhs.get_degree()) {
            return new Pair<PolyF2, PolyF2>(quot, p);
        }
        VectorHelper.resize(quot.coefficients, p.get_degree() - rhs.get_degree() + 1);

        copyFrom(rem, p);
        for (int k = p.get_degree() - rhs.get_degree(); k >= 0; --k) {
            quot.coefficients.set(k, rem.coefficients.get(k + rhs.get_degree()));
            for (int j = k + rhs.get_degree() - 1; j >= k; --j) {
                rem.coefficients.set(j, (rem.coefficients.get(j) ^ (quot.coefficients.get(k) * rhs.coefficients.get(j - k))));
            }
        }


        if (rhs.get_degree() == 0) {
            VectorHelper.resize(rem.coefficients, 1);
            rem.coefficients.set(0, 0);
        } else {
            VectorHelper.resize(rem.coefficients, rhs.get_degree());
        }
        quot.shrink_degree();
        rem.shrink_degree();
        return new Pair<PolyF2, PolyF2>(quot, rem);
    }

    public static void copyFrom(PolyF2 whereCopy, PolyF2 whatCopy) {
        if (whereCopy.coefficients.size() < whatCopy.coefficients.size()) {
            int n = whatCopy.coefficients.size() - whereCopy.coefficients.size();
            for (int i = 0; i < n; i++) {
                whereCopy.coefficients.add(0);
            }
        }
        if (whereCopy.coefficients.size() > whatCopy.coefficients.size()) {
            int n = whereCopy.coefficients.size() - whatCopy.coefficients.size();
            for (int i = 0; i < n; i++) {
                whereCopy.coefficients.remove(whereCopy.coefficients.size() - 1);
            }
        }
        for (int i = 0; i < whatCopy.coefficients.size(); i++) {
            whereCopy.coefficients.set(i, whatCopy.coefficients.get(i));
        }
    }

    public PolyF2 divide(PolyF2 rhs, PolyF2 p) {
        Pair<PolyF2, PolyF2> pair = divmod(rhs, p);
        return pair.first;
    }

    public PolyF2 modulus(PolyF2 rhs, PolyF2 aa) {  //-------------- % ----------
        Pair<PolyF2, PolyF2> pair = divmod(rhs, aa);
        return pair.second;
    }

    public final PolyF2 derivative(PolyF2 polynom) {
        PolyF2 p = new PolyF2(polynom);
        for (int i = 0; i < get_degree(); ++i) {
            p.coefficients.set(i, ((i + 1) * polynom.coefficients.get(i + 1)) & 1);
        }
        p.coefficients.remove(p.coefficients.size() - 1);
        p.shrink_degree();
        return p;
    }

    public final PolyF2 gcd(PolyF2 rhs, PolyF2 p) {
        PolyF2 aa = new PolyF2();
        copyFrom(aa, p);
        PolyF2 bb = new PolyF2();
        copyFrom(bb, rhs);
        while (!bb.is_zero()) {
            PolyF2 tmp = aa.modulus(bb, aa);
            copyFrom(aa, bb);
            copyFrom(bb, tmp);
        }
        return aa;
    }

    public final PolyF2 extract_even_terms() {
        ArrayList<Integer> coeffs = new ArrayList<>();
        for (int i = 0; i < coefficients.size(); i += 2) {
            coeffs.add(coefficients.get(i));
        }
        PolyF2 p = new PolyF2(coeffs);
        p.shrink_degree();
        return p;
    }

    public final ArrayList<Integer> get_coefficients(int number) {
        ArrayList<Integer> ret = new ArrayList<>();
        int num;
        int n;

        if (number == -1) {
            num = coefficients.size();
            n = coefficients.size();
        } else {
            num = number;
            n = Math.min(num, coefficients.size());
        }
        for (int i = 0; i < n; ++i) {
            ret.add(coefficients.get(i));
        }
        for (int i = n; i < num; ++i) {
            ret.add(0);
        }
        return ret;
    }

    public String getDebug() {
        return debug + "\n";
    }

}
