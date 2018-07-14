package main.java.com.jd.util;

import jd.core.CoreConstants;
import jd.core.model.classfile.constant.Constant;
import jd.core.model.classfile.constant.ConstantClass;
import jd.core.model.classfile.constant.ConstantConstant;
import jd.core.model.classfile.constant.ConstantUtf8;
import jd.core.process.deserializer.ClassFormatException;
import jd.core.util.StringConstants;
import main.java.com.jd.util.MessageConstant;
import java.io.*;

public class ClassFileUtil {

	private ClassFileUtil() {
	}

	/*
	 * rapid reading of the structure of the class and the name of the basic root
	 * directory extraction.
	 */
	public static String ExtractDirectoryPath(String pathToClass) throws Exception {
		DataInputStream dis = null;
		String directoryPath = null;

		try {
			/* verify java class */
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(pathToClass)));
			int magic = dis.readInt();
			if (magic != CoreConstants.JAVA_MAGIC_NUMBER)
				throw new ClassFormatException(MessageConstant.INVALID_JAVA_CLASS_FILE);

			/* ===================== File stream extract start ====================== */
			/* int minor_version = */
			dis.readUnsignedShort();
			/* int major_version = */
			dis.readUnsignedShort();

			Constant[] constants = DeserializeConstants(dis);

			/* int access_flags = */
			dis.readUnsignedShort();
			int this_class = dis.readUnsignedShort();

			Constant c = constants[this_class];
			if ((c == null) || (c.tag != ConstantConstant.CONSTANT_Class))
				throw new ClassFormatException(MessageConstant.INVALID_CONSTANT_POOL);

			c = constants[((ConstantClass) c).name_index];
			if ((c == null) || (c.tag != ConstantConstant.CONSTANT_Utf8))
				throw new ClassFormatException(MessageConstant.INVALID_CONSTANT_POOL);

			/* get the package.classname */
			String internalClassName = ((ConstantUtf8) c).bytes;
			/* ===================== File stream extract end ======================== */

			String pathSuffix = internalClassName.replace(StringConstants.INTERNAL_PACKAGE_SEPARATOR,
					File.separatorChar) + StringConstants.CLASS_FILE_SUFFIX;

			int index = pathToClass.indexOf(pathSuffix);

			if (index < 0)
				throw new ClassFormatException(MessageConstant.INVALID_INTERNAL_CLASS_NAME);

			directoryPath = pathToClass.substring(0, index);
		} finally {
			if (dis != null)
				try {
					dis.close();
				} catch (IOException e) {
				}
		}

		return directoryPath;
	}

	public static String ExtractInternalPath(String directoryPath, String pathToClass) {
		if ((directoryPath == null) || (pathToClass == null) || !pathToClass.startsWith(directoryPath))
			return null;

		String s = pathToClass.substring(directoryPath.length());

		return s.replace(File.separatorChar, StringConstants.INTERNAL_PACKAGE_SEPARATOR);
	}

	private static Constant[] DeserializeConstants(DataInputStream dis) throws IOException {
		int count = dis.readUnsignedShort();
		if (count == 0)
			return null;

		Constant[] constants = new Constant[count];

		for (int i = 1; i < count; i++) {
			byte tag = dis.readByte();

			switch (tag) {
			case ConstantConstant.CONSTANT_Class:
				constants[i] = new ConstantClass(tag, dis.readUnsignedShort());
				break;
			case ConstantConstant.CONSTANT_Utf8:
				constants[i] = new ConstantUtf8(tag, dis.readUTF());
				break;
			case ConstantConstant.CONSTANT_Long:
			case ConstantConstant.CONSTANT_Double:
				dis.readInt();
				dis.readInt();
				i++;
				break;
			case ConstantConstant.CONSTANT_Fieldref:
			case ConstantConstant.CONSTANT_Methodref:
			case ConstantConstant.CONSTANT_InterfaceMethodref:
			case ConstantConstant.CONSTANT_NameAndType:
				dis.readUnsignedShort();
				dis.readUnsignedShort();
				break;
			case ConstantConstant.CONSTANT_Integer:
			case ConstantConstant.CONSTANT_Float:
				dis.readInt();
				break;
			case ConstantConstant.CONSTANT_String:
				dis.readUnsignedShort();
				break;
			case 15:
			case 16:
			case 18:
				throw new IllegalArgumentException(MessageConstant.JD_GUI_NOT_SUPPORT);
			default:
				throw new ClassFormatException(MessageConstant.INVALID_CONSTANT_POOL);
			}
		}

		return constants;
	}

}
