<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:res="http://www.w3.org/2005/sparql-results#">
<xsl:output method="text" indent="yes" encoding="UTF-8" />

<xsl:template match="*">
	<xsl:apply-templates/> <!-- process all my elements -->
</xsl:template>

<xsl:template match="res:sparql">
	<xsl:apply-templates/> <!-- apply my other templates here -->
</xsl:template>

<xsl:template match="res:head">
</xsl:template> <!-- Remove these elements -->

<xsl:template match="res:results"> <!-- Process the result set -->
<xsl:for-each select="res:result">
<xsl:if test="res:binding[@name='latitude']/res:literal='<<LATITUDE>>'">
	<xsl:if test="res:binding[@name='longitude']/res:literal='<<LONGITUDE>>'">
<<COLUMNS>>
	</xsl:if>
</xsl:if>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>