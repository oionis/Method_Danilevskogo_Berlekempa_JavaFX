package sample.Classes;
import java.util.*;

public class SquareMatrixF2 {

    private int size;
    private ArrayList<Integer> entries = new ArrayList<>();

    public SquareMatrixF2(int si) {
        this.size = si;

        for (int i = 0; i < si * si; i++) {
            entries.add(0);
        }

    }

    public void set(int row, int column, int value) {
        entries.set(column + size * row, value & 1);
    }

    public final Integer get(int row, int column) {
        return entries.get(column + size * row);
    }

    public final int get_size() {
        return size;
    }

    public static SquareMatrixF2 identity(int size) {
        SquareMatrixF2 m = new SquareMatrixF2(size);
        for (int i = 0; i < size; ++i) {
            m.set(i, i, 1);
        }
        return m;
    }

    public SquareMatrixF2 add(SquareMatrixF2 rhs) {
        int si = Math.min(get_size(), rhs.get_size());
        SquareMatrixF2 m = new SquareMatrixF2(si);
        for (int row = 0; row < si; ++row) {
            for (int col = 0; col < si; ++col) {
                m.set(row, col, get(row, col) ^ rhs.get(row, col));
            }
        }
        return m;
    }

    public SquareMatrixF2 subtract(SquareMatrixF2 rhs) {
        return this.add(rhs);
    }

    public final SquareMatrixF2 get_transpose() {
        SquareMatrixF2 m = new SquareMatrixF2(get_size());
        for (int row = 0; row < get_size(); ++row) {
            for (int col = 0; col < get_size(); ++col) {
                m.set(col, row, get(row, col));
            }
        }
        return m;
    }

    public final void swap_rows(int row1, int row2) {
        for (int col = 0; col < get_size(); ++col) {
            Integer a = get(row1, col);
            Integer b = get(row2, col);
            set(row1, col, b);
            set(row2, col, a);
        }
    }

    public final void add_rows(int addto, int addfrom) {
        for (int col = 0; col < get_size(); ++col) {
            set(addto, col, get(addto, col) ^ get(addfrom, col));
        }
    }
}
