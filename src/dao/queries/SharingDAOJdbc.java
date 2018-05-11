package dao.queries;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.DAO;
import dao.mapping.SharingRowMapper;
import domain.models.Sharing;

public class SharingDAOJdbc {

	private DAO dao;

	public SharingDAOJdbc(DAO dao) {
		this.dao = dao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return dao.getJdbcTemplate();
	}

	public void saveOneSharing(Sharing sharing) {
		try{			
			if(sharing.getId() == 0){
				getJdbcTemplate().update("INSERT INTO MTN_DATA_MONTH_SHARING_EBA (MSISDN,VALUE) VALUES('" + sharing.getMsisdn() + "'," + sharing.getValue() + ")");			
			}
			else if(sharing.getId() > 0){
				getJdbcTemplate().update("UPDATE MTN_DATA_MONTH_SHARING_EBA SET VALUE = VALUE + " + sharing.getValue() + " WHERE ((ID = " + sharing.getId() + ") AND (MSISDN = '" + sharing.getMsisdn() + "'))");
			}

		} catch(EmptyResultDataAccessException emptyEx) {

		} catch(Throwable th) {

		}
	}

	public Sharing getOneSharing(int id) {
		List<Sharing> sharings = getJdbcTemplate().query("SELECT ID,MSISDN,VALUE FROM MTN_DATA_MONTH_SHARING_EBA WHERE ID = " + id, new SharingRowMapper());
		return sharings.isEmpty() ? null : sharings.get(0);
	}
	
	public Sharing getOneSharing(String msisdn)  {
		List<Sharing> sharings = getJdbcTemplate().query("SELECT ID,MSISDN,VALUE FROM MTN_DATA_MONTH_SHARING_EBA WHERE MSISDN = '" + msisdn + "'", new SharingRowMapper());
		return sharings.isEmpty() ? null : sharings.get(0);
	}
	
	public void deleteOneSharing(int id) {
		getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_SHARING_EBA WHERE ID = " + id);
	}
	
	public void deleteOneSharing(String msisdn) {
		getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_SHARING_EBA WHERE MSISDN = '" + msisdn + "'");
	}

}
