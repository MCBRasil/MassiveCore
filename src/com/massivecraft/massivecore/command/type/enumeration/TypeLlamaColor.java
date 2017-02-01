package com.massivecraft.massivecore.command.type.enumeration;

import org.bukkit.entity.Llama.Color;

public class TypeLlamaColor extends TypeEnum<Color>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeLlamaColor i = new TypeLlamaColor();
	public static TypeLlamaColor get() { return i; }
	public TypeLlamaColor()
	{
		super(Color.class);
	}

}
