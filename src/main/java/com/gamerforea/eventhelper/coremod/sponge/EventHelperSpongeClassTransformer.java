package com.gamerforea.eventhelper.coremod.sponge;

import com.gamerforea.eventhelper.coremod.CoreMod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.objectweb.asm.Opcodes.*;

public final class EventHelperSpongeClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("org.spongepowered.mod.event.SpongeModEventManager"))
		{
			byte[] bytes = transformSpongeModEventManager(basicClass);
			if (bytes == basicClass)
				CoreMod.LOGGER.warn("{} can't be transformed", transformedName);
			else
				CoreMod.LOGGER.debug("{} transformed", transformedName);
			return bytes;
		}

		return basicClass;
	}

	private static byte[] transformSpongeModEventManager(byte[] basicClass)
	{
		AtomicBoolean transformed = new AtomicBoolean();
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		new ClassReader(basicClass).accept(new ClassVisitor(ASM5, classWriter)
		{
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
			{
				MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);

				if (name.equals("isIgnoredEvent") && desc.equals("(Lorg/spongepowered/api/event/Event;)Z"))
					return new MethodVisitor(ASM5, methodVisitor)
					{
						@Override
						public void visitCode()
						{
							super.visitCode();

							Label elseLabel = new Label();
							this.visitVarInsn(ALOAD, (access & ACC_STATIC) == 0 ? 1 : 0);
							this.visitMethodInsn(INVOKESTATIC, SpongeMethodHooks.NAME, SpongeMethodHooks.IIE_NAME, SpongeMethodHooks.IIE_DESC, false);
							this.visitJumpInsn(IFEQ, elseLabel);
							this.visitInsn(ICONST_1);
							this.visitInsn(IRETURN);
							this.visitLabel(elseLabel);

							transformed.set(true);
						}

						@Override
						public void visitEnd()
						{
							super.visitEnd();
							CoreMod.LOGGER.debug("org/spongepowered/mod/event/SpongeModEventManager.{}{} transformed", name, desc);
						}
					};

				return methodVisitor;
			}
		}, 0);
		return transformed.get() ? classWriter.toByteArray() : basicClass;
	}
}
