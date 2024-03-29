# SCalc
Simple calculation library to parse and calculate math expressions.

Internal calculation will be performed with BigDecimal's to avoid floating point errors.


## Usage
### Maven
SCalc can be used by adding the following dependency to your maven pom.xml:
```
<dependency>
  <groupId>io.github.matwein</groupId>
  <artifactId>scalc-core</artifactId>
  <version>2.1.0</version>
</dependency>
```

### General
Every calculation starts with the SCalcBuilder:
```
double result = SCalcBuilder.doubleInstance()
    .expression("a + b - c")
    .build()
    .parameter("a", 10)
    .parameter("b", 5)
    .parameter("c", 15)
    .calc();
```

The types of parameters and the result can be freely selected. You can enter every types inherited from java.lang.Number. You can also define custom types by using converters:
```
BigDecimal result = SCalcBuilder.bigDecimalInstance()
    .expression("4 * 4 - var1")
    .build()
    .parameter("var1", new BigDecimal(2))
    .calc();
    
float result = SCalcBuilder.floatInstance()
    .expression("10.2 - 0.3")
    .buildAndCalc();
    
int result = SCalcBuilder.integerInstance()
    .expression("10.2 - 0.3")
    .buildAndCalc();
```

Further examples can be found here: https://github.com/MatWein/SCalc/blob/master/src/test/java/scalc/SCalcTest.java

### Parameter
Parameters for the calculation can be added by using:  
- the .parameter(name, number) method  
- the .parameter(name, number[]) method  
- the .parameter(name, collection<number>) method  
- the .parameter(map) method  
- the .parameter(...) method. This method can be used in form of .parameter("a", 10, "b", 100, "c", 20) or to add parameters like .parameter(10, 100, 20) which will result in param0 = 10,100,20.

Hint: It is also possible to give an extract function to the parameter methods to extract nested properties. Example:
```
List<TestDto> dtos = new ArrayList<>(); ...

double result = SCalcBuilder.doubleInstance()
    .sumExpression()
    .build()
    .parameter(TestDto::getValueToExtract, dtos)
    .calc();
```

Hint: Collections and arrays can also be nested. Example:
```
SCalcBuilder.doubleInstance()
	.expression("sum(ALL_PARAMS)")
	.build()
	.parameter(List.of(10.0, 20.0, 1.0))
	.parameter(Set.of(10.0, 20.0, List.of(0.5, 0.5)))
	.parameter(new Object[] { 10.0, 20.0, new Double[] { 1.0 } })
	.parameter("test", new Number[] { 10.0, 20.0, 1.0 })
	.parameter(new Object[] { new long[] { 10L }, new double[] { 20.0 }, new int[] { 1 } })
	.calc()
```

Result will be 155.

```
List<TestDto> list1 = List.of(new TestDto(10.0), new TestDto(20.0));
List<TestDto> list2 = List.of(new TestDto(30.0), new TestDto(40.0), new TestDto(50.0));
List<TestDto> list3 = List.of(new TestDto(0.0), new TestDto(70.0));

double result = SCalcBuilder.doubleInstance()
	.expression("sum(list1) * sum(list2) * sum(list3)")
	.build()
	.parameter(TestDto::getValueToExtract, "list1", list1)
	.parameter(TestDto::getValueToExtract, "list2", list2)
	.parameter(TestDto::getValueToExtract, "list3", list3)
	.calc();
```

Result will be 252000.

## Custom type converters
Besides the standard Java types, there is the possibility to define your own types. Important to know is, that there are global type converters and local type converters. Global means that every new instance of SCalc will have it. Local type convertes on the other hand have to be declared on every builder call:

### Global
```
SCalcBuilder.registerGlobalConverter(Money.class, MoneyConverter.class);

Money result = SCalcBuilder.instanceFor(Money.class)
    .expression("var1 - var2")
    .build()
    .parameter("var1", 10.9, "var2", 0.9)
    .calc();
```

### Local
```
Money result = SCalcBuilder.instanceFor(Money.class)
    .expression("(√(16, 4) + 2) / (99.99 - 79.99 - 16)")
    .registerConverter(Money.class, MoneyConverter.class)
    .build()
    .parameter(params)
    .calc();
```

