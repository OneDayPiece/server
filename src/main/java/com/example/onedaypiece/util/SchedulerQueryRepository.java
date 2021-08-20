package com.example.onedaypiece.util;

import com.example.onedaypiece.web.domain.challengeRecord.ChallengeRecord;
import com.example.onedaypiece.web.domain.challengeRecord.QChallengeRecord;
import com.example.onedaypiece.web.domain.posting.QPosting;
import com.example.onedaypiece.web.dto.query.posting.QSchedulerIdListDto;
import com.example.onedaypiece.web.dto.query.posting.SchedulerIdListDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.onedaypiece.web.domain.challengeRecord.QChallengeRecord.*;
import static com.example.onedaypiece.web.domain.posting.QPosting.*;
import static com.querydsl.jpa.JPAExpressions.*;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulerQueryRepository {

    private final JPAQueryFactory queryFactory;


    /**
    *진행중인 챌린지
     *
     */
    public List<ChallengeRecord> findAllByChallenge(int week) {
        return queryFactory
                .selectFrom(challengeRecord)
                .innerJoin(challengeRecord.challenge)
                .where(getEmpty(week),
                        challengeRecord.challengeRecordStatus.isTrue(),
                        challengeRecord.challenge.challengeProgress.eq(2L))
                .fetch();
    }

    private BooleanExpression getEmpty(int week) {
        BooleanExpression notEmpty = challengeRecord.challenge.challengeHoliday.isNotEmpty();
        BooleanExpression empty = challengeRecord.challenge.challengeHoliday.eq("");
        return week == 6 || week ==7 ? notEmpty:empty ;
    }

    /**
     *인증받지 못한자
     */
    public List<SchedulerIdListDto> findUncertifiedList(List<Long> challengeId, List<Long> memberId, LocalDateTime today){

        return queryFactory.select(new QSchedulerIdListDto(
                posting.challenge.challengeId,
                posting.member.memberId))
                .from(posting)

                .where(posting.postingStatus.isTrue(),
                        posting.challenge.challengeId.in(challengeId),
                        posting.member.memberId.in(memberId),
                        posting.createdAt.lt(today),
                        posting.postingCount.eq(1L))
                .fetch();
    }
    /**
     *글 쓰지 않은 자
     */
    public List<SchedulerIdListDto> findNotWrittenList(List<Long> challengeId){

        return queryFactory.select(new QSchedulerIdListDto(
                challengeRecord.challenge.challengeId,
                challengeRecord.member.memberId)).distinct()
                .from(challengeRecord)
                .leftJoin(posting)
                .on(challengeRecord.challenge.challengeId.eq(posting.challenge.challengeId),
                        (challengeRecord.member.memberId.eq(posting.member.memberId)))
                .where(challengeRecord.challengeRecordStatus.isTrue(),
                        challengeRecord.challenge.challengeId.in(challengeId),
                        posting.isNull())
                .fetch();
    }
    /**
     * 수정 가능 여부 (당일만 가능)
     */
    public List<Long> findSchedulerUpdatePosting(LocalDateTime today) {
        return queryFactory
                .select(posting.postingId)
                .from(posting)
                .where(posting.postingStatus.isTrue(),
                        posting.postingModifyOk.isTrue(),
                        posting.createdAt.lt(today))
                .fetch();
    }

}
