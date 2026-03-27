package com.icm.telemetria_peru_api.utils;

public final class DvrPhoneNormalizer {
    private DvrPhoneNormalizer() {
    }

    public static String normalize(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return null;
        }

        String digits = rawPhone.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }

        if (digits.length() < 12) {
            digits = "0".repeat(12 - digits.length()) + digits;
        } else if (digits.length() > 12) {
            digits = digits.substring(digits.length() - 12);
        }

        return digits;
    }
}
