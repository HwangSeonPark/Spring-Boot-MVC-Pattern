package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.DonateContentDomain;
import com.co.kr.domain.DonateFileDomain;
import com.co.kr.domain.DonateListDomain;

@Mapper
public interface DonateMapper {

	//list
	public List<DonateListDomain> donateList();
	//content upload
	public void donatecontentUpload(DonateContentDomain donateContentDomain);
	//file upload
	public void donatefileUpload(DonateFileDomain donateFileDomain);

	//content update
	public void dnContentUpdate(DonateContentDomain donateContentDomain);
	//file updata
	public void dnFileUpdate(DonateFileDomain donateFileDomain);

    //content delete 
	public void dnContentRemove(HashMap<String, Object> map);
	//file delete 
	public void dnFileRemove(DonateFileDomain donateFileDomain);
	
	//select one
	public DonateListDomain donateSelectOne(HashMap<String, Object> map);

	//select one file
	public List<DonateFileDomain> donateSelectOneFile(HashMap<String, Object> map);
}