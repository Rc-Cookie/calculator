package com.github.rccookie.math;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.json.JsonArray;
import com.github.rccookie.math.expr.MathEvaluationException;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.math.expr.UnsupportedMathOperationException;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class Matrix implements Number {

    private final Number[][] rows;

    private Matrix(Number[][] rows) {
        this.rows = rows;
    }

    private Matrix(Matrix src, UnaryOperator<Number> operation) {
        this.rows = new Number[src.rows.length][src.rows[0].length];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                rows[i][j] = operation.apply(src.rows[i][j]);
    }

    private Matrix(Matrix src, MatrixOperation operation) {
        this.rows = new Number[src.rows.length][src.rows[0].length];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                rows[i][j] = operation.apply(i, j, src.rows[i][j]);
    }

    private Matrix(Matrix a, Matrix b, BinaryOperator<Number> operation) {
        this.rows = new Number[Math.max(a.rows.length, b.rows.length)][Math.max(a.rows[0].length, b.rows[0].length)];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                rows[i][j] = operation.apply(b.getIndex(i,j), a.getIndex(i,j));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Matrix m && Arrays.deepEquals(rows, m.rows)) ||
               (obj instanceof Number n && isScalar() && n.equals(rows[0][0]));
    }

    @Override
    public int hashCode() {
        return isScalar() ? rows[0][0].hashCode() : Arrays.deepHashCode(rows);
    }

    @Override
    public String toString() {
        if(isScalar())
            return rows[0][0].toString();
        StringBuilder str = new StringBuilder("[");
        for(Number[] row : rows) {
            str.append(row[0]);
            for(int i=1; i<row.length; i++)
                str.append(',').append(row[i]);
            str.append(';');
            if(row.length != 1)
                str.append(' ');
        }
        str.delete(str.length() - 2, str.length());
        return str.append(']').toString();
    }

    @Override
    public double toDouble() {
        if(isScalar())
            return rows[0][0].toDouble();
        throw new UnsupportedOperationException("Cannot convert multi-element matrix to double");
    }

    @Override
    public double toDouble(SymbolLookup lookup) {
        return 0;
    }

    @Override
    public Object toJson() {
        return new JsonArray((Object[]) rows);
    }

    public Matrix.Size size() {
        return new Size(rows.length, rows[0].length);
    }

    public int vectorSize() {
        if(rows.length == 1) return rows[0].length;
        if(rows[0].length == 1) return rows.length;
        throw new MathEvaluationException("Matrix is not a row or column vector, cannot receive vector size");
    }

    public int rowCount() {
        return rows.length;
    }

    public int columnCount() {
        return rows[0].length;
    }

    public int rowSize() {
        return rows[0].length;
    }

    public int columnSize() {
        return rows.length;
    }

    public int elementCount() {
        return rows.length * rows[0].length;
    }

    public boolean isQuadratic() {
        return rows.length == rows[0].length;
    }

    public boolean isVector() {
        return rows.length == 1 || rows[0].length == 1;
    }

    public boolean isRowVector() {
        return rows.length == 1;
    }

    public boolean isColumnVector() {
        return rows[0].length == 1;
    }

    public boolean isScalar() {
        return rows.length == 1 && rows[0].length == 1;
    }

    @Override
    public boolean isZero() {
        for(Number[] row : rows)
            for(Number n : row)
                if(!n.isZero()) return false;
        return true;
    }

    @Override
    public boolean isOne() {
        for(Number[] row : rows)
            for(Number n : row)
                if(!n.isOne()) return false;
        return true;
    }

    public Number getIndex(int rowIndex, int columnIndex) {
        if(rowIndex < 0)
            throw new MathEvaluationException("Matrix row out of lower bounds");
        if(columnIndex < 0)
            throw new MathEvaluationException("Matrix column out of lower bounds");
        if(rowIndex < rows.length && columnIndex < rows[0].length)
            return rows[rowIndex][columnIndex];
        return Number.ZERO();
    }

    public Number getIndex(int vectorIndex) {
        if(rows.length == 1)
            return vectorIndex < rows[0].length ? rows[0][vectorIndex] : Number.ZERO();
        if(rows[0].length == 0)
            return vectorIndex < rows.length ? rows[vectorIndex][0] : Number.ZERO();
        throw new MathEvaluationException("Matrix is not a row or column vector, row and column required");
    }

    public Number get(int row, int column) {
        return getIndex(row-1, column-1);
    }

    public Number get(int vectorElement) {
        return getIndex(vectorElement-1);
    }

    public Number get(Number row, Number column) {
        if(row instanceof Matrix rows)
            return rows.derive(r -> get(r, column));
        if(column instanceof Matrix columns)
            return columns.derive(c -> get(row, c));
        if(!(row instanceof SimpleNumber r && column instanceof SimpleNumber c))
            throw new UnsupportedMathOperationException("get", row, column);
        return get((int) r.toDouble(), (int) c.toDouble());
    }

    public Number get(Number vectorElement) {
        return switch(vectorElement) {
            case Matrix indices -> indices.derive(this::get);
            case SimpleNumber i -> get((int) i.toDouble());
            default -> throw new UnsupportedMathOperationException("get", vectorElement);
        };
    }

    public Number x() {
        return getIndex(0);
    }

    public Number y() {
        return getIndex(1);
    }

    public Number z() {
        return getIndex(2);
    }

    public Number a11() {
        return get(1,1);
    }

    public Number a12() {
        return get(1,2);
    }

    public Number a13() {
        return get(1,3);
    }

    public Number a21() {
        return get(2,1);
    }

    public Number a22() {
        return get(2,2);
    }

    public Number a23() {
        return get(2,3);
    }

    public Number a31() {
        return get(3,1);
    }

    public Number a32() {
        return get(3,2);
    }

    public Number a33() {
        return get(3,3);
    }

    public Number[][] rows() {
        Number[][] rows = new Number[this.rows.length][];
        for(int i=0; i<this.rows[0].length; i++)
            rows[i] = this.rows[i].clone();
        return rows;
    }

    public Number[][] columns() {
        Number[][] columns = new Number[this.rows[0].length][this.rows.length];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                columns[j][i] = rows[i][j];
        return columns;
    }

    public Number[] elements() {
        if(rows.length == 1)
            return rows[0].clone();
        Number[] elements = new Number[rows.length * rows[0].length];
        for(int i=0; i<rows.length; i++)
            System.arraycopy(rows[i], 0, elements, i*rows[0].length, rows[0].length);
        return elements;
    }

    public Number determinant() {
        if(!isQuadratic())
            throw new ArithmeticException("Non-square matrix does not have a determinant");
        if(rows.length == 1)
            return rows[0][0];
        if(rows.length == 2)
            return a11().multiply(a22()).subtract(a12().multiply(a21()));
        if(rows.length == 3) {
            Number a = a11(), b = a12(), c = a13(),
                   d = a21(), e = a22(), f = a23(),
                   g = a31(), h = a32(), i = a33();
            return a.multiply(e).multiply(i)
                    .add(b.multiply(f).multiply(g))
                    .add(c.multiply(d).multiply(h))
                    .subtract(c.multiply(e).multiply(g))
                    .subtract(b.multiply(d).multiply(i))
                    .subtract(a.multiply(f).multiply(h));
        }
        return null; // TODO
    }

    @Override
    public @NotNull Number abs() {
        if(rows.length == 1) {
            if(rows[0].length == 1)
                return rows[0][0];
            Number sum = rows[0][0].multiply(rows[0][0]);
            for(int i=1; i<rows[0].length; i++)
                sum = sum.add(rows[0][i].multiply(rows[0][i]));
            return sum.sqrt();
        }
        if(rows[0].length == 1) {
            Number sum = rows[0][0].multiply(rows[0][0]);
            for(int i=0; i<rows.length; i++)
                sum = sum.add(rows[i][0].multiply(rows[i][0]));
            return sum.sqrt();
        }
        return determinant();
    }

    @Override
    public @NotNull Number negate() {
        return null;
    }

    public @NotNull Matrix submatrixIndexed(int rowIndex, int columnIndex) {
        if(isVector())
            throw new ArithmeticException("Submatrix requires a 2x2 matrix or bigger");
        Arguments.checkRange(rowIndex, 0, rows.length);
        Arguments.checkRange(columnIndex, 0, rows[0].length);
        Number[][] sub = new Number[rows.length-1][rows[0].length-1];
        for(int i=0; i<sub.length; i++)
            for(int j=0; j<sub[0].length; j++)
                sub[i][j] = rows[i<rowIndex ? i : i+1][j<columnIndex ? j : j+1];
        return new Matrix(sub);
    }

    public @NotNull Matrix submatrix(int row, int column) {
        return submatrixIndexed(row-1, column-1);
    }

    public @NotNull Matrix minor() {
        if(!isQuadratic())
            throw new ArithmeticException("Only quadratic matrices have a minor");
        if(rows.length == 1)
            throw new ArithmeticException("1x1 matrix does not have a minor");
        if(rows.length == 2)
            return new2x2(a22(), a21(), a12(), a11());

        Number[][] minor = new Number[rows.length][rows.length];
        Number[][] subCache = new Number[rows.length][rows.length-1];
        for(int i=0; i<subCache.length; i++)
            System.arraycopy(rows[i], 1, subCache[i], 0, subCache[0].length);

        // Reuse matrix object by changing rows directly
        Matrix sub = new Matrix(new Number[minor.length-1][]);
        for(int j=0; j<minor.length; j++) {
            // Fill sub matrix with cache skipping the first row (i=0)
            System.arraycopy(subCache, 1, sub.rows, 0, sub.rows.length);
            for(int i=0; i<minor.length; i++) {
                minor[i][j] = sub.determinant();
                // Next time row i should not be skipped but i+1, which is currently at index i
                if(i != minor.length-1)
                    sub.rows[i] = subCache[i];
            }
            // Next time column j should not be skipped but j+1, which is currently at index j
            if(j != minor.length-1)
                for(int i=0; i<subCache.length; i++)
                    subCache[i][j] = rows[i][j];
        }
        return new Matrix(minor);
    }

    public @NotNull Matrix cofactor() {
        return minor().derive((i,j,x) -> ((i+j)&1) == 0 ? x : x.negate());
    }

    public @NotNull Matrix adjugate() {
        return cofactor().transpose();
    }

    @Override
    public @NotNull Matrix invert() {
        if(!isQuadratic()) {
            Matrix transposition = transpose();
            if(rows.length < rows[0].length)
                return transposition.multiply(multiply(transposition).invert()); // right inverse
            return transposition.multiply(this).invert().multiply(transposition); // left inverse
        }
        if(rows.length == 1)
            return new Matrix(new Number[][] {{ rows[0][0].invert() }});

        Number d = determinant();
        if(d.equals(Number.ZERO()))
            throw new ArithmeticException("Matrix has no inverse");
        d = d.invert();

        if(rows.length == 2)
            return new2x2(a22().multiply(d), a12().negate().multiply(d), a21().negate().multiply(d), a11().multiply(d));
//        if(rows.length == 3)
//            return adjugate().scale(d);
        return adjugate().scale(d);
    }

    @Override
    public @NotNull Number equalTo(Number x) {
        if(x instanceof Matrix m)
            return equalTo(m);
        return isScalar() ? rows[0][0].equalTo(x) : Number.FALSE();
    }

    public @NotNull Number equalTo(Matrix m) {
        if(isScalar()) return m.equalTo(rows[0][0]);
        if(m.isScalar()) return equalTo(m.rows[0][0]);
        boolean equal = true;
        for(int i=0; i<Math.max(rows.length, m.rows.length); i++) {
            for (int j = 0; j < Math.max(rows[0].length, m.rows[0].length); j++) {
                Number equality = getIndex(i,j).equalTo(m.getIndex(i,j));
                if(equality.equals(Number.FALSE()))
                    equal = false;
                else if(!equality.equals(Number.TRUE()))
                    return componentWiseEqualTo(m);
            }
        }
        return Number.bool(equal);
    }

    public @NotNull Matrix componentWiseEqualTo(Matrix m) {
        return derive(m, Number::equalTo);
    }

    @Override
    public @NotNull Matrix lessThan(Number x) {
        return x instanceof Matrix m ?
                derive(m, Number::lessThan) :
                derive(c -> c.lessThan(x));
    }

    @Override
    public @NotNull Matrix greaterThan(Number x) {
        return x instanceof Matrix m ?
                derive(m, Number::greaterThan) :
                derive(c -> c.greaterThan(x));
    }

    public @NotNull Matrix transpose() {
        return new Matrix(columns());
    }

    @Override
    public @NotNull Number add(Number x) {
        return x instanceof Matrix m ?
                derive(m, Number::add) :
                derive(c -> c.add(x));
    }

    @Override
    public @NotNull Number subtract(Number x) {
        return x instanceof Matrix m ?
                derive(m, Number::subtract):
                derive(c -> c.add(x));
    }

    @Override
    public @NotNull Number subtractFrom(Number x) {
        return x instanceof Matrix m ?
                derive(m, Number::subtractFrom):
                derive(c -> c.subtractFrom(x));
    }

    @Override
    public @NotNull Number multiply(Number x) {
        return x instanceof Matrix m ? multiply(m) : scale(x);
    }

    public @NotNull Matrix scale(Number x) {
        return derive(c -> c.multiply(x));
    }

    public @NotNull Matrix multiplyUnsafe(Matrix m) {
        Number[][] result = new Number[rows.length][m.rows[0].length];
        int sumSize = Math.min(rows[0].length, m.rows.length);
        for(int i=0; i<result.length; i++) {
            for(int j=0; j<result[0].length; j++) {
                Number ij = Number.ZERO();
                for(int k=0; k<sumSize; k++)
                    ij.add(rows[i][k].multiply(m.rows[k][j]));
                result[i][j] = ij;
            }
        }
        return new Matrix(result);
    }

    public @NotNull Matrix multiply(Matrix m) {
        if(rows[0].length != m.rows.length)
            throw new ArithmeticException("Trying to multiply non-matching matrices");
        return multiplyUnsafe(m);
    }

    public @NotNull Number dot(Matrix other) {
        int r = Math.min(rows.length, other.rows.length);
        int c = Math.min(rows[0].length, other.rows[0].length);
        Number result = Number.ZERO();
        for(int i=0; i<r; i++)
            for(int j=0; j<c; j++)
                result.add(rows[i][j].multiply(other.rows[i][j]));
        return result;
    }

    @Override
    public @NotNull Number divide(Number x) {
        return invert().multiply(x);
    }

    @Override
    public @NotNull Number divideOther(Number x) {
        return x.multiply(invert());
    }

    @Override
    public @NotNull Number raise(Number x) {
        return null;
    }

    @Override
    public @NotNull Number raiseOther(Number base) {
        return null;
    }

    public Matrix derive(UnaryOperator<Number> componentOperator) {
        return new Matrix(this, componentOperator);
    }

    public Matrix derive(MatrixOperation componentOperator) {
        return new Matrix(this, componentOperator);
    }

    public Matrix derive(Matrix other, BinaryOperator<Number> componentOperator) {
        return new Matrix(this, other, componentOperator);
    }

    public @NotNull Number reduce(BinaryOperator<Number> combiner) {
        Number result = rows[0][0];
        for(int i=1; i<rows[0].length; i++)
            result = combiner.apply(result, rows[0][i]);
        for(int i=1; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                result = combiner.apply(result, rows[i][j]);
        return result;
    }



    public static Matrix create(Matrix.Size size, Number... componentsRowByRow) {
        Arguments.checkNull(size, "size");
        Arguments.deepCheckNull(componentsRowByRow, "componentsRowByRow");
        if(size.elementCount() != componentsRowByRow.length)
            throw new IllegalArgumentException("Wrong number of components (expected "+size+"="+size.elementCount()+", got "+componentsRowByRow.length);
        Number[][] rows = new Number[size.rows][];
        for(int i=0; i<rows.length; i++)
            rows[i] = Arrays.copyOfRange(componentsRowByRow, i*size.rowSize(), (i+1)*size.rowSize());
        return new Matrix(rows);
    }

    public static Matrix create(int rows, int columns, Number... componentsRowByRow) {
        return create(new Size(rows, columns), componentsRowByRow);
    }

    public static Matrix fromRows(Number[]... rows) {
        Arguments.deepCheckNull(rows, "rows");
        if(rows.length == 0 || rows[0].length == 0)
            throw new IllegalArgumentException("Matrix cannot be empty");
        Number[][] copy = new Number[rows.length][];
        copy[0] = rows[0].clone();
        for(int i=1; i<rows.length; i++) {
            if(rows[i].length != rows[0].length)
                throw new IllegalArgumentException("Matrix rows must contain the same number of elements");
            copy[i] = rows[i].clone();
        }
        return new Matrix(copy);
    }

    public static Matrix fromColumns(Number[]... columns) {
        Arguments.deepCheckNull(columns, "columns");
        if(columns.length == 0 || columns[0].length == 0)
            throw new IllegalArgumentException("Matrix cannot be empty");
        for(int i=1; i<columns.length; i++)
            if(columns[i].length != columns[0].length)
                throw new IllegalArgumentException("Matrix columns must contains the same number of elements");
        Number[][] rows = new Number[columns[0].length][columns.length];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                rows[i][j] = columns[j][i];
        return new Matrix(rows);
    }

    public static Matrix fromVectors(Matrix[] rowsOrColumns) {
        Arguments.deepCheckNull(rowsOrColumns, "rowsOrColumns");
        if(rowsOrColumns.length == 0)
            throw new IllegalArgumentException("Matrix cannot be empty");
        if(!rowsOrColumns[0].isVector())
            throw new IllegalArgumentException("Row or column vectors expected, got matrix");

        Matrix.Size size = rowsOrColumns[0].size();
        for(int i=1; i<rowsOrColumns.length; i++)
            if(!rowsOrColumns[i].size().equals(size))
                throw new IllegalArgumentException("Either row or column vectors expected, got mix or matrix");

        if(rowsOrColumns[0].isRowVector()) {
            Number[][] rows = new Number[rowsOrColumns.length][];
            for(int i=0; i<rows.length; i++)
                rows[i] = rowsOrColumns[i].rows[0];
            return new Matrix(rows);
        }

        Number[][] rows = new Number[rowsOrColumns[0].rows.length][rowsOrColumns.length];
        for(int i=0; i<rows.length; i++)
            for(int j=0; j<rows[0].length; j++)
                rows[i][j] = rowsOrColumns[j].rows[i][0];
        return new Matrix(rows);
    }

    public static Matrix rowVector(Number... components) {
        Arguments.deepCheckNull(components, "components");
        return new Matrix(new Number[][] { components.clone() });
    }

    public static Matrix columnVector(Number... components) {
        Arguments.deepCheckNull(components, "components");
        Number[][] rows = new Number[components.length][1];
        for(int i=0; i<rows.length; i++)
            rows[i][0] = components[i];
        return new Matrix(rows);
    }

    public static Matrix identity(int size) {
        Arguments.checkRange(size, 1, null);
        Number[][] rows = new Number[size][size];
        for(int i=0; i<rows.length; i++) {
            Arrays.fill(rows[i], Number.ZERO());
            rows[i][i] = Number.ONE();
        }
        return new Matrix(rows);
    }

    public static Matrix new1x1(Number a11) {
        return new Matrix(new Number[][] {{ a11 }});
    }

    public static Matrix new2x2(Number a11, Number a12, Number a21, Number a22) {
        return new Matrix(new Number[][] {
                { a11, a12 },
                { a21, a22 }
        });
    }

    public static Matrix new3x3(Number a11, Number a12, Number a13,
                                Number a21, Number a22, Number a23,
                                Number a31, Number a32, Number a33) {
        return new Matrix(new Number[][] {
                { a11, a12, a13 },
                { a21, a22, a23 },
                { a31, a32, a33 }
        });
    }



    public record Size(int rows, int columns) {
        public Size {
            Arguments.checkRange(rows, 1, null);
            Arguments.checkRange(columns, 1, null);
        }

        @Override
        public String toString() {
            return rows + "x" + columns;
        }

        public int rowSize() {
            return columns;
        }

        public int columnSize() {
            return rows;
        }

        public int elementCount() {
            return rows * columns;
        }

        public boolean isQuadratic() {
            return rows == columns;
        }

        public boolean isScalar() {
            return rows == 1 && columns == 1;
        }

        public boolean isVector() {
            return rows == 1 || columns == 1;
        }

        public boolean isRowVector() {
            return rows == 1;
        }

        public boolean isColumnVector() {
            return columns == 1;
        }

        public Matrix toVector() {
            return Matrix.columnVector(new Rational(rows), new Rational(columns));
        }
    }

    @FunctionalInterface
    public interface MatrixOperation {
        Number apply(int row, int column, Number element);
    }
}
