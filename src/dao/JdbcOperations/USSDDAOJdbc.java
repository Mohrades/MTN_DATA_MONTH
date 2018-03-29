package dao.JdbcOperations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.domain.model.USSD;
import dao.domain.model.USSDRowMapper;

public class USSDDAOJdbc {

	private DAO dao;

	public USSDDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneUSSD(USSD ussd) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(ussd.getId() == 0) {
				getJdbcTemplate().update("INSERT INTO MTN_DATA_MONTH_USSD_EBA (SESSIONID,MSISDN,INPUT,STEP,LAST_UPDATE_TIME) VALUES(" + ussd.getSessionId() + ",'" + ussd.getMsisdn() + "','" + ussd.getInput() + "'," + ussd.getStep() + ",TIMESTAMP '" + dateFormat.format(new Date()) + "')");			
			}
			else if(ussd.getId() > 0){
				getJdbcTemplate().update("UPDATE MTN_DATA_MONTH_USSD_EBA SET STEP = " + ussd.getStep() + ", INPUT = '" + ussd.getInput() + "', LAST_UPDATE_TIME = TIMESTAMP '" + dateFormat.format(new Date()) + "' WHERE ((ID = " + ussd.getId() + ") AND (SESSIONID = " + ussd.getSessionId() + ") AND (MSISDN = '" + ussd.getMsisdn() + "'))");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}
	}

	public USSD getOneUSSD(int id) {
		List<USSD> ussds = getJdbcTemplate().query("SELECT ID,SESSIONID,MSISDN,STEP,INPUT,LAST_UPDATE_TIME FROM MTN_DATA_MONTH_USSD_EBA WHERE ID = " + id, new USSDRowMapper());
		return ussds.isEmpty() ? null : ussds.get(0);
	}

	public USSD getOneUSSD(long sessionId, String msisdn)  {
		List<USSD> ussds = getJdbcTemplate().query("SELECT ID,SESSIONID,MSISDN,STEP,INPUT,LAST_UPDATE_TIME FROM MTN_DATA_MONTH_USSD_EBA WHERE ((SESSIONID = " + sessionId + ") AND (MSISDN = '" + msisdn + "'))", new USSDRowMapper());
		return ussds.isEmpty() ? null : ussds.get(0);
	}

	public void deleteOneUSSD(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_USSD_EBA WHERE ID = " + id);
	}

	public void deleteOneUSSD(long sessionId) {
		getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_USSD_EBA WHERE SESSIONID = " + sessionId + "");
	}

}
