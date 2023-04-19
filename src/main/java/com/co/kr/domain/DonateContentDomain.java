package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class DonateContentDomain {

	private Integer dnSeq;
	private String dnLoc;
	private String mbId;
	private String dnTitle;
	private String dnContent;

}