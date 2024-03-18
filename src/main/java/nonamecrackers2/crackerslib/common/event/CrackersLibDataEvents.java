package nonamecrackers2.crackerslib.common.event;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import nonamecrackers2.crackerslib.common.data.CrackersLibLangProvider;

public class CrackersLibDataEvents
{
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeClient(), (DataProvider.Factory<CrackersLibLangProvider>)CrackersLibLangProvider::new);
	}
}
