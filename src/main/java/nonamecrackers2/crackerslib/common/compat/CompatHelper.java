package nonamecrackers2.crackerslib.common.compat;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.ModList;

public class CompatHelper
{
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/CompatHelper");
	private static boolean OPTIFINE_LOADED;
	private static boolean VIVECRAFT_LOADED;
	private static boolean hasErrored;
	
	public static void checkForLoaded()
	{
		try
		{
			//Funny way to check if OptiFine is present. If the Config class exists, then OptiFine is loaded
			Class.forName("net.optifine.Config");
			OPTIFINE_LOADED = true;
		}
		catch (ClassNotFoundException e)
		{
		}
		
		try
		{
			Class.forName("org.vivecraft.settings.VRSettings");
			VIVECRAFT_LOADED = true;
		}
		catch (ClassNotFoundException e)
		{
		}
	}
	
	public static boolean isShadersRunning()
	{
		try
		{
			if (isOculusLoaded())
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
			if (!hasErrored)
			{
				LOGGER.error("Failed to check if shaders are running:");
				e.printStackTrace();
				hasErrored = true;
			}
			return false;
		}
	}
	
	public static boolean isVivecraftLoaded()
	{
		return VIVECRAFT_LOADED || ModList.get().isLoaded("vivecraft");
	}
	
	public static boolean isOculusLoaded()
	{
		return ModList.get().isLoaded("oculus");
	}
	
	public static boolean isOptifineLoaded()
	{
		return OPTIFINE_LOADED;
	}
	
	public static boolean isSodiumLoaded()
	{
		return ModList.get().isLoaded("rubidium");
	}
}
