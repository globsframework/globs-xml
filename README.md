The gaol of this library is to parse and generate XML from a Glob.
By default each value of a simple field is an attribut.
Exporting a Glob named dummyObject with two field id dans name will produce:

```
"<dummyObject id='1' name='foo'/>"
```

To produce xml use :

```
XmlGlobBuilder.write(glob, writer);
```

And to parse xml :

```
Glob glob = XmlGlobReader.read(globTypeName -> GlobType, reader); 
```

### annotation

With annotation, it is possible de change the way a field is managed.

If a field has the annotation XmlNode, then the output will be :

```
"<dummyObject id='1'>
   <name>foo</name>
 </dummy>"
```

If a field has the annotation XmlValue or XmlValueAsCData then the output is :

```
"<dummyObject id='1'>foo</dummy>"
```

Inner object are inserted like following (a real exemple with namespace ):

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="http://www.cegid.fr/Retail/1.0">
   <soapenv:Body>
      <ns:GetCustomerDetail>
         <ns:customerId>001000000018</ns:customerId>
         <ns:priosWithParentNS xmlns:prios="http://www.cegid.fr/Retail/1.0">
            <prios:customerId>001000000018</prios:customerId>
         </ns:priosWithParentNS>
      </ns:GetCustomerDetail>
   </soapenv:Body>
</soapenv:Envelope>
```




