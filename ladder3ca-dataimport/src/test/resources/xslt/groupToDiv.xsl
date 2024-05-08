<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    <xsl:param name="pattern">^L(1|2)_\d+\s*$</xsl:param>
    
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
    
    <xsl:template match="tei:p">
        <xsl:variable name="beforeWithPattern">
            <xsl:choose>
                <xsl:when test="preceding::tei:p[matches(string(.), $pattern)]">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:otherwise>no</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="nextWithPattern" select="following::tei:p[matches(string(.), $pattern)][1]"/>
        <xsl:variable name="parent" select="parent::tei:*[1]"/>
        <xsl:choose>
            <xsl:when test="matches(string(.), $pattern)">
                <div>
                    <xsl:attribute name="n" select="normalize-space()"></xsl:attribute>
                    <xsl:call-template name="copyBetween">
                        <xsl:with-param name="start" select="."/>
                        <xsl:with-param name="end" select="$nextWithPattern"/>
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </div>
            </xsl:when>
            <xsl:when test="$beforeWithPattern='no'">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <!-- Delete this -->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="copyBetween">
        <xsl:param name="start"/>
        <xsl:param name="end"/>
        <xsl:param name="parent"/>
        <xsl:variable name="myEnd">
            <xsl:choose>
                <xsl:when test="$end">
                    <xsl:copy-of select="$end/generate-id()"/>
                </xsl:when>
                <xsl:when test="$parent/tei:p[following-sibling::tei:p[1][normalize-space()='Annotations']]">
                    <xsl:copy-of select="$parent/tei:p[following-sibling::tei:p[1][normalize-space()='Annotations']]/generate-id()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$parent/tei:p[not(following-sibling::tei:p)]/generate-id()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:for-each select="$parent/(tei:*|text())">
            <xsl:if test="preceding::tei:p[generate-id() = $start/generate-id()] and following::tei:p[generate-id() = $myEnd] and not(normalize-space(.)='')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates/>
                    <xsl:text>
</xsl:text>
                </xsl:copy>
            </xsl:if>
        </xsl:for-each>
        
    </xsl:template>
    
    
</xsl:stylesheet>