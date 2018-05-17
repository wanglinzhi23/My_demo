package intellif.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.database.entity.BlackDetail;
import intellif.dto.BankMatchDto;
import intellif.dto.FaceResultPKDto;
import intellif.dto.JsonObject;
import intellif.dto.ProcessInfo;
import intellif.dto.ZipPathInfo;
import intellif.service.ImageServiceItf;
import intellif.service.SolrServerItf;
import intellif.utils.CommonUtil;
import intellif.utils.FileUtil;
import intellif.database.entity.BankMatchIdTuple;
import intellif.database.entity.BankMatchRequest;
import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.SearchExcelCell;

/**
 * 
 * @author yktangint
 * 双库碰撞功能
 *
 */

@RestController
@RequestMapping(GlobalConsts.R_ID_BANK_COLLISION)
public class BankCollisionController {
	
	private static Logger LOG = LogManager.getLogger(BankCollisionController.class);
	/*
	 * cachedMatchBankResults: (key, value) -> (targetBankId, resultBankMap)
	 *     resultBankMap: (key, value) -> (staticBankId, matchResultMap)
	 *         matchResultMap: (key, value) -> (row id in target bank, a list of matched photo tuples)
	 */
	/**
	 * 用static缓存双库碰撞的结果 
	 */
	public static ConcurrentHashMap<String, ConcurrentHashMap<Integer, ConcurrentSkipListMap<Long, BankMatchResultTuple>>> cachedMatchBankResults = new ConcurrentHashMap<>();

	//	public static Hashtable<String, ConcurrentSkipListMap<Long, BankMatchResultTuple>> cachedMatchBank = new Hashtable<String, ConcurrentSkipListMap<Long, BankMatchResultTuple>>();
	/**
	 * 双库碰撞的结果的key用队列来存储，该队列只保存5个数据，即双库碰撞的缓存结果也只保留5个。重排序、出入队的实现方法详见双库碰撞接口函数。
	 */
	public static Queue<String> cacheBankQueue = new ConcurrentLinkedQueue<>();
	public static Map<Integer, BankMatchIdTuple> keyToBankIdMap = new ConcurrentHashMap<>();
	
	@Autowired
	private SolrServerItf _solrService;  
	@Autowired
	private PropertiesBean propertiesBean;
	@Autowired
	private BlackDetailDao blackDetailDao;
	@Autowired
	private ImageServiceItf _imageServiceItf;

