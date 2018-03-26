package cn.appinfodb.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;

import cn.appinfodb.pojo.AppCategory;
import cn.appinfodb.pojo.AppInfo;
import cn.appinfodb.pojo.AppVersion;
import cn.appinfodb.service.AppCategoryService;
import cn.appinfodb.service.AppInfoService;
import cn.appinfodb.service.AppVersionService;
import cn.appinfodb.service.developer.DeveloperService;

@Controller
public class DeveloperController {
	private DeveloperService developerService;
	@Autowired
	private AppCategoryService appCategoryService;
	@Autowired
	private AppInfoService appInfoService;
	@Autowired
	private AppVersionService appVersionService;
	
	@Resource(name="developerService")
//	@Autowired
	public void setDeveloperService(DeveloperService developerService) {
		this.developerService = developerService;
	}
	
	//娴嬭瘯鐢�
	@RequestMapping("/appList")
	public String appList(Model model) {
		List<AppCategory> appLevel1 = developerService.getCategoryByParentId(null);
		model.addAttribute("appLevel1", appLevel1);
		return "developer/appList";
	}
	
	//ajax鑾峰彇category
	@RequestMapping(value="/getAppList",produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getAppList1(Long parentId) {
		System.out.println("=================="+parentId+"====================");
		List<AppCategory> appList2 = appCategoryService.getAppByParentId(parentId);
		for(AppCategory app:appList2) {
			System.out.println(app.getCategoryname()+app.getId());
		}
		String json = JSONArray.toJSONString(appList2);
		System.out.println(json);
		return json;
	}
	
	//app鏌ヨ
	@RequestMapping("/appSearch")
	public String appSearch(Model model,
			@RequestParam(value="appName",required=false)String appName,
			@RequestParam(value="category3",required=false)long level3,
			@RequestParam(value="flatform",required=false)long flatform) {
		List<AppCategory> appLevel1 = appCategoryService.getAppByParentId(null);
		List<AppInfo> appList = appInfoService.getAppByName(appName, level3,flatform);
		for(AppInfo app:appList) {
			model.addAttribute("level1", appCategoryService.getAppByLevel(app.getCategorylevel1()));
			model.addAttribute("level2", appCategoryService.getAppByLevel(app.getCategorylevel2()));
			model.addAttribute("level3", appCategoryService.getAppByLevel(app.getCategorylevel3()));
			if(app.getVersionid() == null) {
				model.addAttribute("version", "鏆傛棤鐗堟湰淇℃伅");
			} else {
				String appVersion = appVersionService.getAppVersionByVersionId(app.getVersionid());
				model.addAttribute("version", appVersion);
			}
		}
		model.addAttribute("appList", appList);
		model.addAttribute("appLevel1", appLevel1);
		return "developer/appList";
	}
	
	@RequestMapping("/addVersionPage")
	public String addVersionPage() {
		return "developer/addVersion";
	}
	
	@RequestMapping("/addVersion")
	public String addVersion() {
		return "developer/addVersion";
	}
	
	@RequestMapping(value="/addVersion", method=RequestMethod.POST)
	public String addAppPage(AppVersion appVersion,HttpSession session,Model model,
			@RequestParam(value="apk",required = false) MultipartFile apk) {
		System.out.println(appVersion.getVersionno());
		File file = null;
		int flag = -1;
		if(apk.isEmpty()) {
			return "developer/addVersion";
		}
		String fileName = apk.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(fileName);
		System.out.println(suffix);
		if(apk.getSize() >= 500000000) {
			model.addAttribute("imgError","apk文件不得大于500Mb");
			return "developer/addVersion";
		}else if(suffix.equalsIgnoreCase("apk") || suffix.equalsIgnoreCase("rar") || suffix.equalsIgnoreCase("zip")) {
			String path = session.getServletContext().getRealPath("statics"+File.separator+"apk");
			System.out.println("====path==========: "+path);
			
			file = new File(path, fileName);
			String apklocpath = path + File.separator+fileName;
			appVersion.setApklocpath(apklocpath);
			
		}else {
			model.addAttribute("imgError", "请上传正确文件");
			
			return "developer/addVersion";
		}
		
		appVersion.setCreationdate(new Date());
		flag = appVersionService.addAppVersion(appVersion);
		System.out.println("=======1========"+flag+"================");
		
		/*try {
			apk.transferTo(file);
		}  catch (IOException e) {
			flag = -1;
			e.printStackTrace();
		}*/
		
		if(flag > 0) {
			return "redirect:/appSearch";
		}else {
			return "forward:/addVersionPage";
		}
	}
	

	
	@RequestMapping("/virafyVersionNo")
	@ResponseBody
	public String virafyVersionNo(@RequestParam("appId")Long appId,@RequestParam("versionNo")String versionNo) {
		System.out.println("virafyApkName======================");
		System.out.println(versionNo);
		AppVersion appVersion = appVersionService.getAppVersionByVersionNo(versionNo, appId);
		if(appVersion == null) {
			return "true";
		}
		return "false";
	}
	
	/**
	 * 
	 * @return
	 * 
	 * 閻犲搫鐤囧ù鍝竏dApp濡炪倗鏁诲锟�
	 * 
	 */
	@RequestMapping(value="/addAppPage", method=RequestMethod.GET)
	public String addAppPage() {
		
		return "developer/addApp";
	}
	
	/**
	 * 闁兼儳鍢茶ぐ鍢篸dApp濡炪倗鏁诲浼存儍閸曨亙绻嗛柟顓у灲缁辨繈鐛捄鐑樺�婚柡浣哄瀹撲焦鎯旈幘鏉戞綉闁告梻濮虫穱濠囧箒閿燂拷
	 * @param appInfo
	 * @param session
	 * @param model
	 * @param image
	 * @return
	 */
	
	@RequestMapping(value="/addApp", method=RequestMethod.POST)
	public String addApp(AppInfo appInfo, HttpSession session, Model model, 
			@RequestParam(value="picture",required = false) MultipartFile image) {
		System.out.println("==========addApp ===========");
		System.out.println("appInfo"+appInfo.getFlatformid());
		File file = null;
		int flag = -1;
		if(image.isEmpty()) {
			return "developer/addApp";
		}
		String fileName = image.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(fileName);
		if(image.getSize() >= 500000) {
			model.addAttribute("imgError","闁哄倸娲ｅ▎銏″緞瑜嶉惃顒佺▔瀹ュ牆鍘撮悺鎺戞嚀缁伙拷500k闁挎冻鎷�");
			return "developer/addApp";
		}else if(suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png") 
        		|| suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("pneg")) {
			String path = session.getServletContext().getRealPath("statics"+File.separator+"img");
			System.out.println("====path==========: "+path);
			
			file = new File(path, fileName);
			String logoPicPath = path + File.separator+fileName;
			appInfo.setLogopicpath(logoPicPath);
			
		}else {
			model.addAttribute("imgError", "闁哄倸娲ｅ▎銏ゅ冀閻撳海纭�濞戞挸绉甸婊呮兜椤曞棛纾奸柨娑楃哎~");
			
			return "developer/addApp";
		}
		
		appInfo.setCreationdate(new Date());
		flag = developerService.addApp(appInfo);
		
		try {
			image.transferTo(file);
		}  catch (IOException e) {
			flag = -1;
			e.printStackTrace();
		}
		
		if(flag > 0) {
			return "redirect:/appSearch";
		}else {
			return "forward:/addAppPage";
		}
	}
	/**
	 * addApp濡炪倗鏁诲鏉壳庣拠鎻掝潱APP濞ｅ洠鍓濇导鍛村籍鐠佸湱绀夊ù锝堟硶閺侇棫jax闁兼儳鍢茶ぐ鍥ㄧ▔閿熺晫鐥閿熸垝妞掔花鈺冪棯瑜嬮敓鎴掓缁椾胶鐥閸ㄥ海鐚炬导娆戠闁烩偓鍔嬬花顒勬焻婢跺顏ラ柛鎺戞鐞氼偆鐥閸╋拷
	 * @param id
	 * @return
	 */
	@RequestMapping("/categoryLevel")
	@ResponseBody
	public Object getCategoryLevel(String id) {
		Long parentId = null;
		if(id != null) {
			parentId = Long.parseLong(id);
		}else {
			
		}
		
		List<AppCategory> AppCategorys = developerService.getCategoryByParentId(parentId);
		return AppCategorys;
	}
	
	@RequestMapping("/virafyApkName")
	@ResponseBody
	public String virafyApkName(@RequestParam("apkname")String apkname) {
		System.out.println("virafyApkName======================");
		System.out.println(apkname);
		AppInfo appInfo = developerService.getAppInfoByAPKName(apkname);
		if(appInfo == null) {
			return "true";
		}
		return "false";
	}
}
