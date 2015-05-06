<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:mei="http://www.music-encoding.org/ns/mei"
	xmlns="http://www.tei-c.org/ns/1.0" xmlns:html="http://www/w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xi="http://www.w3.org/2001/XInclude" version="2.0" exclude-result-prefixes="xsl" xpath-default-namespace="http://www.tei-c.org/ns/1.0">
	
	<xsl:output indent="no"></xsl:output>
	
	<xsl:variable name="xmlIds" select="//@xml:id"/>
	
	<xsl:template match="@*|*|processing-instruction()|comment()" priority="-1">
		<xsl:copy>
			<xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
		</xsl:copy>
	</xsl:template>


	<xsl:variable name="src" select="'X'"/>

<!-- process sources with two app ancestors -->

	<xsl:template match="@wit[parent::lem][count(ancestor::app)=2]">
		<xsl:variable name="ancestor" select="./../ancestor::*[name()=('lem','rdg')][1]/@wit"/>
		<xsl:choose>
			<xsl:when test="contains(., $src) or ./../parent::app/rdg/@wit[contains(., $src)]">
				<xsl:attribute name="wit" xml:space="default" select="."/>
			</xsl:when>
			<xsl:when test="contains($ancestor, $src)">
				<xsl:attribute name="wit" xml:space="default" select="concat(.,' ', $src)"/>		
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="wit" xml:space="default" select="."/>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



	<!-- add missing wits to lem with one app ancestor -->
	<!--<xsl:template match="@wit[parent::lem][count(ancestor::app)=1]">
		<xsl:choose>
			<xsl:when test="contains(., $src) or ./../parent::app/rdg/@wit[contains(., $src)]">
				<xsl:attribute name="wit" xml:space="default" select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="wit" xml:space="default" select="concat(.,' ', $src)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>-->



</xsl:stylesheet>