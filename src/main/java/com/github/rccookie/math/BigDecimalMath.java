package com.github.rccookie.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.ProviderException;
import java.util.Collections;
import java.util.Vector;


/** BigDecimal special functions.
 * <a href="http://arxiv.org/abs/0908.3030">A Java Math.BigDecimal Implementation of Core Mathematical Functions</a>
 * @since 2009-05-22
 * @author Richard J. Mathar
 * <a href="http://apfloat.org/">apfloat</a>
 * <a href="http://dfp.sourceforge.net/">dfp</a>
 * <a href="http://jscience.org/">JScience</a>
 */
public final class BigDecimalMath
{

    private BigDecimalMath() { }

    /** The base of the natural logarithm in a predefined accuracy.
     * http://www.cs.arizona.edu/icon/oddsends/e.htm
     * The precision of the predefined constant is one less than
     * the string's length, taking into account the decimal dot.
     * static int E_PRECISION = E.length()-1 ;
     */
    static BigDecimal E = new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663035354"+
            "759457138217852516642742746639193200305992181741359662904357290033429526059563"+
            "073813232862794349076323382988075319525101901157383418793070215408914993488416"+
            "750924476146066808226480016847741185374234544243710753907774499206955170276183"+
            "860626133138458300075204493382656029760673711320070932870912744374704723069697"+
            "720931014169283681902551510865746377211125238978442505695369677078544996996794"+
            "686445490598793163688923009879312773617821542499922957635148220826989519366803"+
            "318252886939849646510582093923982948879332036250944311730123819706841614039701"+
            "983767932068328237646480429531180232878250981945581530175671736133206981125099"+
            "618188159304169035159888851934580727386673858942287922849989208680582574927961"+
            "048419844436346324496848756023362482704197862320900216099023530436994184914631"+
            "409343173814364054625315209618369088870701676839642437814059271456354906130310"+
            "720851038375051011574770417189861068739696552126715468895703503540212340784981"+
            "933432106817012100562788023519303322474501585390473041995777709350366041699732"+
            "972508868769664035557071622684471625607988265178713419512466520103059212366771"+
            "943252786753985589448969709640975459185695638023637016211204774272283648961342"+
            "251644507818244235294863637214174023889344124796357437026375529444833799801612"+
            "549227850925778256209262264832627793338656648162772516401910590049164499828931") ;

    /** Euler's constant Pi.
     * http://www.cs.arizona.edu/icon/oddsends/pi.htm
     */
    static BigDecimal PI = new BigDecimal("3.14159265358979323846264338327950288419716939937510582097494459230781640628620"+
            "899862803482534211706798214808651328230664709384460955058223172535940812848111"+
            "745028410270193852110555964462294895493038196442881097566593344612847564823378"+
            "678316527120190914564856692346034861045432664821339360726024914127372458700660"+
            "631558817488152092096282925409171536436789259036001133053054882046652138414695"+
            "194151160943305727036575959195309218611738193261179310511854807446237996274956"+
            "735188575272489122793818301194912983367336244065664308602139494639522473719070"+
            "217986094370277053921717629317675238467481846766940513200056812714526356082778"+
            "577134275778960917363717872146844090122495343014654958537105079227968925892354"+
            "201995611212902196086403441815981362977477130996051870721134999999837297804995"+
            "105973173281609631859502445945534690830264252230825334468503526193118817101000"+
            "313783875288658753320838142061717766914730359825349042875546873115956286388235"+
            "378759375195778185778053217122680661300192787661119590921642019893809525720106"+
            "548586327886593615338182796823030195203530185296899577362259941389124972177528"+
            "347913151557485724245415069595082953311686172785588907509838175463746493931925"+
            "506040092770167113900984882401285836160356370766010471018194295559619894676783"+
            "744944825537977472684710404753464620804668425906949129331367702898915210475216"+
            "205696602405803815019351125338243003558764024749647326391419927260426992279678"+
            "235478163600934172164121992458631503028618297455570674983850549458858692699569"+
            "092721079750930295532116534498720275596023648066549911988183479775356636980742"+
            "654252786255181841757467289097777279380008164706001614524919217321721477235014") ;

    /** Euler-Mascheroni constant lower-case gamma.
     * http://www.worldwideschool.org/library/books/sci/math/MiscellaneousMathematicalConstants/chap35.html
     */
    static BigDecimal GAMMA = new BigDecimal("0.577215664901532860606512090082402431"+
            "0421593359399235988057672348848677267776646709369470632917467495146314472498070"+
            "8248096050401448654283622417399764492353625350033374293733773767394279259525824"+
            "7094916008735203948165670853233151776611528621199501507984793745085705740029921"+
            "3547861466940296043254215190587755352673313992540129674205137541395491116851028"+
            "0798423487758720503843109399736137255306088933126760017247953783675927135157722"+
            "6102734929139407984301034177717780881549570661075010161916633401522789358679654"+
            "9725203621287922655595366962817638879272680132431010476505963703947394957638906"+
            "5729679296010090151251959509222435014093498712282479497471956469763185066761290"+
            "6381105182419744486783638086174945516989279230187739107294578155431600500218284"+
            "4096053772434203285478367015177394398700302370339518328690001558193988042707411"+
            "5422278197165230110735658339673487176504919418123000406546931429992977795693031"+
            "0050308630341856980323108369164002589297089098548682577736428825395492587362959"+
            "6133298574739302373438847070370284412920166417850248733379080562754998434590761"+
            "6431671031467107223700218107450444186647591348036690255324586254422253451813879"+
            "1243457350136129778227828814894590986384600629316947188714958752549236649352047"+
            "3243641097268276160877595088095126208404544477992299157248292516251278427659657"+
            "0832146102982146179519579590959227042089896279712553632179488737642106606070659"+
            "8256199010288075612519913751167821764361905705844078357350158005607745793421314"+
            "49885007864151716151945");

    /** Natural logarithm of 2.
     * http://www.worldwideschool.org/library/books/sci/math/MiscellaneousMathematicalConstants/chap58.html
     */
    static BigDecimal LOG2 = new BigDecimal("0.693147180559945309417232121458176568075"+
            "50013436025525412068000949339362196969471560586332699641868754200148102057068573"+
            "368552023575813055703267075163507596193072757082837143519030703862389167347112335"+
            "011536449795523912047517268157493206515552473413952588295045300709532636664265410"+
            "423915781495204374043038550080194417064167151864471283996817178454695702627163106"+
            "454615025720740248163777338963855069526066834113727387372292895649354702576265209"+
            "885969320196505855476470330679365443254763274495125040606943814710468994650622016"+
            "772042452452961268794654619316517468139267250410380254625965686914419287160829380"+
            "317271436778265487756648508567407764845146443994046142260319309673540257444607030"+
            "809608504748663852313818167675143866747664789088143714198549423151997354880375165"+
            "861275352916610007105355824987941472950929311389715599820565439287170007218085761"+
            "025236889213244971389320378439353088774825970171559107088236836275898425891853530"+
            "243634214367061189236789192372314672321720534016492568727477823445353476481149418"+
            "642386776774406069562657379600867076257199184734022651462837904883062033061144630"+
            "073719489002743643965002580936519443041191150608094879306786515887090060520346842"+
            "973619384128965255653968602219412292420757432175748909770675268711581705113700915"+
            "894266547859596489065305846025866838294002283300538207400567705304678700184162404"+
            "418833232798386349001563121889560650553151272199398332030751408426091479001265168"+
            "243443893572472788205486271552741877243002489794540196187233980860831664811490930"+
            "667519339312890431641370681397776498176974868903887789991296503619270710889264105"+
            "230924783917373501229842420499568935992206602204654941510613");


    /** Euler's constant.
     * @param mc The required precision of the result.
     * @return 3.14159...
     * @since 2009-05-29
     * @author Richard J. Mathar
     */
    static public BigDecimal pi(final MathContext mc)
    {
        /* look it up if possible */
        if ( mc.getPrecision() < PI.precision() )
            return PI.round(mc) ;
        else
        {
            /* Broadhurst <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             */
            int[] a = {1,0,0,-1,-1,-1,0,0} ;
            BigDecimal S = broadhurstBBP(1,1,a,mc) ;
            return multiplyRound(S,8) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.pi */

    /** Euler-Mascheroni constant.
     * @param mc The required precision of the result.
     * @return 0.577...
     * @since 2009-08-13
     * @author Richard J. Mathar
     */
    static public BigDecimal gamma(MathContext mc)
    {
        /* look it up if possible */
        if ( mc.getPrecision() < GAMMA.precision() )
            return GAMMA.round(mc) ;
        else
        {
            double eps = prec2err(0.577, mc.getPrecision() ) ;


            /* Euler-Stieltjes as shown in Dilcher, Aequat Math 48 (1) (1994) 55-85
             */
            MathContext mcloc =  new MathContext(2+mc.getPrecision()) ;
            BigDecimal resul =  BigDecimal.ONE ;
            resul =  resul.add( log(2,mcloc) ) ;
            resul =  resul.subtract( log(3,mcloc) ) ;

            /* how many terms: zeta-1 falls as 1/2^(2n+1), so the
             * terms drop faster than 1/2^(4n+2). Set 1/2^(4kmax+2) < eps.
             * Leading term zeta(3)/(4^1*3) is 0.017. Leading zeta(3) is 1.2. Log(2) is 0.7
             */
            int kmax = (int)((Math.log(eps/0.7)-2.)/4.) ;
            mcloc =  new MathContext( 1+err2prec(1.2,eps/kmax) ) ;
            for(int n=1; ; n++)
            {
                /* zeta is close to 1. Division of zeta-1 through
                 * 4^n*(2n+1) means divion through roughly 2^(2n+1)
                 */
                BigDecimal c = zeta(2*n+1,mcloc).subtract(BigDecimal.ONE) ;
                BigInteger fourn = new BigInteger(""+(2*n+1)) ;
                fourn = fourn.shiftLeft(2*n) ;
                c = divideRound(c, fourn) ;
                resul = resul.subtract(c) ;
                if ( c.doubleValue() < 0.1*eps)
                    break;
            }
            return resul.round(mc) ;
        }

    } /* com.github.rccookie.math.BigDecimalMath.gamma */


    /** The square root.
     * @param x the non-negative argument.
     * @param mc The required mathematical precision.
     * @return the square root of the BigDecimal.
     * @since 2008-10-27
     * @author Richard J. Mathar
     */
    static public BigDecimal sqrt(final BigDecimal x, final MathContext mc)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("negative argument "+x.toString()+ " of square root") ;
        if ( x.abs().subtract( new BigDecimal(Math.pow(10.,-mc.getPrecision())) ).compareTo(BigDecimal.ZERO) < 0 )
            return BigDecimalMath.scalePrec(BigDecimal.ZERO,mc) ;
        /* start the computation from a double precision estimate */
        BigDecimal s = new BigDecimal( Math.sqrt(x.doubleValue()) ,mc) ;
        final BigDecimal half = new BigDecimal("2") ;

        /* increase the local accuracy by 2 digits */
        MathContext locmc = new MathContext(mc.getPrecision()+2,mc.getRoundingMode()) ;

        /* relative accuracy requested is 10^(-precision)
         */
        final double eps = Math.pow(10.0,-mc.getPrecision()) ;
        for (;;)
        {
            /* s = s -(s/2-x/2s); test correction s-x/s for being
             * smaller than the precision requested. The relative correction is 1-x/s^2,
             * (actually half of this, which we use for a little bit of additional protection).
             */
            if ( Math.abs(BigDecimal.ONE.subtract(x.divide(s.pow(2,locmc),locmc)).doubleValue()) < eps)
                break ;
            s = s.add(x.divide(s,locmc)).divide(half,locmc) ;
            /* debugging
             * System.out.println("itr "+x.round(locmc).toString() + " " + s.round(locmc).toString()) ;
             */
        }
        return s ;
    } /* com.github.rccookie.math.BigDecimalMath.sqrt */

    /** The square root.
     * @param x the non-negative argument.
     * @return the square root of the BigDecimal rounded to the precision implied by x.
     * @since 2009-06-25
     * @author Richard J. Mathar
     */
    static public BigDecimal sqrt(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("negative argument "+x.toString()+ " of square root") ;

        return root(2,x) ;
    } /* com.github.rccookie.math.BigDecimalMath.sqrt */

    /** The cube root.
     * @param x The argument.
     * @return The cubic root of the BigDecimal rounded to the precision implied by x.
     * The sign of the result is the sign of the argument.
     * @since 2009-08-16
     * @author Richard J. Mathar
     */
    static public BigDecimal cbrt(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            return root(3,x.negate()).negate() ;
        else
            return root(3,x) ;
    } /* com.github.rccookie.math.BigDecimalMath.cbrt */

    /** The integer root.
     * @param n the positive argument.
     * @param x the non-negative argument.
     * @return The n-th root of the BigDecimal rounded to the precision implied by x, x^(1/n).
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal root(final int n, final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("negative argument "+x.toString()+ " of root") ;
        if ( n<= 0 )
            throw new ArithmeticException("negative power "+ n + " of root") ;

        if ( n == 1 )
            return x ;

        /* start the computation from a double precision estimate */
        BigDecimal s = new BigDecimal( Math.pow(x.doubleValue(),1.0/n) ) ;

        /* this creates nth with nominal precision of 1 digit
         */
        final BigDecimal nth = new BigDecimal(n) ;

        /* Specify an internal accuracy within the loop which is
         * slightly larger than what is demanded by 'eps' below.
         */
        final BigDecimal xhighpr = scalePrec(x,2) ;
        MathContext mc = new MathContext( 2+x.precision() ) ;

        /* Relative accuracy of the result is eps.
         */
        final double eps = x.ulp().doubleValue()/(2*n*x.doubleValue()) ;
        for (;;)
        {
            /* s = s -(s/n-x/n/s^(n-1)) = s-(s-x/s^(n-1))/n; test correction s/n-x/s for being
             * smaller than the precision requested. The relative correction is (1-x/s^n)/n,
             */
            BigDecimal c = xhighpr.divide( s.pow(n-1),mc ) ;
            c = s.subtract(c) ;
            MathContext locmc = new MathContext( c.precision() ) ;
            c = c.divide(nth,locmc) ;
            s = s. subtract(c) ;
            if ( Math.abs( c.doubleValue()/s.doubleValue()) < eps)
                break ;
        }
        return s.round(new MathContext( err2prec(eps)) ) ;
    } /* com.github.rccookie.math.BigDecimalMath.root */

    /** The hypotenuse.
     * @param x the first argument.
     * @param y the second argument.
     * @return the square root of the sum of the squares of the two arguments, sqrt(x^2+y^2).
     * @since 2009-06-25
     * @author Richard J. Mathar
     */
    static public BigDecimal hypot(final BigDecimal x, final BigDecimal y)
    {
        /* compute x^2+y^2
         */
        BigDecimal z = x.pow(2).add(y.pow(2)) ;

        /* truncate to the precision set by x and y. Absolute error = 2*x*xerr+2*y*yerr,
         * where the two errors are 1/2 of the ulp's.  Two intermediate protectio digits.
         */
        BigDecimal zerr = x.abs().multiply(x.ulp()).add(y.abs().multiply(y.ulp())) ;
        MathContext mc = new MathContext(  2+err2prec(z,zerr) ) ;

        /* Pull square root */
        z = sqrt(z.round(mc)) ;

        /* Final rounding. Absolute error in the square root is (y*yerr+x*xerr)/z, where zerr holds 2*(x*xerr+y*yerr).
         */
        mc = new MathContext(  err2prec(z.doubleValue() ,0.5*zerr.doubleValue() /z.doubleValue() ) ) ;
        return z.round(mc) ;
    } /* com.github.rccookie.math.BigDecimalMath.hypot */

    /** The hypotenuse.
     * @param n the first argument.
     * @param x the second argument.
     * @return the square root of the sum of the squares of the two arguments, sqrt(n^2+x^2).
     * @since 2009-08-05
     * @author Richard J. Mathar
     */
    static public BigDecimal hypot(final int n, final BigDecimal x)
    {
        /* compute n^2+x^2 in infinite precision
         */
        BigDecimal z = (new BigDecimal(n)).pow(2).add(x.pow(2)) ;

        /* Truncate to the precision set by x. Absolute error = in z (square of the result) is |2*x*xerr|,
         * where the error is 1/2 of the ulp. Two intermediate protection digits.
         * zerr is a signed value, but used only in conjunction with err2prec(), so this feature does not harm.
         */
        double zerr = x.doubleValue()*x.ulp().doubleValue() ;
        MathContext mc = new MathContext(  2+err2prec(z.doubleValue(),zerr) ) ;

        /* Pull square root */
        z = sqrt(z.round(mc)) ;

        /* Final rounding. Absolute error in the square root is x*xerr/z, where zerr holds 2*x*xerr.
         */
        mc = new MathContext(  err2prec(z.doubleValue(),0.5*zerr/z.doubleValue() ) ) ;
        return z.round(mc) ;
    } /* com.github.rccookie.math.BigDecimalMath.hypot */


    /** A suggestion for the maximum numter of terms in the Taylor expansion of the exponential.
     */
    static private int TAYLOR_NTERM = 8 ;

    /** The exponential function.
     * @param x the argument.
     * @return exp(x).
     * The precision of the result is implicitly defined by the precision in the argument.
     * In particular this means that "Invalid Operation" errors are thrown if catastrophic
     * cancellation of digits causes the result to have no valid digits left.
     * @since 2009-05-29
     * @author Richard J. Mathar
     */
    static public BigDecimal exp(BigDecimal x)
    {
        /* To calculate the value if x is negative, use exp(-x) = 1/exp(x)
         */
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
        {
            final BigDecimal invx = exp(x.negate() ) ;
            /* Relative error in inverse of invx is the same as the relative errror in invx.
             * This is used to define the precision of the result.
             */
            MathContext mc = new MathContext( invx.precision() ) ;
            return BigDecimal.ONE.divide( invx, mc ) ;
        }
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
        {
            /* recover the valid number of digits from x.ulp(), if x hits the
             * zero. The x.precision() is 1 then, and does not provide this information.
             */
            return scalePrec(BigDecimal.ONE, -(int)(Math.log10( x.ulp().doubleValue() )) ) ;
        }
        else
        {
            /* Push the number in the Taylor expansion down to a small
             * value where TAYLOR_NTERM terms will do. If x<1, the n-th term is of the order
             * x^n/n!, and equal to both the absolute and relative error of the result
             * since the result is close to 1. The x.ulp() sets the relative and absolute error
             * of the result, as estimated from the first Taylor term.
             * We want x^TAYLOR_NTERM/TAYLOR_NTERM! < x.ulp, which is guaranteed if
             * x^TAYLOR_NTERM < TAYLOR_NTERM*(TAYLOR_NTERM-1)*...*x.ulp.
             */
            final double xDbl = x.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue() ;
            if ( Math.pow(xDbl,TAYLOR_NTERM) < TAYLOR_NTERM*(TAYLOR_NTERM-1.0)*(TAYLOR_NTERM-2.0)*xUlpDbl )
            {
                /* Add TAYLOR_NTERM terms of the Taylor expansion (Euler's sum formula)
                 */
                BigDecimal resul = BigDecimal.ONE ;

                /* x^i */
                BigDecimal xpowi = BigDecimal.ONE ;

                /* i factorial */
                BigInteger ifac = BigInteger.ONE ;

                /* TAYLOR_NTERM terms to be added means we move x.ulp() to the right
                 * for each power of 10 in TAYLOR_NTERM, so the addition won't add noise beyond
                 * what's already in x.
                 */
                MathContext mcTay = new MathContext( err2prec(1.,xUlpDbl/TAYLOR_NTERM) ) ;
                for(int i=1 ; i <= TAYLOR_NTERM ; i++)
                {
                    ifac = ifac.multiply(new BigInteger(""+i) ) ;
                    xpowi = xpowi.multiply(x) ;
                    final BigDecimal c= xpowi.divide(new BigDecimal(ifac),mcTay)  ;
                    resul = resul.add(c) ;
                    if ( Math.abs(xpowi.doubleValue()) < i && Math.abs(c.doubleValue()) < 0.5* xUlpDbl )
                        break;
                }
                /* exp(x+deltax) = exp(x)(1+deltax) if deltax is <<1. So the relative error
                 * in the result equals the absolute error in the argument.
                 */
                MathContext mc = new MathContext( err2prec(xUlpDbl/2.) ) ;
                return resul.round(mc) ;
            }
            else
            {
                /* Compute exp(x) = (exp(0.1*x))^10. Division by 10 does not lead
                 * to loss of accuracy.
                 */
                int exSc = (int) ( 1.0-Math.log10( TAYLOR_NTERM*(TAYLOR_NTERM-1.0)*(TAYLOR_NTERM-2.0)*xUlpDbl
                        /Math.pow(xDbl,TAYLOR_NTERM) ) / ( TAYLOR_NTERM-1.0) ) ;
                BigDecimal xby10 = x.scaleByPowerOfTen(-exSc) ;
                BigDecimal expxby10 = exp(xby10) ;

                /* Final powering by 10 means that the relative error of the result
                 * is 10 times the relative error of the base (First order binomial expansion).
                 * This looses one digit.
                 */
                MathContext mc = new MathContext( expxby10.precision()-exSc ) ;
                /* Rescaling the powers of 10 is done in chunks of a maximum of 8 to avoid an invalid operation
                 * response by the BigDecimal.pow library or integer overflow.
                 */
                while ( exSc > 0 )
                {
                    int exsub = Math.min(8,exSc) ;
                    exSc -= exsub ;
                    MathContext mctmp = new MathContext( expxby10.precision()-exsub+2 ) ;
                    int pex = 1 ;
                    while ( exsub-- > 0 )
                        pex *= 10 ;
                    expxby10 = expxby10.pow(pex,mctmp) ;
                }
                return expxby10.round(mc) ;
            }
        }
    } /* com.github.rccookie.math.BigDecimalMath.exp */

    /** The base of the natural logarithm.
     * @param mc the required precision of the result
     * @return exp(1) = 2.71828....
     * @since 2009-05-29
     * @author Richard J. Mathar
     */
    static public BigDecimal exp(final MathContext mc)
    {
        /* look it up if possible */
        if ( mc.getPrecision() < E.precision() )
            return E.round(mc) ;
        else
        {
            /* Instantiate a 1.0 with the requested pseudo-accuracy
             * and delegate the computation to the public method above.
             */
            BigDecimal uni = scalePrec(BigDecimal.ONE, mc.getPrecision() ) ;
            return exp(uni) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.exp */

    /** The natural logarithm.
     * @param x the argument.
     * @return ln(x).
     * The precision of the result is implicitly defined by the precision in the argument.
     * @since 2009-05-29
     * @author Richard J. Mathar
     */
    static public BigDecimal log(BigDecimal x)
    {
        /* the value is undefined if x is negative.
         */
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("Cannot take log of negative "+ x.toString() ) ;
        else if ( x.compareTo(BigDecimal.ONE) == 0 )
        {
            /* log 1. = 0. */
            return scalePrec(BigDecimal.ZERO, x.precision()-1) ;
        }
        else if ( Math.abs(x.doubleValue()-1.0) <= 0.3 )
        {
            /* The standard Taylor series around x=1, z=0, z=x-1. Abramowitz-Stegun 4.124.
             * The absolute error is err(z)/(1+z) = err(x)/x.
             */
            BigDecimal z = scalePrec(x.subtract(BigDecimal.ONE),2) ;
            BigDecimal zpown = z ;
            double eps = 0.5*x.ulp().doubleValue()/Math.abs(x.doubleValue()) ;
            BigDecimal resul = z ;
            for(int k= 2;; k++)
            {
                zpown = multiplyRound(zpown,z) ;
                BigDecimal c = divideRound(zpown,k) ;
                if ( k % 2 == 0)
                    resul = resul.subtract(c) ;
                else
                    resul = resul.add(c) ;
                if ( Math.abs(c.doubleValue()) < eps)
                    break;
            }
            MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
        else
        {
            final double xDbl = x.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue() ;

            /* Map log(x) = log root[r](x)^r = r*log( root[r](x)) with the aim
             * to move roor[r](x) near to 1.2 (that is, below the 0.3 appearing above), where log(1.2) is roughly 0.2.
             */
            int r = (int) (Math.log(xDbl)/0.2) ;

            /* Since the actual requirement is a function of the value 0.3 appearing above,
             * we avoid the hypothetical case of endless recurrence by ensuring that r >= 2.
             */
            r = Math.max(2,r) ;

            /* Compute r-th root with 2 additional digits of precision
             */
            BigDecimal xhighpr = scalePrec(x,2) ;
            BigDecimal resul = root(r,xhighpr) ;
            resul = log(resul).multiply(new BigDecimal(r)) ;

            /* error propagation: log(x+errx) = log(x)+errx/x, so the absolute error
             * in the result equals the relative error in the input, xUlpDbl/xDbl .
             */
            MathContext mc = new MathContext( err2prec(resul.doubleValue(),xUlpDbl/xDbl) ) ;
            return resul.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.log */

    /** The natural logarithm.
     * @param n The main argument, a strictly positive integer.
     * @param mc The requirements on the precision.
     * @return ln(n).
     * @since 2009-08-08
     * @author Richard J. Mathar
     */
    static public BigDecimal log(int n, final MathContext mc)
    {
        /* the value is undefined if x is negative.
         */
        if ( n <= 0 )
            throw new ArithmeticException("Cannot take log of negative "+ n ) ;
        else if ( n == 1)
            return BigDecimal.ZERO ;
        else if ( n == 2)
        {
            if ( mc.getPrecision() < LOG2.precision() )
                return LOG2.round(mc) ;
            else
            {
                /* Broadhurst <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
                 * Error propagation: the error in log(2) is twice the error in S(2,-5,...).
                 */
                int[] a = {2,-5,-2,-7,-2,-5,2,-3} ;
                BigDecimal S = broadhurstBBP(2,1,a, new MathContext(1+mc.getPrecision()) ) ;
                S = S.multiply(new BigDecimal(8)) ;
                S = sqrt(divideRound(S,3)) ;
                return S.round(mc) ;
            }
        }
        else if ( n == 3)
        {
            /* summation of a series roughly proportional to (7/500)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.013^k <= 10^(-precision), so k*log10(0.013) <= -precision
             * so k>= precision/1.87.
             */
            int kmax = (int)(mc.getPrecision()/1.87) ;
            MathContext mcloc = new MathContext( mc.getPrecision()+ 1+(int)(Math.log10(kmax*0.693/1.098)) ) ;
            BigDecimal log3 = multiplyRound( log(2,mcloc),19 ) ;

            /* log3 is roughly 1, so absolute and relative error are the same. The
             * result will be divided by 12, so a conservative error is the one
             * already found in mc
             */
            double eps = prec2err(1.098,mc.getPrecision() )/kmax ;
            Rational r = new Rational(7153,524288) ;
            Rational pk = new Rational(7153,524288) ;
            for(int k=1; ; k++)
            {
                Rational tmp = pk.divide(k) ;
                if ( tmp.doubleValue() < eps)
                    break ;

                /* how many digits of tmp do we need in the sum?
                 */
                mcloc = new MathContext( err2prec(tmp.doubleValue(),eps) ) ;
                BigDecimal c = pk.divide(k).BigDecimalValue(mcloc) ;
                if ( k % 2 != 0)
                    log3 = log3.add(c) ;
                else
                    log3 = log3.subtract(c) ;
                pk = pk.multiply(r) ;
            }
            log3 = divideRound( log3,12 ) ;
            return log3.round(mc) ;
        }
        else if ( n == 5)
        {
            /* summation of a series roughly proportional to (7/160)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.046^k <= 10^(-precision), so k*log10(0.046) <= -precision
             * so k>= precision/1.33.
             */
            int kmax = (int)(mc.getPrecision()/1.33) ;
            MathContext mcloc = new MathContext( mc.getPrecision()+ 1+(int)(Math.log10(kmax*0.693/1.609)) ) ;
            BigDecimal log5 = multiplyRound( log(2,mcloc),14 ) ;

            /* log5 is roughly 1.6, so absolute and relative error are the same. The
             * result will be divided by 6, so a conservative error is the one
             * already found in mc
             */
            double eps = prec2err(1.6,mc.getPrecision() )/kmax ;
            Rational r = new Rational(759,16384) ;
            Rational pk = new Rational(759,16384) ;
            for(int k=1; ; k++)
            {
                Rational tmp = pk.divide(k) ;
                if ( tmp.doubleValue() < eps)
                    break ;

                /* how many digits of tmp do we need in the sum?
                 */
                mcloc = new MathContext( err2prec(tmp.doubleValue(),eps) ) ;
                BigDecimal c = pk.divide(k).BigDecimalValue(mcloc) ;
                log5 = log5.subtract(c) ;
                pk = pk.multiply(r) ;
            }
            log5 = divideRound( log5,6 ) ;
            return log5.round(mc) ;
        }
        else if ( n == 7)
        {
            /* summation of a series roughly proportional to (1/8)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.125^k <= 10^(-precision), so k*log10(0.125) <= -precision
             * so k>= precision/0.903.
             */
            int kmax = (int)(mc.getPrecision()/0.903) ;
            MathContext mcloc = new MathContext( mc.getPrecision()+ 1+(int)(Math.log10(kmax*3*0.693/1.098)) ) ;
            BigDecimal log7 = multiplyRound( log(2,mcloc),3 ) ;

            /* log7 is roughly 1.9, so absolute and relative error are the same.
             */
            double eps = prec2err(1.9,mc.getPrecision() )/kmax ;
            Rational r = new Rational(1,8) ;
            Rational pk = new Rational(1,8) ;
            for(int k=1; ; k++)
            {
                Rational tmp = pk.divide(k) ;
                if ( tmp.doubleValue() < eps)
                    break ;

                /* how many digits of tmp do we need in the sum?
                 */
                mcloc = new MathContext( err2prec(tmp.doubleValue(),eps) ) ;
                BigDecimal c = pk.divide(k).BigDecimalValue(mcloc) ;
                log7 = log7.subtract(c) ;
                pk = pk.multiply(r) ;
            }
            return log7.round(mc) ;

        }

        else
        {
            /* At this point one could either forward to the log(BigDecimal) signature (implemented)
             * or decompose n into Ifactors and use an implemenation of all the prime bases.
             * Estimate of the result; convert the mc argument to an  absolute error eps
             * log(n+errn) = log(n)+errn/n = log(n)+eps
             */
            double res = Math.log((double)n) ;
            double eps = prec2err(res,mc.getPrecision() ) ;
            /* errn = eps*n, convert absolute error in result to requirement on absolute error in input
             */
            eps *= n ;
            /* Convert this absolute requirement of error in n to a relative error in n
             */
            final MathContext mcloc = new MathContext( 1+err2prec((double)n,eps ) ) ;
            /* Padd n with a number of zeros to trigger the required accuracy in
             * the standard signature method
             */
            BigDecimal nb = scalePrec(new BigDecimal(n),mcloc) ;
            return log(nb) ;
        }
    } /* log */

    /** The natural logarithm.
     * @param r The main argument, a strictly positive value.
     * @param mc The requirements on the precision.
     * @return ln(r).
     * @since 2009-08-09
     * @author Richard J. Mathar
     */
    static public BigDecimal log(final Rational r, final MathContext mc)
    {
        /* the value is undefined if x is negative.
         */
        if ( r.compareTo(Rational.ZERO) <= 0 )
            throw new ArithmeticException("Cannot take log of negative "+ r.toString() ) ;
        else if ( r.compareTo(Rational.ONE) == 0)
            return BigDecimal.ZERO ;
        else
        {

            /* log(r+epsr) = log(r)+epsr/r. Convert the precision to an absolute error in the result.
             * eps contains the required absolute error of the result, epsr/r.
             */
            double eps = prec2err( Math.log(r.doubleValue()), mc.getPrecision()) ;

            /* Convert this further into a requirement of the relative precision in r, given that
             * epsr/r is also the relative precision of r. Add one safety digit.
             */
            MathContext mcloc = new MathContext( 1+err2prec(eps)  ) ;

            final BigDecimal resul = log( r.BigDecimalValue(mcloc) );

            return resul.round(mc) ;
        }
    } /* log */

    /** Power function.
     * @param x Base of the power.
     * @param y Exponent of the power.
     * @return x^y.
     *  The estimation of the relative error in the result is |log(x)*err(y)|+|y*err(x)/x|
     * @since 2009-06-01
     * @author Richard J. Mathar
     */
    static public BigDecimal pow(final BigDecimal x, final BigDecimal y)
    {
        if( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("Cannot power negative "+ x.toString()) ;
        else if( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            /* return x^y = exp(y*log(x)) ;
             */
            BigDecimal logx = log(x) ;
            BigDecimal ylogx = y.multiply(logx) ;
            BigDecimal resul = exp(ylogx) ;

            /* The estimation of the relative error in the result is |log(x)*err(y)|+|y*err(x)/x|
             */
            double errR = Math.abs(logx.doubleValue()*y.ulp().doubleValue()/2.)
                    + Math.abs(y.doubleValue()*x.ulp().doubleValue()/2./x.doubleValue()) ;
            MathContext mcR = new MathContext( err2prec(1.0,errR) ) ;
            return resul.round(mcR) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.pow */

    /** Raise to an integer power and round.
     * @param x The base.
     * @param n The exponent.
     * @return x^n.
     * @since 2009-08-13
     * @since 2010-05-26 handle also cases where n is less than zero.
     * @author Richard J. Mathar
     */
    static public BigDecimal powRound(final BigDecimal x, final int n)
    {
        /** Special cases: x^1=x and x^0 = 1
         */
        if ( n == 1 )
            return x;
        else if ( n == 0 )
            return BigDecimal.ONE ;
        else
        {
            /* The relative error in the result is n times the relative error in the input.
             * The estimation is slightly optimistic due to the integer rounding of the logarithm.
             * Since the standard BigDecimal.pow can only handle positive n, we split the algorithm.
             */
            MathContext mc = new MathContext( x.precision() - (int)Math.log10((double)(Math.abs(n))) ) ;
            if ( n > 0 )
                return x.pow(n,mc) ;
            else
                return BigDecimal.ONE.divide( x.pow(-n),mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.powRound */

    /** Raise to an integer power and round.
     * @param x The base.
     * @param n The exponent.
     *   The current implementation allows n only in the interval of the standard int values.
     * @return x^n.
     * @since 2010-05-26
     * @author Richard J. Mathar
     */
    static public BigDecimal powRound(final BigDecimal x, final BigInteger n)
    {
        /** For now, the implementation forwards to the cases where n
         * is in the range of the standard integers. This might, however, be
         * implemented to decompose larger powers into cascaded calls to smaller ones.
         */
        if ( n.compareTo(Rational.MAX_INT) > 0 || n.compareTo(Rational.MIN_INT) < 0)
            throw new ProviderException("Not implemented: big power "+n.toString() ) ;
        else
            return powRound(x,n.intValue() ) ;
    } /* com.github.rccookie.math.BigDecimalMath.powRound */

    /** Raise to a fractional power and round.
     * @param x The base.
     *     Generally enforced to be positive, with the exception of integer exponents where
     *     the sign is carried over according to the parity of the exponent.
     * @param q The exponent.
     * @return x^q.
     * @since 2010-05-26
     * @author Richard J. Mathar
     */
    static public BigDecimal powRound(final BigDecimal x, final Rational q)
    {
        /** Special cases: x^1=x and x^0 = 1
         */
        if ( q.compareTo(BigInteger.ONE) == 0 )
            return x;
        else if ( q.signum() == 0 )
            return BigDecimal.ONE ;
        else if ( q.isInteger() )
        {
            /* We are sure that the denominator is positive here, because normalize() has been
             * called during constrution etc.
             */
            return powRound(x,q.a) ;
        }
        /* Refuse to operate on the general negative basis. The integer q have already been handled above.
         */
        else if ( x.compareTo(BigDecimal.ZERO) < 0 )
            throw new ArithmeticException("Cannot power negative "+ x.toString() ) ;
        else
        {
            if ( q.isIntegerFrac() )
            {
                /* Newton method with first estimate in double precision.
                 * The disadvantage of this first line here is that the result must fit in the
                 * standard range of double precision numbers exponents.
                 */
                double estim = Math.pow( x.doubleValue(),q.doubleValue() ) ;
                BigDecimal res = new BigDecimal(estim) ;

                /* The error in x^q is q*x^(q-1)*Delta(x).
                 * The relative error is q*Delta(x)/x, q times the relative error of x.
                 */
                BigDecimal reserr = new BigDecimal( 0.5* q.abs().doubleValue()
                        * x.ulp().divide(x.abs(),MathContext.DECIMAL64).doubleValue() ) ;

                /* The main point in branching the cases above is that this conversion
                 * will succeed for numerator and denominator of q.
                 */
                int qa = q.a.intValue() ;
                int qb = q.b.intValue() ;

                /* Newton iterations. */
                BigDecimal xpowa = powRound(x, qa) ;
                for( ;; )
                {
                    /* numerator and denominator of the Newton term.  The major
                     * disadvantage of this implementation is that the updates of the powers
                     * of the new estimate are done in full precision calling BigDecimal.pow(),
                     * which becomes slow if the denominator of q is large.
                     */
                    BigDecimal nu = res.pow(qb) .subtract(xpowa) ;
                    BigDecimal de = multiplyRound( res.pow(qb-1),q.b) ;

                    /* estimated correction */
                    BigDecimal eps = nu.divide(de,MathContext.DECIMAL64) ;

                    BigDecimal err = res.multiply(reserr,MathContext.DECIMAL64) ;
                    int precDiv = 2+err2prec(eps,err) ;
                    if ( precDiv <= 0 )
                    {
                        /* The case when the precision is already reached and any precision
                         * will do. */
                        eps = nu.divide(de,MathContext.DECIMAL32) ;
                    }
                    else
                    {
                        MathContext mc = new MathContext(precDiv) ;
                        eps = nu.divide(de,mc) ;
                    }

                    res = subtractRound(res,eps) ;
                    /* reached final precision if the relative error fell below reserr,
                     * |eps/res| < reserr
                     */
                    if ( eps.divide(res,MathContext.DECIMAL64).abs().compareTo(reserr) < 0 )
                    {
                        /* delete the bits of extra precision kept in this
                         * working copy.
                         */
                        MathContext mc = new MathContext(err2prec(reserr.doubleValue())) ;
                        return res.round(mc) ;
                    }
                }
            }
            else
            {
                /* The error in x^q is q*x^(q-1)*Delta(x) + Delta(q)*x^q*log(x).
                 * The relative error is q/x*Delta(x) + Delta(q)*log(x). Convert q to a floating point
                 * number such that its relative error becomes negligible: Delta(q)/q << Delta(x)/x/log(x) .
                 */
                int precq =  3+err2prec( (x.ulp().divide(x,MathContext.DECIMAL64)).doubleValue()
                        / Math.log(x.doubleValue()) ) ;
                MathContext mc = new MathContext(precq) ;

                /* Perform the actual calculation as exponentiation of two floating point numbers.
                 */
                return pow(x, q.BigDecimalValue(mc) ) ;
            }


        }
    } /* com.github.rccookie.math.BigDecimalMath.powRound */

    /** Trigonometric sine.
     * @param x The argument in radians.
     * @return sin(x) in the range -1 to 1.
     * @since 2009-06-01
     * @author Richard J. Mathar
     */
    static public BigDecimal sin(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0)
            return sin(x.negate()).negate() ;
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            /* reduce modulo 2pi
             */
            BigDecimal res = mod2pi(x) ;
            double errpi = 0.5*Math.abs(x.ulp().doubleValue()) ;
            MathContext mc = new MathContext( 2+err2prec(3.14159,errpi) ) ;
            BigDecimal p= pi(mc) ;
            mc = new MathContext( x.precision() ) ;
            if ( res.compareTo(p) > 0 )
            {
                /* pi<x<=2pi: sin(x)= - sin(x-pi)
                 */
                return sin(subtractRound(res,p)) .negate() ;
            }
            else if ( res.multiply(new BigDecimal("1.99999999999999999")).compareTo(p) > 0 )
            {
                /* pi/2<x<=pi: sin(x)= sin(pi-x)
                 */
                return sin(subtractRound(p,res)) ;
            }
            else
            {
                /* for the range 0<=x<Pi/2 one could use sin(2x)=2sin(x)cos(x)
                 * to split this further. Here, use the sine up to pi/4 and the cosine higher up.
                 */
                if ( res.multiply(new BigDecimal("4")).compareTo(p) > 0 )
                {
                    /* x>pi/4: sin(x) = cos(pi/2-x)
                     */
                    return cos( subtractRound(p.divide(new BigDecimal("2")),res) ) ;
                }
                else
                {
                    /* Simple Taylor expansion, sum_{i=1..infinity} (-1)^(..)res^(2i+1)/(2i+1)! */
                    BigDecimal resul = res ;

                    /* x^i */
                    BigDecimal xpowi = res ;

                    /* 2i+1 factorial */
                    BigInteger ifac = BigInteger.ONE ;

                    /* The error in the result is set by the error in x itself.
                     */
                    double xUlpDbl = res.ulp().doubleValue() ;

                    /* The error in the result is set by the error in x itself.
                     * We need at most k terms to squeeze x^(2k+1)/(2k+1)! below this value.
                     * x^(2k+1) < x.ulp; (2k+1)*log10(x) < -x.precision; 2k*log10(x)< -x.precision;
                     * 2k*(-log10(x)) > x.precision; 2k*log10(1/x) > x.precision
                     */
                    int k = (int)(res.precision()/Math.log10(1.0/res.doubleValue()))/2 ;
                    MathContext mcTay = new MathContext( err2prec(res.doubleValue(),xUlpDbl/k) ) ;
                    for(int i=1 ; ; i++)
                    {
                        /* TBD: at which precision will 2*i or 2*i+1 overflow?
                         */
                        ifac = ifac.multiply(new BigInteger(""+(2*i) ) ) ;
                        ifac = ifac.multiply( new BigInteger(""+(2*i+1)) ) ;
                        xpowi = xpowi.multiply(res).multiply(res).negate() ;
                        BigDecimal corr = xpowi.divide(new BigDecimal(ifac),mcTay) ;
                        resul = resul.add( corr ) ;
                        if ( corr.abs().doubleValue() < 0.5*xUlpDbl )
                            break ;
                    }
                    /* The error in the result is set by the error in x itself.
                     */
                    mc = new MathContext(res.precision() ) ;
                    return resul.round(mc) ;
                }
            }
        }
    } /* sin */

    /** Trigonometric cosine.
     * @param x The argument in radians.
     * @return cos(x) in the range -1 to 1.
     * @since 2009-06-01
     * @author Richard J. Mathar
     */
    static public BigDecimal cos(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0)
            return cos(x.negate());
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ONE ;
        else
        {
            /* reduce modulo 2pi
             */
            BigDecimal res = mod2pi(x) ;
            double errpi = 0.5*Math.abs(x.ulp().doubleValue()) ;
            MathContext mc = new MathContext( 2+err2prec(3.14159,errpi) ) ;
            BigDecimal p= pi(mc) ;
            mc = new MathContext( x.precision() ) ;
            if ( res.compareTo(p) > 0 )
            {
                /* pi<x<=2pi: cos(x)= - cos(x-pi)
                 */
                return cos( subtractRound(res,p)) .negate() ;
            }
            else if ( res.multiply(new BigDecimal("1.999999999999999999")).compareTo(p) > 0 )
            {
                /* pi/2<x<=pi: cos(x)= -cos(pi-x)
                 */
                return cos( subtractRound(p,res)).negate() ;
            }
            else
            {
                                /* for the range 0<=x<Pi/2 one could use cos(2x)= 1-2*sin^2(x)
                                * to split this further, or use the cos up to pi/4 and the sine higher up.
                                        throw new ProviderException("Not implemented: cosine ") ;
                                */
                if ( res.multiply(new BigDecimal("4")).compareTo(p) > 0 )
                {
                    /* x>pi/4: cos(x) = sin(pi/2-x)
                     */
                    return sin( subtractRound(p.divide(new BigDecimal("2")),res) ) ;
                }
                else
                {
                    /* Simple Taylor expansion, sum_{i=0..infinity} (-1)^(..)res^(2i)/(2i)! */
                    BigDecimal resul = BigDecimal.ONE ;

                    /* x^i */
                    BigDecimal xpowi = BigDecimal.ONE ;

                    /* 2i factorial */
                    BigInteger ifac = BigInteger.ONE ;

                    /* The absolute error in the result is the error in x^2/2 which is x times the error in x.
                     */
                    double xUlpDbl = 0.5*res.ulp().doubleValue()*res.doubleValue() ;

                    /* The error in the result is set by the error in x^2/2 itself, xUlpDbl.
                     * We need at most k terms to push x^(2k+1)/(2k+1)! below this value.
                     * x^(2k) < xUlpDbl; (2k)*log(x) < log(xUlpDbl);
                     */
                    int k = (int)(Math.log(xUlpDbl)/Math.log(res.doubleValue()) )/2 ;
                    MathContext mcTay = new MathContext( err2prec(1.,xUlpDbl/k) ) ;
                    for(int i=1 ; ; i++)
                    {
                        /* TBD: at which precision will 2*i-1 or 2*i overflow?
                         */
                        ifac = ifac.multiply(new BigInteger(""+(2*i-1) ) ) ;
                        ifac = ifac.multiply( new BigInteger(""+(2*i)) ) ;
                        xpowi = xpowi.multiply(res).multiply(res).negate() ;
                        BigDecimal corr = xpowi.divide(new BigDecimal(ifac),mcTay) ;
                        resul = resul.add( corr ) ;
                        if ( corr.abs().doubleValue() < 0.5*xUlpDbl )
                            break ;
                    }
                    /* The error in the result is governed by the error in x itself.
                     */
                    mc = new MathContext( err2prec(resul.doubleValue(),xUlpDbl) ) ;
                    return resul.round(mc) ;
                }
            }
        }
    } /* com.github.rccookie.math.BigDecimalMath.cos */

    /** The trigonometric tangent.
     * @param x the argument in radians.
     * @return the tan(x)
     * @author Richard J. Mathar
     */
    static public BigDecimal tan(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else if ( x.compareTo(BigDecimal.ZERO) < 0 )
        {
            return tan(x.negate()).negate() ;
        }
        else
        {
            /* reduce modulo pi
             */
            BigDecimal res = modpi(x) ;

            /* absolute error in the result is err(x)/cos^2(x) to lowest order
             */
            final double xDbl = res.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue()/2. ;
            final double eps = xUlpDbl/2./Math.pow(Math.cos(xDbl),2.) ;

            if ( xDbl > 0.8)
            {
                /* tan(x) = 1/cot(x) */
                BigDecimal co = cot(x) ;
                MathContext mc = new MathContext( err2prec(1./co.doubleValue(),eps) ) ;
                return BigDecimal.ONE.divide(co,mc) ;
            }
            else
            {
                final BigDecimal xhighpr = scalePrec(res,2) ;
                final BigDecimal xhighprSq = multiplyRound(xhighpr,xhighpr) ;

                BigDecimal resul = xhighpr.plus() ;

                /* x^(2i+1) */
                BigDecimal xpowi = xhighpr ;

                Bernoulli b = new Bernoulli() ;

                /* 2^(2i) */
                BigInteger fourn = new BigInteger("4") ;
                /* (2i)! */
                BigInteger fac = new BigInteger("2") ;

                for(int i= 2 ; ; i++)
                {
                    Rational f = b.at(2*i).abs() ;
                    fourn = fourn.shiftLeft(2) ;
                    fac = fac.multiply(new BigInteger(""+(2*i))).multiply(new BigInteger(""+(2*i-1))) ;
                    f = f.multiply(fourn).multiply(fourn.subtract(BigInteger.ONE)).divide(fac) ;
                    xpowi = multiplyRound(xpowi,xhighprSq) ;
                    BigDecimal c = multiplyRound(xpowi,f) ;
                    resul = resul.add(c) ;
                    if ( Math.abs(c.doubleValue()) < 0.1*eps)
                        break;
                }
                MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
                return resul.round(mc) ;
            }
        }
    } /* com.github.rccookie.math.BigDecimalMath.tan */

    /** The trigonometric co-tangent.
     * @param x the argument in radians.
     * @return the cot(x)
     * @since 2009-07-31
     * @author Richard J. Mathar
     */
    static public BigDecimal cot(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) == 0 )
        {
            throw new ArithmeticException("Cannot take cot of zero "+ x.toString() ) ;
        }
        else if ( x.compareTo(BigDecimal.ZERO) < 0 )
        {
            return cot(x.negate()).negate() ;
        }
        else
        {
            /* reduce modulo pi
             */
            BigDecimal res = modpi(x) ;

            /* absolute error in the result is err(x)/sin^2(x) to lowest order
             */
            final double xDbl = res.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue()/2. ;
            final double eps = xUlpDbl/2./Math.pow(Math.sin(xDbl),2.) ;

            final BigDecimal xhighpr = scalePrec(res,2) ;
            final BigDecimal xhighprSq = multiplyRound(xhighpr,xhighpr) ;

            MathContext mc = new MathContext( err2prec(xhighpr.doubleValue(),eps) ) ;
            BigDecimal resul = BigDecimal.ONE.divide(xhighpr,mc) ;

            /* x^(2i-1) */
            BigDecimal xpowi = xhighpr ;

            Bernoulli b = new Bernoulli() ;

            /* 2^(2i) */
            BigInteger fourn = new BigInteger("4") ;
            /* (2i)! */
            BigInteger fac = BigInteger.ONE ;

            for(int i= 1 ; ; i++)
            {
                Rational f = b.at(2*i) ;
                fac = fac.multiply(new BigInteger(""+(2*i))).multiply(new BigInteger(""+(2*i-1))) ;
                f = f.multiply(fourn).divide(fac) ;
                BigDecimal c = multiplyRound(xpowi,f) ;
                if ( i % 2 == 0 )
                    resul = resul.add(c) ;
                else
                    resul = resul.subtract(c) ;
                if ( Math.abs(c.doubleValue()) < 0.1*eps)
                    break;

                fourn = fourn.shiftLeft(2) ;
                xpowi = multiplyRound(xpowi,xhighprSq) ;
            }
            mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.cot */

    /** The inverse trigonometric sine.
     * @param x the argument.
     * @return the arcsin(x) in radians.
     * @author Richard J. Mathar
     */
    static public BigDecimal asin(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ONE) > 0 || x.compareTo(BigDecimal.ONE.negate()) < 0 )
        {
            throw new ArithmeticException("Out of range argument "+ x.toString() + " of asin") ;
        }
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else if ( x.compareTo(BigDecimal.ONE) == 0 )
        {
            /* arcsin(1) = pi/2
             */
            double errpi = Math.sqrt(x.ulp().doubleValue()) ;
            MathContext mc = new MathContext( err2prec(3.14159,errpi) ) ;
            return pi(mc).divide(new BigDecimal(2)) ;
        }
        else if ( x.compareTo(BigDecimal.ZERO) < 0 )
        {
            return asin(x.negate()).negate() ;
        }
        else if ( x.doubleValue() > 0.7)
        {
            final BigDecimal xCompl = BigDecimal.ONE.subtract(x) ;
            final double xDbl = x.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue()/2. ;
            final double eps = xUlpDbl/2./Math.sqrt(1.-Math.pow(xDbl,2.)) ;

            final BigDecimal xhighpr = scalePrec(xCompl,3) ;
            final BigDecimal xhighprV = divideRound(xhighpr,4) ;

            BigDecimal resul = BigDecimal.ONE ;

            /* x^(2i+1) */
            BigDecimal xpowi = BigDecimal.ONE ;

            /* i factorial */
            BigInteger ifacN = BigInteger.ONE ;
            BigInteger ifacD = BigInteger.ONE ;

            for(int i=1 ; ; i++)
            {
                ifacN = ifacN.multiply(new BigInteger(""+(2*i-1)) ) ;
                ifacD = ifacD.multiply(new BigInteger(""+i) ) ;
                if ( i == 1)
                    xpowi = xhighprV ;
                else
                    xpowi = multiplyRound(xpowi,xhighprV) ;
                BigDecimal c = divideRound( multiplyRound(xpowi,ifacN),
                        ifacD.multiply(new BigInteger(""+(2*i+1)) ) ) ;
                resul = resul.add(c) ;
                /* series started 1+x/12+... which yields an estimate of the sum's error
                 */
                if ( Math.abs(c.doubleValue()) < xUlpDbl/120.)
                    break;
            }
            /* sqrt(2*z)*(1+...)
             */
            xpowi = sqrt(xhighpr.multiply(new BigDecimal(2))) ;
            resul = multiplyRound(xpowi,resul) ;

            MathContext mc = new MathContext( resul.precision() ) ;
            BigDecimal pihalf = pi(mc).divide(new BigDecimal(2)) ;

            mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return pihalf.subtract(resul,mc) ;
        }
        else
        {
            /* absolute error in the result is err(x)/sqrt(1-x^2) to lowest order
             */
            final double xDbl = x.doubleValue() ;
            final double xUlpDbl = x.ulp().doubleValue()/2. ;
            final double eps = xUlpDbl/2./Math.sqrt(1.-Math.pow(xDbl,2.)) ;

            final BigDecimal xhighpr = scalePrec(x,2) ;
            final BigDecimal xhighprSq = multiplyRound(xhighpr,xhighpr) ;

            BigDecimal resul = xhighpr.plus() ;

            /* x^(2i+1) */
            BigDecimal xpowi = xhighpr ;

            /* i factorial */
            BigInteger ifacN = BigInteger.ONE ;
            BigInteger ifacD = BigInteger.ONE ;

            for(int i=1 ; ; i++)
            {
                ifacN = ifacN.multiply(new BigInteger(""+(2*i-1)) ) ;
                ifacD = ifacD.multiply(new BigInteger(""+(2*i)) ) ;
                xpowi = multiplyRound(xpowi,xhighprSq) ;
                BigDecimal c = divideRound( multiplyRound(xpowi,ifacN),
                        ifacD.multiply(new BigInteger(""+(2*i+1)) ) ) ;
                resul = resul.add(c) ;
                if ( Math.abs(c.doubleValue()) < 0.1*eps)
                    break;
            }
            MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.asin */

    /** The inverse trigonometric cosine.
     * @param x the argument.
     * @return the arccos(x) in radians.
     * @since 2009-09-29
     * @author Richard J. Mathar
     */
    static public BigDecimal acos(final BigDecimal x)
    {
        /* Essentially forwarded to pi/2 - asin(x)
         */
        final BigDecimal xhighpr = scalePrec(x,2) ;
        BigDecimal resul = asin(xhighpr) ;
        double eps = resul.ulp().doubleValue()/2. ;

        MathContext mc = new MathContext( err2prec(3.14159,eps) ) ;
        BigDecimal pihalf = pi(mc).divide(new BigDecimal(2)) ;
        resul = pihalf.subtract(resul) ;

        /* absolute error in the result is err(x)/sqrt(1-x^2) to lowest order
         */
        final double xDbl = x.doubleValue() ;
        final double xUlpDbl = x.ulp().doubleValue()/2. ;
        eps = xUlpDbl/2./Math.sqrt(1.-Math.pow(xDbl,2.)) ;

        mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
        return resul.round(mc) ;

    } /* com.github.rccookie.math.BigDecimalMath.acos */

    /** The inverse trigonometric tangent.
     * @param x the argument.
     * @return the principal value of arctan(x) in radians in the range -pi/2 to +pi/2.
     * @since 2009-08-03
     * @author Richard J. Mathar
     */
    static public BigDecimal atan(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
        {
            return atan(x.negate()).negate() ;
        }
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else if ( x.doubleValue() >0.7 && x.doubleValue() <3.0)
        {
            /* Abramowitz-Stegun 4.4.34 convergence acceleration
             * 2*arctan(x) = arctan(2x/(1-x^2)) = arctan(y).  x=(sqrt(1+y^2)-1)/y
             * This maps 0<=y<=3 to 0<=x<=0.73 roughly. Temporarily with 2 protectionist digits.
             */
            BigDecimal y = scalePrec(x,2) ;
            BigDecimal newx = divideRound( hypot(1,y).subtract(BigDecimal.ONE) , y);

            /* intermediate result with too optimistic error estimate*/
            BigDecimal resul = multiplyRound( atan(newx), 2) ;

            /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp. */
            double eps = x.ulp().doubleValue()/( 2.0*Math.hypot(1.0,x.doubleValue()) ) ;
            MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
        else if ( x.doubleValue() < 0.71 )
        {
            /* Taylor expansion around x=0; Abramowitz-Stegun 4.4.42 */

            final BigDecimal xhighpr = scalePrec(x,2) ;
            final BigDecimal xhighprSq = multiplyRound(xhighpr,xhighpr).negate() ;

            BigDecimal resul = xhighpr.plus() ;

            /* signed x^(2i+1) */
            BigDecimal xpowi = xhighpr ;

            /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp.
             */
            double eps = x.ulp().doubleValue()/( 2.0*Math.hypot(1.0,x.doubleValue()) ) ;

            for(int i= 1 ; ; i++)
            {
                xpowi = multiplyRound(xpowi,xhighprSq) ;
                BigDecimal c = divideRound(xpowi,2*i+1) ;

                resul = resul.add(c) ;
                if ( Math.abs(c.doubleValue()) < 0.1*eps)
                    break;
            }
            MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
        else
        {
            /* Taylor expansion around x=infinity; Abramowitz-Stegun 4.4.42 */

            /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp.
             */
            double eps = x.ulp().doubleValue()/( 2.0*Math.hypot(1.0,x.doubleValue()) ) ;

            /* start with the term pi/2; gather its precision relative to the expected result
             */
            MathContext mc = new MathContext( 2+err2prec(3.1416,eps) ) ;
            BigDecimal onepi= pi(mc) ;
            BigDecimal resul = onepi.divide(new BigDecimal(2)) ;

            final BigDecimal xhighpr = divideRound(-1,scalePrec(x,2)) ;
            final BigDecimal xhighprSq = multiplyRound(xhighpr,xhighpr).negate() ;

            /* signed x^(2i+1) */
            BigDecimal xpowi = xhighpr ;

            for(int i= 0 ; ; i++)
            {
                BigDecimal c = divideRound(xpowi,2*i+1) ;

                resul = resul.add(c) ;
                if ( Math.abs(c.doubleValue()) < 0.1*eps)
                    break;
                xpowi = multiplyRound(xpowi,xhighprSq) ;
            }
            mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
            return resul.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.atan */

    /** The hyperbolic cosine.
     * @param x The argument.
     * @return The cosh(x) = (exp(x)+exp(-x))/2 .
     * @author Richard J. Mathar
     * @since 2009-08-19
     * @since 2015-02-09 corrected result for negative arguments.
     */
    static public BigDecimal cosh(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0)
            return cosh(x.negate());
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ONE ;
        else
        {
            if ( x.doubleValue() > 1.5 )
            {
                /* cosh^2(x) = 1+ sinh^2(x).
                 */
                return hypot(1, sinh(x) ) ;
            }
            else
            {
                BigDecimal xhighpr = scalePrec(x,2) ;
                /* Simple Taylor expansion, sum_{0=1..infinity} x^(2i)/(2i)! */
                BigDecimal resul = BigDecimal.ONE ;

                /* x^i */
                BigDecimal xpowi = BigDecimal.ONE ;

                /* 2i factorial */
                BigInteger ifac = BigInteger.ONE ;

                /* The absolute error in the result is the error in x^2/2 which is x times the error in x.
                 */
                double xUlpDbl = 0.5*x.ulp().doubleValue()*x.doubleValue() ;

                /* The error in the result is set by the error in x^2/2 itself, xUlpDbl.
                 * We need at most k terms to push x^(2k)/(2k)! below this value.
                 * x^(2k) < xUlpDbl; (2k)*log(x) < log(xUlpDbl);
                 */
                int k = (int)(Math.log(xUlpDbl)/Math.log(x.doubleValue()) )/2 ;

                /* The individual terms are all smaller than 1, so an estimate of 1.0 for
                 * the absolute value will give a safe relative error estimate for the indivdual terms
                 */
                MathContext mcTay = new MathContext( err2prec(1.,xUlpDbl/k) ) ;
                for(int i=1 ; ; i++)
                {
                    /* TBD: at which precision will 2*i-1 or 2*i overflow?
                     */
                    ifac = ifac.multiply(new BigInteger(""+(2*i-1) ) ) ;
                    ifac = ifac.multiply( new BigInteger(""+(2*i)) ) ;
                    xpowi = xpowi.multiply(xhighpr).multiply(xhighpr) ;
                    BigDecimal corr = xpowi.divide(new BigDecimal(ifac),mcTay) ;
                    resul = resul.add( corr ) ;
                    if ( corr.abs().doubleValue() < 0.5*xUlpDbl )
                        break ;
                }
                /* The error in the result is governed by the error in x itself.
                 */
                MathContext mc = new MathContext( err2prec(resul.doubleValue(),xUlpDbl) ) ;
                return resul.round(mc) ;
            }
        }
    } /* com.github.rccookie.math.BigDecimalMath.cosh */

    /** The hyperbolic sine.
     * @param x the argument.
     * @return the sinh(x) = (exp(x)-exp(-x))/2 .
     * @author Richard J. Mathar
     * @since 2009-08-19
     */
    static public BigDecimal sinh(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0)
            return sinh(x.negate()).negate() ;
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            if ( x.doubleValue() > 2.4 )
            {
                /* Move closer to zero with sinh(2x)= 2*sinh(x)*cosh(x).
                 */
                BigDecimal two = new BigDecimal(2) ;
                BigDecimal xhalf = x.divide(two) ;
                BigDecimal resul =  sinh(xhalf).multiply(cosh(xhalf)).multiply(two) ;
                /* The error in the result is set by the error in x itself.
                 * The first derivative of sinh(x) is cosh(x), so the absolute error
                 * in the result is cosh(x)*errx, and the relative error is coth(x)*errx = errx/tanh(x)
                 */
                double eps =  Math.tanh(x.doubleValue()) ;
                MathContext mc = new MathContext( err2prec(0.5*x.ulp().doubleValue()/eps) ) ;
                return resul.round(mc) ;
            }
            else
            {
                BigDecimal xhighpr = scalePrec(x,2) ;
                /* Simple Taylor expansion, sum_{i=0..infinity} x^(2i+1)/(2i+1)! */
                BigDecimal resul = xhighpr ;

                /* x^i */
                BigDecimal xpowi = xhighpr ;

                /* 2i+1 factorial */
                BigInteger ifac = BigInteger.ONE ;

                /* The error in the result is set by the error in x itself.
                 */
                double xUlpDbl = x.ulp().doubleValue() ;

                /* The error in the result is set by the error in x itself.
                 * We need at most k terms to squeeze x^(2k+1)/(2k+1)! below this value.
                 * x^(2k+1) < x.ulp; (2k+1)*log10(x) < -x.precision; 2k*log10(x)< -x.precision;
                 * 2k*(-log10(x)) > x.precision; 2k*log10(1/x) > x.precision
                 */
                int k = (int)(x.precision()/Math.log10(1.0/xhighpr.doubleValue()))/2 ;
                MathContext mcTay = new MathContext( err2prec(x.doubleValue(),xUlpDbl/k) ) ;
                for(int i=1 ; ; i++)
                {
                    /* TBD: at which precision will 2*i or 2*i+1 overflow?
                     */
                    ifac = ifac.multiply(new BigInteger(""+(2*i) ) ) ;
                    ifac = ifac.multiply( new BigInteger(""+(2*i+1)) ) ;
                    xpowi = xpowi.multiply(xhighpr).multiply(xhighpr) ;
                    BigDecimal corr = xpowi.divide(new BigDecimal(ifac),mcTay) ;
                    resul = resul.add( corr ) ;
                    if ( corr.abs().doubleValue() < 0.5*xUlpDbl )
                        break ;
                }
                /* The error in the result is set by the error in x itself.
                 */
                MathContext mc = new MathContext(x.precision() ) ;
                return resul.round(mc) ;
            }
        }
    } /* com.github.rccookie.math.BigDecimalMath.sinh */

    /** The hyperbolic tangent.
     * @param x The argument.
     * @return The tanh(x) = sinh(x)/cosh(x).
     * @author Richard J. Mathar
     * @since 2009-08-20
     */
    static public BigDecimal tanh(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) < 0)
            return tanh(x.negate()).negate() ;
        else if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            BigDecimal xhighpr = scalePrec(x,2) ;

            /* tanh(x) = (1-e^(-2x))/(1+e^(-2x)) .
             */
            BigDecimal exp2x = exp( xhighpr.multiply(new BigDecimal(-2)) ) ;

            /* The error in tanh x is err(x)/cosh^2(x).
             */
            double eps = 0.5*x.ulp().doubleValue()/Math.pow( Math.cosh(x.doubleValue()), 2.0 ) ;
            MathContext mc = new MathContext( err2prec(Math.tanh(x.doubleValue()),eps) ) ;
            return BigDecimal.ONE.subtract(exp2x).divide( BigDecimal.ONE.add(exp2x), mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.tanh */

    /** The inverse hyperbolic sine.
     * @param x The argument.
     * @return The arcsinh(x) .
     * @author Richard J. Mathar
     * @since 2009-08-20
     */
    static public BigDecimal asinh(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            BigDecimal xhighpr = scalePrec(x,2) ;

            /* arcsinh(x) = log(x+hypot(1,x))
             */
            BigDecimal logx = log(hypot(1,xhighpr).add(xhighpr)) ;

            /* The absolute error in arcsinh x is err(x)/sqrt(1+x^2)
             */
            double xDbl = x.doubleValue() ;
            double eps = 0.5*x.ulp().doubleValue()/Math.hypot(1.,xDbl ) ;
            MathContext mc = new MathContext( err2prec(logx.doubleValue(),eps) ) ;
            return logx.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.asinh */

    /** The inverse hyperbolic cosine.
     * @param x The argument.
     * @return The arccosh(x) .
     * @author Richard J. Mathar
     * @since 2009-08-20
     */
    static public BigDecimal acosh(final BigDecimal x)
    {
        if ( x.compareTo(BigDecimal.ONE) < 0 )
            throw new ArithmeticException("Out of range argument cosh "+x.toString() ) ;
        else if ( x.compareTo(BigDecimal.ONE) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            BigDecimal xhighpr = scalePrec(x,2) ;

            /* arccosh(x) = log(x+sqrt(x^2-1))
             */
            BigDecimal logx = log( sqrt(xhighpr.pow(2).subtract(BigDecimal.ONE) ) .add(xhighpr)) ;

            /* The absolute error in arcsinh x is err(x)/sqrt(x^2-1)
             */
            double xDbl = x.doubleValue() ;
            double eps = 0.5*x.ulp().doubleValue()/Math.sqrt(xDbl*xDbl-1.) ;
            MathContext mc = new MathContext( err2prec(logx.doubleValue(),eps) ) ;
            return logx.round(mc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.acosh */

    /** The Gamma function.
     * @param x The argument.
     * @return Gamma(x).
     * @since 2009-08-06
     * @author Richard J. Mathar
     */
    static public BigDecimal Gamma(final BigDecimal x)
    {
        /* reduce to interval near 1.0 with the functional relation, Abramowitz-Stegun 6.1.33
         */
        if ( x.compareTo(BigDecimal.ZERO) < 0 )
            return divideRound(Gamma( x.add(BigDecimal.ONE) ),x) ;
        else if ( x.doubleValue() > 1.5 )
        {
            /* Gamma(x) = Gamma(xmin+n) = Gamma(xmin)*Pochhammer(xmin,n).
             */
            int n = (int) ( x.doubleValue()-0.5 );
            BigDecimal xmin1 = x.subtract(new BigDecimal(n)) ;
            return multiplyRound(Gamma(xmin1), pochhammer(xmin1,n) ) ;
        }
        else
        {
            /* apply Abramowitz-Stegun 6.1.33
             */
            BigDecimal z = x.subtract(BigDecimal.ONE) ;

            /* add intermediately 2 digits to the partial sum accumulation
             */
            z = scalePrec(z,2) ;
            MathContext mcloc = new MathContext(z.precision()) ;

            /* measure of the absolute error is the relative error in the first, logarithmic term
             */
            double eps = x.ulp().doubleValue()/x.doubleValue() ;

            BigDecimal resul = log( scalePrec(x,2)).negate() ;

            if ( x.compareTo(BigDecimal.ONE) != 0 )
            {

                BigDecimal gammCompl = BigDecimal.ONE.subtract(gamma(mcloc) ) ;
                resul = resul.add( multiplyRound(z,gammCompl) ) ;
                for(int n=2; ;n++)
                {
                    /* multiplying z^n/n by zeta(n-1) means that the two relative errors add.
                     * so the requirement in the relative error of zeta(n)-1 is that this is somewhat
                     * smaller than the relative error in z^n/n (the absolute error of thelatter  is the
                     * absolute error in z)
                     */
                    BigDecimal c = divideRound(z.pow(n,mcloc),n) ;
                    MathContext m = new MathContext( err2prec(n*z.ulp().doubleValue()/2./z.doubleValue()) ) ;
                    c = c.round(m) ;

                    /* At larger n, zeta(n)-1 is roughly 1/2^n. The product is c/2^n.
                     * The relative error in c is c.ulp/2/c . The error in the product should be small versus eps/10.
                     * Error from 1/2^n is c*err(sigma-1).
                     * We need a relative error of zeta-1 of the order of c.ulp/50/c. This is an absolute
                     * error in zeta-1 of c.ulp/50/c/2^n, and also the absolute error in zeta, because zeta is
                     * of the order of 1.
                     */
                    if ( eps/100./c.doubleValue() < 0.01 )
                        m = new MathContext( err2prec(eps/100./c.doubleValue()) ) ;
                    else
                        m = new MathContext( 2) ;
                    /* zeta(n) -1 */
                    BigDecimal zetm1 = zeta(n,m).subtract(BigDecimal.ONE) ;
                    c = multiplyRound(c,zetm1) ;

                    if ( n % 2 == 0 )
                        resul = resul.add(c) ;
                    else
                        resul = resul.subtract(c) ;

                    /* alternating sum, so truncating as eps is reached suffices
                     */
                    if ( Math.abs(c.doubleValue()) < eps)
                        break;
                }
            }

            /* The relative error in the result is the absolute error in the
             * input variable times the digamma (psi) value at that point.
             */
            double zdbl = z.doubleValue() ;
            eps = psi(zdbl)* x.ulp().doubleValue()/2. ;
            mcloc = new MathContext( err2prec(eps) ) ;
            return exp(resul).round(mcloc) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.gamma */

    /** The Gamma function.
     * @param q The argument.
     * @param mc The required accuracy in the result.
     * @return Gamma(x).
     * @since 2010-05-26
     * @author Richard J. Mathar
     */
    static public BigDecimal Gamma(final Rational q, final MathContext mc)
    {
        if ( q.isBigInteger() )
        {
            if ( q.compareTo(Rational.ZERO) <= 0 )
                throw new ArithmeticException("Gamma at "+q.toString() ) ;
            else
            {
                /* Gamma(n) = (n-1)! */
                Factorial f = new Factorial() ;
                BigInteger g = f.at( q.trunc().intValue()-1 ) ;
                return scalePrec(new BigDecimal(g),mc) ;
            }
        }
        else if ( q.b.intValue() == 2 )
        {
            /* half integer cases which are related to sqrt(pi)
             */
            BigDecimal p = sqrt(pi(mc)) ;
            if ( q.compareTo(Rational.ZERO) >= 0 )
            {
                Rational pro = Rational.ONE ;
                Rational f = q.subtract(1) ;
                while ( f.compareTo(Rational.ZERO) > 0 )
                {
                    pro = pro.multiply(f) ;
                    f = f.subtract(1) ;
                }
                return multiplyRound(p,pro) ;
            }
            else
            {
                Rational pro = Rational.ONE ;
                Rational f = q ;
                while ( f.compareTo(Rational.ZERO) < 0 )
                {
                    pro = pro.divide(f) ;
                    f = f.add(1) ;
                }
                return multiplyRound(p,pro) ;
            }
        }
        else
        {
            /* The relative error of the result is psi(x)*Delta(x). Tune Delta(x) such
             * that this is equivalent to mc: Delta(x) = precision/psi(x).
             */
            double qdbl = q.doubleValue() ;
            double deltx = 5.*Math.pow(10.,-mc.getPrecision()) /psi(qdbl) ;

            MathContext mcx  = new MathContext( err2prec(qdbl,deltx) ) ;
            BigDecimal x =  q.BigDecimalValue(mcx) ;

            /* forward calculation to the general floating point case */
            return Gamma(x) ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.Gamma */

    /** Pochhammer's  function.
     * @param x The main argument.
     * @param n The non-negative index.
     * @return (x)_n = x(x+1)(x+2)*...*(x+n-1).
     * @since 2009-08-19
     * @author Richard J. Mathar
     */
    static public BigDecimal pochhammer(final BigDecimal x, final int n)
    {
        /* reduce to interval near 1.0 with the functional relation, Abramowitz-Stegun 6.1.33
         */
        if ( n < 0 )
            throw new ProviderException("Not implemented: pochhammer with negative index "+n) ;
        else if ( n == 0 )
            return BigDecimal.ONE ;
        else
        {
            /* internally two safety digits
             */
            BigDecimal xhighpr = scalePrec(x,2) ;
            BigDecimal resul = xhighpr ;

            double xUlpDbl = x.ulp().doubleValue() ;
            double xDbl = x.doubleValue() ;
            /* relative error of the result is the sum of the relative errors of the factors
             */
            double eps = 0.5*xUlpDbl/Math.abs(xDbl) ;
            for (int i =1 ; i < n ; i++)
            {
                eps += 0.5*xUlpDbl/Math.abs(xDbl+i) ;
                resul = resul.multiply( xhighpr.add(new BigDecimal(i)) ) ;
                final MathContext mcloc = new MathContext(4+ err2prec(eps) ) ;
                resul = resul.round(mcloc) ;
            }
            return resul.round(new MathContext(err2prec(eps)) )  ;
        }
    } /* com.github.rccookie.math.BigDecimalMath.pochhammer */

    /** Reduce value to the interval [0,2*Pi].
     * @param x the original value
     * @return the value modulo 2*pi in the interval from 0 to 2*pi.
     * @since 2009-06-01
     * @author Richard J. Mathar
     */
    static public BigDecimal mod2pi(BigDecimal x)
    {
        /* write x= 2*pi*k+r with the precision in r defined by the precision of x and not
         * compromised by the precision of 2*pi, so the ulp of 2*pi*k should match the ulp of x.
         * First get a guess of k to figure out how many digits of 2*pi are needed.
         */
        int k = (int)(0.5*x.doubleValue()/Math.PI) ;

        /* want to have err(2*pi*k)< err(x)=0.5*x.ulp, so err(pi) = err(x)/(4k) with two safety digits
         */
        double err2pi ;
        if ( k != 0 )
            err2pi = 0.25*Math.abs(x.ulp().doubleValue()/k) ;
        else
            err2pi = 0.5*Math.abs(x.ulp().doubleValue()) ;
        MathContext mc = new MathContext( 2+err2prec(6.283,err2pi) ) ;
        BigDecimal twopi= pi(mc).multiply(new BigDecimal(2)) ;

        /* Delegate the actual operation to the BigDecimal class, which may return
         * a negative value of x was negative .
         */
        BigDecimal res = x.remainder(twopi) ;
        if ( res.compareTo(BigDecimal.ZERO) < 0 )
            res =  res.add(twopi) ;

        /* The actual precision is set by the input value, its absolute value of x.ulp()/2.
         */
        mc = new MathContext( err2prec(res.doubleValue(),x.ulp().doubleValue()/2.) ) ;
        return res.round(mc) ;
    } /* mod2pi */

    /** Reduce value to the interval [-Pi/2,Pi/2].
     * @param x The original value
     * @return The value modulo pi, shifted to the interval from -Pi/2 to Pi/2.
     * @since 2009-07-31
     * @author Richard J. Mathar
     */
    static public BigDecimal modpi(BigDecimal x)
    {
        /* write x= pi*k+r with the precision in r defined by the precision of x and not
         * compromised by the precision of pi, so the ulp of pi*k should match the ulp of x.
         * First get a guess of k to figure out how many digits of pi are needed.
         */
        int k = (int)(x.doubleValue()/Math.PI) ;

        /* want to have err(pi*k)< err(x)=x.ulp/2, so err(pi) = err(x)/(2k) with two safety digits
         */
        double errpi ;
        if ( k != 0 )
            errpi = 0.5*Math.abs(x.ulp().doubleValue()/k) ;
        else
            errpi = 0.5*Math.abs(x.ulp().doubleValue()) ;
        MathContext mc = new MathContext( 2+err2prec(3.1416,errpi) ) ;
        BigDecimal onepi= pi(mc) ;
        BigDecimal pihalf = onepi.divide(new BigDecimal(2)) ;

        /* Delegate the actual operation to the BigDecimal class, which may return
         * a negative value of x was negative .
         */
        BigDecimal res = x.remainder(onepi) ;
        if ( res.compareTo(pihalf) > 0 )
            res =  res.subtract(onepi) ;
        else if ( res.compareTo(pihalf.negate()) < 0 )
            res =  res.add(onepi) ;

        /* The actual precision is set by the input value, its absolute value of x.ulp()/2.
         */
        mc = new MathContext( err2prec(res.doubleValue(),x.ulp().doubleValue()/2.) ) ;
        return res.round(mc) ;
    } /* modpi */

    /** Riemann zeta function.
     * @param n The positive integer argument.
     * @param mc Specification of the accuracy of the result.
     * @return zeta(n).
     * @since 2009-08-05
     * @author Richard J. Mathar
     */
    static public BigDecimal zeta(final int n, final MathContext mc)
    {
        if( n <= 0 )
            throw new ProviderException("Not implemented: zeta at negative argument "+n) ;
        if( n == 1 )
            throw new ArithmeticException("Pole at zeta(1) ") ;

        if( n % 2 == 0 )
        {
            /* Even indices. Abramowitz-Stegun 23.2.16. Start with 2^(n-1)*B(n)/n!
             */
            Rational b = (new Bernoulli()).at(n).abs() ;
            b = b.divide((new Factorial()).at(n)) ;
            b = b.multiply( BigInteger.ONE.shiftLeft(n-1) );

            /* to be multiplied by pi^n. Absolute error in the result of pi^n is n times
             * error in pi times pi^(n-1). Relative error is n*error(pi)/pi, requested by mc.
             * Need one more digit in pi if n=10, two digits if n=100 etc, and add one extra digit.
             */
            MathContext mcpi = new MathContext( mc.getPrecision() + (int)(Math.log10(10.0*n)) ) ;
            final BigDecimal piton = pi(mcpi).pow(n,mc) ;
            return multiplyRound( piton, b) ;
        }
        else if ( n == 3)
        {
            /* Broadhurst BBP <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             * Error propagation: S31 is roughly 0.087, S33 roughly 0.131
             */
            int[] a31 = {1,-7,-1,10,-1,-7,1,0} ;
            int[] a33 = {1,1,-1,-2,-1,1,1,0} ;
            BigDecimal S31 = broadhurstBBP(3,1,a31,mc) ;
            BigDecimal S33 = broadhurstBBP(3,3,a33,mc) ;
            S31 = S31.multiply(new BigDecimal(48)) ;
            S33 = S33.multiply(new BigDecimal(32)) ;
            return S31.add(S33).divide(new BigDecimal(7),mc) ;
        }
        else if ( n == 5)
        {
            /* Broadhurst BBP <a href=http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             * Error propagation: S51 is roughly -11.15, S53 roughly 22.165, S55 is roughly 0.031
             * 9*2048*S51/6265 = -3.28. 7*2038*S53/61651= 5.07. 738*2048*S55/61651= 0.747.
             * The result is of the order 1.03, so we add 2 digits to S51 and S52 and one digit to S55.
             */
            int[] a51 = {31,-1614,-31,-6212,-31,-1614,31,74552} ;
            int[] a53 = {173,284,-173,-457,-173,284,173,-111} ;
            int[] a55 = {1,0,-1,-1,-1,0,1,1} ;
            BigDecimal S51 = broadhurstBBP(5,1,a51, new MathContext(2+mc.getPrecision()) ) ;
            BigDecimal S53 = broadhurstBBP(5,3,a53, new MathContext(2+mc.getPrecision()) ) ;
            BigDecimal S55 = broadhurstBBP(5,5,a55, new MathContext(1+mc.getPrecision()) ) ;
            S51 = S51.multiply(new BigDecimal(18432)) ;
            S53 = S53.multiply(new BigDecimal(14336)) ;
            S55 = S55.multiply(new BigDecimal(1511424)) ;
            return S51.add(S53).subtract(S55).divide(new BigDecimal(62651),mc) ;
        }
        else
        {
            /* Cohen et al Exp Math 1 (1) (1992) 25
             */
            Rational betsum = new Rational() ;
            Bernoulli bern = new Bernoulli() ;
            Factorial fact = new Factorial() ;
            for(int npr=0 ; npr <= (n+1)/2 ; npr++)
            {
                Rational b = bern.at(2*npr).multiply(bern.at(n+1-2*npr)) ;
                b = b.divide(fact.at(2*npr)).divide(fact.at(n+1-2*npr)) ;
                b = b.multiply(1-2*npr) ;
                if ( npr % 2 ==0 )
                    betsum = betsum.add(b) ;
                else
                    betsum = betsum.subtract(b) ;
            }
            betsum = betsum.divide(n-1) ;
            /* The first term, including the facor (2pi)^n, is essentially most
             * of the result, near one. The second term below is roughly in the range 0.003 to 0.009.
             * So the precision here is matching the precisionn requested by mc, and the precision
             * requested for 2*pi is in absolute terms adjusted.
             */
            MathContext mcloc = new MathContext( 2+mc.getPrecision() + (int)(Math.log10((double)(n))) ) ;
            BigDecimal ftrm = pi(mcloc).multiply(new BigDecimal(2)) ;
            ftrm = ftrm.pow(n) ;
            ftrm = multiplyRound(ftrm, betsum.BigDecimalValue(mcloc) ) ;
            BigDecimal exps = new BigDecimal(0) ;

            /* the basic accuracy of the accumulated terms before multiplication with 2
             */
            double eps = Math.pow(10.,-mc.getPrecision()) ;

            if ( n % 4 == 3)
            {
                /* since the argument n is at least 7 here, the drop
                 * of the terms is at rather constant pace at least 10^-3, for example
                 * 0.0018, 0.2e-7, 0.29e-11, 0.74e-15 etc for npr=1,2,3.... We want 2 times these terms
                 * fall below eps/10.
                 */
                int kmax = mc.getPrecision()/3 ;
                eps /= kmax ;
                /* need an error of eps for 2/(exp(2pi)-1) = 0.0037
                 * The absolute error is 4*exp(2pi)*err(pi)/(exp(2pi)-1)^2=0.0075*err(pi)
                 */
                BigDecimal exp2p = pi( new MathContext(3+err2prec(3.14, eps/0.0075)) ) ;
                exp2p = exp(exp2p.multiply(new BigDecimal(2))) ;
                BigDecimal c =  exp2p.subtract(BigDecimal.ONE) ;
                exps = divideRound(1,c) ;
                for(int npr=2 ; npr<= kmax ; npr++)
                {
                    /* the error estimate above for npr=1 is the worst case of
                     * the absolute error created by an error in 2pi. So we can
                     * safely re-use the exp2p value computed above without
                     * reassessment of its error.
                     */
                    c =  powRound(exp2p,npr).subtract(BigDecimal.ONE) ;
                    c =  multiplyRound(c, (new BigInteger(""+npr)).pow(n) ) ;
                    c =  divideRound(1,c) ;
                    exps = exps.add(c) ;
                }
            }
            else
            {
                /* since the argument n is at least 9 here, the drop
                 * of the terms is at rather constant pace at least 10^-3, for example
                 * 0.0096, 0.5e-7, 0.3e-11, 0.6e-15 etc. We want these terms
                 * fall below eps/10.
                 */
                int kmax = (1+mc.getPrecision())/3 ;
                eps /= kmax ;
                /* need an error of eps for 2/(exp(2pi)-1)*(1+4*Pi/8/(1-exp(-2pi)) = 0.0096
                 * at k=7 or = 0.00766 at k=13 for example.
                 * The absolute error is 0.017*err(pi) at k=9, 0.013*err(pi) at k=13, 0.012 at k=17
                 */
                BigDecimal twop = pi( new MathContext(3+err2prec(3.14, eps/0.017)) ) ;
                twop = twop.multiply(new BigDecimal(2)) ;
                BigDecimal exp2p = exp(twop) ;
                BigDecimal c =  exp2p.subtract(BigDecimal.ONE) ;
                exps = divideRound(1,c) ;
                c =  BigDecimal.ONE.subtract(divideRound(1,exp2p)) ;
                c =  divideRound(twop,c).multiply(new BigDecimal(2)) ;
                c =  divideRound(c,n-1).add(BigDecimal.ONE) ;
                exps = multiplyRound(exps,c) ;
                for(int npr=2 ; npr<= kmax ; npr++)
                {
                    c =  powRound(exp2p,npr).subtract(BigDecimal.ONE) ;
                    c =  multiplyRound(c, (new BigInteger(""+npr)).pow(n) ) ;

                    BigDecimal d =  divideRound(1, exp2p.pow(npr) ) ;
                    d =  BigDecimal.ONE.subtract(d) ;
                    d =  divideRound(twop,d).multiply(new BigDecimal(2*npr)) ;
                    d =  divideRound(d,n-1).add(BigDecimal.ONE) ;

                    d = divideRound(d,c) ;

                    exps = exps.add(d) ;
                }
            }
            exps = exps.multiply(new BigDecimal(2)) ;
            return ftrm.subtract(exps,mc) ;
        }
    } /* zeta */

    /** Riemann zeta function.
     * @param n The positive integer argument.
     * @return zeta(n)-1.
     * @since 2009-08-20
     * @author Richard J. Mathar
     */
    static public double zeta1(final int n)
    {
        /* precomputed static table in double precision
         */
        final double[] zmin1 = {0.,0.,
                6.449340668482264364724151666e-01,
                2.020569031595942853997381615e-01,8.232323371113819151600369654e-02,
                3.692775514336992633136548646e-02,1.734306198444913971451792979e-02,
                8.349277381922826839797549850e-03,4.077356197944339378685238509e-03,
                2.008392826082214417852769232e-03,9.945751278180853371459589003e-04,
                4.941886041194645587022825265e-04,2.460865533080482986379980477e-04,
                1.227133475784891467518365264e-04,6.124813505870482925854510514e-05,
                3.058823630702049355172851064e-05,1.528225940865187173257148764e-05,
                7.637197637899762273600293563e-06,3.817293264999839856461644622e-06,
                1.908212716553938925656957795e-06,9.539620338727961131520386834e-07,
                4.769329867878064631167196044e-07,2.384505027277329900036481868e-07,
                1.192199259653110730677887189e-07,5.960818905125947961244020794e-08,
                2.980350351465228018606370507e-08,1.490155482836504123465850663e-08,
                7.450711789835429491981004171e-09,3.725334024788457054819204018e-09,
                1.862659723513049006403909945e-09,9.313274324196681828717647350e-10,
                4.656629065033784072989233251e-10,2.328311833676505492001455976e-10,
                1.164155017270051977592973835e-10,5.820772087902700889243685989e-11,
                2.910385044497099686929425228e-11,1.455192189104198423592963225e-11,
                7.275959835057481014520869012e-12,3.637979547378651190237236356e-12,
                1.818989650307065947584832101e-12,9.094947840263889282533118387e-13,
                4.547473783042154026799112029e-13,2.273736845824652515226821578e-13,
                1.136868407680227849349104838e-13,5.684341987627585609277182968e-14,
                2.842170976889301855455073705e-14,1.421085482803160676983430714e-14,
                7.105427395210852712877354480e-15,3.552713691337113673298469534e-15,
                1.776356843579120327473349014e-15,8.881784210930815903096091386e-16,
                4.440892103143813364197770940e-16,2.220446050798041983999320094e-16,
                1.110223025141066133720544570e-16,5.551115124845481243723736590e-17,
                2.775557562136124172581632454e-17,1.387778780972523276283909491e-17,
                6.938893904544153697446085326e-18,3.469446952165922624744271496e-18,
                1.734723476047576572048972970e-18,8.673617380119933728342055067e-19,
                4.336808690020650487497023566e-19,2.168404344997219785013910168e-19,
                1.084202172494241406301271117e-19,5.421010862456645410918700404e-20,
                2.710505431223468831954621312e-20,1.355252715610116458148523400e-20,
                6.776263578045189097995298742e-21,3.388131789020796818085703100e-21,
                1.694065894509799165406492747e-21,8.470329472546998348246992609e-22,
                4.235164736272833347862270483e-22,2.117582368136194731844209440e-22,
                1.058791184068023385226500154e-22,5.293955920339870323813912303e-23,
                2.646977960169852961134116684e-23,1.323488980084899080309451025e-23,
                6.617444900424404067355245332e-24,3.308722450212171588946956384e-24,
                1.654361225106075646229923677e-24,8.271806125530344403671105617e-25,
                4.135903062765160926009382456e-25,2.067951531382576704395967919e-25,
                1.033975765691287099328409559e-25,5.169878828456431320410133217e-26,
                2.584939414228214268127761771e-26,1.292469707114106670038112612e-26,
                6.462348535570531803438002161e-27,3.231174267785265386134814118e-27,
                1.615587133892632521206011406e-27,8.077935669463162033158738186e-28,
                4.038967834731580825622262813e-28,2.019483917365790349158762647e-28,
                1.009741958682895153361925070e-28,5.048709793414475696084771173e-29,
                2.524354896707237824467434194e-29,1.262177448353618904375399966e-29,
                6.310887241768094495682609390e-30,3.155443620884047239109841220e-30,
                1.577721810442023616644432780e-30,7.888609052210118073520537800e-31
        } ;
        if( n <= 0 )
            throw new ProviderException("Not implemented: zeta at negative argument "+n) ;
        if( n == 1 )
            throw new ArithmeticException("Pole at zeta(1) ") ;

        if( n < zmin1.length )
            /* look it up if available */
            return zmin1[n] ;
        else
        {
            /* Result is roughly 2^(-n), desired accuracy 18 digits. If zeta(n) is computed, the equivalent accuracy
             * in relative units is higher, because zeta is around 1.
             */
            double eps = 1.e-18*Math.pow(2.,(double)(-n) ) ;
            MathContext mc = new MathContext( err2prec(eps) ) ;
            return zeta(n,mc).subtract(BigDecimal.ONE).doubleValue() ;
        }
    } /* zeta */


    /** trigonometric cot.
     * @param x The argument.
     * @return cot(x) = 1/tan(x).
     * @author Richard J. Mathar
     */
    static public double cot(final double x)
    {
        return 1./Math.tan(x) ;
    }

    /** Digamma function.
     * @param x The main argument.
     * @return psi(x).
     *  The error is sometimes up to 10 ulp, where AS 6.3.15 suffers from cancellation of digits and psi=0
     * @since 2009-08-26
     * @author Richard J. Mathar
     */
    static public double psi(final double x)
    {
        /* the single positive zero of psi(x)
         */
        final double psi0 = 1.46163214496836234126265954232572132846819;
        if ( x > 2.0)
        {
            /* Reduce to a value near x=1 with the standard recurrence formula.
             * Abramowitz-Stegun 6.3.5
             */
            int m = (int) ( x-0.5 );
            double xmin1 = x-m ;
            double resul = 0. ;
            for(int i=1; i <= m ; i++)
                resul += 1./(x-i) ;
            return resul+psi(xmin1) ;
        }
        else if ( Math.abs(x-psi0) < 0.55)
        {
            /* Taylor approximation around the local zero
             */
            final double [] psiT0 = { 9.67672245447621170427e-01, -4.42763168983592106093e-01,
                    2.58499760955651010624e-01, -1.63942705442406527504e-01, 1.07824050691262365757e-01,
                    -7.21995612564547109261e-02, 4.88042881641431072251e-02, -3.31611264748473592923e-02,
                    2.25976482322181046596e-02, -1.54247659049489591388e-02, 1.05387916166121753881e-02,
                    -7.20453438635686824097e-03, 4.92678139572985344635e-03, -3.36980165543932808279e-03,
                    2.30512632673492783694e-03, -1.57693677143019725927e-03, 1.07882520191629658069e-03,
                    -7.38070938996005129566e-04, 5.04953265834602035177e-04, -3.45468025106307699556e-04,
                    2.36356015640270527924e-04, -1.61706220919748034494e-04, 1.10633727687474109041e-04,
                    -7.56917958219506591924e-05, 5.17857579522208086899e-05, -3.54300709476596063157e-05,
                    2.42400661186013176527e-05, -1.65842422718541333752e-05, 1.13463845846638498067e-05,
                    -7.76281766846209442527e-06, 5.31106092088986338732e-06, -3.63365078980104566837e-06,
                    2.48602273312953794890e-06, -1.70085388543326065825e-06, 1.16366753635488427029e-06,
                    -7.96142543124197040035e-07, 5.44694193066944527850e-07, -3.72661612834382295890e-07,
                    2.54962655202155425666e-07, -1.74436951177277452181e-07, 1.19343948298302427790e-07,
                    -8.16511518948840884084e-08, 5.58629968353217144428e-08, -3.82196006191749421243e-08,
                    2.61485769519618662795e-08, -1.78899848649114926515e-08, 1.22397314032336619391e-08,
                    -8.37401629767179054290e-09, 5.72922285984999377160e-09} ;
            final double xdiff = x-psi0 ;
            double resul = 0. ;
            for( int i = psiT0.length-1; i >=0 ; i--)
                resul = resul*xdiff+psiT0[i] ;
            return resul*xdiff ;
        }
        else if ( x < 0. )
        {
            /* Reflection formula */
            double xmin = 1.-x ;
            return psi(xmin) + Math.PI/Math.tan(Math.PI*xmin) ;
        }
        else
        {
            double xmin1 = x-1 ;
            double resul = 0. ;
            for(int k=26 ; k>= 1; k--)
            {
                resul -= zeta1(2*k+1) ;
                resul *= xmin1*xmin1 ;
            }
            /* 0.422... = 1 -gamma */
            return resul + 0.422784335098467139393487909917597568
                    + 0.5/xmin1-1./(1-xmin1*xmin1)- Math.PI/( 2.*Math.tan(Math.PI*xmin1) );
        }
    } /* psi */


    /** Broadhurst ladder sequence.
     * @param n
     * @param p
     * @param mc Specification of the accuracy of the result
     * @return S_(n,p)(a)
     * @since 2009-08-09
     * <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
     * @author Richard J. Mathar
     */
    static protected BigDecimal broadhurstBBP(final int n, final int p, final int a[], MathContext mc)
    {
        /* Explore the actual magnitude of the result first with a quick estimate.
         */
        double x = 0.0 ;
        for(int k=1; k < 10 ; k++)
            x += a[ (k-1) % 8]/Math.pow(2., p*(k+1)/2)/Math.pow((double)k,n) ;

        /* Convert the relative precision and estimate of the result into an absolute precision.
         */
        double eps = prec2err(x,mc.getPrecision()) ;

        /* Divide this through the number of terms in the sum to account for error accumulation
         * The divisor 2^(p(k+1)/2) means that on the average each 8th term in k has shrunk by
         * relative to the 8th predecessor by 1/2^(4p).  1/2^(4pc) = 10^(-precision) with c the 8term
         * cycles yields c=log_2( 10^precision)/4p = 3.3*precision/4p  with k=8c
         */
        int kmax= (int)(6.6*mc.getPrecision()/p) ;

        /* Now eps is the absolute error in each term */
        eps /= kmax ;
        BigDecimal res = BigDecimal.ZERO ;
        for(int c =0 ; ; c++)
        {
            Rational r = new Rational() ;
            for (int k=0; k < 8 ; k++)
            {
                Rational tmp = new Rational(new BigInteger(""+a[k]),(new BigInteger(""+(1+8*c+k))).pow(n)) ;
                /* floor( (pk+p)/2)
                 */
                int pk1h = p*(2+8*c+k)/2 ;
                tmp = tmp.divide( BigInteger.ONE.shiftLeft(pk1h) ) ;
                r = r.add(tmp) ;
            }

            if ( Math.abs(r.doubleValue()) < eps)
                break;
            MathContext mcloc = new MathContext( 1+err2prec(r.doubleValue(),eps) ) ;
            res = res.add( r.BigDecimalValue(mcloc) ) ;
        }
        return res.round(mc) ;
    } /* broadhurstBBP */







    /** Convert the finite representation of a floating point value to
     * its fraction.
     * @param x The number to be translated.
     * @return The rational number with the same decimal expansion as x.
     * @since 2012-03-09
     * @author Richard J. Mathar
     */
    public static Rational toRational(BigDecimal x)
    {
        /* represent the floating point number by the exact rational
         * variant of the current truncated representation
         */
        int s = x.scale() ;
        if ( s > 0)
            return new Rational( x.unscaledValue(), BigInteger.TEN.pow(s) ) ;
        else
            return new Rational( x.unscaledValue().multiply(BigInteger.TEN.pow(-s)), BigInteger.ONE) ;
    } /* toRational */

    /** Continued fraction.
     * @param x The number the absolute value of which will be decomposed.
     * @return A list of the form [a0,a1,a2,a3,...] where
     *  The decomposition is |x| = a0+1/(a1+1/(a2+1/(a3+...))).
     * @since 2012-03-09
     * @author Richard J. Mathar
     */
    public static Vector<BigInteger> cfrac(final BigDecimal x)
    {
        /* forward to the implementation in the com.github.rccookie.math.BigDecimalMath.Rational class
         */
        return toRational(x).cfrac() ;
    } /* cfrac */


    /** Add a BigDecimal and a BigInteger.
     * @param x The left summand
     * @param y The right summand
     * @return The sum x+y.
     * @since 2012-03-02
     * @author Richard J. Mathar
     */
    static public BigDecimal add(final BigDecimal x, final BigInteger y)
    {
        return x.add(new BigDecimal(y)) ;
    } /* add */


    /** Add and round according to the larger of the two ulp's.
     * @param x The left summand
     * @param y The right summand
     * @return The sum x+y.
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal addRound(final BigDecimal x, final BigDecimal y)
    {
        BigDecimal resul = x.add(y) ;
        /* The estimation of the absolute error in the result is |err(y)|+|err(x)|
         */
        double errR = Math.abs( y.ulp().doubleValue()/2. ) + Math.abs( x.ulp().doubleValue()/2. ) ;
        MathContext mc = new MathContext( err2prec(resul.doubleValue(),errR) ) ;
        return resul.round(mc) ;
    } /* addRound */

    /** Subtract and round according to the larger of the two ulp's.
     * @param x The left term.
     * @param y The right term.
     * @return The difference x-y.
     * @since 2009-07-30
     */
    static public BigDecimal subtractRound(final BigDecimal x, final BigDecimal y)
    {
        BigDecimal resul = x.subtract(y) ;
        /* The estimation of the absolute error in the result is |err(y)|+|err(x)|
         */
        double errR = Math.abs( y.ulp().doubleValue()/2. ) + Math.abs( x.ulp().doubleValue()/2. ) ;
        MathContext mc = new MathContext( err2prec(resul.doubleValue(),errR) ) ;
        return resul.round(mc) ;
    } /* subtractRound */

    /** Multiply and round.
     * @param x The left factor.
     * @param y The right factor.
     * @return The product x*y.
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal multiplyRound(final BigDecimal x, final BigDecimal y)
    {
        BigDecimal resul = x.multiply(y) ;
        /* The estimation of the relative error in the result is the sum of the relative
         * errors |err(y)/y|+|err(x)/x|
         */
        MathContext mc = new MathContext( Math.min(x.precision(),y.precision()) ) ;
        return resul.round(mc) ;
    } /* multiplyRound */

    /** Multiply and round.
     * @param x The left factor.
     * @param f The right factor.
     * @return The product x*f.
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal multiplyRound(final BigDecimal x, final Rational f)
    {
        if (  f.compareTo(BigInteger.ZERO) == 0 )
            return BigDecimal.ZERO ;
        else
        {
            /* Convert the rational value with two digits of extra precision
             */
            MathContext mc = new MathContext( 2+x.precision() ) ;
            BigDecimal fbd = f.BigDecimalValue(mc) ;

            /* and the precision of the product is then dominated by the precision in x
             */
            return multiplyRound(x,fbd) ;
        }
    }

    /** Multiply and round.
     * @param x The left factor.
     * @param n The right factor.
     * @return The product x*n.
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal multiplyRound(final BigDecimal x, final int n)
    {
        BigDecimal resul = x.multiply(new BigDecimal(n)) ;
        /* The estimation of the absolute error in the result is |n*err(x)|
         */
        MathContext mc = new MathContext( n != 0 ? x.precision(): 0 ) ;
        return resul.round(mc) ;
    }

    /** Multiply and round.
     * @param x The left factor.
     * @param n The right factor.
     * @return the product x*n
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal multiplyRound(final BigDecimal x, final BigInteger n)
    {
        BigDecimal resul = x.multiply(new BigDecimal(n)) ;
        /* The estimation of the absolute error in the result is |n*err(x)|
         */
        MathContext mc = new MathContext( n.compareTo(BigInteger.ZERO) != 0 ? x.precision(): 0 ) ;
        return resul.round(mc) ;
    }

    /** Divide and round.
     * @param x The numerator
     * @param y The denominator
     * @return the divided x/y
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal divideRound(final BigDecimal x, final BigDecimal y)
    {
        /* The estimation of the relative error in the result is |err(y)/y|+|err(x)/x|
         */
        MathContext mc = new MathContext( Math.min(x.precision(),y.precision()) ) ;
        BigDecimal resul = x.divide(y,mc) ;
        /* If x and y are precise integer values that may have common factors,
         * the method above will truncate trailing zeros, which may result in
         * a smaller apparent accuracy than starte... add missing trailing zeros now.
         */
        return scalePrec(resul,mc) ;
    }

    /** Divide and round.
     * @param x The numerator
     * @param n The denominator
     * @return the divided x/n
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal divideRound(final BigDecimal x, final int n)
    {
        /* The estimation of the relative error in the result is |err(x)/x|
         */
        MathContext mc = new MathContext( x.precision() ) ;
        return x.divide(new BigDecimal(n),mc) ;
    }

    /** Divide and round.
     * @param x The numerator
     * @param n The denominator
     * @return the divided x/n
     * @since 2009-07-30
     * @author Richard J. Mathar
     */
    static public BigDecimal divideRound(final BigDecimal x, final BigInteger n)
    {
        /* The estimation of the relative error in the result is |err(x)/x|
         */
        MathContext mc = new MathContext( x.precision() ) ;
        return x.divide(new BigDecimal(n),mc) ;
    } /* divideRound */

    /** Divide and round.
     * @param n The numerator
     * @param x The denominator
     * @return the divided n/x
     * @since 2009-08-05
     * @author Richard J. Mathar
     */
    static public BigDecimal divideRound(final BigInteger n, final BigDecimal x)
    {
        /* The estimation of the relative error in the result is |err(x)/x|
         */
        MathContext mc = new MathContext( x.precision() ) ;
        return new BigDecimal(n).divide(x,mc) ;
    } /* divideRound */

    /** Divide and round.
     * @param n The numerator.
     * @param x The denominator.
     * @return the divided n/x.
     * @since 2009-08-05
     * @author Richard J. Mathar
     */
    static public BigDecimal divideRound(final int n, final BigDecimal x)
    {
        /* The estimation of the relative error in the result is |err(x)/x|
         */
        MathContext mc = new MathContext( x.precision() ) ;
        return new BigDecimal(n).divide(x,mc) ;
    }

    /** Append decimal zeros to the value. This returns a value which appears to have
     * a higher precision than the input.
     * @param x The input value
     * @param d The (positive) value of zeros to be added as least significant digits.
     * @return The same value as the input but with increased (pseudo) precision.
     * @author Richard J. Mathar
     */
    static public BigDecimal scalePrec(final BigDecimal x, int d)
    {
        return x.setScale(d+x.scale()) ;
    }

    /** Boost the precision by appending decimal zeros to the value. This returns a value which appears to have
     * a higher precision than the input.
     * @param x The input value
     * @param mc The requirement on the minimum precision on return.
     * @return The same value as the input but with increased (pseudo) precision.
     * @author Richard J. Mathar
     */
    static public BigDecimal scalePrec(final BigDecimal x, final MathContext mc)
    {
        final int diffPr = mc.getPrecision() - x.precision() ;
        if ( diffPr > 0 )
            return scalePrec(x, diffPr) ;
        else
            return x ;
    } /* com.github.rccookie.math.BigDecimalMath.scalePrec */

    /** Convert an absolute error to a precision.
     * @param x The value of the variable
     * @param xerr The absolute error in the variable
     * @return The number of valid digits in x.
     *    The value is rounded down, and on the pessimistic side for that reason.
     * @since 2009-06-25
     * @author Richard J. Mathar
     */
    static public int err2prec(BigDecimal x, BigDecimal xerr)
    {
        return err2prec( xerr.divide(x,MathContext.DECIMAL64).doubleValue() );
    }

    /** Convert an absolute error to a precision.
     * @param x The value of the variable
     *    The value returned depends only on the absolute value, not on the sign.
     * @param xerr The absolute error in the variable
     *    The value returned depends only on the absolute value, not on the sign.
     * @return The number of valid digits in x.
     *    Derived from the representation x+- xerr, as if the error was represented
     *    in a "half width" (half of the error bar) form.
     *    The value is rounded down, and on the pessimistic side for that reason.
     * @since 2009-05-30
     * @author Richard J. Mathar
     */
    static public int err2prec(double x, double xerr)
    {
        /* Example: an error of xerr=+-0.5 at x=100 represents 100+-0.5 with
         * a precision = 3 (digits).
         */

        double p = Math.log10(Math.abs(0.5*x/xerr) );
        return Math.max(1, p >= Integer.MAX_VALUE / 2d ? Integer.MAX_VALUE / 2 : 1 + (int) p);
//        return 1+(int)(Math.log10(Math.abs(0.5*x/xerr) ) );
    }

    /** Convert a relative error to a precision.
     * @param xerr The relative error in the variable.
     *    The value returned depends only on the absolute value, not on the sign.
     * @return The number of valid digits in x.
     *    The value is rounded down, and on the pessimistic side for that reason.
     * @since 2009-08-05
     * @author Richard J. Mathar
     */
    static public int err2prec(double xerr)
    {
        /* Example: an error of xerr=+-0.5 a precision of 1 (digit), an error of
         * +-0.05 a precision of 2 (digits)
         */
        return 1+(int)(Math.log10(Math.abs(0.5/xerr) ) );
    }

    /** Convert a precision (relative error) to an absolute error.
     *    The is the inverse functionality of err2prec().
     * @param x The value of the variable
     *    The value returned depends only on the absolute value, not on the sign.
     * @param prec The number of valid digits of the variable.
     * @return the absolute error in x.
     *    Derived from the an accuracy of one half of the ulp.
     * @since 2009-08-09
     * @author Richard J. Mathar
     */
    static public double prec2err(final double x, final int prec)
    {
        return 5.*Math.abs(x)*Math.pow(10.,-prec) ;
    }

    /** com.github.rccookie.math.BigDecimalMath.Bernoulli numbers.
    * @since 2006-06-25
    * @author Richard J. Mathar
    */
    private static class Bernoulli
    {
            /*
            * The list of all com.github.rccookie.math.BigDecimalMath.Bernoulli numbers as a vector, n=0,2,4,....
            */
            static Vector<Rational> a = new Vector<Rational>() ;

            public Bernoulli()
            {
                    if ( a.size() == 0 )
                    {
                            a.add(Rational.ONE) ;
                            a.add(new Rational(1,6)) ;
                    }
            }

            /** Set a coefficient in the internal table.
            * @param n the zero-based index of the coefficient. n=0 for the constant term.
            * @param value the new value of the coefficient.
            * @author Richard J. Mathar
            */
            protected void set(final int n, final Rational value)
            {
                    final int nindx = n /2 ;
                    if ( nindx < a.size())
                            a.set(nindx,value) ;
                    else
                    {
                            while ( a.size() < nindx )
                                    a.add( Rational.ZERO ) ;
                            a.add(value) ;
                    }
            }

            /** The com.github.rccookie.math.BigDecimalMath.Bernoulli number at the index provided.
            * @param n the index, non-negative.
            * @return the B_0=1 for n=0, B_1=-1/2 for n=1, B_2=1/6 for n=2 etc
            * @author Richard J. Mathar
            */
            public Rational at(int n)
            {
                    if ( n == 1)
                            return(new Rational(-1,2)) ;
                    else if ( n % 2 != 0 )
                            return Rational.ZERO ;
                    else
                    {
                            final int nindx = n /2 ;
                            if( a.size() <= nindx )
                            {
                                            for(int i= 2*a.size() ; i <= n; i+= 2)
                                                    set(i, doubleSum(i) ) ;
                            }
                            return a.elementAt(nindx) ;
                    }
            }

            /* Generate a new B_n by a standard double sum.
            * @param n The index of the com.github.rccookie.math.BigDecimalMath.Bernoulli number.
            * @return The com.github.rccookie.math.BigDecimalMath.Bernoulli number at n.
            * @author Richard J. Mathar
            */
            private Rational doubleSum(int n)
            {
                    Rational resul = Rational.ZERO ;
                    for(int k=0 ; k <= n ; k++)
                    {
                            Rational jsum = Rational.ZERO ;
                            BigInteger bin = BigInteger.ONE ;
                            for(int j=0 ; j <= k ; j++)
                            {
                                    BigInteger jpown = (new BigInteger(""+j)).pow(n);
                                    if ( j % 2 == 0)
                                            jsum = jsum.add(bin.multiply(jpown)) ;
                                    else
                                            jsum = jsum.subtract(bin.multiply(jpown)) ;

                                    /* update binomial(k,j) recursively
                                    */
                                    bin = bin.multiply( new BigInteger(""+(k-j))). divide( new BigInteger(""+(j+1)) ) ;
                            }
                            resul = resul.add(jsum.divide(new BigInteger(""+(k+1)))) ;
                    }
                    return resul ;
            }



    } /* com.github.rccookie.math.BigDecimalMath.Bernoulli */

    /** Factorials.
    * @since 2006-06-25
    * @since 2012-02-15 Storage of the values based on Ifactor, not BigInteger.
    * @author Richard J. Mathar
    */
    private static class Factorial
    {
            /** The list of all factorials as a vector.
            */
            static Vector<Ifactor> a = new Vector<Ifactor>() ;

            /** ctor().
            * Initialize the vector of the factorials with 0!=1 and 1!=1.
            * @author Richard J. Mathar
            */
            public Factorial()
            {
                    if ( a.size() == 0 )
                    {
                            a.add(Ifactor.ONE) ;
                            a.add(Ifactor.ONE) ;
                    }
            } /* ctor */

            /** Compute the factorial of the non-negative integer.
            * @param n the argument to the factorial, non-negative.
            * @return the factorial of n.
            * @author Richard J. Mathar
            */
            public BigInteger at(int n)
            {
                    /* extend the internal list if needed.
                    */
                    growto(n) ;
                    return a.elementAt(n).n ;
            } /* at */

            /** Compute the factorial of the non-negative integer.
            * @param n the argument to the factorial, non-negative.
            * @return the factorial of n.
            * @author Richard J. Mathar
            */
            public Ifactor toIfactor(int n)
            {
                    /* extend the internal list if needed.
                    */
                    growto(n) ;
                    return a.elementAt(n) ;
            } /* at */

            /** Extend the internal table to cover up to n!
            * @param n The maximum factorial to be supported.
            * @since 2012-02-15
            * @author Richard J. Mathar
            */
            private void growto(int n)
            {
                    /* extend the internal list if needed. Size to be 2 for n<=1, 3 for n<=2 etc.
                    */
                    while ( a.size() <=n )
                    {
                            final int lastn = a.size()-1 ;
                            final Ifactor nextn = new Ifactor(lastn+1) ;
                            a.add(a.elementAt(lastn).multiply(nextn) ) ;
                    }
            } /* growto */

    } /* com.github.rccookie.math.BigDecimalMath.Factorial */

    /** Fractions (rational numbers).
    * They are ratios of two BigInteger numbers, reduced to coprime
    * numerator and denominator.
    * @since 2006-06-25
    * @author Richard J. Mathar
    */
    private static class Rational implements Cloneable, Comparable<Rational>
    {
            /** numerator
            */
            BigInteger a ;

            /** denominator, always larger than zero.
            */
            BigInteger b ;

            /** The maximum and minimum value of a standard Java integer, 2^31.
            * @since 2009-05-18
            */
            static public BigInteger MAX_INT = new BigInteger("2147483647") ;
            static public BigInteger MIN_INT = new BigInteger("-2147483648") ;

            /** The constant 0.
            */
            static public Rational ZERO = new Rational() ;

            /** The constant 1.
            */
            static Rational ONE = new Rational(1,1) ;

            /** The constant 1/2
            * @since 2010-05-25
            */
            static public Rational HALF = new Rational(1,2) ;

            /** Default ctor, which represents the zero.
            * @since 2007-11-17
            * @author Richard J. Mathar
            */
            public Rational()
            {
                    a = BigInteger.ZERO ;
                    b = BigInteger.ONE ;
            } /* ctor */

            /** ctor from a numerator and denominator.
            * @param a the numerator.
            * @param b the denominator.
            * @author Richard J. Mathar
            */
            public Rational(BigInteger a, BigInteger b)
            {
                    this.a = a ;
                    this.b = b ;
                    normalize() ;
            } /* ctor */

            /** ctor from a numerator.
            * @param a the BigInteger.
            * @author Richard J. Mathar
            */
            public Rational(BigInteger a)
            {
                    this.a = a ;
                    b = new BigInteger("1") ;
            } /* ctor */

            /** ctor from a numerator and denominator.
            * @param a the numerator.
            * @param b the denominator.
            * @author Richard J. Mathar
            */
            public Rational(int a, int b)
            {
                    this(new BigInteger(""+a),new BigInteger(""+b)) ;
            } /* ctor */

            /** ctor from an integer.
            * @param n the integer to be represented by the new instance.
            * @since 2010-07-18
            * @author Richard J. Mathar
            */
            public Rational(int n)
            {
                    this(n,1) ;
            } /* ctor */

            /** ctor from a string representation.
            * @param str the string.
            *   This either has a slash in it, separating two integers, or, if there is no slash,
            *   is representing the numerator with implicit denominator equal to 1.
            * Warning: this does not yet test for a denominator equal to zero
            * @author Richard J. Mathar
            */
            public Rational(String str) throws NumberFormatException
            {
                    this(str,10) ;
            } /* ctor */

            /** ctor from a string representation in a specified base.
            * @param str the string.
            *   This either has a slash in it, separating two integers, or, if there is no slash,
            *   is just representing the numerator.
            * @param radix the number base for numerator and denominator
            * Warning: this does not yet test for a denominator equal to zero
            * @author Richard J. Mathar
            */
            public Rational(String str, int radix) throws NumberFormatException
            {
                    int hasslah = str.indexOf("/") ;
                    if ( hasslah == -1 )
                    {
                            a = new BigInteger(str,radix) ;
                            b = new BigInteger("1",radix) ;
                            /* no normalization necessary here */
                    }
                    else
                    {
                            /* create numerator and denominator separately
                            */
                            a = new BigInteger(str.substring(0,hasslah),radix) ;
                            b = new BigInteger(str.substring(hasslah+1),radix) ;
                            normalize() ;
                    }
            } /* ctor */

            /** ctor from a terminating continued fraction.
            * Constructs the value of cfr[0]+1/(cfr[1]+1/(cfr[2]+...))).
            * @param cfr The coefficients cfr[0], cfr[1],... of the continued fraction.
            *  An exception is thrown if any of these is zero.
            * @since 2012-03-08
            * @author Richard J. Mathar
            */
            public Rational(Vector<BigInteger> cfr)
            {
                    if ( cfr.size() == 0)
                            throw new NumberFormatException("Empty continued fraction") ;
                    else if ( cfr.size() == 1)
                    {
                            this.a = cfr.firstElement() ;
                            this.b = BigInteger.ONE ;
                    }
                    else
                    {
                            /* recursive this = cfr[0]+1/(cfr[1]+...) where cfr[1]+... = rec =rec.a/rec.b
                            * this = cfr[0]+rec.b/rec.a = (cfr[0]*rec.a+rec.b)/rec.a .
                            * Create a cloned version of references to cfr, without cfr[0]
                            */
                            Vector<BigInteger> clond = new Vector<BigInteger>() ;
                            for(int i=1 ; i < cfr.size() ; i++)
                                    clond.add(cfr.elementAt(i)) ;
                            Rational rec = new Rational(clond) ;
                            this.a = cfr.firstElement().multiply(rec.a).add(rec.b) ;
                            this.b = rec.a ;
                            normalize() ;
                    }
            } /* ctor */

            /** Create a copy.
            * @since 2008-11-07
            * @author Richard J. Mathar
            */
            public Rational clone()
            {
                    /* protected access means this does not work
                    * return new com.github.rccookie.math.BigDecimalMath.Rational(a.clone(), b.clone()) ;
                    */
                    BigInteger aclon = new BigInteger(""+a) ;
                    BigInteger bclon = new BigInteger(""+b) ;
                    return new Rational(aclon,bclon) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.clone */

            /** Multiply by another fraction.
            * @param val a second rational number.
            * @return the product of this with the val.
            * @author Richard J. Mathar
            */
            public Rational multiply(final Rational val)
            {
                    BigInteger num = a.multiply(val.a) ;
                    BigInteger deno = b.multiply(val.b) ;
                    /* Normalization to an coprime format will be done inside
                    * the ctor() and is not duplicated here.
                    */
                    return ( new Rational(num,deno) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.multiply */

            /** Multiply by a BigInteger.
            * @param val a second number.
            * @return the product of this with the value.
            * @author Richard J. Mathar
            */
            public Rational multiply(final BigInteger val)
            {
                    Rational val2 = new Rational(val,BigInteger.ONE) ;
                    return ( multiply(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.multiply */

            /** Multiply by an integer.
            * @param val a second number.
            * @return the product of this with the value.
            * @author Richard J. Mathar
            */
            public Rational multiply(final int val)
            {
                    BigInteger tmp = new BigInteger(""+val) ;
                    return multiply(tmp) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.multiply */

            /** Power to an integer.
            * @param exponent the exponent.
            * @return this value raised to the power given by the exponent.
            *  If the exponent is 0, the value 1 is returned.
            * @author Richard J. Mathar
            */
            public Rational pow(int exponent)
            {
                    if ( exponent == 0 )
                            return new Rational(1,1) ;

                    BigInteger num = a.pow(Math.abs(exponent)) ;
                    BigInteger deno = b.pow(Math.abs(exponent)) ;
                    if ( exponent > 0 )
                            return ( new Rational(num,deno) ) ;
                    else
                            return ( new Rational(deno,num) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.pow */

            /** Power to an integer.
            * @param exponent the exponent.
            * @return this value raised to the power given by the exponent.
            *  If the exponent is 0, the value 1 is returned.
            * @author Richard J. Mathar
            * @since 2009-05-18
            */
            public Rational pow(BigInteger exponent) throws NumberFormatException
            {
                    /* test for overflow */
                    if ( exponent.compareTo(MAX_INT) == 1 )
                            throw new NumberFormatException("Exponent "+exponent.toString()+" too large.") ;
                    if ( exponent.compareTo(MIN_INT) == -1 )
                            throw new NumberFormatException("Exponent "+exponent.toString()+" too small.") ;

                    /* promote to the simpler interface above */
                    return pow( exponent.intValue() ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.pow */

            /** r-th root.
            * @param r the inverse of the exponent.
            *  2 for the square root, 3 for the third root etc
            * @return this value raised to the inverse power given by the root argument, this^(1/r).
            * @since 2009-05-18
            * @author Richard J. Mathar
            */
            public Rational root(BigInteger r) throws NumberFormatException
            {
                    /* test for overflow */
                    if ( r.compareTo(MAX_INT) == 1 )
                            throw new NumberFormatException("Root "+r.toString()+" too large.") ;
                    if ( r.compareTo(MIN_INT) == -1 )
                            throw new NumberFormatException("Root "+r.toString()+" too small.") ;

                    int rthroot = r.intValue() ;
                    /* cannot pull root of a negative value with even-valued root */
                    if ( compareTo(ZERO) == -1 && (rthroot % 2) ==0 )
                            throw new NumberFormatException("Negative basis "+ toString()+" with odd root "+r.toString()) ;

                    /* extract a sign such that we calculate |n|^(1/r), still r carrying any sign
                    */
                    final boolean flipsign = ( compareTo(ZERO) == -1 && (rthroot % 2) != 0) ? true : false ;

                    /* delegate the main work to ifactor#root()
                    */
                    Ifactor num =  new Ifactor(a.abs()) ;
                    Ifactor deno = new Ifactor(b) ;
                    final Rational resul = num.root(rthroot).divide( deno.root(rthroot) ) ;
                    if ( flipsign)
                            return resul.negate() ;
                    else
                            return resul ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.root */

            /** Raise to a rational power.
            * @param exponent The exponent.
            * @return This value raised to the power given by the exponent.
            *  If the exponent is 0, the value 1 is returned.
            * @since 2009-05-18
            * @author Richard J. Mathar
            */
            public Rational pow(Rational exponent) throws NumberFormatException
            {
                    if ( exponent.a.compareTo(BigInteger.ZERO) == 0 )
                            return new Rational(1,1) ;

                    /* calculate (a/b)^(exponent.a/exponent.b) as ((a/b)^exponent.a)^(1/exponent.b)
                    * = tmp^(1/exponent.b)
                    */
                    Rational tmp = pow(exponent.a) ;
                    return tmp.root(exponent.b) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.pow */

            /** Divide by another fraction.
            * @param val A second rational number.
            * @return The value of this/val
            * @author Richard J. Mathar
            */
            public Rational divide(final Rational val)
            {
                    if( val.compareTo(Rational.ZERO) == 0 )
                            throw new ArithmeticException("Dividing "+ toString() + " through zero.") ;
                    BigInteger num = a.multiply(val.b) ;
                    BigInteger deno = b.multiply(val.a) ;
                    /* Reduction to a coprime format is done inside the ctor,
                    * and not repeated here.
                    */
                    return ( new Rational(num,deno) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.divide */

            /** Divide by an integer.
            * @param val a second number.
            * @return the value of this/val
            * @author Richard J. Mathar
            */
            public Rational divide(BigInteger val)
            {
                    if( val.compareTo(BigInteger.ZERO) == 0 )
                            throw new ArithmeticException("Dividing "+ toString() + " through zero.") ;
                    Rational val2 = new Rational(val,BigInteger.ONE) ;
                    return ( divide(val2)) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.divide */

            /** Divide by an integer.
            * @param val A second number.
            * @return The value of this/val
            * @author Richard J. Mathar
            */
            public Rational divide(int val)
            {
                    if( val == 0 )
                            throw new ArithmeticException("Dividing "+ toString() + " through zero.") ;
                    Rational val2 = new Rational(val,1) ;
                    return ( divide(val2)) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.divide */

            /** Add another fraction.
            * @param val The number to be added
            * @return this+val.
            * @author Richard J. Mathar
            */
            public Rational add(Rational val)
            {
                    BigInteger num = a.multiply(val.b).add(b.multiply(val.a)) ;
                    BigInteger deno = b.multiply(val.b) ;
                    return ( new Rational(num,deno) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.add */

            /** Add another integer.
            * @param val The number to be added
            * @return this+val.
            * @author Richard J. Mathar
            */
            public Rational add(BigInteger val)
            {
                    Rational val2 = new Rational(val,BigInteger.ONE) ;
                    return ( add(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.add */

            /** Add another integer.
            * @param val The number to be added
            * @return this+val.
            * @since May 26 2010
            * @author Richard J. Mathar
            */
            public Rational add(int val)
            {
                    BigInteger val2 = a.add(b.multiply(new BigInteger(""+val))) ;
                    return new Rational(val2,b) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.add */

            /** Compute the negative.
            * @return -this.
            * @author Richard J. Mathar
            */
            public Rational negate()
            {
                    return ( new Rational(a.negate(),b) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.negate */

            /** Subtract another fraction.
            * @param val the number to be subtracted from this
            * @return this - val.
            * @author Richard J. Mathar
            */
            public Rational subtract(Rational val)
            {
                    Rational val2 = val.negate() ;
                    return ( add(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.subtract */

            /** Subtract an integer.
            * @param val the number to be subtracted from this
            * @return this - val.
            * @author Richard J. Mathar
            */
            public Rational subtract(BigInteger val)
            {
                    Rational val2 = new Rational(val,BigInteger.ONE) ;
                    return ( subtract(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.subtract */

            /** Subtract an integer.
            * @param val the number to be subtracted from this
            * @return this - val.
            * @author Richard J. Mathar
            */
            public Rational subtract(int val)
            {
                    Rational val2 = new Rational(val,1) ;
                    return ( subtract(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.subtract */

            /** binomial (n choose m).
            * @param n the numerator. Equals the size of the set to choose from.
            * @param m the denominator. Equals the number of elements to select.
            * @return the binomial coefficient.
            * @since 2006-06-27
            * @author Richard J. Mathar
            */
            public static Rational binomial(Rational n, BigInteger m)
            {
                    if ( m.compareTo(BigInteger.ZERO) == 0 )
                            return Rational.ONE ;
                    Rational bin = n ;
                    for(BigInteger i=new BigInteger("2") ; i.compareTo(m) != 1 ; i = i.add(BigInteger.ONE) )
                    {
                            bin = bin.multiply(n.subtract(i.subtract(BigInteger.ONE))).divide(i) ;
                    }
                    return bin ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.binomial */

            /** binomial (n choose m).
            * @param n the numerator. Equals the size of the set to choose from.
            * @param m the denominator. Equals the number of elements to select.
            * @return the binomial coefficient.
            * @since 2009-05-19
            * @author Richard J. Mathar
            */
            public static Rational binomial(Rational n, int m)
            {
                    if ( m == 0 )
                            return Rational.ONE ;
                    Rational bin = n ;
                    for( int i=2 ; i <= m ; i++ )
                    {
                            bin = bin.multiply(n.subtract(i-1)).divide(i) ;
                    }
                    return bin ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.binomial */

            /** Hankel's symbol (n,k)
            * @param n the first parameter.
            * @param k the second parameter, greater or equal to 0.
            * @return Gamma(n+k+1/2)/k!/GAMMA(n-k+1/2)
            * @since 2010-07-18
            * @author Richard J. Mathar
            */
            public static Rational hankelSymb(Rational n, int k)
            {
                    if ( k == 0 )
                            return Rational.ONE ;
                    else if ( k < 0)
                            throw new ArithmeticException("Negative parameter "+k) ;
                    Rational nkhalf = n.subtract(k).add(Rational.HALF) ;
                    nkhalf = nkhalf.Pochhammer(2*k) ;
                    Factorial f = new Factorial() ;
                    return nkhalf.divide(f.at(k)) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.binomial */

            /** Get the numerator.
            * @return The numerator of the reduced fraction.
            * @author Richard J. Mathar
            */
            public BigInteger numer()
            {
                    return a ;
            }

            /** Get the denominator.
            * @return The denominator of the reduced fraction.
            * @author Richard J. Mathar
            */
            public BigInteger denom()
            {
                    return b ;
            }

            /** Absolute value.
            * @return The absolute (non-negative) value of this.
            * @author Richard J. Mathar
            */
            public Rational abs()
            {
                    return( new Rational(a.abs(),b.abs())) ;
            }

            /** floor(): the nearest integer not greater than this.
            * @return The integer rounded towards negative infinity.
            * @author Richard J. Mathar
            */
            public BigInteger floor()
            {
                    /* is already integer: return the numerator
                    */
                    if ( b.compareTo(BigInteger.ONE) == 0 )
                            return a;
                    else if ( a.compareTo(BigInteger.ZERO) > 0 )
                            return a.divide(b);
                    else
                            return a.divide(b).subtract(BigInteger.ONE) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.floor */

            /** ceil(): the nearest integer not smaller than this.
            * @return The integer rounded towards positive infinity.
            * @since 2010-05-26
            * @author Richard J. Mathar
            */
            public BigInteger ceil()
            {
                    /* is already integer: return the numerator
                    */
                    if ( b.compareTo(BigInteger.ONE) == 0 )
                            return a;
                    else if ( a.compareTo(BigInteger.ZERO) > 0 )
                            return a.divide(b).add(BigInteger.ONE) ;
                    else
                            return a.divide(b) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.ceil */

            /** Remove the fractional part.
            * @return The integer rounded towards zero.
            * @author Richard J. Mathar
            */
            public BigInteger trunc()
            {
                    /* is already integer: return the numerator
                    */
                    if ( b.compareTo(BigInteger.ONE) == 0 )
                            return a;
                    else
                            return a.divide(b);
            } /* com.github.rccookie.math.BigDecimalMath.Rational.trunc */

            /** Compares the value of this with another constant.
            * @param val the other constant to compare with
            * @return -1, 0 or 1 if this number is numerically less than, equal to,
            *    or greater than val.
            * @author Richard J. Mathar
            */
            public int compareTo(final Rational val)
            {
                    /* Since we have always kept the denominators positive,
                    * simple cross-multiplying works without changing the sign.
                    */
                    final BigInteger left = a.multiply(val.b) ;
                    final BigInteger right = val.a.multiply(b) ;
                    return left.compareTo(right) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.compareTo */

            /** Compares the value of this with another constant.
            * @param val the other constant to compare with
            * @return -1, 0 or 1 if this number is numerically less than, equal to,
            *    or greater than val.
            * @author Richard J. Mathar
            */
            public int compareTo(final BigInteger val)
            {
                    final Rational val2 = new Rational(val,BigInteger.ONE) ;
                    return ( compareTo(val2) ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.compareTo */

            /** Return a string in the format number/denom.
            * If the denominator equals 1, print just the numerator without a slash.
            * @return the human-readable version in base 10
            * @author Richard J. Mathar
            */
            public String toString()
            {
                    if ( b.compareTo(BigInteger.ONE) != 0)
                            return( a.toString()+"/"+b.toString() ) ;
                    else
                            return a.toString() ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.toString */

            /** Return a double value representation.
            * @return The value with double precision.
            * @since 2008-10-26
            * @author Richard J. Mathar
            */
            public double doubleValue()
            {
                    /* To meet the risk of individual overflows of the exponents of
                    * a separate invocation a.doubleValue() or b.doubleValue(), we divide first
                    * in a BigDecimal environment and convert the result.
                    */
                    BigDecimal adivb  = (new BigDecimal(a)).divide(new BigDecimal(b), MathContext.DECIMAL128) ;
                    return adivb.doubleValue() ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.doubleValue */

            /** Return a float value representation.
            * @return The value with single precision.
            * @since 2009-08-06
            * @author Richard J. Mathar
            */
            public float floatValue()
            {
                    BigDecimal adivb  = (new BigDecimal(a)).divide(new BigDecimal(b), MathContext.DECIMAL128) ;
                    return adivb.floatValue() ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.floatValue */

            /** Return a representation as BigDecimal.
            * @param mc the mathematical context which determines precision, rounding mode etc
            * @return A representation as a BigDecimal floating point number.
            * @since 2008-10-26
            * @author Richard J. Mathar
            */
            public BigDecimal BigDecimalValue(MathContext mc)
            {
                    /* numerator and denominator individually rephrased
                    */
                    BigDecimal n = new BigDecimal(a) ;
                    BigDecimal d = new BigDecimal(b) ;
                    /* the problem with n.divide(d,mc) is that the apparent precision might be
                    * smaller than what is set by mc if the value has a precise truncated representation.
                    * 1/4 will appear as 0.25, independent of mc
                    */
                    return scalePrec(n.divide(d,mc),mc) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.BigDecimalValue */

            /** Return a string in floating point format.
            * @param digits The precision (number of digits)
            * @return The human-readable version in base 10.
            * @since 2008-10-25
            * @author Richard J. Mathar
            */
            public String toFString(int digits)
            {
                    if ( b.compareTo(BigInteger.ONE) != 0)
                    {
                            MathContext mc = new MathContext(digits, RoundingMode.DOWN) ;
                            BigDecimal f = (new BigDecimal(a)).divide(new BigDecimal(b),mc) ;
                            return( f.toString() ) ;
                    }
                    else
                            return a.toString() ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.toFString */

            /** Compares the value of this with another constant.
            * @param val The other constant to compare with
            * @return The arithmetic maximum of this and val.
            * @since 2008-10-19
            * @author Richard J. Mathar
            */
            public Rational max(final Rational val)
            {
                    if ( compareTo(val) > 0 )
                            return this;
                    else
                            return val;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.max */

            /** Compares the value of this with another constant.
            * @param val The other constant to compare with
            * @return The arithmetic minimum of this and val.
            * @since 2008-10-19
            * @author Richard J. Mathar
            */
            public Rational min(final Rational val)
            {
                    if ( compareTo(val) < 0 )
                            return this;
                    else
                            return val;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.min */

            /** Compute Pochhammer's symbol (this)_n.
            * @param n The number of product terms in the evaluation.
            * @return Gamma(this+n)/Gamma(this) = this*(this+1)*...*(this+n-1).
            * @since 2008-10-25
            * @author Richard J. Mathar
            */
            public Rational Pochhammer(final BigInteger n)
            {
                    if ( n.compareTo(BigInteger.ZERO) < 0 )
                            return null;
                    else if ( n.compareTo(BigInteger.ZERO) == 0 )
                            return Rational.ONE ;
                    else
                    {
                            /* initialize results with the current value
                            */
                            Rational res = new Rational(a,b) ;
                            BigInteger i = BigInteger.ONE ;
                            for( ; i.compareTo(n) < 0 ; i=i.add(BigInteger.ONE) )
                                    res = res.multiply( add(i) ) ;
                            return res;
                    }
            } /* com.github.rccookie.math.BigDecimalMath.Rational.pochhammer */

            /** Compute pochhammer's symbol (this)_n.
            * @param n The number of product terms in the evaluation.
            * @return Gamma(this+n)/GAMMA(this).
            * @since 2008-11-13
            * @author Richard J. Mathar
            */
            public Rational Pochhammer(int n)
            {
                    return Pochhammer(new BigInteger(""+n)) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.pochhammer */

            /** True if the value is integer.
            * Equivalent to the indication whether a conversion to an integer
            * can be exact.
            * @since 2010-05-26
            * @author Richard J. Mathar
            */
            public boolean isBigInteger()
            {
                    return ( b.abs().compareTo(BigInteger.ONE) == 0 ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.isBigInteger */

            /** True if the value is integer and in the range of the standard integer.
            * Equivalent to the indication whether a conversion to an integer
            * can be exact.
            * @since 2010-05-26
            * @author Richard J. Mathar
            */
            public boolean isInteger()
            {
                    if ( ! isBigInteger() )
                            return false;
                    return ( a.compareTo(MAX_INT) <= 0 && a.compareTo(MIN_INT) >= 0 ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.isInteger */


            /** Conversion to an integer value, if this can be done exactly.
            * @since 2011-02-13
            * @author Richard J. Mathar
            */
            int intValue()
            {
                    if ( ! isInteger() )
                            throw new NumberFormatException("cannot convert "+toString()+" to integer.") ;
                    return a.intValue() ;
            }

            /** Conversion to a BigInteger value, if this can be done exactly.
            * @since 2012-03-02
            * @author Richard J. Mathar
            */
            BigInteger BigIntegerValue()
            {
                    if ( ! isBigInteger() )
                            throw new NumberFormatException("cannot convert "+toString()+" to BigInteger.") ;
                    return a ;
            }

            /** True if the value is a fraction of two integers in the range of the standard integer.
            * @since 2010-05-26
            * @author Richard J. Mathar
            */
            public boolean isIntegerFrac()
            {
                    return ( a.compareTo(MAX_INT) <= 0 && a.compareTo(MIN_INT) >= 0
                            && b.compareTo(MAX_INT) <= 0 && b.compareTo(MIN_INT) >= 0 ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.isIntegerFrac */

            /** The sign: 1 if the number is larger than zero, 0 if it equals zero, -1 if it is smaller than zero.
            * @return the signum of the value.
            * @since 2010-05-26
            * @author Richard J. Mathar
            */
            public int signum()
            {
                    return ( b.signum() * a.signum() ) ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.signum */

            /** Terminating continued fractions.
            * @return The list of a0, a1, a2,... in this =a0+1/(a1+1/(a2+1/(a3+...)))).
            *  If this here is zero, the list is empty.
            * @since 2012-03-09
            * @author Richard J. Mathar
            */
            public Vector<BigInteger> cfrac()
            {
                    if ( signum() < 0 )
                            throw new NumberFormatException("Unsupported cfrac for negative "+this) ;
                    Vector<BigInteger> cf = new Vector<BigInteger>() ;
                    if ( signum() != 0)
                    {
                            BigInteger[] nRem = a.divideAndRemainder(b) ;
                            cf.add( nRem[0]) ;
                            /* recursive call : this = nRem[0]+nRem[1]/b = nRem[0] + 1/(b/nRem[1])
                            */
                            if ( nRem[1].signum() != 0 )
                                    cf.addAll( (new Rational(b,nRem[1])).cfrac() ) ;
                    }
                    return cf ;
            } /* com.github.rccookie.math.BigDecimalMath.Rational.cfrac */

            /** The Harmonic number at the index specified.
            * @param n the index, non-negative.
            * @return the sum of the inverses of the integers from 1 to n.
            *   H_1=1 for n=1, H_2=3/2 for n=2 etc.
            *   For values of n less than 1, zero is returned.
            * @since 2008-10-19
            * @author Richard J. Mathar
            * @author Richard J. Mathar
            */
            static public Rational harmonic(int n)
            {
                    if ( n < 1)
                            return(new Rational(0,1)) ;
                    else
                    {
                            /* start with 1 as the result
                            */
                            Rational a = new Rational(1,1) ;

                            /* add 1/i for i=2..n
                            */
                            for( int i=2 ; i <=n ; i++)
                                    a = a.add(new Rational(1,i)) ;
                            return a ;
                    }
            } /* harmonic */

            /** Normalize to coprime numerator and denominator.
            * Also copy a negative sign of the denominator to the numerator.
            * @since 2008-10-19
            * @author Richard J. Mathar
            */
            protected void normalize()
            {
                    /* compute greatest common divisor of numerator and denominator
                    */
                    final BigInteger g = a.gcd(b) ;
                    if ( g.compareTo(BigInteger.ONE) > 0 )
                    {
                            a = a.divide(g) ;
                            b = b.divide(g);
                    }
                    if ( b.compareTo(BigInteger.ZERO) == -1 )
                    {
                            a = a.negate() ;
                            b = b.negate() ;
                    }
            } /* com.github.rccookie.math.BigDecimalMath.Rational.normalize */
    } /* com.github.rccookie.math.BigDecimalMath.Rational */

    /** Factored integers.
    * This class contains a non-negative integer with the prime factor decomposition attached.
    * @since 2006-08-14
    * @since 2012-02-14 The internal representation contains the bases, and becomes sparser if few
    *    prime factors are present.
    * @author Richard J. Mathar
    */
    private static class Ifactor implements Cloneable, Comparable<Ifactor>
    {
            /**
            * The standard representation of the number
            */
            public BigInteger n ;

            /*
            * The bases and powers of the prime factorization.
            * representation n = primeexp[0]^primeexp[1]*primeexp[2]^primeexp[3]*...
            * The value 0 is represented by an empty vector, the value 1 by a vector of length 1
            * with a single power of 0.
            */
            public Vector<Integer> primeexp ;

            final public static Ifactor ONE = new Ifactor(1) ;

            final public static Ifactor ZERO = new Ifactor(0) ;

            /** Constructor given an integer.
            * constructor with an ordinary integer
            * @param number the standard representation of the integer
            * @author Richard J. Mathar
            */
            public Ifactor(int number)
            {
                    n = new BigInteger(""+number) ;
                    primeexp = new Vector<Integer>() ;
                    if( number > 1 )
                    {
                            int primindx = 0 ;
                            Prime primes = new Prime() ;
                            /* Test division against all primes.
                            */
                            while(number > 1)
                            {
                                    int ex=0 ;
                                    /* primindx=0 refers to 2, =1 to 3, =2 to 5, =3 to 7 etc
                                    */
                                    int p = primes.at(primindx).intValue() ;
                                    while( number % p == 0 )
                                    {
                                            ex++ ;
                                            number /= p ;
                                            if ( number == 1 )
                                                    break ;
                                    }
                                    if ( ex > 0 )
                                    {
                                            primeexp.add(p) ;
                                            primeexp.add(ex) ;
                                    }
                                    primindx++ ;
                            }
                    }
                    else if ( number == 1)
                    {
                            primeexp.add(1) ;
                            primeexp.add(0) ;
                    }
            } /* Ifactor */

            /** Constructor given a BigInteger .
            * Constructor with an ordinary integer, calling a prime factor decomposition.
            * @param number the BigInteger representation of the integer
            * @author Richard J. Mathar
            */
            public Ifactor(BigInteger number)
            {
                    n = number ;
                    primeexp = new Vector<Integer>() ;
                    if ( number.compareTo(BigInteger.ONE) == 0 )
                    {
                            primeexp.add(1) ;
                            primeexp.add(0) ;
                    }
                    else
                    {
                            int primindx = 0 ;
                            Prime primes = new Prime() ;
                            /* Test for division against all primes.
                            */
                            while(number.compareTo(BigInteger.ONE) == 1)
                            {
                                    int ex=0 ;
                                    BigInteger p = primes.at(primindx) ;
                                    while( number.remainder(p).compareTo(BigInteger.ZERO) == 0 )
                                    {
                                            ex++ ;
                                            number = number.divide(p) ;
                                            if ( number.compareTo(BigInteger.ONE) == 0 )
                                                    break ;
                                    }
                                    if ( ex > 0 )
                                    {
                                            primeexp.add(p.intValue()) ;
                                            primeexp.add(ex) ;
                                    }
                                    primindx++ ;
                            }
                    }
            } /* Ifactor */

            /** Constructor given a list of exponents of the prime factor decomposition.
            * @param pows the vector with the sorted list of exponents.
            *  pows[0] is the exponent of 2, pows[1] the exponent of 3, pows[2] the exponent of 5 etc.
            *  Note that this list does not include the primes, but assumes a continuous prime-smooth basis.
            * @author Richard J. Mathar
            */
            public Ifactor(Vector<Integer> pows)
            {
                    primeexp = new Vector<Integer>(2* pows.size()) ;
                    if ( pows.size() > 0 )
                    {
                            n = BigInteger.ONE ;
                            Prime primes = new Prime() ;
                            /* Build the full number by the product of all powers of the primes.
                            */
                            for(int primindx=0 ; primindx < pows.size() ; primindx++)
                            {
                                    int ex= pows.elementAt(primindx).intValue() ;
                                    final BigInteger p = primes.at(primindx) ;
                                    n = n.multiply( p.pow(ex) ) ;
                                    primeexp.add(p.intValue()) ;
                                    primeexp.add(ex) ;
                            }
                    }
                    else
                            n = BigInteger.ZERO ;
            } /* Ifactor */

            /** Copy constructor.
            * @param oth the value to be copied
            * @author Richard J. Mathar
            */
            public Ifactor(Ifactor oth)
            {
                    n = oth.n ;
                    primeexp = oth.primeexp ;
            } /* Ifactor */

            /** Deep copy.
            * @since 2009-08-14
            * @author Richard J. Mathar
            */
            public Ifactor clone()
            {
                    Vector<Integer> p = (Vector<Integer>)primeexp.clone();
                    Ifactor cl = new Ifactor(0) ;
                    cl.n = new BigInteger(""+n) ;
                    return cl ;
            } /* Ifactor.clone */

            /** Comparison of two numbers.
            * The value of this method is in allowing the Vector.contains() calls that use the value,
            * not the reference for comparison.
            * @param oth the number to compare this with.
            * @return true if both are the same numbers, false otherwise.
            * @author Richard J. Mathar
            */
            public boolean equals(final Ifactor oth)
            {
                    return (  n.compareTo(oth.n) == 0 ) ;
            } /* Ifactor.equals */

            /** Multiply with another positive integer.
            * @param oth the second factor.
            * @return the product of both numbers.
            * @author Richard J. Mathar
            */
            public Ifactor multiply(final BigInteger oth)
            {
                    /* the optimization is to factorize oth _before_ multiplying
                    */
                    return( multiply(new Ifactor(oth)) ) ;
            } /* Ifactor.multiply */

            /** Multiply with another positive integer.
            * @param oth the second factor.
            * @return the product of both numbers.
            * @author Richard J. Mathar
            */
            public Ifactor multiply(final int oth)
            {
                    /* the optimization is to factorize oth _before_ multiplying
                    */
                    return( multiply(new Ifactor(oth)) ) ;
            } /* Ifactor.multiply */

            /** Multiply with another positive integer.
            * @param oth the second factor.
            * @return the product of both numbers.
            * @author Richard J. Mathar
            */
            public Ifactor multiply(final Ifactor oth)
            {
                    /* This might be done similar to the lcm() implementation by adding
                    * the powers of the components and calling the constructor with the
                    * list of exponents. This here is the simplest implementation, but slow because
                    * it calls another prime factorization of the product:
                    * return( new Ifactor(n.multiply(oth.n))) ;
                    */
                    return multGcdLcm(oth,0) ;
            }

            /** Lowest common multiple of this with oth.
            * @param oth the second parameter of lcm(this,oth)
            * @return the lowest common multiple of both numbers. Returns zero
            *   if any of both arguments is zero.
            * @author Richard J. Mathar
            */
            public Ifactor lcm(final Ifactor oth)
            {
                    return multGcdLcm(oth,2) ;
            }

            /** Greatest common divisor of this and oth.
            * @param oth the second parameter of gcd(this,oth)
            * @return the lowest common multiple of both numbers. Returns zero
            *   if any of both arguments is zero.
            * @author Richard J. Mathar
            */
            public Ifactor gcd(final Ifactor oth)
            {
                    return multGcdLcm(oth,1) ;
            }

            /** Multiply with another positive integer.
            * @param oth the second factor.
            * @param type 0 to multiply, 1 for gcd, 2 for lcm
            * @return the product, gcd or lcm of both numbers.
            * @author Richard J. Mathar
            */
            protected Ifactor multGcdLcm(final Ifactor oth, int type)
            {
                    Ifactor prod = new Ifactor(0) ;
                    /* skip the case where 0*something =0, falling thru to the empty representation for 0
                    */
                    if( primeexp.size() != 0 && oth.primeexp.size() != 0)
                    {
                            /* Cases of 1 times something return something.
                            * Cases of lcm(1, something) return something.
                            * Cases of gcd(1, something) return 1.
                            */
                            if ( primeexp.firstElement().intValue() == 1 && type == 0)
                                    return oth ;
                            else if ( primeexp.firstElement().intValue() == 1 && type == 2)
                                    return oth ;
                            else if ( primeexp.firstElement().intValue() == 1 && type == 1)
                                    return this ;
                            else if ( oth.primeexp.firstElement().intValue() == 1 && type ==0)
                                    return this ;
                            else if ( oth.primeexp.firstElement().intValue() == 1 && type ==2)
                                    return this ;
                            else if ( oth.primeexp.firstElement().intValue() == 1 && type ==1)
                                    return oth ;
                            else
                            {
                                    int idxThis = 0 ;
                                    int idxOth = 0 ;
                                    switch(type)
                                    {
                                    case 0 :
                                            prod.n = n.multiply(oth.n) ;
                                            break;
                                    case 1 :
                                            prod.n = n.gcd(oth.n) ;
                                            break;
                                    case 2 :
                                            /* the awkward way, lcm = product divided by gcd
                                            */
                                            prod.n = n.multiply(oth.n).divide( n.gcd(oth.n) ) ;
                                            break;
                                    }

                                    /* scan both representations left to right, increasing prime powers
                                    */
                                    while( idxOth < oth.primeexp.size()  || idxThis < primeexp.size() )
                                    {
                                            if ( idxOth >= oth.primeexp.size() )
                                            {
                                                    /* exhausted the list in oth.primeexp; copy over the remaining 'this'
                                                    * if multiplying or lcm, discard if gcd.
                                                    */
                                                    if ( type == 0 || type == 2)
                                                    {
                                                            prod.primeexp.add( primeexp.elementAt(idxThis) ) ;
                                                            prod.primeexp.add( primeexp.elementAt(idxThis+1) ) ;
                                                    }
                                                    idxThis += 2 ;
                                            }
                                            else if ( idxThis >= primeexp.size() )
                                            {
                                                    /* exhausted the list in primeexp; copy over the remaining 'oth'
                                                    */
                                                    if ( type == 0 || type == 2)
                                                    {
                                                            prod.primeexp.add( oth.primeexp.elementAt(idxOth) ) ;
                                                            prod.primeexp.add( oth.primeexp.elementAt(idxOth+1) ) ;
                                                    }
                                                    idxOth += 2 ;
                                            }
                                            else
                                            {
                                                    Integer p ;
                                                    int ex ;
                                                    switch ( primeexp.elementAt(idxThis).compareTo(oth.primeexp.elementAt(idxOth) ) )
                                                    {
                                                    case 0 :
                                                            /* same prime bases p in both factors */
                                                            p = primeexp.elementAt(idxThis) ;
                                                            switch(type)
                                                            {
                                                            case 0 :
                                                                    /* product means adding exponents */
                                                                    ex = primeexp.elementAt(idxThis+1).intValue() +
                                                                            oth.primeexp.elementAt(idxOth+1).intValue() ;
                                                                    break;
                                                            case 1 :
                                                                    /* gcd means minimum of exponents */
                                                                    ex = Math.min( primeexp.elementAt(idxThis+1).intValue() ,
                                                                            oth.primeexp.elementAt(idxOth+1).intValue()) ;
                                                                    break;
                                                            default :
                                                                    /* lcm means maximum of exponents */
                                                                    ex = Math.max( primeexp.elementAt(idxThis+1).intValue() ,
                                                                            oth.primeexp.elementAt(idxOth+1).intValue()) ;
                                                                    break;
                                                            }
                                                            prod.primeexp.add( p ) ;
                                                            prod.primeexp.add(ex) ;
                                                            idxOth += 2 ;
                                                            idxThis += 2 ;
                                                            break ;
                                                    case 1:
                                                            /* this prime base bigger than the other and taken later */
                                                            if ( type == 0 || type == 2)
                                                            {
                                                                    prod.primeexp.add( oth.primeexp.elementAt(idxOth) ) ;
                                                                    prod.primeexp.add( oth.primeexp.elementAt(idxOth+1) ) ;
                                                            }
                                                            idxOth += 2 ;
                                                            break ;
                                                    default:
                                                            /* this prime base smaller than the other and taken now */
                                                            if ( type == 0 || type == 2)
                                                            {
                                                                    prod.primeexp.add( primeexp.elementAt(idxThis) ) ;
                                                                    prod.primeexp.add( primeexp.elementAt(idxThis+1) ) ;
                                                            }
                                                            idxThis += 2 ;
                                                    }
                                            }
                                    }
                            }
                    }
                    return prod ;
            } /* Ifactor.multGcdLcm */

            /** Integer division through  another positive integer.
            * @param oth the denominator.
            * @return the division of this through the oth, discarding the remainder.
            * @author Richard J. Mathar
            */
            public Ifactor divide(final Ifactor oth)
            {
                    /* todo: it'd probably be faster to cancel the gcd(this,oth) first in the prime power
                    * representation, which would avoid a more strenous factorization of the integer ratio
                    */
                    return  new Ifactor(n.divide(oth.n)) ;
            } /* Ifactor.divide */

            /** Summation with another positive integer
            * @param oth the other term.
            * @return the sum of both numbers
            * @author Richard J. Mathar
            */
            public Ifactor add(final BigInteger oth)
            {
                    /* avoid refactorization if oth is zero...
                    */
                    if ( oth.compareTo(BigInteger.ZERO) != 0 )
                            return  new Ifactor(n.add(oth)) ;
                    else
                            return this ;
            } /* Ifactor.add */

            /** Exponentiation with a positive integer.
            * @param exponent the non-negative exponent
            * @return n^exponent. If exponent=0, the result is 1.
            * @author Richard J. Mathar
            */
            public Ifactor pow(final int exponent) throws ArithmeticException
            {
                    /* three simple cases first
                    */
                    if ( exponent < 0 )
                            throw new ArithmeticException("Cannot raise "+ toString() + " to negative " + exponent) ;
                    else if ( exponent == 0)
                            return new Ifactor(1) ;
                    else if ( exponent == 1)
                            return this ;

                    /* general case, the vector with the prime factor powers, which are component-wise
                    * exponentiation of the individual prime factor powers.
                    */
                    Ifactor pows = new Ifactor(0) ;
                    for(int i=0 ; i < primeexp.size() ; i += 2)
                    {
                            Integer p = primeexp.elementAt(i) ;
                            int ex = primeexp.elementAt(i+1).intValue() ;
                            pows.primeexp.add( p ) ;
                            pows.primeexp.add(ex * exponent) ;
                    }
                    return pows ;
            } /* Ifactor.pow */

            /** Pulling the r-th root.
            * @param r the positive or negative (nonzero) root.
            * @return n^(1/r).
            *   The return value falls into the Ifactor class if r is positive, but if r is negative
            *   a Rational type is needed.
            * @since 2009-05-18
            * @author Richard J. Mathar
            */
            public Rational root(final int r) throws ArithmeticException
            {
                    if ( r == 0 )
                            throw new ArithmeticException("Cannot pull zeroth root of "+ toString()) ;
                    else if ( r < 0 )
                    {
                            /* a^(-1/b)= 1/(a^(1/b))
                            */
                            final Rational invRoot = root(-r) ;
                            return Rational.ONE.divide(invRoot) ;
                    }
                    else
                    {
                            BigInteger pows = BigInteger.ONE ;
                            for(int i=0 ; i < primeexp.size() ; i += 2)
                            {
                                    /* all exponents must be multiples of r to succeed (that is, to
                                    * stay in the range of rational results).
                                    */
                                    int ex = primeexp.elementAt(i+1).intValue() ;
                                    if ( ex % r != 0 )
                                            throw new ArithmeticException("Cannot pull "+ r+"th root of "+ toString()) ;

                                    pows.multiply( new BigInteger(""+primeexp.elementAt(i)).pow(ex/r) ) ;
                            }
                            /* convert result to a Rational; unfortunately this will loose the prime factorization */
                            return new Rational(pows) ;
                    }
            } /* Ifactor.root */


            /** The set of positive divisors.
            * @return the vector of divisors of the absolute value, sorted.
            * @since 2010-08-27
            * @author Richard J. Mathar
            */
            public Vector<BigInteger> divisors()
            {
                    /* Recursive approach: the divisors of p1^e1*p2^e2*..*py^ey*pz^ez are
                    * the divisors that don't contain  the factor pz, and the
                    * divisors that contain any power of pz between 1 and up to ez multiplied
                    * by 1 or by a product that contains the factors p1..py.
                    */
                    Vector<BigInteger> d=new Vector<BigInteger>() ;
                    if ( n.compareTo(BigInteger.ZERO) == 0 )
                            return d ;
                    d.add(BigInteger.ONE) ;
                    if ( n.compareTo(BigInteger.ONE) > 0 )
                    {
                            /* Computes sigmaIncopml(p1^e*p2^e2...*py^ey) */
                            Ifactor dp = dropPrime() ;

                            /* get ez */
                            final int ez = primeexp.lastElement().intValue() ;

                            Vector<BigInteger> partd = dp.divisors() ;

                            /* obtain pz by lookup in the prime list */
                            final BigInteger pz = new BigInteger( primeexp.elementAt(primeexp.size()-2).toString()) ;

                            /* the output contains all products of the form partd[]*pz^ez, ez>0,
                            * and with the exception of the 1, all these are appended.
                            */
                            for(int i =1 ; i < partd.size() ; i++)
                                    d.add( partd.elementAt(i) ) ;
                            for(int e =1 ; e <= ez ; e++)
                            {
                                    final BigInteger pzez = pz.pow(e) ;
                                    for(int i =0 ; i < partd.size() ; i++)
                                            d.add( partd.elementAt(i).multiply(pzez) ) ;
                            }
                    }
                    Collections.sort(d) ;
                    return d ;
            } /* Ifactor.divisors */

            /** Sum of the divisors of the number.
            * @return the sum of all divisors of the number, 1+....+n.
            * @author Richard J. Mathar
            */
            public Ifactor sigma()
            {
                    return sigma(1) ;
            } /* Ifactor.sigma */

            /** Sum of the k-th powers of divisors of the number.
            * @param k The exponent of the powers.
            * @return the sum of all divisors of the number, 1^k+....+n^k.
            * @author Richard J. Mathar
            */
            public Ifactor sigma(int k)
            {
                    /* the question is whether keeping a factorization  is worth the effort
                    * or whether one should simply multiply these to return a BigInteger...
                    */
                    if( n.compareTo(BigInteger.ONE) == 0 )
                            return  ONE ;
                    else if( n.compareTo(BigInteger.ZERO) == 0 )
                            return  ZERO ;
                    else
                    {
                            /* multiplicative: sigma_k(p^e) = [p^(k*(e+1))-1]/[p^k-1]
                            * sigma_0(p^e) = e+1.
                            */
                            Ifactor resul = Ifactor.ONE ;
                            for(int i=0 ; i < primeexp.size() ; i += 2)
                            {
                                    int ex = primeexp.elementAt(i+1).intValue() ;
                                    if ( k == 0 )
                                            resul = resul.multiply(ex+1) ;
                                    else
                                    {
                                            Integer p = primeexp.elementAt(i) ;
                                            BigInteger num = (new BigInteger(p.toString())).pow(k*(ex+1)).subtract(BigInteger.ONE) ;
                                            BigInteger deno = (new BigInteger(p.toString())).pow(k).subtract(BigInteger.ONE) ;
                                            /* This division is of course exact, no remainder
                                            * The costly prime factorization is hidden here.
                                            */
                                            Ifactor f = new Ifactor(num.divide(deno)) ;
                                            resul = resul.multiply(f) ;
                                    }
                            }
                            return resul ;
                    }
            } /* Ifactor.sigma */

            /** Divide through the highest possible power of the highest prime.
            * If the current number is the prime factor product p1^e1 * p2*e2* p3^e3*...*py^ey * pz^ez,
            * the value returned has the final factor pz^ez eliminated, which gives
            * p1^e1 * p2*e2* p3^e3*...*py^ey.
            * @return the new integer obtained by removing the highest prime power.
            *   If this here represents 0 or 1, it is returned without change.
            * @since 2006-08-20
            * @author Richard J. Mathar
            */
            public Ifactor dropPrime()
            {
                    /* the cases n==1 or n ==0
                    */
                    if ( n.compareTo(BigInteger.ONE) <= 0 )
                            return this ;

                    /* The cases n>1
                    * Start empty. Copy all but the last factor over to the result
                    * the vector with the new prime factor powers, which contain the
                    * old prime factor powers up to but not including the last one.
                    */
                    Ifactor pows=new Ifactor(0) ;
                    pows.n = BigInteger.ONE ;
                    for(int i = 0 ; i < primeexp.size()-2 ; i += 2)
                    {
                            pows.primeexp.add( primeexp.elementAt(i)) ;
                            pows.primeexp.add( primeexp.elementAt(i+1)) ;
                            BigInteger p = new BigInteger( primeexp.elementAt(i).toString() ) ;
                            int ex = primeexp.elementAt(i+1).intValue() ;
                            pows.n = pows.n.multiply( p.pow(ex) ) ;
                    }
                    return pows ;
            } /* Ifactor.dropPrime */

            /** Test whether this is a square of an integer (perfect square).
            * @return true if this is an integer squared (including 0), else false
            * @author Richard J. Mathar
            */
            public boolean issquare()
            {
                    boolean resul= true ;
                    /* check the exponents, located at the odd-indexed positions
                    */
                    for(int i=1 ; i < primeexp.size() ; i += 2)
                    {
                            if ( primeexp.elementAt(i).intValue() % 2 != 0)
                                    return false ;
                    }
                    return true  ;
            } /* Ifactor.issquare */

            /** The sum of the prime factor exponents, with multiplicity.
            * @return the sum over the primeexp numbers
            * @author Richard J. Mathar
            */
            public int bigomega()
            {
                    int resul= 0 ;
                    for(int i=1 ; i < primeexp.size() ; i += 2)
                            resul += primeexp.elementAt(i).intValue() ;
                    return(resul) ;
            } /* Ifactor.bigomega */

            /** The sum of the prime factor exponents, without multiplicity.
            * @return the number of distinct prime factors.
            * @since 2008-10-16
            * @author Richard J. Mathar
            */
            public int omega()
            {
                    return primeexp.size()/2 ;
            } /* Ifactor.omega */

            /** The square-free part.
            * @return the minimum m such that m times this number is a square.
            * @since 2008-10-16
            * @author Richard J. Mathar
            */
            public BigInteger core()
            {
                    BigInteger resul = BigInteger.ONE ;
                    for(int i=0 ; i < primeexp.size() ; i += 2)
                            if ( primeexp.elementAt(i+1).intValue() % 2 != 0)
                                    resul = resul.multiply( new BigInteger(primeexp.elementAt(i).toString()) );
                    return resul ;
            } /* Ifactor.core */

            /** The Moebius function.
            * 1 if n=1, else, if k is the number of distinct prime factors, return (-1)^k,
            * else, if k has repeated prime factors, return 0.
            * @return the moebius function.
            * @author Richard J. Mathar
            */
            public int moebius()
            {
                    if( n.compareTo(BigInteger.ONE) <= 0 )
                            return 1 ;
                    /* accumulate number of different primes in k */
                    int k=1 ;
                    for(int i=0 ; i < primeexp.size() ; i += 2)
                    {
                            final int e = primeexp.elementAt(i+1).intValue() ;
                            if ( e > 1 )
                                    return 0 ;
                            else if ( e == 1)
                                    /* accumulates (-1)^k */
                                    k *= -1 ;

                    }
                    return( k ) ;
            } /* Ifactor.moebius */

            /** Maximum of two values.
            * @param oth the number to compare this with.
            * @return the larger of the two values.
            * @author Richard J. Mathar
            */
            public Ifactor max(final Ifactor oth)
            {
                    if( n.compareTo(oth.n) >= 0 )
                            return this ;
                    else
                            return oth  ;
            } /* Ifactor.max */

            /** Minimum of two values.
            * @param oth the number to compare this with.
            * @return the smaller of the two values.
            * @author Richard J. Mathar
            */
            public Ifactor min(final Ifactor oth)
            {
                    if( n.compareTo(oth.n) <= 0 )
                            return this ;
                    else
                            return oth ;
            } /* Ifactor.min */

            /** Maximum of a list of values.
            * @param set list of numbers.
            * @return the largest in the list.
            * @author Richard J. Mathar
            */
            public static Ifactor max(final Vector<Ifactor> set)
            {
                    Ifactor resul = set.elementAt(0) ;
                    for(int i=1; i < set.size() ; i++)
                            resul = resul.max(set.elementAt(i)) ;
                    return resul ;
            } /* Ifactor.max */

            /** Minimum of a list of values.
            * @param set list of numbers.
            * @return the smallest in the list.
            * @author Richard J. Mathar
            */
            public static Ifactor min(final Vector<Ifactor> set)
            {
                    Ifactor resul = set.elementAt(0) ;
                    for(int i=1; i < set.size() ; i++)
                            resul = resul.min(set.elementAt(i)) ;
                    return resul ;
            } /* Ifactor.min */

            /** Compare value against another Ifactor
            * @param oth The value to be compared agains.
            * @return 1, 0 or -1 according to being larger, equal to or smaller than oth.
            * @since 2012-02-15
            * @author Richard J. Mathar
            */
            public int compareTo( final Ifactor oth)
            {
                    return n.compareTo(oth.n) ;
            } /* compareTo */

            /** Convert to printable format
            * @return a string of the form n:prime^pow*prime^pow*prime^pow...
            * @author Richard J. Mathar
            */
            public String toString()
            {
                    String resul = new String(n.toString()+":") ;
                    if ( n.compareTo(BigInteger.ONE) == 0 )
                            resul += "1" ;
                    else
                    {
                            boolean firstMul = true ;
                            for(int i=0 ; i < primeexp.size() ; i += 2)
                            {
                                    if ( ! firstMul)
                                            resul += "*" ;
                                    if ( primeexp.elementAt(i+1).intValue()  > 1 )
                                            resul += primeexp.elementAt(i).toString()+"^"+primeexp.elementAt(i+1).toString() ;
                                    else
                                            resul +=  primeexp.elementAt(i).toString() ;
                                    firstMul = false ;
                            }
                    }
                    return resul ;
            } /* Ifactor.toString */
    } /* Ifactor */

    /** Prime numbers.
    * The implementation is a very basic computation of the set of all primes
    * on demand, growing infinitely without any defined upper limit.
    * The effects of such scheme are (i) the lookup-times become shorter after
    * a while as more and more primes have been used and stored. The applications
    * appear to become faster.  (ii) Using the implementation for factorizations
    * may easily require all available memory and stall finally, because indeed
    * a dense list of primes with growing upper bound is kept without any hashing or lagging scheme.
    * @since 2006-08-11
    * @author Richard J. Mathar
    */
    private static class Prime
    {
            /** The list of all numbers as a vector.
            */
            static Vector<BigInteger> a = new Vector<BigInteger>();

            /** The maximum integer covered by the high end of the list.
            */
            static protected BigInteger nMax = new BigInteger("-1");

            /** Default constructor initializing a list of primes up to 17.
            * 17 is enough to call the Miller-Rabin tests on the first 7 primes without further
            * action.
            * @author Richard J. Mathar
            */
            public Prime()
            {
                    if ( a.size() == 0 )
                    {
                            a.add(new BigInteger(""+2)) ;
                            a.add(new BigInteger(""+3)) ;
                            a.add(new BigInteger(""+5)) ;
                            a.add(new BigInteger(""+7)) ;
                            a.add(new BigInteger(""+11)) ;
                            a.add(new BigInteger(""+13)) ;
                            a.add(new BigInteger(""+17)) ;
                    }
                    nMax = a.lastElement() ;
            }

            /** Test if a number is a prime.
            * @param n the integer to be tested for primality
            * @return true if prime, false if not
            * @author Richard J. Mathar
            */
            public boolean contains(BigInteger n)
            {
                    /* not documented
                    * return ( n.isProbablePrime() ) ;
                    */
                    switch ( millerRabin(n) )
                    {
                    case -1:
                            return false ;
                    case 1:
                            return true ;
                    }
                    growto(n) ;
                    return( a.contains(n) ) ;
            }

            /** Test whether a number n is a strong pseudoprime to base a.
            * @param n the integer to be tested for primality
            * @param a the base
            * @return true if the test is passed, so n may be a prime.
            *   false if the test is not passed, so n is not a prime.
            * @author Richard J. Mathar
            * @since 2010-02-25
            */
            public boolean isSPP(final BigInteger n, final BigInteger a)
            {
                    final BigInteger two = new BigInteger(""+2) ;


                    /* numbers less than 2 are not prime
                    */
                    if ( n.compareTo(two) == -1 )
                            return false ;
                    /* 2 is prime
                    */
                    else if ( n.compareTo(two) == 0 )
                            return true ;
                    /* even numbers >2 are not prime
                    */
                    else if ( n.remainder(two).compareTo(BigInteger.ZERO) == 0 )
                            return false ;
                    else
                    {
                            /* q= n- 1 = d *2^s with d odd
                            */
                            final BigInteger q = n.subtract(BigInteger.ONE) ;
                            int s = q.getLowestSetBit() ;
                            BigInteger d = q.shiftRight(s) ;

                            /* test whether a^d = 1 (mod n)
                            */
                            if ( a.modPow(d,n).compareTo(BigInteger.ONE) == 0 )
                                    return true ;

                            /* test whether a^(d*2^r) = -1 (mod n), 0<=r<s
                            */
                            for(int r=0; r < s ; r++)
                            {
                                    if ( a.modPow(d.shiftLeft(r),n).compareTo(q) == 0 )
                                            return true ;
                            }
                            return false ;
                    }
            }

            /** Miller-Rabin primality tests.
            * @param n The prime candidate
            * @return -1 if n is a composite, 1 if it is a prime, 0 if it may be a prime.
            * @since 2010-02-25
            * @author Richard J. Mathar
            */
            public int millerRabin(final BigInteger n)
            {
                    /* list of limiting numbers which fail tests on k primes, A014233 in the OEIS
                    */
                    final String[] mr ={"2047", "1373653", "25326001", "3215031751", "2152302898747", "3474749660383",
                            "341550071728321"} ;
                    int mrLim = 0 ;
                    while( mrLim < mr.length )
                    {
                            int l = n.compareTo(new BigInteger(mr[mrLim])) ;
                            if ( l < 0 )
                                    break;
                            /* if one of the pseudo-primes: this is a composite
                            */
                            else if ( l == 0 )
                                    return -1 ;
                            mrLim++ ;
                    }
                    /* cannot test candidates larger than the last in the mr list
                    */
                    if ( mrLim == mr.length)
                            return 0;

                    /* test the bases prime(1), prime(2) up to prime(mrLim+1)
                    */
                    for(int p =0 ; p <= mrLim ; p++)
                            if ( isSPP(n, at(p)) == false )
                                    return -1;
                    return 1;
            }

            /** return the ith prime
            * @param i the zero-based index into the list of primes
            * @return the ith prime. This is 2 if i=0, 3 if i=1 and so forth.
            * @author Richard J. Mathar
            */
            public BigInteger at(int i)
            {
                    /* If the current list is too small, increase in intervals
                    * of 5 until the list has at least i elements.
                    */
                    while ( i >= a.size() )
                    {
                            growto(nMax.add(new BigInteger(""+5))) ;
                    }
                    return ( a.elementAt(i) ) ;
            }

            /** return the count of primes less than or equal to n
            * @param n the upper limit of the scan
            * @return 0 if n is less than 2; 1 if n=2; 2 if n=3 or 4; 3 if n=5 or 6; and so forth.
            * @author Richard J. Mathar
            */
            public BigInteger pi(BigInteger n)
            {
                    /* If the current list is too small, increase in intervals
                    * of 5 until the list has at least i elements.
                    */
                    growto(n) ;
                    BigInteger r = new BigInteger("0") ;
                    for(int i=0 ; i<a.size() ; i++)
                            if ( a.elementAt(i).compareTo(n) <= 0 )
                                    r = r.add(BigInteger.ONE) ;
                    return r ;
            }

            /** return the smallest prime larger than n
            * @param n lower limit of the search
            * @return the next larger prime.
            * @since 2008-10-16
            * @author Richard J. Mathar
            */
            public BigInteger nextprime(BigInteger n)
            {
                    /* if n <=1, return 2 */
                    if ( n.compareTo(BigInteger.ONE) <= 0)
                            return ( a.elementAt(0) ) ;

                    /* If the currently largest element in the list is too small, increase in intervals
                    * of 5 until the list has at least i elements.
                    */
                    while ( a.lastElement().compareTo(n) <= 0)
                    {
                            growto(nMax.add(new BigInteger(""+5))) ;
                    }
                    for(int i=0 ; i < a.size() ; i++)
                            if ( a.elementAt(i).compareTo(n) == 1)
                                    return ( a.elementAt(i) ) ;
                    return ( a.lastElement() ) ;
            }

            /** return the largest prime smaller than n
            * @param n upper limit of the search
            * @return the next smaller prime.
            * @since 2008-10-17
            * @author Richard J. Mathar
            */
            public BigInteger prevprime(BigInteger n)
            {
                    /* if n <=2, return 0 */
                    if ( n.compareTo(BigInteger.ONE) <= 0)
                            return BigInteger.ZERO ;

                    /* If the currently largest element in the list is too small, increase in intervals
                    * of 5 until the list has at least i elements.
                    */
                    while ( a.lastElement().compareTo(n) < 0)
                            growto(nMax.add(new BigInteger(""+5))) ;

                    for(int i=0 ; i < a.size() ; i++)
                            if ( a.elementAt(i).compareTo(n) >= 0)
                                    return ( a.elementAt(i-1) ) ;
                    return ( a.lastElement() ) ;
            }

            /** extend the list of known primes up to n
            * @param n the maximum integer known to be prime or not prime after the call.
            * @author Richard J. Mathar
            */
            protected void growto(BigInteger n)
            {
                    while( nMax.compareTo(n) == -1)
                    {
                            nMax = nMax.add(BigInteger.ONE) ;
                            boolean isp = true ;
                            for(int p=0; p < a.size() ; p++)
                            {
                                    /*
                                    * Test the list of known primes only up to sqrt(n)
                                    */
                                    if ( a.get(p).multiply(a.get(p)).compareTo(nMax) == 1 )
                                            break ;

                                    /*
                                    * The next case means that the p'th number in the list of known primes divides
                                    * nMax and nMax cannot be a prime.
                                    */
                                    if ( nMax.remainder(a.get(p)).compareTo(BigInteger.ZERO) == 0 )
                                    {
                                            isp = false ;
                                            break ;
                                    }
                            }
                            if( isp )
                                    a.add(nMax) ;
                    }
            }
    } /* Prime */
} /* com.github.rccookie.math.BigDecimalMath */
