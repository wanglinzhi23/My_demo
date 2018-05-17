package intellif.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.dto.JsonObject;
import intellif.service.TableDivideServiceItf;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.TableRecord;

@RestController
@RequestMapping(GlobalConsts.R_ID_TABLE)
public class TableController {
	
	@Autowired
	private TableRecordDao tableRecordDao;
	
	@Autowired
	private TableDivideServiceItf iTableDivideServiceItf;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@RequestMapping(value = "/code/{value}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "response a string describing if the table code is successfully get or not")
	public JsonObject findByTableCode(@PathVariable("value")String value){
		return new JsonObject(tableRecordDao.findAllByTableCode(value));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "response a string describing if the current time is successfully get or not")
	public JsonObject findByCurrentTime(){
		Date date = Calendar.getInstance().getTime();
		return new JsonObject(tableRecordDao.findAllByTime(date));
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT",value = "response a string descrbing if the id is successfully get or not")
	public JsonObject update(@PathVariable("id")long id, @RequestBody @Valid TableRecord tableRecord){
		tableRecord.setId(id);
		return new JsonObject(tableRecordDao.save(tableRecord));
	}
	
	@RequestMapping(value = "/{date}", method = RequestMethod.DELETE)
	@ApiOperation(httpMethod = "DELETE",value = "response a string descrbing if the date is successfully delete or not")
	public JsonObject deleteTables(@PathVariable("date")String dateStr){
		try{
			SimpleDateFormat dateFormatHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date  date = dateFormatHMS.parse(dateStr);
			iTableDivideServiceItf.dropTables(date);
		  	return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
		}catch(Exception e){
//			LOG.error("delete face and image tables error",e);
			return new JsonObject("delete face and image tables error", 1001);
		}
	}
	
}