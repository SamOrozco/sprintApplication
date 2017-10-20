package com.company.utils;

import java.util.Collection;

public class ValidationUtils {
    public static boolean nullOrEmpty(String value) {
        return (value == null || value.isEmpty());
    }

    public static boolean looseStringCollectionMatch(Collection<String> valueCollection, Collection<String> valueTwoCollection) {
        if (valueCollection.size() != valueTwoCollection.size()) return false;
        for (String valueOne : valueCollection) {
            if (!valueTwoCollection.contains(valueOne)) return false;
        }
        return true;
    }

}
