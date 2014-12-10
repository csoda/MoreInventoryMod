package moreinventory.client.gui;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import moreinventory.core.MoreInventoryMod;
import moreinventory.util.ArrayListExtended;
import moreinventory.util.BlockMeta;
import moreinventory.util.BlockMetaFilter;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectTransportableChest extends GuiScreen
{
	private final GuiScreen parent;

	private ChestList chestList;
	private GuiButton doneButton;
	private GuiTextField filterTextField;

	public GuiSelectTransportableChest(GuiScreen parent)
	{
		this.parent = parent;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (chestList == null)
		{
			chestList = new ChestList();
		}

		chestList.func_148122_a(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 10;
		doneButton.yPosition = height - doneButton.height - 4;

		buttonList.clear();
		buttonList.add(doneButton);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}

					chestList.getSelected().clear();
					chestList.scrollToTop();
					break;
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		chestList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(MoreInventoryMod.CONFIG_LANG + "general.transportableChests.select"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (filterTextField.isFocused())
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				filterTextField.setFocused(false);
			}

			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				chestList.setFilter(null);
			}
			else if (changed || code == Keyboard.KEY_RETURN)
			{
				chestList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);

				if (parent == null)
				{
					mc.setIngameFocus();
				}
			}
			else if (code == Keyboard.KEY_BACK)
			{
				chestList.getSelected().clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++chestList.nameType > 1)
				{
					chestList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				chestList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				chestList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				chestList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				chestList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				chestList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				chestList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				chestList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				chestList.getSelected().clear();
				chestList.getSelected().addAll(chestList.getContents());
			}
		}
	}

	class ChestList extends GuiListSlot<BlockMeta>
	{
		private final ArrayListExtended<BlockMeta>
		chests = new ArrayListExtended(),
		contents = new ArrayListExtended();
		private final Set<BlockMeta> selected = Sets.newTreeSet(MIMUtils.blockMetaComparator);
		private final Map<String, List<BlockMeta>> filterCache = Maps.newHashMap();

		private int nameType;

		public ChestList()
		{
			super(GuiSelectTransportableChest.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			MIMUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					chests.clear();
					getContents().clear();
					getSelected().clear();
					filterCache.clear();

					List list = Lists.newArrayList();

					for (Object obj : GameData.getBlockRegistry())
					{
						try
						{
							if (obj == null || !(obj instanceof Block))
							{
								continue;
							}

							Block block = (Block)obj;
							CreativeTabs tab = block.getCreativeTabToDisplayOn();

							if (tab == null)
							{
								tab = CreativeTabs.tabAllSearch;
							}

							list.clear();
							block.getSubBlocks(Item.getItemFromBlock(block), tab, list);

							for (int i = 0; i < 15; ++i)
							{
								try
								{
									if (block.hasTileEntity(i))
									{
										TileEntity tile = block.createTileEntity(null, i);

										if (tile != null && tile instanceof IInventory)
										{
											chests.add(new BlockMeta(block, i));
										}
									}
								}
								catch (Throwable e) {}
							}
						}
						catch (Throwable e) {}
					}

					getContents().addAll(chests);
				}
			});
		}

		@Override
		protected ArrayListExtended<BlockMeta> getContents()
		{
			return contents;
		}

		@Override
		protected Set<BlockMeta> getSelected()
		{
			return selected;
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			BlockMeta entry = getContents().get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = new ItemStack(entry.block, 1, entry.meta);
			String name = null;

			try
			{
				if (itemstack.getItem() == null)
				{
					switch (nameType)
					{
						case 1:
							name = GameRegistry.findUniqueIdentifierFor(entry.block).toString();
							break;
						case 2:
							name = entry.block.getUnlocalizedName();
							name = name.substring(name.indexOf(".") + 1);
							break;
						default:
							name = entry.block.getLocalizedName();
							break;
					}
				}
				else
				{
					switch (nameType)
					{
						case 1:
							name = GameRegistry.findUniqueIdentifierFor(entry.block).toString() + ", " + itemstack.getItemDamage();
							break;
						case 2:
							name = itemstack.getUnlocalizedName();
							name = name.substring(name.indexOf(".") + 1);
							break;
						default:
							name = itemstack.getDisplayName();
							break;
					}
				}
			}
			catch (Throwable e) {}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			BlockMeta entry = getContents().get(index, null);

			if (entry != null && !getSelected().remove(entry))
			{
				// Single selection
				getSelected().clear();

				getSelected().add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			BlockMeta entry = getContents().get(index, null);

			return entry != null && getSelected().contains(entry);
		}

		protected void setFilter(final String filter)
		{
			MIMUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<BlockMeta> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = chests;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(getSelected());
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(chests, new BlockMetaFilter(filter))));
						}

						result = filterCache.get(filter);
					}

					if (!getContents().equals(result))
					{
						getContents().clear();
						getContents().addAll(result);
					}
				}
			});
		}
	}
}