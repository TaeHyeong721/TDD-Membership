package com.th.atdd.membership.app.membership.dto;

import com.th.atdd.membership.app.enums.MembershipType;
import com.th.atdd.membership.app.membership.validation.ValidationGroups.MembershipAccumulateMarker;
import com.th.atdd.membership.app.membership.validation.ValidationGroups.MembershipAddMaker;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MembershipRequest {

    @NotNull(groups = {MembershipAddMaker.class, MembershipAccumulateMarker.class})
    @Min(value = 0, groups = {MembershipAddMaker.class, MembershipAccumulateMarker.class})
    private final Integer point;

    @NotNull(groups = {MembershipAddMaker.class})
    private final MembershipType membershipType;
}
