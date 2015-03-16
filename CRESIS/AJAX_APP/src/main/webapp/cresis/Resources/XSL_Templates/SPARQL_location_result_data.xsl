<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:res="http://www.w3.org/2005/sparql-results#">
<xsl:output method="text" indent="yes" encoding="UTF-8" />

<xsl:template match="*">
	<xsl:apply-templates/> <!-- process all my elements -->
</xsl:template>

<xsl:template match="res:sparql">
	<xsl:apply-templates/> <!-- apply my other templates here -->
</xsl:template>

<xsl:template match="res:head"> <!-- Remove these corresponding elements -->
</xsl:template>

<xsl:template match="res:results"> <!-- Process the result set -->
	<xsl:for-each-group select="res:result" group-by="concat(res:result,res:binding[@name='locname'],res:binding[@name='lat'],res:binding[@name='lon'])">
	<xsl:sort select="concat(res:result,res:binding[@name='locname'],res:binding[@name='lat'],res:binding[@name='lon'])" />
	<xsl:sort select="count(current-group())" />
<xsl:value-of select="res:binding[@name='locname']/res:literal" /><xsl:text>,</xsl:text>
<xsl:value-of select="res:binding[@name='lon']/res:literal"/><xsl:text>,</xsl:text>
<xsl:value-of select="res:binding[@name='lat']/res:literal"/><xsl:text>,</xsl:text>
<xsl:value-of select="count(current-group())" />
<xsl:text>
</xsl:text>
	</xsl:for-each-group>
</xsl:template>

</xsl:stylesheet>