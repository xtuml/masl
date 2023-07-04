/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Filter {

    private static final String split_ = "[^\\p{Alnum}]+";
    private static final String splitU_Ul = "(?<=\\p{Upper})(?=\\p{Upper}\\p{Lower})";
    private static final String splitl_U = "(?<=\\p{Lower})(?=\\p{Upper})";
    private static final String split9_A = "(?<=\\p{Digit})(?=\\p{Alpha})";
    private static final String splitA_9 = "(?<=\\p{Alpha})(?=\\p{Digit})";

    private static final Pattern
            splitPattern =
            Pattern.compile(split_ + "|" + splitU_Ul + "|" + splitl_U + "|" + split9_A + "|" + splitA_9);

    interface WordFilter {

        String convert(String string);
    }

    public static final WordFilter TO_LOWER = new WordFilter() {

        @Override
        public String convert(final String string) {
            return string.toLowerCase();
        }
    };

    public static final WordFilter TO_UPPER = new WordFilter() {

        @Override
        public String convert(final String string) {
            return string.toUpperCase();
        }
    };

    public static final WordFilter TO_CAMEL = new WordFilter() {

        @Override
        public String convert(final String string) {
            return TextUtils.upperFirst(string.toLowerCase());
        }
    };

    public static final WordFilter NO_CHANGE = new WordFilter() {

        @Override
        public String convert(final String string) {
            return string;
        }
    };

    public static final WordFilter INITIAL_LETTER = new WordFilter() {

        @Override
        public String convert(final String string) {
            return string.substring(0, 1);
        }
    };

    private final String separator;
    private final WordFilter firstWordFilter;
    private final WordFilter wordFilter;

    private final List<String> prefix;
    private final List<String> suffix;

    Filter(final WordFilter wordFilter, final String separator) {
        this(new ArrayList<String>(), wordFilter, wordFilter, separator, new ArrayList<String>());
    }

    Filter(final WordFilter firstWordFilter, final WordFilter wordFilter, final String separator) {
        this(new ArrayList<String>(), firstWordFilter, wordFilter, separator, new ArrayList<String>());
    }

    Filter(final List<String> prefix,
           final WordFilter firstWordFilter,
           final WordFilter wordFilter,
           final String separator,
           final List<String> suffix) {
        this.separator = separator;
        this.prefix = prefix;
        this.suffix = suffix;
        this.firstWordFilter = firstWordFilter;
        this.wordFilter = wordFilter;
    }

    public Filter addPrefix(final String prefix) {
        final LinkedList<String> newPrefix = new LinkedList<String>(this.prefix);
        newPrefix.addFirst(prefix);
        return new Filter(newPrefix, firstWordFilter, wordFilter, separator, suffix);
    }

    public Filter addSuffix(final String suffix) {
        final LinkedList<String> newSuffix = new LinkedList<String>(this.suffix);
        newSuffix.addLast(suffix);
        return new Filter(prefix, firstWordFilter, wordFilter, separator, newSuffix);
    }

    public String convert(final String string) {

        final List<Object> allWords = new ArrayList<Object>();
        allWords.addAll(prefix);
        allWords.addAll(Arrays.asList(splitPattern.split(string)));
        allWords.addAll(suffix);

        final String[] words = allWords.toArray(new String[allWords.size()]);

        if (words.length > 0) {
            words[0] = firstWordFilter.convert(words[0]);
        }

        for (int i = 1; i < words.length; ++i) {
            words[i] = wordFilter.convert(words[i]);
        }

        return TextUtils.formatList(Arrays.asList(words), "", separator, "");
    }

    public static final Filter CamelCase = new Filter(TO_CAMEL, "");
    public static final Filter camelCase = new Filter(TO_LOWER, TO_CAMEL, "");
    public static final Filter UPPER_CASE = new Filter(TO_UPPER, "_");
    public static final Filter lower_case = new Filter(TO_LOWER, "_");
    public static final Filter nullFilter = new Filter(NO_CHANGE, "_");
    public static final Filter initialLetters = new Filter(INITIAL_LETTER, "");
}
