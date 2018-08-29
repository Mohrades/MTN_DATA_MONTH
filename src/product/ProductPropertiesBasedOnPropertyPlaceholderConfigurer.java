package product;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;

@Component(value="productProperties")
public class ProductPropertiesBasedOnPropertyPlaceholderConfigurer implements ProductProperties {

	@Value("${gsm.mcc}")
	private short mcc;

	@Value("${gsm.name}")
	private String gsm_name;

	@Value("${gsm.short_code}")
	private short sc;

	@Value("${sms.notifications.header}")
	private String sms_notifications_header;

	private List<String> mnc;

	@Value("${msisdn.length}")
	private byte msisdn_length;

	@Value("${Anumber.da}")
	private int Anumber_da;

	@Value("${Bnumber.da}")
	private int Bnumber_da;

	@Value("${da.expires_in}")
	private String da_expires_in;

	@Value("${sharing.data.volume.limit}")
	private int sharing_data_volume_limit;

	private List<String> data_volume;

	private List<String> Anumber_serviceClass_include_filter;
	private List<String> Anumber_db_include_filter;
	private List<String> Anumber_serviceClass_exclude_filter;
	private List<String> Anumber_db_exclude_filter;

	private List<String> Bnumber_serviceClass_include_filter;
	private List<String> Bnumber_db_include_filter;
	private List<String> Bnumber_serviceClass_exclude_filter;
	private List<String> Bnumber_db_exclude_filter;

	private List<String> air_hosts;
	@Value("${air.io.sleep}")
	private int air_io_sleep;
	@Value("${air.io.timeout}")
	private int air_io_timeout;
	@Value("${air.io.threshold}")
	private int air_io_threshold;
	@Value("${air.test.connection.msisdn}")
	private String air_test_connection_msisdn;
	@Value("${air.preferred.host}")
	private byte air_preferred_host;

	@Value("${gsm.mnc}")
	public void setMnc(final String gsmmnc) {
		if(isSet(gsmmnc)) {
			mnc = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(gsmmnc);
		}
	 }

	@Value("${air.hosts}")
	public void setAir_hosts(final String air_hosts) {
		if(isSet(air_hosts)) {
			this.air_hosts = Splitter.onPattern("[;]").trimResults().omitEmptyStrings().splitToList(air_hosts);
		}
	}

	@Value("${data.volume}")
	public void setData_volume(final String data_volume) {
		if(isSet(data_volume)) {
			this.data_volume = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(data_volume);
		}
	}

	@Value("${Anumber.serviceClass.include_filter}")
	public void setAnumber_serviceClass_include_filter(final String anumber_serviceClass_include_filter) {
		if(isSet(anumber_serviceClass_include_filter)) {
			Anumber_serviceClass_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_serviceClass_include_filter);
		}
	}

	@Value("${Anumber.db.include_filter}")
	public void setAnumber_db_include_filter(final String anumber_db_include_filter) {
		if(isSet(anumber_db_include_filter)) {
			Anumber_db_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_db_include_filter);
		}
	}

	@Value("${Anumber.serviceClass.exclude_filter}")
	public void setAnumber_serviceClass_exclude_filter(final String anumber_serviceClass_exclude_filter) {
		if(isSet(anumber_serviceClass_exclude_filter)) {
			Anumber_serviceClass_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_serviceClass_exclude_filter);
		}
	}

	@Value("${Anumber.db.exclude_filter}")
	public void setAnumber_db_exclude_filter(final String anumber_db_exclude_filter) {
		if(isSet(anumber_db_exclude_filter)) {
			Anumber_db_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(anumber_db_exclude_filter);
		}
	}

	@Value("${Bnumber.serviceClass.include_filter}")
	public void setBnumber_serviceClass_include_filter(final String bnumber_serviceClass_include_filter) {
		if(isSet(bnumber_serviceClass_include_filter)) {
			Bnumber_serviceClass_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_serviceClass_include_filter);
		}
	}

	@Value("${Bnumber.db.include_filter}")
	public void setBnumber_db_include_filter(final String bnumber_db_include_filter) {
		if(isSet(bnumber_db_include_filter)) {
			Bnumber_db_include_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_db_include_filter);
		}
	}

	@Value("${Bnumber.serviceClass.exclude_filter}")
	public void setBnumber_serviceClass_exclude_filter(final String bnumber_serviceClass_exclude_filter) {
		if(isSet(bnumber_serviceClass_exclude_filter)) {
			Bnumber_serviceClass_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_serviceClass_exclude_filter);
		}
	}

	@Value("${Bnumber.db.exclude_filter}")
	public void setBnumber_db_exclude_filter(final String bnumber_db_exclude_filter) {
		if(isSet(bnumber_db_exclude_filter)) {
			Bnumber_db_exclude_filter = Splitter.onPattern("[,]").trimResults().omitEmptyStrings().splitToList(bnumber_db_exclude_filter);
		}
	}

	public short getMcc() {
		return mcc;
	}

	public String getGsm_name() {
		return gsm_name;
	}

	public short getSc() {
		return sc;
	}

	public String getSms_notifications_header() {
		return sms_notifications_header;
	}

	public List<String> getMnc() {
		return mnc;
	}

	public byte getMsisdn_length() {
		return msisdn_length;
	}

	public int getAnumber_da() {
		return Anumber_da;
	}

	public int getBnumber_da() {
		return Bnumber_da;
	}

	public String getDa_expires_in() {
		return da_expires_in;
	}

	public List<String> getData_volume() {
		return data_volume;
	}

	public int getSharing_data_volume_limit() {
		return sharing_data_volume_limit;
	}

	public List<String> getAnumber_serviceClass_include_filter() {
		return Anumber_serviceClass_include_filter;
	}

	public List<String> getAnumber_db_include_filter() {
		return Anumber_db_include_filter;
	}

	public List<String> getAnumber_serviceClass_exclude_filter() {
		return Anumber_serviceClass_exclude_filter;
	}

	public List<String> getAnumber_db_exclude_filter() {
		return Anumber_db_exclude_filter;
	}

	public List<String> getBnumber_serviceClass_include_filter() {
		return Bnumber_serviceClass_include_filter;
	}

	public List<String> getBnumber_db_include_filter() {
		return Bnumber_db_include_filter;
	}

	public List<String> getBnumber_serviceClass_exclude_filter() {
		return Bnumber_serviceClass_exclude_filter;
	}

	public List<String> getBnumber_db_exclude_filter() {
		return Bnumber_db_exclude_filter;
	}
	public List<String> getAir_hosts() {
		return air_hosts;
	}

	public int getAir_io_sleep() {
		return air_io_sleep;
	}

	public int getAir_io_timeout() {
		return air_io_timeout;
	}

	public int getAir_io_threshold() {
		return air_io_threshold;
	}

	public String getAir_test_connection_msisdn() {
		return air_test_connection_msisdn;
	}

	public byte getAir_preferred_host() {
		return air_preferred_host;
	}

	public void setAir_preferred_host(byte air_preferred_host) {
		this.air_preferred_host = air_preferred_host;
	}

	public boolean isSet(String property_value) {
		if((property_value == null) || (property_value.trim().length() == 0)) return false;
		else return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
