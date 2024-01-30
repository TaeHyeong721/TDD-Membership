package com.th.atdd.membership.app.membership.repository;

import com.th.atdd.membership.app.enums.MembershipType;
import com.th.atdd.membership.app.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

    List<Membership> findAllByUserId(String userId);
}
