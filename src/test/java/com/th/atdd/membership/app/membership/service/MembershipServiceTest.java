package com.th.atdd.membership.app.membership.service;

import com.th.atdd.membership.app.enums.MembershipType;
import com.th.atdd.membership.app.membership.dto.MembershipAddResponse;
import com.th.atdd.membership.app.membership.dto.MembershipDetailResponse;
import com.th.atdd.membership.app.membership.entity.Membership;
import com.th.atdd.membership.app.membership.repository.MembershipRepository;
import com.th.atdd.membership.app.point.service.RatePointService;
import com.th.atdd.membership.exception.MembershipErrorResult;
import com.th.atdd.membership.exception.MembershipException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @InjectMocks
    private MembershipService target;

    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private RatePointService ratePointService;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;
    private final Long membershipId = -1L;

    @Test
    public void 멤버십등록실패_이미존재함() {
        //given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        //when&then
        assertThatThrownBy(() -> target.addMembership(userId, membershipType, point))
                .isInstanceOf(MembershipException.class);

    }

    @Test
    public void 멤버십등록성공() {
        //given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        //when
        final MembershipAddResponse result = target.addMembership(userId, membershipType, point);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);

        //verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(MembershipType.NAVER)
                .build();
    }

    @Test
    public void 멤버십목록조회() {
        //given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        //when
        final List<MembershipDetailResponse> result = target.getMembershipList(userId);

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 멤버십상세조회실패_존재하지않음() throws Exception {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회_본인이아님() throws Exception {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회성공() throws Exception {
        //given
        doReturn(Optional.of(membership())).when(membershipRepository).findById(membershipId);

        //when
        final MembershipDetailResponse result = target.getMembership(membershipId, userId);

        //then
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(result.getPoint()).isEqualTo(point);
    }

    @Test
    public void 멤버십삭제실패_존재하지않음() throws Exception {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님() throws Exception {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, "notowner"));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십삭제성공() throws Exception {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.removeMembership(membershipId, userId);

        //then
    }

    @Test
    public void 멤버십적립실패_존재하지않음() throws Exception {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, userId, 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십적립실패_본인이아님() throws Exception {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, "notowner", 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십적립성공() throws Exception {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.accumulateMembershipPoint(membershipId, userId, 10000);

        //then
    }
}
