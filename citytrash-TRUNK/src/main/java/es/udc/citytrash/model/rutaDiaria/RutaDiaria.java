package es.udc.citytrash.model.rutaDiaria;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import es.udc.citytrash.model.camion.Camion;
import es.udc.citytrash.model.ruta.Ruta;
import es.udc.citytrash.model.rutaDiariaContenedores.RutaDiariaContenedores;
import es.udc.citytrash.model.tipoDeBasura.TipoDeBasura;
import es.udc.citytrash.model.trabajador.Trabajador;

@Entity
@BatchSize(size = 10)
@Table(name = "TBL_RUTAS_DIARIAS")
public class RutaDiaria {

	RutaDiaria() {

	}

	public RutaDiaria(Ruta ruta, Calendar fecha) {
		this.ruta = ruta;
		this.fecha = fecha;
		// this.tiposDeBasura = ruta != null ? ruta.getTiposDeBasura() : new
		// ArrayList<TipoDeBasura>();
	}

	@Id
	@Column(name = "RUTA_DIARIA_ID")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "RutaDiariaIdGenerator")
	@GenericGenerator(name = "RutaDiariaIdGenerator", strategy = "native")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "RUTA_ID")
	public Ruta getRuta() {
		return ruta;
	}

	public void setRuta(Ruta ruta) {
		this.ruta = ruta;
	}

	@Column(name = "FECHA", nullable = false)
	@Temporal(TemporalType.DATE)
	public Calendar getFecha() {
		return fecha;
	}

	public void setFecha(Calendar fecha) {
		this.fecha = fecha;
	}

	@Column(name = "FECHA_HORA_INICIO")
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getFechaHoraInicio() {
		return fechaHoraInicio;
	}

	public void setFechaHoraInicio(Calendar fechaHoraInicio) {
		this.fechaHoraInicio = fechaHoraInicio;
	}

	@Column(name = "FECHA_HORA_FIN")
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getFechaHoraFin() {
		return fechaHoraFin;
	}

	public void setFechaHoraFin(Calendar fechaHoraFin) {
		this.fechaHoraFin = fechaHoraFin;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "RECOGEDOR1")
	public Trabajador getRecogedor1() {
		return recogedor1;
	}

	public void setRecogedor1(Trabajador recogedor1) {
		this.recogedor1 = recogedor1;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "RECOGEDOR2")
	public Trabajador getRecogedor2() {
		return recogedor2;
	}

	public void setRecogedor2(Trabajador recogedor2) {
		this.recogedor2 = recogedor2;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "CONDUCTOR_ASIGNADO")
	public Trabajador getConductor() {
		return conductor;
	}

	public void setConductor(Trabajador conductor) {
		this.conductor = conductor;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "TRABAJADOR_ACTUALIZA")
	public Trabajador getTrabajadorActualiza() {
		return trabajadorActualiza;
	}

	public void setTrabajadorActualiza(Trabajador trabajadorActualiza) {
		this.trabajadorActualiza = trabajadorActualiza;
	}

	@Column(name = "FECHA_HORA_ACTUALIZACION")
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	public Calendar getfHUltimaActualizacion() {
		return fHUltimaActualizacion;
	}

	public void setfHUltimaActualizacion(Calendar fHUltimaActualizacion) {
		this.fHUltimaActualizacion = fHUltimaActualizacion;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "CAMION_ID")
	public Camion getCamion() {
		return camion;
	}

	public void setCamion(Camion camion) {
		this.camion = camion;
	}

	@Transient
	public double getDuracion() {
		Calendar ahora = Calendar.getInstance();
		Calendar fi = this.fechaHoraInicio != null ? this.fechaHoraInicio : ahora;
		Calendar ff = this.fechaHoraFin != null ? fechaHoraFin : ahora;
		// Cálculo de la diferencia de tiempo
		long milliSec1 = fi.getTimeInMillis();
		long milliSec2 = ff.getTimeInMillis();
		long timeDifInMilliSec = milliSec2 - milliSec1;
		return timeDifInMilliSec / (60 * 1000);
	}

	@OneToMany(mappedBy = "pk.rutaDiaria", cascade = CascadeType.ALL)
	public List<RutaDiariaContenedores> getRutaDiariaContenedores() {
		return rutaDiariaContenedores;
	}

	public void setRutaDiariaContenedores(List<RutaDiariaContenedores> rutaDiariaContenedores) {
		this.rutaDiariaContenedores = rutaDiariaContenedores;
	}

	public void addRutaDiariaContenedores(RutaDiariaContenedores rutaDiariaContenedores) {
		this.rutaDiariaContenedores.add(rutaDiariaContenedores);
	}

	/* http://www.baeldung.com/hibernate-many-to-many */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TBL_RD_TP", joinColumns = { @JoinColumn(name = "RUTA_DIARIA_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "TIPO_BASURA_ID") })
	public List<TipoDeBasura> getTiposDeBasura() {
		return tiposDeBasura;
	}

	public void setTiposDeBasura(List<TipoDeBasura> tiposDeBasura) {
		if (tiposDeBasura == null)
			this.tiposDeBasura = new ArrayList<TipoDeBasura>();
		else
			this.tiposDeBasura = tiposDeBasura;
	}

	public void eliminarTipoDeBasura(TipoDeBasura tipo) {
		if (tipo != null)
			this.tiposDeBasura.remove(tipo);
	}

	public void addTipoDeBasura(TipoDeBasura t) {
		if (this.tiposDeBasura == null) {
			this.tiposDeBasura = new ArrayList<TipoDeBasura>();
		}

		if (t != null) {
			if (!this.tiposDeBasura.contains(t)) {
				this.tiposDeBasura.add(t);
			}

		}
	}

	public boolean containsTipoDeBasura(TipoDeBasura tipo) {
		if (tipo == null)
			return false;
		if (this.tiposDeBasura == null)
			return false;
		else
			return this.tiposDeBasura.contains(tipo);
	}

	long id;
	private Ruta ruta;
	private Calendar fecha;
	private Calendar fechaHoraInicio;
	private Calendar fechaHoraFin;
	private Trabajador recogedor1 = null;
	private Trabajador recogedor2 = null;
	private Trabajador conductor = null;
	private Trabajador trabajadorActualiza = null;
	private Calendar fHUltimaActualizacion;
	private Camion camion = null;
	private List<RutaDiariaContenedores> rutaDiariaContenedores = new ArrayList<RutaDiariaContenedores>();
	private List<TipoDeBasura> tiposDeBasura;

	@Override
	public String toString() {
		return "RutaDiaria [id=" + id + ", ruta=" + ruta + ", fecha=" + fecha + ", fechaHoraInicio=" + fechaHoraInicio
				+ ", fechaHoraFin=" + fechaHoraFin + ", recogedor1=" + recogedor1 + ", recogedor2=" + recogedor2
				+ ", conductor=" + conductor + ", trabajadorActualiza=" + trabajadorActualiza
				+ ", fHUltimaActualizacion=" + fHUltimaActualizacion + ", camion=" + camion
				+ ", rutaDiariaContenedores=" + rutaDiariaContenedores + ", tiposDeBasura=" + tiposDeBasura + "]";
	}
}
