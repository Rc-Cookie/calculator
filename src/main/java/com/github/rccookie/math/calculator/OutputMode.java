package com.github.rccookie.math.calculator;

import java.util.function.BiFunction;

import com.github.rccookie.math.rendering.CharacterSet;
import com.github.rccookie.math.rendering.RenderableExpression;

public enum OutputMode {
    INLINE(RenderableExpression::renderInline),
    SUPPORTED_UNICODE((e,o) -> e.renderAsciiArt(o.setCharset(Calculator.SUPPORTED_CHARSET))),
    ASCII((e,o) -> e.renderAsciiArt(o.setCharset(CharacterSet.ASCII))),
    UNICODE((e,o) -> e.renderAsciiArt(o.setCharset(CharacterSet.UNICODE))),
    LATEX(RenderableExpression::renderLatex),
    MATHML((e,o) -> e.renderMathML(o,false));


    private final BiFunction<RenderableExpression, RenderableExpression.RenderOptions, ?> toString;

    OutputMode(BiFunction<RenderableExpression, RenderableExpression.RenderOptions, ?> toString) {
        this.toString = toString;
    }

    public String renderToString(RenderableExpression expr, RenderableExpression.RenderOptions options) {
        return toString.apply(expr, options).toString();
    }
}
