package com.github.rccookie.math.solve;

import java.util.Arrays;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

public final class LinearEquationSystem {

    private final int unknowns;
    private final Number[][] rows;


    public LinearEquationSystem(int unknowns, Number[]... rows) {
        if(Arguments.checkNull(rows, "rows").length == 0)
            throw new IllegalArgumentException("At least one row expected");
        this.rows = new Number[rows.length][];
        for(int i=0; i<rows.length; i++) {
            this.rows[i] = Arguments.deepCheckNull(rows[i], "rows["+i+"]").clone();
            if(rows[i].length != rows[1].length) throw new IllegalArgumentException("Rows have different number of elements");
        }
        if(rows[0].length == 0) throw new IllegalArgumentException("Rows cannot be empty");
        this.unknowns = Arguments.checkRange(unknowns, 1, rows[0].length + 1);
    }

    private LinearEquationSystem(boolean ignored, int unknowns, Number[][] rows) {
        this.unknowns = unknowns;
        this.rows = new Number[rows.length][];
        Arrays.setAll(this.rows, i -> rows[i].clone());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for(Number[] row : rows) {
            if(str.length() != 1)
                str.append(",");
            str.append("[");
            for(int i=0; i<unknowns; i++) {
                if(i != 0) str.append(", ");
                str.append(row[i]);
            }
            str.append(" | ");
            for(int i=unknowns; i<row.length; i++) {
                if(i != unknowns) str.append(", ");
                str.append(row[i]);
            }
            str.append("]");
        }
        return str.append("]").toString();
    }

    public String toMatrixString() {
        if(rows.length == 1) {
            StringBuilder str = new StringBuilder("[");
            for(int j=0; j<unknowns; j++) {
                if(j != 0) str.append(", ");
                str.append(rows[0][j]);
            }
            return str.append("]").toString();
        }

        StringBuilder[] rows = new StringBuilder[this.rows.length];
        for(int i=0; i<rows.length; i++) {
            if(i == 0) rows[i] = new StringBuilder("\u250C ");
            else if(i == rows.length-1) rows[i] = new StringBuilder("\u2514 ");
            else rows[i] = new StringBuilder("\u2502 ");
        }

        for(int j=0; j<this.rows[0].length; j++) {
            String[] column = new String[rows.length];
            for(int i=0; i<rows.length; i++)
                column[i] = this.rows[i][j].toString();
            int width = Arrays.stream(column).mapToInt(String::length).max().getAsInt();

            for(int i=0; i<rows.length; i++) {
                String prefix = j == 0 ? "" : j == unknowns ? (i == 0 ? " \u2577 " : i == rows.length-1 ? " \u2575 " : " \u2502 ") : " ";
                rows[i].append(prefix)
                        .append(" ".repeat((width - column[i].length() + 1) / 2))
                        .append(column[i])
                        .append(" ".repeat((width - column[i].length()) / 2));
            }
        }

        for(int i=0; i<rows.length; i++) {
            if(i == 0) rows[i].append(" \u2510");
            else if(i == rows.length-1) rows[i].append(" \u2518");
            else rows[i].append(" \u2502");
        }

        return String.join("\n", rows);
    }

    public int rowCount() {
        return rows.length;
    }

    public int columnCount() {
        return rows[0].length;
    }

    public int unknownCount() {
        return unknowns;
    }

    public Number get(int i, int j) {
        return rows[i][j];
    }

    public Number[] getRow(int i) {
        return rows[i].clone();
    }

    public Number[] getColumn(int j) {
        Number[] column = new Number[rows.length];
        for(int i=0; i<column.length; i++)
            column[i] = rows[i][j];
        return column;
    }

    public boolean isHomogenous() {
        return rows[0].length == unknowns;
    }

    public boolean isEchelonForm() {
        int left = -1;
        for(Number[] row : rows) {
            int j = 0;
            for(; j<=Math.min(left, unknowns); j++)
                if(!row[j].isZero()) return false;
            while(j < unknowns && row[j].isZero()) j++;
            left = j;
        }
        return true;
    }

    public boolean isReducedEchelonForm() {
        return isReducedEchelonForm(true);
    }

    private boolean isReducedEchelonForm(boolean checkEchelon) {
        if(checkEchelon && !isEchelonForm()) return false;
        for(int i=0; i<rows.length; i++) {
            int j = 0;
            while(j < unknowns && rows[i][j].isZero()) j++;
            if(j == unknowns) break;
            if(!rows[i][j].isOne()) return false;
            for(int i2=0; i2<rows.length; i2++)
                if(i != i2 && !rows[i2][j].isZero()) return false;
        }
        return true;
    }


    public LinearEquationSystem multiply(int row, Number factor) {
        LinearEquationSystem out = copy();
        for(int j=0; j<rows[0].length; j++)
            out.rows[row][j] = rows[row][j].multiply(factor);
        return out;
    }

