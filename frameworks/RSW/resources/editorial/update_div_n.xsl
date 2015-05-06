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

	<xsl:template match="div">
		<xsl:copy>
			<xsl:call-template name="createDivNAttribute"/>
			<xsl:apply-templates select="@*[not(name()='n')]|node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- add @n to a div element if it does have an ancestor div -->
	<xsl:template name="createDivNAttribute">
		<xsl:choose>
			<xsl:when test="@type='part'">
				<xsl:attribute name="n">
					<xsl:variable name="index" select="count(preceding-sibling::div)+1"/>
					<xsl:number value="$index" format="A"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="parent::div[@type=('part', 'critical-apparatus')]">
				<xsl:attribute name="n">
					<xsl:variable name="index" select="count(preceding-sibling::div)+1"/>
					<xsl:number value="$index" format="I"/>
				</xsl:attribute>
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>