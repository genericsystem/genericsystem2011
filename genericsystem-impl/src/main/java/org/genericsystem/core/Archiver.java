package org.genericsystem.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.OverlappingFileLockException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Archiver {

	private static final Logger log = LoggerFactory.getLogger(Archiver.class);

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private Engine engine;
	private File directory;

	private FileOutputStream lockFile;

	private File formalFile;
	private File contentFile;

	private Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();

	public Archiver(Engine engine, String directoryPath) {
		this.engine = engine;
		prepareAndLockDirectory(directoryPath);
		ZipArchiver zipArchiver = decompress();
		if (zipArchiver != null)
			new SnapshotLoader(zipArchiver).loadSnapshot();
		else
			((EngineImpl) engine).restoreEngine();
	}

	private void prepareAndLockDirectory(String directoryPath) {
		if (directoryPath == null)
			return;
		File directory = new File(directoryPath);
		if (directory.exists()) {
			if (!directory.isDirectory())
				throw new IllegalStateException("Datasource path : " + directoryPath + " is not a directory");
		} else if (!directory.mkdirs())
			throw new IllegalStateException("Can't make directory : " + directoryPath);
		try {
			lockFile = new FileOutputStream(directoryPath + File.separator + Statics.LOCK_FILE_NAME);
			lockFile.getChannel().tryLock();
			this.directory = directory;
		} catch (OverlappingFileLockException e) {
			throw new IllegalStateException("Locked directory : " + directoryPath);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private ZipArchiver decompress() {
		if (lockFile != null) {
			NavigableMap<Long, File> snapshotsMap = snapshotsMap();
			if (!snapshotsMap.isEmpty())
				try {
					ZipArchiver zipArchiver = new ZipArchiver();
					zipArchiver.decompress(directory.getAbsolutePath(), Statics.getFilename(snapshotsMap.lastKey()));
					return zipArchiver;
				} catch (IOException e) {
					return null;
				}
		}
		return null;
	}

	private NavigableMap<Long, File> snapshotsMap() {
		NavigableMap<Long, File> snapshotsMap = new TreeMap<Long, File>();
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			if (!file.isDirectory()) {
				if (filename.endsWith(".content"))
					filename = filename.substring(0, filename.length() - ".content".length());
				if (filename.endsWith(".formal"))
					filename = filename.substring(0, filename.length() - ".formal".length());
				if (filename.matches(Statics.MATCHING_REGEX))
					try {
						snapshotsMap.put(getTimestamp(filename), file);
					} catch (ParseException pe) {
						throw new IllegalStateException(pe);
					}
			}
		}
		return snapshotsMap;
	}

	private void doSnapshot(Engine engine) {
		log.info("WRITE");
		long ts = engine.pickNewTs();
		String fileName = Statics.getFilename(ts);
		formalFile = new File(directory.getAbsolutePath() + File.separator + fileName + Statics.FORMAL_EXTENSION);
		contentFile = new File(directory.getAbsolutePath() + File.separator + fileName + Statics.CONTENT_EXTENSION);
		saveSnapshot(new Transaction(engine));
		// log.info("START COMPRESS");
		// compressTemporarySnapshot(fileName);
		// log.info("END COMPRESS");
		// formalFile.delete();
		// contentFile.delete();
		// log.info("START CONFIRM");
		// confirmTemporarySnapshot(fileName);
		// log.info("END CONFIRM");
	}

	private void saveSnapshot(AbstractContext context) {
		try {
			ObjectOutputStream formalObjectOutput = new ObjectOutputStream(buildNewTemporaryFormal(formalFile));
			ObjectOutputStream contentObjectOutput = new ObjectOutputStream(buildNewTemporaryContent(contentFile));
			NavigableSet<Generic> orderGenerics = context.orderDependencies(context.getEngine());
			for (Generic orderGeneric : orderGenerics)
				writeGeneric(((GenericImpl) orderGeneric), formalObjectOutput, contentObjectOutput);
			contentObjectOutput.flush();
			contentObjectOutput.close();
			formalObjectOutput.flush();
			formalObjectOutput.close();
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		}
	}

	private void compressTemporarySnapshot(String fileName) {
		ZipArchiver.compress(directory.getAbsolutePath(), fileName, formalFile, contentFile);
	}

	private void confirmTemporarySnapshot(String fileName) {
		ZipArchiver.confirm(directory.getAbsolutePath(), fileName);
		manageOldSnapshots();
	}

	// todo ici write

	private OutputStream buildNewTemporaryFormal(File formalFile) {
		try {
			return new FileOutputStream(formalFile);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private OutputStream buildNewTemporaryContent(File contentFile) {
		try {
			return new FileOutputStream(contentFile);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private void manageOldSnapshots() {
		NavigableMap<Long, File> snapshotsMap = snapshotsMap();
		long lastTs = snapshotsMap.lastKey();
		long firstTs = snapshotsMap.firstKey();
		long ts = firstTs;
		for (long snapshotTs : new TreeSet<Long>(snapshotsMap.keySet()))
			if (snapshotTs != lastTs && snapshotTs != firstTs)
				if ((snapshotTs - ts) < minInterval((lastTs - snapshotTs)))
					removeSnapshot(snapshotsMap, snapshotTs);
				else
					ts = snapshotTs;
	}

	private static long minInterval(long periodNumber) {
		return (long) Math.floor(periodNumber / Statics.ARCHIVER_COEFF);
	}

	private static void removeSnapshot(NavigableMap<Long, File> snapshotsMap, long ts) {
		snapshotsMap.get(ts).delete();
		snapshotsMap.remove(ts);
	}

	private static long getTimestamp(final String filename) throws ParseException {
		return Long.parseLong(filename.substring(filename.lastIndexOf("---") + 3));
	}

	public void close() {
		if (lockFile != null)
			try {
				scheduler.shutdown();
				doSnapshot(engine);
				lockFile.close();
				lockFile = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
	}

	public void startScheduler() {
		if (lockFile != null)
			if (Statics.SNAPSHOTS_PERIOD > 0L) {
				scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						doSnapshot(engine);
					}
				}, Statics.SESSION_TIMEOUT, Statics.SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
			}
	}

	private void writeGeneric(GenericImpl generic, ObjectOutputStream formalObjectOutput, ObjectOutputStream contentObjectOutput) throws IOException {
		writeTs(generic, formalObjectOutput);
		contentObjectOutput.writeLong(generic.homeTreeNode.ts);
		contentObjectOutput.writeObject(generic.homeTreeNode.value);
		formalObjectOutput.writeInt(generic.getMetaLevel());
		if (!homeTreeMap.containsKey(generic.homeTreeNode.ts)) {
			contentObjectOutput.writeLong(generic.homeTreeNode.metaNode.ts);
			homeTreeMap.put(generic.homeTreeNode.ts, generic.homeTreeNode);
		}
		if (generic.isEngine())
			return;
		writeAncestors(generic.getSupers(), formalObjectOutput);
		writeAncestors(generic.getComponents(), formalObjectOutput);
		formalObjectOutput.writeObject(GenericImpl.class.equals(generic.getClass()) ? null : generic.getClass());
	}

	private static void writeTs(Generic generic, ObjectOutputStream formalObjectOutput) throws IOException {
		formalObjectOutput.writeLong(((GenericImpl) generic).getDesignTs());
		log.info("write " + generic + " " + ((GenericImpl) generic).getDesignTs());
		formalObjectOutput.writeLong(((GenericImpl) generic).getBirthTs());
		formalObjectOutput.writeLong(((GenericImpl) generic).getLastReadTs());
		formalObjectOutput.writeLong(((GenericImpl) generic).getDeathTs());
	}

	private static void writeAncestors(Snapshot<Generic> dependencies, ObjectOutputStream formalObjectOutput) throws IOException {
		formalObjectOutput.writeInt(dependencies.size());
		for (Generic dependency : dependencies)
			formalObjectOutput.writeLong(((GenericImpl) dependency).getDesignTs());
	}

	private class SnapshotLoader extends HashMap<Long, Generic> {
		private static final long serialVersionUID = 3139276947667714316L;

		private ZipArchiver zipArchiver;
		private ObjectInputStream formalInputStream;
		private ObjectInputStream contentInputStream;

		private SnapshotLoader(ZipArchiver zipArchiver) {
			this.zipArchiver = zipArchiver;
			try {
				this.formalInputStream = new ObjectInputStream(zipArchiver.getFormalInputStream());
				this.contentInputStream = new ObjectInputStream(zipArchiver.getContentInputStream());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			homeTreeMap = new HashMap<>();
		}

		private void loadSnapshot() {
			log.info("LOAD");
			try {
				Engine engine = loadEngine();
				for (;;)
					loadGeneric(engine);
			} catch (EOFException ignore) {
				// zipArchiver.formalFile.delete();
				// zipArchiver.contentFile.delete();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		private Engine loadEngine() throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			Serializable value = (Serializable) contentInputStream.readObject();
			int metaLevel = formalInputStream.readInt();
			long metaNodeTS = contentInputStream.readLong();
			((EngineImpl) engine).restoreEngine(homeTreeNodeTs, ts[0], ts[1], ts[2], ts[3]);
			put(ts[0], engine);
			homeTreeMap.put(homeTreeNodeTs, ((EngineImpl) engine).homeTreeNode);
			assert engine.getValue().equals(value);
			assert engine.getMetaLevel() == metaLevel;
			return engine;
		}

		private void loadGeneric(Engine engine) throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			Serializable homeTreeNodeValue = (Serializable) contentInputStream.readObject();
			int metaLevel = formalInputStream.readInt();
			HomeTreeNode homeTreeNode = null;
			// Serializable homeTreeNodeValue = null;
			HomeTreeNode metaNode = null;
			if (homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeNode = homeTreeMap.get(homeTreeNodeTs);
			else {
				// homeTreeNodeValue = (Serializable) contentInputStream.readObject();
				metaNode = homeTreeMap.get(contentInputStream.readLong());
			}
			Generic[] supers = loadAncestors(formalInputStream);
			Generic[] components = loadAncestors(formalInputStream);
			Generic generic = engine.getFactory().newGeneric((Class<?>) formalInputStream.readObject());
			GenericImpl plug;
			if (homeTreeMap.containsKey(homeTreeNodeTs))
				plug = ((GenericImpl) generic).restore(homeTreeNode, ts[0], ts[1], ts[2], ts[3], supers, components).plug();
			else {
				plug = ((GenericImpl) generic).restore(homeTreeNodeTs, homeTreeNodeValue, metaNode, ts[0], ts[1], ts[2], ts[3], supers, components).plug();
				homeTreeMap.put(homeTreeNodeTs, plug.homeTreeNode);
			}
			put(ts[0], plug);
			log.info("LOAD generic " + ts[0]);
			assert homeTreeNodeValue.equals(plug.getValue()) : homeTreeNodeValue;
			assert plug.getMetaLevel() == metaLevel;
		}

		private Generic[] loadAncestors(ObjectInputStream in) throws IOException {
			int length = in.readInt();
			Generic[] ancestors = new Generic[length];
			for (int index = 0; index < length; index++)
				ancestors[index] = get(in.readLong());
			return ancestors;
		}

		private long[] loadTs(ObjectInputStream in) throws IOException {
			long[] ts = new long[4];
			ts[0] = in.readLong(); // designTs
			ts[1] = in.readLong(); // birthTs
			ts[2] = in.readLong(); // lastReadTs
			ts[3] = in.readLong(); // deathTs
			return ts;
		}

	}

	// private static class SnapshotZipOutputStream extends OutputStream {
	//
	// private ZipOutputStream zipOutputStream;
	//
	// private SnapshotZipOutputStream(String directoryPath, String fileName) throws FileNotFoundException {
	// zipOutputStream = new ZipOutputStream(new FileOutputStream(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION));
	// ZipEntry zipEntry = new ZipEntry(fileName + Statics.SNAPSHOT_EXTENSION);
	// try {
	// zipOutputStream.putNextEntry(zipEntry);
	// // TODO second file
	// zipOutputStream.putNextEntry(new ZipEntry(fileName + Statics.SNAPSHOT_EXTENSION + "2"));
	// } catch (IOException e) {
	// throw new IllegalStateException(e);
	// }
	// }
	//
	// @Override
	// public void write(int b) throws IOException {
	// zipOutputStream.write(b);
	// }
	//
	// @Override
	// public void flush() throws IOException {
	// zipOutputStream.flush();
	// }
	//
	// @Override
	// public void close() throws IOException {
	// zipOutputStream.close();
	// }
	//
	// }

	// private static class SnapshotZipInputStream extends InputStream {
	//
	// private ZipInputStream zipInputStream;
	//
	// private SnapshotZipInputStream(String directoryPath, String fileName) throws FileNotFoundException {
	// zipInputStream = new ZipInputStream(new FileInputStream(new File(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION)));
	// try {
	// zipInputStream.getNextEntry();
	// } catch (IOException e) {
	// throw new IllegalStateException(e);
	// }
	// }
	//
	// @Override
	// public int read() throws IOException {
	// return zipInputStream.read();
	// }
	//
	// @Override
	// public void close() throws IOException {
	// zipInputStream.close();
	// }
	// }

	private static class ZipArchiver {

		private File formalFile;
		private File contentFile;

		public static void compress(String directoryPath, String fileName, File formalFile, File contentFile) {
			try {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION));
				zos.putNextEntry(new ZipEntry(formalFile.getName()));

				FileInputStream fileInputStream = new FileInputStream(formalFile);
				try {
					for (;;)
						zos.write(fileInputStream.read());
				} catch (EOFException ignore) {
					fileInputStream.close();
				}
				zos.putNextEntry(new ZipEntry(contentFile.getName()));

				zos.flush();
				zos.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public static void confirm(String directoryPath, String fileName) {
			new File(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION).renameTo(new File(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION));
		}

		public void decompress(String directoryPath, String fileName) throws IOException {
			formalFile = new File(directoryPath + File.separator + fileName + Statics.FORMAL_EXTENSION);
			contentFile = new File(directoryPath + File.separator + fileName + Statics.CONTENT_EXTENSION);

			// log.info("DECOMPRESS " + directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION);
			// File zipFile = new File(directoryPath + File.separator + fileName + Statics.ZIP_EXTENSION);
			// ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
			// formalFile = write(inputStream, directoryPath + File.separator + fileName + Statics.FORMAL_EXTENSION);
			// log.info("A");
			// contentFile = write(inputStream, directoryPath + File.separator + fileName + Statics.CONTENT_EXTENSION);
			// inputStream.close();
		}

		private File write(ZipInputStream inputStream, String path) throws IOException {
			File file = new File(path);
			file.createNewFile();
			inputStream.getNextEntry();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = inputStream.read(buffer)) > 0)
				fos.write(buffer, 0, len);
			fos.close();
			inputStream.closeEntry();
			return file;
		}

		public FileInputStream getFormalInputStream() {
			try {
				return new FileInputStream(formalFile);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public FileInputStream getContentInputStream() {
			try {
				return new FileInputStream(contentFile);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

	}

}