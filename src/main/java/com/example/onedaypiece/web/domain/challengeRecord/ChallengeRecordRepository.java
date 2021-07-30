package com.example.onedaypiece.web.domain.challengeRecord;

import com.example.onedaypiece.web.domain.challenge.Challenge;
import com.example.onedaypiece.web.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ChallengeRecordRepository extends JpaRepository<ChallengeRecord, Long> {
    @Query("select c from ChallengeRecord c Where c.challengeRecordStatus = true and c.challenge = :challenge")
    List<ChallengeRecord> findAllByChallenge(Challenge challenge);

    @Query("select c from ChallengeRecord c Where c.challengeRecordStatus = true and c.challenge.challengeProgress = 1")
    List<ChallengeRecord> findAllStatusTrueAndProgressNotStartedYet();

    void deleteAllByChallenge(Challenge challenge);

    @Query("select c from ChallengeRecord c Where c.challengeRecordStatus = true and c.member = :member")
    List<ChallengeRecord> findAllByMember(Member member);

    @Query("select CASE WHEN count(c)>0 then true else false end " +
            "from ChallengeRecord c " +
            "Where c.challengeRecordStatus = true and c.member = :member and c.challenge = :challenge")
    boolean existsByChallengeAndMember(Challenge challenge, Member member);

    @Query("select count(c) from ChallengeRecord c Where c.challengeRecordStatus = true and c.challenge = :challenge")
    int countByChallenge(Challenge challenge);

    // 챌린지에 참여한인원원
   @Query("select count(c) from ChallengeRecord c Where c.challenge = :challenge")
    int challengecount(Challenge challenge);



   // 진행중인첼린지
   @Query("select c from ChallengeRecord c Where c.challengeRecordStatus = true and c.member = :member and c.challenge.challengeProgress = :progress")
   List<ChallengeRecord> findAllByMemberAndProgress(Member member, Long progress);

}
