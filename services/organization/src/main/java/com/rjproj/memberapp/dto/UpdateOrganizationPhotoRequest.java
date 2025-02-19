package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.ImageType;

public record UpdateOrganizationPhotoRequest(
        ImageType imageType
) {
}
