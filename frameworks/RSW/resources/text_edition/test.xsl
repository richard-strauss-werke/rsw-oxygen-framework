<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:mei="http://www.music-encoding.org/ns/mei"
	xmlns="http://www.tei-c.org/ns/1.0" xmlns:html="http://www/w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xi="http://www.w3.org/2001/XInclude" version="2.0" exclude-result-prefixes="xsl" xpath-default-namespace="http://www.tei-c.org/ns/1.0">
	
<!--	
	//app[(ancestor::lem[contains(@wit, 'X')] or ancestor::lem[contains(@wit, 'X')] or not(ancestor::lem) and not(ancestor::rdg)) and not(rdg[(contains(@wit, 'X'))] or lem[(contains(@wit, 'X'))])]/*/@wit[contains(., 'E')]
-->	
	
	<!-- TODO dann auch noch prüfen, ob es lesarten gibt, die denselben inhalt haben (das muss vereinheitlicht werden!) -->
	
	
	<!-- auch domains bei den links prüfen -->
	
	<xsl:output indent="yes"></xsl:output>
	
	<xsl:variable name="xmlIds" select="//@xml:id"/>
	
	<xsl:variable name="witnesses" select="//@xml:id[parent::witness]"/>
	
	<!-- identity -->
	<!--<xsl:template match="@*|*|processing-instruction()|comment()">
		<xsl:copy>
			<xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
		</xsl:copy>
	</xsl:template>-->
	
	<!-- pass-though -->
	<xsl:template match="*" priority="-1">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	
	<xsl:template match="/">
		<results>

		
			<test>
				<xsl:call-template name="transposTargetTest"/>
			</test>

			<!-- TODO test subsequent page numbers and coverage of page breaks -->
			
			<!-- test if each witness is called in each app (exception: apps with a parent app not calling that witness -->
			<test>
				<xsl:call-template name="witnessCoveryTest"/>
			</test>

			<!-- test for undefined wit or ed references -->
			<test>
				<xsl:call-template name="witnessDefinedTest"/>
			</test>
			
			<!-- check targets of @target -->
			<test>
				<xsl:call-template name="targetTest"/>
			</test>
			
			<!-- TODO check if one source is in lemma and reading or multiple readings! -->
			<test>
				<xsl:call-template name="siblingWitTest"/>
			</test>

			<!-- check: each source in a wit attribute must have a parent lem or rdg whose @src contains that source -->
			<test>
				<xsl:call-template name="ancestorWitTest"/>
			</test>

			<!-- check: each source in a lem/wit must occur in each descendant app's src/@wit or rdg/@wit exactly once -->
			<test>
				<xsl:call-template name="descendantWitTest"/>
			</test>

		</results>
	</xsl:template>

	
	<xsl:template name="transposTargetTest">
		<xsl:for-each select="//anchor[@type='transpos-target']">
			<xsl:variable name="id" select="concat('#',@xml:id)"/>
			<xsl:variable name="targetCount" select="count(//ptr[@type='place'][@target=$id])"/>
			<xsl:if test="$targetCount != 1">
				<log level="warn" reason="source-ne-1" target-count="{$targetCount}">
					<xsl:copy-of select="."/>
				</log>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>


	<!-- test if each witness is called in each app (exception: apps with a parent app not calling that witness -->
	<xsl:template name="witnessCoveryTest">
		<xsl:for-each select="$witnesses">
			<xsl:variable name="this" select="."/>
			<xsl:for-each select="//app[(ancestor::*[name()=('lem','rdg')][1][contains(@wit, $this)] or (not(ancestor::lem) and not(ancestor::rdg))) and not(rdg[(contains(@wit, $this))] or lem[(contains(@wit, $this))])]">
				<log level="warn" reason="wit-not-covered" wit="{$this}">
					<xsl:copy-of select="."/>
				</log>
			</xsl:for-each>
			
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="witnessDefinedTest">
		<xsl:for-each select="//@wit|//@ed">
			<xsl:for-each select="tokenize(., ' ')">
				<xsl:variable name="this" select="substring-after(., '#')"/>
				<xsl:if test="not($witnesses[.=$this])">
					<log level="error" reason="wit-undefined" wit="{$this}"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="targetTest">
		<xsl:for-each select="//@target">
			<xsl:for-each select="tokenize(., ' ')">
				<xsl:variable name="this" select="substring(.,2)"/>
				<xsl:if test="not($xmlIds[.=$this])">
					<log level="error" reason="target-undefined" target="{$this}"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="siblingWitTest">
		<xsl:for-each select="//@wit">
			<xsl:variable name="att" select="."></xsl:variable>
			<xsl:for-each select="tokenize(., ' ')">
				<xsl:variable name="value" select="."/>
				<xsl:if test="count($att/../parent::app/*/@wit[contains(., $value)]) > 1">
					<log level="error" reason="multiple-witnesses" wit="{$value}">
						<xsl:copy-of select="$att/../parent::app/*/@wit[contains(., $value)]/.."></xsl:copy-of>
					</log>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="ancestorWitTest">
		<xsl:for-each select="//@wit">
			<xsl:variable name="att" select="."></xsl:variable>
			<xsl:for-each select="tokenize(., ' ')">
				<xsl:variable name="value" select="."/>
				<xsl:for-each select="$att/ancestor::rdg|$att/ancestor::lem">
					<xsl:if test="not(contains(@wit, $value))">
						<log level="error" reason="missing-wit-in-ancestor">
							<xsl:copy-of select="$att/../.."/>
						</log>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="descendantWitTest">
		<xsl:for-each select="//@wit">
			<xsl:variable name="att" select="."></xsl:variable>
			<xsl:for-each select="tokenize(., ' ')">
				<xsl:variable name="value" select="."/>
				<xsl:for-each select="$att/..//app">
					<xsl:if test="(count(lem[contains(@wit, $value)]) + count(rdg[contains(@wit, $value)]) ne 1) and contains(ancestor::*[name()=('lem','rdg')][1]/@wit,$value)">
						<log level="error" reason="descendant-app-wit-count-ne-one" wit="{$value}">
							<xsl:copy-of select="."/>
						</log>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	


</xsl:stylesheet>