	@RequestMapping(value = "/progress/{key}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "双库碰撞读取进度")
	public JsonObject matchbankPrograss(@PathVariable("key") long key) {
		return new JsonObject(GlobalConsts.bankMatchMap.get(key));
	}
	
	@RequestMapping(value = "/matchbank/result/{key}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "返回双库碰撞结果")
	public JsonObject createdSelectedPhotoZipLink(@PathVariable("key") int key, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
		if (keyToBankIdMap.get(key) == null) return null;
		
		BankMatchDto resp = new BankMatchDto();
		Map<Long, BankMatchResultTuple> respdata = null;
		BankMatchIdTuple bankidtuple = keyToBankIdMap.get(key);
		ConcurrentSkipListMap<Long, BankMatchResultTuple> cachedMap = cachedMatchBankResults.get(String.valueOf(bankidtuple.getTargetbankid())).get(bankidtuple.getStaticbankid());
		NavigableSet<Long> navigableSet = cachedMap.keySet();
		Iterator<Long> itr = navigableSet.iterator();
		List<Long> keylist = IteratorUtils.toList(itr);
		int length = keylist.size();
		int totalPage = length % pageSize == 0 ? length / pageSize : length / pageSize + 1;
		if ((page - 1) * pageSize < length) {
			if (page * pageSize < length) {
				respdata = cachedMap.subMap(keylist.get((page - 1) * pageSize), keylist.get(page * pageSize));
			} else {
				respdata = cachedMap.subMap(keylist.get((page - 1) * pageSize), true, keylist.get(length - 1), true);
			}
		}
		resp.setTotalpage(totalPage);
		resp.setResult(respdata);
		return new JsonObject(resp);
	}
	
	@RequestMapping(value = "/matchbank", method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "双库碰撞")
	public JsonObject matchFaceInBankWithTargetWarehouse(@RequestBody @Valid BankMatchRequest request) {
		final long targettype = request.getTargetType();
		final int staticbankid = request.getStaticBankId();
		int matchnum = request.getMatchnum();
		Random random = new Random();
		int randkey = random.nextInt(Integer.MAX_VALUE) + 1;
		while (keyToBankIdMap.get(randkey) != null) {
			randkey = random.nextInt(Integer.MAX_VALUE) + 1;
		}
		final int key = randkey;
		Runnable task = () -> {
			try {
				ConcurrentHashMap<Integer, ConcurrentSkipListMap<Long, BankMatchResultTuple>> cachedMatchBank = cachedMatchBankResults.get(String.valueOf(targettype));
				if (cachedMatchBank == null) {
					cachedMatchBank = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<Long, BankMatchResultTuple>>(); 
					cachedMatchBankResults.put(String.valueOf(targettype),cachedMatchBank);
				}
				ConcurrentSkipListMap<Long, BankMatchResultTuple> cachedOldMap = cachedMatchBank.get(staticbankid);
				if (MapUtils.isEmpty(cachedOldMap)) {
					cachedMatchBank.put(staticbankid, new ConcurrentSkipListMap<Long, BankMatchResultTuple>());
					_solrService.searchFaceByDatasetInBank(targettype, staticbankid, key, matchnum);
				} else {
					//如果有缓存，则查询目标库是否有新增或删除
					_solrService.searchFaceByNewRecordsInBank(cachedOldMap.lastKey(), targettype, staticbankid, key, matchnum);
				}
				
				if (cacheBankQueue.contains(String.valueOf(targettype))) {
					Iterator<String> itemitr = cacheBankQueue.iterator();
					while (itemitr.hasNext()) {
						if (String.valueOf(targettype).equals(itemitr.next())) {
							cacheBankQueue.remove(String.valueOf(targettype));
							break;
						}
					}
				} else if (cacheBankQueue.size() >= 5) {
					keyToBankIdMap.entrySet().removeIf(entry -> cacheBankQueue.peek().equals(entry.getValue().getTargetbankid()));
					cachedMatchBank.remove(cacheBankQueue.poll());
				}
				cacheBankQueue.add(String.valueOf(targettype));
			} catch (Exception e) {
				e.printStackTrace();
			}
			keyToBankIdMap.put(key, new BankMatchIdTuple(targettype, staticbankid));
		};
		new Thread(task).start();
		return new JsonObject(key);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pkzip/download/progress/{key}")
	@ApiOperation(httpMethod = "GET", value = "pkzip导出显示进度")
	public JsonObject handleProcessZipDownload(@PathVariable("key") int key) {
		return new JsonObject(GlobalConsts.downloadPkFaceMap.get(key));
	}

	@RequestMapping(value = "/pkzip/targetbankid/{targetbankid}/basebankid/{basebankid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "zip导出pk搜索到的图片")
	public JsonObject createZipResultsByCamera(
			//@PathVariable("bankid") String bankid, @PathVariable("key") int key) {
			@PathVariable("targetbankid") Long targetbankid, @PathVariable("basebankid") Integer basebankid) {

		// ProcessInfo process = new ProcessInfo();
		ProcessInfo newzipprocess = new ProcessInfo();
		newzipprocess.setTotalSize(-1);
		newzipprocess.setSuccessNum(0);
		newzipprocess.setFailedNum(0);
		Random random = new Random();
	    int key = random.nextInt(Integer.MAX_VALUE) + 1; //key修改为后端产生 提供给前端了
	    while(GlobalConsts.downloadPkFaceMap.get(key)!=null){
	    	 key = random.nextInt(Integer.MAX_VALUE) + 1;   //保证生成key的唯一性
	    }
		GlobalConsts.downloadPkFaceMap.put(key, newzipprocess);

		ArrayList<SearchExcelCell> excelList = new ArrayList<SearchExcelCell>();

		List<String> TargetList = new ArrayList<String>(); // 目标库中的100张人脸

		List<String> TestList = new ArrayList<String>(); // 基础库中的1000张人脸

		List<Integer> searchSize = new ArrayList<Integer>(); // 目标库对应的搜索结果
		// 以防没有搜到10张的情况

		//另起一个线程执行导出
		final int randomkey = key;
		Runnable task = () -> {
			setList(targetbankid, basebankid, newzipprocess, TestList, TargetList, searchSize);
			System.out.println("@@@@@@@@@@testlist的大小" + TestList.size());
			System.out.println("@@@@@@@@@@targetlist的大小" + TargetList.size());
	
			try {
	
				Map<Integer, List<String>> cameraMap = new HashMap<Integer, List<String>>();
				String userName = SecurityContextHolder.getContext()
						.getAuthentication().getName();
				int rand = CommonUtil.getRandomNumber(2);
				String randPath = userName + "_"
						+ Calendar.getInstance().getTime().getTime() + "_" + rand;
	
				for (int i = 0, j = 0; j < TestList.size(); i = i + 1) {
	
					List<String> flist = new ArrayList<String>();
	
					flist.addAll(TestList.subList(j, j + searchSize.get(i)));
					j = j + searchSize.get(i);
	
					cameraMap.put(i, flist);
	
				}
	
				for (int i = 0, k = 0; k < TestList.size(); i = i + 1) {
	
					List<String> faceResultList = cameraMap.get(new Integer(i));
	
					List<String> blackResultList = new ArrayList<String>(); // blacklist
																			// 其实也就一张目标人脸
																			// 地址
					blackResultList.add(TargetList.get(i));
					ArrayList<String> blacknam = new ArrayList<String>();
					blacknam.add(getFileName(blackResultList.get(0)));
					
					int num = i + 1;
	
					saveImageToDir(blackResultList, randPath, blacknam, "/" + num
							+ "/blackFace", newzipprocess); // 存储那一张目标图片
	
					ArrayList<String> nam = new ArrayList<String>();
					for (int j = 0; j < faceResultList.size(); j++) {
						nam.add(getFileName(faceResultList.get(j)));
					}
	
					SearchExcelCell c = saveImageToDir(faceResultList, randPath,
							nam, "/" + num + "/searchFace", newzipprocess); // 存储10张搜索结果图
					c.setTargetFaceName(blacknam.get(0));
					excelList.add(c);
					k = k + searchSize.get(i);
	
				}
	
				// 兼容 静态库为空 搜索结果为空的情况
				if (TestList.size() == 0 && TargetList.size() != 0) {
					for (int i = 0; i < TargetList.size(); i++) {
						List<String> blackResultList = new ArrayList<String>();
						blackResultList.add(TargetList.get(i));
						ArrayList<String> blacknam = new ArrayList<String>();
						blacknam.add(getFileName(blackResultList.get(0)));
						int num = i + 1;
	
						saveImageToDir(blackResultList, randPath, blacknam, "/"
								+ num + "/blackFace", newzipprocess);
						SearchExcelCell c = new SearchExcelCell();
						c.setTargetFaceName(blacknam.get(0));
						excelList.add(c);
					}
	
				}
	
				exportExcel(excelList, randPath);
				compressZip(randPath);
				String path = FileUtil.getZipHttpUrl(propertiesBean.getIsJar())
						+ "export/zip/" + randPath + "/pkfaceData.zip";
				//return new JsonObject(path);
				GlobalConsts.downloadPkResultMap.put(randomkey, new ZipPathInfo(path, System.currentTimeMillis()));
			} catch (Exception e) {
				LOG.error("export face error", e);
//				return new JsonObject(e.getMessage(), 1001);
			}
		};
		new Thread(task).start();
		return new JsonObject(key);
	}

	//导出结果地址
	 @RequestMapping(value = "/pkzip/result/{key}", method = RequestMethod.GET)
	    @ApiOperation(httpMethod = "GET", value = "返回导出双库碰撞pk结果的链接")
	    public JsonObject GetZipLink(@PathVariable("key") int key) {
	    	if (GlobalConsts.downloadPkResultMap.get(key) == null) return null;
	    	return new JsonObject(GlobalConsts.downloadPkResultMap.get(key).getZipFilePath());
	    }
	

	private void compressZip(String randPath) throws Exception {
		File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
				+ "export/zip/" + randPath + "/");
		FileUtil.deleteFile(file, true);
		FileUtil.checkFileExist(file);
		FileUtil.zipCompress(FileUtil.getZipUrl(propertiesBean.getIsJar())
		// + "export/image/" + randPath + "/搜索结果/",
				+ "export/image/" + randPath,
				FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/"
						+ randPath + "/pkfaceData.zip");
		File zipFile = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
				+ "export/zip/" + randPath + "/pkfaceData.zip");
		if (!zipFile.exists()) {
			throw new Exception("压缩失败");
		}
	}


	private SearchExcelCell saveImageToDir(List<String> faceList,
			String randPath, ArrayList<String> fileNameList, String appendName,
			ProcessInfo process) throws Exception {

		SearchExcelCell cell = new SearchExcelCell();

		File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
				+ "export/image/" + randPath + "/搜索结果/" + appendName + "/");
		FileUtil.checkFileExist(file);

		int j = 0;

		if (faceList.size() > 0) {
			cell.setBasicFaceOne(getFileName(faceList.get(0)));

		}
		if (faceList.size() > 1) {
			cell.setBasicFaceSec(getFileName(faceList.get(1)));
		}
		if (faceList.size() > 2) {
			cell.setBasicFaceThir(getFileName(faceList.get(2)));
		}
		if (faceList.size() > 3) {
			cell.setBasicFaceFour(getFileName(faceList.get(3)));
		}
		if (faceList.size() > 4) {
			cell.setBasicFaceFiv(getFileName(faceList.get(4)));
		}
		if (faceList.size() > 5) {
			cell.setBasicFaceSix(getFileName(faceList.get(5)));
		}
		if (faceList.size() > 6) {
			cell.setBasicFaceSev(getFileName(faceList.get(6)));
		}
		if (faceList.size() > 7) {
			cell.setBasicFaceEight(getFileName(faceList.get(7)));
		}
		if (faceList.size() > 8) {
			cell.setBasicFaceNine(getFileName(faceList.get(8)));
		}
		if (faceList.size() > 9) {
			cell.setBasicFaceTen(getFileName(faceList.get(9)));
		}

		for (int i = 0; i < faceList.size(); i++) {
            System.out.println("@@@@@"+faceList.size());
			String item = faceList.get(i);
			boolean state = false;
			try {
				String url = item;

				String fullFileName = FileUtil.getZipUrl(propertiesBean
						.getIsJar())
						+ "export/image/"
						+ randPath
						+ "/搜索结果/"
						+ appendName + "/" + fileNameList.get(i) + ".jpg";
			 
				boolean status = FileUtil.copyUrl(url, fullFileName);
				if(status){
					process.setSuccessNum(process.getSuccessNum() + 1);
				}else{
					process.setFailedNum(process.getFailedNum() + 1);
					FileUtil.deleteFile(new File(fullFileName), true);
				}
				
				j++;
			} catch (Exception e) {
				process.setFailedNum(process.getFailedNum() + 1);
				System.err.println("保存图片失败");
				continue;
			}
		}
		return cell;
	}
	


	public void exportExcel(ArrayList celllist, String randPath) {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("pk结果");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

		@SuppressWarnings("deprecation")
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("序号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("目标库图片编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("排名第1基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("排名第2基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("排名第3基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("排名第4基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("排名第5基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 7);
		cell.setCellValue("排名第6基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 8);
		cell.setCellValue("排名第7基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 9);
		cell.setCellValue("排名第8基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 10);
		cell.setCellValue("排名第9基础库编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 11);
		cell.setCellValue("排名第10基础库编号");
		cell.setCellStyle(style);

		for (int i = 0; i < celllist.size(); i++) {
			row = sheet.createRow((int) i + 1);
			SearchExcelCell cel = (SearchExcelCell) celllist.get(i);
			// 第四步，创建单元格，并设置值
			row.createCell((short) 0).setCellValue(i + 1);
			row.createCell((short) 1).setCellValue(cel.getTargetFaceName());
			row.createCell((short) 2).setCellValue(cel.getBasicFaceOne());
			row.createCell((short) 3).setCellValue(cel.getBasicFaceSec());
			row.createCell((short) 4).setCellValue(cel.getBasicFaceThir());
			row.createCell((short) 5).setCellValue(cel.getBasicFaceFour());
			row.createCell((short) 6).setCellValue(cel.getBasicFaceFiv());
			row.createCell((short) 7).setCellValue(cel.getBasicFaceSix());
			row.createCell((short) 8).setCellValue(cel.getBasicFaceSev());
			row.createCell((short) 9).setCellValue(cel.getBasicFaceEight());
			row.createCell((short) 10).setCellValue(cel.getBasicFaceNine());
			row.createCell((short) 11).setCellValue(cel.getBasicFaceTen());

		}

		// 自适应调整列宽
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		sheet.autoSizeColumn((short) 11);

		try {
			String FilePath = FileUtil.getZipUrl(propertiesBean.getIsJar())
					+ "export/image/" + randPath + "/搜索结果/face.xls";
			FileOutputStream fout = new FileOutputStream(FilePath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFileName(String filePath) {
		int formatLength = filePath.split("\\.").length;
		String format = filePath.split("\\.")[formatLength - 1];
		String f = filePath.split("." + format)[0];
		int length = f.split("/").length;
		String fileName = f.split("/")[length - 1];
		return fileName;
	}

	// 获取到搜索人脸的实际图片list数据
	public void setList(Long targetbankId, Integer basebankid, ProcessInfo process,
			List<String> testlist, List<String> targetlist,
			List<Integer> searchSize) {

		ConcurrentHashMap<Integer, ConcurrentSkipListMap<Long, BankMatchResultTuple>> table = cachedMatchBankResults.get(String.valueOf(targetbankId));
		ConcurrentSkipListMap<Long, BankMatchResultTuple> cachedMap = (ConcurrentSkipListMap<Long, BankMatchResultTuple>) table.get(basebankid);
		NavigableSet<Long> navigableSet = cachedMap.keySet();
	
		// 这一处循环是为了加速totalsize的获取
		Iterator<Long> itre = navigableSet.iterator();
		int g = 0;
		int testsize = 0;
		while (itre.hasNext()) {
			long blackid = itre.next();
			testsize = testsize + cachedMap.get(blackid).getResultList().size();
		}
		int totalSize = testsize + cachedMap.size();
		process.setTotalSize(totalSize);
		Iterator<Long> itr = navigableSet.iterator();
		int k=0;
		while (itr.hasNext()) {
			Long blackid = itr.next();
			BlackDetail bd = blackDetailDao.findById(blackid).get(0);
			long fromImageId = bd.getFromImageId();
			ImageInfo m = _imageServiceItf.findById(fromImageId);
			String targetImagePath = "";
			if (m != null) {
				targetImagePath = m.getUri();
			}
			targetlist.add(targetImagePath);
			List<FaceResultPKDto> faceResultDtoList = cachedMap.get(blackid).getResultList();
			// TestMap.put(g + "", faceResultDtoList);
			searchSize.add(faceResultDtoList.size());
			g = g + 1;
			for (int i = 0; i < faceResultDtoList.size(); i++) {
				testlist.add(faceResultDtoList.get(i).getFile());
			}
			/*
			 * int totalSize = TestList.size() + TargetList.size();
			 * zipprocess.setTotalSize(totalSize);
			 */// 这种获取方式有点慢
           k++;
		}
		// }
	}
	
}
