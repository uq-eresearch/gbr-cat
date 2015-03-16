<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:res="http://www.w3.org/2005/sparql-results#">
<xsl:output method="text" indent="yes" encoding="UTF-8" />

<xsl:template match="*">
	<xsl:apply-templates/> <!-- process all my elements -->
</xsl:template>

<xsl:template match="res:sparql">&lt;?xml version="1.0" encoding="UTF-8"?&gt; <!-- Start at the top of the file -->
&lt;kml xmlns="http://earth.google.com/kml/2.1"&gt;
&lt;Document&gt;
&lt;Style id="earthicon"&gt;
      &lt;IconStyle&gt;
        &lt;Icon&gt;
          &lt;href&gt;root://icons/palette-3.png&lt;/href&gt;
          &lt;x&gt;64&lt;/x&gt;
          &lt;y&gt;32&lt;/y&gt;
          &lt;w&gt;32&lt;/w&gt;
          &lt;h&gt;32&lt;/h&gt;
        &lt;/Icon&gt;
      &lt;/IconStyle&gt;
 &lt;/Style&gt;
 &lt;Style id="2000ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ffffffff&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
&lt;Style id="1000ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ffff00ff&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
&lt;Style id="500ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ffffff00&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
&lt;Style id="200ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ff0000ff&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
&lt;Style id="100ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ff00ff00&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
&lt;Style id="under100ColourPlacemark"&gt;
	&lt;IconStyle&gt;
		&lt;color&gt;ffff0000&lt;/color&gt;
		&lt;Icon&gt;
			&lt;href&gt;http://maps.google.com/mapfiles/kml/paddle/wht-blank.png&lt;/href&gt;
		&lt;/Icon&gt;
	&lt;/IconStyle&gt;
&lt;/Style&gt;
	<xsl:apply-templates/> <!-- apply my other templates here -->
</xsl:template>

<xsl:template match="res:head"> <!-- Remove these corresponding elements -->
</xsl:template>
<xsl:template match="res:results"> <!-- Process the result set -->
	<xsl:for-each-group select="res:result" group-by="concat(res:result,res:binding[@name='locationName'],res:binding[@name='latitude'],res:binding[@name='longitude'])">
	<xsl:sort select="concat(res:result,res:binding[@name='locationName'],res:binding[@name='latitude'],res:binding[@name='longitude'])" />
	<xsl:sort select="count(current-group())" />
&lt;Placemark&gt;
    &lt;name&gt;
    	<xsl:value-of select="res:binding[@name='locationName']/res:literal" />
    &lt;/name&gt;
    &lt;description&gt;
    	<xsl:value-of select="res:binding[@name='longitude']/res:literal"/><xsl:text>,</xsl:text><xsl:value-of select="res:binding[@name='latitude']/res:literal"/><xsl:text>
    	</xsl:text>
    	<xsl:value-of select="count(current-group())" /><xsl:text> sightings.</xsl:text>
	&lt;/description&gt;
	&lt;styleUrl&gt;
	<xsl:choose>
	<xsl:when test="count(current-group()) &gt; 2000">
	<xsl:text>	#2000ColourPlacemark</xsl:text>
	</xsl:when>
	<xsl:when test="count(current-group()) &gt; 1000">
	<xsl:text>	#1000ColourPlacemark</xsl:text>
	</xsl:when>
	<xsl:when test="count(current-group()) &gt; 500">
	<xsl:text>	#500ColourPlacemark</xsl:text>
	</xsl:when>
	<xsl:when test="count(current-group()) &gt; 200">
	<xsl:text>	#200ColourPlacemark</xsl:text>
	</xsl:when>
	<xsl:when test="count(current-group()) &gt; 100">
	<xsl:text>	#100ColourPlacemark</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	<xsl:text>	#under100ColourPlacemark</xsl:text>
	</xsl:otherwise>
	</xsl:choose>
	&lt;/styleUrl&gt;
    &lt;Point&gt;
      	&lt;coordinates&gt;
      		<xsl:value-of select="res:binding[@name='longitude']/res:literal"/><xsl:text>,</xsl:text><xsl:value-of select="res:binding[@name='latitude']/res:literal"/>
    	&lt;/coordinates&gt;
	&lt;/Point&gt;
&lt;/Placemark&gt;
	<xsl:text>	</xsl:text>
	</xsl:for-each-group>
&lt;/Document&gt;
&lt;/kml&gt;
</xsl:template>

</xsl:stylesheet>