    public LinearEquationSystem multiplyAdd(int row, int toRow, Number factor) {
        LinearEquationSystem out = copy();
        for(int j=0; j<rows[0].length; j++)
            out.rows[toRow][j] = rows[toRow][j].add(rows[row][j].multiply(factor));
        return out;
    }

    public LinearEquationSystem swap(int row1, int row2) {
        if(Arguments.checkRange(row1, 0, rows.length) == Arguments.checkRange(row2, 0, rows.length))
            return this;
        LinearEquationSystem out = copy();
        out.swapInplace(row1, row2);
        return out;
    }

    private void swapInplace(int row1, int row2) {
        if(row1 == row2) return;
        Number[] tmp = rows[row1];
        rows[row1] = rows[row2];
        rows[row2] = tmp;
    }

    public LinearEquationSystem toEchelonForm() {
        if(isEchelonForm()) return this;

        LinearEquationSystem out = copy();
        int startI = 0;
        for(int j=0; j<unknowns; j++) {

            // Move rows with zeros to bottom
            int endI = rows.length;
            for(int i=startI; i<endI; i++)
                if(out.rows[i][j].isZero())
                    out.swapInplace(i--, --endI);

            // All relevant rows have zeros in this column?
            if(endI == startI) continue;

            // Find row with the greatest coefficient in column and move to top, for stability purposes
            int max = startI;
            for(int i=startI+1; i<endI; i++)
                if(out.rows[i][j].greaterThan(out.rows[i][j]).isOne())
                    max = i;
            out.swapInplace(startI, max);

            // Subtract selected row from all rows below to produce zeros in this column
            for(int i=startI+1; i<endI; i++) {
                Number factor = out.rows[i][j].divide(out.rows[startI][j]);
                out.rows[i][j] = Number.ZERO();
                for(int j2=j+1; j2<rows[0].length; j2++)
                    out.rows[i][j2] = out.rows[i][j2].subtract(out.rows[startI][j2].multiply(factor));
            }

            // The row added to the others is irrelevant now
            startI++;
        }
        return out;
    }

    public LinearEquationSystem toReducedEchelonForm() {
        LinearEquationSystem echelon = toEchelonForm();
        if(echelon.isReducedEchelonForm(false)) return echelon;

        LinearEquationSystem out = toEchelonForm().copy();

        for(int i=rows.length-1; i>=0; i--) {

            // Find non-zero start of row
            int j = 0;
            while(j < unknowns && out.rows[i][j].isZero()) j++;

            // Row only has zeros
            if(j == unknowns) continue;

            // Subtract row from each row above to eliminate factors in row
            for(int i2=i-1; i2>=0; i2--) {
                Number factor = out.rows[i2][j].divide(out.rows[i][j]);
                out.rows[i2][j] = Number.ZERO();
                for(int j2=unknowns; j2<rows[0].length; j2++)
                    out.rows[i2][j2] = out.rows[i2][j2].subtract(out.rows[i][j2].multiply(factor));
            }

            // Normalize row
            for(int j2=unknowns; j2<rows[0].length; j2++)
                out.rows[i][j2] = out.rows[i][j2].divide(out.rows[i][j]);
            out.rows[i][j] = Number.ONE();
        }

        return out;
    }

    public Number[][] solve() {
        LinearEquationSystem s = toReducedEchelonForm();

        Number[][] solutions = new Number[Math.max(1, rows[0].length - unknowns)][rows.length];
        for(int i=0; i<rows.length; i++) {
            boolean zero = true;
            for(int j=0; j<unknowns; j++) {
                if(!s.rows[i][j].isZero()) {
                    zero = false;
                    break;
                }
            }
            if(zero) return solutions; // All lower rows are zeros too, so those variables are all unbound

            if(isHomogenous())
                solutions[0][i] = Number.ZERO();
            else for(int j=0; j<solutions.length; j++)
                solutions[j][i] = s.rows[i][j+unknowns];
        }
        return solutions;
    }


    private LinearEquationSystem copy() {
        return new LinearEquationSystem(true, unknowns, rows);
    }


    public static void main(String[] args) {
        long[][] lRows = {
                { 1, 2, 3, 10 },
                { 4, 5, 6, 20 },
                { 7, 8, 10, 30 }
        };
        Number[][] rows = new Number[lRows.length][lRows[0].length];
        for(int i=0; i<rows.length; i++) for(int j=0; j<rows[0].length; j++)
            rows[i][j] = new Rational(lRows[i][j]);
        LinearEquationSystem lgs = new LinearEquationSystem(3, rows);
        Console.log(lgs.toMatrixString());
        Console.log(lgs.isEchelonForm(), lgs.isReducedEchelonForm());

        LinearEquationSystem zsf = lgs.toEchelonForm();
        Console.log(zsf.toMatrixString());
        Console.log(zsf.isEchelonForm(), zsf.isReducedEchelonForm());

        LinearEquationSystem rzsf = zsf.toReducedEchelonForm();
        Console.log(rzsf.toMatrixString());
        Console.log(rzsf.isEchelonForm(), rzsf.isReducedEchelonForm());

        Console.log((Object) rzsf.solve());
    }
}
