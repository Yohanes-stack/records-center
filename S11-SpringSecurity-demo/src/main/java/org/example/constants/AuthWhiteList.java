package org.example.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthWhiteList {

    public static final List<String> AUTH_WHITELIST;

    static {
        /**
         * 需要放行的URL
         */
        final String[] WHITELIST = {
                // -- register url
                "/sign",
                // -- swagger ui
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/getAuthentication"
                // other public endpoints of your API may be appended to this array
        };
        AUTH_WHITELIST = Stream.of(WHITELIST).collect(Collectors.toList());
    }

}