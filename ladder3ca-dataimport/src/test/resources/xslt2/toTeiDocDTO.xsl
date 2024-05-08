<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    <xsl:param name="poolid"/>
    
    <xsl:variable name="createdOn">
        <xsl:variable name="createdOnP" select="//tei:p[starts-with(., 'Created on:')]"/>
        <xsl:analyze-string select="$createdOnP/normalize-space()" regex="^Created on: (.*)$">
            <xsl:matching-substring>
                <xsl:value-of select="regex-group(1)"/>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:variable>
    
    <xsl:variable name="modifiedOn">
        <xsl:variable name="createdOnP" select="//tei:p[starts-with(., 'Modified on:')]"/>
        <xsl:analyze-string select="$createdOnP/normalize-space()" regex="^Modified on: (.*)$">
            <xsl:matching-substring>
                <xsl:value-of select="regex-group(1)"/>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:variable>
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">        
        <TeiDocDTO>
            <poolId>
                <xsl:value-of select="$poolid"/>
            </poolId>
            <createdOn>
                <xsl:value-of select="$createdOn"/>
            </createdOn>
            <modifiedOn>
                <xsl:value-of select="$modifiedOn"/>
            </modifiedOn>
            <xsl:for-each select="//tei:div[@n]">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </TeiDocDTO>        
    </xsl:template>
    
    <xsl:template match="tei:div">
        <data>
            <id>
                <xsl:value-of select="@n"/>
            </id>
            <text>
                <xsl:value-of select="string(.)"/>
            </text>
            <xsl:for-each select=".//tei:seg">
                <markedPlaces>
                    <markedText>
                        <xsl:value-of select="string()"/>
                    </markedText>
                    <type>
                        <xsl:value-of select="@type"/>
                    </type>
                    <subtype>
                        <xsl:value-of select="@subtype"/>
                    </subtype>
                    <from>
                        <xsl:value-of select="@from"/>
                    </from>
                    <to>
                        <xsl:value-of select="@to"/>
                    </to>
                </markedPlaces>
            </xsl:for-each>
        </data>        
    </xsl:template>
    
    
</xsl:stylesheet>