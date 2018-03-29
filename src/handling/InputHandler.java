package handling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import connexions.AIRRequest;
import dao.DAO;
import dao.JdbcOperations.RollBackDAOJdbc;
import dao.JdbcOperations.SharingDAOJdbc;
import dao.JdbcOperations.USSDDAOJdbc;
import dao.domain.model.RollBack;
import dao.domain.model.Sharing;
import dao.domain.model.USSD;
import env.WebAppProperties;
import filter.NumberValidator;
import util.BalanceAndDate;
import util.DedicatedAccount;

public class InputHandler {

	/*HashMap<Integer, Long> sharings = null;*/

	public InputHandler() {
		/*sharings = new HashMap<Integer, Long>();
		sharings.put(1, (long) (100*10*100));
		sharings.put(2, (long) (250*10*100));
		sharings.put(3, (long) (500*10*100));
		sharings.put(4, (long) (1.5*1024*10*100));
		sharings.put(5, (long) (3.5*1024*10*100));
		sharings.put(6, (long) (5*1024*10*100));*/
	}

	public void handle(WebAppProperties webAppProperties, Map<String, String> parameters, Map<String, Object> modele, HttpServletRequest request, DAO dao) {
		long sessionId = Long.parseLong(parameters.get("sessionid"));

		USSD ussd = new USSDDAOJdbc(dao).getOneUSSD(sessionId, parameters.get("msisdn"));

		// entry
		if(ussd == null) {
			if(parameters.get("input").equals("250*1")) {
				entryStep(dao, new USSD(0, sessionId, parameters.get("msisdn"), "1=1&2=", 2, new Date()), modele, "Veuillez choisir le volume a offir\n\n" + "1- 100Mo\n2- 250Mo\n3- 500Mo\n4- 1.5Go\n5- 3.5Go\n6- 5Go");
			}
			else if(parameters.get("input").equals("250*2")) {
				// envoie SMS de statut
				statut(webAppProperties, dao, null, modele, parameters.get("msisdn"));
			}
			else if(parameters.get("input").equals("250*1*1") || parameters.get("input").equals("250*1*2") || parameters.get("input").equals("250*1*3") || parameters.get("input").equals("250*1*4") || parameters.get("input").equals("250*1*5") || parameters.get("input").equals("250*1*6")) {
				entryStep(dao, new USSD(0, sessionId, parameters.get("msisdn"), "1=1&2=" + Integer.parseInt(parameters.get("input").substring(6, 7)) + "&3=", 3, new Date()), modele, "Veuillez entrer le numero");
			}
			else if(parameters.get("input").equals("250")) {
				entryStep(dao, new USSD(0, sessionId, parameters.get("msisdn"), "1=", 1, new Date()), modele, "Bienvenue sur le service Data Month\n\n" + "1- Partager\n2- Statut");
			}
			else {
				endStep(dao, ussd, modele, "Votre requete est erronee", null, null, null, null);
			}
		}

		// flow continue
		else {
			try {
				long choice = Long.parseLong(parameters.get("input"));

				if(ussd.getInput().equals("1=")) {
					if(choice == 1) {
						nextStep(dao, ussd, parameters, false);

						modele.put("next", true);
						modele.put("message", "Veuillez choisir le volume a offir\n\n" + "1- 100Mo\n2- 250Mo\n3- 500Mo\n4- 1.5Go\n5- 3.5Go\n6- 5Go");						
					}
					else if(choice == 2) {
						// envoie SMS de statut
						statut(webAppProperties, dao, ussd, modele, parameters.get("msisdn"));
					}
					else {
						endStep(dao, ussd, modele, "Veuillez entrer un chiffre valide", null, null, null, null);
					}
				}

				else if(ussd.getInput().equals("1=1&2=")) {
					if((choice > 0) && (choice < 7)) {
						nextStep(dao, ussd, parameters, false);

						modele.put("next", true);
						modele.put("message", "Veuillez entrer le numero");
					}
					else {
						endStep(dao, ussd, modele, "Veuilez entrer un chiffre valide", null, null, null, null);
					}
				}

				else if(ussd.getInput().equals("1=1&2=1&3=") || ussd.getInput().equals("1=1&2=2&3=") || ussd.getInput().equals("1=1&2=3&3=") || ussd.getInput().equals("1=1&2=4&3=") || ussd.getInput().equals("1=1&2=5&3=") || ussd.getInput().equals("1=1&2=6&3=")) {
					// validate choice as msisdn
					/*if(((choice+"").length() == 11) && ((choice+"").startsWith("229"))) {*/
					if(new NumberValidator().onNet(webAppProperties, webAppProperties.getMcc() + "" + choice)) {
						// check parameters.get("msisdn") is allowed
						if((new NumberValidator()).isFiltered(dao, webAppProperties, parameters.get("msisdn"), "A")) {
							// check choice is allowed
							if((new NumberValidator()).isFiltered(dao, webAppProperties, webAppProperties.getMcc() + "" + choice, "B")) {
								// check cumulated data is less or equal than 5Go
								Sharing sharing = new SharingDAOJdbc(dao).getOneSharing(webAppProperties.getMcc() + "" + choice);

								long volume_data = 0;

								try {
									volume_data = Long.parseLong(webAppProperties.getData_volume().get(Integer.parseInt(ussd.getInput().substring(6, 7)) - 1));

								} catch(NullPointerException ex) {
									
								} catch(NumberFormatException ex) {
									
								} catch(Exception ex) {

								} catch(Throwable th) {

								}

								if((volume_data > 0) && ((sharing == null) || ((sharing.getValue() + volume_data) <= (webAppProperties.getSharing_data_volume_limit())))) {
									// int choice = Integer.parseInt(parameters.get("imput").substring(6, 7));
									if(sharing(webAppProperties, dao, parameters.get("msisdn"), webAppProperties.getMcc() + "" + choice, Integer.parseInt(ussd.getInput().substring(6, 7)))) {
										String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
										String volume = volume_data / (10*100) + "";

										endStep(dao, ussd, modele, "Vous avez envoye avec succes " + volume + "Mo au " + choice + " le " + date + ". Votre Volume Internet expire le " + "31-05-2018 a 23:59", parameters.get("msisdn"), "Vous avez recu avec succes " + volume + "Mo du " + parameters.get("msisdn") + " le " + date + ". Votre Volume Internet expire le " + "31-05-2018 a 23:59. Pour verifier, tapez: *124*DA#", choice+"", "Data-Month");
									}
									else {
										endStep(dao, ussd, modele, "Y'ello, Votre requete ne peut etre traite. Veuillez reesayer plus tard...", null, null, null, null);
									}
								}
								else {
									endStep(dao, ussd, modele, "Y'ello, le numero " + (choice+"") + " ne peut pas recevoir le volume Data choisi", null, null, null, null);
								}
							}
							else {
								endStep(dao, ussd, modele, "Y'ello, le numero " + (choice+"") + " n'est pas autorise a recevoir de volume Data", null, null, null, null);
							}
						}
						else {
							endStep(dao, ussd, modele, "Y'ello, vous ne pouvez pas partager de volume Data", null, null, null, null);
						}
					}
					else {
						endStep(dao, ussd, modele, "Veuillez entrer un numero MTN valide", null, null, null, null);
					}
				}

				else {
					endStep(dao, ussd, modele, "Veuillez entrer un chiffre valide", null, null, null, null);
				}

			} catch(NumberFormatException ex) {
				endStep(dao, ussd, modele, "Veuillez entrer un chiffre valide", null, null, null, null);

			} catch(Exception ex) {
				endStep(dao, ussd, modele, "Votre requete est erronee", null, null, null, null);

			} catch(Throwable th) {
				endStep(dao, ussd, modele, "Votre requete est erronee", null, null, null, null);
			}
		}
	}

