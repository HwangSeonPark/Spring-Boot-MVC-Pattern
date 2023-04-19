package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class DonateListDomain {

	private String dnSeq;
	private String dnLoc;
	private String mbId;
	private String dnTitle;
	private String dnContent;
	private String dnCreateAt;
	private String dnUpdateAt;

}