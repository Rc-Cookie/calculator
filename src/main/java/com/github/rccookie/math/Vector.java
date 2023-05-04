package com.github.rccookie.math;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.github.rccookie.json.JsonArray;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.math.expr.MathEvaluationException;
import com.github.rccookie.math.expr.MathExpressionSyntaxException;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.primitive.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class Vector implements Number {

    static {
        JsonDeserialization.register(Vector.class, json -> {
            if(!json.isArray())
                return new Vector(json.as(Number.class));
            return new Vector(json.as(Number[].class));
        });
    }

    private final Number[] components;

    public Vector(Number... components) {
        if(Arguments.checkNull(components, "components").length == 0)
            throw new IllegalArgumentException("Vector requires at least one component");
        this.components = components.clone();
    }

    private Vector(boolean ignored, Number... components) {
        if(components.length == 0)
            throw new IllegalArgumentException("Vector requires at least one component");
        this.components = components;
    }

    private Vector(Vector v, UnaryOperator<Number> operator) {
        this.components = new Number[v.components.length];
        for(int i=0; i<components.length; i++)
            components[i] = operator.apply(v.components[i]);
    }

    private Vector(Vector a, Vector b, BinaryOperator<Number> operator) {
        this.components = new Number[Math.max(a.components.length, b.components.length)];
        for(int i=0; i<components.length; i++)
            components[i] = operator.apply(a.get(i), b.get(i));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Vector v && Arrays.equals(components, v.components)) ||
               (obj instanceof Number n && Arrays.stream(components).allMatch(n::equals));
    }

    @Override
    public int hashCode() {
        return components.length == 1 ? components[0].hashCode() : Arrays.hashCode(components);
    }

    @Override
    public String toString() {
        if(components.length == 1)
            return components[0].toString();
        return Arrays.toString(components);
    }

    @Override
    public double toDouble(SymbolLookup c) {
        if(components.length == 1)
            return components[0].toDouble(c);
        throw new UnsupportedOperationException("Cannot convert multi-component vector to double");
    }

    @Override
    public Object toJson() {
        return new JsonArray((Object[]) components.clone());
    }

    @Override
    public @NotNull Vector equalTo(Number x) {
        return x instanceof Vector v ? equalTo(v) : derive(c -> c.equalTo(x));
    }

    public Vector equalTo(Vector x) {
        return derive(x, Number::equalTo);
    }

    @Override
    public @NotNull Vector lessThan(Number x) {
        return x instanceof Vector v ? lessThan(v) : derive(c -> c.lessThan(x));
    }

    public Vector lessThan(Vector x) {
        return derive(x, Number::lessThan);
    }

    @Override
    public @NotNull Vector greaterThan(Number x) {
        return x instanceof Vector v ? greaterThan(v) : derive(c -> c.greaterThan(x));
    }

    public Vector greaterThan(Vector x) {
        return derive(x, Number::greaterThan);
    }

    public Number get(int index) {
        if(index < 0) throw new MathEvaluationException("Vector index out of lower bounds"); // Don't specify index, may be 0-indexed or 1-indexed for calculator
        return index < components.length ? components[index] : SymbolLookup.UNSPECIFIED;
    }

    public Number get(int row, int column) {
        if(row < 0 || column < 0)
            throw new MathEvaluationException("Matrix index out of lower bounds");
        if(row >= components.length)
            return SymbolLookup.UNSPECIFIED;
        Number rowComponents = components[row];
        if(rowComponents instanceof Vector v)
            return v.get(column);
        return column == 0 ? rowComponents : SymbolLookup.UNSPECIFIED;
    }

    public Number get(Number index) {
        return switch(index) {
            case Vector indices -> indices.derive(this, ($, i) -> get(i));
            case SimpleNumber n -> get((int) n.toDouble());
            default -> throw new UnsupportedOperationException();
        };
    }

    public Number get(Number row, Number column) {
        if(row instanceof Vector rows)
            return rows.derive(this, ($,r) -> get(r, column));
        if(column instanceof Vector columns)
            return columns.derive(this, ($,c) -> get(row, c));
        if(!(row instanceof SimpleNumber && column instanceof SimpleNumber))
            throw new UnsupportedOperationException();
        return get((int) row.toDouble(), (int) column.toDouble());
    }

    public Number x() {
        return components[0];
    }

    public Number y() {
        return get(1);
    }

    public Number z() {
        return get(2);
    }

    public Vector[] rows() {
        if(isScalar()) return new Vector[] { this };
        if(!isMatrix())
            throw new MathEvaluationException("Vector is not a matrix, cannot receive rows");

        Vector[] rows = new Vector[components.length];
        for(int i=0; i<rows.length; i++)
            rows[i] = (Vector) components[i];
        return rows;
    }

    public Vector[] columns() {
        if(isScalar()) return new Vector[] { this };
        if(!isMatrix())
            throw new MathEvaluationException("Vector is not a matrix, cannot receive columns");

        Vector[] columns = new Vector[((Vector) components[0]).components.length];
        for(int i=0; i<columns.length; i++) {
            Number[] column = new Number[components.length];
            for(int j=0; j<column.length; j++)
                column[j] = ((Vector) components[j]).components[i];
            columns[i] = new Vector(column);
        }
        return columns;
    }

    public int size() {
        return components.length;
    }

    public int rowCount() {
        if(!isMatrix())
            throw new MathEvaluationException("Vector is not a matrix, cannot receive row count");
        return components.length;
    }

    public int columnCount() {
        if(!isMatrix())
            throw new MathEvaluationException("Vector is not a matrix, cannot receive column count");
        if(components[0] instanceof Vector row)
            return row.components.length;
        return 1; // Primitive as row => one element
    }

    public Vector[] getMatrixRows() {
        if(!isMatrix()) throw new MathEvaluationException("Vector is not a matrix, cannot receive matrix rows");
        Vector[] rows = new Vector[components.length];
        for(int i=0; i<rows.length; i++)
            rows[i] = Vector.asVector(components[i]);
        return rows;
    }

    public boolean isScalar() {
        return components.length == 1 && (!(components[0] instanceof Vector row) || row.isScalar());
    }

    private Number getScalarValue() {
        if(components.length != 1) throw new MathEvaluationException("Matrix is not a scalar");
        if(!(components[0] instanceof Vector v)) return components[0];
        return v.getScalarValue();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isMatrix() {
        if(components.length == 1) return true;
        int expected = size(components[0]);
        for(int i=1; i<components.length; i++)
            if(size(components[i]) != expected)
                return false;
        return true;
    }

    public boolean isColumn() {
        return getMatrixSize().y == 1;
    }

    public boolean isRow() {
        return getMatrixSize().x == 1;
    }

    private int2 getMatrixSize() {
        return new int2(components.length, size(components[0]));
    }

    private static int size(Number x) {
        return x instanceof Vector v ? v.size() : 1;
    }

    public boolean isZero() {
        for(Number c : components)
            if(!c.equals(Number.ZERO())) return false;
        return true;
    }

    @Override
    public @NotNull Vector add(Number x) {
        return x instanceof Vector v ? add(v) : derive(c -> c.add(x));
    }

    @NotNull
    public Vector add(Vector x) {
        return derive(x, Number::add);
    }

    @Override
    public @NotNull Vector subtract(Number x) {
        return x instanceof Vector v ? subtract(v) : derive(c -> c.subtract(x));
    }

    @NotNull
    public Vector subtract(Vector x) {
        return derive(x, Number::subtract);
    }

    @Override
    public @NotNull Vector subtractFrom(Number x) {
        return x instanceof Vector v ? subtractFrom(v) : derive(c -> c.subtractFrom(x));
    }

    @NotNull
    public Vector subtractFrom(Vector x) {
        return derive(x, Number::subtractFrom);
    }

    @Override
    @NotNull
    public Number multiply(Number x) {
        if(!(x instanceof Vector v) || !isMatrix() || !v.isMatrix()) return derive(c -> c.multiply(x));
        if(v.isScalar()) return derive(c -> c.multiply(v.getScalarValue()));
        if(isScalar()) return v.derive(c -> c.multiply(getScalarValue()));
        if((isColumn() && v.isColumn()) || (isRow() && v.isRow())) return dot(v);
        return matrixMultiply(v);
    }

    @NotNull
    public Number matrixMultiply(Vector matrix) {
        int2 size = getMatrixSize(), otherSize = matrix.getMatrixSize();
        int m = size.x, n = size.y, otherM = otherSize.x, otherN = otherSize.y;
        if(n != otherM) throw new MathEvaluationException("Trying to multiply incompatible matrices");

        Vector result = new Vector(true, new Number[m]);
        for(int i=0; i<m; i++) {
            Vector row = new Vector(true, new Number[otherN]);
            for(int j=0; j<otherN; j++) {
                Number sum = Number.ZERO();
                for(int k=0; k<n; k++)
                    sum = sum.add(get(i,k).multiply(matrix.get(k,j)));
                row.components[j] = sum;
            }
            result.components[i] = row;
        }
        return result;
    }

    @NotNull
    public Number dot(Vector x) {
        Number y = components[0].multiply(x.components[0]);
        for(int i=1; i<Math.min(components.length, x.components.length); i++)
            y = y.add(components[i].multiply(x.components[i]));
        return y;
    }

    @NotNull
    public Number multiplyComponentwise(Vector x) {
        return derive(x, Number::multiply);
    }

    @NotNull
    public Vector cross(Vector x) {
        if(components.length != 3 || x.components.length != 3)
            throw new ArithmeticException("Cross product only defined for 3d vectors");
        return new Vector(true,
                y().multiply(x.z()).subtract(z().multiply(x.y())),
                z().multiply(x.x()).subtract(x().multiply(x.z())),
                x().multiply(x.y()).subtract(y().multiply(x.x()))
        );
    }

    @Override
    public @NotNull Vector divide(Number x) {
        return x instanceof Vector v ? divide(v) : derive(c -> c.divide(x));
    }

    @NotNull
    public Vector divide(Vector x) {
        return derive(x, Number::divide);
    }

    @Override
    public @NotNull Vector divideOther(Number x) {
        return x instanceof Vector v ? divideOther(v) : derive(c -> c.divideOther(x));
    }

    @NotNull
    public Vector divideOther(Vector x) {
        return derive(x, Number::divideOther);
    }

    @Override
    public @NotNull Vector raise(Number x) {
        return x instanceof Vector v ? raise(v) : derive(c -> c.raise(x));
    }

    @NotNull
    public Vector raise(Vector x) {
        return derive(x, Number::raise);
    }

    @Override
    public @NotNull Vector raiseOther(Number x) {
        return x instanceof Vector v ? raiseOther(v) : derive(c -> c.raiseOther(x));
    }

    @NotNull
    public Vector raiseOther(Vector x) {
        return derive(x, Number::raiseOther);
    }

    @NotNull
    public Number sqrAbs() {
        return dot(this);
    }

    @Override
    public @NotNull Number abs() {
        return sqrAbs().sqrt();
    }

    @Override
    public @NotNull Vector negate() {
        return derive(Number::negate);
    }

    @Override
    public @NotNull Vector invert() {
        return derive(Number::invert);
    }

    public @NotNull Vector normalize() {
        if(isZero()) return this;
        return divide(abs());
    }


    public Vector derive(UnaryOperator<Number> operator) {
        return new Vector(this, operator);
    }

    public Vector derive(Vector x, BinaryOperator<Number> operator) {
        return new Vector(this, x, operator);
    }



    public static Vector asVector(Number x) {
        return x instanceof Vector v ? v : new Vector(x);
    }

    public static Vector matrix(int rows, int columns, Number... componentsRowByRow) {
        Arguments.checkRange(rows, 1, null);
        Arguments.checkRange(columns, 1, null);
        Arguments.deepCheckNull(componentsRowByRow, "componentsRowByRow");
        if(componentsRowByRow.length != rows * columns)
            throw new IllegalArgumentException("Wrong number of components (expected " + rows + "*" + columns + "=" + rows*columns + ", got " + componentsRowByRow.length);
        Vector[] rowVectors = new Vector[rows];
        for(int i=0; i<rows; i++)
            rowVectors[i] = new Vector(Arrays.copyOfRange(componentsRowByRow, i, i + columns));
        return new Vector(true, rowVectors);
    }

    public static Vector matrixFromRows(Vector... rows) {
        Arguments.deepCheckNull(rows, "rows");
        if(rows.length == 0)
            throw new IllegalArgumentException("Matrix requires at least one row");
        int expected = rows[0].size();
        for(int i=1; i<rows.length; i++)
            if(rows[i].size() != expected)
                throw new MathExpressionSyntaxException("Matrix rows must have same number of entries");
        return new Vector(rows);
    }

    public static Vector matrixFromColumns(Vector... columns) {
        Arguments.deepCheckNull(columns, "columns");
        if(columns.length == 0)
            throw new IllegalArgumentException("Matrix requires at least one column");
        Vector[] rows = new Vector[columns[0].size()];

        for(int i=1; i<columns.length; i++)
            if(columns[i].size() != rows.length)
                throw new MathExpressionSyntaxException("Matrix columns must have same number of entries");

        for(int i=0; i<rows.length; i++) {
            Number[] row = new Number[columns.length];
            for(int j=0; j<row.length; j++)
                row[j] = columns[j].get(i);
            rows[i] = new Vector(row);
        }

        return new Vector(true, rows);
    }
}
