<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="text"/>
    
    <xsl:variable name="newLine" select="'&#x0a;'"></xsl:variable>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//tei:body/tei:div"/>
    </xsl:template>
    
    <xsl:template match="tei:div">
        <xsl:apply-templates/>
        <xsl:value-of select="$newLine"/>
    </xsl:template>
    
    <xsl:template match="text()">
        <xsl:choose>
            <xsl:when test="ancestor::tei:seg[@type and @subtype]">
                <xsl:variable name="tag">
                    <xsl:if test="string-length(ancestor::tei:seg/@type) &gt;= 3">
                        <xsl:value-of select="upper-case(substring(ancestor::tei:seg/@type, 1, 3))"/>
                    </xsl:if>
                    <xsl:text>-</xsl:text>
                    <xsl:if test="string-length(ancestor::tei:seg/@subtype) &gt;= 3">
                        <xsl:value-of select="upper-case(substring(ancestor::tei:seg/@subtype, 1, 3))"/>
                    </xsl:if>
                </xsl:variable>
                <xsl:call-template name="addTag">
                    <xsl:with-param name="text" select="replace(., '\s', ' ')"/>
                    <xsl:with-param name="tag" select="$tag"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="addTag">
                    <xsl:with-param name="text" select="replace(., '\s', ' ')"/>
                    <xsl:with-param name="tag">IGNORED</xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="addTag">
        <xsl:param name="text"/>
        <xsl:param name="tag"/>
        <xsl:analyze-string select="$text" regex="([^\d\w]+)">
            <xsl:matching-substring>
                <xsl:choose>
                    <xsl:when test="matches(., '\s*')">
                        <!-- Do nothing -->
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                        <xsl:text>_</xsl:text>
                        <xsl:value-of select="$tag"/>
                        <xsl:text> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:value-of select="."/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$tag"/>
                <xsl:text> </xsl:text>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
</xsl:stylesheet>