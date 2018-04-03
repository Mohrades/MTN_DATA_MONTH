package filter;

import java.util.List;

import connexions.AIRRequest;
import dao.DAO;
import dao.JdbcOperations.MSISDNDAOJdbc;
import env.WebAppProperties;
import util.AccountDetails;

public class MSISDNValidator {

	public MSISDNValidator() {

	}

	public boolean isFiltered(DAO dao, WebAppProperties webAppProperties, String msisdn, String type) {
		if(type.equals("A")) {
			if(onNet(webAppProperties, msisdn)) {
				return validate(dao, webAppProperties.getAnumber_serviceClass_include_filter(), webAppProperties.getAnumber_db_include_filter(), webAppProperties.getAnumber_serviceClass_exclude_filter(), webAppProperties.getAnumber_db_exclude_filter(), msisdn);
			}
		}
		else if(type.equals("B")) {
			if(onNet(webAppProperties, msisdn)) {
				return validate(dao, webAppProperties.getBnumber_serviceClass_include_filter(), webAppProperties.getBnumber_db_include_filter(), webAppProperties.getBnumber_serviceClass_exclude_filter(), webAppProperties.getBnumber_db_exclude_filter(), msisdn);
			}
		}

		return false;
	}

	private boolean validate(DAO dao, List<String> number_serviceClass_include_filter, List<String> number_db_include_filter, List<String> number_serviceClass_exclude_filter, List<String> number_db_exclude_filter, String msisdn) {
		// include
		boolean included = false;
		// exclude
		boolean excluded = true;

		try {
			// include
			if((number_serviceClass_include_filter == null) && (number_db_include_filter == null)) {
				included = true;
			}
			else {
				if(number_serviceClass_include_filter != null) {
					if(isServiceClassFiltered(number_serviceClass_include_filter, msisdn)) {
						included = true;
					}
					else if(number_db_include_filter != null) {
						included = isDataTableFiltered(dao, number_db_include_filter, msisdn);
					}
				}
				else if(number_db_include_filter != null) {
					included = isDataTableFiltered(dao, number_db_include_filter, msisdn);
				}
			}

			// exclude
			if((number_serviceClass_exclude_filter == null) && (number_db_exclude_filter == null)) {
				excluded = false;
			}
			else {
				if(number_serviceClass_exclude_filter != null) {
					if(isServiceClassFiltered(number_serviceClass_exclude_filter, msisdn)) {
						excluded = true;
					}
					else if(number_db_exclude_filter != null) {
						excluded = isDataTableFiltered(dao, number_db_exclude_filter, msisdn);
					}
				}
				else if(number_db_exclude_filter != null) {
					excluded = isDataTableFiltered(dao, number_db_exclude_filter, msisdn);
				}
			}

		} catch(Throwable th) {
			included = false;
			excluded = true;
		}

		return included && (!excluded);
	}

	private boolean isServiceClassFiltered(List<String> number_serviceClass_filter, String msisdn) {
		try {
			AccountDetails accountDetails = new AIRRequest().getAccountDetails(msisdn);
			
			if(number_serviceClass_filter.contains(accountDetails.getServiceClassCurrent() + "")) {
				return true;
			}

		} catch(NullPointerException ex) {

		} catch(Exception ex) {

		} catch(Throwable th) {

		}

		return false;
	}

	private boolean isDataTableFiltered(DAO dao, List<String> number_db_filter, String msisdn) {
		try {
			for(String tableName : number_db_filter) {
				try {
					if(new MSISDNDAOJdbc(dao).getOneMSISDN(msisdn, tableName) != null) {
						return true;
					}

				} catch(NullPointerException ex) {

				} catch(Exception ex) {

				} catch(Throwable ex) {

				}
			}

		} catch(Throwable th) {

		}

		return false;
	}

	public boolean onNet(WebAppProperties webAppProperties, String msisdn) {
		String country_code = webAppProperties.getMcc() + "";

		if((country_code.length() + webAppProperties.getMsisdn_length()) == (msisdn.length())) {
			for(String prefix : webAppProperties.getMnc()) {
				if(msisdn.startsWith(country_code+prefix)) {
					return true;
				}
			}
		}

		return false;
	}

}
