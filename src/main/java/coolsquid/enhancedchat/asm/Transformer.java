package coolsquid.enhancedchat.asm;

import java.util.Map;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import coolsquid.enhancedchat.config.ConfigManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions("coolsquid.enhancedchat.asm")
public class Transformer implements IFMLLoadingPlugin, IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.equals("net.minecraft.util.ChatAllowedCharacters")) {
			ClassNode c = createClassNode(bytes);
			MethodNode m = getMethod(c, "isAllowedCharacter", "(C)Z", "a", "(C)Z");
			InsnList l = new InsnList();
			l.add(new VarInsnNode(Opcodes.ILOAD, 0));
			l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class),
					"isAllowedCharacter", "(C)Z", false));
			l.add(new InsnNode(Opcodes.IRETURN));
			m.instructions.insertBefore(m.instructions.getFirst(), l);
			return toBytes(c);
		} else if (transformedName.equals("net.minecraft.client.gui.GuiNewChat")) {
			ClassNode c = createClassNode(bytes);
			MethodNode m = getMethod(c, "setChatLine", "(Lnet/minecraft/util/text/ITextComponent;IIZ)V", "a",
					"(Lhh;IIZ)V");
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode n = m.instructions.get(i);
				if (n.getOpcode() == Opcodes.BIPUSH && n instanceof IntInsnNode && ((IntInsnNode) n).operand == 100) {
					m.instructions.insert(n, new FieldInsnNode(Opcodes.GETSTATIC,
							Type.getInternalName(ConfigManager.class), "chatHistoryLimit", "I"));
					m.instructions.remove(n);
				}
			}
			return toBytes(c);
		}
		return bytes;
	}

	private MethodNode getMethod(ClassNode c, String name, String desc, String obfName, String obfDesc) {
		for (MethodNode m: c.methods) {
			if ((m.name.equals(name) || m.name.equals(obfName)) && (m.desc.equals(desc) || m.desc.equals(obfDesc))) {
				return m;
			}
		}
		return null;
	}

	private static ClassNode createClassNode(byte[] bytes) {
		ClassNode c = new ClassNode();
		ClassReader r = new ClassReader(bytes);
		r.accept(c, ClassReader.EXPAND_FRAMES);
		return c;
	}

	private static byte[] toBytes(ClassNode c) {
		ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		c.accept(w);
		return w.toByteArray();
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { Transformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}