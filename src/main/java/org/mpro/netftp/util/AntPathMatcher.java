package org.mpro.netftp.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PathMatcher implementation for Ant-style path patterns. Examples are provided below.
 *
 * <p>
 * Part of this mapping code has been kindly borrowed from <a href="http://ant.apache.org">Apache Ant</a>.
 *
 * <p>
 * The mapping matches URLs using the following rules:<br>
 * <ul>
 * <li>? matches one character</li>
 * <li>* matches zero or more characters</li>
 * <li>** matches zero or more 'directories' in a path</li>
 * </ul>
 *
 * <p>
 * Some examples:<br>
 * <ul>
 * <li><code>com/t?st.jsp</code> - matches <code>com/test.jsp</code> but also <code>com/tast.jsp</code> or
 * <code>com/txst.jsp</code></li>
 * <li><code>com/*.jsp</code> - matches all <code>.jsp</code> files in the <code>com</code> directory</li>
 * <li><code>com/&#42;&#42;/test.jsp</code> - matches all <code>test.jsp</code> files underneath the <code>com</code>
 * path</li>
 * <li><code>org/springframework/&#42;&#42;/*.jsp</code> - matches all <code>.jsp</code> files underneath the
 * <code>org/springframework</code> path</li>
 * <li><code>org/&#42;&#42;/servlet/bla.jsp</code> - matches <code>org/springframework/servlet/bla.jsp</code> but also
 * <code>org/springframework/testing/servlet/bla.jsp</code> and <code>org/servlet/bla.jsp</code></li>
 * </ul>
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Arjen Poutsma
 * @since 16.07.2003
 */
public class AntPathMatcher {

    public static final String DEFAULT_PATH_SEPARATOR = "/";

    private String pathSeparator = DEFAULT_PATH_SEPARATOR;

    public void setPathSeparator(String pathSeparator) {
        this.pathSeparator = (pathSeparator != null ? pathSeparator : DEFAULT_PATH_SEPARATOR);
    }

    public boolean isPattern(String path) {
        return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
    }

    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true, null);
    }

    public boolean matchStart(String pattern, String path) {
        return doMatch(pattern, path, false, null);
    }

    protected boolean doMatch(String pattern, String path, boolean fullMatch,
        Map<String, String> uriTemplateVariables) {
        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        }

        String[] pattDirs = StringHelper.tokenizeToStringArray(pattern, this.pathSeparator);
        String[] pathDirs = StringHelper.tokenizeToStringArray(path, this.pathSeparator);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxStart];
            if ("**".equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator)
                    : !path.endsWith(this.pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(this.pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxEnd];
            if (patDir.equals("**")) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }

    private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {
        AntPathStringMatcher matcher = new AntPathStringMatcher(pattern, str, uriTemplateVariables);
        return matcher.matchStrings();
    }

    public String extractPathWithinPattern(String pattern, String path) {
        String[] patternParts = StringHelper.tokenizeToStringArray(pattern, this.pathSeparator);
        String[] pathParts = StringHelper.tokenizeToStringArray(path, this.pathSeparator);

        StringBuilder builder = new StringBuilder();

        // Add any path parts that have a wildcarded pattern part.
        int puts = 0;
        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            if ((patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) && pathParts.length >= i + 1) {
                if (puts > 0 || (i == 0 && !pattern.startsWith(this.pathSeparator))) {
                    builder.append(this.pathSeparator);
                }
                builder.append(pathParts[i]);
                puts++;
            }
        }

        // Append any trailing path parts.
        for (int i = patternParts.length; i < pathParts.length; i++) {
            if (puts > 0 || i > 0) {
                builder.append(this.pathSeparator);
            }
            builder.append(pathParts[i]);
        }

        return builder.toString();
    }

    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        Map<String, String> variables = new LinkedHashMap<String, String>();
        boolean result = doMatch(pattern, path, true, variables);
        if (!result)
            throw new IllegalStateException("Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
        return variables;
    }

    public String combine(String pattern1, String pattern2) {
        if (StringHelper.isBlank(pattern1) && StringHelper.isBlank(pattern2)) {
            return "";
        } else if (StringHelper.isBlank(pattern1)) {
            return pattern2;
        } else if (StringHelper.isBlank(pattern2)) {
            return pattern1;
        } else if (match(pattern1, pattern2)) {
            return pattern2;
        } else if (pattern1.endsWith("/*")) {
            if (pattern2.startsWith("/")) {
                // /hotels/* + /booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2.substring(1);
            } else {
                // /hotels/* + booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2;
            }
        } else if (pattern1.endsWith("/**")) {
            if (pattern2.startsWith("/")) {
                // /hotels/** + /booking -> /hotels/**/booking
                return pattern1 + pattern2;
            } else {
                // /hotels/** + booking -> /hotels/**/booking
                return pattern1 + "/" + pattern2;
            }
        } else {
            int dotPos1 = pattern1.indexOf('.');
            if (dotPos1 == -1) {
                // simply concatenate the two patterns
                if (pattern1.endsWith("/") || pattern2.startsWith("/")) {
                    return pattern1 + pattern2;
                } else {
                    return pattern1 + "/" + pattern2;
                }
            }
            String fileName1 = pattern1.substring(0, dotPos1);
            String extension1 = pattern1.substring(dotPos1);
            String fileName2;
            String extension2;
            int dotPos2 = pattern2.indexOf('.');
            if (dotPos2 != -1) {
                fileName2 = pattern2.substring(0, dotPos2);
                extension2 = pattern2.substring(dotPos2);
            } else {
                fileName2 = pattern2;
                extension2 = "";
            }
            String fileName = fileName1.endsWith("*") ? fileName2 : fileName1;
            String extension = extension1.startsWith("*") ? extension2 : extension1;

            return fileName + extension;
        }
    }

    public Comparator<String> getPatternComparator(String path) {
        return new AntPatternComparator(path);
    }

    private static class AntPatternComparator implements Comparator<String> {

        private final String path;

        private AntPatternComparator(String path) {
            this.path = path;
        }

        public int compare(String pattern1, String pattern2) {
            if (pattern1 == null && pattern2 == null) {
                return 0;
            } else if (pattern1 == null) {
                return 1;
            } else if (pattern2 == null) {
                return -1;
            }
            boolean pattern1EqualsPath = pattern1.equals(path);
            boolean pattern2EqualsPath = pattern2.equals(path);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            } else if (pattern1EqualsPath) {
                return -1;
            } else if (pattern2EqualsPath) {
                return 1;
            }
            int wildCardCount1 = StringHelper.countOccurrencesOf(pattern1, "*");
            int wildCardCount2 = StringHelper.countOccurrencesOf(pattern2, "*");
            if (wildCardCount1 < wildCardCount2) {
                return -1;
            } else if (wildCardCount2 < wildCardCount1) {
                return 1;
            }
            int bracketCount1 = StringHelper.countOccurrencesOf(pattern1, "{");
            int bracketCount2 = StringHelper.countOccurrencesOf(pattern2, "{");
            if (bracketCount1 < bracketCount2) {
                return -1;
            } else if (bracketCount2 < bracketCount1) {
                return 1;
            }
            return 0;
        }
    }

}
