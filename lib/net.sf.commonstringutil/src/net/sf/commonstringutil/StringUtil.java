/* Copyright (C) 2011 Muhammad Edwin & Natalino Nugeraha
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions, and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions, and the disclaimer that follows
these conditions in the documentation and/or other materials
provided with the distribution.

3. The name "FastStringUtility" must not be used to endorse or promote products
derived from this software without prior written permission.  For
written permission.

4. Products derived from this software may not be called "FastStringUtility", nor
may "FastStringUtility" appear in their name, without prior written permission
from the FastStringUtility Project Management.

In addition, we request (but do not require) that you include in the
end-user documentation provided with the redistribution and/or in the
software itself an acknowledgement equivalent to the following:
"This product includes software developed by the
FastStringUtil Project (http://www.baculsoft.com/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

This software consists of voluntary contributions made by many
individuals on behalf of the FastStringUtil Project and was originally
created by Natalino Nugeraha Putrama <nugie@baculsoft.com> and
Muhammad Edwin <edwinkun@gmail.com>.  For more information
on the FastStringUtil Project, please see <http://www.baculsoft.com/>.
 */
package net.sf.commonstringutil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Natalino Nugeraha
 * @author Muhammad Edwin
 * @version 1.0.0
 * 
 */
public final class StringUtil {

