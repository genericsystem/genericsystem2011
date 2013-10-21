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
					return ZipArchiver.decompress(directory.getAbsolutePath() + File.separator + Statics.getFilename(snapshotsMap.lastKey()));
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

	private long getTimestamp(final String filename) throws ParseException {
		return Long.parseLong(filename.substring(filename.lastIndexOf("---") + 3));
	}

	public void startScheduler() {
		if (lockFile != null)
			if (Statics.SNAPSHOTS_PERIOD > 0L) {
				final SnapshotWriter snapshotWriter = new SnapshotWriter();
				scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						snapshotWriter.doSnapshot(engine);
					}
				}, Statics.SESSION_TIMEOUT, Statics.SNAPSHOTS_PERIOD, TimeUnit.MILLISECONDS);
			}
	}

	public void close() {
		if (lockFile != null)
			try {
				scheduler.shutdown();
				new SnapshotWriter().doSnapshot(engine);
				lockFile.close();
				lockFile = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
	}

	private class SnapshotWriter {

		private Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
		private ZipArchiver zipArchiver;

		public void doSnapshot(Engine engine) {
			long ts = engine.pickNewTs();
			String fileName = directory.getAbsolutePath() + File.separator + Statics.getFilename(ts);
			zipArchiver = new ZipArchiver(new File(fileName + Statics.FORMAL_EXTENSION), new File(fileName + Statics.CONTENT_EXTENSION));
			saveSnapshot(new Transaction(engine));
			compressTemporarySnapshot(fileName);
			zipArchiver.deleteFiles();
			confirmTemporarySnapshot(fileName);
		}

		private void saveSnapshot(AbstractContext context) {
			try {
				ObjectOutputStream formalObjectOutput = new ObjectOutputStream(buildNewTemporaryFormal(zipArchiver.getFormalFile()));
				ObjectOutputStream contentObjectOutput = new ObjectOutputStream(buildNewTemporaryContent(zipArchiver.getContentFile()));
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

		private void writeGeneric(GenericImpl generic, ObjectOutputStream formalObjectOutput, ObjectOutputStream contentObjectOutput) throws IOException {
			writeTs(generic, formalObjectOutput);
			contentObjectOutput.writeLong(generic.homeTreeNode.ts);
			if (!homeTreeMap.containsKey(generic.homeTreeNode.ts)) {
				contentObjectOutput.writeLong(generic.homeTreeNode.metaNode.ts);
				contentObjectOutput.writeObject(generic.homeTreeNode.value);
				homeTreeMap.put(generic.homeTreeNode.ts, generic.homeTreeNode);
			}
			if (generic.isEngine())
				return;
			writeAncestors(generic.getSupers(), formalObjectOutput);
			writeAncestors(generic.getComponents(), formalObjectOutput);
			formalObjectOutput.writeObject(GenericImpl.class.equals(generic.getClass()) ? null : generic.getClass());
		}

		private void writeTs(Generic generic, ObjectOutputStream formalObjectOutput) throws IOException {
			formalObjectOutput.writeLong(((GenericImpl) generic).getDesignTs());
			formalObjectOutput.writeLong(((GenericImpl) generic).getBirthTs());
			formalObjectOutput.writeLong(((GenericImpl) generic).getLastReadTs());
			formalObjectOutput.writeLong(((GenericImpl) generic).getDeathTs());
		}

		private void writeAncestors(Snapshot<Generic> dependencies, ObjectOutputStream formalObjectOutput) throws IOException {
			formalObjectOutput.writeInt(dependencies.size());
			for (Generic dependency : dependencies)
				formalObjectOutput.writeLong(((GenericImpl) dependency).getDesignTs());
		}

		private void compressTemporarySnapshot(String path) {
			zipArchiver.compress(path);
		}

		private void confirmTemporarySnapshot(String path) {
			zipArchiver.confirm(path);
			manageOldSnapshots();
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

		private long minInterval(long periodNumber) {
			return (long) Math.floor(periodNumber / Statics.ARCHIVER_COEFF);
		}

		private void removeSnapshot(NavigableMap<Long, File> snapshotsMap, long ts) {
			snapshotsMap.get(ts).delete();
			snapshotsMap.remove(ts);
		}
	}

	private class SnapshotLoader extends HashMap<Long, Generic> {
		private static final long serialVersionUID = 3139276947667714316L;

		private Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
		private ZipArchiver zipArchiver;
		private ObjectInputStream formalInputStream;
		private ObjectInputStream contentInputStream;

		private SnapshotLoader(ZipArchiver zipArchiver) {
			try {
				this.zipArchiver = zipArchiver;
				formalInputStream = new ObjectInputStream(new FileInputStream(zipArchiver.getFormalFile()));
				contentInputStream = new ObjectInputStream(new FileInputStream(zipArchiver.getContentFile()));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		private void loadSnapshot() {
			try {
				Engine engine = loadEngine();
				for (;;)
					loadGeneric(engine);
			} catch (EOFException ignore) {
				zipArchiver.getFormalFile().delete();
				zipArchiver.getContentFile().delete();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		private Engine loadEngine() throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			contentInputStream.readLong();
			contentInputStream.readObject();
			((EngineImpl) engine).restoreEngine(homeTreeNodeTs, ts[0], ts[1], ts[2], ts[3]);
			put(ts[0], engine);
			homeTreeMap.put(homeTreeNodeTs, ((EngineImpl) engine).homeTreeNode);
			return engine;
		}

		private void loadGeneric(Engine engine) throws IOException, ClassNotFoundException {
			long[] ts = loadTs(formalInputStream);
			long homeTreeNodeTs = contentInputStream.readLong();
			HomeTreeNode homeTreeNode = null;
			if (homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeNode = homeTreeMap.get(homeTreeNodeTs);
			else
				homeTreeNode = homeTreeMap.get(contentInputStream.readLong()).bindInstanceNode(homeTreeNodeTs, (Serializable) contentInputStream.readObject());
			Generic[] supers = loadAncestors(formalInputStream);
			Generic[] components = loadAncestors(formalInputStream);
			Generic generic = engine.getFactory().newGeneric((Class<?>) formalInputStream.readObject());
			GenericImpl plug = ((GenericImpl) generic).restore(homeTreeNode, ts[0], ts[1], ts[2], ts[3], supers, components).plug();
			if (!homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeMap.put(homeTreeNodeTs, plug.homeTreeNode);
			put(ts[0], plug);
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

	private static class ZipArchiver {

		private File formalFile;
		private File contentFile;

		public ZipArchiver(File formalFile, File contentFile) {
			this.formalFile = formalFile;
			this.contentFile = contentFile;
		}

		public void compress(String path) {
			try {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION));

				zos.putNextEntry(new ZipEntry(formalFile.getName()));
				FileInputStream fileInputStream = new FileInputStream(formalFile);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fileInputStream.read(buffer)) > 0)
					zos.write(buffer, 0, len);
				fileInputStream.close();

				zos.putNextEntry(new ZipEntry(contentFile.getName()));
				fileInputStream = new FileInputStream(contentFile);
				while ((len = fileInputStream.read(buffer)) > 0)
					zos.write(buffer, 0, len);
				fileInputStream.close();

				zos.flush();
				zos.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public void confirm(String path) {
			new File(path + Statics.ZIP_EXTENSION + Statics.PART_EXTENSION).renameTo(new File(path + Statics.ZIP_EXTENSION));
		}

		public void deleteFiles() {
			formalFile.delete();
			contentFile.delete();
		}

		public static ZipArchiver decompress(String path) throws IOException {
			ZipInputStream inputStream = new ZipInputStream(new FileInputStream(new File(path + Statics.ZIP_EXTENSION)));
			File formalFile = write(inputStream, path + Statics.FORMAL_EXTENSION);
			File contentFile = write(inputStream, path + Statics.CONTENT_EXTENSION);
			inputStream.close();
			return new ZipArchiver(formalFile, contentFile);
		}

		private static File write(ZipInputStream inputStream, String path) throws IOException {
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

		public File getFormalFile() {
			return formalFile;
		}

		public File getContentFile() {
			return contentFile;
		}

	}

}
