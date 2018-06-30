package es.udc.citytrash.model.rutaTipoDeBasura;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

import es.udc.citytrash.model.ruta.Ruta;
import es.udc.citytrash.model.tipoDeBasura.TipoDeBasura;

/**
 * Tabla RutaTipoDeBasura
 * 
 * @author hmia
 * 
 *
 */

@Entity
@BatchSize(size = 5)
@Table(name = "TBL_RU_TP")
@AssociationOverrides({ @AssociationOverride(name = "pk.ruta", joinColumns = @JoinColumn(name = "RUTA_ID")),
		@AssociationOverride(name = "pk.tipo", joinColumns = @JoinColumn(name = "TIPO_BASURA")) })
public class RutaTipoDeBasura implements Serializable {
	/**
	 * 
	 */
	public RutaTipoDeBasura() {

	}

	/**
	 * 
	 * @param ruta
	 * @param tipo
	 */
	public RutaTipoDeBasura(Ruta ruta, TipoDeBasura tipo) {
		this.pk.setRuta(ruta);
		this.pk.setTipo(tipo);
	}

	@EmbeddedId
	public RutaTipoDeBasuraPK getPk() {
		return pk;
	}

	public void setPk(RutaTipoDeBasuraPK pk) {
		this.pk = pk;
	}

	@Transient
	public Ruta getRuta() {
		return getPk().getRuta();
	}

	public void setRuta(Ruta ruta) {
		getPk().setRuta(ruta);
	}

	@Transient
	public TipoDeBasura getTipo() {
		return getPk().getTipo();
	}

	public void setTipo(TipoDeBasura tipo) {
		getPk().setTipo(tipo);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RutaTipoDeBasura that = (RutaTipoDeBasura) o;

		if (getPk() != null ? !getPk().equals(that.getPk()) : that.getPk() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (getPk() != null ? getPk().hashCode() : 0);
	}

	/**
	 * atributos
	 */
	private static final long serialVersionUID = 1L;
	private RutaTipoDeBasuraPK pk = new RutaTipoDeBasuraPK();

	@Override
	public String toString() {
		return "RutaTipoDeBasura [pk=" + pk + "]";
	}
}