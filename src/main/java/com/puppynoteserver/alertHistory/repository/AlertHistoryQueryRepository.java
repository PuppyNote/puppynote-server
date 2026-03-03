package com.puppynoteserver.alertHistory.repository;

import com.puppynoteserver.alertHistory.entity.AlertDestinationType;
import com.puppynoteserver.alertHistory.entity.AlertHistory;
import com.puppynoteserver.alertHistory.entity.AlertHistoryStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.puppynoteserver.alertHistory.entity.QAlertHistory.*;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Repository
public class AlertHistoryQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Page<AlertHistory> findByUserId(Long userId, Pageable pageable) {
		List<AlertHistory> content = jpaQueryFactory
			.select(alertHistory)
			.from(alertHistory)
			.where(
				isUserIdEqualsTo(userId)
			)
			.orderBy(alertHistory.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalAlertHistoriesCount(userId);

		return new PageImpl<>(content, pageable, total);
	}

	public boolean hasUncheckedAlerts(Long userId) {
		long count = ofNullable(
			jpaQueryFactory
				.select(alertHistory.count())
				.from(alertHistory)
				.where(
					isUserIdEqualsTo(userId),
					isStatusEqualsTo(AlertHistoryStatus.UNCHECKED)
				)
				.fetchOne()
		).orElse(0L);
		return count > 0;
	}

	private long getTotalAlertHistoriesCount(Long userId) {
		return ofNullable(
			jpaQueryFactory
				.select(alertHistory.count())
				.from(alertHistory)
				.where(
					isUserIdEqualsTo(userId)
				)
				.fetchOne()
		).orElse(0L);
	}

	public boolean hasFriendCode(Long userId, String friendCode) {
		long count = ofNullable(
				jpaQueryFactory
						.select(alertHistory.count())
						.from(alertHistory)
						.where(
								isUserIdEqualsTo(userId),
								isAlertDestinationTypeEqualsTo(AlertDestinationType.FRIEND_CODE),
								isAlertDestinationInfoEqualsTo(friendCode)
						)
						.fetchOne()
		).orElse(0L);
		return count > 0;
	}

	private BooleanExpression isUserIdEqualsTo(Long userId) {
		return alertHistory.user.id.eq(userId);
	}

	private BooleanExpression isStatusEqualsTo(AlertHistoryStatus status) {
		return alertHistory.alertHistoryStatus.eq(status);
	}

	private BooleanExpression isAlertDestinationTypeEqualsTo(AlertDestinationType type) {
		return alertHistory.alertDestinationType.eq(type);
	}

	private BooleanExpression isAlertDestinationInfoEqualsTo(String code) {
		return alertHistory.alertDestinationInfo.eq(code);
	}
}
