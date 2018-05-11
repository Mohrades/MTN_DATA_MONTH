package dao.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import domain.models.Sharing;

public class SharingRowMapper implements RowMapper<Sharing>{

	@Override
	public Sharing mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub

		return new Sharing(rs.getInt("ID"), rs.getString("MSISDN"), rs.getLong("VALUE"));
	}

}
