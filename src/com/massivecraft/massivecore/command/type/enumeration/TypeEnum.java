package com.massivecraft.massivecore.command.type.enumeration;

import java.util.Arrays;
import java.util.Collection;

import com.massivecraft.massivecore.command.type.TypeAbstractChoice;
import com.massivecraft.massivecore.util.Txt;

public class TypeEnum<T extends Enum<T>> extends TypeAbstractChoice<T>
{
	// -------------------------------------------- //
	// FIELD
	// -------------------------------------------- //
	
	protected final Class<T> clazz;
	public Class<T> getClazz() { return this.clazz; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public TypeEnum(Class<T> clazz)
	{
		if ( ! clazz.isEnum()) throw new IllegalArgumentException("clazz must be enum");
		this.clazz = clazz;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return Txt.getNicedEnumString(this.getClazz().getSimpleName());
	}
	
	@Override
	public String getNameInner(T value)
	{
		return Txt.getNicedEnum(value);
	}

	@Override
	public String getIdInner(T value)
	{
		return String.valueOf(value.ordinal());
	}

	@Override
	public Collection<T> getAll()
	{
		return Arrays.asList(getEnumValues(this.getClazz()));
	}
	
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	public static <T extends Enum<T>> T[] getEnumValues(Class<T> clazz)
	{
		if (clazz == null) throw new IllegalArgumentException("clazz is null");
		if ( ! clazz.isEnum()) throw new IllegalArgumentException("clazz must be enum");
		
		T[] ret = clazz.getEnumConstants();
		if (ret == null) throw new RuntimeException("failed to retrieve enum constants");
		
		return ret;
	}
	
}
