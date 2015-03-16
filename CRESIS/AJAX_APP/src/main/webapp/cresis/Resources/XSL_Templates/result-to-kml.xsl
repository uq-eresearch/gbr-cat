<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:res="http://www.w3.org/2005/sparql-results#"><xsl:output method="text" indent="yes" encoding="UTF-8" />
	<xsl:template match="res:sparql">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;kml xmlns="http://earth.google.com/kml/2.1"&gt; <!-- Mark up the KML file -->
&lt;Document&gt;<!-- Process all the results fromt he SPARQL result set file -->
	<xsl:choose>
	  <xsl:when test="res:results">
	    <xsl:apply-templates select="//res:result"/> <!-- Apply the template which will switch all the variables in the result set file -->
	  </xsl:when>
	</xsl:choose>
&lt;/Document&gt;
&lt;/kml&gt;
  </xsl:template>
  <xsl:template match="res:result"> <!-- Where all the hard work is done check for the variable name and create the valid kml based on the variable -->
  &lt;Placemark&gt;
    &lt;name&gt;<!-- TODO: NEED TO GET THE NAME xsl:value-of select="res:binding[@name='event']/res:uri"/ -->
    Query Result
    &lt;/name&gt;
    &lt;description&gt;
      &lt;![CDATA[
      &lt;strong&gt;Sighting of: <xsl:value-of select="res:binding[@name='commonName']/res:literal"/>, Genus: &lt;i&gt;<xsl:value-of select="res:binding[@name='genusName']/res:literal"/>&lt;/i&gt;&lt;/strong&gt;
      &lt;p&gt;Location: <xsl:value-of select="res:binding[@name='locationName']/res:literal"/>&lt;/p&gt;
      &lt;p&gt;Date: <xsl:value-of select="res:binding[@name='date']/res:literal"/>&lt;/p&gt;
      &lt;table&gt;
        &lt;tr bgcolor="#E0E0E0"&gt;
         &lt;td&gt;&lt;strong&gt;Property&lt;/strong&gt;&lt;/td&gt;
         &lt;td&gt;&lt;strong&gt;Value&lt;/strong&gt;&lt;/td&gt;
         &lt;td&gt;&lt;strong&gt;Unit&lt;/strong&gt;&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr bgcolor="#C2C2C2"&gt;
         &lt;td&gt;&lt;strong&gt;<xsl:value-of select="res:binding[@name='property']/res:literal"/>&lt;/strong&gt;&lt;/td&gt;
         &lt;td&gt;&lt;strong&gt;<xsl:value-of select="res:binding[@name='value']/res:literal"/>&lt;/strong&gt;&lt;/td&gt;
         &lt;td&gt;&lt;strong&gt;<xsl:value-of select="res:binding[@name='unit']/res:literal"/>&lt;/strong&gt;&lt;/td&gt;
        &lt;/tr&gt;
      &lt;/table&gt;
      ]]&gt;
    &lt;/description&gt;
    &lt;TimeStamp&gt;
	  &lt;when&gt;
	    <xsl:value-of select="res:binding[@name='date']/res:literal"/>
      &lt;/when&gt;
    &lt;/TimeStamp&gt;
    &lt;Point&gt;
      &lt;coordinates&gt;
        <xsl:value-of select="res:binding[@name='longitude']/res:literal"/><xsl:text>,</xsl:text><xsl:value-of select="res:binding[@name='latitude']/res:literal"/>
      &lt;/coordinates&gt;
    &lt;/Point&gt;
  &lt;/Placemark&gt;
  </xsl:template>
</xsl:stylesheet>