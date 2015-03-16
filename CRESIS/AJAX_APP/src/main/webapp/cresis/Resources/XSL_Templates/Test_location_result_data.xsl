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
<xsl:for-each select="res:result">
<observation>
<xsl:if test="res:binding[@name='locname']"><locname><xsl:value-of select="res:binding[@name='lat_end']/res:literal" /></locname></xsl:if><xsl:text>,</xsl:text>
<xsl:if test="res:binding[@name='lat']"><lat><xsl:value-of select="res:binding[@name='lat']/res:literal" /></lat></xsl:if><xsl:text>,</xsl:text>
<xsl:if test="res:binding[@name='lon']"><lon><xsl:value-of select="res:binding[@name='lon']/res:literal" /></lon></xsl:if>
</observation>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>