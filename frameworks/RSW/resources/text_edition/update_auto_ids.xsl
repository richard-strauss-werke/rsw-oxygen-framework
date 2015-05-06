<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:mei="http://www.music-encoding.org/ns/mei"
	xmlns="http://www.tei-c.org/ns/1.0" xmlns:html="http://www/w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xi="http://www.w3.org/2001/XInclude" version="2.0" exclude-result-prefixes="xsl xi html mei" xpath-default-namespace="http://www.tei-c.org/ns/1.0">
	
	<xsl:variable name="autoIdPrefix" select="'_'"/>
	
	<xsl:preserve-space elements="l p sp stage speaker rdg lem"/>
	
	<!-- identity -->
	<xsl:template match="@*|node()|processing-instruction()|comment()" priority="-1">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()|processing-instruction()|comment()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="app[not(@xml:id) or starts-with(@xml:id, $autoIdPrefix)]">
		<app xml:id="{$autoIdPrefix}{format-number(count(preceding::app)+count(ancestor::app),'00000')}">
			<xsl:apply-templates select="@*[not(local-name()='id')]|*|text()|processing-instruction()|comment()"/>
		</app>
	</xsl:template>


</xsl:stylesheet>