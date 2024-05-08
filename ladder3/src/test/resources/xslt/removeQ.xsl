<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    
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
    
    <xsl:template match="@*|tei:*" mode="noq">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates mode="noq"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|tei:*" mode="noq2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates mode="noq2"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="@*|tei:*" mode="noq3">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates mode="noq3"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template
        match="text()[starts-with(normalize-space(), '&quot;') or ends-with(normalize-space(), '&quot;')]"
        mode="noq">
        <xsl:variable name="norm" select="normalize-space()"/>
        <xsl:variable name="shouldEliminateQ">
            <xsl:variable name="divText" select="ancestor::tei:div/normalize-space()"/>
            <xsl:choose>
                <xsl:when test="starts-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:when test="ends-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>no</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$shouldEliminateQ = 'yes'">
                <xsl:choose>
                    <xsl:when
                        test="starts-with(normalize-space(), '&quot;') and ends-with(normalize-space(), '&quot;') and string-length(normalize-space()) &gt; 1">
                        <xsl:analyze-string select="." regex="^(\s*)&quot;(.*)&quot;(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                                <xsl:value-of select="regex-group(3)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:when test="starts-with(normalize-space(), '&quot;')">
                        <xsl:analyze-string select="." regex="^(\s*)&quot;(.*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:analyze-string select="." regex="^(.*)&quot;(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template
        match="text()[starts-with(normalize-space(), '“') or ends-with(normalize-space(), '“')]"
        mode="noq2">
        <xsl:variable name="norm" select="normalize-space()"/>
        <xsl:variable name="shouldEliminateQ">
            <xsl:variable name="divText" select="ancestor::tei:div/normalize-space()"/>
            <xsl:choose>
                <xsl:when test="starts-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:when test="ends-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>no</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$shouldEliminateQ = 'yes'">
                <xsl:choose>
                    <xsl:when
                        test="starts-with(normalize-space(), '“') and ends-with(normalize-space(), '“') and string-length(normalize-space()) &gt; 1">
                        <xsl:analyze-string select="." regex="^(\s*)“(.*)“(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                                <xsl:value-of select="regex-group(3)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:when test="starts-with(normalize-space(), '“')">
                        <xsl:analyze-string select="." regex="^(\s*)“(.*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:analyze-string select="." regex="^(.*)“(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
    
    <xsl:template
        match="text()[starts-with(normalize-space(), '“') or ends-with(normalize-space(), '”')]"
        mode="noq3">
        <xsl:variable name="norm" select="normalize-space()"/>
        <xsl:variable name="shouldEliminateQ">
            <xsl:variable name="divText" select="ancestor::tei:div/normalize-space()"/>
            <xsl:choose>
                <xsl:when test="starts-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:when test="ends-with($divText, $norm)">
                    <xsl:text>yes</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>no</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$shouldEliminateQ = 'yes'">
                <xsl:choose>
                    <xsl:when
                        test="starts-with(normalize-space(), '“') and ends-with(normalize-space(), '”') and string-length(normalize-space()) &gt; 1">
                        <xsl:analyze-string select="." regex="^(\s*)“(.*)”(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                                <xsl:value-of select="regex-group(3)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:when test="starts-with(normalize-space(), '“')">
                        <xsl:analyze-string select="." regex="^(\s*)“(.*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:analyze-string select="." regex="^(.*)”(\s*)$" flags="s">
                            <xsl:matching-substring>
                                <xsl:value-of select="regex-group(1)"/>
                                <xsl:value-of select="regex-group(2)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
    
    <xsl:template match="tei:div">
        <xsl:choose>
            <xsl:when test="matches(normalize-space(), '^&quot;.*&quot;$')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates mode="noq"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="matches(normalize-space(), '^“.*“')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates mode="noq2"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="matches(normalize-space(), '^“.*”')">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates mode="noq3"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>