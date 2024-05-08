<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    <xsl:param name="type"/>
    <xsl:param name="subtype"/>
    
    <xsl:output method="xml"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@*|tei:*">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:seg">
        <xsl:variable name="div" select="ancestor::tei:div[@n][1]"/>
        <xsl:variable name="seg" select="generate-id()"/>
        <xsl:variable name="textBefore">
            <xsl:for-each select="$div//text()">
                <xsl:if test="following::tei:seg/generate-id()=$seg">
                    <xsl:value-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <!-- <xsl:message>div: <xsl:value-of select="$div/@n"/> Seg: <xsl:value-of select="string(.)"/> Textbefore: <xsl:value-of select="$textBefore"/></xsl:message> -->
        <xsl:variable name="from" select="string-length($textBefore)"/>
        <xsl:variable name="to" select="$from + string-length(.)"/>
        <xsl:copy>
            <xsl:attribute name="from" select="$from"/>
            <xsl:attribute name="to" select="$to"/>
            <xsl:attribute name="type" select="$type"/>
            <xsl:attribute name="subtype" select="$subtype"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>