package logicaBusiness.entit‡;

import java.sql.Timestamp;

/**
 * Classe per raccogliere le informazioni su un dato di rilevazione
 * @author Ivan Lamparelli
 *
 */
public class DatoRilevazione {
	private String nomeAmbiente;	
	private String tipoSensore;
	private String codiceSensore;
	private int valore;
	private boolean errore;
	private String unit‡DiMisura;
	private Timestamp data;
	private String messaggio;
	
	public DatoRilevazione(String unNomeAmbiente, String unTipoSensore, String unCodiceSensore, 
			int unValore, boolean flagErrore, String unaUnit‡DiMisura, Timestamp unaData, String unMessaggio) {
		this.nomeAmbiente = unNomeAmbiente;
		this.tipoSensore = unTipoSensore;
		this.codiceSensore = unCodiceSensore;
		this.valore = unValore; 
		this.errore = flagErrore;
		this.unit‡DiMisura = unaUnit‡DiMisura;
		this.data = unaData;
		this.messaggio = unMessaggio;
	}
	
	public String getNomeAmbiente() {
		return nomeAmbiente;
	}
	
	public String getTipoSensore() {
		return tipoSensore;
	}
	
	public String getCodiceSensore() {
		return codiceSensore;
	}
	
	public int getValore() {
		return valore;
	}
	
	public boolean ËErrore() {
		return errore;
	}
	
	public String getUnit‡DiMisura() throws NullPointerException {
		return unit‡DiMisura;
	}
		
	public Timestamp getData() {
		return data;
	}
	
	public String getMessaggio() {
		return messaggio;
	}
	
}
