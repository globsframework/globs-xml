# Globs XML

Parse and generate XML from a [Glob](https://globsframework.org). The `GlobType` describes the document —
its fields are the attributes and the child nodes — and annotations move a field between attribute, node,
text value and CDATA, or attach a namespace. Built on
[saxstack](https://github.com/globsframework/saxstack), so nothing but the branch being read or written is
held in memory.

## Requirements

Java 21, `org.globsframework:globs` and `saxstack`.

## Installation

```xml
<dependency>
    <groupId>org.globsframework</groupId>
    <artifactId>globs-xml</artifactId>
    <version>5.2.0</version>
</dependency>
```

## Reading and writing a Glob

By default every simple field is an **attribute**. Exporting a Glob named `dummyObject` with two fields `id`
and `name` produces:

```xml
<dummyObject id='1' name='foo'/>
```

```java
XmlGlobBuilder.write(glob, writer);                              // write
Glob glob = XmlGlobReader.read(globTypeName -> GlobType, reader); // read
```

The reader takes a `GlobTypeAccessor` — a function from the tag name to the `GlobType` to instantiate — so
the same reader serves a model resolved at runtime. `XmlGlobBuilder.write(glob, writer, true)` and
`XmlGlobReader.read(accessor, reader, true)` flip the default the other way: every field becomes a node
instead of an attribute.

## Annotations

| Annotation | Effect on a field |
| --- | --- |
| `XmlAsNode` | the field becomes a child node instead of an attribute; `create(name, mandatory)` renames it and makes it required |
| `XmlValue` | the field is the tag's text content |
| `XmlValueAsCData` | same, wrapped in a `CDATA` section |
| `ValueIsXml` | the String field already holds XML — it is written through untouched and read back verbatim |
| `XmlExportDateFormat` | the pattern (and zone) used to render a date/time field |
| `XmlNS` (on the type) | declares the namespace prefix and URL of that node |
| `XmlUseParentNS` | the node reuses its parent's namespace instead of declaring its own |

With `XmlAsNode` on `name`:

```xml
<dummyObject id='1'>
   <name>foo</name>
</dummyObject>
```

With `XmlValue` or `XmlValueAsCData`:

```xml
<dummyObject id='1'>foo</dummyObject>
```

With `ValueIsXml`, a String field carrying `<hello><a>data</a></hello>` round-trips as that subtree:

```xml
<DataWithInnerXml name="toto"><SUB_XML><hello><a>data</a></hello></SUB_XML></DataWithInnerXml>
```

## Namespaces

Inner objects are inserted as nodes, and `XmlNS` / `XmlUseParentNS` decide the prefixes — a real example:

```xml
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

```java
GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Envelope");
typeBuilder.addAnnotation(XmlNS.create("soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
header = typeBuilder.declareStringField("Header", XmlAsNode.UNIQUE_INSTANCE);
body   = typeBuilder.declareGlobField("Body", () -> Y2SoapBodyType.TYPE);
```

Reading is tolerant about how the document declares them: the same Glob comes back whether the peer uses a
prefix or a default `xmlns` on the node.

## The repository-oriented API

Next to the annotation-driven mapping, the `org.globsframework.xml` package works against a
`GlobRepository`, in the `<globs><type field='...'/></globs>` format core uses for its own test data:

```java
XmlGlobParser.parse(globModel, repository, reader, "globs");   // read into a repository
XmlGlobWriter.write(globs, repository, writer);                // write a collection back
GlobStream stream = XmlGlobStreamReader.parse(xml, globModel); // read as a stream, no repository
XmlChangeSetWriter.prettyWrite(changeSet, writer);             // serialize a ChangeSet
XmlChangeSetParser.parse(...)                                  // and read it back
```

## Building

```bash
mvn -o test
```

## License

Apache License 2.0 — see <https://www.apache.org/licenses/LICENSE-2.0.txt>.

## Links

- [Globs Framework](https://globsframework.org)
- [GitHub repository](https://github.com/globsframework/globs-xml)
- [saxstack](https://github.com/globsframework/saxstack) — the SAX layer underneath
