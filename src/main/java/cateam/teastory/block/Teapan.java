package cateam.teastory.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cateam.teastory.achievement.AchievementLoader;
import cateam.teastory.creativetab.CreativeTabsLoader;
import cateam.teastory.item.ItemLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Teapan extends Block
{
	public Teapan()
	{
		super(Material.wood);     
		this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.setHardness(0.5F);
        this.setStepSound(soundTypeWood);
        this.setUnlocalizedName("teapan");
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.EMPTY));
        this.setCreativeTab(CreativeTabsLoader.tabteastory);
	}
	
	@Override
	public boolean isOpaqueCube()
	{
	    return false;
	}
	
	@Override
	public boolean isFullCube()
    {
        return false;
    }
	
	@Override
	public ArrayList getDrops(IBlockAccess world, BlockPos pos, IBlockState blockstate, int fortune)
	{
	    ArrayList drops = new ArrayList();
	    drops.add(new ItemStack(BlockLoader.teapan, 1));
	    int meta = BlockLoader.teapan.getMetaFromState(blockstate);
	    if (meta == 1)
	    {
    		drops.add(new ItemStack(ItemLoader.tea_leaf, 8));
    	}
	    else if (meta == 2)
    	    {
    	    	drops.add(new ItemStack(ItemLoader.dried_tea, 8));
		    }
	        else if (meta == 3)
            	{
            		drops.add(new ItemStack(ItemLoader.half_dried_tea, 8));
            	}
	            else if (meta == 4)
                    {
                    	drops.add(new ItemStack(ItemLoader.wet_tea, 8));
                    }
	    return drops;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
        int meta = getMetaFromState(worldIn.getBlockState(pos));
        if (worldIn.canLightningStrike(pos.up()) && meta > 0)
        {
        	worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(4));
        }
        else if (meta == 1 || meta == 3 || meta == 4)
       	{
            float f = getDryingChance(worldIn, pos);
            if (f == 0.0F)
            { 
              	return;
            }
            else if (rand.nextInt((int)(25.0F / f) + 1) == 0)      		
            {
            	switch (meta)
            	{
            	    case 1:
            	    	if (worldIn.canSeeSky(pos))
            	    	{
            	    	    worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(2));
            	    	}
            	    	else
            	    	{
            	    		worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(3));
            	    	}
            	    	return;
            	    case 3:
            	    	if (worldIn.canSeeSky(pos))
                            worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(2));
            	    	return;
            	    case 4:
            	    	worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(1));
            	    	return;	
            	}
            }
       	}
    }
	
	public static float getDryingChance(World worldIn, BlockPos pos)
    {
        float f;
        BiomeGenBase biome = worldIn.getBiomeGenForCoords(pos);
        boolean isDaytime = worldIn.getWorldTime() % 24000L < 12000L;
        float humidity = biome.rainfall;
        float temperature = biome.getFloatTemperature(pos);
        boolean isRaining = worldIn.isRaining();
        if (isRaining)
        {
        	return 0.0F;
        }
        if (isDaytime)
       	{
       		f = worldIn.getLight(pos.up()) * 0.07F;
       	}
       	else
       	{
       		f = worldIn.getLightFor(EnumSkyBlock.BLOCK, pos.up()) * 0.025F;
       	}
        f = (float)((double)f * ((double)humidity >= 0.2D ? (double)humidity >= 0.5D ? (double)humidity >= 0.8D ? 0.3D : 0.7D : 1.0D : 1.4D));
        f = (float)((double)f * ((double)temperature >= 0.15D ? (double)temperature >= 0.5D ? (double)temperature > 0.95D ? 1.3D : 0.9D : 0.5D : 0.1D));
        return f;
    }
	
	@Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
		((EntityPlayer) placer).triggerAchievement(AchievementLoader.teaBasket);
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
	    list.add(new ItemStack(itemIn, 1, 0));
	}
	
    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] { TYPE });
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(TYPE, getState(meta));
    }
    
    public Comparable getState(int meta)
    {
    	switch(meta)
    	{
    	case 1:
    		return EnumType.FULL;
    	case 2:
            return EnumType.DRIED;
    	case 3:
        	return EnumType.MATCHA;
    	case 4:
            return EnumType.WET;
        default:
            return EnumType.EMPTY;
    	}
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumType type = (EnumType) state.getValue(TYPE);
        return type.getID();
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
        	switch(getMetaFromState(worldIn.getBlockState(pos)))
        	{
        	    case 0:
        	    	if ((playerIn.getHeldItem() == null) || !(playerIn.getHeldItem().getItem() == ItemLoader.half_dried_tea && playerIn.getHeldItem().stackSize >=8) && !(playerIn.getHeldItem().getItem() == ItemLoader.tea_leaf && playerIn.getHeldItem().stackSize >=8) && !(playerIn.getHeldItem().getItem() == ItemLoader.wet_tea && playerIn.getHeldItem().stackSize >=8) && (Block.getBlockFromItem(playerIn.getHeldItem().getItem()) != BlockLoader.teapan))
        	    	{
        	    		playerIn.addChatMessage(new ChatComponentTranslation("teastory.teapan.message"));
        	    	}
        	    	return true;
        	    default:
        	    	return true;
        	}
        }
        else
        {
            switch(getMetaFromState(worldIn.getBlockState(pos)))
        	{
        	    case 1:
        	       	worldIn.setBlockState(pos, BlockLoader.teapan.getDefaultState());
        	        ItemStack itemstack1 = new ItemStack(ItemLoader.tea_leaf, 8);
        	        worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack1));
                    return true;
        	    case 2:
        	       	worldIn.setBlockState(pos, BlockLoader.teapan.getDefaultState());
                    ItemStack itemstack2 = new ItemStack(ItemLoader.dried_tea, 8);
                    worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack2));
        	        return true;
        	    case 3:
        	    	worldIn.setBlockState(pos, BlockLoader.teapan.getDefaultState());
        	    	playerIn.triggerAchievement(AchievementLoader.halfDriedTea);
                    ItemStack itemstack3 = new ItemStack(ItemLoader.half_dried_tea, 8);
                    worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack3));
                    return true;
        	    case 4:
        	    	worldIn.setBlockState(pos, BlockLoader.teapan.getDefaultState());
        	    	playerIn.triggerAchievement(AchievementLoader.wetTea);
                    ItemStack itemstack4 = new ItemStack(ItemLoader.wet_tea, 8);
                    worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack4));
                    return true;
                default:
                	if (playerIn.getHeldItem() != null)
                    {
                		if (playerIn.getHeldItem().stackSize >=8)
                		{
                	    	if (playerIn.getHeldItem().getItem() == ItemLoader.tea_leaf)
                    		{
                	                worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(1));
                	                if (!playerIn.capabilities.isCreativeMode)
                                    {
                	            	    playerIn.getHeldItem().stackSize = playerIn.getHeldItem().stackSize - 8;
                                    }
        	                        return true;
                	    	}
                			else if (playerIn.getHeldItem().getItem() == ItemLoader.wet_tea)
                 	      	{	
                		        worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(4));
                		        if (!playerIn.capabilities.isCreativeMode)
                                {
            	            	    playerIn.getHeldItem().stackSize = playerIn.getHeldItem().stackSize - 8;
                                }
               	                return true;
             	    	    }
                    		else if (playerIn.getHeldItem().getItem() == ItemLoader.half_dried_tea)
                        	{	
                    	        worldIn.setBlockState(pos, BlockLoader.teapan.getStateFromMeta(3));
                    	        if (!playerIn.capabilities.isCreativeMode)
                                {
            	            	    playerIn.getHeldItem().stackSize = playerIn.getHeldItem().stackSize - 8;
                                }
                                return true;
                    	    }
                    		else return false;
                		}
                		else return false;
            	    }
                	else return false;
        	}
        }
    }
	
	public static final PropertyEnum TYPE = PropertyEnum.create("type", Teapan.EnumType.class);
	
	public enum EnumType implements IStringSerializable
    {
		EMPTY(0, "empty"),
	    FULL(1, "full"),
		DRIED(2, "dried"),
		MATCHA(3, "matcha"),
		WET(4, "wet");

	    private int ID;
	    private String name;
	    
	    private EnumType(int ID, String name)
        {
	        this.ID = ID;
	        this.name = name;
	    }
	    
	    @Override
	    public String getName()
        {
	        return name;
	    }

	    public int getID()
        {
	        return ID;
	    }
	    
	    @Override
	    public String toString()
        {
	        return getName();
	    }
	}
}