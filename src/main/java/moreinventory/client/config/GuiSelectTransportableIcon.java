package moreinventory.client.config;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.gui.GuiListSlot;
import moreinventory.core.MoreInventoryMod;
import moreinventory.util.ArrayListExtended;
import moreinventory.util.MIMUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

@SideOnly(Side.CLIENT)
public class GuiSelectTransportableIcon extends GuiScreen
{
	private final GuiScreen parent;
	private GuiTextField iconField;

	private IconList iconList;
	private GuiButton doneButton;
	private GuiTextField filterTextField;

	public GuiSelectTransportableIcon(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectTransportableIcon(GuiScreen parent, GuiTextField iconField)
	{
		this(parent);
		this.iconField = iconField;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (iconList == null)
		{
			iconList = new IconList();
		}

		iconList.func_148122_a(width, height, 32, height - 28);

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
					if (iconList.selected != null && iconField != null)
					{
						iconField.setText(Integer.toString(iconList.selected));
					}

					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}

					iconList.scrollToTop();
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
		iconList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(MoreInventoryMod.CONFIG_LANG + "general.transportableBlocks.select.icon"), width / 2, 15, 0xFFFFFF);

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
			if (!CharUtils.isAsciiControl(c) && !CharUtils.isAsciiNumeric(c))
			{
				return;
			}

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
				iconList.setFilter(null);
			}
			else if (changed || code == Keyboard.KEY_RETURN)
			{
				iconList.setFilter(text);
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
				iconList.selected = null;
			}
			else if (code == Keyboard.KEY_UP)
			{
				iconList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				iconList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				iconList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				iconList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				iconList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				iconList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
		}
	}

	class IconList extends GuiListSlot<Integer>
	{
		private final ArrayListExtended<Integer>
		icons = new ArrayListExtended<>(),
		contents = new ArrayListExtended<>();
		private final Map<String, List<Integer>> filterCache = Maps.newHashMap();

		private Integer selected;

		public IconList()
		{
			super(GuiSelectTransportableIcon.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			icons.clear();
			getContents().clear();
			filterCache.clear();
			selected = null;

			for (int i = 0; i < 30; ++i)
			{
				icons.add(i);
			}

			getContents().addAll(icons);
		}

		@Override
		protected ArrayListExtended<Integer> getContents()
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
			Integer entry = getContents().get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = new ItemStack(MoreInventoryMod.transporter, 1, entry + 21);

			try
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 0.0F, 32.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				GL11.glEnable(GL11.GL_LIGHTING);
				RenderHelper.enableGUIStandardItemLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				itemRender.zLevel += 100.0F;
				itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), itemstack, width / 2 - 8, par3 - 2);
				itemRender.zLevel -= 100.0F;
				GL11.glPopMatrix();
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			catch (Throwable ignored) {}

			drawString(fontRendererObj, Integer.toString(entry), width / 2 - 100, par3 + 1, 0xE0E0E0);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			selected = getContents().get(index, null);
		}

		@Override
		protected boolean isSelected(int index)
		{
			Integer entry = getContents().get(index, null);

			return entry != null && selected == entry;
		}

		protected void setFilter(final String filter)
		{
			MIMUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<Integer> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = icons;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(icons, new Predicate<Integer>()
							{
								@Override
								public boolean apply(Integer i)
								{
									return i == NumberUtils.toInt(filter, -1);
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
	}
}