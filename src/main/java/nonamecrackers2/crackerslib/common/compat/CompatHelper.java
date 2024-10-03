package nonamecrackers2.crackerslib.common.compat;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.neoforged.fml.ModList;

public class CompatHelper
{
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/CompatHelper");
	private static final List<String> COMPAT_ERRORS = Lists.newArrayList();
	private static boolean optifineLoaded;
	private static boolean vivecraftStandaloneLoaded;
	
	public static void checkForLoaded()
	{
		try
		{
			//Funny way to check if OptiFine is present. If the Config class exists, then OptiFine is loaded
			Class.forName("net.optifine.Config");
			optifineLoaded = true;
		}
		catch (ClassNotFoundException e)
		{
		}
		
		try
		{
			Class.forName("org.vivecraft.settings.VRSettings");
			vivecraftStandaloneLoaded = true;
		}
		catch (ClassNotFoundException e)
		{
		}
	}
	
	public static boolean areShadersRunning()
	{
		try
		{
			if (isIrisLoaded())
			{
				var clazz = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
				var instanceGetter = clazz.getMethod("getInstance");
				var irisApi = instanceGetter.invoke(null);
				return (boolean)irisApi.getClass().getMethod("isShaderPackInUse").invoke(irisApi);
			}
			else if (isOptifineLoaded())
			{
				var clazz = Class.forName("net.optifine.Config");
				var method = clazz.getMethod("isShaders");
				return (boolean)method.invoke(null);
			}
			else
			{
				return false;
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException e)
		{
			doErrorFor("shaders", () -> {
				LOGGER.error("Failed to check if shaders are enabled:");
				e.printStackTrace();
			});
			return false;
		}
	}
	
	public static boolean isVrActive()
	{
		if (ModList.get().isLoaded("vivecraft"))
		{
			try
			{
				var clazz = Class.forName("org.vivecraft.api_beta.client.VivecraftClientAPI");
				var instanceGetter = clazz.getMethod("getInstance");
				var vivecraftApi = instanceGetter.invoke(null);
				return (boolean)vivecraftApi.getClass().getMethod("isVrActive").invoke(vivecraftApi);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException e)
			{
				doErrorFor("vr", () -> {
					LOGGER.error("Failed to check if VR is active:");
					e.printStackTrace();
				});
				return false;
			}
		}
		return vivecraftStandaloneLoaded;
	}
	
	public static boolean isVivecraftLoaded()
	{
		return vivecraftStandaloneLoaded || ModList.get().isLoaded("vivecraft");
	}
	
	public static boolean isIrisLoaded()
	{
		return ModList.get().isLoaded("iris");
	}
	
	public static boolean isOptifineLoaded()
	{
		return optifineLoaded;
	}
	
	public static boolean isSodiumLoaded()
	{
		return ModList.get().isLoaded("sodium");
	}
	
	private static final void doErrorFor(String mod, Runnable runnable)
	{
		if (!COMPAT_ERRORS.contains(mod))
		{
			runnable.run();
			COMPAT_ERRORS.add(mod);
		}
	}
}
