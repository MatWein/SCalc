# SCalc
Simple calculation library to parse and calculate math expressions.

Internal calculation will be performed with BigDecimal's to avoid floating point errors.

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