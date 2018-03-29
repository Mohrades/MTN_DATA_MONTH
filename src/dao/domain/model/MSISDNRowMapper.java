package dao.domain.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MSISDNRowMapper implements RowMapper<MSISDN>{

	@Override
	public MSISDN mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new MSISDN(rs.getInt("ID"), rs.getString("MSISDN"));

	}

}
