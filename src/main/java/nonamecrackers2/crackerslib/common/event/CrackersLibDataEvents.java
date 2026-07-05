package nonamecrackers2.crackerslib.common.event;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import nonamecrackers2.crackerslib.common.data.CrackersLibLangProvider;

public class CrackersLibDataEvents
{
	public static void gatherClientData(GatherDataEvent.Client event)
	{
		DataGenerator generator = event.getGenerator();
		generator.addProvider(true, (DataProvider.Factory<CrackersLibLangProvider>)CrackersLibLangProvider::new);
	}
}
