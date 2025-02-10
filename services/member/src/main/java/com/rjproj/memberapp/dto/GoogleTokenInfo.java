package com.rjproj.memberapp.dto;

import java.util.List;

public record GoogleTokenInfo(
        String sub,         // Unique user identifier
        String aud,         // Audience (your client ID)
        String email,       // User email
        String name,        // Full name
        String given_name,  // First name
        String family_name, // Last name
        String picture,     // Profile picture URL
        String locale,      // Locale (e.g., en-US)
        String hd,          // Hosted domain (if applicable)
        Long iat,           // Issued at
        Long exp,           // Expiration
        List<Address> addresses  // New field for addresses
) {
    // Define an inner Address class (or a record)
    public record Address(
            String streetAddress,
            String city,
            String region,
            String postalCode,
            String country
    ) {}
}
