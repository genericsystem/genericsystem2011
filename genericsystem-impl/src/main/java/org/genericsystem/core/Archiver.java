package org.genericsystem.core;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.OverlappingFileLockException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class Archiver {

	// private static final Logger log = LoggerFactory.getLogger(Archiver.class);

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private Engine engine;
	private File directory;

	private FileOutputStream lockFile;

	public Archiver(Engine engine, String directoryPath) {
		this.engine = engine;
		prepareAndLockDirectory(directoryPath);
		String snapshotPath = getSnapshotPath();
		if (snapshotPath != null)
			new SnapshotLoader().loadSnapshot(snapshotPath);
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

	private String getSnapshotPath() {
		if (lockFile != null) {
			NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory);
			if (!snapshotsMap.isEmpty())
				return directory.getAbsolutePath() + File.separator + Statics.getFilename(snapshotsMap.lastKey());
		}
		return null;
	}

	private static NavigableMap<Long, File> snapshotsMap(File directory) {
		NavigableMap<Long, File> snapshotsMap = new TreeMap<Long, File>();
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			if (!file.isDirectory() && filename.endsWith(Statics.ZIP_EXTENSION)) {
				filename = filename.substring(0, filename.length() - Statics.ZIP_EXTENSION.length());
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

	private static long getTimestamp(final String filename) throws ParseException {
		return Long.parseLong(filename.substring(filename.lastIndexOf("---") + 3));
	}

	public void startScheduler() {
		if (lockFile != null)
			if (Statics.SNAPSHOTS_PERIOD > 0L) {
				scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						SnapshotWriter.doSnapshot(directory, engine);
					}
				}, Statics.SESSION_TIMEOUT, Statics.SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
			}
	}

	public void close() {
		if (lockFile != null)
			try {
				scheduler.shutdown();
				SnapshotWriter.doSnapshot(directory, engine);
				lockFile.close();
				lockFile = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
	}

	private static class SnapshotWriter {

		public static void doSnapshot(File directory, Engine engine) {
			String path = directory.getAbsolutePath() + File.separator;
			String fileName = Statics.getFilename(engine.pickNewTs());
			try (ByteArrayOutputStream formalOutputStream = new ByteArrayOutputStream(); ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(path + fileName + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION));) {

				zipOutput.putNextEntry(new ZipEntry(fileName + Statics.CONTENT_EXTENSION));
				saveSnapshot(engine, new ObjectOutputStream(formalOutputStream), new ObjectOutputStream(zipOutput));
				zipOutput.closeEntry();

				zipOutput.putNextEntry(new ZipEntry(fileName + Statics.FORMAL_EXTENSION));
				zipOutput.write(formalOutputStream.toByteArray());
				zipOutput.closeEntry();

				formalOutputStream.close();
				zipOutput.flush();
				zipOutput.close();

				new File(path + fileName + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION).renameTo(new File(path + fileName + Statics.ZIP_EXTENSION));
				manageOldSnapshots(directory);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}

		private static void saveSnapshot(Engine engine, ObjectOutputStream tmpFormal, ObjectOutputStream tmpContent) throws IOException {
			Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
			for (Generic orderGeneric : new Transaction(engine).orderDependencies(engine))
				writeGeneric(((GenericImpl) orderGeneric), tmpFormal, tmpContent, homeTreeMap);
		}

		private static void writeGeneric(GenericImpl generic, ObjectOutputStream tmpFormal, ObjectOutputStream tmpContent, Map<Long, HomeTreeNode> homeTreeMap) throws IOException {
			writeTs(generic, tmpFormal);
			tmpContent.writeLong(generic.homeTreeNode.ts);
			if (!homeTreeMap.containsKey(generic.homeTreeNode.ts)) {
				tmpContent.writeLong(generic.homeTreeNode.metaNode.ts);
				tmpContent.writeObject(generic.homeTreeNode.getValue());
				homeTreeMap.put(generic.homeTreeNode.ts, generic.homeTreeNode);
			}
			if (generic.isEngine())
				return;
			writeAncestors(generic.getSupers(), tmpFormal);
			writeAncestors(generic.getComponents(), tmpFormal);
			tmpFormal.writeObject(GenericImpl.class.equals(generic.getClass()) ? null : generic.getClass());
		}

		private static void writeTs(Generic generic, ObjectOutputStream tmpFormal) throws IOException {
			tmpFormal.writeLong(((GenericImpl) generic).getDesignTs());
			tmpFormal.writeLong(((GenericImpl) generic).getBirthTs());
			tmpFormal.writeLong(((GenericImpl) generic).getLastReadTs());
			tmpFormal.writeLong(((GenericImpl) generic).getDeathTs());
		}

		private static void writeAncestors(Snapshot<Generic> dependencies, ObjectOutputStream formalObjectOutput) throws IOException {
			formalObjectOutput.writeInt(dependencies.size());
			for (Generic dependency : dependencies)
				formalObjectOutput.writeLong(((GenericImpl) dependency).getDesignTs());
		}

		private static void manageOldSnapshots(File directory) {
			NavigableMap<Long, File> snapshotsMap = snapshotsMap(directory);
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

	}

	private class SnapshotLoader extends HashMap<Long, Generic> {
		private static final long serialVersionUID = 3139276947667714316L;

		public void loadSnapshot(String path) {
			try (ObjectInputStream contentInputStream = new ObjectInputStream(readContent(path)); ObjectInputStream formalInputStream = new ObjectInputStream(readFormal(path));) {
				Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
				Engine engine = loadEngine(formalInputStream, contentInputStream, homeTreeMap);
				for (;;)
					loadGeneric(engine, formalInputStream, contentInputStream, homeTreeMap);
			} catch (EOFException ignore) {
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		private ZipInputStream readFormal(String path) throws IOException {
			ZipInputStream inputStream = new ZipInputStream(new FileInputStream(new File(path + Statics.ZIP_EXTENSION)));
			inputStream.getNextEntry();
			inputStream.getNextEntry();
			return inputStream;
		}

		private ZipInputStream readContent(String path) throws IOException {
			ZipInputStream inputStream = new ZipInputStream(new FileInputStream(new File(path + Statics.ZIP_EXTENSION)));
			inputStream.getNextEntry();
			return inputStream;
		}

		private Engine loadEngine(ObjectInputStream formalInputStream, ObjectInputStream contentInputStream, Map<Long, HomeTreeNode> homeTreeMap) throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			contentInputStream.readLong();
			contentInputStream.readObject();
			((EngineImpl) engine).restoreEngine(homeTreeNodeTs, ts[0], ts[1], ts[2], ts[3]);
			put(ts[0], engine);
			homeTreeMap.put(homeTreeNodeTs, ((EngineImpl) engine).homeTreeNode);
			return engine;
		}

		private void loadGeneric(Engine engine, ObjectInputStream formalInputStream, ObjectInputStream contentInputStream, Map<Long, HomeTreeNode> homeTreeMap) throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			HomeTreeNode homeTreeNode = homeTreeMap.get(homeTreeNodeTs);
			if (null == homeTreeNode)
				homeTreeNode = homeTreeMap.get(contentInputStream.readLong()).bindInstanceNode(homeTreeNodeTs, (Serializable) contentInputStream.readObject());
			Generic[] supers = loadAncestors(formalInputStream);
			Generic[] components = loadAncestors(formalInputStream);
			Generic generic = engine.getFactory().newGeneric((Class<?>) formalInputStream.readObject());
			((GenericImpl) generic).restore(homeTreeNode, ts[0], ts[1], ts[2], ts[3], supers, components).plug();
			if (!homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeMap.put(homeTreeNodeTs, ((GenericImpl) generic).homeTreeNode);
			put(ts[0], generic);
		}

		private long[] loadTs(ObjectInputStream in) throws IOException {
			long[] ts = new long[4];
			ts[0] = in.readLong(); // designTs
			ts[1] = in.readLong(); // birthTs
			ts[2] = in.readLong(); // lastReadTs
			ts[3] = in.readLong(); // deathTs
			return ts;
		}

		private Generic[] loadAncestors(ObjectInputStream in) throws IOException {
			int length = in.readInt();
			Generic[] ancestors = new Generic[length];
			for (int index = 0; index < length; index++)
				ancestors[index] = get(in.readLong());
			return ancestors;
		}

	}

}
