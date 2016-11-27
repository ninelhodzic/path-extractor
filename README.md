# path-extractor

Simple value extractor from Map<String,Object> object.

Sample:

```
Map<String,Object> map = new HashMap<>();
map.put("name", "value");
Map<String,Object> mapChild = new HashMap<>();
map.put("child", 10);

String expression = "$name.child$" // or expression="name.child";
PathExtractor pathExtractor = new PathExtractor(map);
Number val = pathExtractor.evaluate(expression);

Assert.assertTrue(val == 10);

```