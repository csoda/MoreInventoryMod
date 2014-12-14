package moreinventory.client.config;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.client.config.GuiButtonExt;
import moreinventory.client.gui.GuiListSlot;
import moreinventory.client.gui.GuiSelectTransportableChest;
import moreinventory.core.MoreInventoryMod;
import moreinventory.util.ArrayListExtended;
import moreinventory.util.BlockMeta;
import moreinventory.util.MIMUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public class GuiTransportableChests extends GuiScreen
{
	private final GuiScreen parent;

	private ChestList chestList;
	private GuiButton doneButton, editButton, cancelButton, addButton, removeButton, clearButton;
	private GuiTextField filterTextField;

	private boolean editMode;
	private GuiTextField blockField, blockMetaField, iconField;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	public GuiTransportableChests(GuiScreen parent)
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

		chestList.func_148122_a(width, height, 32, height - (editMode ? 100 : 28));

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 65, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 135;
		doneButton.yPosition = height - doneButton.height - 4;

		if (editButton == null)
		{
			editButton = new GuiButtonExt(1, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.edit"));
			editButton.enabled = false;
		}

		editButton.xPosition = doneButton.xPosition - doneButton.width - 3;
		editButton.yPosition = doneButton.yPosition;
		editButton.enabled = !chestList.selected.isEmpty();
		editButton.visible = !editMode;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(2, 0, 0, editButton.width, editButton.height, I18n.format("gui.cancel"));
		}

		cancelButton.xPosition = editButton.xPosition;
		cancelButton.yPosition = editButton.yPosition;
		cancelButton.visible = editMode;

		if (removeButton == null)
		{
			removeButton = new GuiButtonExt(4, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.remove"));
		}

		removeButton.xPosition = editButton.xPosition - editButton.width - 3;
		removeButton.yPosition = doneButton.yPosition;
		removeButton.visible =  !editMode;

		if (addButton == null)
		{
			addButton = new GuiButtonExt(3, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.add"));
		}

		addButton.xPosition = removeButton.xPosition - removeButton.width - 3;
		addButton.yPosition = doneButton.yPosition;
		addButton.visible = !editMode;

		if (clearButton == null)
		{
			clearButton = new GuiButtonExt(5, 0, 0, removeButton.width, removeButton.height, I18n.format("gui.clear"));
		}

		clearButton.xPosition = removeButton.xPosition;
		clearButton.yPosition = removeButton.yPosition;
		clearButton.visible = false;

		buttonList.clear();
		buttonList.add(doneButton);

		if (editMode)
		{
			buttonList.add(cancelButton);
		}
		else
		{
			buttonList.add(editButton);
			buttonList.add(addButton);
			buttonList.add(removeButton);
			buttonList.add(clearButton);
		}

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 122, 16);
			filterTextField.setMaxStringLength(500);
		}

		filterTextField.xPosition = width / 2 - 200;
		filterTextField.yPosition = height - filterTextField.height - 6;

		editLabelList.clear();
		editLabelList.add(I18n.format("moreinv.config.edit.block"));
		editLabelList.add("");
		editLabelList.add(I18n.format("moreinv.config.edit.iconIndex"));

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRendererObj.getStringWidth(key));
		}

		if (blockField == null)
		{
			blockField = new GuiTextField(fontRendererObj, 0, 0, 0, 15);
			blockField.setMaxStringLength(100);
		}

		int i = maxLabelWidth + 8 + width / 2;
		blockField.xPosition = width / 2 - i / 2 + maxLabelWidth + 10;
		blockField.yPosition = chestList.bottom + 5;
		int fieldWidth = width / 2 + i / 2 - 45 - blockField.xPosition + 40;
		blockField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		if (blockMetaField == null)
		{
			blockMetaField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			blockMetaField.setMaxStringLength(2);
		}

		blockMetaField.xPosition = blockField.xPosition + blockField.width + 3;
		blockMetaField.yPosition = blockField.yPosition;
		blockMetaField.width = fieldWidth / 4 - 1;

		if (iconField == null)
		{
			iconField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			iconField.setMaxStringLength(2);
		}

		iconField.xPosition = blockField.xPosition;
		iconField.yPosition = blockField.yPosition + blockField.height + 5;
		iconField.width = fieldWidth;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(blockField);
			editFieldList.add(blockMetaField);
			editFieldList.add(iconField);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (!button.enabled)
		{
			return;
		}

		switch (button.id)
		{
			case 0:
				if (editMode)
				{
					for (final ChestEntry entry : chestList.selected)
					{
						MIMUtils.getPool().execute(new RecursiveAction()
						{
							@Override
							protected void compute()
							{
								if (!Strings.isNullOrEmpty(blockField.getText()))
								{
									entry.blockMeta = new BlockMeta(blockField.getText(), NumberUtils.toInt(blockMetaField.getText()));
								}

								if (!Strings.isNullOrEmpty(iconField.getText()))
								{
									entry.iconIndex = NumberUtils.toInt(iconField.getText(), 19);
								}
							}
						});
					}

					actionPerformed(cancelButton);

					chestList.scrollToTop();
					chestList.scrollToSelected();
				}
				else
				{
					actionPerformed(cancelButton);

					chestList.selected.clear();
					chestList.scrollToTop();
				}

				break;
			case 1:
				if (editMode)
				{
					actionPerformed(cancelButton);
				}
				else
				{
					editMode = true;
					initGui();

					chestList.scrollToTop();
					chestList.scrollToSelected();

					if (chestList.selected.size() == 1)
					{
						ChestEntry entry = chestList.selected.iterator().next();

						blockField.setText(MIMUtils.getUniqueName(entry.blockMeta.block));
						blockMetaField.setText(Integer.toString(entry.blockMeta.meta));
						iconField.setText(Integer.toString(entry.iconIndex));
					}
					else
					{
						blockField.setText("");
						blockMetaField.setText("");
						iconField.setText("");
					}
				}

				break;
			case 2:
				if (editMode)
				{
					editMode = false;
					initGui();
				}
				else
				{
					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}
				}

				break;
			case 3:
				mc.displayGuiScreen(new GuiSelectTransportableChest(this));
				break;
			case 4:
				MIMUtils.getPool().execute(new RecursiveAction()
				{
					@Override
					protected void compute()
					{
						for (ChestEntry entry : chestList.selected)
						{
							chestList.chests.remove(entry);
							chestList.contents.remove(entry);
						}

						chestList.selected.clear();
					}
				});

				break;
			case 5:
				chestList.selected.addAll(chestList.chests);
				actionPerformed(removeButton);
				break;
		}
	}

	@Override
	public void updateScreen()
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				textField.updateCursorCounter();
			}
		}
		else
		{
			editButton.enabled = !chestList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		chestList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(MoreInventoryMod.CONFIG_LANG + "general.transportableChests"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			GuiTextField textField;

			for (int i = 0; i < editFieldList.size(); ++i)
			{
				textField = editFieldList.get(i);
				textField.drawTextBox();
				drawString(fontRendererObj, editLabelList.get(i), textField.xPosition - maxLabelWidth - 10, textField.yPosition + 3, 0xBBBBBB);
			}
		}
		else
		{
			filterTextField.drawTextBox();
		}
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();

		if (blockMetaField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				blockMetaField.setText(Integer.toString(Math.max(NumberUtils.toInt(blockMetaField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				blockMetaField.setText(Integer.toString(Math.min(NumberUtils.toInt(blockMetaField.getText()) + 1, 15)));
			}
		}
		else if (iconField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				iconField.setText(Integer.toString(Math.max(NumberUtils.toInt(iconField.getText()) - 1, 1)));
			}
			else if (i > 0)
			{
				iconField.setText(Integer.toString(Math.min(NumberUtils.toInt(iconField.getText()) + 1, 30)));
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				textField.mouseClicked(x, y, code);
			}

			if (!isShiftKeyDown() && blockField.isFocused())
			{
				blockField.setFocused(false);

				mc.displayGuiScreen(new GuiSelectTransportableChest(this, blockField, blockMetaField));
			}
		}
		else
		{
			filterTextField.mouseClicked(x, y, code);
		}
	}

	@Override
	public void handleKeyboardInput()
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT)
		{
			clearButton.visible = !editMode && Keyboard.getEventKeyState();
		}
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

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
	}

	class ChestEntry
	{
		BlockMeta blockMeta;
		int iconIndex;

		public ChestEntry(BlockMeta blockMeta, int icon)
		{
			this.blockMeta = blockMeta;
			this.iconIndex = icon;
		}
	}

	class ChestList extends GuiListSlot<ChestEntry> implements Comparator<ChestEntry>
	{
		private final ArrayListExtended<ChestEntry>
		chests = new ArrayListExtended<>(),
		contents = new ArrayListExtended<>();
		private final Set<ChestEntry> selected = Sets.newTreeSet(this);
		private final Map<String, List<ChestEntry>> filterCache = Maps.newHashMap();

		private int nameType;

		public ChestList()
		{
			super(GuiTransportableChests.this.mc, 0, 0, 0, 0, 22);
		}

		@Override
		protected ArrayListExtended<ChestEntry> getContents()
		{
			return contents;
		}

		@Override
		protected Set<ChestEntry> getSelected()
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
			ChestEntry entry = getContents().get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = new ItemStack(entry.blockMeta.block, 1, entry.blockMeta.meta);
			String name = null;

			try
			{
				if (itemstack.getItem() == null)
				{
					switch (nameType)
					{
						case 1:
							name = MIMUtils.getUniqueName(entry.blockMeta.block);
							break;
						case 2:
							name = entry.blockMeta.block.getUnlocalizedName();
							name = name.substring(name.indexOf(".") + 1);
							break;
						default:
							name = entry.blockMeta.block.getLocalizedName();
							break;
					}
				}
				else
				{
					switch (nameType)
					{
						case 1:
							name = MIMUtils.getUniqueName(entry.blockMeta.block) + ", " + itemstack.getItemDamage();
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
			ChestEntry entry = getContents().get(index, null);

			if (entry != null && !getSelected().remove(entry))
			{
				getSelected().add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			ChestEntry entry = getContents().get(index, null);

			return entry != null && getSelected().contains(entry);
		}

		protected void setFilter(final String filter)
		{
			MIMUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<ChestEntry> result;

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
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(chests, new Predicate<ChestEntry>()
							{
								@Override
								public boolean apply(ChestEntry entry)
								{
									return MIMUtils.blockMetaFilter(entry.blockMeta, filter);
								}
							})));
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

		@Override
		public int compare(ChestEntry o1, ChestEntry o2)
		{
			int i = MIMUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = MIMUtils.blockMetaComparator.compare(o1.blockMeta, o2.blockMeta);

				if (i == 0)
				{
					i = Integer.compare(o1.iconIndex, o2.iconIndex);
				}
			}

			return i;
		}
	}
}