package com.massivecraft.massivecore.cmd.arg;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.store.SenderIdSource;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;

public abstract class ARSenderIdAbstract<T> extends ARAbstract<T>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	protected final SenderIdSource source;
	protected final boolean online;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public ARSenderIdAbstract(SenderIdSource source, boolean online)
	{
		this.source = source;
		this.online = online;
	}
	
	public ARSenderIdAbstract(SenderIdSource source)
	{
		this(source, false);
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract T getResultForSenderId(String senderId);
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		if (online) return "online player";
		else return "player";
	}
	
	@Override
	public T read(String arg, CommandSender sender) throws MassiveException
	{
		// arg --> senderId
		String senderId = this.getSenderIdFor(arg);
		// All of our subclasses return null if senderId is null.
		// Thus we don't need to check for that being null, but only check ret.
		
		// Create & populate Ret
		T ret = this.getResultForSenderId(senderId);
		
		if (ret == null)
		{
			// No alternatives found
			throw new MassiveException().addMsg("<b>No %s matches \"<h>%s<b>\".", this.getTypeName(), arg);
		}
	
		// Return Ret
		return ret;
	}
	
	// This is used for arbitrary command order.
	// There might be no matching sender at this time, but that does not matter.
	// As long as the format is correct the arg is valid.
	@Override
	public boolean isValid(String arg, CommandSender sender)
	{
		// Allow names and uuid by format.
		if (MUtil.isValidPlayerName(arg)) return true;
		if (MUtil.isUuid(arg)) return true;
		
		// Check data presence. This handles specials like "@console".
		if (IdUtil.getIdToData().containsKey(arg)) return true;
		if (IdUtil.getNameToData().containsKey(arg)) return true;
		
		return false;
	}
	
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		// Create Ret
		Set<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		// Fill Ret
		Set<String> names;
		if (online)
		{
			names = IdUtil.getOnlineNames();
			for (String name : names)
			{
				if ( ! Mixin.canSee(sender, name)) continue;
				ret.add(name);
			}
		}
		else
		{
			names = IdUtil.getAllNames();
			ret.addAll(names);
		}
		
		// Return Ret
		return ret;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public String getSenderIdFor(String arg)
	{
		// Get senderId from the arg.
		// Usually it's just the lowercase form.
		// It might also be a name resolution.
		String senderId = arg.toLowerCase();
		String betterId = IdUtil.getId(senderId);
		if (betterId != null) senderId = betterId;
		
		for (Collection<String> coll : this.source.getSenderIdCollections())
		{
			// If the senderId exists ...
			if ( ! coll.contains(senderId)) continue;
			
			// ... and the online check passes ...
			if (this.online && !Mixin.isOnline(senderId)) continue;
			
			// ... and the result is non null ...
			T result = this.getResultForSenderId(senderId);
			if (result == null) continue;
			
			// ... then we are go!
			return senderId;
		}
		
		return null;
	}

}
