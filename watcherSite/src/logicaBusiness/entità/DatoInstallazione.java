package logicaBusiness.entit‡;

/**
 * Classe per raccogliere le informazioni su un dato di installazione
 * @author Ivan Lamparelli
 *
 */
public class DatoInstallazione {
	private String nomeAmbiente;
	private String tipoAmbiente;
	private String tipoSensore;
	private String unit‡MisuraSensore;
	private String marcaSensore; 
	private String modelloSensore;
	private String codiceSensore;
	private String posizioneSensore;
	
	public DatoInstallazione(String unNomeAmbiente, String unTipoAmbiente, String unTipoSensore, String unaUnit‡DiMisura, String unaMarcaSensore,
			String unModelloSensore, String unCodiceSensore, String unaPosizione) {
		this.nomeAmbiente = unNomeAmbiente;
		this.tipoAmbiente = unTipoAmbiente;
		this.tipoSensore = unTipoSensore;
		this.unit‡MisuraSensore = unaUnit‡DiMisura;
		this.marcaSensore = unaMarcaSensore;
		this.modelloSensore = unModelloSensore;
		this.codiceSensore = unCodiceSensore;
		this.posizioneSensore = unaPosizione;
	}

	public String getNomeAmbiente() {
		return nomeAmbiente;
	}

	public String getTipoAmbiente() {
		return tipoAmbiente;	
	}

	public String getTipoSensore() {
		return tipoSensore;
	}
	
	public String getUnit‡DiMisura() {
		return unit‡MisuraSensore;
	}

	public String getMarcaSensore() {
		return marcaSensore;
	}

	public String getModelloSensore() {
		return modelloSensore;
	}

	public String getCodiceSensore() {
		return codiceSensore;
	}
		
	public String getPosizioneSensore() {
		return posizioneSensore;
	}
	
}
