package com.hangout.hangout.domain.report.entity;

import com.hangout.hangout.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "REPORT_REASON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportReason extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_REASON_ID")
    private Long id;

    @Column(name = "REPORT_TYPE")
    private String type;
}