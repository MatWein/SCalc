package scalc;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.function.Function;

public class SCalcUtil {
	/**
	 * Calculates the sum of all elements within the given collection.<br/>
	 * Attention: Be aware of the maximum calculation scale in <code>scalc.SCalcOptions#DEFAULT_SCALE</code>.
	 * If you need an higher scale use SCalcBuilder directly instead of this shortcut method!
	 * @param returnType Type to convert the result number into.
	 * @param numbersToSummarize Elements to calculate sum.
	 */
	public static <RETURN_TYPE> RETURN_TYPE summarizeCollection(Class<RETURN_TYPE> returnType, Collection<?> numbersToSummarize) {
		return SCalcBuilder.instanceFor(returnType)
				.sumExpression()
				.build()
				.paramsAsCollection(numbersToSummarize)
				.calc();
	}
	
	/**
	 * Calculates the sum of all (extracted) elements within the given collection.<br/>
	 * Attention: Be aware of the maximum calculation scale in <code>scalc.SCalcOptions#DEFAULT_SCALE</code>.
	 * If you need an higher scale use SCalcBuilder directly instead of this shortcut method!
	 * @param returnType Type to convert the result number into.
	 * @param numbersToSummarize Elements to calculate sum.
	 * @param paramExtractor Extractor will be called for each element within the collection. The output will be summarized.
	 */
	public static <RETURN_TYPE, INPUT_TYPE> RETURN_TYPE summarizeCollection(
			Class<RETURN_TYPE> returnType,
			Collection<INPUT_TYPE> numbersToSummarize,
			Function<INPUT_TYPE, Object> paramExtractor) {
		
		return SCalcBuilder.instanceFor(returnType)
				.sumExpression()
				.build()
				.paramsAsCollection(paramExtractor, numbersToSummarize)
				.calc();
	}
	
	/**
	 * Calculates the sum of all given elements.<br/>
	 * Attention: Be aware of the maximum calculation scale in <code>scalc.SCalcOptions#DEFAULT_SCALE</code>.
	 * If you need an higher scale use SCalcBuilder directly instead of this shortcut method!
	 * @param returnType Type to convert the result number into.
	 * @param numbersToSummarize Elements to calculate sum.
	 */
	public static <RETURN_TYPE> RETURN_TYPE summarize(Class<RETURN_TYPE> returnType, Object... numbersToSummarize) {
		return SCalcBuilder.instanceFor(returnType)
				.sumExpression()
				.build()
				.params(numbersToSummarize)
				.calc();
	}
	
	/**
	 * Rounds the given input to the wished scale using RoundingMode.HALF_UP.
	 * @param numberToRound Any number or objects if there was a global number converter was registered.
	 * @param scale The wished result scale.
	 */
	public static <RETURN_TYPE> RETURN_TYPE round(RETURN_TYPE numberToRound, int scale) {
		return round(numberToRound, scale, RoundingMode.HALF_UP);
	}
	
	/**
	 * Rounds the given input to the wished scale.
	 * @param numberToRound Any number or objects if there was a global number converter was registered.
	 * @param scale The wished result scale.
	 * @param roundingMode Rounding mode to use.
	 */
	public static <RETURN_TYPE> RETURN_TYPE round(RETURN_TYPE numberToRound, int scale, RoundingMode roundingMode) {
		if (numberToRound == null) {
			return null;
		}
		
		return (RETURN_TYPE)round(numberToRound, scale, roundingMode, numberToRound.getClass());
	}
	
	/**
	 * Rounds the given input to the wished scale using RoundingMode.HALF_UP and converts the result to the given return type.
	 * @param numberToRound Any number or objects if there was a global number converter was registered.
	 * @param scale The wished result scale.
	 * @param returnType Return type to convert the result into.
	 */
	public static <RETURN_TYPE> RETURN_TYPE round(
			Object numberToRound,
			int scale,
			Class<RETURN_TYPE> returnType) {
		
		return round(numberToRound, scale, RoundingMode.HALF_UP, returnType);
	}
	
	/**
	 * Rounds the given input to the wished scale and converts the result to the given return type.
	 * @param numberToRound Any number or objects if there was a global number converter was registered.
	 * @param scale The wished result scale.
	 * @param roundingMode Rounding mode to use.
	 * @param returnType Return type to convert the result into.
	 */
	public static <RETURN_TYPE> RETURN_TYPE round(
			Object numberToRound,
			int scale,
			RoundingMode roundingMode,
			Class<RETURN_TYPE> returnType) {
		
		if (numberToRound == null) {
			return null;
		}
		
		return SCalcBuilder.instanceFor(returnType)
				.expression("return ALL_PARAMS;")
				.calculationScale(scale, roundingMode)
				.resultScale(scale, roundingMode)
				.build()
				.params(numberToRound)
				.calc();
	}
}