	public void statut(WebAppProperties webAppProperties, DAO dao, USSD ussd, Map<String, Object> modele, String msisdn) {
		BalanceAndDate balance = null;

		if((new NumberValidator()).isFiltered(dao, webAppProperties, msisdn, "A")) {
			balance = new AIRRequest().getBalanceAndDate(msisdn, (int) webAppProperties.getAnumber_da());
		}
		else if((new NumberValidator()).isFiltered(dao, webAppProperties, msisdn, "B")) {
			balance = new AIRRequest().getBalanceAndDate(msisdn, (int) webAppProperties.getBnumber_da());
		}

		if(balance == null) {
			endStep(dao, ussd, modele, "Desole, vous n'avez pas de volume Data", null, null, null, null);
		}
		else {
			endStep(dao, ussd, modele, "votre volume Data est de " + (balance.getAccountValue()/(10*100)) + "Mo et est valable jusqu'au 31/05/2018 a 23:59", null, null, null, null);
			// balance.getExpiryDate();
		}
	}

	@SuppressWarnings("deprecation")
	public boolean sharing(WebAppProperties webAppProperties, DAO dao, String Anumber, String Bnumber, int choice) {
		try {
			long volume_data = 0;

			try {
				volume_data = Long.parseLong(webAppProperties.getData_volume().get(choice-1));

			} catch(NullPointerException ex) {
				
			} catch(NumberFormatException ex) {
				
			} catch(Exception ex) {
				
			} catch(Throwable th) {

			}

			// do action only if volume_data is strictly positive
			if(volume_data <= 0) return false;

			HashSet<BalanceAndDate> balances = new HashSet<BalanceAndDate>();
			balances.add(new DedicatedAccount((int) webAppProperties.getAnumber_da(), -volume_data, null));

			// update Anumber Balance
			if(new AIRRequest().updateBalanceAndDate(Anumber, balances, "TEST", "TEST", "ebafrique")) {
				balances.clear();
				//balances.add(new BalanceAndDate(261, volume_data, null));
				Date expires = new Date();
				expires.setHours(23);
				expires.setMinutes(59);
				expires.setSeconds(59);
				expires.setDate(31);
				expires.setMonth(4);
				expires.setYear(118);
				balances.add(new DedicatedAccount((int) webAppProperties.getBnumber_da(), volume_data, expires));

				// update Bnumber Balance
				if(new AIRRequest().updateBalanceAndDate(Bnumber, balances, "TEST", "TEST", "ebafrique")) {
					// update Bnumber sharing
					Sharing sharing = new SharingDAOJdbc(dao).getOneSharing(Bnumber);
					if(sharing == null) {
						sharing = new Sharing(0, Bnumber, volume_data);
					}
					else {
						sharing.setValue(volume_data);
					}

					new SharingDAOJdbc(dao).saveOneSharing(sharing);

					return true;
				}
				// rollback
				else {
					balances.clear();
					balances.add(new DedicatedAccount((int) webAppProperties.getAnumber_da(), volume_data, null));

					if(new AIRRequest().updateBalanceAndDate(Anumber, balances, "TEST", "TEST", "ebafrique")) {

					}
					else {
						new RollBackDAOJdbc(dao).saveOneRollBack(new RollBack(0, 1, choice, Anumber, Bnumber, null));
					}
				}
			}

		} catch(Throwable th) {
			th.printStackTrace();
		}

		return false;
	}
	
	public void entryStep(DAO dao, USSD ussd, Map<String, Object> modele, String message) {
		new USSDDAOJdbc(dao).saveOneUSSD(ussd);

		modele.put("next", true);
		modele.put("message", message);
	}

	public void endStep(DAO dao, USSD ussd, Map<String, Object> modele, String messageA, String Anumber, String messageB, String Bnumber, String senderName) {
		if((ussd != null) && (ussd.getId() > 0)) {
			new USSDDAOJdbc(dao).deleteOneUSSD(ussd.getId());
		}

		modele.put("next", false);
		modele.put("message", messageA);
		
		if(senderName != null) {
			if(Anumber != null) {

			}
			if(Bnumber != null) {

			}
		}
	}

	public void nextStep(DAO dao, USSD ussd, Map<String, String> parameters, boolean reset) {
		if(reset) {
			ussd.setStep(1);
			ussd.setInput("1=");
		}
		else {
			ussd.setStep(ussd.getStep() + 1);
			ussd.setInput(ussd.getInput() + parameters.get("input") + "&" + ussd.getStep() + "=");
		}

		new USSDDAOJdbc(dao).saveOneUSSD(ussd);
	}

}