### Implicit
If you don't want to register type converters there is the possibility to extend your custom number types with the interface **INumber**. Attention: custom types extending the INumber interface cannot be used as return type because SCalc has no idea on how to create a new instance of this type.
```
public class Percentage implements INumber { ... }

double result = SCalcBuilder.doubleInstance()
    .subtractExpression()
    .build()
    .parameter(new Percentage(0.0001))
    .parameter(new Percentage(0.0006))
    .calc();
```


## Custom user functions
In some cases it may be helpful to define custom calculation functions to use in expressions.  
This can be done like in the following examples.

### Global
```
SCalcBuilder.registerGlobalUserFunction("percent", (options, functionParams) -> functionParams.get(0)
    .multiply(new BigDecimal(100))
    .divide(functionParams.get(1), options.getCalculationScale(), options.getCalculationRoundingMode())
    .setScale(options.getCalculationScale(), options.getCalculationRoundingMode()));

int result = SCalcBuilder.integerInstance()
    .expression("percent(12, 200) + percent(6, 100)")
    .buildAndCalc();
```

### Local
```
FunctionImpl function = (options, functionParams) -> functionParams.get(0).multiply(new BigDecimal(-1).setScale(
    options.getCalculationScale(),
    options.getCalculationRoundingMode())
);
		
int result = SCalcBuilder.integerInstance()
    .expression("negate(2) + 1")
    .registerUserFunction("negate", function)
    .buildAndCalc();
```


## Predefined content
The SCalc library has some predefined functions and constants that can be used in every expression. Keep in mind that this functions and constants can have multiple aliases. For a full listing see:  
- scalc.internal.functions.Functions  
- scalc.internal.constants.Constants

### Functions
- root(value, root=2)  
description: Calculates the xth root. If no second param is given, it calculates the square root.  
aliases: √, wurzel  
example: √(16) = 4, Root(16, 4) = 2  
- sum(...)  
description: Calculates the sum of all given parameters. May be empty.  
aliases: ∑, summe  
example: ∑(16) = 16, Summe(16, 4) = 20, sum() = 0  
- max(...)  
description: Returns the maximum number of all given parameters.  
example: max(16, 4, 24, 1) = 24  
- min(...)  
description: Returns the minimum number of all given parameters.  
example: max(16, 4, 24, 1) = 1  
- avg(...)  
description: Returns the maximum number of all given parameters.  
aliases: durchschnitt  
example: avg(16, 4, 24, 1) = 11.25  
- abs(value)  
description: Returns the absolute number of the given parameter.  
example: abs(-16) = 16
- round(value, scale=2, roundingMode=HALF_UP)  
description: Sets the rounding scale for a given value.  
example: round(22.999999), round(0.12121212, 4), round(0.5, 1, HALF_DOWN)
- sin(value)  
description: Returns the sinus of the given value.  
example: sin(6) = -0.27941549819
- cos(value)  
description: Returns the cosinus of the given value.  
example: cos(6) = 0.96017028665
- tan(value)  
description: Returns the tangens of the given value.  
example: tan(6) = -0.29100619138
- ln(value)  
description: Returns the natural logarithm (base e) of the given value.  
example: ln(6) = 1.79175946923
- log(value)  
description: Returns the base 10 logarithm of the given value.  
example: log(6) = 0.77815125038

### Constants
- PI  
value: 3.14159265358979323846  
aliases: π  
- ALL_PARAMS  
value: All given parameters. Can be used for example for the sum calculation: ∑(ALL_PARAMS)  
example: 12, 7, 1  
- E  
value: 2.7182818284590452354  
description: Euler number


## Utility classes
There are some shortcut methods you can use to reduce boilerplate code:

### Round value
```
double result = SCalcUtil.round(10.55, 1, RoundingMode.HALF_UP, double.class)
double result = SCalcUtil.round(10.5567, 2);
Money result = SCalcUtil.round(new Money(0.000012), 5);
BigDecimal result = SCalcUtil.round(-999.99, 2, BigDecimal.class);
```

### Calculate sum
```
double result = SCalcUtil.summarize(double.class, -10.0, 15.0, 60.1);
double result = SCalcUtil.summarize(double.class, 1.0, BigDecimal.valueOf(2.0), 7L);
Double result = SCalcUtil.summarizeCollection(Double.class, numbers, Money::getValue);
```

## Expressions
There are three basic types of expressions you can use with SCalc:

