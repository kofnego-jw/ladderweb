<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    <xsl:param name="colorInfo">EEE8AA</xsl:param>
    
    <xsl:output method="xml"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@*">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
       
    <xsl:template match="tei:hi[@rend='background(yellow)']">
        <seg type="marked">
            <xsl:apply-templates/>
        </seg>
    </xsl:template>
    
    <xsl:template match="tei:hi[not(@rend='background(yellow)')]">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="tei:anchor[not(@type='START' and @subtype=$colorInfo)]"></xsl:template>
    
    <xsl:template match="tei:p/@rend"/>
    
    <xsl:template match="tei:anchor[@type='START' and @subtype=$colorInfo]">
        <xsl:variable name="n" select="@n"/>
        <xsl:variable name="end" select="//tei:anchor[@type='END' and @n=$n]"/>
        <xsl:variable name="parent" select="ancestor::tei:*[1]"/>
        <seg type="marked">
            <xsl:call-template name="copyBetween">
                <xsl:with-param name="start" select="."/>
                <xsl:with-param name="end" select="$end"/>
                <xsl:with-param name="parent" select="$parent"/>
            </xsl:call-template>
        </seg>
    </xsl:template>
    
    <xsl:template match="tei:*|text()">
        <xsl:variable name="inBetween">
            <xsl:choose>
                <xsl:when test="preceding::tei:anchor[1][@type='START' and @subtype=$colorInfo] and following::tei:anchor[1][@type='END']">yes</xsl:when>
                <xsl:otherwise>no</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="$inBetween='no'">
            <xsl:copy>
                <xsl:apply-templates select="@*"/>
                <xsl:apply-templates/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="copyBetween">
        <xsl:param name="start"/>
        <xsl:param name="end"/>
        <xsl:param name="parent" />
        <xsl:message>Start <xsl:value-of select="$start/generate-id()"/></xsl:message>
        <xsl:message>End <xsl:value-of select="$end/generate-id()"/></xsl:message>
        <xsl:message>Parent <xsl:value-of select="$parent"/></xsl:message>
        <xsl:for-each select="$parent/(tei:*|text())">
            <xsl:if test="preceding::tei:anchor[1][generate-id() = $start/generate-id()] and following::tei:anchor[1][generate-id() = $end/generate-id()]">
                <xsl:message>Found : <xsl:value-of select="."/></xsl:message>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:if>
        </xsl:for-each>
        
    </xsl:template>
    
</xsl:stylesheet>