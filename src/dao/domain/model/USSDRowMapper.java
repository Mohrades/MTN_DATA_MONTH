package dao.domain.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class USSDRowMapper implements RowMapper<USSD>{

	@Override
	public USSD mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub		

		return  new USSD(rs.getInt("ID"), rs.getLong("SESSIONID"), rs.getString("MSISDN"), rs.getString("INPUT"), rs.getInt("STEP"), rs.getTimestamp("LAST_UPDATE_TIME"));
	}

}
