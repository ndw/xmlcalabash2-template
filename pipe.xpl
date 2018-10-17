<p:declare-step version="3.0"
                xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:demo="http://demo.xmlcalabash.com/steps"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-inline-prefixes="demo xs"
                name="main">
  <p:output port="result" sequence='true' serialization="map { 'omit-xml-declaration': true() }"/>
  <p:option name="opt" select="1"/>
  <p:option name="version" select="'scala'"/>

  <p:declare-step type="demo:scala-template">
    <p:input port="source" sequence="true"/>
    <p:output port="result"/>
    <p:option name="number" as="xs:integer"/>
  </p:declare-step>

  <p:declare-step type="demo:java-template">
    <p:input port="source" sequence="true"/>
    <p:output port="result"/>
    <p:option name="number" as="xs:integer"/>
  </p:declare-step>

  <p:identity>
    <p:with-input port="source">
      <p:inline><first/></p:inline>
      <p:inline><second/></p:inline>
      <p:inline><third/></p:inline>
    </p:with-input>
  </p:identity>

  <p:choose>
    <p:when test="$version = 'scala'">
      <demo:scala-template number="{$opt}"/>
    </p:when>
    <p:when test="$version = 'java'">
      <demo:java-template number="{$opt}"/>
    </p:when>
    <p:otherwise>
      <p:identity>
        <p:with-input port="source">
          <p:inline><unexpected>Version was {$version}?</unexpected></p:inline>
        </p:with-input>
      </p:identity>
    </p:otherwise>
  </p:choose>

</p:declare-step>
