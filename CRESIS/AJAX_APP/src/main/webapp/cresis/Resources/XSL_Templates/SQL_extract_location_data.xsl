<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="results"> <!-- Process the result set -->
<results>
<xsl:for-each select="result">
<xsl:if test="lat='--LATITUDE--'">
	<xsl:if test="lon='--LONGITUDE--'">
<record>
<xsl:copy-of select="*"/>
</record>
	</xsl:if>
</xsl:if>
</xsl:for-each>
</results>
</xsl:template>
</xsl:stylesheet>