### Single operator expressions
This is the smallest form of expression. You can only enter one single operator, like "+", "-", "*", "/", "^". This will set the operator between every operand/parameter and calculates the result:
```
double result = SCalcBuilder.doubleInstance()
    .expression("+")
    .build()
    .parameter(2, 3, 2)
    .calc();
```
Result will be 2+3+2=7.

It is recommended to use the following expression methods:
```
.sumExpression() == .expression(SCalcExpressions.SUM_EXPRESSION) == .expression("+")
.subtractExpression() == .expression(SCalcExpressions.SUBTRACT_EXPRESSION) == .expression("-")
.multiplyExpression() == .expression(SCalcExpressions.MULTIPLY_EXPRESSION) == .expression("*")
.divideExpression() == .expression(SCalcExpressions.DIVIDE_EXPRESSION) == .expression("/")
.powExpression() == .expression(SCalcExpressions.POW_EXPRESSION) == .expression("^")
```

### Standard expressions
Standard expressions were used in previous examples. It is a single line math expression:
```
Map<String, Object> params = new HashMap<>();
params.put("a", 10);
params.put("b", 2);
    
BigDecimal result = SCalcBuilder.bigDecimalInstance()
    .expression("a + b * √(16)")
    .resultScale(1, RoundingMode.HALF_UP)
    .build()
    .parameter(params)
    .calc();
```
Result will be 18.

You can also use pow chars in your expressions:
```
BigDecimal result = SCalcBuilder.bigDecimalInstance()
    .expression("3² + 2³")
    .buildAndCalc();
```

### Definition expressions
This is the most complex and powerful kind of expression. You can enter multiple lines separated with a semicolon. Each line contains a function or varible definition/assignment. At the end you have to write a return statement to calculate the result:
```
double result = SCalcBuilder.doubleInstance()
    .expression(
        "f(x, y)=10 + (x * y) - 1;" +
        "g(x)=wurzel(x);" +
        "variable1=7;" +
        "return f(2, 3) + g(4) - variable1;")
    .buildAndCalc();
```
Result will be 10.


## Reason
So what is the sense about using this library? First reason could be a better readability. But the most important reason is to avoid floating point errors. The next simple JUnit test shows the problem:
```
@Test
public void calc_FloatingPoint1() {
    Double result = SCalcBuilder.doubleInstance()
            .expression("a + b")
            .build()
            .parameter("a", 0.7)
            .parameter("b", 0.1)
            .calc();

    Assert.assertEquals(0.8, result, 0);
    Assert.assertEquals(0.7999999999999999, 0.7 + 0.1, 0);

    result = SCalcBuilder.doubleInstance()
            .expression("0.9 - 0.1")
            .buildAndCalc();

    Assert.assertEquals(0.8, result, 0);
    Assert.assertEquals(0.8, 0.9 - 0.1, 0);
}
```
If you simply write 0.7 + 0.1 in Java the result will be 0.7999999999999999. If you now multiply this with a larger number, your result will be wrong. SCalc avoids this problem by using BigDecimals for internal calculation.

Another reason is, that you can specify the calculation precision. **Per default all calculation will be done with a scale of 10 and the result also will be rounded (HALF_UP) to 10 digits.** (See scalc.SCalcOptions.DEFAULT_SCALE)

## Calculation within iteration
For calculations within iterations, it is recommended to create only one SCalc instance and reuse it. See following example:

```
SCalc<Long> sCalc = SCalcBuilder.longInstance()
    .expression("f(a, b)=√(a² - (b² / 2)); return f(a, b);")
    .resultScale(64)
    .calculationScale(64)
    .build();
                
for (long i = 0; i < 100000; i++) {
    long result = sCalc
        .parameter("a", i)
        .parameter("b", i)
        .calc();
    System.out.println(String.format("%s >> %s", i, result));
}
```

## Debugging
SCalc can print calculation steps if needed. Example:
```
double result = SCalcBuilder.doubleInstance()
    .expression("summe_alle = sum(ALL_PARAMS); faktor(x) = param0 * param2 * x; return summe_alle / faktor(3);")
    .debug(true)
    .debugLogger(message -> System.out.println(message))
    .build()
    .parameter(TestDto::getValueToExtract, dtos)
    .calc();
```

## Java compatibility
SCalc Version | Compatible with Java Versions
--- | ---
\<= 1.2.0 | \>= 7
\>= 1.3.0 | \>= 8
\>= 2.0.0 | \>= 11
