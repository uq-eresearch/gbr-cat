<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8" />

<xsl:template match="results"> <!-- Process the result set -->
<results> 
<xsl:for-each select="result">
<xsl:if test="lat='--LATITUDE--'">
	<xsl:if test="lon='--LONGITUDE--'">
<record>
<!-- xsl:if test="lat"><lat><xsl:value-of select="lat" /></lat></xsl:if>
<xsl:if test="lon"><lon><xsl:value-of select="lon" /></lon></xsl:if>
<xsl:if test="lat_end"><lat_end><xsl:value-of select="lat_end" /></lat_end></xsl:if>
<xsl:if test="lon_end"><lon_end><xsl:value-of select="lon_end" /></lon_end></xsl:if>
<xsl:if test="locname"><locname><xsl:value-of select="locname" /></locname></xsl:if>
<xsl:if test="target"><target><xsl:value-of select="target" /></target></xsl:if>
<xsl:if test="characteristic"><characteristic><xsl:value-of select="characteristic" /></characteristic></xsl:if>
<xsl:if test="startdate"><startdate><xsl:value-of select="startdate" /></startdate></xsl:if>
<xsl:if test="enddate"><enddate><xsl:value-of select="enddate" /></enddate></xsl:if>
<xsl:if test="actor"><actor><xsl:value-of select="actor" /></actor></xsl:if>
<xsl:if test="value"><value><xsl:value-of select="value" /></value></xsl:if>
<xsl:if test="unit"><unit><xsl:value-of select="unit" /></unit></xsl:if>
<xsl:if test="tool"><tool><xsl:value-of select="tool" /></tool></xsl:if>
<xsl:if test="species"><species><xsl:value-of select="species" /></species></xsl:if>
<xsl:if test="genus"><genus><xsl:value-of select="genus" /></genus></xsl:if>
<xsl:if test="morphology"><morphology><xsl:value-of select="morphology" /></morphology></xsl:if>
<xsl:if test="group_name"><group_name><xsl:value-of select="group_name" /></group_name></xsl:if>
<xsl:if test="eco_process"><eco_process><xsl:value-of select="eco_process" /></eco_process></xsl:if -->
</record>
	</xsl:if>
</xsl:if>
</xsl:for-each>
</results>
</xsl:template>
</xsl:stylesheet>