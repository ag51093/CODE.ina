package logicaPresentazione;

import java.util.LinkedList;

/**
 * La classe si occupa di organizzare la visualizzazione
 *  dei dati del trasferimento automatico
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class VistaTrasferimento {
	
	private static final String ON = "ON";
	private static final String OFF = "OFF";	
	
	/**
	 * Il metodo recupera lo stato del trasferimento automatico per l'utente target della ricerca
	 * 
	 * @param unoStatoTrasferimento - un booleano contenente lo stato attuale del trasferimento automatico
	 * @return una stringa contenente lo stato attuale del trasferimento automatico scritto come "ON" o "OFF"
	 */
	public String scriviStatoTrasferimento(boolean unoStatoTrasferimento) {
		String stato = "";
		if (unoStatoTrasferimento) {
			stato = ON;
		} else {
			stato = OFF;
		} 
		return stato;
	}
	
	/**
	 * Il metodo interno costruisce una lista di input checkbox corrispondente agli ambienti dell'utente, eventualmente pre-selezionati
	 * @param listaAmbientiCompleta lista completa degli ambienti dell'utente
	 * @param listaAmbientiSelezionati lista degli ambienti selezionati dall'utente
	 * @return stringa contenente una lista di checkbox eventualmente pre-selezionate in base ai parametri ricevuti in input
	 */
	public static String mostraCheckboxAmbienti(LinkedList<String> listaAmbientiCompleta, LinkedList<String> listaAmbientiSelezionati) {
		String risultato = "";
		int sbsize = 128;
		StringBuilder stringBuilder = new StringBuilder(sbsize);
		stringBuilder.append(risultato);
		
		int sizeAmbientiCompleti = listaAmbientiCompleta.size();
		int sizeAmbientiSelezionati = listaAmbientiSelezionati.size();
		
		if ((sizeAmbientiCompleti > 0) && (sizeAmbientiSelezionati <= sizeAmbientiCompleti)) {
			for (int i = 0; i < sizeAmbientiCompleti; i++) {
				boolean checked = false;
				for (int j = 0; j < sizeAmbientiSelezionati; j++) {
					if (listaAmbientiCompleta.get(i).equals(listaAmbientiSelezionati.get(j))) {
						checked = true;
						stringBuilder.append("<input type=\"checkbox\" name=\"selezionaambiente\" value=\""+ listaAmbientiSelezionati.get(j) +"\" checked>" + listaAmbientiSelezionati.get(j) + "</input><br>");
				        break;
					}
				}
				if(!checked) {
					stringBuilder.append("<input type=\"checkbox\" name=\"selezionaambiente\" value=\"" + listaAmbientiCompleta.get(i) + "\">" + listaAmbientiCompleta.get(i) + "</input><br>");		      
				}
			}		  
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * Il metodo restituisce le opzioni che permettono di selezionare e/o inserire il destinatario dell'inoltro dei dati del Trasferimento Automatico
	 * @param unaMail mail identificativa dell'utente target della ricerca
	 * @return una stringa contentente la lista delle opzioni di selezione della mail di inoltro
	 */
	public String mostraOpzioniInoltroEmail(String unaMail, String unaMailInoltro) {
		/*NOTA BENE: Se si seleziona l'invio a terza parte potrebbe essere fornita una stringa non valida come una stringa vuota*/
		String risultato = "";
		
		if ((unaMailInoltro).equals(unaMail)) {
			risultato += "<input type=\"radio\" name=\"selezionaemail\" value=\"DEFAULT_MAIL\" checked>Invia a te stesso</input><br>"
				+	  "<input type=\"radio\" name=\"selezionaemail\" value=\"CUSTOM_MAIL\"><input type=\"text\" name=\"insterzaparte\" placeholder=\"Email Terza Parte\"></input></input>";
		} else {
			risultato += "<input type=\"radio\" name=\"selezionaemail\" value=\"DEFAULT_MAIL\">Invia a te stesso</input><br>"
				+	 "<input type=\"radio\" name=\"selezionaemail\" value=\"CUSTOM_MAIL\" checked><input type=\"text\" name=\"insterzaparte\" placeholder=\"" + unaMailInoltro + "\"></input></input>";
		}
		
		return risultato;
	}
	
	/**
	 * Il metodo restituisce le opzioni per personalizzare il Trasferimento
	 * 
	 * @param unaMail mail identificativa dell'utente target della ricerca
	 * @return una stringa contenente la lista delle opzioni di personalizzazione
	 */
	public String mostraOpzioniTrasferimento(String unaMail, String deiNomiAmbienti, String unaMailInoltro) {
		String risultato = "";
		
		risultato += "<strong>Personalizza Trasferimento Automatico:</strong><br><br>"
					+ "<table>"
					+	"<tr>"
					+	"<td style=\"border-bottom: none;\">"
					+	"Selezione ambienti per l'inoltro:<br><br>"
					+	deiNomiAmbienti
					+	"</td>"
					+ 	"<td style=\"border-bottom: none;\">"
					+	"Inserisci email per l'inoltro:<br><br>"
					+	"(Email attuale: <strong>"
					+ 	unaMailInoltro
					+	"</strong>)"
					+	"<br><br>"
					+	mostraOpzioniInoltroEmail(unaMail, unaMailInoltro)
					+	"</td>"
					+	"</tr>"
					+	"</table><br>"
					+	"<button type=\"submit\" style=\"display: block; margin: 0 auto;\">Conferma</button>";
		
		return risultato;
	}
}
