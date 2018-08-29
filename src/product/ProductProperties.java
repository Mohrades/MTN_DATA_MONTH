package product;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface ProductProperties extends InitializingBean, DisposableBean {

	public void setMnc(final String gsmmnc) ;

	public void setAir_hosts(final String air_hosts) ;

	public void setData_volume(final String data_volume) ;

	public void setAnumber_serviceClass_include_filter(final String anumber_serviceClass_include_filter) ;

	public void setAnumber_db_include_filter(final String anumber_db_include_filter) ;

	public void setAnumber_serviceClass_exclude_filter(final String anumber_serviceClass_exclude_filter) ;

	public void setAnumber_db_exclude_filter(final String anumber_db_exclude_filter) ;

	public void setBnumber_serviceClass_include_filter(final String bnumber_serviceClass_include_filter) ;

	public void setBnumber_db_include_filter(final String bnumber_db_include_filter) ;

	public void setBnumber_serviceClass_exclude_filter(final String bnumber_serviceClass_exclude_filter) ;

	public void setBnumber_db_exclude_filter(final String bnumber_db_exclude_filter) ;

	public short getMcc() ;

	public String getGsm_name() ;

	public short getSc() ;

	public String getSms_notifications_header() ;

	public List<String> getMnc() ;

	public byte getMsisdn_length() ;

	public int getAnumber_da() ;

	public int getBnumber_da() ;

	public String getDa_expires_in() ;

	public List<String> getData_volume() ;

	public int getSharing_data_volume_limit() ;

	public List<String> getAnumber_serviceClass_include_filter() ;

	public List<String> getAnumber_db_include_filter() ;

	public List<String> getAnumber_serviceClass_exclude_filter() ;

	public List<String> getAnumber_db_exclude_filter() ;

	public List<String> getBnumber_serviceClass_include_filter() ;

	public List<String> getBnumber_db_include_filter() ;

	public List<String> getBnumber_serviceClass_exclude_filter() ;

	public List<String> getBnumber_db_exclude_filter() ;

	public List<String> getAir_hosts() ;

	public int getAir_io_sleep() ;

	public int getAir_io_timeout() ;

	public int getAir_io_threshold() ;

	public String getAir_test_connection_msisdn() ;

	public byte getAir_preferred_host() ;

	public void setAir_preferred_host(byte air_preferred_host) ;

}
