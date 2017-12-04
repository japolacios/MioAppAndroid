package com.mio.app.mioapp.model;

public class Noticia 
{
	public String id;
	public String noticia;
	public String fecha;
	public String estado;
	
	public Noticia(String id, String noticia, String fecha, String estado)
	{
		this.id         = id;
		this.noticia    = noticia;
		this.fecha      = fecha;
		this.estado     = estado;
	}
	public Noticia(String noticia, String fecha, String estado)
	{
		
		this.noticia    = noticia;
		this.fecha      = fecha;
		this.estado     = estado;
	}
	
	@Override
	public String toString()
	{
		return noticia + " " + fecha;
	}
	
	

}
