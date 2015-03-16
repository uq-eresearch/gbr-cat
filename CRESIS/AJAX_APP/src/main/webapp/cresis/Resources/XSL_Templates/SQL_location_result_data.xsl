<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" encoding="UTF-8" />

<xsl:template match="results"> <!-- Process the result set -->
	<xsl:for-each-group select="result" group-by="concat(result,locname,lat,lon)">
	<xsl:sort select="concat(result,locname,lat,lon)" />
	<xsl:sort select="count(current-group())" />
<xsl:value-of select="locname" /><xsl:text>,</xsl:text>
<xsl:value-of select="lon" /><xsl:text>,</xsl:text>
<xsl:value-of select="lat" /><xsl:text>,</xsl:text>
<xsl:value-of select="count(current-group())" />
<xsl:text>
</xsl:text>
	</xsl:for-each-group>
</xsl:template>

</xsl:stylesheet>