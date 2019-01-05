# SCalc
Simple calculation library to parse and calculate math expressions.

Internal calculation will be performed with BigDecimal's to avoid floating point errors.


## Maven
SCalc is available as maven dependency:
```
<not yet finished>
```


## Usage
### General
Every calculation start with the SCalcBuilder:
```
Double result = SCalcBuilder.doubleInstance()
    .expression("a + b - c")
    .parameter("a", 10)
    .parameter("b", 5)
    .parameter("c", 15)
    .build()
    .calc();
```

The types of parameters and the result can be freely selected. You can enter every types inherited from java.lang.Number. You can also define custom types by using converters:
```
BigDecimal result = SCalcBuilder.bigDecimalInstance()
    .expression("4 * 4 - var1")
    .parameter("var1", new BigDecimal(2))
    .build()
    .calc();
```

### Parameter
Parameters for the calculation can be added by using:  
- the .parameter(name, number) method  
- the .params(map) method  
- the .params(...) method. This method can be used in form of .params("a", 10, "b", 100, "c", 20) or to add unnamed parameters like .params(10, 100, 20)


## Custom type converters
Besides the standard Java types, there is the possibility to define your own types. Important to know is, that there are global type converters and local type converters. Global means that every new instance of SCalc will have it. Local type convertes on the other hand have to be declared on every builder call:

### Global
```
SCalcBuilder.registerGlobalConverter(Money.class, MoneyConverter.class);

Money result = SCalcBuilder.instanceFor(Money.class)
    .expression("var1 - var2")
    .params("var1", 10.9, "var2", 0.9)
    .build()
    .calc();
```

### Local
```
Money result = SCalcBuilder.instanceFor(Money.class)
    .expression("(√(16, 4) + 2) / (99.99 - 79.99 - 16)")
    .params(params)
    .registerConverter(Money.class, MoneyConverter.class)
    .build()
    .calc();
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


## Expressions
There are three basic types of expressions you can use with SCalc:

### Single operator expressions
This is the smallest form of expression. You can only enter one single operator, like "+", "-", "*", "/", "^". This will set the operator between every operand/parameter and calculates the result:
```
Double result = SCalcBuilder.doubleInstance()
    .expression("+")
    .params(2, 3, 2)
    .build()
    .calc();
```
Result will be 2+3+2=7.

### Standard expressions
Standard expressions were used in previous examples. It is a single line math expression:
```
Map<String, Object> params = new HashMap<>();
params.put("a", 10);
params.put("b", 2);
    
BigDecimal result = SCalcBuilder.bigDecimalInstance()
    .expression("a + b * √(16)")
    .params(params)
    .resultScale(1, RoundingMode.HALF_UP)
    .build()
    .calc();
```
Result will be 18.

### Definition expressions
This is the most complex and powerful kind of expression. You can enter multiple lines separated with a semicolon. Each line contains a function or varible definition/assignment. At the end you have to write a return statement to calculate the result:
```
Double result = SCalcBuilder.doubleInstance()
    .expression(
        "f(x, y)=10 + (x * y) - 1;" +
        "g(x)=wurzel(x);" +
        "variable1=7;" +
        "return f(2, 3) + g(4) - variable1;")
    .build()
    .calc();
```
Result will be 10.


## Reason
So what is the sense about using this library? First reason could be a better readability. But the most important reason is to avoid floating point errors. The next simple JUnit test shows the problem:
```
@Test
public void calc_FloatingPoint1() {
    Double result = SCalcBuilder.doubleInstance()
            .expression("a + b")
            .parameter("a", 0.7)
            .parameter("b", 0.1)
            .build()
            .calc();

    Assert.assertEquals(0.8, result, 0);
    Assert.assertEquals(0.7999999999999999, 0.7 + 0.1, 0);

    result = SCalcBuilder.doubleInstance()
            .expression("0.9 - 0.1")
            .build()
            .calc();

    Assert.assertEquals(0.8, result, 0);
    Assert.assertEquals(0.8, 0.9 - 0.1, 0);
}
```
If you simply write 0.7 + 0.1 in Java the result will be 0.7999999999999999. If you now multiply this with a larger number, your result will be wrong. SCalc avoids this problem by using BigDecimals for internal calculation.

Another reason is, that you can specify the calculation precision. Per default all calculation will be done with a scale of 10 and the result also will be rounded (HALF_UP) to 10 digits.