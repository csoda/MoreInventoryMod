package moreinventory.client.config;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.config.GuiTransportableBlocks.BlockEntry;
import moreinventory.client.gui.GuiListSlot;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

@SideOnly(Side.CLIENT)
public class GuiSelectTransportableBlock extends GuiScreen
{
	private final GuiScreen parent;
	private GuiTextField blockField;
	private GuiTextField blockMetaField;

	private BlockList blockList;
	private GuiButton doneButton;
	private GuiTextField filterTextField;

	public GuiSelectTransportableBlock(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectTransportableBlock(GuiScreen parent, GuiTextField blockField, GuiTextField blockMetaField)
	{
		this(parent);
		this.blockField = blockField;
		this.blockMetaField = blockMetaField;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (blockList == null)
		{
			blockList = new BlockList();
		}

		blockList.func_148122_a(width, height, 32, height - 28);

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
					if (blockList.selected != null)
					{
						if (parent != null && parent instanceof GuiTransportableBlocks)
						{
							GuiTransportableBlocks gui = (GuiTransportableBlocks)parent;

							if (!gui.editMode)
							{
								BlockEntry entry = gui.new BlockEntry(new BlockMeta(blockList.selected.block, blockList.selected.meta), 19);

								gui.blockField.setText(MIMUtils.getUniqueName(blockList.selected.block));
								gui.blockMetaField.setText(Integer.toString(blockList.selected.meta));
								gui.blockList.blocks.addIfAbsent(entry);
								gui.blockList.contents.addIfAbsent(entry);
								gui.blockList.selected = entry;
								gui.editMode = true;
								gui.initGui();

								mc.displayGuiScreen(new GuiSelectTransportableIcon(gui, gui.iconField));
								return;
							}
						}

						if (blockField != null)
						{
							blockField.setText(MIMUtils.getUniqueName(blockList.selected.block));
						}

						if (blockMetaField != null)
						{
							blockMetaField.setText(Integer.toString(blockList.selected.meta));
						}
					}

					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}

					blockList.scrollToTop();
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
		blockList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(MoreInventoryMod.CONFIG_LANG + "general.transportableBlocks.select"), width / 2, 15, 0xFFFFFF);

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
			boolean changed = !text.equals(prev);

			if (Strings.isNullOrEmpty(text) && changed)
			{
				blockList.setFilter(null);
			}
			else if (changed || code == Keyboard.KEY_RETURN)
			{
				blockList.setFilter(text);
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
				blockList.selected = null;
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++blockList.nameType > 1)
				{
					blockList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				blockList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				blockList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				blockList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				blockList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				blockList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				blockList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
		}
	}

	class BlockList extends GuiListSlot<BlockMeta>
	{
		private final ArrayListExtended<BlockMeta>
		blocks = new ArrayListExtended<>(),
		contents = new ArrayListExtended<>();
		private final Map<String, List<BlockMeta>> filterCache = Maps.newHashMap();

		private int nameType;
		private BlockMeta selected;

		public BlockList()
		{
			super(GuiSelectTransportableBlock.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			MIMUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					blocks.clear();
					getContents().clear();
					filterCache.clear();
					selected = null;

					List list = Lists.newArrayList();
					Set<String> con = Sets.newHashSet();

					if (parent != null && parent instanceof GuiTransportableBlocks)
					{
						for (BlockEntry entry : ((GuiTransportableBlocks)parent).blockList.blocks)
						{
							con.add(entry.blockMeta.toString());
						}
					}

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

							for (Object item : list)
							{
								try
								{
									if (item != null && item instanceof ItemStack)
									{
										ItemStack itemstack = (ItemStack)item;
										block = Block.getBlockFromItem(itemstack.getItem());
										int meta = itemstack.getItemDamage();

										if (block.hasTileEntity(meta))
										{
											TileEntity tile = block.createTileEntity(null, meta);

											if (tile != null && tile instanceof IInventory)
											{
												String name = MIMUtils.getUniqueName(block);

												if (!con.contains(name + "@" + meta) && !con.contains(name + "@-1"))
												{
													blocks.addIfAbsent(new BlockMeta(block, meta));
												}
											}
										}
									}
								}
								catch (Throwable ignored) {}
							}
						}
						catch (Throwable ignored) {}
					}

					getContents().addAll(blocks);
				}
			});
		}

		@Override
		protected ArrayListExtended<BlockMeta> getContents()
		{
			return contents;
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
							name = MIMUtils.getUniqueName(entry.block);
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
							name = MIMUtils.getUniqueName(entry.block) + ", " + itemstack.getItemDamage();
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
			catch (Throwable ignored) {}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			selected = getContents().get(index, null);
		}

		@Override
		protected boolean isSelected(int index)
		{
			BlockMeta entry = getContents().get(index, null);

			return entry != null && selected == entry;
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
						result = blocks;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(blocks, new BlockMetaFilter(filter))));
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