    private static final FBS CH_DON_ENCODE;
    private static final int CASE_DIFF = ('a' - 'A');
    private static final String DEF_ENCODE_NAME = "UTF-8";
    private static Charset DEF_CHARSET;
    private static final char CH_PLUS = '+';
    private static final char CH_SPACE = ' ';
    private static final char CH_PERCENT = '%';
    private static final char CH_EQUAL = '=';
    private static final int BLENGTH = 128;
    static final int LLENGTH = 64;
    private static final int TF_BITGRP = 24;
    private static final int BIT8 = 8;
    private static final int BIT16 = 16;
    private static final int BYTE4 = 4;
    private static final int SIGN = -128;
    private static final int INT_MAX = -Integer.MAX_VALUE;
    private static final int INT_MIN1 = -Integer.MAX_VALUE / 10;
    private static final int INT_MIN2 = Integer.MIN_VALUE / 10;
    private static final short SHORT_MAX = -Short.MAX_VALUE;
    private static final short SHORT_MIN1 = -Short.MAX_VALUE / 10;
    private static final short SHORT_MIN2 = Short.MIN_VALUE / 10;
    private static final long LONG_MAX = -Long.MAX_VALUE;
    private static final long LONG_MIN1 = -Long.MAX_VALUE / 10;
    private static final long LONG_MIN2 = Long.MIN_VALUE / 10;
    private static final byte[] BASE64ALP = new byte[BLENGTH];
    private static final char[] LBASE64ALP = new char[LLENGTH];
    public static final String STR_EMP = "";
    private static final String LINESEP = System.getProperty("line.separator",
            "\n");
    private static final char CH_NULL = '\0';
    private static final char CH_ZERO = '0';
    private static final char CH_LCASE_A = 'a';
    private static final short CH_LCASE_A_M10 = 87;
    private static final short CH_UCASE_A_M10 = 55;
    private static final char LIM_ST_UCASE = 'A' - 1;
    private static final char LI_N_UCASE = 'Z' + 1;
    private static final char LIM_ST_LCASE = 'a' - 1;
    private static final char LIM_N_LCASE = 'z' + 1;
    private static final char[] BYTEHEX;
    private static final char[] UCASE = {'\000', '\001', '\002', '\003',
        '\004', '\005', '\006', '\007', '\010', '\011', '\012', '\013',
        '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023',
        '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033',
        '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043',
        '\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053',
        '\054', '\055', '\056', '\057', '\060', '\061', '\062', '\063',
        '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073',
        '\074', '\075', '\076', '\077', '\100', 'A', 'B', 'C', 'D', 'E',
        'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '\133', '\134', '\135',
        '\136', '\137', '\140', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z', '\173', '\174', '\175', '\176', '\177'};
    private static final char[] LCASE = {'\000', '\001', '\002', '\003',
        '\004', '\005', '\006', '\007', '\010', '\011', '\012', '\013',
        '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023',
        '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033',
        '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043',
        '\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053',
        '\054', '\055', '\056', '\057', '\060', '\061', '\062', '\063',
        '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073',
        '\074', '\075', '\076', '\077', '\100', '\141', '\142', '\143',
        '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
        '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
        '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\133',
        '\134', '\135', '\136', '\137', '\140', '\141', '\142', '\143',
        '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
        '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
        '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\173',
        '\174', '\175', '\176', '\177'};
    private static final char[] DIGITS = {CH_ZERO, '1', '2', '3', '4', '5',
        '6', '7', '8', '9', CH_LCASE_A, 'b', 'c', 'd', 'e', 'f'};
    private static final IllegalArgumentException ILLEGAL_CHARACTER = new IllegalArgumentException(
            "must [0-9],[a-z],[A-Z]");
    private static final char[][] ZERO_POOL = new char[65][];
    private static final char[][] SPACE_POOL = new char[65][];

    static {
        int i, j;
        StringBuilder builder = new StringBuilder();
        ZERO_POOL[0] = SPACE_POOL[0] = STR_EMP.toCharArray();
        for (i = 1; i < 65; i++) {
            SPACE_POOL[i] = new char[i];
            ZERO_POOL[i] = new char[i];
            for (j = 0; j < i; j++) {
                SPACE_POOL[i][j] = CH_SPACE;
                ZERO_POOL[i][j] = CH_ZERO;
            }
        }
        builder.setLength(0);
        for (i = 1; i < 65; i++) {
            SPACE_POOL[i] = new char[i];
            for (j = 0; j < i; j++) {
                SPACE_POOL[i][j] = CH_SPACE;
            }
        }
        CH_DON_ENCODE = new FBS(256);
        for (i = 'a'; i <= 'z'; i++) {
            CH_DON_ENCODE.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            CH_DON_ENCODE.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            CH_DON_ENCODE.set(i);
        }
        CH_DON_ENCODE.set(CH_SPACE);
        CH_DON_ENCODE.set('-');
        CH_DON_ENCODE.set('_');
        CH_DON_ENCODE.set('.');
        CH_DON_ENCODE.set('*');

        DEF_CHARSET = Charset.forName(DEF_ENCODE_NAME);
        for (i = 0; i < BLENGTH; ++i) {
            BASE64ALP[i] = -1;
        }
        for (i = 'Z'; i >= 'A'; i--) {
            BASE64ALP[i] = (byte) (i - 'A');
        }
        for (i = 'z'; i >= 'a'; i--) {
            BASE64ALP[i] = (byte) (i - CH_LCASE_A + 26);
        }

        for (i = '9'; i >= '0'; i--) {
            BASE64ALP[i] = (byte) (i - CH_ZERO + 52);
        }

        BASE64ALP[CH_PLUS] = 62;
        BASE64ALP['/'] = 63;

        for (i = 0; i <= 25; i++) {
            LBASE64ALP[i] = (char) ('A' + i);
        }

        for (i = 26, j = 0; i <= 51; i++, j++) {
            LBASE64ALP[i] = (char) (CH_LCASE_A + j);
        }

        for (i = 52, j = 0; i <= 61; i++, j++) {
            LBASE64ALP[i] = (char) (CH_ZERO + j);
        }
        LBASE64ALP[62] = CH_PLUS;
        LBASE64ALP[63] = '/';
        char[] hexDigits = "0123456789abcdef".toCharArray();
        BYTEHEX = new char[16 * 16 * 2];
        for (i = 0, j = 0; i < 256; ++i) {
            BYTEHEX[j++] = hexDigits[i >>> 4];
            BYTEHEX[j++] = hexDigits[i & 15];
        }
    }

    private StringUtil() {
    }

    /**
     * 
     * @param o
     * @return
     */
    public static final String valueOf(final Object o) {
        return (o instanceof String) ? (String) o : (o == null ? "null" : o.toString());
    }

    /***
     * 
     * @param s
     * @return
     */
    public static final Boolean valueOfBoolean(final String s) {
        return Boolean.valueOf(parseBoolean(s));
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final boolean parseBoolean(final String s) {
        if ("true".equalsIgnoreCase(
                s)) {
            return true;
        }
        if (s == null) {
            return false;
        }
        switch (s.length()) {
            case 2: {
                char ch0 = s.charAt(0);
                char ch1 = s.charAt(1);
                return (ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N');
            }
            case 3: {
                char ch = s.charAt(0);
                return (ch == 'y') ? (s.charAt(1) == 'e' || s.charAt(1) == 'E')
                        && (s.charAt(2) == 's' || s.charAt(2) == 'S') : (s.charAt(1) == 'E' || s.charAt(1) == 'e')
                        && (s.charAt(2) == 'S' || s.charAt(2) == 's');
            }
            case 4: {
                char ch = s.charAt(0);
                return ch == 't' ? (s.charAt(1) == 'r' || s.charAt(1) == 'R')
                        && (s.charAt(2) == 'u' || s.charAt(2) == 'U')
                        && (s.charAt(3) == 'e' || s.charAt(3) == 'E') : (s.charAt(1) == 'R' || s.charAt(1) == 'r')
                        && (s.charAt(2) == 'U' || s.charAt(2) == 'u')
                        && (s.charAt(3) == 'E' || s.charAt(3) == 'e');
            }
        }
        return false;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final Integer valueOfInteger(final String s) {
        return Integer.valueOf(parseInt(s));
    }

    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final Integer valueOfInteger(final String s,int radix) {
        return Integer.valueOf(parseInt(s));
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final int parseInt(final String s) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");
        }
        final int neg;
        int num = 0;

        final char ch = s.charAt(0);
        int d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = ch - CH_ZERO;
            if (d < 0 || d > 9) {
                throw new NumberFormatException("Not number :  " + s);
            }
            num = -d;
            neg = -1;

        }
        final int limitInt;
        final int multmax;
        if (neg == -1) {
            limitInt = INT_MAX;
            multmax = INT_MIN1;
        } else {
            limitInt = Integer.MIN_VALUE;
            multmax = INT_MIN2;
        }
        //
        int i = 1;
        while (i < len) {
            d = s.charAt(i++) - CH_ZERO;
            if (d < 0 || d > 9) {
                throw new NumberFormatException("Not number :  " + s);
            }
            if (num < multmax) {
                throw new NumberFormatException("Underflow number :  " + s);
            }
            num *= 10;
            if (num < (limitInt + d)) {
                throw new NumberFormatException("Overflow number :  " + s);
            }
            num -= d;
        }
        return neg * num;
    }

    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final int parseInt(final String s, final int radix) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");
        }
        final int neg;
        int num = 0;

        final char ch = s.charAt(0);
        int d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = ch - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d + 9);
                neg = -1;

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d);
                neg = -1;

            }

        }
        final int limitInt;
        final int multmax;
        if (neg == -1) {
            limitInt = INT_MAX;
            multmax = INT_MIN1;
        } else {
            limitInt = Integer.MIN_VALUE;
            multmax = INT_MIN2;
        }
        int i = 1;
        while (i < len) {
            d = s.charAt(i++) - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d + 9);

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d);
            }
        }
        return neg * num;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final Short valueOfShort(final String s) {
        return Short.valueOf(parseShort(s));
    }

    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final Short valueOfShort(final String s,int radix) {
        return Short.valueOf(parseShort(s));
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final short parseShort(final String s) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");

        }
        final short neg;
        int num = 0;
        final char ch = s.charAt(0);
        short d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = (short) (ch - CH_ZERO);
            if (d < 0 || d > 9) {
                throw new NumberFormatException("Not number :  " + s);
            }
            num = -d;
            neg = -1;

        }
        final short limitInt;
        final short multmax;

        if (neg == -1) {
            limitInt = SHORT_MAX;
            multmax = SHORT_MIN1;
        } else {
            limitInt = Short.MIN_VALUE;
            multmax = SHORT_MIN2;
        }
        int i = 1;
        while (i < len) {
            d = (short) (s.charAt(i++) - CH_ZERO);
            if (d < 0 || d > 9) {
                throw new NumberFormatException("Not number :  " + s);
            }
            if (num < multmax) {
                throw new NumberFormatException("Underflow number :  " + s);
            }
            num *= 10;
            if (num < (limitInt + d)) {
                throw new NumberFormatException("Overflow number :  " + s);
            }
            num -= d;
        }
        return (short) (neg * num);
    }

    
    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final short parseShort(final String s, final int radix) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");
        }
        final short neg;
        int num = 0;

        final char ch = s.charAt(0);
        int d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = ch - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d + 9);
                neg = -1;

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d);
                neg = -1;

            }

        }
        final short limitInt;
        final short multmax;
        if (neg == -1) {
            limitInt = SHORT_MAX;
            multmax = SHORT_MIN1;
        } else {
            limitInt = Short.MIN_VALUE;
            multmax = SHORT_MIN2;
        }
        int i = 1;
        while (i < len) {
            d = s.charAt(i++) - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d + 9);

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d);
            }
        }
        return (short)(neg * num);
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public static final Long valueOfLong(final String s) {
        return Long.valueOf(parseLong(s));
    }

    
    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final Long valueOfLong(final String s,final int radix) {
        return Long.valueOf(parseLong(s));
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final long parseLong(final String s) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");

        }
        final short neg;
        long num = 0;
        final char ch = s.charAt(0);
        long d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = ch - CH_ZERO;
            if (d < 0 || d > 9) {
                throw new NumberFormatException("Not number :  " + s);
            }
            num = -d;
            neg = -1;

        }
        final long limitInt;
        final long multmax;

        if (neg == -1) {
            limitInt = LONG_MAX;
            multmax = LONG_MIN1;
        } else {
            limitInt = Long.MIN_VALUE;
            multmax = LONG_MIN2;
        }
        int i = 1;
        while (i < len) {
            d = s.charAt(i++) - CH_ZERO;
            if (d < 0 || d > 9) {
                if (i != len && d != 'l' && d != 'L') {
                    throw new NumberFormatException("Not number :  " + s);
                }
            } else if (num < multmax) {
                throw new NumberFormatException("Underflow number :  " + s);
            }
            num *= 10;
            if (num < (limitInt + d)) {
                throw new NumberFormatException("Overflow number :  " + s);
            }
            num -= d;
        }
        return neg * num;
    }

    /**
     * 
     * @param s
     * @param radix
     * @return
     */
    public static final long parseLong(final String s, final int radix) {
        if (s == null) {
            throw new NumberFormatException("Null string ");
        }
        final int len = s.length();
        if (len == 0) {
            throw new NumberFormatException("Empty string ");
        }
        final short neg;
        long num = 0;

        final char ch = s.charAt(0);
        int d;
        if (ch == '-') {
            if (len == 1) {
                throw new NumberFormatException("Missing digits:  " + s);
            }
            neg = 1;
        } else {
            d = ch - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d + 9);
                neg = -1;

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + s);
                }
                num = -(d);
                neg = -1;

            }

        }
        final long limitInt;
        final long multmax;
        if (neg == -1) {
            limitInt = LONG_MAX;
            multmax = LONG_MIN1;
        } else {
            limitInt = Long.MIN_VALUE;
            multmax = LONG_MIN2;
        }
        int i = 1;
        while (i < len) {
            d = s.charAt(i++) - CH_ZERO;
            if (d > 9) {
                d = d - 16;
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d + 9);

            } else {
                if (d < 0 || d > 9) {
                    throw new NumberFormatException("Not number :  " + d);
                }
                if (num < multmax) {
                    throw new NumberFormatException("Underflow number :  " + s);
                }
                num *= radix;
                if (num < (limitInt + d)) {
                    throw new NumberFormatException("Overflow number :  " + s);
                }
                num -= (d);
            }
        }
        return neg * num;
    }
    
    /**
     * 
     * @return newLine
     */
    public static final String getNewLine() {
        return LINESEP;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final boolean isEmpty(final String s) {
        return (s == null || s.length() == 0);
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final boolean isEmptyWithTrim(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 
     * @param s
     * @return s.toUpperCase();
     * @see java.lang.String#toUpperCase()
     */
    public static final String toUpperCase(final String s) {
        char[] c = null;
        int i = s.length();
        while (i-- > 0) {
            char c1 = s.charAt(i);
            if (c1 > LIM_ST_LCASE && c1 < LIM_N_LCASE) {
                char c2 = UCASE[c1];
                if (c1 != c2) {
                    c = s.toCharArray();
                    c[i] = c2;
                    break;
                }
            }
        }
        char c1;
        while (i-- > 0) {
            c1 = c[i];
            if (c1 > LIM_ST_LCASE && c1 < LIM_N_LCASE) {
                c[i] = UCASE[c1];
            }
        }
        return c == null ? s : new String(c, 0, i);
    }

    /**
     * 
     * @param s
     * @return String.toLowerCase()
     * @see java.lang.String#toLowerCase()
     */
    public static String toLowerCase(final String s) {
        char[] c = null;
        int i = s.length();
        while (i-- > 0) {
            char c1 = s.charAt(i);
            if (c1 > LIM_ST_UCASE && c1 < LI_N_UCASE) {
                char c2 = LCASE[c1];
                if (c1 != c2) {
                    c = s.toCharArray();
                    c[i] = c2;
                    break;
                }
            }
        }
        char c1;
        while (i-- > 0) {
            c1 = c[i];
            if (c1 > LIM_ST_UCASE && c1 < LI_N_UCASE) {
                c[i] = LCASE[c1];
            }
        }
        return c == null ? s : new String(c, 0, i);
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final String uncapitalize(final String s) {
        int i = s.length();
        if (i > 0) {
            char[] c = null;
            char c1 = s.charAt(0);
            if (c1 > LIM_ST_UCASE && c1 < LI_N_UCASE) {
                char c2 = LCASE[c1];
                if (c1 != c2) {
                    c = s.toCharArray();
                    c[0] = c2;
                    return new String(c, 0, i);
                }
            }
        }
        return s;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static final String capitalize(final String s) {
        int i = s.length();
        if (i > 0) {
            char c1 = s.charAt(i);
            if (c1 > LIM_ST_LCASE && c1 < LIM_N_LCASE) {
                char c2 = UCASE[c1];
                if (c1 != c2) {
                    char[] c = s.toCharArray();
                    c[i] = c2;
                    return new String(c, 0, i);
                }
            }
        }
        return s;
    }

    /**
     * 
     * @param s the input string to be checked
     * @param with
     * @see java.lang.String#startsWith(String)
     * @return
     */
    public static final boolean startsWithIgnoreCase(final String s,
            final String with) {
        if (with == null) {
            return true;
        }
        if (s == null || s.length() < with.length()) {
            return false;
        }
        final int wlen = with.length();
        for (int i = 0; i < wlen; i++) {
            char c1 = s.charAt(i);
            char c2 = with.charAt(i);
            if (c1 != c2) {
                if (c1 <= 127) {
                    c1 = LCASE[c1];
                }
                if (c2 <= 127) {
                    c2 = LCASE[c2];
                }
                if (c1 != c2) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param s
     * @param with
     * @see java.lang.String#endsWith(String)
     * @return
     */
    public static final boolean endsWithIgnoreCase(final String s,
            final String with) {
        if (with == null) {
            return true;
        }
        if (s == null) {
            return false;
        }

        int sl = s.length();
        final int wl = with.length();

        if (sl < wl) {
            return false;
        }

        for (int i = wl; i-- > 0;) {
            char c1 = s.charAt(--sl);
            char c2 = with.charAt(i);
            if (c1 != c2) {
                if (c1 <= 127) {
                    c1 = LCASE[c1];
                }
                if (c2 <= 127) {
                    c2 = LCASE[c2];
                }
                if (c1 != c2) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static final String replace(final String s, final String pat,
            final String rep) {
        if (pat.length() == 0) {
            return s;
        }
        final int replen = rep.length(), patlen = pat.length();
        if (patlen == 1) {
            if (replen == 1) {
                return s.replace(pat.charAt(0), rep.charAt(0));
            } else {
                return replace(s, pat.charAt(0), rep);
            }
        } else {
            if (replen == 1) {
                return replace(s, rep, rep.charAt(0));
            } else {
                int nPos = 0, indexCharArray = 0;;
                final int sLen = s.length(), arrLen = (replen > patlen) ? sLen * replen : sLen;
                final char[] charArray = new char[arrLen];
                if (replen == 0) {
                    int nIndex;
                    while (true) {
                        nIndex = s.indexOf(pat, nPos);
                        if (nIndex < 0) {
                            s.getChars(nPos, sLen, charArray, indexCharArray);
                            indexCharArray += nPos;
                            break;
                        } else {
                            s.getChars(nPos, nIndex, charArray, indexCharArray);
                            indexCharArray += (nIndex - nPos);
                            nPos = nIndex + patlen;
                        }
                    }
                    if (indexCharArray > arrLen) {
                        final char[] charsArrayRet = new char[indexCharArray];
                        System.arraycopy(charArray, 0, charsArrayRet, 0, arrLen);
                        return new String(charsArrayRet);
                    } else if (indexCharArray < arrLen) {
                        final char[] charsArrayRet = new char[indexCharArray];
                        System.arraycopy(charArray, 0, charsArrayRet, 0, indexCharArray);
                        return new String(charsArrayRet);
                    }
                    return new String(charArray);
                } else {
                    int nIndex;
                    while (true) {
                        nIndex = s.indexOf(pat, nPos);
                        if (nIndex < 0) {
                            s.getChars(nPos, sLen, charArray, indexCharArray);
                            indexCharArray += (sLen - (nPos));
                            break;
                        } else {
                            s.getChars(nPos, nIndex, charArray, indexCharArray);
                            indexCharArray += (nIndex - nPos);
                            rep.getChars(0, replen, charArray, indexCharArray);
                            indexCharArray += replen;
                            nPos = nIndex + patlen;
                        }
                    }
                    if (indexCharArray > arrLen) {
                        final char[] charsArrayRet = new char[indexCharArray];
                        System.arraycopy(charArray, 0, charsArrayRet, 0, arrLen);
                        return new String(charsArrayRet);
                    } else if (indexCharArray < arrLen) {
                        final char[] charsArrayRet = new char[indexCharArray];
                        System.arraycopy(charArray, 0, charsArrayRet, 0, indexCharArray);
                        return new String(charsArrayRet);
                    }
                    return new String(charArray);
                }
            }
        }
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replace(final String s, final String pat,
            final char rep) {
        int nPos = 0;
        final char[] charsArray = new char[s.length()];
        int indexCharArr = 0;
        while (true) {
            int nIndex = s.indexOf(pat, nPos);
            if (nIndex < 0) {
                s.getChars(nPos, s.length(), charsArray, indexCharArr);
                indexCharArr += nPos;
                break;
            } else {
                s.getChars(nPos, nIndex, charsArray, indexCharArr);
                indexCharArr += (nIndex - nPos);
                charsArray[indexCharArr] = rep;
                indexCharArr++;
                nPos = nIndex + pat.length();
            }
        }
        if (indexCharArr < s.length()) {
            final char[] charsArrayRet = new char[indexCharArr];
            System.arraycopy(charsArray, 0, charsArrayRet, 0, indexCharArr);
            return new String(charsArrayRet);
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replace(final String s, final char pat,
            final String rep) {
        int nPos = 0, indexCharArr = 0;
        final int sLen = s.length(), replen = rep.length();
        if (replen == 0) {
            final char[] charsArray = new char[sLen];
            while (true) {
                int nIndex = s.indexOf(pat, nPos);
                if (nIndex < 0) {
                    s.getChars(nPos, sLen, charsArray, indexCharArr);
                    indexCharArr += nPos;
                    break;
                } else {
                    s.getChars(nPos, nIndex, charsArray, indexCharArr);
                    indexCharArr += (nIndex - nPos);
                    nPos = nIndex + 1;
                }
            }
            if (indexCharArr < s.length()) {
                final char[] charsArrayRet = new char[indexCharArr];
                System.arraycopy(charsArray, 0, charsArrayRet, 0, indexCharArr);
                return new String(charsArrayRet);
            }
            return new String(charsArray);
        } else {
            if (replen == 1) {
                return s.replace(pat, rep.charAt(0));
            }
            final char[] charsArray = new char[sLen * replen];
            int nIndex;
            while (true) {
                nIndex = s.indexOf(pat, nPos);
                if (nIndex < 0) {
                    if (nPos < sLen) {
                        s.getChars(nPos, sLen, charsArray, indexCharArr);
                        indexCharArr += (sLen - nPos);
                    }
                    break;
                } else {
                    s.getChars(nPos, nIndex, charsArray, indexCharArr);
                    indexCharArr += (nIndex - nPos);
                    rep.getChars(0, replen, charsArray, indexCharArr);
                    indexCharArr += replen;
                    nPos = nIndex + 1;
                }
            }
            if (indexCharArr != charsArray.length) {
                final char[] charsArrayRet = new char[indexCharArr];
                System.arraycopy(charsArray, 0, charsArrayRet, 0, charsArrayRet.length);
                return new String(charsArrayRet);
            }
            return new String(charsArray);

        }

    }

    /**
     * 
     * @param s
     * @param c
     * @return
     */
    public static String removeLast(final String s, final char c) {
        int nIndex = s.lastIndexOf(c);
        return nIndex < 0 ? s : s.substring(0, nIndex - 1);
    }

    /**
     * 
     * @param s
     * @param pat
     * @return
     */
    public static String removeLast(final String s, final String pat) {
        if (pat.length() == 0) {
            return s;
        }
        final int nIndex = s.lastIndexOf(pat);
        return nIndex < 0 ? s : s.substring(0, nIndex);
    }

    /**
     * 
     * @param s
     * @param c
     * @return
     */
    public static String removeFirst(final String s, final char c) {
        final int nIndex = s.indexOf(c, 0);
        return nIndex < 0 ? s : s.substring(nIndex + 1);
    }

    /**
     * 
     * @param s
     * @param pat
     * @return
     */
    public static String removeFirst(final String s, final String pat) {
        if (pat.length() == 0) {
            return s;
        }
        final int nIndex = s.indexOf(pat, 0);
        return nIndex < 0 ? s : s.substring(nIndex + pat.length());
    }

    /**
     * 
     * @param s
     * @param pat
     * @return
     */
    public static final String remove(final String s, final char pat) {
        if (s == null || s.length() == 0) {
            return s;
        }
        int nIndex = s.indexOf(pat, 0);
        if (nIndex < 0) {
            return s;
        } else {
            final int sLen = s.length();
            final char[] charArray = new char[sLen];
            int indexCharArray = nIndex, nPos = nIndex + 1;
            s.getChars(0, nIndex, charArray, 0);
            while (true) {
                nIndex = s.indexOf(pat, nPos);
                if (nIndex < 0) {
                    s.getChars(nPos, sLen, charArray, indexCharArray);
                    indexCharArray += (sLen - nPos);
                    break;
                } else {
                    s.getChars(nPos, nIndex, charArray, indexCharArray);
                    indexCharArray += (nIndex - nPos);
                    nPos = nIndex + 1;
                }
            }
            if (indexCharArray == 0) {
                return STR_EMP;
            } else if (indexCharArray < sLen) {
                final char[] charArrRet = new char[indexCharArray];
                System.arraycopy(charArray, 0, charArrRet, 0, indexCharArray);
                return new String(charArrRet);
            }
            return new String(charArray);
        }
    }

    /**
     * 
     * @param s
     * @param pat
     * @return
     */
    public static final String remove(final String s, final String pat) {
        if (s == null || s.length() == 0 || pat == null || pat.length() == 0) {
            return s;
        } else if (pat.length() == 1) {
            return remove(s, pat.charAt(0));
        }
        int nIndex = s.indexOf(pat, 0);
        if (nIndex < 0) {
            return s;
        } else {
            final int sLen = s.length(), patLen = pat.length();
            final char[] charArray = new char[sLen];
            int indexCharArray = nIndex, nPos = nIndex + pat.length();
            s.getChars(0, nIndex, charArray, 0);
            while (true) {
                nIndex = s.indexOf(pat, nPos);
                if (nIndex < 0) {
                    s.getChars(nPos, sLen, charArray, indexCharArray);
                    indexCharArray += (sLen - nPos);
                    break;
                } else {
                    s.getChars(nPos, nIndex, charArray, indexCharArray);
                    indexCharArray += (nIndex - nPos);
                    nPos = nIndex + patLen;

                }
            }
            if (indexCharArray == 0) {
                return STR_EMP;
            } else if (indexCharArray < sLen) {
                final char[] charArrRet = new char[indexCharArray];
                System.arraycopy(charArray, 0, charArrRet, 0, indexCharArray);
                return new String(charArrRet);
            }
            return new String(charArray);
        }
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static final String replaceFirst(final String s, final String pat,
            final String rep) {
        if (s == null || s.length() == 0 || pat == null || pat.length() == 0 || rep == null) {
            return s;
        }
        int pos = s.indexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray;
        if (pat.length() > rep.length()) {
            charsArray = new char[s.length() + (pat.length() - rep.length())];
        } else {
            charsArray = new char[s.length() - (pat.length() - rep.length())];
        }
        s.getChars(0, pos, charsArray, 0);
        rep.getChars(0, rep.length(), charsArray, pos);
        s.getChars(pos + pat.length(), s.length(), charsArray, pos + rep.length());
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceFirst(final String s, final String pat,
            final char rep) {
        if (s == null || s.length() == 0 || pat == null || pat.length() == 0) {
            return s;
        }
        int pos = s.indexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray = new char[s.length() + 1 - pat.length()];
        s.getChars(0, pos, charsArray, 0);
        charsArray[pos] = rep;
        s.getChars(pos + pat.length(), s.length(), charsArray, pos + 1);
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceFirst(final String s, final char pat,
            final char rep) {
        if (s == null) {
            return s;
        }
        int pos = s.indexOf(pat);
        if (pos != 0) {
            return s;
        }
        final char[] chars = s.toCharArray();
        chars[pos] = rep;
        return new String(chars);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceFirst(final String s, final char pat,
            final String rep) {
        if (s == null || s.length() == 0 || rep == null) {
            return s;
        }
        int pos = s.indexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray = new char[s.length() + (rep.length() - 1)];
        s.getChars(0, pos, charsArray, 0);
        rep.getChars(0, rep.length(), charsArray, pos);
        s.getChars(pos + 1, s.length(), charsArray, pos + rep.length());
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static final String replaceLast(final String s, final String pat,
            final String rep) {
        if (s == null || s.length() == 0 || pat == null || pat.length() == 0 || rep == null) {
            return s;
        }
        int pos = s.lastIndexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray;
        if (pat.length() > rep.length()) {
            charsArray = new char[s.length() + (pat.length() - rep.length())];
        } else {
            charsArray = new char[s.length() - (pat.length() - rep.length())];
        }
        s.getChars(0, pos, charsArray, 0);
        rep.getChars(0, rep.length(), charsArray, pos);
        s.getChars(pos + pat.length(), s.length(), charsArray, pos + rep.length());
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceLast(final String s, final String pat,
            final char rep) {
        if (s == null || s.length() == 0 || pat == null || pat.length() == 0) {
            return s;
        }
        int pos = s.lastIndexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray = new char[s.length() + 1 - pat.length()];
        s.getChars(0, pos, charsArray, 0);
        charsArray[pos] = rep;
        s.getChars(pos + pat.length(), s.length(), charsArray, pos + 1);
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceLast(final String s, final char pat,
            final char rep) {
        if (s == null) {
            return s;
        }
        int pos = s.lastIndexOf(pat);
        if (pos != 0) {
            return s;
        }
        final char[] chars = s.toCharArray();
        chars[pos] = rep;
        return new String(chars);
    }

    /**
     * 
     * @param s
     * @param pat
     * @param rep
     * @return
     */
    public static String replaceLast(final String s, final char pat,
            final String rep) {
        if (s == null || s.length() == 0 || rep == null) {
            return s;
        }
        int pos = s.lastIndexOf(pat);
        if (pos == -1) {
            return s;
        }
        final char[] charsArray = new char[s.length() + (rep.length() - 1)];
        s.getChars(0, pos, charsArray, 0);
        rep.getChars(0, rep.length(), charsArray, pos);
        s.getChars(pos + 1, s.length(), charsArray, pos + rep.length());
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param separator
     * @return
     */
    public static final String[] split(final String s, final char separator) {                
        int lastIndex = 0,sz=0,currentIndex = 0,newSz=20;
        String[] strArray=new String[newSz];        
        while ((currentIndex = s.indexOf(separator, lastIndex)) != -1) {
        	strArray[sz++]=s.substring(lastIndex, currentIndex);               		
            lastIndex = currentIndex + 1;
            if(sz==newSz){
            	String[] tmp=strArray;
            	newSz=sz+(s.length()-lastIndex);
            	strArray=new String[newSz];
            	System.arraycopy(tmp, 0, strArray, 0, sz);
            }
            
        }
        if (sz==0) {
            return new String[]{s};
        }
        if(sz==newSz){
        	final String[] tmp=strArray;
        	strArray=new String[sz+1];
        	System.arraycopy(tmp, 0, strArray, 0, sz);        	
        }
        strArray[sz++]=s.substring(lastIndex);        
        return strArray;        

    }    
    /**
     * 
     * @param s
     * @param separator
     * @return
     */
    public static final String[] split(final String s, final String separator) {
        if (separator.length() == 1) {
            return split(s, separator.charAt(0));
        } else if (s.length() < separator.length()) {
            return new String[]{s};
        }
        int lastIndex = 0,sz=0,currentIndex = 0,newSz=20;
        String[] strArray=new String[newSz];        
        while ((currentIndex = s.indexOf(separator, lastIndex)) != -1) {
        	strArray[sz++]=s.substring(lastIndex, currentIndex);               		
            lastIndex = currentIndex + separator.length();
            if(sz==newSz){
            	String[] tmp=strArray;
            	newSz=sz+(s.length()-lastIndex);
            	strArray=new String[newSz];
            	System.arraycopy(tmp, 0, strArray, 0, sz);
            }
            
        }
        if (sz==0) {
            return new String[]{s};
        }
        if(sz==newSz){
        	final String[] tmp=strArray;
        	strArray=new String[sz+1];
        	System.arraycopy(tmp, 0, strArray, 0, sz);        	
        }
        strArray[sz++]=s.substring(lastIndex);        
        return strArray;        
    }

    /**
     * 
     * @param s
     * @param separator
     * @return
     */    
    public static final List<String> splitAsList(final String s, final char separator) {
    	int lastIndex = 0,sz=0,currentIndex = 0,newSz=20;
        String[] strArray=new String[newSz];        
        while ((currentIndex = s.indexOf(separator, lastIndex)) != -1) {
        	strArray[sz++]=s.substring(lastIndex, currentIndex);               		
            lastIndex = currentIndex + 1;
            if(sz==newSz){
            	String[] tmp=strArray;
            	newSz=sz+(s.length()-lastIndex);
            	strArray=new String[newSz];
            	System.arraycopy(tmp, 0, strArray, 0, sz);
            }
            
        }
        if (sz==0) {
            return Arrays.asList(new String[]{s});
        }
        if(sz==newSz){
        	final String[] tmp=strArray;
        	strArray=new String[sz+1];
        	System.arraycopy(tmp, 0, strArray, 0, sz);        	
        }
        strArray[sz++]=s.substring(lastIndex);        
        return Arrays.asList(strArray);        
    }
    /**
     * 
     * @param s
     * @param del
     * @return
     */
    public static List splitAsList(final String s, final String separator) {
        if (separator == null) {
            return null;
        } else if (separator.length() == 1) {
            return splitAsList(s, separator.charAt(0));
        } else if (s.length() < separator.length()) {
            return Arrays.asList(new String[0]);
        }
    	int lastIndex = 0,sz=0,currentIndex = 0,newSz=20;
        String[] strArray=new String[newSz];        
        while ((currentIndex = s.indexOf(separator, lastIndex)) != -1) {
        	strArray[sz++]=s.substring(lastIndex, currentIndex);               		
            lastIndex = currentIndex + separator.length();
            if(sz==newSz){
            	String[] tmp=strArray;
            	newSz=sz+(s.length()-lastIndex);
            	strArray=new String[newSz];
            	System.arraycopy(tmp, 0, strArray, 0, sz);
            }
            
        }
        if (sz==0) {
            return Arrays.asList(new String[]{s});
        }
        if(sz==newSz){
        	final String[] tmp=strArray;
        	strArray=new String[sz+1];
        	System.arraycopy(tmp, 0, strArray, 0, sz);        	
        }
        strArray[sz++]=s.substring(lastIndex);        
        return Arrays.asList(strArray);        

    }

    /**
     * 
     * @param s
     * @param del
     * @return
     */
    public static final String[] splitIncludeDelimiter(final String s, final char del) {
        final String delStr=new String(new char[]{del});
        final String[] strArray=new String[s.length()];
        int lastIndex = 0,sz=0,currentIndex = 0;
        while ((currentIndex = s.indexOf(del, lastIndex)) != -1) {
            strArray[sz++]=s.substring(lastIndex, currentIndex);
            strArray[sz++]=delStr;
            lastIndex = currentIndex + 1;
        }
        if (sz==0) {
            return new String[]{s};
        }
        strArray[sz++]=s.substring(lastIndex);        
        if(sz!=strArray.length){
        	String[] retStrArray=new String[sz];
        	System.arraycopy(strArray, 0, retStrArray,0, sz);
        	return retStrArray;
        }
        return strArray;
    }
    
    /**
     * 
     * @param s
     * @param del
     * @return
     */
    public static String[] splitIncludeDelimiter(final String s,
            final String del) {
        final String[] strArray=new String[s.length()];
        int lastIndex = 0,sz=0,currentIndex = 0;
        while ((currentIndex = s.indexOf(del, lastIndex)) != -1) {
            strArray[sz++]=s.substring(lastIndex, currentIndex);
            strArray[sz++]=del;
            lastIndex = currentIndex + 1;
        }
        if (sz==0) {
            return new String[]{s};
        }
        strArray[sz++]=s.substring(lastIndex);        
        if(sz!=strArray.length){
        	String[] retStrArray=new String[sz];
        	System.arraycopy(strArray, 0, retStrArray,0, sz);
        	return retStrArray;
        }
        return strArray;    
       }

    /**
     * 
     * @param s
     * @return
     */
    public static final String urlEncode(final String s) {
        String str = null;
        try {
            str = urlEncode(s, DEF_ENCODE_NAME);
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    /**
     * 
     * @param s
     * @param enc
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String urlEncode(final String s, final String enc)
            throws UnsupportedEncodingException {
        if (enc == null) {
            throw new NullPointerException("charsetName");
        }
        Charset charset;
        if (DEF_ENCODE_NAME.equalsIgnoreCase(enc)) {
            charset = DEF_CHARSET;
        } else {
            try {
                charset = Charset.forName(enc);
            } catch (IllegalCharsetNameException e) {
                throw new UnsupportedEncodingException(enc);
            } catch (UnsupportedCharsetException e) {
                throw new UnsupportedEncodingException(enc);
            }
        }
        final char[] chars = s.toCharArray();
        final int len = s.length();
        boolean needToChange = false;
        final char[] chArrayWr = new char[len], charArray = new char[len * 3];
        int indexChArray = 0, intTmp, cnt = 0;
        for (int i = 0; i < len;) {
            intTmp = (int) chars[i];
            if (CH_DON_ENCODE.get(intTmp)) {
                if (intTmp == CH_SPACE) {
                    intTmp = CH_PLUS;
                    needToChange = true;
                    charArray[indexChArray++] = CH_PLUS;
                } else {
                    charArray[indexChArray++] = chars[i];
                }
                i++;
            } else {
                do {
                    chArrayWr[cnt++] = (char) intTmp;
                    if (intTmp >= 0xD800 && intTmp <= 0xDBFF) {
                        if ((i + 1) < len) {
                            int d = (int) chars[i + 1];
                            if (d >= 0xDC00 && d <= 0xDFFF) {
                                chArrayWr[cnt++] = (char) d;
                                i++;
                            }
                        }
                    }
                    i++;
                } while (i < len && !CH_DON_ENCODE.get((intTmp = (int) chars[i])));
                String str = new String(Arrays.copyOf(chArrayWr, cnt));
                byte[] ba = str.getBytes(charset);
                byte b;
                char ch;
                for (int j = 0, blen = ba.length; j < blen; j++) {
                    b = ba[j];
                    charArray[indexChArray++] = CH_PERCENT;

                    ch = fastForDigit((b >> 4) & 0xF);
                    if (isLetter(ch)) {
                        ch -= CASE_DIFF;
                    }
                    charArray[indexChArray++] = ch;

                    ch = fastForDigit(b & 0xF);
                    if (isLetter(ch)) {
                        ch -= CASE_DIFF;
                    }
                    charArray[indexChArray++] = ch;
                }
                cnt = 0;
                needToChange = true;
            }
        }
        if (needToChange) {
            if (indexChArray < charArray.length) {
                final char[] charArrRet = new char[indexChArray];
                System.arraycopy(charArray, 0, charArrRet, 0, indexChArray);
                return new String(charArrRet);
            }
            return new String(charArray);

        }
        return s;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static String urlDecode(final String s) {
        String str = null;
        try {
            str = urlDecode(s, DEF_ENCODE_NAME);
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    /**
     * 
     * @param s
     * @param enc
     * @return
     * @throws UnsupportedEncodingException
     */
    public static final String urlDecode(final String s, String enc)
            throws UnsupportedEncodingException {
        if (enc == null || enc.length() == 0) {
            throw new UnsupportedEncodingException(
                    "URLDecoder: empty string enc parameter");
        }
        final int numChars = s.length();
        final char[] chars = s.toCharArray();
        int i = 0, indexArray = 0, pos;
        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = chars[i];
            switch (c) {
                case CH_PLUS:
                    chars[indexArray++] = CH_SPACE;
                    i++;
                    break;
                case CH_PERCENT:
                    if (bytes == null) {
                        bytes = new byte[(numChars - i) / 3];
                    }
                    pos = 0;
                    try {
                        while (((i + 2) < numChars) && (c == CH_PERCENT)) {
                            bytes[pos++] = (byte) parseInt(
                                    s.substring(i + 1, i += 3), 16);
                            if (i < numChars) {
                                c = chars[i];
                            }
                        }
                        if ((i < numChars) && (c == CH_PERCENT)) {
                            throw new IllegalArgumentException(
                                    "URLDecoder: Incomplete trailing escape (%) pattern");
                        }
                        String stringTmp = new String(bytes, 0, pos, enc);
                        stringTmp.getChars(0, stringTmp.length(), chars, indexArray);
                        indexArray += stringTmp.length();
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "URLDecoder: Illegal hex characters in escape (%) pattern - "
                                + e.getMessage());
                    }
                    break;
                default:
                    chars[indexArray++] = c;
                    i++;
                    break;
            }
        }
        if (indexArray != numChars) {
            return new String(chars, 0, indexArray);
        }
        return s;
    }

    /**
     * 
     * @param octect
     * @return
     */
    public static boolean isWhiteSpace(final char octect) {
        return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
    }

    /**
     * 
     * @param octect
     * @return
     */
    public static final boolean isPad(final char octect) {
        return (octect == CH_EQUAL);
    }

    /**
     * 
     * @param octect
     * @return
     */
    private static final boolean isData(final char octect) {
        return (octect < BLENGTH && BASE64ALP[octect] != -1);
    }

    /**
     * 
     * @param binary
     * @return
     */
    public static String base64Encode(final byte[] binary) {
        if (binary == null) {
            return null;
        }
        final int lengthDataBits = binary.length * BIT8;
        if (lengthDataBits == 0) {
            return STR_EMP;
        }

        int fewerThan24bits = lengthDataBits % TF_BITGRP;
        int numberTriplets = lengthDataBits / TF_BITGRP;
        int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1
                : numberTriplets;
        final char[] encodedData = new char[numberQuartet * 4];
        int encodedIndex = 0;
        int dataIndex = 0;
        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;
        for (int i = 0; i < numberTriplets; i++) {
            b1 = binary[dataIndex++];
            b2 = binary[dataIndex++];
            b3 = binary[dataIndex++];

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                    : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
                    : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex++] = LBASE64ALP[val1];
            encodedData[encodedIndex++] = LBASE64ALP[val2 | (k << 4)];
            encodedData[encodedIndex++] = LBASE64ALP[(l << 2) | val3];
            encodedData[encodedIndex++] = LBASE64ALP[b3 & 0x3f];
        }

        if (fewerThan24bits == BIT8) {
            b1 = binary[dataIndex];
            k = (byte) (b1 & 0x03);
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex++] = LBASE64ALP[val1];
            encodedData[encodedIndex++] = LBASE64ALP[k << 4];
            encodedData[encodedIndex++] = CH_EQUAL;
            encodedData[encodedIndex++] = CH_EQUAL;
        } else if (fewerThan24bits == BIT16) {
            b1 = binary[dataIndex];
            b2 = binary[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                    : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex++] = LBASE64ALP[val1];
            encodedData[encodedIndex++] = LBASE64ALP[val2 | (k << 4)];
            encodedData[encodedIndex++] = LBASE64ALP[l << 2];
            encodedData[encodedIndex++] = CH_EQUAL;
        }

        return new String(encodedData);
    }

    /**
     * 
     * @param s
     * @return
     */
    public static byte[] base64Decode(final String s) {
        return base64Decode(s.toCharArray());
    }

    /**
     * 
     * @param sEnc
     * @return
     */
    private static byte[] base64Decode(final char[] charArr) {
        int len = removeWhiteSpace(charArr);

        if (len % BYTE4 != 0) {
            return null;
        }

        int numberQuadruple = (len / BYTE4);

        if (numberQuadruple == 0) {
            return new byte[0];
        }

        byte decodedData[] = null;

        int i = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        decodedData = new byte[(numberQuadruple) * 3];
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
        char d1 = 0, d2 = 0, d3 = 0, d4 = 0;
        for (; i < numberQuadruple - 1; i++) {
            if (!isData((d1 = charArr[dataIndex++]))
                    || !isData((d2 = charArr[dataIndex++]))
                    || !isData((d3 = charArr[dataIndex++]))
                    || !isData((d4 = charArr[dataIndex++]))) {
                return null;
            }
            b1 = BASE64ALP[d1];
            b2 = BASE64ALP[d2];
            b3 = BASE64ALP[d3];
            b4 = BASE64ALP[d4];

            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
        }
        if (!isData((d1 = charArr[dataIndex++]))
                || !isData((d2 = charArr[dataIndex++]))) {
            return null;
        }

        b1 = BASE64ALP[d1];
        b2 = BASE64ALP[d2];
        d3 = charArr[dataIndex++];
        d4 = charArr[dataIndex++];
        if (!isData((d3)) || !isData((d4))) {
            if (isPad(d3) && isPad(d4)) {
                if ((b2 & 0xf) != 0) {
                    return null;
                }
                byte[] tmp = new byte[i * 3 + 1];
                System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                return tmp;
            } else if (!isPad(d3) && isPad(d4)) {
                b3 = BASE64ALP[d3];
                if ((b3 & 0x3) != 0) {
                    return null;
                }
                byte[] tmp = new byte[i * 3 + 2];
                System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                return tmp;
            } else {
                return null;
            }
        } else {
            b3 = BASE64ALP[d3];
            b4 = BASE64ALP[d4];
            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
        }
        return decodedData;
    }

    /**
     * 
     * @param chars
     * @return
     */
    private static int removeWhiteSpace(final char[] chars) {
        if (chars == null) {
            return 0;
        }
        int newSize = 0;
        final int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (!isWhiteSpace(chars[i])) {
                chars[newSize++] = chars[i];
            }
        }
        return newSize;
    }

    /**
     * 
     * @param s
     * @param c
     * @return
     */
    public static String trim(final String s, final String c) {
        final int length = s.length();
        if (c == null) {
            return s;
        }
        final int cLength = c.length();
        if (c.length() == 0) {
            return s;
        }
        int start = 0;
        int end = length;
        boolean found = false;
        int i;
        for (i = 0; !found && i < length; i++) {
            char ch = s.charAt(i);
            found = true;
            for (int j = 0; found && j < cLength; j++) {
                if (c.charAt(j) == ch) {
                    found = false;
                }
            }
        }
        if (!found) {
            return STR_EMP;
        }
        start = i - 1;
        found = false;
        for (i = length - 1; !found && i >= 0; i--) {
            char ch = s.charAt(i);
            found = true;
            for (int j = 0; found && j < cLength; j++) {
                if (c.charAt(j) == ch) {
                    found = false;
                }
            }
        }
        end = i + 2;
        return s.substring(start, end);
    }

    /**
     * 
     * @param s
     * @param c
     * @return
     */
    public static String trim(final String s, final char c) {
        final int length = s.length();
        int cLength = 1;
        int start = 0;
        int end = length;
        boolean found = false;
        int i;
        for (i = 0; !found && i < length; i++) {
            char ch = s.charAt(i);
            found = true;
            for (int j = 0; found && j < cLength; j++) {
                if (c == ch) {
                    found = false;
                }
            }
        }
        if (!found) {
            return STR_EMP;
        }
        start = i - 1;
        found = false;
        for (i = length - 1; !found && i >= 0; i--) {
            char ch = s.charAt(i);
            found = true;
            for (int j = 0; found && j < cLength; j++) {
                if (c == ch) {
                    found = false;
                }
            }
        }
        end = i + 2;
        return s.substring(start, end);
    }

    /**
     * 
     * @param ch
     * @param start
     * @param length
     * @return
     */
    public static boolean isWhiteSpace(final char ch[], final int start,
            final int length) {
        int end = start + length;
        for (int s = start; s < end; s++) {
            if (!isWhiteSpace(ch[s])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param bytes
     * @return
     */
    public static char[] hexEncode(final byte[] bytes) {
        final int l = bytes.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS[0x0F & bytes[i]];
        }
        return out;
    }

    /**
     * 
     * @param str
     * @return
     * @throws RuntimeException
     */
    public static byte[] hexDecode(final String str) throws RuntimeException {
        return hexDecode(str.toCharArray(), str.length());
    }

    /**
     * 
     * @param data
     * @return
     * @throws RuntimeException
     */
    private static byte[] hexDecode(final char[] data, final int len)
            throws RuntimeException {
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    /**
     * 
     * @param ch
     * @param index
     * @return
     * @throws RuntimeException
     */
    private static int toDigit(final char ch, final int index)
            throws RuntimeException {
        final int digit = fastDigit(ch);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal charcter " + ch
                    + " at index " + index);
        }
        return digit;
    }

    /**
     * 
     * @param digit
     * @see Character#forDigit(int, int)
     * @return
     */
    public static char fastForDigit(final int digit) {
        if ((digit < 0)) {
            return CH_NULL;
        }
        if (digit < 10) {
            return (char) (CH_ZERO + digit);
        }
        return (char) (CH_LCASE_A - 10 + digit);
    }

    /**
     * 
     * @param c
     * @see Character#digit(char, int)
     * @return
     */
    public static int fastDigit(final char c) {
        if (c >= CH_ZERO && c <= '9') {
            return (c - CH_ZERO);
        }
        if (c >= CH_LCASE_A && c <= 'f') {
            return (c - CH_LCASE_A_M10);
        }

        if (c >= 'A' && c <= 'F') {
            return (c - CH_UCASE_A_M10);
        }
        throw ILLEGAL_CHARACTER;
    }

    /**
     * 
     * @param c
     * @see Character#digit(int, int)
     * @return
     */
    public static int fastDigit(final int c) {
        if (c >= CH_ZERO && c <= '9') {
            return (c - CH_ZERO);
        }
        if (c >= CH_LCASE_A && c <= 'f') {
            return (c - CH_LCASE_A_M10);
        }

        if (c >= 'A' && c <= 'F') {
            return (c - CH_UCASE_A_M10);
        }
        throw ILLEGAL_CHARACTER;
    }

    /**
     * 
     * @param c
     * @see Character#isLetter(char)
     * @return
     */
    public static boolean isLetter(final int c) {
        return (c >= 'A' && c <= 'Z') || (c >= CH_LCASE_A && c <= 'z');
    }

    /**
     * 
     * @param s
     * @return
     */
    public static String escapeForCSV(final String s) {
        if (s != null) {
            if ((s.indexOf(',') >= 0) || (s.indexOf('\n') >= 0)
                    || (s.indexOf('\r') >= 0) || (s.indexOf('\"') >= 0)) {
                final String tmp = StringUtil.replace(s, '\"', "\"\"");
                final char[] charArrayTmp = new char[tmp.length() + 2];
                charArrayTmp[0] = charArrayTmp[tmp.length() + 1] = '\"';
                tmp.getChars(1, tmp.length(), charArrayTmp, 1);
                return new String(charArrayTmp);
            } else {
                return s;
            }
        } else {
            return STR_EMP;
        }
    }

    /**
     * 
     * @param arr1
     * @param arr2
     * @return
     */
    public static String[] merge(final String[] arr1, final String[] arr2) {
        return (arr1 == null) ? ((arr2 == null) ? null : new ArrayListString(
                arr2, arr2.length).toArray())
                : ((arr2 == null)
                ? new ArrayListString(arr1, arr1.length).toArray()
                : new ArrayListString(arr1, arr1.length).append(arr2).toArray());
    }

    /**
     * 
     * @param s
     * @param len
     * @return
     */
    public static String spacePadRight(String s, final int len) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        int fll = len - s.length();
        final char[] charArrays = new char[len];
        s.getChars(0, s.length(), charArrays, 0);
        int index = s.length();
        int res = fll / 65;
        while (res > 0) {
            System.arraycopy(SPACE_POOL[64], 0, charArrays, index, 64);
            index += 65;
            res--;
        }
        fll = fll % 65;
        if (fll > 0) {
            System.arraycopy(SPACE_POOL[fll], 0, charArrays, index, SPACE_POOL[fll].length);
            index += SPACE_POOL[fll].length;
        }
        return new String(charArrays);
    }

    /**
     * 
     * @param s
     * @param len
     * @return
     */
    public static String spacePadLeft(String s, final int len) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        int fll = len - s.length();
        final char[] charArray = new char[len];
        int index = 0, res = fll / 65;
        while (res > 0) {
            System.arraycopy(SPACE_POOL[64], 0, charArray, index, 64);
            index += 65;
            res--;
        }
        res = fll;
        fll = fll % 65;
        if (fll > 0) {
            System.arraycopy(SPACE_POOL[fll], 0, charArray, index, SPACE_POOL[fll].length);
        }
        s.getChars(0, s.length(), charArray, res);
        return new String(charArray);
    }

    /**
     * 
     * @param s
     * @param len
     * @return
     */
    public static String zeroPadRight(String s, final int len) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        int fll = len - s.length(), index = s.length(), res = fll / 65;
        final char[] charArray = new char[len];
        s.getChars(0, s.length(), charArray, 0);
        while (res > 0) {
            System.arraycopy(ZERO_POOL[64], 0, charArray, index, 64);
            index += 65;
            res--;
        }
        fll = fll % 65;
        if (fll > 0) {
            System.arraycopy(ZERO_POOL[fll], 0, charArray, index, ZERO_POOL[fll].length);
        }
        return new String(charArray);
    }

    /**
     * 
     * @param s
     * @param len
     * @return
     */
    public static String zeroPadLeft(String s, final int len) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        int fll = len - s.length();
        final char[] charArray = new char[len];
        int index = 0, res = fll / 65;
        while (res > 0) {
            System.arraycopy(ZERO_POOL[64], 0, charArray, index, 64);
            index += 65;
            res--;
        }
        res = fll;
        fll = fll % 65;
        if (fll > 0) {
            System.arraycopy(ZERO_POOL[fll], 0, charArray, index, ZERO_POOL[fll].length);
        }
        s.getChars(0, s.length(), charArray, res);
        return new String(charArray);
    }

    /**
     * 
     * @param s
     * @param len
     * @param c
     * @return
     */
    public static String padright(String s, final int len, final char c) {
        if (c == '0') {
            return zeroPadRight(s, len);
        } else if (c == ' ') {
            return spacePadRight(s, len);
        }
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        final char[] charsArray = new char[len];
        int index = s.length();
        s.getChars(0, s.length(), charsArray, 0);
        int fll = len - s.length();
        while (fll-- > 0) {
            charsArray[index++] = c;
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param s
     * @param len
     * @param c
     * @return
     */
    public static String padleft(String s, final int len, char c) {
        if (c == '0') {
            return zeroPadLeft(s, len);
        } else if (c == ' ') {
            return spacePadLeft(s, len);
        }
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        final char[] charArray = new char[len];
        int index = 0;
        int fll = len - s.length();
        while (fll-- > 0) {
            charArray[index++] = c;
        }
        s.getChars(0, s.length(), charArray, index);
        return new String(charArray);
    }

    /**
     * 
     * @param regex
     * @param array
     * @return
     */
    public static boolean matchRegex(final String regex, final String[] array) {
        final Pattern pat = new Pattern(regex);
        for (int i = array.length; i >= 0; i--) {
            if (!pat.contains(array[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param regex
     * @param s
     * @return
     */
    public static boolean matchRegex(final String regex, final char[] s) {
        final Pattern pat = new Pattern(regex);
        return pat.contains(s, 0, s.length);
    }

    /**
     * 
     * @param regex
     * @param s
     * @return
     */
    public static boolean matchRegex(final String regex, final String s) {
        final Pattern pat = new Pattern(regex);
        return pat.contains(s);
    }

    /**
     * 
     * @param s
     * @param escapeAscii
     * @return
     */
    public static String escapeUnicodeString(final String s,
            final boolean escapeAscii) {
        if (s == null) {
            return null;
        }
        final StringBuilder unicodeWord = new StringBuilder();
        final int len = s.length();
        if (!escapeAscii) {
            for (int i = 0; i < len; i++) {
                char ch = s.charAt(i);
                if ((ch >= CH_SPACE) && (ch <= '~')) {
                    unicodeWord.append(ch);
                } else {
                    unicodeWord.append("\\u");
                    String hex = Integer.toHexString(ch & 0xFFFF);
                    int codeSizeDiff = 4 - hex.length();
                    if (codeSizeDiff > 0) {
                        for (int diff = 0; diff < codeSizeDiff; diff++) {
                            unicodeWord.append('0');
                        }
                    }
                    unicodeWord.append(toUpperCase(hex));
                }
            }
        } else {
            for (int i = 0; i < len; i++) {
                char ch = s.charAt(i);
                unicodeWord.append("\\u");
                String hex = Integer.toHexString(ch & 0xFFFF);
                int codeSizeDiff = 4 - hex.length();
                if (codeSizeDiff > 0) {
                    for (int diff = 0; diff < codeSizeDiff; diff++) {
                        unicodeWord.append('0');
                    }
                }
                unicodeWord.append(toUpperCase(hex));
            }
        }

        return unicodeWord.toString();
    }

    /**
     * 
     * @param buf
     * @return
     */
    public static String asHex(final byte[] buf) {
        final int sz = buf.length;
        final char[] chars = new char[2 * sz];
        for (int i = 0, o = 0; i < sz; ++i) {
            int index = buf[i];
            chars[o++] = BYTEHEX[index++];
            chars[o++] = BYTEHEX[index];
        }
        return new String(chars, 0, sz);
    }

    /**
     * 
     * @param str
     * @param count
     * @return
     */
    public static String repeat(final String str, final int count) {
        if (count <= 0) {
            return str;
        }
        final int strLen = str.length();
        final char[] bufCharArray = new char[strLen * count];
        for (int i = count - 1; i >= 0; str.getChars(0, strLen, bufCharArray, i * strLen), i--);
        return new String(bufCharArray);
    }

    /**
     * 
     * @param ch
     * @param times
     * @return
     */
    public static String repeat(char ch, int times) {
        char[] buffer = new char[times];
        for (int i = times - 1; i >= 0; buffer[i--] = ch);
        return new String(buffer, 0, times);
    }

    /**
     * 
     * @param sep
     * @param strings
     * @return
     */
    public static final String join(String sep, final String[] strings) {
        if (strings == null) {
            return null;
        }
        final int length = strings.length;
        if (length == 0) {
            return STR_EMP;
        }
        final int sepLen;
        if (sep == null) {
            sepLen = 4;
            sep = "null";
        } else {
            sepLen = sep.length();
        }
        int intTmp = strings[length - 1] == null ? 4 : strings[length - 1].length();
        for (int i = length - 2; i >= 0; i--) {
            intTmp += sepLen + (strings[i] == null ? 4 : strings[i].length());
        }
        final char[] charsArray = new char[intTmp];
        if (strings[0] == null) {
            charsArray[0] = 'n';
            charsArray[1] = 'u';
            charsArray[2] = 'l';
            charsArray[3] = 'l';
            intTmp = 4;
        } else if (strings[0].length() != 0) {
            strings[0].getChars(0, strings[0].length(), charsArray, 0);
            intTmp += strings[0].length();
        }
        for (int i = 1; i < length; i++) {
            sep.getChars(0, sepLen, charsArray, intTmp);
            intTmp += sepLen;
            if (strings[0] == null) {
                charsArray[intTmp + 1] = 'n';
                charsArray[intTmp + 2] = 'u';
                charsArray[intTmp + 3] = 'l';
                charsArray[intTmp + 4] = 'l';
                intTmp += 4;
            } else if (strings[i].length() != 0) {
                strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
                intTmp += strings[i].length();
            }
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param sep
     * @param objects
     * @return 
     */
    public static final String join(String sep, final Object[] objects) {
        if (objects == null) {
            return null;
        }
        final int length = objects.length;
        if (length == 0) {
            return STR_EMP;
        }
        final int sepLen;
        if (sep == null) {
            sepLen = 4;
            sep = "null";
        } else {
            sepLen = sep.length();
        }
        final String[] strings = new String[length];
        strings[length - 1] = objects[length - 1] == null ? "null" : objects[length - 1].toString();
        int intTmp = strings[length - 1].length();
        for (int i = length - 2; i >= 0; i--) {
            strings[i] = objects[i] == null ? "null" : objects[i].toString();
            intTmp += sepLen + strings[i].length();
        }
        final char[] charsArray = new char[intTmp];
        strings[0].getChars(0, strings[0].length(), charsArray, 0);
        intTmp = strings[0].length();
        for (int i = 1; i < length; i++) {
            sep.getChars(0, sepLen, charsArray, intTmp);
            intTmp += sepLen;
            strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
            intTmp += strings[i].length();
        }
        return new String(charsArray);

    }

    /**
     * 
     * @param sep
     * @param strings
     * @return 
     */
    public static final String join(final char sep, final String[] strings) {
        if (strings == null) {
            return null;
        }
        final int length = strings.length;
        if (length == 0) {
            return STR_EMP;
        }
        int intTmp = strings[length - 1] == null ? 4 : strings[length - 1].length();
        for (int i = length - 2; i >= 0; i--) {
            intTmp += 1 + (strings[i] == null ? 4 : strings[i].length());
        }
        final char[] charsArray = new char[intTmp];
        if (strings[0] == null) {
            charsArray[0] = 'n';
            charsArray[1] = 'u';
            charsArray[2] = 'l';
            charsArray[3] = 'l';
            intTmp = 4;
        } else if (strings[0].length() != 0) {
            strings[0].getChars(0, strings[0].length(), charsArray, 0);
            intTmp += strings[0].length();
        }
        for (int i = 1; i < length; i++) {
            charsArray[intTmp] = sep;
            intTmp += 1;
            if (strings[0] == null) {
                charsArray[intTmp + 1] = 'n';
                charsArray[intTmp + 2] = 'u';
                charsArray[intTmp + 3] = 'l';
                charsArray[intTmp + 4] = 'l';
                intTmp += 4;
            } else if (strings[i].length() != 0) {
                strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
                intTmp += strings[i].length();
            }
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param sep
     * @param objects
     * @return 
     */
    public static final String join(char sep, final Object[] objects) {
        if (objects == null) {
            return null;
        }
        final int length = objects.length;
        if (length == 0) {
            return STR_EMP;
        }
        final String[] strings = new String[length];
        strings[length - 1] = objects[length - 1] == null ? "null" : objects[length - 1].toString();
        int intTmp = strings[length - 1].length();
        for (int i = length - 2; i >= 0; i--) {
            strings[i] = objects[i] == null ? "null" : objects[i].toString();
            intTmp += 1 + strings[i].length();
        }
        final char[] charsArray = new char[intTmp];
        strings[0].getChars(0, strings[0].length(), charsArray, 0);
        intTmp = strings[0].length();
        for (int i = 1; i < length; i++) {
            charsArray[intTmp] = sep;
            intTmp += 1;
            strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
            intTmp += strings[i].length();
        }
        return new String(charsArray);

    }

    /**
     * 
     * @param sep
     * @param it
     * @return
     */
    public static String join(String sep, final List list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return STR_EMP;
        }
        final int length = list.size();
        final int sepLen;
        if (sep == null) {
            sepLen = 4;
            sep = "null";
        } else {
            sepLen = sep.length();
        }
        final String[] strings = new String[length];
        int intTmp = length - 1;
        strings[intTmp] = list.get(intTmp) == null ? "null" : list.get(intTmp).toString();
        intTmp = strings[intTmp].length();

        for (int i = length - 2; i >= 0; i--) {
            strings[i] = list.get(i) == null ? "null" : list.get(i).toString();
            intTmp += sepLen + strings[i].length();
        }
        final char[] charsArray = new char[intTmp];
        strings[0].getChars(0, strings[0].length(), charsArray, 0);
        intTmp = strings[0].length();
        for (int i = 1; i < length; i++) {
            sep.getChars(0, sepLen, charsArray, intTmp);
            intTmp += sepLen;
            strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
            intTmp += strings[i].length();
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param sep
     * @param list
     * @return 
     */
    public static String join(char sep, final List list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return STR_EMP;
        }
        final int length = list.size();
        final String[] strings = new String[length];
        int intTmp = length - 1;
        strings[intTmp] = list.get(intTmp) == null ? "null" : list.get(intTmp).toString();
        intTmp = strings[intTmp].length();

        for (int i = length - 2; i >= 0; i--) {
            strings[i] = list.get(i) == null ? "null" : list.get(i).toString();
            intTmp += 1 + strings[i].length();
        }
        final char[] charsArray = new char[intTmp];
        strings[0].getChars(0, strings[0].length(), charsArray, 0);
        intTmp = strings[0].length();
        for (int i = 1; i < length; i++) {
            charsArray[intTmp] = sep;
            intTmp += 1;
            strings[i].getChars(0, strings[i].length(), charsArray, intTmp);
            intTmp += strings[i].length();
        }
        return new String(charsArray);
    }

    /**
     * 
     * @param str
     * @param len
     * @return
     */
    public static String truncate(final String str, final int len) {
        return (str.length() <= len) ? str : str.substring(0, len);
    }

    /**
     * 
     * @param objs
     * @return
     */
    public static String toString(final Object[] objs) {
        if (objs == null) {
            return STR_EMP;
        }
        final int len = objs.length;
        if (len == 0) {
            return STR_EMP;
        }
        final StringBuilder buf = new StringBuilder(len * 12);
        for (int i = 0; i < len - 1; i++) {
            buf.append(objs[i]).append(", ");
        }
        return buf.append(objs[len - 1]).toString();
    }

    /**
     * 
     * @param str
     * @return
     */
    public static boolean isQuoted(final String str) {
        return str != null && str.length() != 0 && str.charAt(0) == '`'
                && str.charAt(str.length() - 1) == '`';
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final String quote(final String str) {
        return (str == null || str.length() == 0 || (str.charAt(0) == '`' && str.charAt(str.length() - 1) == '`')) ? str : new StringBuilder(
                str.length() + 2).append('`').append(str).append('`').toString();
    }

    /**
     * 
     * @param lengths
     * @param str
     * @return
     */
    public static final String[] splitFixLength(final int[] lengths,
            final String str) {
        return splitFixLength(lengths, str, 0);
    }

    /**
     * 
     * @param lengths
     * @param str
     * @param startFrom
     * @return
     */
    public static final String[] splitFixLength(final int[] lengths,
            final String str, final int startFrom) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0 || lengths == null || lengths.length == 0
                || lengths.length == 1) {
            return new String[]{str};
        }
        final int arrLength = lengths.length;
        String[] arrayString = new String[arrLength];
        int start = startFrom;
        String temp;
        int i = 0;
        try {
            int tmpLen;
            for (i = 0; i < arrLength; i++) {
                tmpLen = lengths[i];
                temp = str.substring(start, start + tmpLen);
                arrayString[i] = temp;
                start += tmpLen;
            }
        } catch (StringIndexOutOfBoundsException strIdxEx) {
            temp = str.substring(start, str.length());
            arrayString[i] = temp;
        }
        if (i == arrLength) {
            return arrayString;
        }
        final String[] tmpArrStr = new String[i];
        System.arraycopy(arrayString, 0, tmpArrStr, 0, i);
        return tmpArrStr;
    }

    /**
     * 
     * @param str
     * @param finder
     * @return
     */
    public static final int countText(final String str, final String finder) {
        if ((str == null) || (finder == null) || (str.length() == 0)
                || (finder.length() == 0)) {
            return 0;
        }
        int count = 0;
        int pos = 0, idx;
        while ((idx = str.indexOf(finder, pos)) != -1) {
            count++;
            pos = idx + finder.length();
        }
        return count;
    }

    /**
     * 
     * @param array
     * @return
     */
    public static final String[] removeDuplicate(final String[] array) {
        if (array == null || array.length < 2) {
            return array;
        }
        final int length = array.length;
        final ArrayListString list = new ArrayListString(length);

        for (int i = 0; i < length; i++) {
            if (!list.contains(array[i])) {
                list.add(array[i]);
            }
        }
        return list.toArray();
    }

    /**
     * 
     * @param strLocale
     * @return
     */
    public static final Locale parseLocaleString(final String strLocale) {
        if (strLocale == null || strLocale.length() == 0) {
            return null;
        }
        final String[] parts = split(strLocale, '_');
        final int length = parts.length;
        if (length > 3) {
            return new Locale(parts[0], parts[2], parts[3]);
        }
        switch (length) {
            case 1:
                return new Locale(parts[0], STR_EMP, STR_EMP);
            case 2:
                return new Locale(parts[0], parts[1], STR_EMP);
            case 3:
                return new Locale(parts[0], parts[2], parts[3]);
        }
        return null;
    }

    /**
     * 
     * @param strArray
     * @param str
     * @param ignoreCase
     * @return
     */
    public static final boolean isExist(final String[] strArray,
            final String str, final boolean ignoreCase) {
        if ((!isEmptyWithTrim(str)) || (strArray == null)
                || (strArray.length == 0)) {
            return false;
        }
        if (ignoreCase) {
            for (int i = 0; i < strArray.length; i++) {
                if (str.equalsIgnoreCase(strArray[i])) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < strArray.length; i++) {
                if (str.equals((strArray[i]))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static String ltrim(final String str) {
        if (str == null || str.length() == 0) {
            return null;
        } else if (str.length() < 2) {
            return str.charAt(0) == ' ' ? STR_EMP : str;
        }
        int sPos = 1;
        int sidx;
        while ((sidx = str.indexOf(' ', sPos)) != -1) {
            if (StringUtil.isLetter(str.charAt(sidx - 1))) {
                break;
            } else {
                sPos++;
            }
        }
        return sPos == 1 ? str : str.substring(sPos, str.length());
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final String rtrim(final String str) {
        if (str == null || str.length() == 0) {
            return null;
        } else if (str.length() < 3) {
            char c = str.charAt(0);
            return c == ' ' ? str.charAt(1) == ' ' ? STR_EMP : str : str.charAt(1) == ' ' ? new String(new char[]{c}) : str;
        }
        int sPos = str.length() - 2;
        int sidx;
        while ((sidx = str.lastIndexOf(' ', sPos)) != -1) {
            if (StringUtil.isLetter(str.charAt(sidx + 1))) {
                break;
            } else {
                sPos--;
            }
        }
        return (sPos == str.length() - 2) ? ((str.lastIndexOf(' ', str.length()) != -1) ? str.substring(0,
                sPos + 1) : str) : str.substring(0, sPos + 1);
    }

    /**
     * 
     * @param string1
     * @param string2
     * @return 
     */
    public static final String add(final String string1, final String string2) {
        if (string1 == null) {
            if (string2 == null) {
                return "nullnull";
            } else if (string2.length() == 0) {
                return "null";
            } else {
                final char[] newChars = new char[string2.length() + 4];
                newChars[0] = 'n';
                newChars[1] = 'u';
                newChars[2] = 'l';
                newChars[3] = 'l';
                string2.getChars(0, string2.length(), newChars, 4);
                return new String(newChars);
            }
        } else if (string1.length() == 0) {
            if (string2 == null) {
                return "null";
            } else if (string2.length() == 0) {
                return "";
            } else {
                return string2;
            }
        } else if (string2 == null) {
            final int strLen = string1.length();
            final char[] newChars = new char[strLen + 4];
            string1.getChars(0, strLen, newChars, 0);
            newChars[strLen] = 'n';
            newChars[strLen + 1] = 'u';
            newChars[strLen + 2] = 'l';
            newChars[strLen + 3] = 'l';
            return new String(newChars);
        } else if (string2.length() == 0) {
            return string1;
        } else {
            char[] newChars = new char[string1.length() + string2.length()];
            string1.getChars(0, string1.length(), newChars, 0);
            string2.getChars(0, string2.length(), newChars, string1.length());
            return new String(newChars);
        }
    }

    /**
     * 
     * @param string1
     * @param integer2
     * @return 
     */
    public static final String add(final String string1, final int integer2) {
        if (string1 == null) {
            String string2 = Integer.toString(integer2);
            final char[] newChars = new char[string2.length() + 4];
            newChars[0] = 'n';
            newChars[1] = 'u';
            newChars[2] = 'l';
            newChars[3] = 'l';
            string2.getChars(0, string2.length(), newChars, 4);
            return new String(newChars);
        } else if (string1.length() == 0) {
            return Integer.toString(integer2);
        } else {
            String string2 = Integer.toString(integer2);
            char[] newChars = new char[string1.length() + string2.length()];
            string1.getChars(0, string1.length(), newChars, 0);
            string2.getChars(0, string2.length(), newChars, string1.length());
            return new String(newChars);
        }
    }

    /**
     * 
     * @param integer1
     * @param string2
     * @return 
     */
    public static final String add(final int integer1, final String string2) {
        if (string2 == null) {
            String string1 = Integer.toString(integer1);
            final int strLen = string1.length();
            final char[] newChars = new char[strLen + 4];
            string1.getChars(0, strLen, newChars, 0);
            newChars[strLen] = 'n';
            newChars[strLen + 1] = 'u';
            newChars[strLen + 2] = 'l';
            newChars[strLen + 3] = 'l';
            return new String(newChars);
        } else if (string2.length() == 0) {
            return Integer.toString(integer1);
        } else {
            String string1 = Integer.toString(integer1);
            char[] newChars = new char[string1.length() + string2.length()];
            string1.getChars(0, string1.length(), newChars, 0);
            string2.getChars(0, string2.length(), newChars, string1.length());
            return new String(newChars);
        }
    }

    /**
     * 
     * @param integer1
     * @param integer2
     * @return 
     */
    public static final String add(final int integer1, final int integer2) {
        String string1 = Integer.toString(integer1);
        String string2 = Integer.toString(integer2);
        char[] newChars = new char[string1.length() + string2.length()];
        string1.getChars(0, string1.length(), newChars, 0);
        string2.getChars(0, string2.length(), newChars, string1.length());
        return new String(newChars);
    }

    /**
     * 
     * @param integer1
     * @param integer2
     * @return 
     */
    public static final String add(final Integer integer1, final Integer integer2) {
        if (integer1 == null) {
            if (integer2 == null) {
                return "nullnull";
            }
            String string2 = Integer.toString(integer2);
            char[] newChars = new char[4 + string2.length()];
            newChars[0] = 'n';
            newChars[1] = 'u';
            newChars[2] = 'l';
            newChars[3] = 'l';
            string2.getChars(0, string2.length(), newChars, 4);
            return new String(newChars);

        } else if (integer2 == null) {
            String string1 = Integer.toString(integer1);
            int strLen = string1.length();
            char[] newChars = new char[string1.length() + 4];
            string1.getChars(0, string1.length(), newChars, 0);
            newChars[strLen] = 'n';
            newChars[strLen + 1] = 'u';
            newChars[strLen + 2] = 'l';
            newChars[strLen + 3] = 'l';
            return new String(newChars);

        } else {
            String string1 = Integer.toString(integer1);
            String string2 = Integer.toString(integer2);
            char[] newChars = new char[string1.length() + string2.length()];
            string1.getChars(0, string1.length(), newChars, 0);
            string2.getChars(0, string2.length(), newChars, string1.length());
            return new String(newChars);
        }
    }
}
