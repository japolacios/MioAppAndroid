package com.mio.app.mioapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @class GlobalData
 * @author edwin.ospina@iptotal.com( Edwin Ramiro Ospina) -
 *         juan.bernal@iptotal.com( Juan Mart�n Bernal)
 * @date 28 de marzo 2014
 * 
 * @version 1.0
 * 
 * @Licence Derechos reservados de Autor (c) IP Total Software S.A.
 * 
 * @description se encarga de la lectura, salida e inserci�n de data en las
 *              preferencias del sistema
 */
public class GlobalData {

	/**
	 * Retorna la data almacenada en las preferencias globales
	 * 
	 * @param key
	 *            , clave con la cual se asoci� la data al guardarla
	 * @param context
	 *            , contexto del cual se solicita.
	 * @return valor para la key � null en caso de no existir un valor
	 */
	public static String get(String key, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("private", Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getString(key, null);
		else
			return null;
	}

	/**
	 * almacena una variable en las preferencias del sistema
	 * 
	 * @param key
	 *            , clave con la cual se asociar la data para guardarla
	 * @param value
	 *            , data a guardar.
	 * @param context
	 *            , contexto del cual se solicita.
	 */
	public static void set(String key, String value, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("private", Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		
		editor.commit();
	}

	public static void delete(String key, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("private", Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static boolean isKey(String key, Context context) {
		return (get( key, context)!= null)?true: false;
	}
}
