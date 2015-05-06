<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:mei="http://www.music-encoding.org/ns/mei"
	xmlns="http://www.tei-c.org/ns/1.0" xmlns:html="http://www/w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xi="http://www.w3.org/2001/XInclude" version="2.0" exclude-result-prefixes="xsl xi html mei" xpath-default-namespace="http://www.tei-c.org/ns/1.0">
	
	<xsl:variable name="autoIdPrefix" select="'_'"/>
	
	<xsl:output indent="no"/>
	
	<!-- identity -->
	<xsl:template match="@*|node()|processing-instruction()|comment()" priority="-1">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()|processing-instruction()|comment()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="note[@place='foot']">
		<xsl:copy>
			<xsl:attribute name="n">
				<xsl:value-of select="count(preceding::note[@place='foot'])+1"/>
			</xsl:attribute>
			<xsl:apply-templates select="@*[not(name()='n')]|node()|processing-instruction()|comment()"/>
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>