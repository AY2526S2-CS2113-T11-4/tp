package dextro.parser;

import java.util.HashMap;
import java.util.Map;

public class ArgumentTokenizer {

    private final Map<String, String> tokenMap = new HashMap<>();

    public ArgumentTokenizer(String args, String... prefixes) {
        for (String prefix : prefixes) {
            int start = args.indexOf(prefix);
            if (start != -1) {
                int valueStart = start + prefix.length();
                int nextPrefixIndex = args.length();
                for (String other : prefixes) {
                    if (!other.equals(prefix)) {
                        int i = args.indexOf(other, valueStart);
                        if (i != -1 && i < nextPrefixIndex) {
                            nextPrefixIndex = i;
                        }
                    }
                }
                String value = args.substring(valueStart, nextPrefixIndex).trim();
                tokenMap.put(prefix, value);
            }
        }
    }

    public String getValue(String prefix) {
        return tokenMap.get(prefix);
    }
}
