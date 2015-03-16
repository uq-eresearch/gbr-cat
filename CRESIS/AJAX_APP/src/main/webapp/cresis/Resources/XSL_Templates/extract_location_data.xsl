<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:res="http://www.w3.org/2005/sparql-results#">
<xsl:output method="xml" indent="yes" encoding="UTF-8" />

<xsl:template match="*">
	<xsl:apply-templates/> <!-- process all my elements -->
</xsl:template>

<xsl:template match="res:sparql">
	<results> <xsl:apply-templates/> </results> <!-- apply my other templates here -->
</xsl:template>

<xsl:template match="res:head">
</xsl:template> <!-- Remove these elements -->

<xsl:template match="res:results"> <!-- Process the result set -->
<xsl:for-each select="res:result">
<xsl:if test="res:binding[@name='lat']/res:literal='--LATITUDE--'">
	<xsl:if test="res:binding[@name='lon']/res:literal='--LONGITUDE--'">
<record>
<xsl:if test="res:binding[@name='lat']"><lat><xsl:value-of select="res:binding[@name='lat']/res:literal" /></lat></xsl:if>
<xsl:if test="res:binding[@name='lon']"><lon><xsl:value-of select="res:binding[@name='lon']/res:literal" /></lon></xsl:if>
<xsl:if test="res:binding[@name='lat_end']"><lat_end><xsl:value-of select="res:binding[@name='lat_end']/res:literal" /></lat_end></xsl:if>
<xsl:if test="res:binding[@name='lon_end']"><lon_end><xsl:value-of select="res:binding[@name='lon_end']/res:literal" /></lon_end></xsl:if>
<xsl:if test="res:binding[@name='locname']"><locname><xsl:value-of select="res:binding[@name='locname']/res:literal" /></locname></xsl:if>
<xsl:if test="res:binding[@name='target']"><target><xsl:value-of select="res:binding[@name='target']/res:literal" /></target></xsl:if>
<xsl:if test="res:binding[@name='characteristic']"><characteristic><xsl:value-of select="res:binding[@name='characteristic']/res:literal" /></characteristic></xsl:if>
<xsl:if test="res:binding[@name='begindate']"><begindate><xsl:value-of select="res:binding[@name='begindate']/res:literal" /></begindate></xsl:if>
<xsl:if test="res:binding[@name='enddate']"><enddate><xsl:value-of select="res:binding[@name='enddate']/res:literal" /></enddate></xsl:if>
<xsl:if test="res:binding[@name='actorname']"><actorname><xsl:value-of select="res:binding[@name='actorname']/res:literal" /></actorname></xsl:if>
<xsl:if test="res:binding[@name='value']"><value><xsl:value-of select="res:binding[@name='value']/res:literal" /></value></xsl:if>
<xsl:if test="res:binding[@name='unitsymbol']"><unitsymbol><xsl:value-of select="res:binding[@name='unitsymbol']/res:literal" /></unitsymbol></xsl:if>
<xsl:if test="res:binding[@name='species']"><species><xsl:value-of select="res:binding[@name='species']/res:literal" /></species></xsl:if>
<xsl:if test="res:binding[@name='genus']"><genus><xsl:value-of select="res:binding[@name='genus']/res:literal" /></genus></xsl:if>
<xsl:if test="res:binding[@name='morphology']"><morphology><xsl:value-of select="res:binding[@name='morphology']/res:literal" /></morphology></xsl:if>
<xsl:if test="res:binding[@name='group_name']"><group_name><xsl:value-of select="res:binding[@name='group_name']/res:literal" /></group_name></xsl:if>
<xsl:if test="res:binding[@name='eco_process']"><eco_process><xsl:value-of select="res:binding[@name='morphology']/res:literal" /></eco_process></xsl:if>
</record>
	</xsl:if>
</xsl:if>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>