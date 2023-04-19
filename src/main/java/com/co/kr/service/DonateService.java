package com.co.kr.service;


import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.DonateContentDomain;
import com.co.kr.domain.DonateFileDomain;
import com.co.kr.domain.DonateListDomain;
import com.co.kr.vo.FileListVO;

public interface DonateService {
	
	//list
	public List<DonateListDomain> donateList();
		
	// 인서트 및 업데이트
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 하나 삭제
	public void dnContentRemove(HashMap<String, Object> map);
	
	// 하나 삭제
	public void dnFileRemove(DonateFileDomain donateFileDomain);

	// 하나 리스트 조회
	public DonateListDomain donateSelectOne(HashMap<String, Object> map);
	
	// 하나 파일 리스트 조회
	public List<DonateFileDomain> donateSelectOneFile(HashMap<String, Object> map);
	
	public List<DonateListDomain> dnsearchTitle(HashMap<String, String> map);
}