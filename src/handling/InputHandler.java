package handling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;

import com.google.common.base.Splitter;

import connexions.AIRRequest;
import dao.DAO;
import dao.JdbcOperations.RollBackDAOJdbc;
import dao.JdbcOperations.SharingDAOJdbc;
import dao.JdbcOperations.USSDDAOJdbc;
import dao.domain.model.RollBack;
import dao.domain.model.Sharing;
import dao.domain.model.USSD;
import env.SMPPConnector;
import env.USSDMenu;
import env.WebAppProperties;
import filter.MSISDNValidator;
import util.BalanceAndDate;
import util.DedicatedAccount;

public class InputHandler {

	public InputHandler() {

	}

	public void handle(MessageSource i18n, WebAppProperties webAppProperties, Map<String, String> parameters, Map<String, Object> modele, HttpServletRequest request, DAO dao) {
		USSD ussd = null;

		try {
			long sessionId = Long.parseLong(parameters.get("sessionid"));
			ussd = new USSDDAOJdbc(dao).getOneUSSD(sessionId, parameters.get("msisdn"));

			if(ussd == null) {
				ussd = new USSD(0, sessionId, parameters.get("msisdn"), parameters.get("input").trim(), 1, null);
			}
			else {
				ussd.setStep(ussd.getStep() + 1);
				ussd.setInput((ussd.getInput() + "*" + parameters.get("input").trim()).trim());
			}

			// USSD Flow Status
			Map<String, Object> flowStatus = new USSDFlow().validate(ussd, (new USSDMenu()).getContent(webAppProperties.getSc()), webAppProperties, i18n);

			// -1 : exit with error (delete state from ussd table; message)
			if(((Integer)flowStatus.get("status")) == -1) {
				endStep(dao, ussd, modele, webAppProperties, (String)flowStatus.get("message"), null, null, null, null);
			}

			// 0  : successful (delete state from ussd table; actions and message)
			else if(((Integer)flowStatus.get("status")) == 0) {
				String short_code = webAppProperties.getSc() +  "";

				if(ussd.getInput().equals(short_code + "*2")) {
					// envoie SMS de statut
					statut(i18n, webAppProperties, dao, ussd, modele);
				}
				else if(ussd.getInput().startsWith(short_code + "*1")) {
					if((new MSISDNValidator()).isFiltered(dao, webAppProperties, ussd.getMsisdn(), "A")) {
						try {
							// List<String> inputs = Splitter.onPattern("[*]").trimResults().splitToList(ussd.getInput());
							List<String> inputs = Splitter.onPattern("[*]").trimResults().omitEmptyStrings().splitToList(ussd.getInput());
							
							Long.parseLong(inputs.get(3));

							if(inputs.size() == 4) {
								sharing(dao, ussd, i18n, webAppProperties, modele, inputs);
							}
							else {
								throw new Exception();
							}

						} catch(NumberFormatException ex) {
							endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("request.unavailable", null, null, null), null, null, null, null);

						} catch(Exception ex) {
							endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("request.unavailable", null, null, null), null, null, null, null);

						} catch(Throwable ex) {
							endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("request.unavailable", null, null, null), null, null, null, null);
						}
					}
					else endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("menu.disabled", null, null, null), null, null, null, null);
				}
				else {
					endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);
				}
			}

			// 1  : flow continues (save state; message)
			else if(((Integer)flowStatus.get("status")) == 1) {
				if((new MSISDNValidator()).isFiltered(dao, webAppProperties, ussd.getMsisdn(), "A")) nextStep(dao, ussd, false, (String)flowStatus.get("message"), modele, webAppProperties);
				else {
					// envoie SMS de statut
					statut(i18n, webAppProperties, dao, ussd, modele);
					// endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("menu.disabled", null, null, null), null, null, null, null);
				}
			}

			// this case should not occur
			else {
				endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);
			}

		} catch(NullPointerException ex) {
			endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);

		} catch(Exception ex) {
			endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);

		} catch(Throwable th) {
			endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);
		}
	}

	public void statut(MessageSource i18n, WebAppProperties webAppProperties, DAO dao, USSD ussd, Map<String, Object> modele) {
		BalanceAndDate balance = (new MSISDNValidator()).isFiltered(dao, webAppProperties, ussd.getMsisdn(), "A") ? new AIRRequest().getBalanceAndDate(ussd.getMsisdn(), (int) webAppProperties.getAnumber_da()) : (new MSISDNValidator()).isFiltered(dao, webAppProperties, ussd.getMsisdn(), "B") ? new AIRRequest().getBalanceAndDate(ussd.getMsisdn(), (int) webAppProperties.getBnumber_da()) : null;

		if(balance == null) {
			endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("status.failed", null, null, null), null, null, null, null);
		}
		else {
			if((balance.getAccountValue()/(10*100)) >= 1024) {
				// endStep(dao, ussd, modele, i18n.getMessage("status", new Object [] {new Formatter().format("%4d", (double)units/1024), "Go", (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(balance.getExpiryDate())}, null, null), null, null, null, null);
				// endStep(dao, ussd, modele, i18n.getMessage("status", new Object [] {new Formatter().format("%04d", (double)units/1024), "Go", (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(balance.getExpiryDate())}, null, null), null, null, null, null);
				endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("status", new Object [] {new Formatter().format("%.2f", ((double)(balance.getAccountValue()/(10*100)))/1024), "Go", (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(balance.getExpiryDate())}, null, null), null, null, null, null);
			}
			else {
				endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("status", new Object [] {balance.getAccountValue()/(10*100), "Mo", (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(balance.getExpiryDate())}, null, null), null, null, null, null);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public boolean doActions(WebAppProperties webAppProperties, DAO dao, String Anumber, String Bnumber, int choice) {
		try {
			long volume_data = 0;

			try {
				volume_data = Long.parseLong(webAppProperties.getData_volume().get(choice-1));

			} catch(NullPointerException|NumberFormatException ex) {

			} catch(Exception ex) {

			} catch(Throwable th) {

			}

			// do action only if volume_data is strictly positive
			if(volume_data <= 0) return false;

			HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
			if((int) webAppProperties.getAnumber_da() == 0) balances.add(new BalanceAndDate((int) webAppProperties.getAnumber_da(), -volume_data, null));
			else balances.add(new DedicatedAccount((int) webAppProperties.getAnumber_da(), -volume_data, null));

			// update Anumber Balance
			if(new AIRRequest().updateBalanceAndDate(Anumber, balances, "TEST", "TEST", "eBA")) {
				balances.clear();
				Date expires = new Date();
				expires.setSeconds(59);expires.setMinutes(59);expires.setHours(23);expires.setDate(31);expires.setMonth(4);expires.setYear(118);
				if((int) webAppProperties.getBnumber_da() == 0) balances.add(new BalanceAndDate((int) webAppProperties.getBnumber_da(), volume_data, expires));
				else balances.add(new DedicatedAccount((int) webAppProperties.getBnumber_da(), volume_data, expires));

				// update Bnumber Balance
				if(new AIRRequest().updateBalanceAndDate(Bnumber, balances, "TEST", "TEST", "eBA")) {
					// update Bnumber sharing
					Sharing sharing = new SharingDAOJdbc(dao).getOneSharing(Bnumber);
					if(sharing == null) sharing = new Sharing(0, Bnumber, volume_data);
					else sharing.setValue(volume_data);

					new SharingDAOJdbc(dao).saveOneSharing(sharing);

					return true;
				}
				// rollback
				else {
					balances.clear();
					if((int) webAppProperties.getAnumber_da() == 0) balances.add(new BalanceAndDate((int) webAppProperties.getAnumber_da(), volume_data, null));
					else balances.add(new DedicatedAccount((int) webAppProperties.getAnumber_da(), volume_data, null));

					if(new AIRRequest().updateBalanceAndDate(Anumber, balances, "TEST", "TEST", "eBA"));
					else new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, choice, Anumber, Bnumber, null));
				}
			}

		} catch(Throwable th) {

		}

		return false;
	}

	public void endStep(DAO dao, USSD ussd, Map<String, Object> modele, WebAppProperties webAppProperties, String messageA, String Anumber, String messageB, String Bnumber, String senderName) {
		if((ussd != null) && (ussd.getId() > 0)) {
			new USSDDAOJdbc(dao).deleteOneUSSD(ussd.getId());
		}

		modele.put("next", false);
		modele.put("message", messageA);

		if(senderName != null) {
			if(Anumber != null) {
				if(Anumber.startsWith(webAppProperties.getMcc() + "")) Anumber = Anumber.substring((webAppProperties.getMcc() + "").length());
				new SMPPConnector().submitSm(senderName, Anumber, messageA);
			}
			if(Bnumber != null) {
				if(Bnumber.startsWith(webAppProperties.getMcc() + "")) Bnumber = Bnumber.substring((webAppProperties.getMcc() + "").length());
				new SMPPConnector().submitSm(senderName, Bnumber, messageB);
			}
		}
	}

	public void nextStep(DAO dao, USSD ussd, boolean reset, String message, Map<String, Object> modele, WebAppProperties webAppProperties) {
		if(reset) {
			ussd.setStep(1);
			ussd.setInput(webAppProperties.getSc() + "");
		}
		else {
			//
		}

		new USSDDAOJdbc(dao).saveOneUSSD(ussd);

		modele.put("next", true);
		modele.put("message", message);
	}

	public void sharing(DAO dao, USSD ussd, MessageSource i18n, WebAppProperties webAppProperties, Map<String, Object> modele, List<String> inputs) {
		// validate choice as msisdn
		/*if(((choice+"").length() == 11) && ((choice+"").startsWith("229"))) {*/
		if(new MSISDNValidator().onNet(webAppProperties, webAppProperties.getMcc() + "" + inputs.get(3))) {
			// check parameters.get("msisdn") is allowed
			if((new MSISDNValidator()).isFiltered(dao, webAppProperties, ussd.getMsisdn(), "A")) {
				// check choice is allowed
				if((new MSISDNValidator()).isFiltered(dao, webAppProperties, webAppProperties.getMcc() + "" + inputs.get(3), "B")) {
					// check cumulated data is less or equal than 5Go
					Sharing sharing = new SharingDAOJdbc(dao).getOneSharing(webAppProperties.getMcc() + "" + inputs.get(3));

					long volume_data = 0;

					try {
						volume_data = Long.parseLong(webAppProperties.getData_volume().get(Integer.parseInt(inputs.get(2)) - 1));

					} catch(NullPointerException|NumberFormatException ex) {

					} catch(Exception ex) {

					} catch(Throwable th) {

					}

					if((volume_data > 0) && ((sharing == null) || ((sharing.getValue() + volume_data) <= (webAppProperties.getSharing_data_volume_limit())))) {
						// int choice = Integer.parseInt(parameters.get("imput").substring(6, 7));
						if(doActions(webAppProperties, dao, ussd.getMsisdn(), webAppProperties.getMcc() + "" + inputs.get(3), Integer.parseInt(inputs.get(2)))) {
							String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
							String volume = volume_data / (10*100) + "";

							endStep(dao, ussd, modele, webAppProperties, "Vous avez envoye avec succes " + volume + "Mo au " + inputs.get(3) + " le " + date + ". Votre Volume Internet expire le " + "31-05-2018 a 23:59", ussd.getMsisdn(), "Vous avez recu avec succes " + volume + "Mo du " + ussd.getMsisdn() + " le " + date + ". Votre Volume Internet expire le " + "31-05-2018 a 23:59. Pour verifier, tapez: *124*DA#", inputs.get(3)+"", "STAFFDATA");
						}
						else {
							endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("service.internal.error", null, null, null), null, null, null, null);
						}
					}
					else {
						long gap = (sharing == null) ? (webAppProperties.getSharing_data_volume_limit()) : (webAppProperties.getSharing_data_volume_limit() - sharing.getValue());

						if(gap >= Long.parseLong(webAppProperties.getData_volume().get(0))) {
							if((gap/(10*100)) >= 1024) {
								endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("data.sharing.receiver.gap", new Object [] {inputs.get(3), new Formatter().format("%.2f", ((double)(gap/(10*100)))/1024), "Go"}, null, null), null, null, null, null);
							}
							else {
								endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("data.sharing.receiver.gap", new Object [] {inputs.get(3), gap/(10*100), "Mo"}, null, null), null, null, null, null);
							}
						}
						else endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("data.sharing.receiver.limits.exceeded", new Object[] {inputs.get(3)}, null, null), null, null, null, null);
					}
				}
				else {
					endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("data.sharing.receiver.disabled", new Object[] {inputs.get(3)}, null, null), null, null, null, null);
				}
			}
			else {
				endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("data.sharing.disabled", null, null, null), null, null, null, null);
			}
		}
		else {
			endStep(dao, ussd, modele, webAppProperties, i18n.getMessage("msisdn.onnet.required", new Object [] {webAppProperties.getGsm_name()}, null, null), null, null, null, null);
		}
	}

}
