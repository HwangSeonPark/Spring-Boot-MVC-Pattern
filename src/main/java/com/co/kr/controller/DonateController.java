package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.DonateFileDomain;
import com.co.kr.domain.DonateListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.DonateService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DonateController {

	@Autowired
	private DonateService donateService;

	@PostMapping(value = "dnupload")
	public ModelAndView dnUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq)
			throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		int dnSeq = donateService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화

	
		mav = dnSelectOneCall(fileListVO, String.valueOf(dnSeq), request);
		mav.setViewName("donate/donateList.html");
		return mav;

	}

	public ModelAndView dnSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String dnSeq,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("dnSeq", Integer.parseInt(dnSeq));
		DonateListDomain donateListDomain = donateService.donateSelectOne(map);
		System.out.println("donateListDomain" + donateListDomain);
		List<DonateFileDomain> fileList = donateService.donateSelectOneFile(map);

		for (DonateFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", donateListDomain);
		mav.addObject("files", fileList);

		// 삭제시 사용할 용도
		session.setAttribute("files", fileList);

		return mav;

	}

	@GetMapping("dndetail")
	public ModelAndView dnDetail(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("dnSeq") String dnSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		// 하나파일 가져오기
		mav = dnSelectOneCall(fileListVO, dnSeq, request);
		mav.setViewName("donate/donateList.html");
		return mav;
	}

	@GetMapping("dnedit")
	public ModelAndView dnedit(FileListVO fileListVO, @RequestParam("dnSeq") String dnSeq, HttpServletRequest request)
			throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("dnSeq", Integer.parseInt(dnSeq));
		DonateListDomain donateListDomain = donateService.donateSelectOne(map);
		List<DonateFileDomain> fileList = donateService.donateSelectOneFile(map);

		for (DonateFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(donateListDomain.getDnSeq());
		fileListVO.setContent(donateListDomain.getDnContent());
		fileListVO.setTitle(donateListDomain.getDnTitle());
		fileListVO.setLoc(donateListDomain.getDnLoc());
		fileListVO.setIsEdit("edit"); // upload 재활용하기위해서
		
		mav.addObject("detail", donateListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen", fileList.size());
		mav.setViewName("donate/donateEditList.html");
		return mav;
	}

	@PostMapping("dneditSave")
	public ModelAndView editSave(@ModelAttribute("fileListVO") FileListVO fileListVO,
			MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();

		// 저장
		donateService.fileProcess(fileListVO, request, httpReq);

		mav = dnSelectOneCall(fileListVO, fileListVO.getSeq(), request);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		mav.setViewName("donate/donateList.html");
		return mav;
	}

	@GetMapping("dnremove")
	public ModelAndView dnRemove(@RequestParam("dnSeq") String dnSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<DonateFileDomain> fileList = null;
		if (session.getAttribute("files") != null) {
			fileList = (List<DonateFileDomain>) session.getAttribute("files");
		}

		map.put("dnSeq", Integer.parseInt(dnSeq));

		// 내용삭제
		donateService.dnContentRemove(map);

		for (DonateFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());

			try {

				// 파일 물리삭제
				Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
				// db 삭제
				donateService.dnFileRemove(list);

			} catch (DirectoryNotEmptyException e) {
				throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 세션해제
		session.removeAttribute("files"); // 삭제
		 
		mav = dnListCall();
		mav.setViewName("donate/donateList.html");

		return mav;
	}

	// 리스트 가져오기 따로 함수뺌
	public ModelAndView dnListCall() {
		ModelAndView mav = new ModelAndView();
		List<DonateListDomain> items = donateService.donateList();
		mav.addObject("items", items);
		return mav;
	}
	   @RequestMapping(value="searchContent")
	   public ModelAndView searchContent(HttpServletRequest request) {
	      ModelAndView mav = new ModelAndView();
	      HashMap<String, String> map = new HashMap<String, String>();
	      map.put("dnTitle",request.getParameter("searchtitle"));
	      System.out.println("searchtitle : "+request.getParameter("searchtitle"));
	      List<DonateListDomain> items = donateService.dnsearchTitle(map);
	      System.out.println("items ==> " + items);
	      mav.addObject("items", items);
	      mav.setViewName("donate/donateList.html");
	      return mav;
	   }
	

}